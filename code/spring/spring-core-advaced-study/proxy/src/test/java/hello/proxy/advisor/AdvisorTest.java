package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

@Slf4j
public class AdvisorTest {

    @Test
    @DisplayName("어드바이저 테스트")
    void advisorTest1() {
        ServiceImpl target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // advisor -> pointcut과 advisor을 다 가지고 있다.
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());

        // 프록시 팩토리 안에 advisor를 넣어준다.
        // 프록시 팩토리는 advisor가 필수!!!!
        proxyFactory.addAdvisor(advisor);

        // advisor(pointcut, advisor) -> 프록시 팩토리 -> 프록시
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        // client -> 프록시 호출 -> 프록시는 어드바이저에서 pointcut과 advice를 호출
        proxy.save();
        proxy.find();
    }

    @Test
    @DisplayName("pointcut 직접구현")
    void advisorTest2() {
        ServiceImpl target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // advisor -> pointcut과 advisor을 다 가지고 있다.
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());

        // 프록시 팩토리 안에 advisor를 넣어준다.
        // 프록시 팩토리는 advisor가 필수!!!!
        proxyFactory.addAdvisor(advisor);

        // advisor(pointcut, advisor) -> 프록시 팩토리 -> 프록시
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        // client -> 프록시 호출 -> 프록시는 어드바이저에서 pointcut과 advice를 호출
        proxy.save();
        proxy.find();
    }

    @Test
    @DisplayName("스프링이 제공하는 pointcut")
    void advisorTest3() {
        ServiceImpl target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // 스프링이 제공하는 포인트컷
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();

        // save()만 pointcut 적용
        pointcut.setMappedNames("save");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);

        // advisor(pointcut, advisor) -> 프록시 팩토리 -> 프록시
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        // save()만 pointcut, find는 pointcut적용x 프록시 적용안됨.
        proxy.save();
        proxy.find();
    }


    // pointcut 구현체
    static class MyPointcut implements Pointcut {
        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    // MethodMatcher 구현체 - pointcut에 사용될 method filter를 구현하는 것
    static class MyMethodMatcher implements MethodMatcher {

        private String matchName = "save";

        // 이것만 봐도 됨.
        // pointcut이 적용될 메서드 구현.
        // 요구사항: save() 메서드만 프록스에서 pointcut 구현하자. true값만 pointcut 적용
        @Override
        public boolean matches(Method method, Class<?> targetClass) {

            boolean result = method.getName().equals(matchName);
            log.info("포인트컷 호출 method={}, targetClass={}", method.getName(), targetClass);
            log.info("포인트컷 결과:{}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }
}
