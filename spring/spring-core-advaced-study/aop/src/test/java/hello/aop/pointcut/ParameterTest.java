package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import(ParameterTest.ParameterAspect.class)
public class ParameterTest {

    @Autowired
    MemberService memberService;

    @Test
    void success(){
        log.info("memberService Proxy={}",memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ParameterAspect{

        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember(){}

//        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
            Object arg1 = joinPoint.getArgs()[0]; // [index]첫번째 파라미터 정보 받기
            log.info("[logArgs1] {}, arg={}",joinPoint.getSignature(), arg1);
            return joinPoint.proceed();
        }

        /**
         *
         * @param joinPoint
         * @param argTest -> @Around에서 작성한 args()안에 파라미터 값임. arg는 해당 메서드의 파라미터 명과 같아야한다.
         * @return
         * @throws Throwable
         */
//        @Around("allMember() && args(argTest,..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint,Object argTest) throws Throwable {
            log.info("[logArgs1] {}, arg={}",joinPoint.getSignature(), argTest);
            return joinPoint.proceed();
        }

//        @Before("allMember() && args(sameArg,..)") // 모든 타입의 로그를 찍고싶다면 Object를 이용하자.
        public void logArg3(String sameArg){ // String로 확정 시켰기에 String 이외에는 동작하지 않는다.
            log.info("[logArg3] sameArg={}", sameArg);
        }

//        @Before("allMember() && this(obj)") // 프록시 객체를 대상
        public void thisArgs(JoinPoint joinPoint, MemberService obj){
            log.info("[this]{}, obj={}",joinPoint.getSignature(),obj.getClass());
        }

//        @Before("allMember() && target(obj)") // 실제 대상 구현체 대상
        public void targetArgs(JoinPoint joinPoint, MemberService obj){
            log.info("[target]{}, obj={}",joinPoint.getSignature(),obj.getClass());
        }

//        @Before("allMember() && @target(annotation)")
        public void atTargetArgs(JoinPoint joinPoint, ClassAop annotation){
            log.info("[@target]{}, obj={}",joinPoint.getSignature(),annotation);
        }

//        @Before("allMember() && @within(annotation)")
        public void atwWithinArgs(JoinPoint joinPoint, ClassAop annotation){
            log.info("[@target]{}, obj={}",joinPoint.getSignature(),annotation);
        }

        @Before("allMember() && @annotation(annotation)")
        public void atAnnotationArgs(JoinPoint joinPoint, MethodAop annotation){
            // @Method("test value") -> 이 값이 전달됨
            log.info("[@target]{}, annotationValue={}",joinPoint.getSignature(),annotation.value());
        }
    }
}
