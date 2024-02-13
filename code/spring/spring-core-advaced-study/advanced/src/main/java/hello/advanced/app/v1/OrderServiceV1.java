package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.helloTrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {

    private final OrderRepositoryV1 orderRepository;
    private final HelloTraceV1 trace;

    public void orderItem(String itemId) throws IllegalAccessException {

        TraceStatus status = null;
        try{
            status = trace.begin("OrderService.reqeust()");
            orderRepository.save(itemId);
            trace.end(status);
        }catch (Exception e){
            trace.exception(status,e);
            // 예외를 먹는 것이아닌 예외 발생시켜야한다. 로그 기능 때문에 예외를 먹으면 안된다.
            throw e;
        }

        orderRepository.save(itemId);
    }
}
