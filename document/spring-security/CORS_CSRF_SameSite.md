## CORS(Cross Origin Resource Sharing, 교차 출처 리소스 공유)

- 웹에서는 보안을 위해 기본적으로 한 출처 A의 웹페이지에서 출처 B 웹 페이지의 데이터를 직접 불러오는 것을 제한하는데 이를 '**동일 출처 정책(Same-Origin Policy)**' 이라고 한다.
- 만약 다른 출처의 리소스를 안전하게 사용하고자 할 경우 CORS 가 등장하며 CORS는 특별한 HTTP 헤더를 통해 한 웹 페이지가 다른 출처의 리소스에 접근할 수 있도록 '허가'를 구하는 방
법이라 할 수 있다. 즉, **웹 애플리케이션이 다른 출처의 데이터를 사용하고자 할 때, 브라우저가 그 요청을 대신해서 해당 데이터를 사용해도 되는지 다른 출처에게 물어보는 것이라 할 수 있다.**
    - 브라우자가 두 어플리케이션 사이에서 데이터를 공유 할 수 있는지 파악해서 공유할 수 있으면 응답, 거부한다면 브라우저가 제한한다.
- 출처를 비교하는 로직은 서버에 구현된 스펙이 아닌 브라우저에 구현된 스펙 기준으로 처리되며 브라우저는 클라이언트의 요청 헤더와 서버의 응답헤더를 비교해서 최종 응답을 결정한다.
    - 브라우저가 판단하는 것
- 두 개의 출처를 비교하는 방법은 URL의 구성요소 중**Protocol ,Host, Port**이 세 가지가 동일한지 확인하면 되고 나머지는 틀려도 상관없다.

