package hello.proxy.pureproxy.code;

public class ProxyPatternClient {

    // 서버 객체지만 프록시가 임무를 수행하던, 실 서버가 수행하던 클라이언트는 모른다.
    private Subject subject;

    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    // 클라이언트가 서버에게 임무를 시킨다.
    public void execute(){
        subject.operation();
    }
}
