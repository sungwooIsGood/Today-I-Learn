package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV2 {

    // pointcut을 따로 분리함으로써 여러군대서 사용이 가능, 모듈화가 가능하게 된다.
    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder(){}; // pointcut signature라고 한다.

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("[log] {}",joinPoint.getSignature()); // join point 시그니처 - 메서드 정보가 찍힌다고 보면 된다.
        return joinPoint.proceed(); // 실제 타겟 호출
    }
}
