package com.example.productorderservice.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductPatchServiceTest {

    private ProductService productService;
    private ProductPort mock;
    @BeforeEach
    void setup(){
         mock = Mockito.mock(ProductPort.class);
        productService = new ProductService(mock);
    }

    @Test
    void 상품수정() {
        final Long productId = 1L;

        String name = "상품수정";
        int price = 10000;
        DiscountPolicy discountPolicy = DiscountPolicy.NONE;
        Product product = new Product("상품", 100, DiscountPolicy.NONE);
        Mockito.when(mock.getProduct(productId)).thenReturn(product);

        UpdateProductRequest request = new UpdateProductRequest(name, price, discountPolicy);

        productService.updateProduct(productId, request);
    }

    @Test
    void update() {

        String name = "상품수정";
        int price = 10000;
        DiscountPolicy discountPolicy = DiscountPolicy.NONE;
        Product product = new Product("상품", 11, discountPolicy);
        product.update(name,price,discountPolicy);

        assertThat(product.getName()).isEqualTo("상품수정");
        assertThat(product.getPrice()).isEqualTo(10000);
        assertThat(product.getDiscountPolicy()).isEqualTo(discountPolicy);
    }
}
