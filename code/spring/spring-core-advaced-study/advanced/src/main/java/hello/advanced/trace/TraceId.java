package hello.advanced.trace;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TraceId {
    
    private String id;
    private int level;

    public TraceId() {
        this.id = createdId();
        this.level = 0;
    }

    private TraceId(String id, int level){
        this.id = id;
        this.level = level;
    }
    private String createdId() {
        // 앞 8자리만  UUID 트랜잭션 아이디 로그사용
        return UUID.randomUUID().toString().substring(0,8); // 로그 트랜잭션 ID
    }

    /**
     * TraceId 깊이 증가
     */
    public TraceId createNextId(){
        return new TraceId(id, level+1);
    }

    /**
     * TraceId 깊이 감소
     */
    public TraceId createPreviousId(){
        return new TraceId(id, level-1);
    }

    /**
     * 첫번째 로그 확인
     */
    public boolean isFiresLevel(){
        return level == 0;
    }

}
