package com.promise.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QueueServiceImpl implements QueueService {
    Logger logger = LoggerFactory.getLogger(QueueServiceImpl.class);
    @Value("${aws.queue.name}")
    private String queueName;
    private final AmazonSQS amazonSqsClient;

    public QueueServiceImpl(AmazonSQS amazonSqsClient) {
        this.amazonSqsClient = amazonSqsClient;
    }
    @Override
    public String createQueue(String queueName) {
        if (queueName == null || queueName.isEmpty()) {
            logger.warn("[createQueue] queue name invalid");
            return "can't create a queue with no name";
        }
        CreateQueueResult resp = null;
        try {
           resp =  amazonSqsClient.createQueue(queueName);
        } catch(AmazonSQSException e) {
            if(e.getErrorCode().equals("QueueAlreadyExists")) {
                return "Queue already exist";
            }
        }catch (Exception e) {
          //  log.error("an error occured while creating queue");
            throw e;
        }
        return "successfully created queue with url : " + resp.getQueueUrl();
    }

    @Override
    public String deleteQueue(String queueName) {
        if (queueName == null || queueName.isEmpty()) {
            return "can't delete a queue with no name";
        }
        try {
             amazonSqsClient.deleteQueue(queueName);
        } catch (Exception e) {
            logger.error("an error occured while deleting queue");
            return "An error occured why deleting queue";
        }

        return "Queue with name " + queueName + " successfully deleted";
    }



    //https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/java/example_code/sqs/src/main/java/aws/example/sqs/DeadLetterQueues.java

    @Override
    public String setDeadLetterQueue(String sourceQueue, String deadLetterQueue) {
        try {
            String dl_queue_url = amazonSqsClient.getQueueUrl(deadLetterQueue)
                    .getQueueUrl();

            GetQueueAttributesResult queue_attrs = amazonSqsClient.getQueueAttributes(
                    new GetQueueAttributesRequest(dl_queue_url)
                            .withAttributeNames("QueueArn"));

            String dl_queue_arn = queue_attrs.getAttributes().get("QueueArn");

            // Set dead letter queue with redrive policy on source queue.
            String src_queue_url = amazonSqsClient.getQueueUrl(sourceQueue)
                    .getQueueUrl();

            SetQueueAttributesRequest request = new SetQueueAttributesRequest()
                    .withQueueUrl(src_queue_url)
                    .addAttributesEntry("RedrivePolicy",
                            "{\"maxReceiveCount\":\"2\", \"deadLetterTargetArn\":\""
                                    + dl_queue_arn + "\"}");

            amazonSqsClient.setQueueAttributes(request);
        } catch(Exception e) {
            logger.error("[setDeadLetterQueue] An error occured while setting dead letter queue ", e);
            return "An error occured while setting dead letter queue";
        }
        return "Dead letter queue successfully set.";
    }
}
