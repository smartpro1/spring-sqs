package com.promise.sqs.controller;

import com.promise.sqs.dto.DeadLetterQueueRequest;
import com.promise.sqs.service.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    Logger logger = LoggerFactory.getLogger(QueueController.class);
    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/{queueName}")
    public String createQueue(@PathVariable String queueName) {
        logger.info("create queue request received with queueName " + queueName);
        return queueService.createQueue(queueName);
    }
    @DeleteMapping("/{queueName}")
    public String deleteByQueueName(@PathVariable String queueName) {
        logger.info("delete queue request received.");
        return queueService.deleteQueue(queueName);
    }

    @PostMapping("/assign-dead-letter-queue")
    public String assignDeadLetterQueue(@RequestBody DeadLetterQueueRequest dlqRequest) {
        return queueService.setDeadLetterQueue(dlqRequest.sourceQueue(), dlqRequest.deadLetterQueue());
    }

}
