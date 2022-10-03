package io.spring.batch.helloworld;

import io.spring.batch.helloworld.batch.CustomItemReader;
import io.spring.batch.helloworld.batch.CustomerItemListener;
import io.spring.batch.helloworld.batch.EmptyInputStepFailer;
import io.spring.batch.helloworld.domain.Customer;
import io.spring.batch.helloworld.domain.CustomerService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class CustomInputJob {

    @Bean
    public CustomItemReader customItemReader() {
        CustomItemReader customItemReader = new CustomItemReader();

        customItemReader.setName("customerItemReader");

        return customItemReader;
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CustomerService customerService;


    @Bean
    public ItemWriter itemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public CustomerItemListener customerItemListener() {
        return new CustomerItemListener();
    }

    @Bean
    public EmptyInputStepFailer emptyInputStepFailer() {
        return new EmptyInputStepFailer();
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(customItemReader())
                .writer(itemWriter())
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .listener(customerItemListener())
                .listener(emptyInputStepFailer())
//                .noSkip(ParseException.class)
//                .skipLimit(10)
                .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
//                .incrementer(new RunIdIncrementer())
                .start(copyFileStep())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(CustomInputJob.class, args);
    }
}
