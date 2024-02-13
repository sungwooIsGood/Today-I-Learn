package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallback;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderControllerV5 {

    private final OrderServiceV5 orderService;


    // 기존에 HelloTrace 대신 인터페이스인 FieldLogTrace를 사용한다.
    // traceId를 파라미터로 더이상 전달하여 주지 않아도 된다.
    private final TraceTemplate traceTemplate;

    // @Autowired 생략가능
    public OrderControllerV5(OrderServiceV5 orderService, TraceTemplate traceTemplate, LogTrace trace) {
        this.orderService = orderService;
        this.traceTemplate = new TraceTemplate(trace);
    }

    @GetMapping("/v5/request")
    public String request(String itemId) throws IllegalAccessException {
       return traceTemplate.execute("OrderController.reqeust()", new TraceCallback<>() {
            @Override
            public String call() throws IllegalAccessException {
                orderService.orderItem(itemId);
                return "ok";
            }
        });

    }
}
