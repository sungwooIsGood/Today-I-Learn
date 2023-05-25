package hello.aop.exam.aop.internalCall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@RequiredArgsConstructor 생성자 주입x
public class CallServiceV1 {

    // 생성자 주입은 절대 안된다. 순환참조 에러 발생
    private CallServiceV1 callServiceV1;

    /**
     * https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes
     * 스프링부트 2.6릴리즈 버전부터는 순환 참조를 금지하도록 변경됐다.
     * spring.main.allow-circular-references=true
     * 를 통해 임시적으로 풀어준다.(application.properties 파일)
     */
    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        log.info("callServiceV1 setter={}", callServiceV1.getClass()); // setter 사용시 프록시가 들어오는 것을 확인 할 수 있다.
        this.callServiceV1 = callServiceV1;
    }

    public void external(){
        log.info("call external");
        callServiceV1.internal();
    }

    public void internal(){
        log.info("call internal");
    }
}
