package hello.advanced.trace.helloTrace;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

class HelloTraceV2Test {

    @Test
    void begin_end(){
        HelloTraceV2 trace = new HelloTraceV2();

        // 로그 시작 - TraceStatus 상태 초기화

        TraceStatus status1 = trace.begin("hello1");
        TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");

        // 로그 종료
        trace.end(status2);
        trace.end(status1);
    }

    @Test
    void begin_exception(){
        HelloTraceV2 trace = new HelloTraceV2();

        TraceStatus status1 = trace.begin("hello");
        TraceStatus status2 = trace.beginSync(status1.getTraceId(), "hello2");

        trace.exception(status2,new IllegalAccessException());
        trace.exception(status1,new IllegalAccessException());
    }
}