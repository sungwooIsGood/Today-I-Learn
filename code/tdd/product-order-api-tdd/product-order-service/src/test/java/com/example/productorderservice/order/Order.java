package com.example.productorderservice.order;

import com.example.productorderservice.product.Product;
import jdk.jfr.Enabled;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor
class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Product product;
    private int quantity;

    public Order(Product product, int quantity) {
        Assert.notNull(product, "상품은 필수");
        Assert.isTrue(quantity > 0, "수량은 0보다 커야 합니다.");
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

}
