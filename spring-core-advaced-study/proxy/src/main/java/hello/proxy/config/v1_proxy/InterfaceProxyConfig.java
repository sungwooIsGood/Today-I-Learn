package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프록시를 빈 등록
 */
@Configuration
public class InterfaceProxyConfig {

    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace){
//        return new OrderControllerV1Impl(orderService()); // 구현체를 반환하면 안된다. 프록시를 반환 시키자
        OrderControllerV1Impl controllerImpl = new OrderControllerV1Impl(orderService(logTrace)); // target
        return new OrderControllerInterfaceProxy(controllerImpl,logTrace); // 프록시를 스프링 빈으로 등록
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace){
//        return new OrderServiceV1Impl(orderRepository());
        OrderServiceV1Impl serviceImpl = new OrderServiceV1Impl(orderRepository(logTrace)); // target
        return new OrderServiceInterfaceProxy(serviceImpl,logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace){
//        return new OrderRepositoryV1Impl();
        OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl(); // target
        return new OrderRepositoryInterfaceProxy(repositoryImpl,logTrace);
    }
}
