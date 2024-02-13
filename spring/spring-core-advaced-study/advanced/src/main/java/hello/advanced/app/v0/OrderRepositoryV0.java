package hello.advanced.app.v0;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV0 {

    public void save(String itemId) throws IllegalAccessException {
        // 저장로직
        if(itemId.equals("ex")){
            throw new IllegalAccessException("예외발생!!");
        }
        sleep(1000);

    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
