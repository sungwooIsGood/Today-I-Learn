**Spring Security는 Servlet에 도착하기 전 앞단에서 FilterChain에서 요청에 대한 인증/인가 작업을 다 거친다. Spring Security는 Filter 기반으로 요청을 처리하고 수행한다. 모든 것은 Filter 기반으로 움직인다.**

이제부터 하나씩 알아보자.

### **Filter**

- 서블릿 필터는 웹애플리케이션에서 **클라이언트의 요청과 서버의 응답을 가공하거나 검사하는데 사용 되는 구성 요소이다.**
- 서블릿 필터는 클라이언트의 요청이 서블릿에 도달하기 전이나 서블릿이 응답을 클라이언트에게 보내기 전에특정 작업을 수행할 수 있다.
- 서블릿 필터는 서블릿 컨테이너(WAS)에서 생성되고 실행되고 종료된다.
<img width="1131" alt="스크린샷 2024-06-29 오후 1 49 14" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/eb61772b-ad89-4bda-a012-369732ac6a5f">

이제 하나씩 필터 체인에서 하나씩 필터들의 역할을 알아보자.

### **DelegatingFilterProxy**

- Filter를 구현한 Filter 클래스이다.
- DelegatingFilterProxy는 스프링에서 사용 되는 특별한 서블릿 필터로, 서블릿 컨테이너와 스프링 애플리케이션 컨텍스트간의 연결고리 역할을 하는 필터이다.
    - **중요한 점! → 일반적인 Filter들은 Spring container의 기능을 사용할 수 없다.(DI, AOP 등) 그렇기 때문에 DelegatingFilterProxy 를 통해 Spring Container 특별한 기능들을 가지고 요청에 대한 처리를 수행하게 끔 만들어진 것이다.**
- DelegatingFilterProxy는 **서블릿 필터의 기능을 수행하는 동시에 스프링의 의존성 주입 및 빈 관리 기능과 연동 되도록 설계된 필터이다.**
- DelegatingFilterProxy는 **springSecurityFilterChain**이름으로 생성된 빈을 ApplicationContext에서찾아 요청을 위임한다.
    - 실제 보안 처리를 수행하지 않는다.
    - 즉, **DelegatingFilterProxy는 하는 일이 없다. springSecurityFilterChain이라는 이름을 가진 Bean에게 요청을 위임하는 역할을 수행할 뿐이다.**
    <img width="1204" alt="스크린샷 2024-06-29 오후 2 01 40" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/c1e6939a-1bdc-41e4-a899-49f954db4b7d">

### **FilterChainProxy**

- springSecurityFilterChain의 이름으로 생성 되는 Filter Bean으로서 **DelegatingFilterProxy으로부터 요청을 위임 받고 보안 처리 역할을 한다.**
    - 위 그림을 보면 Spring Bean에서 Filter를 사용하는데 이 사용되는 Filter가 **FilterChainProxy이다.**
    - **DelegatingFilterProxy가 위임한 역할을 수행한다고 보면 좋다.**
- 내부적으로 하나 이상의 SecurityFilterChain객체들을 가지고 있으며 **요청URL 정보를 기준**으로 적절한SecurityFilterChain을 선택하여 필터들을 호출한다.
- HttpSecurity를 통해 API추가 시 관련 필터들이 추가된다.
- 사용자의 요청을 필터 순서대로 호출 함으로 보안 기능을 동작 시키고 필요시 직접 필터를 생성해서 기존의 필터 전/후로 추가 가능하다.
    - 클라이언트 요청 → DelegatingFilterProxy → ApplicationContext(Spring Bean) → FilterChainProxy → 요청에 필요한 Filter들을 호출 → 예외 케이스 / 권한이 있으면 서블릿으로 요청을 전달한다.
      <img width="1199" alt="스크린샷 2024-06-29 오후 2 15 39" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/dcb09190-4b02-4cc8-a19a-049eb88be358">