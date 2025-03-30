# spring-sqs

## Description
Demo project for Spring Boot integration with amazon sqs.

## Features
- Create Queue
- Assign Dead-letter Queue
- Delete Queue
- Send Message
- Poll Message

## Getting Started

### Prerequisites
- AWS Account
- Familiarity with Spring Boot
- If any of the below guides look strange to you, you can follow the step-by-step process with the link below
- https://medium.com/@akeni.promise/spring-boot-integration-with-amazon-sqs-a-comprehensive-guide-with-best-practices-e4d63e5de10d

Installation
1. Clone the repository

git clone https://github.com/smartpro1/spring-sqs.git or via ssh git@github.com:smartpro1/spring-sqs.git

2. Navigate into the project directory
   cd spring-sqs

3. Create IAM user with SQSFullAccess Permission
4. Generate access and secret keys
5. Run the application with the above credentials using the below command
   mvn spring-boot:run "-Dspring-boot.run.arguments=--aws.access.key=YOUR_ACCESS_KEY --aws.secret.key=YOUR_SECRET_KEY --aws.queue.name=YOUR_SOURCE_QUEUE_NAME --aws.region=YOUR_REGION"

# Create Source Queue request
POST: http://localhost:8080/api/v1/queue/my-test-queue

# Create dead-letter Queue request
POST: http://localhost:8080/api/v1/queue/my-test-queue-dl

# Assign dead-letter Queue request
POST: http://localhost:8080/api/v1/queue/assign-dead-letter-queue
{
"sourceQueue" : "my-test-queue",
"deadLetterQueue": "my-test-queue-dl"
}

# Send Message Request
POST: http://localhost:8080/api/v1/message/send
{
"productId" : "3443r-0a22-4c8e-a19b",
"productName": "Matter",
"productPrice" : 27.00,
"serialNumber" : 44
}

- Polling happens through scheduler in the ConsumerServiceImpl class
- You can see polled messages in your local console once the the scheduler's initial delay of 20s elapses
- To see messages sent to sqs queues comment @EnableScheduling in SpringSqsApplication and re-run application

# Delete Queue Request
DELETE: http://localhost:8080/api/v1/queue/REPLACE_ME_WITH_QUEUE_NAME


  
