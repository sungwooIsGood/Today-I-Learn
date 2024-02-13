package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * 어드바이스 순서 !!
 * Advice별로 순서를 보장받지는 못한다.
 * 클래스 별로 Order 어노테이션을 걸어주어야 한다.
 */
@Slf4j
@Aspect
public class AspectV5Order {

    /**
     * 그럼 어떻게 한 클래스 안에서 advice 의 순서를 보장받게 할 수 있을까?
     * 내부 클래스를 생성하여 내부 클래스에다가 @Order를 붙여주면 된다.
     * 즉, Aspect 별로 외부 클래스를 만들어도 되고, 내부 클래스로 해도 된다.
     * 편한대로 사용하자.
     */
    @Aspect
    @Order(2)
    public static class LogAspect{
        // 패키지명으로 가져오면 외부 pointcut가져올 수 있음
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[log] {}",joinPoint.getSignature()); // join point 시그니처 - 메서드 정보가 찍힌다고 보면 된다.
            return joinPoint.proceed(); // 실제 타겟 호출
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect{
        // hello.aop.order 패키지와 하위 패키지 이면서 동시에 클래스 이름 패턴이 *Service인 것
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
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
}
