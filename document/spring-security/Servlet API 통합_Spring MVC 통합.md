## Servlet API 통합

시작에 앞서

- 스프링 시큐리티는 **다양한 프레임워크 및 API 와의 통합을 제공하고 있다.**
    - Servlet 3 과 Spring MVC 와 통합을 통해 여러 편리한 기능들을 사용할 수 있다.
- 인증 관련 기능들을 필터가 아닌 **서블릿 영역에서 처리할 수 있다.**

### 1. SecurityContextHolderAwareRequestFilter

- HTTP 요청이 처리될 때 `HttpServletRequest` **에 보안 관련 메소드를 추가적으로 제공하는 래퍼(`SecurityContextHolderAwareRequestWrapper`) 클래스를 적용한다.**
    - 전달 받은 `HttpServeltRequest`에다 래핑하는 클래스를 제공하여 보안 처리를 진행하게 해준다. 그래서 이 요청 객체에 보안 작업이 끝난 후 여러 Filter들을 거쳐 Servlet까지 도달하게 된다.
- 그래서 개발자는 Servlet API 의 보안 메소드를 사용하여 인증, 로그인, 로그아웃 등의 작업을 수행할 수 있다.

### 2. HttpServlet3RequestFactory

- Servlet 3 API 와의 통합을 제공하기 위한 `Servlet3SecurityContextHolderAwareRequestWrapper`객체를 생성한다.

### 3. Servlet3SecurityContextHolderAwareRequestWrapper

- `HttpServletRequest` 의 래퍼 클래스로서Servlet 3.0의 기능을 지원하면서 동시에 `SecurityContextHolder` 와의 통합을 제공한다.
- 이 래퍼를 사용함으로써SecurityContext 에 쉽게 접근할 수 있고 **Servlet 3.0의 비동기 처리와 같은 기능을 사용하는 동안 보안 컨텍스트를 올바르게 관리할 수 있다.**

### 구조 및 API

