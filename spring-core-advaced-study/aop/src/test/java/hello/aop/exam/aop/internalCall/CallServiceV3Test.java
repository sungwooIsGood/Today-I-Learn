package hello.aop.exam.aop.internalCall;

import hello.aop.exam.aop.internalCall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(CallLogAspect.class)
@SpringBootTest
@Slf4j
class CallServiceV3Test {

    // Spring 컨테이너 안에는 프록시 객체가 있음. 아래는 프록시 객체
    @Autowired
    CallServiceV1 callServiceV3;

    @Test
    void external() {
        // setter주입 시 cglib인 프록시가 로그에 찍히는 것을 볼 수 있다.
        callServiceV3.external();
    }

}