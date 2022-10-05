package com.cloud.batch.chapter12;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableBatchProcessing
@EnableRetry
@SpringBootApplication
public class Chapter12Application {

	public static void main(String[] args) {
		SpringApplication.run(Chapter12Application.class, args);
	}

}
