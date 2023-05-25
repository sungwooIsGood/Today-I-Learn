package hello.aop.exam.aop.internalCall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InterService {

    public void internal(){
        log.info("call internal");
    }
}
