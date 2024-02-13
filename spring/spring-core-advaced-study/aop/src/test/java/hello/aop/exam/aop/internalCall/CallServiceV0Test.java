package hello.aop.exam.aop.internalCall;

import hello.aop.exam.aop.internalCall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(CallLogAspect.class)
@SpringBootTest
@Slf4j
class CallServiceV0Test {

    // Spring 컨테이너 안에는 프록시 객체가 있음. 아래는 프록시 객체
    @Autowired
    CallServiceV0 callServiceV0;

    /**
     * 1. 클라이언트가 external을 호출할 때 프록시를 호출하게 된다.
     * 2. AOP 프록시는 target.external() 앞단에(@Before)에 있어 프록시가 아닌 타켓 객체 호출한다.
     * 3. external()로 타겟 객체가 나오고 타겟 객체이기 때문에 internal은 AOP 프록시 대상이 되지 않았던 것이다.
     * 결과적으로 자기 자신(this) 인스턴스인 내부호출은 AOP 프록시 대상이 아니다!!!
     * AOP 프록시는 내부호출 때 적용이 안된다.
     */
    @Test
    void external() {
        // cglib인 프록시가 로그에 찍히는 것을 볼 수 있다.
        log.info("target={}",callServiceV0.getClass());
        callServiceV0.external();
        // internal은 external안에 있어 aop 로그가 안찍혀 있는 것을 확인 할 수 있다.
    }

    @Test
    void internal() {
        callServiceV0.internal();
    }

}