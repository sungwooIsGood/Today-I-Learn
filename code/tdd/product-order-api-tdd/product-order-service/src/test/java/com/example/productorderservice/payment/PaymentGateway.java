package com.example.productorderservice.payment;

interface PaymentGateway {


    void excute(int totalPrice, String cardNumber);
}
