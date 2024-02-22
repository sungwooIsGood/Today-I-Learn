package com.example.productorderservice.payment;

import org.springframework.util.Assert;

record PaymentRequest(Long orderId, String cardNumber) {

    PaymentRequest {
        Assert.notNull(orderId, "필수 값입니다.");
        Assert.hasText(cardNumber, "카드 번호는 필수입니다.");
    }
}
