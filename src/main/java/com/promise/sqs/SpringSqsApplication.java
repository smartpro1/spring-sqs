package com.promise.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSqsApplication.class, args);
	}

}