![Untitled](https://github.com/user-attachments/assets/02712fd3-14ba-4fc7-a545-858c4e738d88)
![Untitled (1)](https://github.com/user-attachments/assets/86e60591-05ba-42a6-a4f6-3dccc617f965)

→ [https://domain-a.com](https://domain-a.com/) 의 프론트 엔드 JavaScript 코드가 **XMLHttpRequest를 사용하여**
[https://domain-b.com/data.json을](https://domain-b.com/data.json%EC%9D%84) 요청하는 경우 **보안 상의 이유로, 브라우저는 스크립
트에서 시작한 교차 출처 HTTP 요청을 제한한다.**

→ XMLHttpRequest와 Fetch API는 **동일 출처 정책을 따르기 때문에 이 API를 사용하는 웹 애
플리케이션은 자신의 출처와 동일한 리소스만 불러올 수 있으며, 다른 출처의 리소스를 불러오
려면 그 출처에서 올바른 CORS 헤더를 포함한 응답을 반환해야 한다.**

### CORS 종류(**Simple Request, Preflight Request**)

- **Simple Request**
    - Simple Request 는 **예비 요청(Prefilght) 과정 없이 자동으로 CORS 가 작동하여 서버에 본 요청을 한 후, 서버가 응답의 헤더에Access-Control-Allow-Origin 과 같은 값
    을 전송하면 브라우저가 서로 비교 후 CORS 정책 위반 여부를 검사하는 방식이다**.
    - 제약 사항
        - GET, POST, HEAD 중의 한 가지 Method를 사용해야 한다.
            - DELETE, PUT, PATCH 사용 x
        - 헤더는 Accept, Accept-Language, Content-Language, Content-Type, DPR, Downlink, Save-Data, Viewport-Width Width 만 가능
            - Custom Header 는 허용 x
        - Content-type 은 application/x-www-form-urlencoded, multipart/form-data, text/plain 만 가능하다.
            - json도 포함되지 않음.

![Untitled (2)](https://github.com/user-attachments/assets/fff15d86-a771-4f32-9b06-fa9e6fb2b4cc)

- **Preflight Request (예비 요청), 주로 사용**
    - **브라우저는 요청을 한번에 보내지 않고, 예비 요청과 본 요청으로 나누어 서버에 전달**하는데 브라우저가 예비 요청을 보내는 것을 Preflight 라고 하며 이 예비 요청의 메소드에는
    OPTIONS 가 사용된다.
    - 예비 요청의 역할은 본 요청을 보내기 전에 **브라우저 스스로 안전한 요청인지 확인하는 것으로 요청 사양이 Simple Request 에 해당하지 않을 경우 브라우저가 Preflight
    Request 을 실행한다.**

![Untitled (3)](https://github.com/user-attachments/assets/61270a61-4ac6-402c-9177-ab2136b29410)


![Untitled (4)](https://github.com/user-attachments/assets/07f6e5cc-90fe-41e0-9369-074ba33024e5)

→ 브라우저가 보낸 요청을 보면Origin에 대한 정보 뿐만 아니라 예비 요청 이후에 전송할 본 요청에 대한 다른 정보들도 함께 포함되어 있는 것을 볼 수 있다.

→ 이 예비 요청에서 브라우저는 Access-Control-Request-Headers 를 사용하여 자신이 본 요청에서
Content-Type 헤더를 사용할 것을 알려주거나, Access-Control-Request-Method를 사용하여 GET
메소드를 사용할 것을 서버에게 미리 알려주고 있다.

![Untitled (5)](https://github.com/user-attachments/assets/b2cb2221-bf7c-49ed-a8f9-e1bc7657bbc1)

→ 서버가 보내준 응답 헤더에 포함된 Access-Control-Allow-Origin: [https://security.io](https://security.io/) 의 의미는 해
당 URL 외의 다른 출처로 요청할 경우에는 CORS 정책을 위반했다고 판단하고 오류 메시지를 내고 응답을 버리게 된다.

### 동일 출처 기준

![Untitled (6)](https://github.com/user-attachments/assets/48a3c35f-1ca7-42e1-88c4-53473980eb0f)

### CORS 해결 -서버에서 Access-Control-Allow-* 세팅

- **Access-Control-Allow-Origin**
    - **헤더에 작성된 출처만 브라우저가 리소스를 접근할 수 있도록 허용한다.**
    - *(와일드카드), https://security.io
- **Access-Control-Allow-Methods**
    - preflight request 에 대한 응답으로 실제 요청 중에 사용할 수 있는 메서드를 나타낸다.
    - 기본값은GET,POST,HEAD,OPTIONS, *
- **Access-Control-Allow-Headers**
    - preflight request 에 대한 응답으로 실제 요청 중에 사용할 수 있는 헤더 필드 이름을 나타낸다.
    - 기본값은 Origin,Accept,X-Requested-With,Content-Type, Access-Control-Request-Method,Access-Control-Request-Headers, Custom Header, *
- **Access-Control-Allow-Credentials**
    - 실제 요청에 쿠기나 인증 등의 사용자 자격 증명이 포함될 수 있음을 나타낸다. Client의credentials:include 옵션일 경우 true 는 필수
- **Access-Control-Max-Age**
    - preflight 요청 결과를 캐시 할 수 있는 시간을 나타내는 것으로 해당 시간 동안은 preflight 요청을 다시 하지 않게 된다

### cors() &  CorsFilter

- CORS 의 사전 요청(pre-flight request)에는 **쿠키 (JSESSIONID)가 포함되어 있지 않기 때문에  Spring Security 이전에 처리되어야 한다.**
- **사전 요청에 쿠키가 없고 Spring Security 가 가장 먼저 처리되면 요청은 사용자가 인증되지 않았다고 판단하고 거부할 수 있다.**
- CORS 가 먼저 처리 되도록 하기 위해서 CorsFilter 를 사용할 수 있으며 CorsFilter 에 CorsConfigurationSource 를 제공함으로써 Spring Security 와 통합 할 수 있다.
- Spring MVC 이전에 Security에서 먼저 CORS 관련 작업을 진행한다.

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

	/**
	* 커스텀하게 사용할 CorsConfigurationSource 를 설정한다.
	*	CorsConfigurationSource를 설정하지 않으면 Spring MVC의 CORS 구성을 사용한다.
	*/
	http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

	return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {

	CorsConfiguration configuration = new CorsConfiguration();
	configuration.addAllowedOrigin("https://example.com"));
	configuration.addAllowedMethod("GET","POST"));
	configuration.setAllowCredentials(true);

	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", configuration); // /**는 모든 경로
	return source;
}
```

---

## CSRF(Cross Site Request Forgery, 사이트 간 요청 위조)

시작에 앞서

- 웹 애플리케이션의 보안 취약점으로공격자가 사용자로 하여금 이미 인증된 다른 사이트에 대해 원치 않는 작업을 수행하게 만드는 기법을 말한다.
- **CSRF는 사용자의 브라우저가 자동으로 보낼 수 있는 인증 정보, 예를 들어 쿠키나 기본 인증 세션을 이용하여 사용자가 의도하지 않은 요청을 서버로 전송하게 만든다.**
- **사용자가 로그인한 상태에서 악의적인 웹사이트를 방문하거나 이메일 등을 통해 악의적인 링크를 클릭할 때 발생할 수 있다.**
- **중요!!!**
    - **별도에 설정 없이 Default로 설정 되어있다. 그래서 요청을 할 때 Header나 파라미터에 csrf 값을 명시적으로 던져야 한다.**

### CSRF 진행 순서

![Untitled (7)](https://github.com/user-attachments/assets/a8a4bdb2-afed-4844-af20-2a5301df1d8b)

1. 사용자가 이용하고자 하는 웹 사이트에 접속하여 세션 쿠키를 발급 받고 인증을 받는다.
2. 웹 사이트는 쿠키를 발급했기 때문에 쿠키를 가지고 오면 인증을 거친 사용자라고 인식한다.
3. 이 때, 공격자가 악의적인 웹 사이트의 링크를 사용자에게 전달한다.
4. 사용자는 링크에 접속하게 되면 태그와 같이 버튼 같은 것을 클릭해 사용자가 공격용 페이지를 열면 브라우저는 이미지 파일을 받아오기 위해 공격용 URL을 열게 된다.
    1. 웹 사이트는 브라우저가 전달한 쿠키 값 같은 것을 사용자가 접근한 것으로 파악하게 된다.
5. 즉, 사용자의 승인 없이 배송지가 등록됨으로써 공격이 완료된다.

### CSRF 기능 활성화

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

	http.csrf(Customizer.withDefaults());

	return http.build();
}
```

- `http.csrf(Customizer.withDefaults())`
    - csrf 의 기능을 활성화 한다. 별도 설정하지 않아도 활성화 상태로 초기화 된다.
- **CSRF토큰은 서버에 의해 생성되어 클라이언트의 세션에 저장되고,** **폼을 통해 서버로 전송되는 모든 변경 요청에 포함되어야 하며, 서버는 이 토큰을 검증하여 요청의 유효성을 확인한다.**
- 기본 설정은 'GET', 'HEAD', 'TRACE', 'OPTIONS’ 와 같은 안전한 메서드를 무시한다.
    - **'POST', 'PUT', 'DELETE’ 와 같은 변경 요청 메서드에 대해서만 CSRF 토큰 검사를 수행한다.**
- **중요!**
    - 실제 CSRF 토큰이 브라우저에 의해 자동으로 포함되지 않는 요청 부분에 위치해야 한다는 것이다.
        - CSRF 토큰을 브라우저에서 자동으로 전달되는 것에 포함되지 않아야 한다.
        - HTTP 매개변수나 헤더에 실제 CSRF 토큰을 요구하는 것이 CSRF 공격을 방지하는데 효과적이라 할 수 있다.
    - **반면에 쿠키에 CSRF 토큰을 요구하는 것은 브라우저가 쿠키를 자동으로 요청에 포함 시키기 때문에 효과적이지 않다고 볼 수 있다. ⇒ 지양해야 한다.**

### CSRF 기능 비활성화

- **csrf 의 기능 전체 비활성화**

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
	http.csrf(csrf -> csrf.disabled()
	return http.build();
}
```

- **csrf 보호가 필요하지 않은 특정 엔드 포인트만 비활성화**

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
	http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/*"));
  return http.build();
}
```

## CSRF 토큰 유지 및 검증

### CSRF  토큰 유지CsrfTokenRepository(인터페이스)

- CSRF토큰은 `CsrfTokenRepository`를 사용하여 영속화 하며 `HttpSessionCsrfTokenRepository`와 `CookieCsrfTokenRepository`를 지원한다.
    - 두 군데 중 원하는 위치에 토큰을 저장하도록 설정을 통해 지정할 수 있다.

### 1. 세션에 토큰 저장 -HttpSessionCsrfTokenRepository

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

  HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
  http.csrf(csrf -> csrf.csrfTokenRepository(repository));

  return http.build();
}
```

- 기본적으로 토큰을 세션에 저장하기 위해 `HttpSessionCsrfTokenRepository`를 사용한다.
    - default 값임. → 초기화가 `HttpSessionCsrfTokenRepository` 저장되도록 된다.
- `HttpSessionCsrfTokenRepository`는 **기본적으로 HTTP 요청 헤더인 `X-CSRF-TOKEN` 또는 요청 매개변수인 `_csrf`에서 토큰을 읽는다.**
    - 만약**`X-CSRF-TOKEN`나`_csrf` 로 보내지 않으면 서버는 읽을 수 없다.**

  ![Untitled (8)](https://github.com/user-attachments/assets/b480d3af-da7c-4ff8-a5e9-db4209310d69)


### 2. 쿠키에 토큰 저장 -CookieCsrfTokenRepository → 지양하는 게 좋다.

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

  CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();

	// 둘 중 하나만 선택해야 한다.
	http.csrf(csrf -> csrf.csrfTokenRepository(repository)); // 1)
  http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // 2)

  return http.build();
}

```

- JavaScript 기반 애플리케이션을 지원하기 위해 CsrfToken 을 쿠키에 유지할 수 있으며 구현체로 `CookieCsrfTokenRepository`를 사용할 수 있다.
- `CookieCsrfTokenRepository`는 기본적으로 `XSRF-TOKEN` 명을 가진 쿠키에 작성하고 HTTP 요청 헤더인 `X-XSRF-TOKEN` 또는 요청 매개변수인 `_csrf`에서 읽는다.
    - 헤더, 파라미터, 쿠키 이름 변경이 가능하다.

  ![Untitled (9)](https://github.com/user-attachments/assets/57ab30cd-d7af-4aac-917f-8113432811cf)

- JavaScript 에서 쿠키를 읽을 수 있도록 HttpOnly를 명시적으로 false로 설정할 수 있다.
- JavaScript로 직접 쿠키를 읽을 필요가 없는 경우 보안을 개선하기 위해 HttpOnly 를 생략하는 것이 좋다.

### CSRF  토큰 처리CsrfTokenRequestHandler

- CsrfToken 은 CsrfTokenRequestHandler 를 사용하여 토큰을 생성 및 응답하고 HTTP 헤더 또는 요청 매개변수로부터 토큰의 유효성을 검증하도록 한다.
- `XorCsrfTokenRequestAttributeHandler` 와 `CsrfTokenRequestAttributeHandler` 를 제공하며 사용자 정의 핸들러를 구현할 수 있다.

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

  XorCsrfTokenRequestAttributeHandler csrfTokenHandler = new XorCsrfTokenRequestAttributeHandler();
  http.csrf(csrf -> csrf.csrfTokenRequestHandler(csrfTokenHandler));

  return http.build();
}

```

- `_csrf`  및 `CsrfToken.class.getName()` 명으로 HttpServletRequest 속성에 CsrfToken 을 저장하며 HttpServletRequest 으로부터 CsrfToken 을 꺼내어 참조할 수 있다.
- 토큰 값을 요청 헤더 (기본적으로 `X-CSRF-TOKEN` (세션) 또는 `X-XSRF-TOKEN` (쿠키)중 하나) 또는 요청 매개변수 `_csrf` 중 하나로부터 토큰의 유효성 비교 및 검증을 해결한다.
- 클라이언트의 매 요청마다 CSRF 토큰 값(UUID) 에 난수를 인코딩하여 변경한 CsrfToken 이 반환 되도록 보장한다. **세션에 저장된 원본 토큰 값은 그대로 유지한다.**
    - 헤더 값 또는 요청 매개변수로 전달된 인코딩 된 토큰은 원본 토큰을 얻기 위해 디코딩되며, 그런 다음 세션 혹은 쿠키에 저장된 영구적인 CsrfToken과 비교된다.

### CSRF 토큰 지연 로딩

- 기본적으로 Spring Security는 CsrfToken을 필요할 때까지 로딩을 지연시키는 전략을 사용한다. 그러므로 CsrfToken 은 HttpSession 에 저장되어 있기 때문에 매 요청마다 세션으로부터 CsrfToken 을 로드할 필요가 없어져 성능을 향상시킬 수 있다.
- CsrfToken 은 POST 와 같은 안전하지 않은 HTTP 메서드를 사용하여 요청이 발생할 때와 CSRF 토큰을 응답에 렌더링하는 모든 요청에서 필요하기 때문에 그 외 요청에는 지연로딩 하는 것이 권장된다.

```java
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

  XorCsrfTokenRequestAttributeHandler handler = new XorCsrfTokenRequestAttributeHandler();

  handler.setCsrfRequestAttributeName(null); //지연된 토큰을 사용하지 않고 CsrfToken 을 모든 요청마다 로드한다
  http.csrf(csrf -> csrf
          .csrfTokenRequestHandler(handler));

  return http.build();
}
```

## CSRF 통합

시작에 앞서

- **CSRF 공격을 방지하기 위한 토큰 패턴을 사용하려면 실제 CSRF 토큰을 HTTP 요청에 포함해야 한다.**
- 그래서 브라우저에 의해 HTTP 요청에 자동으로 포함되지 않는 요청 부분(폼 매개변수, HTTP 헤더 또는 기타 부분) 중 하나에 포함되어야 한다.
- 클라이언트 어플리케이션이 CSRF로 보호된 백엔드 애플리케이션과 통합하는 여러 가지 방법이 있다.

### 1. HTML Forms - JPS, Thymeleaf 등

- HTML 폼을 서버에 제출하려면 CSRF 토큰을 hidden 값으로 Form 에 포함해야 한다.

    ```java
    <form action="/memberJoin" method="post">
    	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>

    // 결과
    <input type="hidden" name="_csrf" value="4bfd1575-3ad1-4d21-96c7-4ef2d9f86721"/>
    ```

- 폼에 실제 CSRF 토큰을 자동으로 포함하는 뷰는 다음과 같다.
    - Thymeleaf
    - Spring 의 폼 태그 라이브러리

    ```java
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    ```


### 2. JavaScript Applications

- **Single Page Application**
1. `CookieCsrfTokenRepository.withHttpOnlyFalse()` 를 사용해서 클라이언트가 서버가 발행한 쿠키로부터 CSRF 토큰을 읽을 수 있도록 한다.
2. 사용자 정의 `CsrfTokenRequestHandler` 을 만들어 클라이언트가 요청 헤더나 요청 파라미터로 CSRF 토큰을 제출할 경우 이를 검증하도록 구현한다.
3. 클라이언트의 요청에 대해 CSRF 토큰을 쿠키에 렌더링해서 응답할 수 있도록 필터를 구현한다.

![Untitled (10)](https://github.com/user-attachments/assets/bf67e9d1-6235-4342-a32b-655d313935d2)

### 3. Multi Page Application

- **JavaScript 가 각 페이지에서 로드되는 멀티 페이지 애플리케이션의 경우 CSRF 토큰을 쿠키에 노출시키는 대신HTML 메타 태그 내에 CSRF 토큰을 포함시킬 수 있다.**

- **HTML 메타 태그에 CSRF 토큰 포함**

```java
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
</html>
```

- **AJAX 요청에서 CSRF 토큰 포함**

```java
function login() {
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
const csrfToken = $('meta[name="_csrf"]').attr('content')
fetch('/api/login', {
method: 'POST',
headers: {[csrfHeader]: csrfToken}
})
```

### CSRF Filter

![Untitled (11)](https://github.com/user-attachments/assets/4be4404b-2fe6-4f72-a005-09c7d632bc48)

---

## SameSite

시작에 앞서

- SameSite 는 최신 방식의 CSRF 공격 방어 방법 중 하나로서 서버가 쿠키를 설정할 때 SameSite 속성을 지정하여 cross site 간 **쿠키 전송에 대한 제어를 핸들링 할 수 있다.**
- **Spring Security는 세션 쿠키의 생성을 직접 제어하지 않기 때문에 SameSite 속성에 대한 지원을 제공하지 않지만 Spring Session 은 SameSite 속성을 지원한다.**

### Samesite 속성

- **Strict**
    - 동일 사이트에서 오는 모든 요청에 쿠키가 포함되고 cross site 간 HTTP 요청에 쿠키가 포함되지 않는다.

      ![Untitled (12)](https://github.com/user-attachments/assets/6a65c8e6-4cb3-4620-9ee6-f157dbad208b)

- **Lax**
    - 동일 사이트에서 오거나 Top Level Navigation 에서 오는 **요청 및 메소드가 읽기 전용인 경우 쿠키가 전송되고 그렇지 않으면 HTTP 요청에 쿠키가 포함되지 않는다.**
    - 사용자가 링크(<a>)를 클릭하거나window.location.replace, 302리다이렉트 등의 이동이 포함된다. 그러나 <iframe>이나<img>를 문서에 삽입, AJAX 통신 등은 **쿠키가 전송되지 않는다.**

      ![Untitled (13)](https://github.com/user-attachments/assets/1384d9e1-8311-4b16-af9a-5c0556c6baa0)

- **None**
    - 동일 사이트 cross site 및 **모든 요청의 경우에도 쿠키가 전송된다.**
    - **이 모드에서는 HTTS에 의한 Secure 쿠키로 설정되어야 한다.**

      ![Untitled (14)](https://github.com/user-attachments/assets/8514b0ad-4371-4c12-ba67-63ef5af0fc6f)


### SameSite 예시

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/84f25bf1-ed0e-41f3-a436-bbac2f460292/Untitled.png)

- 대부분의 현대 브라우저는 SameSite 속성을 지원하지만, 여전히 사용 중인 오래된 브라우저는 지원하지 않을 수 있다.
- SameSite를 CSRF 공격에 대한 유일한 방어 수단으로서가 아니라 심층적으로 강화된 방어의 일환으로 사용하는 것을 권장하고 있다.

### Spring Session 으로 SameSite 적용하기

- Spring security에서는 SameSite를 지원하고 있지 않기에 Spring session 라이브러리를 사용하여 SameSite를 적용한다.

```java
implementation group: 'org.springframework.session', name: 'spring-session-core', version: '3.2.1'
```

```java
@Configuration
@EnableSpringHttpSession
public class HttpSessionConfig {

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setUseSecureCookie(true);
		serializer.setUseHttpOnlyCookie(true);
		serializer.setSameSite("Lax");
		return serializer;
	}

	@Bean
	public SessionRepository<MapSession> sessionRepository() {
		return new MapSessionRepository(new ConcurrentHashMap<>());
	}
}
```