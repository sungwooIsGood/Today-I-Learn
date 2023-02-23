package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryInterfaceProxy implements OrderRepositoryV1 {

    private final OrderRepositoryV1 target;
    private final LogTrace logTrace;

    @Override
    public void save(String itemId) {

        TraceStatus status = null;
        try{
            status = logTrace.begin("OrderRepository.request()");

            // target 호출 -> target은 프록시에서 호출할 대상을 target이라고 한다.
            // 부가기능 로직은 proxy에서 처리한다.
            target.save(itemId);
            logTrace.end(status);
        } catch (Exception e){
            logTrace.exception(status,e);
            throw e;
        }
    }
}
