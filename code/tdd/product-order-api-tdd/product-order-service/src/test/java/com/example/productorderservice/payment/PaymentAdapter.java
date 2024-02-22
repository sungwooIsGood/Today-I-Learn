package com.example.productorderservice.payment;

import com.example.productorderservice.order.Order;
import com.example.productorderservice.product.DiscountPolicy;
import com.example.productorderservice.product.Product;

public class PaymentAdapter implements PaymentPort {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;

    public PaymentAdapter(PaymentGateway paymentGateway, PaymentRepository paymentRepository) {
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Order getOrder(Long orderId) {
        return new Order(new Product("상품", 1000, DiscountPolicy.NONE), 2);
    }

    @Override
    public void pay(int totalPrice, String cardNumber) {
        paymentGateway.excute(totalPrice,cardNumber); // pg 연동 역할, pg API 호출 담당
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }
}
