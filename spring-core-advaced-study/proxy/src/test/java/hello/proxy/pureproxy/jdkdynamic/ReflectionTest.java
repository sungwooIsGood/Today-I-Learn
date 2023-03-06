package hello.proxy.pureproxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {

    @Test
    void reflection0(){

        /**
         * 전체 로직이 비슷함.
         * 메서드만 다름.
         * 공통로직 1과 공통로직 2를 하나의 메서드를 뽑을 수 있을까?
         */
        Hello target = new Hello();

        // 공통 로직1 시작
        log.info("start");
        String result1 = target.callA();
        log.info("result={}",result1);
        // 공통 로직1 종료

        // 공통 로직2 시작
        log.info("start");
        String result2 = target.callB(); // 호출하는 메서드가 다름
        log.info("result={}",result2);
        // 공통 로직2 종료


    }

    @DisplayName("리플렉션을 적용해본 메서드")
    @Test
    void reflection1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // 클래스 정보를 얻는 메서드
        Class classHello = Class.forName("hello.proxy.pureproxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보를 얻어오는 메서드
        Method methodCallA = classHello.getMethod("callA");

        /**
         * callA()를 호출한다. target인스턴스의 있는 callA를 호출한다.
         */
        Object result1 = methodCallA.invoke(target);
        log.info("result={}",result1);

        // callB 메서드 정보를 얻어오는 메서드
        Method methodCallB = classHello.getMethod("callB");

        /**
         * callB()를 호출한다. target인스턴스의 있는 callA를 호출한다.
         */
        Object result2 = methodCallB.invoke(target);
        log.info("result={}",result2);

    }

    /**
     * 이 좋은 리플렉션은 가급적 '지양'한다.
     * 런타임 시점에 동작하기 때문에 컴파일 시점에 오류를 잡지 못한다.
     * 예를들어 서비스 배포하고 클라이언트가 해당 메서드를 호출할 때 에러가 발생! 치명적!
     */
    @DisplayName("동적 리플렉션을 적용해본 메서드")
    @Test
    void reflection2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // 클래스 정보를 얻는 메서드
        Class classHello = Class.forName("hello.proxy.pureproxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보를 얻어오는 메서드
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA,target); // 동적 리플렉션 적용


        // callB 메서드 정보를 얻어오는 메서드
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB,target);

    }

    private void dynamicCall(Method method, Object target) throws InvocationTargetException, IllegalAccessException {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}",result);
    }



    @Slf4j
    static class Hello{
        public String callA(){
            log.info("callA");
            return "A";
        }

        public String callB(){
            log.info("callB");
            return "B";
        }
    }
}
