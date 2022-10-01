package io.spring.batch.helloworld.job;

import io.spring.batch.helloworld.job.HelloWorldTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@EnableBatchProcessing
//@Configuration
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

//    @Bean
//    public Job job() {
//        return this.jobBuilderFactory.get("job")
//                .start(step1())
//                .build();
//    }
//
//    @Bean
//    public Step step1() {
//        return this.stepBuilderFactory.get("step1")
//                .tasklet(new HelloWorldTasklet())
//                .listener(promotionListener())
//                .build();
//    }
//
//    @Bean
//    public Tasklet helloWorldTasklet() {
//        return new HelloWorld();
//    }
//
//    public static class HelloWorld implements Tasklet {
//        private static final String HELLO_WORLD = "Hello, %s";
//
//        @Override
//        public RepeatStatus execute(StepContribution step, ChunkContext context) throws Exception {
//            String name = (String) context.getStepContext()
//                    .getJobParameters()
//                    .get("name");
//
//            ExecutionContext jobContext = context.getStepContext()
//                    .getStepExecution()
////                    .getJobExecution()
//                    .getExecutionContext();
//            jobContext.put("name", name);
//
//            System.out.println(String.format(HELLO_WORLD, name));
//
//            return RepeatStatus.FINISHED;
//        }
//    }
//
//    @Bean
//    public StepExecutionListener promotionListener() {
//        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
//
//        listener.setKeys(new String[] {"name"});
//        return listener;
//    }

//    청크 기반 스텝
    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(itemReader(null))
                .writer(itemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> itemReader(
            @Value("#{jobParameters['inputFile']}") Resource inputFile
    ) {
        return new FlatFileItemReaderBuilder<String>()
                .name("itemReader")
                .resource(inputFile)
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<String> itemWriter(
            @Value("#{jobParameters['outputFile']}") Resource outputFile
    ) {
        return new FlatFileItemWriterBuilder<String>()
                .name("itemWriter")
                .resource(outputFile)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }
}
