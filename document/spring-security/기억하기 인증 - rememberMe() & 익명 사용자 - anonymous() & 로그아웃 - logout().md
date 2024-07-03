## RememberMe 인증

- 사용자가 웹 사이트나 애플리케이션에 **로그인할 때 자동으로 인증 정보를 기억하는 기능이다.**
- UsernamePasswordAuthenticationFilter 와 함께 사용되며, AbstractAuthenticationProcessingFilter 슈퍼 클래스에서 훅을 통해 구현된다.
- 인증 성공 시 RememberMeServices.loginSuccess() 를 통해 RememberMe 토큰을 생성하고 쿠키로 전달한다.
- 인증 실패 시 RememberMeServices.loginFail() 를 통해 쿠키를 지운다. 또한, LogoutFilter 와 연계해서 로그아웃 시 쿠키를 지운다.

### 토큰 생성

- 기본적으로 암호화된 토큰으로 생성 되어지며 브라우저에 쿠키를 보내고, 향후 세션에서 이 쿠키를 감지하여 자동 로그인이 이루어지는 방식이다.
    - **base64(username + ":" + expirationTime + ":" + algorithmName + ":" algorithmHex(username + ":" + expirationTime + ":" password + ":" + key))**
        - username: UserDetailsService 로 식별 가능한 사용자 이름
        - password: 검색된 UserDetails 에 일치하는 비밀번호
        - expirationTime: remember-me 토큰이 만료되는 날짜와 시간, 밀리초로 표
        - key: remember-me 토큰의 수정을 방지하기 위한 개인 키
        - algorithmName: remember-me 토큰 서명을 생성하고 검증하는 데 사용되는 알고리즘(기본적으로 SHA-256 알고리즘을 사용)

### RememberMeServices 구현체

- TokenBasedRememberMeServices - 쿠키 기반 토큰의 보안을 위해 해싱을 사용한다.
- PersistentTokenBasedRememberMeServices -생성된 토큰을 저장하기 위해 데이터베이스나 다른 영구 저장 매체를 사용한다.
- **두 구현 모두 사용자의 정보를 검색하기 위한 UserDetailsService 가 필요하다.**

### rememberMe() API

- RememberMeConfigurer 설정 클래스를 통해 여러 API 들을 설정할 수 있다.
- 내부적으로 RememberMeAuthenticationFilter 가 생성되어 자동 인증 처리를 담당하게 된다.

### 실습

```java
http.rememberMe(httpSecurityRememberMeConfigurer -> 
	httpSecurityRememberMeConfigurer
		.alwaysRemember(true)
		.tokenValiditySeconds(3600)
		.userDetailsService(userDetailService)
		.rememberMeParameter("remember") 
		.rememberMeCookieName("remember") 
		.key("security")
	);
```

- `alwaysRemember()` : "기억하기(remember-me)" 매개변수가 설정되지 않았을 때에도 쿠키가 항상 생성되어야 하는지에 대한 여부를 나타낸다.
- `tokenValiditySeconds()` : 토큰이 유효한 시간(초 단위)을 지정할 수 있다.
- `userDetailsService()` : UserDetails 를 조회하기 위해 사용되는 UserDetailsService를 지정한다.
- `rememberMeParameter()` : 로그인 시 사용자를 기억하기 위해 사용되는 HTTP 매개변수이며 기본값은 'remember-me' 이다.
- `rememberMeCookieName()` : 기억하기(remember-me) 인증을 위한 토큰을 저장하는 쿠키 이름이며 기본값은 'remember-me' 이다.
- `key()` : 기억하기(remember-me) 인증을 위해 생성된 토큰을 식별하는 키를 설정한다.

## 기억하기 인증 필터 -
RememberMeAuthenticationFilter

- SecurityContextHolder에 Authentication이 포함되지 않은 경우 실행되는 필터이다.
    - **우린 폼인증이나 httpbasic을 통해 인증을 거친다. 이 과정에서 Authentication 객체를 SecurityContextHolder에 저장하는데, 이는 Authentication 객체가 있다는 것은 인증을 받은 경우이다. 하지만 RememberMeAuthenticationFilter를 거친다면 Authentication 객체가 없어도 유효한 토큰이 있다면 인증 처리가 된다.**
