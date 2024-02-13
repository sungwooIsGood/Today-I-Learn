package com.example.productorderservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

public class APITest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp(){
        RestAssured.port = port;
    }
}
