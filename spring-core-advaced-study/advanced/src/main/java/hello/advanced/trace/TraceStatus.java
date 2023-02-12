package hello.advanced.trace;

import lombok.Getter;

/**
 * 로그 상태 정보를 알려준다.
 * 로그의 시작과 로그의 종료를 사용하는 클래스
 */
@Getter
public class TraceStatus {

    private TraceId traceId;
    private Long startTimeMs;
    private String message;

    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
