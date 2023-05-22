package hello.aop.exam;

import hello.aop.exam.annotation.Retry;
import hello.aop.exam.annotation.Trace;
import org.springframework.stereotype.Repository;

@Repository
public class ExamRepository {

    private static int seq = 0;

    /**
     * 5번에 1번 실패하는 요청
     */
    @Trace
    // annotation의 값을 4로 바꿔준다는 의미
    @Retry(value = 4) // 실무에서 retry를 사용할 때, 항상 몇번을 호출할지가 필요하다.
    public String save(String itemId){
        seq++;
        if(seq% 5 == 0){
            throw new IllegalStateException("예외 발생");
        }
        return "ok";
    }
}
