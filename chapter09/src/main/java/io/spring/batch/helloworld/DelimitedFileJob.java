package io.spring.batch.helloworld;

import io.spring.batch.helloworld.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@EnableBatchProcessing
//@SpringBootApplication
public class DelimitedFileJob {

    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> customerItemWriter(
            @Value("#{jobParameters['outputFile']}") Resource outputFile) {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("customerItemWriter")
                .resource(outputFile)
                .delimited()
                .delimiter(";")
				.names(new String[] {"zip",
						"state",
						"city",
						"address",
						"lastName",
						"firstName"})
                .shouldDeleteIfEmpty(true)
//                .shouldDeleteIfExists(false)
                .append(true)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
				.names(new String[] {"firstName",
						"middleInitial",
						"lastName",
						"address",
						"city",
						"state",
						"zip"})
                .targetType(Customer.class)
                .build();
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step formatStep() {
        return this.stepBuilderFactory.get("formatStep")
                .<Customer, Customer>chunk(10)
                .reader(customerFileReader(null))
                .writer(customerItemWriter(null))
                .build();
    }

    @Bean
    public Job formatJob() {
        return this.jobBuilderFactory.get("formatJob")
                .start(formatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DelimitedFileJob.class, "customerFile=file:/Users/taemin/spring-batch/chapter09/src/main/resources/data/customer.csv","outputFile=file:/Users/taemin/spring-batch/chapter09/src/main/resources/output/delimitedCustomers.txt");
    }
}
