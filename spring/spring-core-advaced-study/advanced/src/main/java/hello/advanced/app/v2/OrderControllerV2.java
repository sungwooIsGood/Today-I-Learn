package hello.advanced.app.v2;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.helloTrace.HelloTraceV1;
import hello.advanced.trace.helloTrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderServiceV2 orderService;
    private final HelloTraceV2 trace;


    @GetMapping("/v2/request")
    public String request(String itemId) throws IllegalAccessException {
        TraceStatus status = null;
        try{
            status = trace.begin("OrderController.reqeust()");
            orderService.orderItem(itemId,status.getTraceId());
            trace.end(status);
        }catch (Exception e){
            trace.exception(status,e);
            // 예외를 먹는 것이아닌 예외 발생시켜야한다. 로그 기능 때문에 예외를 먹으면 안된다.
            throw e;
        }


        return "ok";
    }
}
