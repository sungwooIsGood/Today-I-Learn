package hello.aop.exam.aop.internalCall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * 구조 변경을 통한 내부호출 시 AOP 프록시 적용 예시
 * 대신 구조상 환경이 맞을 때만 적용 해야한다. 필수로 해야하는 것은 아니다.
 */
public class CallServiceV3 {

    private final InterService internalService;

    /**
     * 즉, 내부호출인 코드를 분리해서 외부 호출로 만들어 주어야 한다.
     */
    public void external(){
        log.info("call external");
        internalService.internal(); // 외부 메서드 호출
    }

}
