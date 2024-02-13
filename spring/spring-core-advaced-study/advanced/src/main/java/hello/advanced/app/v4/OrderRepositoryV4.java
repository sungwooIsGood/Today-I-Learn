package hello.advanced.app.v4;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV4 {

    private final LogTrace trace;

        /**
         *  제네릭에서 void타입은 Void객체로 받으며
         *  반환 타입은 null;로 해주면 된다.
         */
    public void save(String itemId) throws IllegalAccessException {

        AbstractTemplate<Void> template = new AbstractTemplate(trace) {
            @Override
            protected Void call() throws IllegalAccessException {
                // 저장로직
                if(itemId.equals("ex")){
                    throw new IllegalAccessException("예외발생!!");
                }
                sleep(1000);
                return null;
            }
        };
        template.execute("OrderRepository.reqeust()");
    }


    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
