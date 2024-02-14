package com.example.productorderservice.product;

import org.springframework.util.Assert;

record UpdateProductRequest(String name, int price, DiscountPolicy discountPolicy) {
    UpdateProductRequest {
        Assert.hasText(name, "상품명은 필수");
        Assert.isTrue(price > 0, "0원보다 커야합니다.");
        Assert.notNull(discountPolicy, "할인 정책은 필수입니다.");

    }
}
