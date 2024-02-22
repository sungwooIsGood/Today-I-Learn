package com.example.productorderservice.payment;

import com.example.productorderservice.product.DiscountPolicy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DiscountPolicyTest {

    @Test
    void applyDiscount(){
        int price = 1000;
        int discountPrice = DiscountPolicy.NONE.applyDiscount(price);

        assertThat(discountPrice).isEqualTo(price);
    }

    @Test
    void name(){
        int price = 2000;
        int discountPrice = DiscountPolicy.FIX_1000_AMUNT.applyDiscount(price);

        assertThat(discountPrice).isEqualTo(1000);
    }
}
