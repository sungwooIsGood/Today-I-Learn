package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

@Slf4j
public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy(){
        ServiceInterface target = new ServiceImpl();

        /**
         * 팩토리를 생성하면서 target을 인자값으로 넘겨주면서
         * proxyFactory는 이미 target의 정보를 다 알고 있다.
         */
        ProxyFactory proxyFactory = new ProxyFactory(target);

        // MethodInvocation을 상속받은 advice를 인수로 넣어 준다.
        proxyFactory.addAdvice(new TimeAdvice());

        // getAdvice를 통해 프록시 팩토리에서 advice를 꺼낸 모습이다.
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("targetClass={}",target.getClass());
        log.info("proxyFactory={}",proxy.getClass());

        proxy.save();

        // 프록시 팩토리를 적용했을 때 프록시 제대로 동작 됐는지 확인 해볼수 있는 메서드
        AopUtils.isAopProxy(proxy);
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();

        // jdk 동적 프록시인지 확인
        AopUtils.isJdkDynamicProxy(proxy);
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();

        // CGLIB 확인
        AopUtils.isCglibProxy(proxy);
        // false인 이유 JDK 동적 프록시이기 때문, JDK 동적 프록시는 인터페이스 상속을 받기 때문에.
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사")
    void concreteProxy(){
        ConcreteService target = new ConcreteService();

        /**
         * 팩토리를 생성하면서 target을 인자값으로 넘겨주면서
         * proxyFactory는 이미 target의 정보를 다 알고 있다.
         */
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());

        // getAdvice를 통해 프록시 팩토리에서 advice를 꺼낸 모습이다.
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();

        log.info("targetClass={}",target.getClass());
        log.info("proxyFactory={}",proxy.getClass());

        proxy.call();

        // 프록시 팩토리를 적용했을 때 프록시 제대로 동작 됐는지 확인 해볼수 있는 메서드
        AopUtils.isAopProxy(proxy);
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();

        // jdk 동적 프록시인지 확인
        AopUtils.isJdkDynamicProxy(proxy);
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();

        // CGLIB 확인
        AopUtils.isCglibProxy(proxy);
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스기반 프록시 사용 가능")
    void proxyTargetClass(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        /**
         * CGLIB를 기반으로 생성하는 옵션
         * target은 인터페이스지만 구체클래스 기반으로 하는 CGLIB가 적용된다.
         */
        proxyFactory.setProxyTargetClass(true);

        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("targetClass={}",target.getClass());
        log.info("proxyFactory={}",proxy.getClass());

        proxy.save();

        // 프록시 팩토리를 적용했을 때 프록시 제대로 동작 됐는지 확인 해볼수 있는 메서드
        AopUtils.isAopProxy(proxy);
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();

        // jdk 동적 프록시인지 확인
        AopUtils.isJdkDynamicProxy(proxy);
        // proxy 옵션으로 인해 false
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();

        // CGLIB 확인
        AopUtils.isCglibProxy(proxy);
        // proxy 옵션으로 인해 true
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }
}
