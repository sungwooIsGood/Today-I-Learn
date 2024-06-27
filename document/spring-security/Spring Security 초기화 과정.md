회사에서 6.x 버전을 기준으로 작업을 할 일이 있어 6.x버전으로 공부를 사직하고자 한다.

들어가기에 앞서 Spring Security의 특징을 간략하게 알아보자.

## 자동 설정의 의한 기본 보안 작동

시큐리티는 서버가 동작하면 스프링 시큐리티의 초기화 작업 및 보안 설정이 이루어진다. **별도의 설정이나 코드를작성하지 않아도 기본적인 웹 보안 기능이 현재 시스템에 연동되어 작동한다.**

1. 기본적으로 **모든 요청에 대하여** **인증여부를 검증하고 인증이 승인되어야 자원에 접근이 가능하다.**
2. 인증 방식은 **폼 로그인 방식**과 **httpBasic 로그인 방식**을 제공한다.
3. 인증을 시도할 수 있는 로그인 페이지가 자동적으로 생성되어 렌더링 된다.
4. 인증 승인이 이루어질 수 있도록 한 개의 계정이 기본적으로 제공된다. 이 기본적으로 제공되는 계정가지고 폼 로그인 혹 httpBasic 방식으로 로그인하게 된다.
    - **SecurityProperties 설정 클래스에 의해 하나의 계정이 생성된다.**
        - username : user
        - password : 빌드 시 랜덤 문자열을 제공해준다.
5. 기본 설정 클래스는 **SpringBootWebSecurityConfiguration이다.**

---

### SecurityBuilder / SecurityConfigurer

Spring Security는 초기화 시 인증 & 인가와 관련된 여러가지 작업들을 진행한다. 인증 & 인가를 종합적으로 처리하는 대표적인 클래스가 두 개있다.

**바로 SecurityBuilder / SecurityConfigurer이다.**

- **SecurityBuilder**
    - 빌더 클래스로서 **웹 보안을 구성하는 빈 객체와 설정 클래스들을 생성하는 역할**을 하며 **대표적으로 WebSecurity, HttpSecurity**가 있다.
    - **SecurityBuilder는 SecurityConfigurer를 참조**하고 있으며 **인증 및 인가 초기화 작업은 SecurityConfigurer에 의해 진행된다.**
- **SecurityConfigurer**
    - **Http 요청과 관련된 보안 처리를 담당하는 필터들을 생성하고 여러 초기화 설정**에 관여한다.

![Untitled (1)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/c759a07c-9c1b-45cf-83e4-d5afabf97f08)


큰 줄기는 대략 이렇다.

![Untitled](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/7a22ee9d-28a8-4818-8f6f-b7a9d5b61b62)


1. 프로젝트가 빌드 되면서 자동 설정에 의해 Security Builder 클래스를 생성한다.
2. Security Builder는 Security Configurer를 생성한다.
    1. 내부적으로 `init(SecurityBuilder builder), configure(SecurityBuilder builder)`메서드를 통해 초기화 작업을 진행한다.
3. 초기화가 이루어 진 후 Filter가 생성 되고 인증&인가 작업이 이루어진다.

구체적으로 한번 보자.

![Untitled (2)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/505251b1-3434-43cb-bc31-58148e4feae5)

1. **자동 설정 클래스에 의해 HttpSecurityConfiguration 클래스가 생성된다.**
2. **SecurityBuilder를 통해 HttpSecurity 빈 객체가 생성된다.**
3. **HttpSecurity빈이 SecurityConfigurer 객체의 초기화 작업을 진행한다.**
4. **초기화 작업이 진행되며 각 Configurer들에서 각각의 Filter들이 생성된다.**
5. **Filter들에 의해 HttpSecurity는 여러개의 Filter들을 가진 객체가 되어있는 것이다.**
6. **HttpSecurity를 가지고 build를 하게 되며 초기화 작업이 완성된다.**

---

## WebSecurity / HttpSecurity

### HttpSecurity

- **HttpSecurityConfiguration 에서 HttpSecurity 를 생성하고 초기화**를 진행한다.
- **HttpSecurity는 보안에 필요한 각 설정 클래스와 필터들을 생성하고 최종적으로 SecurityFilterChain 빈 생성한다.**

![Untitled](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/fb3a0d23-fb0e-4049-9722-851b2c60a6b5)

1. HttpSecurity 객체는 내부적으로 각 Configurer들을 생성하고 설정 클래스들을 통해 초기화 작업을 시작한다.
2. `build()` 메서드가 실행되면 `init()` 과 `configurer()` 메서드를 통해 필터들이 생성이 되며 SecurityFilterChain이라는 빈이 생성이 되면서 여러개의 Filter 목록들이 저장된다.
3. SecurityFilterChain 는 각 인증/인가 처리 할 필터들을 가지게 된다.

### SecurityFilterChain 인터페이스

- 여러 개의 Filter들을 관리한다.

![Untitled (1)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/2a532259-85c7-4d2a-b33a-b5506dbc99ad)

**`boolean matches(HttpServletRequest request)`**

- 이 메서드는 클라이언트의 요청이 현재 **SecurityFilterChain에 의해 처리되어야 하는지 여부를 결정한다.**
    - SecurityFilterChain은 하나가 아닌 여러 개가 생성될 수 있다. 그렇기 때문에 요청에 적합한 SecurityFilterChain을 **`matches()`**를 통해 생성한다.
- true를 반환하면 현재 요청이 이 필터 체인에 의해 처리되어야 함을 의미하며, false를 반환하면
  다른 필터 체인이나 처리 로직에 의해 처리되어야 함을 의미한다.
- 이를 통해 특정 요청에 대해 적절한 보안 필터링 로직이 적용될 수 있도록 한다.

**`List<Filter> getFilters()`**

- 이 메서드는 현재 SecurityFilterChain 에 포함된 Filter 객체의 리스트를 반환한다.
- **이 메서드를 통해 어떤 필터들이 현재 필터 체인에 포함되어 있는지 확인할 수 있으며**, **각 필터는
  요청 처리 과정에서 특정 작업(예: 인증, 권한 부여, 로깅 등)을 수행한다.**

![Untitled (2)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/66951295-ec9d-4990-aae5-22cce13d4e76)

### WebSecurity

- WebSecurityConfiguration에서 WebSecurity를 생성하고 초기화를 진행한다.
- **WebSecurity는 HttpSecurity 에서 생성한 SecurityFilterChain 빈을 SecurityBuilder 에 저장한다.**
- **WebSecurity가 build() 를 실행하면 SecurityBuilder에서 SecurityFilterChain을 꺼내어 FilterChainProxy 생성자에게 전달한다.**

![Untitled (3)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/3128a471-ed2a-4f92-9b58-0d2a7541105d)

- WebSecurity는 HttpSecurity 보다 상위의 개념이라고 볼 수 있다. HttpSecurity가 만든 SecurityFilterChain을 SecurityBuilder에 저장하기 때문이다. 또, 저장한 것을 Proxy에 전달하기 때문이다. **SecurityFilterChain는 이 자체로 목록을 가지고만 있을 뿐, 클라이언트의 요청을 처리하지 않는다. 요청 처리는 FilgerChainProxy에서 처리한다.**