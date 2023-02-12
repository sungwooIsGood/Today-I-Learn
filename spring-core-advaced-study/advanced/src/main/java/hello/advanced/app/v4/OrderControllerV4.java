package hello.advanced.app.v4;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV4 {

    private final OrderServiceV4 orderService;


    // 기존에 HelloTrace 대신 인터페이스인 FieldLogTrace를 사용한다.
    // traceId를 파라미터로 더이상 전달하여 주지 않아도 된다.
    private final LogTrace trace;


    @GetMapping("/v4/request")
    public String request(String itemId) throws IllegalAccessException {

        AbstractTemplate<String> template = new AbstractTemplate<>(trace) {
            @Override
            protected String call() throws IllegalAccessException {
                orderService.orderItem(itemId);
                return "ok";
            }
        };

        String execute = template.execute("OrderController.reqeust()");
        return execute;

    }
}
