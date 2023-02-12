package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV2Test {

    @Test
    @DisplayName("파라미터를 전달 받는 방식 - 전략 객체 전달")
    void StrategyV1(){
        ContextV2 contextV2 = new ContextV2();
        contextV2.execute(new StrategyLogic1());
        contextV2.execute(new StrategyLogic2());
    }

    @Test
    @DisplayName("파라미터를 전달 받는 방식 - 익명 내부 클래스")
    void StrategyV2(){
        ContextV2 contextV2 = new ContextV2();
        contextV2.execute(new Strategy(){

            @Override
            public void call() {
                log.info("비즈니스 로직 1 실행");
            }
        });
        contextV2.execute(new Strategy(){

            @Override
            public void call() {
                log.info("비즈니스 로직 2 실행");
            }
        });
    }

    @Test
    @DisplayName("파라미터를 전달 받는 방식 - 람다")
    void StrategyV3(){
        ContextV2 contextV2 = new ContextV2();
        contextV2.execute(() -> log.info("비즈니스 로직 1실행"));
        contextV2.execute(() -> log.info("비즈니스 로직 2실행"));
    }
}
