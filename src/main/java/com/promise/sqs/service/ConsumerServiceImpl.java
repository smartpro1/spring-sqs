package com.promise.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promise.sqs.dto.MessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ConsumerServiceImpl implements ConsumerService{
    Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    @Value("${aws.queue.name}")
    private String queueName;
    private final AmazonSQS amazonSqsClient;
    private final ObjectMapper objectMapper;

    private String queueUrl = null;

    private final Map<String, MessageRequest> FAKE_DB = new HashMap<>();

    public ConsumerServiceImpl(AmazonSQS amazonSqsClient, ObjectMapper objectMapper) {

        this.amazonSqsClient = amazonSqsClient;
        this.objectMapper = objectMapper;
    }
    @Override
    @Scheduled(fixedDelay = 10000, initialDelay = 20000) // Initial delay 20s, method invocation every 10s after the completion of the previous run
    public void pollMessage() {
        logger.info(" polling message from queueName: " + queueName);
        try {
            String queueUrl = getQueueUrl();
            /*
              Long polling is in effect when the WaitTimeSeconds is > 0 as shown below.
              With Long polling, if there's no message in the queue, it doesn't return immediately.
              It waits for the specified time  if new messages would arrive before returning.
              This saves us multiple polling and saves us money as well
             */
            ReceiveMessageRequest messageRequestConfig = new ReceiveMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(20); // Long polling(best practice) - if there's no message in queue, wait for 20s
            ReceiveMessageResult receiveMessageResult = amazonSqsClient.receiveMessage(messageRequestConfig);
            List<DeleteMessageBatchRequestEntry> messagesToBeDeleted = null;

            if (!receiveMessageResult.getMessages().isEmpty()) {
                messagesToBeDeleted = new ArrayList<>();
                List<Message> messages = receiveMessageResult.getMessages();
                logger.info("message of size {} received, processing messages...", messages.size());
                for (Message message : messages) {

                    // Ideally, before processing a message, you want to query the db for a unique key in the message in order to prevent duplicate processing
                    // I will use a hashmap as a fake db
                    // In a high throughput system, querying the db can be expensive, you might use a cache instead and expire it periodically

                    MessageRequest messageRequest = objectMapper.readValue(message.getBody(), MessageRequest.class);

                    // IDEMPOTENCY DESIGN
                    if (FAKE_DB.containsKey(messageRequest.serialNumber())) {
                        logger.warn("Duplicate record found with serial number {}", messageRequest.serialNumber());
                        addDeleteBoundMessageToList(messagesToBeDeleted, message);
                        continue;
                    }

                    // simulating dead-letter queue scenario for testing purpose only
                    if (messageRequest.serialNumber() == null || messageRequest.serialNumber().isEmpty()) {
                        // After visibility timeout the message is returned to the queue
                        // After twice being returned to the main queue, it is sent to the dead-letter queue
                        logger.warn("message request with invalid serial number identified with product number {}", messageRequest.productId());
                        continue;
                    }

                    logger.info(message.getMessageId() + "successfully processed");
                    addDeleteBoundMessageToList(messagesToBeDeleted, message);
                    FAKE_DB.put(messageRequest.serialNumber(), messageRequest);
                }
            }
              /*
          Batch deleting instead of deleting every time we processed a message can help improve performance because
          every delete is an external api call which is expensive
         */
          if (messagesToBeDeleted != null && !messagesToBeDeleted.isEmpty()) {
                amazonSqsClient.deleteMessageBatch(queueUrl,  messagesToBeDeleted);
          }
        } catch (Exception e) {
            //When the queue doesn't exist an exception would be thrown, and other exceptions as well
            logger.error("An exception occured while polling message : {}", e.getMessage());
        }

    }

    private String getQueueUrl() {
        if (queueUrl == null) {
            queueUrl = amazonSqsClient.getQueueUrl(queueName).getQueueUrl();
        }
        return queueUrl;
    }

    private void addDeleteBoundMessageToList(List<DeleteMessageBatchRequestEntry> messagesToBeDeleted, Message message) {
        DeleteMessageBatchRequestEntry entry = new DeleteMessageBatchRequestEntry().withId(message.getMessageId()).withReceiptHandle(message.getReceiptHandle());
        messagesToBeDeleted.add(entry);
    }


}
