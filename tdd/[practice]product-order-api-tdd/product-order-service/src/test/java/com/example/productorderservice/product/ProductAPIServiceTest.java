package com.example.productorderservice.product;

import com.example.productorderservice.APITest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 랜덤 포트 생성
public class ProductAPIServiceTest extends APITest {

    @Test
    void 상품등록_API(){
        // API 요청
        final AddProductRequest request = ProductSteps.상품등록요청_생성();

        ExtractableResponse<Response> response = ProductSteps.상품등록요청(request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 상품조회_API(){
        Long productId = 1L;

        ExtractableResponse<Response> response = ProductSteps.상품조회요청(productId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
