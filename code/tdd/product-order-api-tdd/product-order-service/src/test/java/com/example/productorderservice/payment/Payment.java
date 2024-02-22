package com.example.productorderservice.payment;

import com.example.productorderservice.order.Order;

class Payment {

    private Long id;
    private final Order order;
    private final String cardNumber;
    private int price;

    public Payment(Order order, String cardNumber) {
        this.order = order;
        this.cardNumber = cardNumber;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getPrice() {
        return order.getTotalPrice();
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
