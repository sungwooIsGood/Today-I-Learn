package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AopTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("aop 됐는지 확인")
    void aopInfo(){
        // AopUtils.isAopProxy() -> aop 적용 확인용 메서드
        log.info("isAopProxy, orderService={},",AopUtils.isAopProxy(orderService));
        log.info("isAopProxy, orderRepository={},",AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success(){
        orderService.orderItem("itemA");
    }

    @Test
    void exception(){
        // 예외 잡기 위한 Assertions
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf((IllegalStateException.class));
    }
}
