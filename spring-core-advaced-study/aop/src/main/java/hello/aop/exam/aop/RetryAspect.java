package hello.aop.exam.aop;

import hello.aop.exam.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class RetryAspect {

    @Around("@annotation(retry)") // 파라미터의 Retry annotaion을 적고, @Around 타입을 적으면 매핑이 이루어 진다. 많이 깔끔 해진다.
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {

        log.info("[retry] {} retry={}",joinPoint.getSignature(),retry);
        int maxRetry = retry.value(); // Retry 안에 값을 설정할 수 있으면 왼쪽 코드와 같이 값을 꺼내올 수도 있다.
        Exception exceptionHolder = null;

        for(int retryCount = 1; retryCount <= maxRetry; retryCount++){
            try{
                log.info("[retry] try count{}/{}",retryCount,maxRetry);
                return joinPoint.proceed();
            } catch (Exception e){
                exceptionHolder = e;
                log.info("exceptionHolder", e);
            }
        }
        throw exceptionHolder; // 완전 예상하지 못한 상황에 대한 예외를 던저 준 것이다.

    }
}
