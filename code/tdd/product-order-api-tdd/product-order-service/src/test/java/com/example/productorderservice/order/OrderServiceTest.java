package com.example.productorderservice.order;

import com.example.productorderservice.product.DiscountPolicy;
import com.example.productorderservice.product.Product;
import com.example.productorderservice.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class OrderServiceTest {

    private OrderService orderService;
    private OrderPort orderPort;

    @BeforeEach
    void setup(){
        OrderRepository orderRepository = new OrderRepository();
        orderPort = new OrderPort(){

            @Override
            public Product getProductById(Long productId) {
                return new Product("상품명",1000, DiscountPolicy.NONE);
            }

            @Override
            public void save(Order order) {
                orderRepository.save(order);
            }
        };
        orderService = new OrderService(orderPort);
    }

    @Test
    void 상품주문(){
        final Long productId = 1L;
        final int quantity = 2;
        final CreateOrderRequest request = new CreateOrderRequest(productId, quantity);
        orderService.createOrder(request);

    }

    record CreateOrderRequest(Long productId, int quantity) {
        CreateOrderRequest {
            Assert.notNull(productId,"상품 ID 값은 필수");
            Assert.isTrue(quantity > 0, "수량은 0보다 커야 합니다.");
        }
    }

    private class OrderService {

        private final OrderPort orderPort;

        public OrderService(OrderPort orderPort) {
            this.orderPort = orderPort;
        }

        public void createOrder(CreateOrderRequest request) {
            Product product = orderPort.getProductById(request.productId());

            Order order = new Order(product,request.quantity());

            orderPort.save(order);
        }
    }

    private class OrderAdapter implements OrderPort {

        private final ProductRepository productRepository;
        private final OrderRepository orderRepository;

        public OrderAdapter(ProductRepository productRepository, OrderRepository orderRepository) {
            this.productRepository = productRepository;
            this.orderRepository = orderRepository;
        }

        @Override
        public Product getProductById(Long productId) {
            return productRepository.findById(productId)
                    .orElseThrow(IllegalArgumentException::new);
        }

        @Override
        public void save(Order order) {
            orderRepository.save(order);
        }
    }

    interface OrderPort{

        Product getProductById(Long productId);
        void save(Order order);
    }


    private class Order {
        private Long id;

        private Product product;
        private int quantity;

        public Order(Product product, int quantity) {
            Assert.notNull(product,"상품은 필수");
            Assert.isTrue(quantity > 0, "수량은 0보다 커야 합니다.");
            this.product = product;
            this.quantity = quantity;
        }

        public Long getId() {
            return id;
        }

        public void assignId(Long id) {
            this.id = id;
        }
    }

    private class OrderRepository {

        private Map<Long, Order> persistence = new HashMap<>();
        private Long sequence = 0L;

        public void save(Order order) {
            order.assignId(++sequence);
            persistence.put(order.getId(),order);
        }
    }
}
