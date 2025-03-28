package com.promise.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promise.sqs.dto.MessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublisherServiceImpl implements PublisherService{
    Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);
    @Value("${aws.queue.name}")
    private String queueName;
    private String queueUrl = null;
    private final AmazonSQS amazonSqsClient;
    private final ObjectMapper objectMapper;

    public PublisherServiceImpl(AmazonSQS amazonSqsClient, ObjectMapper objectMapper) {
        this.amazonSqsClient = amazonSqsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String sendMessage(MessageRequest messageRequest) {
        SendMessageResult result = null;
        try {
          String queueUrl = getQueueUrl();
          logger.info("queueUrl : {}", queueUrl);
          result = amazonSqsClient.sendMessage(queueUrl, objectMapper.writeValueAsString(messageRequest));
      } catch (Exception e) {
          logger.error("[sendMessage] Queue Exception error message: {}", e.getMessage());
          e.printStackTrace();
      }
      return result == null ? "An error occurred during send message request"
                            : "Send message request successful with message id" + result.getMessageId();
    }

    private String getQueueUrl() {
        if (queueUrl == null) {
            queueUrl = amazonSqsClient.getQueueUrl(queueName).getQueueUrl();
        }
        return queueUrl;
    }

}
