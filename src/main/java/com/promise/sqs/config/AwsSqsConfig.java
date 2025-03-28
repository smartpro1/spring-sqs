package com.promise.sqs.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSqsConfig {
    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.key}")
    private String secretKey;

    @Value("${aws.queue.name}")
    private String queueName;
    @Value("${aws.region}")
    private String region;

  @Bean
  public AmazonSQS amazonSqsClient() {
      BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
      return AmazonSQSClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
              .withRegion(region)
              .build();
  }

}
