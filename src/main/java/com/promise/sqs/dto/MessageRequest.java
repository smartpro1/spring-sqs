package com.promise.sqs.dto;

import java.math.BigDecimal;

public record MessageRequest(String productId, String productName, BigDecimal productPrice, String serialNumber) {
}
