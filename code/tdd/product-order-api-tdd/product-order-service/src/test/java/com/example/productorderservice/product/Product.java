package com.example.productorderservice.product;

import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "products")
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private DiscountPolicy discountPolicy;

    public Product() {}

    public Product(Long id, String name, int price, DiscountPolicy discountPolicy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discountPolicy = discountPolicy;
    }

    public Product(String name, int price, DiscountPolicy discountPolicy) {
        Assert.hasText(name, "상품명은 필수");
        Assert.isTrue(price > 0, "0원보다 커야합니다.");
        Assert.notNull(discountPolicy, "할인 정책은 필수입니다.");
        this.name = name;
        this.price = price;
        this.discountPolicy = discountPolicy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }
}
