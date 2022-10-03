package io.spring.batch.helloworld;

import io.spring.batch.helloworld.batch.CustomerFieldSetMapper;
import io.spring.batch.helloworld.batch.CustomerFileLineTokenizer;
import io.spring.batch.helloworld.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

@EnableBatchProcessing
//@SpringBootApplication
public class DelimitedJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    @Bean
//    @StepScope
//    public FlatFileItemReader<Customer> customerItemReader(
//            @Value("#{jobParameters['customerFile']}") Resource inputFile
//            ) {
//        return new FlatFileItemReaderBuilder<Customer>()
//                .name("customerItemReader")
//                .delimited()
//                .names(new String[] {"firstName", "middleInitial", "lastName", "addressNumber", "street", "city", "state", "zipCode"})
////                .targetType(Customer.class)
//                .fieldSetMapper(new CustomerFieldSetMapper())
//                .resource(inputFile)
//                .build();
//    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .lineTokenizer(new CustomerFileLineTokenizer())
                .targetType(Customer.class)
                .resource(inputFile)
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return items -> items.forEach(System.out::println);
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null))
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
        List<String> strings = Arrays.asList("customerFile=file:///Users/gimtaemin/spring-batch/chapter07/src/main/resources/input/customer.csv");
        SpringApplication.run(DelimitedJob.class, strings.toArray(new String[1]));
    }
}
