package hello.advanced.trace.helloTrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class HelloTraceV1Test {

    @Test
    void begin_end(){
        HelloTraceV1 trace = new HelloTraceV1();
        // 로그 시작 - TraceStatus 상태 초기화
        TraceStatus status = trace.begin("hello");

        // 로그 종료
        trace.end(status);
    }

    @Test
    void begin_exception(){
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.exception(status,new IllegalAccessException());
    }
}