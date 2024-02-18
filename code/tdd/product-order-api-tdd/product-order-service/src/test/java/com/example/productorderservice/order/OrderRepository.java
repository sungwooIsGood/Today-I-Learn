package com.example.productorderservice.order;

import java.util.HashMap;
import java.util.Map;

class OrderRepository {

    private Map<Long, Order> persistence = new HashMap<>();
    private Long sequence = 0L;

    public void save(Order order) {
        order.assignId(++sequence);
        persistence.put(order.getId(), order);
    }
}
