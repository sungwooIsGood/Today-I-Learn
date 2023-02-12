package hello.proxy.pureproxy;

import hello.proxy.pureproxy.code.Subject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheProxy implements Subject {

    private Subject target;
    private String cacheValue; // 반환 값을 cache화 시킨 것 반환 값을 넣어주면 된다.

    // 의존관계 주입
    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");
        if(cacheValue == null){
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}
