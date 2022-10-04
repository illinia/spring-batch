package io.spring.batch.helloworld;

import io.spring.batch.helloworld.batch.CustomerOutputFileSuffixCreator;
import io.spring.batch.helloworld.batch.CustomerRecordCountFooterCallback;
import io.spring.batch.helloworld.batch.CustomerXmlHeaderCallback;
import io.spring.batch.helloworld.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
//@SpringBootApplication
public class MultiResourceHeaderFooterJob {

//    @Bean
//    @StepScope
//    public StaxEventItemWriter<Customer> delegateItemWriter(
//            CustomerXmlHeaderCallback headerCallback) throws Exception {
//        Map<String, Class> aliases = new HashMap<>();
//        aliases.put("customer", Customer.class);
//
//        XStreamMarshaller marshaller = new XStreamMarshaller();
//
//        marshaller.setAliases(aliases);
//        marshaller.afterPropertiesSet();
//
//        return new StaxEventItemWriterBuilder<Customer>()
//                .name("customerItemWriter")
//                .marshaller(marshaller)
//                .rootTagName("customer")
//                .headerCallback(headerCallback)
//                .build();
//    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> delegateCustomerItemWriter(
            CustomerRecordCountFooterCallback footerCallback) throws Exception {
        BeanWrapperFieldExtractor<Customer> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"firstName", "lastName", "address", "city", "state", "zip"});
        fieldExtractor.afterPropertiesSet();

        FormatterLineAggregator<Customer> lineAggregator = new FormatterLineAggregator<>();

        lineAggregator.setFormat("%s %s lives at %s %s in %s, %s.");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setName("delegateCustomerItemWriter");
        itemWriter.setLineAggregator(lineAggregator);
        itemWriter.setAppendAllowed(true);
        itemWriter.setFooterCallback(footerCallback);

        return itemWriter;
    }

    @Bean
    public JdbcCursorItemReader<Customer> customerJdbcCursorItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .dataSource(dataSource)
                .sql("select * from customer")
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Autowired
    private CustomerOutputFileSuffixCreator customerOutputFileSuffixCreator;

    @Bean
    public MultiResourceItemWriter<Customer> multiResourceItemWriter() throws Exception {
        return new MultiResourceItemWriterBuilder<Customer>()
                .name("multiCustomerFileWriter")
                .delegate(delegateCustomerItemWriter(null))
                .itemCountLimitPerResource(25)
                .resource(new FileSystemResource("/Users/taemin/spring-batch/chapter09/src/main/resources/customer/customer"))
                .resourceSuffixCreator(customerOutputFileSuffixCreator)
                .build();
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step formatStep() throws Exception {
        return this.stepBuilderFactory.get("multiXmlGeneratorStep")
                .<Customer, Customer>chunk(10)
                .reader(customerJdbcCursorItemReader(null))
                .writer(multiResourceItemWriter())
                .build();
    }

    @Bean
    public Job formatJob() throws Exception {
        return this.jobBuilderFactory.get("formatJob")
                .start(formatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(MultiResourceHeaderFooterJob.class, "customerFile=file:/Users/taemin/spring-batch/chapter09/src/main/resources/data/customer.csv");
    }
}
