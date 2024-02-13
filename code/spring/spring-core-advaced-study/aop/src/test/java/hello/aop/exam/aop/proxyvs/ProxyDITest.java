package hello.aop.exam.aop.proxyvs;

import hello.aop.exam.aop.proxyvs.code.ProxyDIAspect;
import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
// Spring boot는 기본적으로 CGLIB이다. 아래 옵션을 통해 JDK 동적 프록시로 변환 해준다. 물론 인터페이스가 없으면 CGLIB로 동작한다.
// 또한 아래 properties는 application.properties에서 적는 것과 같다.
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) // JDK 동적 프록시
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) // CGLIB, 없어도 적용
@SpringBootTest
@Import(ProxyDIAspect.class)
public class ProxyDITest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberServiceImpl memberServiceImpl;

    /**
     * 아래는 빌드가 안된다!! 이유는?
     * JDK 동적프록시 - 인터페이스만 있을 때는 문제가 없다. JDK 동적 프록시는 memberService를 구현한 것이기 때문에 알 수 있다.
     * Impl은 구체클래스이다. JDK는 memberService는 알아도 memberServiceImpl을 전혀 알지 못한다.
     * 첫 문장에서 말했듯이 JDK는 memberService를 기반으로 만들어졌기 때문이다.
     * $Proxy53는 memberService에서 태생된 것 당연히 impl을 찾는다면 모른다. 즉, 자식이 누구인지 모른다.
     */
    @Test
    void jdkGo(){
        log.info("memberService class={}",memberService.getClass());
//        log.info("memberServiceImpl class={}",memberServiceImpl.getClass());
//        memberServiceImpl.hello("hello");
    }

    /**
     * CGLIB는 구체클래스를 상속받아서 만들어진다.
     * 때문에 프록시는 구체클래스(memberService)를 알고 구체클래스는 인터페이스(memberServiceImpl)를 안다.
     */
    @Test
    void cglibGo(){
        log.info("memberService class={}",memberService.getClass());
        log.info("memberServiceImpl class={}",memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }


}
