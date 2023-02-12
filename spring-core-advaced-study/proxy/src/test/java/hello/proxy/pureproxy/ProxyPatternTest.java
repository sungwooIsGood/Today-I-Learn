package hello.proxy.pureproxy;

import hello.proxy.pureproxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.code.RealSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {

    @DisplayName(value = "프록시 사용하지 않은 것")
    @Test
    void noProxyTest(){
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
        // 만약 똑같은 데이터를 계속 호출해야한다면?
        // data라는 것을 어느 저장소에 넣고 다음에 또 쓴다면 성능은 좋아진다.
    }

    // 위에 코드를 손대지 않고 성능을 올려보자
    // DB 접근이 많을 때 사용하면 좋을 듯
    @Test
    void cacheProxyTest(){
        RealSubject realSubject = new RealSubject(); // 서버 객체
        CacheProxy cacheProxy = new CacheProxy(realSubject); // 프록시
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy); // 클라이언트 객체
        client.execute();
        client.execute();
        client.execute();

    }
}
