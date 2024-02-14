package com.example.productorderservice.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProductPatchServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPort productPort;

    @Test
    void 상품수정() {

        productService.addProduct(ProductSteps.상품등록요청_생성());

        final Long productId = 1L;
        String name = "상품수정";
        int price = 10000;
        DiscountPolicy discountPolicy = DiscountPolicy.NONE;

        UpdateProductRequest request = new UpdateProductRequest(name, price, discountPolicy);
        productService.updateProduct(productId, request);

        GetProductResponse response = productService.getProduct(productId);

        assertThat(response.name()).isEqualTo("상품수정");

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