- **세션이 만료 되었거나 어플리케이션 종료로 인해 인증 상태가 소멸된 경우 토큰 기반 인증을 사용해 유효성을 검사하고 토큰이 검증되면 자동 로그인 처리를 수행한다.**
    - 내 생각은 세션 시간은 짧게 하면서 쿠키 값을 길게 해도 좋을 것 같다.
        - 약간…. jwt와 같이 access token / refresh token 같이 사용해야 하는 듯 하다.

### 구조

![Untitled](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/f8003329-f9f4-41dd-8736-7a707b9a43e9)

1. client가 GET /user를 호출하면 RememberMeAuthenticationFilter를 거치게 되고, Authentication가 있는지 확인한다.
2. 없으면 RememberMeServices.autologin()에서 자동 로그인 처리를 한다.
3. RememberMeAuthenticationToken에다가 사용자의 정보와 권한 정보를 넣는다.
4. Authenticationmanager에 전달하고 세션에다가 저장하는 과정을 거친다.

---

## 익명 사용자 - anonymous()

- 스프링 시큐리티에서 "익명으로 인증된" 사용자와 인증되지 않은 사용자 간에 실제 개념적 차이는 없으며 단지 액세스 제어 속성을 구성하는 더 편리한 방법을 제공한다고 볼 수 있다.
    - 즉, 로그인 하지 않은 이용자로 이해하면 편하다.
- SecurityContextHolder 가 항상 Authentication 객체를 포함하고 null 을 포함하지 않는다는 것을 규칙을 세우게 되면 클래스를 더 보안적으로 신경쓸 수있다.
- 인증 사용자와 익명 인증 사용자를 구분해서 어떤 기능을 수행하고자 할 때 유용할 수 있으며 익명 인증 객체를 세션에 저장하지 않는다.
    - 그렇기 때문에, 익명 인증 사용자의 권한을 별도로 운용할 수 있다. **즉 인증된 사용자가 접근할 수 없도록 구성이 가능하다.**
    - **하지만 별로 사용하지는 않는다고 한다….**

### 스프링 MVC 에서 익명 인증 사용하기

- 스프링 MVC가 **HttpServletRequest#getPrincipal 을 사용하여 파라미터를 해결하는데 요청이 익명일 때 이 값은 null 이다.**

```java
public String method(Authentication authentication) {
	 if (authentication instanceof AnonymousAuthenticationToken) {
		 return "anonymous";
	 } else {
		 return "not anonymous";
	 }
}
```

→ AnonymousAuthenticationToken 토큰에 익명 사용자인지 확인한 후 익명 사용자 전용 로직을 작성할 수 있다. 이걸 어떻게 아냐면 Authentication authentication은 null이며, Principal을 통해 익명 사용자를 아는 것이다.

- 만약 익명 요청에서 Authentication 을 얻고 싶다면 @CurrentSecurityContext를 사용하면 된다.
- CurrentSecurityContextArgumentResolver 에서 요청을 가로채어 처리한다.

```java
public String method(@CurrentSecurityContext SecurityContext context){
	 return context.getAuthentication().getName();
}
```

### AnonymousAuthenticationFilter

- SecurityContextHolder 에 Authentication 객체가 없을 경우 감지하고 필요한 경우 새로운 Authentication 객체로 채운다.

### 구조

![Untitled (1)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/e7f56c8c-ac8c-4d45-bdb9-1971dd739583)

1. client가 GET /index를 호출한다. 첫 요청 때는 SecurityContextHolder에 Authentication 객체가 null이다. AnonymousAuthenticationFilter를 거치게 된다.
2. Authentication이 null이면 AnonymousAuthenticationToken을 만들고 SecurityContextHolder에 익명 사용자에 대한 정보를 담는다.
    1. anonymousUser와 ROLE_ANONYMOUS를 담게 된다.

