package com.example.productorderservice.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ProductReadServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void 상품조회(){
        // 상품 등록(3)
        productService.addProduct(ProductSteps.상품등록요청_생성());
        final long productId = 1L;

        // 상품 조회(2)
        GetProductResponse response = productService.getProduct(productId);

        // 상품 검증(1)
        assertThat(response).isNotNull();
    }
}
