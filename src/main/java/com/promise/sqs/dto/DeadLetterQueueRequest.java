package com.promise.sqs.dto;

public record DeadLetterQueueRequest(String sourceQueue, String deadLetterQueue) {
}
