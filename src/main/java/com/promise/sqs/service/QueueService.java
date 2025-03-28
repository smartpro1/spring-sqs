package com.promise.sqs.service;

public interface QueueService {
    String createQueue(String queueName);
    String deleteQueue(String queueName);

   String setDeadLetterQueue(String sourceQueue, String deadLetterQueue);
}
