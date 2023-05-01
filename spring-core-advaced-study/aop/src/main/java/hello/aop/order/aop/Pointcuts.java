package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder(){}; // pointcut signature라고 한다.

    // 클래스 이름 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService(){}

    // allOrder() + allService()
    @Pointcut("allOrder() && allService()")
    public void orderAndService(){}
}
