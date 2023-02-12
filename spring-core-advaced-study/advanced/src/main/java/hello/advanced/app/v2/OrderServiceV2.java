package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.helloTrace.HelloTraceV1;
import hello.advanced.trace.helloTrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 orderRepository;
    private final HelloTraceV2 trace;

    public void orderItem(String itemId, TraceId tracedId) throws IllegalAccessException {

        TraceStatus status = null;
        try{
            status = trace.beginSync(tracedId,"OrderService.reqeust()");
            orderRepository.save(itemId,tracedId);
            trace.end(status);
        }catch (Exception e){
            trace.exception(status,e);
            // 예외를 먹는 것이아닌 예외 발생시켜야한다. 로그 기능 때문에 예외를 먹으면 안된다.
            throw e;
        }
    }
}
