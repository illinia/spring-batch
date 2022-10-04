package io.spring.batch.helloworld;

import io.spring.batch.helloworld.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
//@SpringBootApplication
public class CompositeItemWriterJob {

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> compositeWriterItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("compositewriterItemReader")
                .resource(inputFile)
                .delimited()
				.names(new String[] {"firstName",
						"middleInitial",
						"lastName",
						"address",
						"city",
						"state",
						"zip",
						"email"})
                .targetType(Customer.class)
                .build();
    }

	@Bean
	@StepScope
	public StaxEventItemWriter<Customer> xmlDelegateItemWriter(
			@Value("#{jobParameters['outputFile']}") Resource outputFile) {

		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);

		XStreamMarshaller marshaller = new XStreamMarshaller();

		marshaller.setAliases(aliases);

		marshaller.afterPropertiesSet();

		return new StaxEventItemWriterBuilder<Customer>()
				.name("customerItemWriter")
				.resource(outputFile)
				.marshaller(marshaller)
				.rootTagName("customers")
				.build();
	}

	@Bean
	public JdbcBatchItemWriter<Customer> jdbcDelegateItemWriter(DataSource dataSource) throws Exception {
		return new JdbcBatchItemWriterBuilder<Customer>()
				.namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
				.sql("INSERT INTO CUSTOMER (first_name, " +
						"middle_initial, " +
						"last_name, " +
						"address, " +
						"city, " +
						"state, " +
						"zip) VALUES (:firstName, " +
						":middleInitial, " +
						":lastName, " +
						":address, " +
						":city, " +
						":state, " +
						":zip)")
				.beanMapped()
				.build();
	}

	@Bean
	public CompositeItemWriter<Customer> compositeItemWriter() throws Exception {
		return new CompositeItemWriterBuilder<Customer>()
				.delegates(Arrays.asList(xmlDelegateItemWriter(null),
						jdbcDelegateItemWriter(null)))
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
				.reader(compositeWriterItemReader(null))
				.writer(compositeItemWriter())
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
		SpringApplication.run(CompositeItemWriterJob.class, "customerFile=file:/Users/taemin/spring-batch/chapter09/src/main/resources/data/customerWithEmail.csv", "outputFile=file:/Users/taemin/spring-batch/chapter09/src/main/resources/customer/customer");
	}
}