![Untitled (2)](https://github.com/user-attachments/assets/0fb27d85-f6ba-4197-87d9-e838d6604070)

- `SecurityContextHolderAwareRequestFilter` 는 인증을 수행하기 위한 핵심적인 클래스, 유지 하기 위한 클래스, 로그아웃을 위한 클래스 등을 구현하게 해준다.
    - **즉, 인증에 관련된 핵심적인 부분을 가지고 있다.**
- `SecurityContextHolderAwareRequestFilter` 작업이 실행되면 `HttpServlet3RequestFactory` 가 생성되고 인증을 수행할 수 있는 객체들을 가지게 된다.
    - **즉, Filter가 전달해준 객체를 사용해서 인증을 수행할 수 있는 준비 작업을 한다. → `HttpServlet3RequestFactory` 를 만든다.**
- `HttpServlet3RequestFactory` 는   `Servlet3SecurityContextHolderAwareRequestWrapper` 클래스를 생성한다. 추가적인 보안 메서드를 가지고 있어 인증에 대한 여러가지 작업들을 추가할 수 있다. 그래서 나중에 MVC나 Serlvlet에서 사용할 수 있게 된다.

### 실습

```java
@GetMapping("/login")
public String login(HttpServletRequest request, MemberDto memberDto) throws ServletException, IOException {
	request.login(memberDto.getUsername(),memberDto.getPassword());
	System.out.println("PreAuthorize");
	return "/index";
} 

@GetMapping("/users")
public List<User> users(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	boolean authenticate = request.authenticate(response);
	
	if(authenticate){
		UserService.users();
	}
	
	return Collection.emptyList();
}

```

- Spring MVC는 Servlet이기에 `(HttpServletRequest request)` 를 받게 되는데, `HttpServletRequest` 는 `Servlet3SecurityContextHolderAwareRequestWrapper` 객체이다.
- 여기서 `request.login(memberDto.getUsername(),memberDto.getPassword());` 를 통해 이전에 공부했던 시큐리티에서 인증 Filter에서 처리했던 작업들을 진행하게 된다.
- 즉, 편리하게 MVC에서 구현할 수 있다는 장점을 가지고 있다.
- `request.authenticate(response);` 도 마찬가지로 MVC와 통합하여 편리하게 인증 상태인지 알 수 있는 것이다.

---

## Spring MVC 통합

### @AuthenticationPrincipal

- Spring Security는 Spring MVC 인자에 대한 현재 `Authentication.getPrincipal()`을 자동으로 해결 할 수 있는 `AuthenticationPrincipalArgumentResolver`를 제공한다.
    - 이전에는 코드로 직접 SecurityContextHolder에 접근하여 인증 객체를 꺼내와야 했다. 이 또한, 반복적인 작업이 될 수 있는데 `@AuthenticationPrincipal` 를 통해 반복적인 코드 작업이 최소화 될 수 있다.
- Spring MVC 에서 `@AuthenticationPrincipal` 을 메서드 인자에 선언하게 되면 Spring Security 와 독립적으로 사용할 수 있다.

![Untitled (3)](https://github.com/user-attachments/assets/37b76332-17b4-4697-864a-cb9539efd0d0)

### @AuthenticationPrincipal(expression="표현식")

- Principal객체 내부에서 특정 필드나 메서드에 접근하고자 할 때 사용할 수 있으며 사용자 세부 정보가 Principal내부의 중첩된 객체에 있는 경우 유용하다.
    - 즉, 인증 객체 안에 customer 변수에 해당하는 값을 가져올 수 있다는 것이다.

```java
@RequestMapping("/user")
public Customer findUser(@AuthenticationPrincipal(expression = "customer") Customer customer) {…}

// 추가 예시, User 객체(시큐리티에서 제공) 안에 username이 있다. 그 username의 값을 바로 가지고 올 때.
@RequestMapping("/user")
public Customer findUser(@AuthenticationPrincipal(expression = "username") String customer) {…}

```

![Untitled (4)](https://github.com/user-attachments/assets/55ce2590-bc73-48b5-ad5b-5f26491fd03d)

### @AuthenticationPrincipal 메타 주석

![Untitled (5)](https://github.com/user-attachments/assets/7fba07df-bb31-4ccc-9af7-dbd5c22c7523)

- `@AuthenticationPrincipal` 을 커스텀 주석으로 메타 주석화하여 Spring Security 에 대한 종속성을 제거할 수도 있다.

```java
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser{}
```

→ 인증을 받지 못한 유저는 기본적으로 String인 anonymous로 들어온다. 이걸 원하지 않는다면, `@AuthenticationPrincipal(expression="")` 을 사용하면 된다.

- ex) anonymous 유저면 null로 받겠다.
    - `@AuthenticationPrincipal(expression="#this == 'anonymousUser'? null : username")`
    - `#this` ⇒ principal 객체

---

## Spring MVC 비동기 통합

시작에 앞서

- Spring Security 는 Spring MVC Controller 에서 `Callable` 을 실행하는 비동기 스레드에 `SecurityContext` 를 자동으로 설정하도록 지원한다.
- Spring Security 는 `WebAsyncManager` 와 통합하여 `SecurityContextHolder` 에서 사용 가능한 `SecurityContext` 를 `Callable` 에서 접근 가능하도록 해 준다.

→ 이 말은 부모 스레드도 스레드 로컬이 있고 자식 스레드도 스레드 로컬을 가지고 있다. 하지만 부모 스레드에 있는 스레드 로컬이 가지는 `SecurityContext` 객체가 자식 스레드 로컬에게 공유가 되지 않는다. **하지만 `Callable` 을 실행하게 되면 별도의 스레드가 생성되어 부모 스레드와 자식 스레드간에 `SecurityContext` 를 공유한다는 말이다.**

### WebAsyncManagerIntegrationFilter

- `SecurityContext` 와 `WebAsyncManager` 사이의 통합을 제공하며 `WebAsyncManager` 를 생성하고 `SecurityContextCallableProcessingInterceptor`를 `WebAsyncManager` 에 등록한다.

### WebAsyncManager

- 스레드 풀의 비동기 스레드를 생성하고 `Callable` 를 받아 실행시키는 주체로서 등록된 `SecurityContextCallableProcessingInterceptor` 를 통해 현재 스레드(부모 스레드)가 보유하고 있는 `SecurityContext` 객체를 비동기 스레드(자식 스레드)의 스레드 로컬 에 저장시킨다.

### 실습

```java
@GetMapping("/callble")
public Callable<Authentication> processUpload() {

	// Main Thread 영역
	SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().getContext();
	System.out.println("securityContext = " + securityContext);

	// 비동기 Thread 영역
	return new Callable<Authentication>() {
		public Authentication call() throws Exception {
		
		// 부모 스레드가 가지고 있는 똑같은 securityContext에 접근한 것이다.
		SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().getContext();
		
		System.out.println("securityContext = " + securityContext);
		Authentication authentication = securityContext.getAuthentication();
		
		return authentication;
	}
	
	// Main Thread 영역
	// .....
}
```

- 비동기 스레드가 수행하는 `Callable` 영역 내에서 자신의 `ThreadLocal` 에 저장된 `SecurityContext` 를 참조할 수 있으며 이는 부모 스레드가 가지고 있는`SecurityContext` 와 동일한 객체이다.
- **중요!**
- **`@Async`나 다른 비동기 기술은 스프링 시큐리티와 통합되어 있지 않기 때문에 비동기 스레드에 `SecurityContext` 가 적용되지 않는다.**

### 흐름도

![Untitled (6)](https://github.com/user-attachments/assets/c7e2490b-0467-4476-aba8-a77505f7c98d)

1. 클라이언트가 요청을 하면 부모 스레드가 받게 된다.
2. `WebAsyncManagerIntegrationFilter`가 `WebAsyncManager` 와 `SecurityContextCallableProcessingInterceptor`  객체를 생성하고 한다.
3. `WebAsyncManager` 가 ThreadPoolExecutor를 이용해서 비동기 스레드를 실행할 수 있는 스레드를 생성한다. 스레드 풀에 있는 스레드를 이용한다.
4. `WebAsyncManager` 클래스가 부모 스레드가 가지고 있는 `SecurityContext` 를 `SecurityContextCallableProcessingInterceptor` 에 미리 저장 해놓는다.
5. 생성된 비동기 스레드는 스레드 로컬이 있을 건데 현재까지 스레드 로컬에는 `SecurityContext` 객체가 없다.
6. `SecurityContextCallableProcessingInterceptor` 부터 `SecurityContext` 객체를 가지고와 비동기 스레드 안에 있는 스레드 로컬에 저장한다.
7. 그 후 `Callable` 을 수행하는 비동기 스레드는 자신의 스레드 로컬에 저장되어 있는 `SecurityContext` 를 참조할 수 있다.
