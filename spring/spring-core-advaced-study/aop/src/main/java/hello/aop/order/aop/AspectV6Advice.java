package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Bean;

@Slf4j
@Aspect
public class AspectV6Advice {

    /**
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     * ProceedingJoinPoint 파라미터를 꼭 써주어야한다.
     * joinPoint.proceed()를 사용하지 않으면 타겟 메서드가 호출이 안된다.
     * 때문에 타겟을 호출하지 않으면 장애를 유발할 수 있다. 개발자의 실수가 있으면 안된다.
     */
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint)throws Throwable{

        try{
            // @Before
            log.info("[트랜잭션 시작] {}",joinPoint.getSignature());
            Object result = joinPoint.proceed();

            // @AfterReturning
            log.info("[트랜잭션 커밋] {}",joinPoint.getSignature());
            return result;
        } catch (Exception e){
            // @AfterThrowing
            log.info("[트랜잭션 롤백] {}",joinPoint.getSignature());
            throw e;
        } finally {
            // @After
            log.info("[리소스 릴리스] {}",joinPoint.getSignature());
        }
    }

    /**
     * Before
     * @param joinPoint -> ProceedingJoinPoint는 @Around만 가능 JoinPoint만 써야한다.
     * @Around와 다르게 joinPoint.proceed()를 하지 않아도 된다.
     */
    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore1(JoinPoint joinPoint){
        log.info("[before] {}",joinPoint.getSignature());
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore2(){
        log.info("[before] joinPoint 객체가 없어도 된다.");
    }

    /**
     * AfterReturning
     * 파라미터의 return값과 returning의 이름이 매칭된다.
     * return값을 조작 할 수는 없다.
     * return 은 aop가 적용된 메서드가 끝나고 return 된 값을 받아오는 것이다.
     * void 타입의 메서드라면 String과 매칭이 안되는 것처럼 aop가 적용될 메서드들의 타입과 같아야한다.
     * 다르다면 aop가 적용이 안되서 호출 자체가 안된다!!!
     */
    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, String result){
        log.info("[return] {}, [result] {}", joinPoint.getSignature(), result);
    }

    /**
     *
     * @param joinPoint
     * @param ex
     * AfterReturning와 같은 원리로 예외도 타입이 맞아야한다.
     * 부모 타입은 적용 가능 Exception -> IllegalException 이러면 가능
     */
    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex){
        log.info("[ex] {} [message] {}", ex);
    }

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint){
        log.info("[after] {}",joinPoint.getSignature());
    }
}
