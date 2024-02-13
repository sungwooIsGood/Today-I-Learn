package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

public class OrderServiceConcreteProxy extends OrderServiceV2 {

    private final OrderServiceV2 target;
    private final LogTrace logTrace;

    public OrderServiceConcreteProxy(OrderServiceV2 target, LogTrace logTrace) {
        // 부모타입 기본생성자이다. -> super();
        // 부모인 orderServiceV2를 보면 기본 생성자가 아니라 에러를 나타내는 빨간줄이 나타났다.
        // 부모 타입의 생성자를 강제로 사용해야하기 때문이다. -> super(new OrderRepositoryV2());
        // 하지만 부모 타입의 생성자를 사용하지 않을 것이기 때문에 null로 주입.
        super(null);
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public void orderItem(String itemId) {
        TraceStatus status = null;
        try{
            status = logTrace.begin("OrderService.orderItem()");

            target.orderItem(itemId);
            logTrace.end(status);
        } catch (Exception e){
            logTrace.exception(status,e);
            throw e;
        }
    }
}
