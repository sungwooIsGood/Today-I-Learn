package com.example.productorderservice.payment;

import java.util.HashMap;
import java.util.Map;

class PaymentRepository {


    private Map<Long, Payment> persistence = new HashMap<>();
    private Long sequence = 0L;

    public void save(Payment payment) {
        payment.assignId(++sequence);
        persistence.put(payment.getId(), payment);

    }
}
