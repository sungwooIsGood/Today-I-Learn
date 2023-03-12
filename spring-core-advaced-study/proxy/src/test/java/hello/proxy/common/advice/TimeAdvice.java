package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class TimeAdvice implements MethodInterceptor {

    // proxyFactory인 객체가 사용할 메서드
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        /**
         * target 정보가 없어도 프록시 팩토리를 생성할 때 인수값으로 넘겨 주기 때문에
         * proceed를 통해 target 클래스를 찾아 알아서 실행을 해준다.
         */
        Object result = invocation.proceed();

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}",resultTime);
        return result;
    }
}
