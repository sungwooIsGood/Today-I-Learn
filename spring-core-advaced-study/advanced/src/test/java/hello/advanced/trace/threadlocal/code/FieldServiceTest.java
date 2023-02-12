package hello.advanced.trace.threadlocal.code;

import hello.advanced.trace.logTrace.FieldLogTrace;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FieldServiceTest {

    private FieldService fieldService = new FieldService();

    @Test
    void field() throws InterruptedException {
        log.info("main start");
        Runnable userA = () -> {
            fieldService.logic("userA");
        };

        Runnable userB = () ->{
            fieldService.logic("userB");
        };

        // threadA는 Runnable userA를 실행
        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");

        // threadB는 Runnable userB를 실행
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
//        sleep(2000); // 동시성문제가 아예 없는 코드
        sleep(100); // 동시성문제가 아예 없는 코드
        threadB.start();
        sleep(3000); // 메인 쓰레드 종료 대기
        log.info("main exit");
    }

    private void sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
