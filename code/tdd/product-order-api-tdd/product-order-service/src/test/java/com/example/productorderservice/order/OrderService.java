package com.example.productorderservice.order;

import com.example.productorderservice.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class OrderService {

    @Autowired
    private OrderPort orderPort;


    public void createOrder(CreateOrderRequest request) {
        Product product = orderPort.getProductById(request.productId());

        Order order = new Order(product, request.quantity());

        orderPort.save(order);
    }
}
