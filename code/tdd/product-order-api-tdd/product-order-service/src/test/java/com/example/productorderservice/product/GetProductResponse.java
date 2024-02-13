package com.example.productorderservice.product;

import org.springframework.util.Assert;

record GetProductResponse(
        Long id,
        String name,
        int price,
        DiscountPolicy discountPolicy
) {
    public GetProductResponse {
        Assert.notNull(id, "상품 ID는 필수");
        Assert.hasText(name, "상품명은 필수");
        Assert.isTrue(price>0,"상품은 0원 이상");
        Assert.notNull(discountPolicy, "할인 정책은 필수");
    }
}
