package hello.aop.exam.aop.internalCall.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV2 {

//    private final ApplicationContext applicationContext;
    // 지연조회를 위한 ObjectProvider
    private final ObjectProvider<CallServiceV2> callServiceV2ObjectProvider;
    public CallServiceV2(ObjectProvider<CallServiceV2> callServiceV2ObjectProvider) {
        this.callServiceV2ObjectProvider = callServiceV2ObjectProvider;
    }

    public void external(){
        log.info("call external");
//        CallServiceV2 callServiceV2 = applicationContext.getBean(CallServiceV2.class);
        /**
         * 스프링 빈 생성 시점이 아니라 실제 객체를 사용하는 시점으로 지연할 수 있다.
         * 아래 getObject();를 호출하는 시점에 스프링 컨테이너에서 빈을 조회한다.
         */
        CallServiceV2 callServiceV2 = callServiceV2ObjectProvider.getObject();
        callServiceV2.internal(); // 내부 메서드 호출(this.internal())
    }

    public void internal(){
        log.info("call internal");
    }
}
