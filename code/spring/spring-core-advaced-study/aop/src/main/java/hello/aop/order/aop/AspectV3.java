package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV3 {

    // pointcut을 따로 분리함으로써 여러군대서 사용이 가능, 모듈화가 가능하게 된다.
    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder(){}; // pointcut signature라고 한다.

    // 클래스 이름 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService(){}

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("[log] {}",joinPoint.getSignature()); // join point 시그니처 - 메서드 정보가 찍힌다고 보면 된다.
        return joinPoint.proceed(); // 실제 타겟 호출
    }

    // hello.aop.order 패키지와 하위 패키지 이면서 동시에 클래스 이름 패턴이 *Service인 것
    @Around("allOrder() && allService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint)throws Throwable{

        try{
            log.info("[트랜잭션 시작] {}",joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}",joinPoint.getSignature());
            return result;
        } catch (Exception e){
            log.info("[트랜잭션 롤백] {}",joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리스] {}",joinPoint.getSignature());
        }
    }
}
