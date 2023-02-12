package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.ContextV1;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV1Test {

    @Test
    void strategyV0(){
        logic1();
        logic2();
    }

    private void logic1(){
        long startTime = System.currentTimeMillis();

        // 핵심 비즈니스 로직 실행
        log.info("비즈니스 로직1을 실행");
        // 핵심 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime = {}", resultTime);

    }

    private void logic2(){
        long startTime = System.currentTimeMillis();

        // 핵심 비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        // 핵심 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime = {}", resultTime);

    }

    @Test
    void strategyV1(){
        StrategyLogic1 strategyLogic1 = new StrategyLogic1();
        ContextV1 contextV1 = new ContextV1(strategyLogic1);
        contextV1.execute();

        StrategyLogic1 strategyLogic2 = new StrategyLogic1();
        ContextV1 contextV2 = new ContextV1(strategyLogic2);
        contextV2.execute();
    }

    @Test
    @DisplayName("익명 내부 클래스 전략 패턴1 - 인스턴스 변수")
    void strategyV2(){
        Strategy strategyLogic1 = new Strategy() {

            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        };
        ContextV1 contextV1 = new ContextV1(strategyLogic1);
        contextV1.execute();

        Strategy strategyLogic2 = new Strategy() {

            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        };
        ContextV1 contextV2 = new ContextV1(strategyLogic2);
        contextV2.execute();
    }

    @Test
    @DisplayName("익명 내부 클래스 전략 패턴2 - 변수 선언 없이")
    void strategyV3(){
        ContextV1 contextV1 = new ContextV1(
                new Strategy() {
                    @Override
                    public void call() {
                        log.info("비즈니스 로직1 실행");
                    }
                }

        );
        contextV1.execute();
        ContextV1 contextV2 = new ContextV1(
                new Strategy() {
                    @Override
                    public void call() {
                        log.info("비즈니스 로직2 실행");
                    }
                }
        );
        contextV2.execute();
    }

    /**
     * 주의!! 인터페이스 메서드가 한가지만 있어야 한다.
     */
    @Test
    @DisplayName("람다 사용")
    void strategyV4(){
        ContextV1 contextV1 = new ContextV1(() -> log.info("비즈니스로직1 실행"));
        contextV1.execute();
        ContextV1 contextV2 = new ContextV1(() -> log.info("비즈니스로직2 실행"));
        contextV2.execute();

    }
}