### 실습

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/anonymous").hasRole("GUEST") // 익명 객체만 접근 가능하도록 설정도 가능하다.
        .requestMatchers("/anonymousContext","/authentication").permitAll() // 익명 객체만 접근 가능하도록 설정도 가능하다.
        .anyRequest().authenticated()
)
.anonymous(anonymous -> anonymous
        .principal("guest") // 해당 메서드는 보통 사용자 정보가 들어가는데, 여기서는 익명 사용자의 이름을 담는다. / default 값은 anonymousUser 이다.
        .authorities("ROLE_GUEST") // 익명 사용자를 위한 권한
);
```

---

## 로그아웃

- 스프링 시큐리티는 기본적으로 **DefaultLogoutPageGeneratingFilter 를 통해 로그아웃 페이지를 제공하며 “ GET / logout ” URL 로 접근이 가능하다.**
- 로그아웃 실행은 **기본적으로 POST / logout 으로만 가능하나 CSRF 기능을 비활성화 할 경우 혹은 RequestMatcher 를 사용할 경우 GET, PUT, DELETE 모두 가능하다.**
- **로그아웃 필터를 거치지 않고 스프링 MVC 에서 커스텀 하게 구현할 수 있으며,** **로그인 페이지가 커스텀하게 생성될 경우 로그아웃 기능도 커스텀하게 구현해야 한다.**

### 실습

```java
http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
	.logoutUrl("/logoutProc") 
	.logoutRequestMatcher(new AntPathRequestMatcher("/logoutProc","POST")) 
	.logoutSuccessUrl("/logoutSuccess") 
	.logoutSuccessHandler((request, response, authentication) -> { 
		.response.sendRedirect("/logoutSuccess");
	})
	.deleteCookies("JSESSIONID“, “CUSTOM_COOKIE”) 
	.invalidateHttpSession(true) 
	.clearAuthentication(true) 
	.addLogoutHandler((request, response, authentication) -> {}) 
	.permitAll();
```

- `logoutUrl("/logoutProc")` : 로그아웃이 발생하는 URL 을 지정한다 (기본 값: /logout)
- `logoutRequestMatcher(new AntPathRequestMatcher("/logoutProc","POST"))` : 로그아웃이 발생하는 RequestMatcher 을 지정한다. logoutUrl 보다 우선적이다. 메서드를 지정하지 않으면logout URL이 어떤 HTTP 메서드로든 요청될 때 로그아웃 할 수 있다 .
- `logoutSuccessUrl("/logoutSuccess")` : 로그아웃이 발생한 후 리다이렉션 될 URL이다. 기본값은 "/login?logout"이다.
- `logoutSuccessHandler((request, response, authentication) -> {
  .response.sendRedirect("/logoutSuccess");
  })`
  : 사용할 LogoutSuccessHandler 를 설정하며, 이것이 지정되면 logoutSuccessUrl(String)은 무시된다.
- `deleteCookies("JSESSIONID“, “CUSTOM_COOKIE”)` : 로그아웃 성공 시 제거될 쿠키의 이름을 지정할 수 있다.
- `invalidateHttpSession(true)` : HttpSession을 무효화해야 하는 경우 true (기본값), 그렇지 않으면 false 이다.
- `clearAuthentication(true)` : 로그아웃 시 SecurityContextLogoutHandler가 인증(Authentication)을 삭제 해야 하는지 여부를 명시한다.
- `addLogoutHandler((request, response, authentication) -> {})` : 기존의 로그아웃 핸들러 뒤에 새로운 LogoutHandler를 추가 한다.
- `permitAll()` : `logoutUrl(), RequestMatcher()` 의 URL 에 대한 모든 사용자의 접근을 허용 한다.

### 구조

![Untitled (2)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/b04890a3-dac8-479e-81ca-62bc1cad4dab)

1. client가 POST 방식으로 로그아웃을 진행한다.
2. 요청을 LogoutFilter 가 낚아채어 RequestMatcher에 거쳐 /logout URL로, POST /logout API로 왔는지 두가지 다 확인한 후 로그아웃을 진행한다.