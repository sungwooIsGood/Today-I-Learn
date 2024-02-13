package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTemplate {

    /**
     * 핵심 비즈니스 로직만 변하기 때문에 변하지 않는 부분을 템플릿화 -> execute()
     * 바뀌는 핵심 비즈니스 로직을 call메서드에 몰아둔다. -> call()
     * 변하지 않는 코드는 부모 클래스에 두고,
     * 변하는 코드는 자식클래스에서 오버라이딩하여 처리한다.
     *
     * 즉, 변하지 않는 로직은 부모클래스에 구성, 변하는 로직은 자식 클래스에 구성.
     */
    public void execute(){
        long startTime = System.currentTimeMillis();

        // 핵심 비즈니스 로직 실행
        call();
        // 핵심 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime = {}", resultTime);
    }

    protected abstract void call();
}
