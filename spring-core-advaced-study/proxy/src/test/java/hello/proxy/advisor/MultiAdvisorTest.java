package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

@Slf4j
public class MultiAdvisorTest {

    @Test
    @DisplayName("다중 프록시 적용")
    /**
     * 단점: 다중 advisor를 적용하려면 proxy를 여러개 생성해야한다.
     * 이러한 단점을 보완하기 위해 스프링은 하나의 프록시로, 여러 advisor를 만들 수 있다.
     * test2를 보자.
     */
    void multiAdvisorTest1(){
        // client -> proxy2(advisor) -> proxy1(advisor1) -> target

        // 프록시1 생성
        ServiceImpl target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory.getProxy();

        // 프록시2 생성
        // 주의!!! target을 넣는 것이 아닌 proxy1을 넣어주어야 한다.
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        // 실행
        // 기대값: 클라이언트가 proxy2를 호출 proxy2는 proxy1를 호출 proxy1은 target을 호출
        proxy2.save();
    }

    @Test
    @DisplayName("하나의 proxy, 여러 adviosr")
    /**
     * 프록시를 여러개 사용하는 것보다 하나의 프록시로 여러 advisor를 관리 하는 것이 성능의 좋다.
     * 이는 AOP 시에도 동일하게 적용된다. 하나의 프록시의 여러 advisor가 적용된다.
     * 때문에 test1, test2를 나눠서 공부해본 것이다.
     */
    void multiAdvisorTest2(){
        // client -> proxy -> advisor2 -> advisor1 -> target

        // 다중 advisor 생성
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

        // 프록시 생성
        ServiceImpl target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        // 순서 중요. advisor2가 먼저 호출 되도록 만든 것.
        proxyFactory.addAdvisor(advisor2);
        proxyFactory.addAdvisor(advisor1);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        // 실행
        // 기대값: 클라이언트가 proxy를 (advisor2 -> advisor1) -> target
        proxy.save();
    }

    @Slf4j
    static class Advice1 implements MethodInterceptor{
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {

            log.info("advice1 호출");

            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor{
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {

            log.info("advice2 호출");

            return invocation.proceed();
        }
    }
}
