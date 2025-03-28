package com.promise.sqs.service;

import com.promise.sqs.dto.MessageRequest;

public interface PublisherService {
    String sendMessage(MessageRequest messageRequest);
}
