package hello.aop.exam.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

@Slf4j
public class ProxyCastingTest {

    /**
     * 인터페이스 기반 프록시 - JDK프록시
     * 구체 클래스 기반 프록시 - CGLIB
     */
    @Test
    void jdkProxy(){
        MemberServiceImpl target = new MemberServiceImpl(); // 구체 클래스
        ProxyFactory proxyFactory = new ProxyFactory(target);
        // 기본 옵션 외의 해당 옵션을 통해 프록시 종류 선택 가능
        proxyFactory.setProxyTargetClass(false); // false 혹은 생략하면 자동 false JDK 동적 프록시 적용


        // 프록시를 인터페이스로 캐스팅 성공
        MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

        // JDK 동적 프록시를 구현 클래스 캐스팅 시도 실패, cLassCastException 예외 발생
        Assertions.assertThrows(ClassCastException.class, ()-> {
            MemberServiceImpl memberService = (MemberServiceImpl) memberServiceProxy;
        });
    }

    @Test
    void cglibProxy(){
        MemberServiceImpl target = new MemberServiceImpl(); // 구체 클래스
        ProxyFactory proxyFactory = new ProxyFactory(target);
        // 기본 옵션 외의 해당 옵션을 통해 프록시 종류 선택 가능
        proxyFactory.setProxyTargetClass(true); // true면 CGLIB 프록시 적용


        // 프록시를 인터페이스로 캐스팅 성공
        MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

        // CGLIB 프록시를 구현 클래스 캐스팅 시도 성공
        Assertions.assertThrows(ClassCastException.class, ()-> {
            MemberServiceImpl memberService = (MemberServiceImpl) memberServiceProxy;
        });

        /**
         * 한마디로 정의하면
         * 부모(인터페이스)는 자식(구현 클래스)이 타입이 될 수 없고 자식은 부모 타입으로 타입변환이 가능하다.
         * CGLIB 시 할아버지(MemberService) <- 아버지(MemberServiceImpl) <- 나(프록시) 가능
         * JDK 동적 프로시 시 할아버지(MemberService) -> 아버지(MemberServiceImpl) -> 나(프록시) 불가능
         */



    }
}
