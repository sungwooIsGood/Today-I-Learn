package hello.advanced.app.v3;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.helloTrace.HelloTraceV2;
import hello.advanced.trace.logTrace.FieldLogTrace;
import hello.advanced.trace.logTrace.LogTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV3 {

    private final OrderServiceV3 orderService;

    // 기존에 HelloTrace 대신 인터페이스인 FieldLogTrace를 사용한다.
    // traceId를 파라미터로 더이상 전달하여 주지 않아도 된다.
    private final LogTrace trace;


    @GetMapping("/v3/request")
    public String request(String itemId) throws IllegalAccessException {
        TraceStatus status = null;
        try{
            status = trace.begin("OrderController.reqeust()");
            orderService.orderItem(itemId);
            trace.end(status);
        }catch (Exception e){
            trace.exception(status,e);
            // 예외를 먹는 것이아닌 예외 발생시켜야한다. 로그 기능 때문에 예외를 먹으면 안된다.
            throw e;
        }

        return "ok";
    }
}
