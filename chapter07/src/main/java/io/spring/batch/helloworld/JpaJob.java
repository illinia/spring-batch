package io.spring.batch.helloworld;

import io.spring.batch.helloworld.batch.CustomerByCityQueryProvider;
import io.spring.batch.helloworld.domain.Customer;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernatePagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

@EnableBatchProcessing
//@SpringBootApplication
public class JpaJob {

//    @Bean
//    @StepScope
//    public JpaPagingItemReader<Customer> customerItemReader(
//            EntityManagerFactory entityManagerFactory,
//            @Value("#{jobParameters['city']}") String city
//    ) {
//        return new JpaPagingItemReaderBuilder<Customer>()
//                .name("customerItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select c from Customer c where city = :city")
//                .parameterValues(Collections.singletonMap("city", city))
//                .build();
//    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Customer> customerItemReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{jobParameters['city']}") String city
    ) {
        CustomerByCityQueryProvider queryProvider = new CustomerByCityQueryProvider();

        queryProvider.setCityName(city);

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryProvider(queryProvider)
                .parameterValues(Collections.singletonMap("city", city))
                .build();
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public ItemWriter itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null, null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(copyFileStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(JpaJob.class, "city=Chicago");
    }
}
