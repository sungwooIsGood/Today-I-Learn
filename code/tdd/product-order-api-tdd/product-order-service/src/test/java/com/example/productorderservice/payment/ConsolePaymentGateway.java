package com.example.productorderservice.payment;

public class ConsolePaymentGateway implements PaymentGateway {
    @Override
    public void excute(int totalPrice, String cardNumber) {
        System.out.println("결제 완료");
    }
}
