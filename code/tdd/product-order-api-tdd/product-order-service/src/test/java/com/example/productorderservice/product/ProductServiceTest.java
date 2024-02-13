package com.example.productorderservice.product;

import com.example.productorderservice.APITest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import static com.example.productorderservice.product.ProductSteps.상품등록요청;
import static com.example.productorderservice.product.ProductSteps.상품등록요청_생성;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;


    @Test
    void 상품등록테스트(){

        // API 요청
        final AddProductRequest request = 상품등록요청_생성();

        productService.addProduct(request);
    }



}
