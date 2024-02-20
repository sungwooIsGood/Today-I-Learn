package com.example.productorderservice.order;

import com.example.productorderservice.APITest;
import com.example.productorderservice.product.AddProductRequest;
import com.example.productorderservice.product.ProductService;
import com.example.productorderservice.product.ProductSteps;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 랜덤 포트 생성
public class OrderServiceTest extends APITest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;




    @Test
    void 상품주문(){

        // given
        productService.addProduct(ProductSteps.상품등록요청_생성());

        final Long productId = 1L;
        final int quantity = 2;
        final CreateOrderRequest request = new CreateOrderRequest(productId, quantity);
        orderService.createOrder(request);
    }

    @Test
    void API테스트(){
        AddProductRequest 상품등록요청_생성 = ProductSteps.상품등록요청_생성();

        ExtractableResponse<Response> response = 상품주문요청(상품등록요청_생성);

        assertThat(response).isEqualTo(HttpStatus.CREATED.value());
    }
    private static ExtractableResponse<Response> 상품주문요청(AddProductRequest 상품등록요청_생성) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(상품등록요청_생성)
                .when()
                .post("/orders")
                .then()
                .log().all().extract();
        return response;
    }
}
