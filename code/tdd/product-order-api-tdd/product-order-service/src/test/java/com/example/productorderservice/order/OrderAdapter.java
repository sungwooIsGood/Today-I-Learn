package com.example.productorderservice.order;

import com.example.productorderservice.product.Product;
import com.example.productorderservice.product.ProductRepository;

class OrderAdapter implements OrderPort {

    private final ProductRepository productRepository;
    private final OrderServiceTest.OrderRepository orderRepository;

    public OrderAdapter(ProductRepository productRepository, OrderServiceTest.OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public void save(OrderServiceTest.Order order) {
        orderRepository.save(order);
    }
}
