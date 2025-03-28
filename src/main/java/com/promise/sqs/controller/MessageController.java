package com.promise.sqs.controller;


import com.promise.sqs.dto.MessageRequest;
import com.promise.sqs.service.PublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final PublisherService publisherService;

    public MessageController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageRequest messageRequest) {
       logger.info("processing  request with serial number : {}", messageRequest.serialNumber());
        return publisherService.sendMessage(messageRequest);
    }

}
