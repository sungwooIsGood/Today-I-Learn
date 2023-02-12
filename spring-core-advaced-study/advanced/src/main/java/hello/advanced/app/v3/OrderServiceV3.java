package hello.advanced.app.v3;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logTrace.FieldLogTrace;
import hello.advanced.trace.logTrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV3 {

    private final OrderRepositoryV3 orderRepository;
    private final LogTrace trace;

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
    }
}
