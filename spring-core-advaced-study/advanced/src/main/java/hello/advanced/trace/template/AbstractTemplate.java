package hello.advanced.trace.template;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logTrace.LogTrace;

public abstract class AbstractTemplate<T> {

    private final LogTrace trace;

    public AbstractTemplate(LogTrace trace) {
        this.trace = trace;
    }


    /**
     * 제네릭을 사용한 이유
     * Controller 반환타입 String
     * Service, Repository 반환타입 void
     */
    public T execute(String message) throws IllegalAccessException {
        TraceStatus status = null;
        try{
            status = trace.begin(message);

            // 비즈니스 로직 호출
            T result = call();

            trace.end(status);
            return result;
        } catch (Exception e){
            trace.exception(status,e);
            throw e;
        }
    }

    protected abstract T call() throws IllegalAccessException;
}
