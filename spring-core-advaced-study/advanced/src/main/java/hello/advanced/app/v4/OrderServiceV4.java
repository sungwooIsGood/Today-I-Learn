package hello.advanced.app.v4;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV4 {

    private final OrderRepositoryV4 orderRepository;
    private final LogTrace trace;

    /**
     *  제네릭에서 void타입은 Void객체로 받으며
     *  반환 타입은 null;로 해주면 된다.
     */
    public void orderItem(String itemId) throws IllegalAccessException {

        AbstractTemplate<Void> template = new AbstractTemplate(trace) {
            @Override
            protected Void call() throws IllegalAccessException {
                orderRepository.save(itemId);
                return null;
            }
        };
        template.execute("OrderService.reqeust()");
    }
}
