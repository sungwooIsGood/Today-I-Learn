package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ExecutionTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException{
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class); // 파라미터가 String이라 String.class로 한 것
    }

    @Test
    void printMethod(){
        log.info("helloMethod={}",helloMethod);
    }

    /**
     * execution(접근제어자? 반환타입 선언타입? 메서드이름(파라미터) 예외?)
     * ?는 생략가능 한 것
     * . 는 정확한 위치
     * ..은 하위 모든 위
     * public java.lang.String hello.aop.order.aop.member.MemberServiceImpl.hello(java.lang.String)
     */
    @Test
    @DisplayName("정확한 매칭 '?' 포함")
    void exactMath(){
        // 최상위 타입이 pointcut이라 setExpression으로 pointcut 설정 가능
        // 정확한 매칭
        pointcut.setExpression("execution(public String hello.aop.order.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    /**
     * '*' = 아무 값이나 들어와도 된다.
     * 파라미터에서 ..은 파라미터의 타입과 파라미터 수가 상관이 없다.
     */
    @Test
    @DisplayName("'?'를 제외한 '*'를 이용한 매칭")
    void allMatch(){
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 명만 정확하게 매칭")
    void nameMatch(){
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 명을 패턴 매칭 'hel'이 들어간 메서드는 다 pointcut 적용")
    void nameMatchStar1(){
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 명을 패턴 매칭 'el'이 들어간 메서드는 다 pointcut 적용")
    void nameMatchStar2(){
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("메서드 명을 패턴 매칭 'el'이 들어간 메서드는 다 pointcut 적용")
    void nameMatchFalse(){
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("정확한 패키지 명을 패턴 'pointcut 적용")
    void packageExactMatch1(){
        pointcut.setExpression("execution(* hello.aop.order.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName(" '*'를 이용한 패키지 명 'pointcut 적용 - aop 하위 패키지 모두, 모든 메서드 적용 파라미터 갯수, 종류 상관 없이")
    void packageExactMatch2(){
        pointcut.setExpression("execution(* hello.aop.order.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName(" '*'를 이용한 패키지 명 서브 패키지 - 하위 패키지 디렉토리 갯수가 딱 맞던가 ..을 이용해야한다. ")
    void packageMatchSubPackage1(){
        pointcut.setExpression("execution(* hello.aop.*.*.*.*.*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();

        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName(" '*'를 이용한 패키지 명 실패 케이스 - ")
    void packageExactFalse(){
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isFalse();
    }

    @Test
    void typeExactMatch(){
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("impl 대신 인터페이스 적용, 부모타입에다 적용하면 자식은 다 적용 된다.")
    void typeMatchSuperType(){
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("부모타입에다 적용하면 자식은 다 적용 되지만, 부모가 가지고있는 메서드만 적용된다.")
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod,MemberServiceImpl.class)).isTrue();
    }

    @DisplayName("String 타입의 파라미터 허용 '*(String)' ")
    @Test
    void argsMatch(){
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @DisplayName("파라미터가 없는 경우 '*()' ")
    @Test
    void argsMatchNoArgs(){
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @DisplayName("정확한 하나의 파라미터 허용, 모든 타입 허용, 즉, 파라미터 갯수만 같을 경우 '*(*)' ")
    @Test
    void argsMatchStar(){
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @DisplayName("모든 케이스를 다 만족, (), (Object x), (Object x, Object y) ... -> '*(*)' ")
    @Test
    void argsMatchAll(){
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @DisplayName("String 타입으로 시작 후 모든 타입 허용 (Stirng x), (String x, Object y) ... -> '*(String, ..)' ")
    @Test
    void argsMatchComplex(){
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }
}
