## 다중 보안 설정

시작에 앞서

- Spring Security 는 여러 SecurityFilterChain @Bean을 등록해서 다중 보안 기능을 구성 할 수 있다.

```java
@Configuration
@EnableWebSecurity
public class MultiHttpSecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.securityMatcher("/api/**")
			.authorizeHttpRequests(authorize -> authorize .anyRequest().hasRole("ADMIN"))
			.httpBasic(withDefaults());
		
		return http.build();
	}
	
	@Bean
	public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
		http .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated()) // HttpSecurity 가  /api/ 를 제외한 모든 URL 에 적용 된다
		.formLogin(withDefaults());
		
		return http.build();
	}
}

```

- `@Order` 를 사용하여 어떤 SecurityFilterChain 을 먼저 수행 할지 지정한다. 이 때, 필터의 순서가 중요하다. 그렇기 때문에 어느 filterChain을 먼저 실행시킬지 잘 알아야한다.
    - `@Order` 가 지정되지 않으면 마지막으로 간주 된다.
- `securityMatcher` HttpSecurity가 `/api/`로 시작하는 URL 에만 적용 된다.

### 다중 보안 설정 초기화 구성

![Untitled (2)](https://github.com/user-attachments/assets/8b9e8f88-f35f-4ac5-8908-ab5de07940c7)

### 다중 보안 설정 요청 흐름도

![Untitled (3)](https://github.com/user-attachments/assets/ad6517ba-eba8-4c32-bc0c-749454cbb298)

- 클라이언트가 /api/users로 호출 했을 때 Order(1)을 먼저 확인 한다. 그 다음 Order(2)를 확인 하게 되고 둘 중 하나라도 맞는 filter의 흐름을 타게 된다.
- HttpSecurity 인스턴스 별로 보안 기능이 작동 한다.
- 요청에 따라 RequestMatcher 와 매칭되는 필터가 작동된다.

---

## Custom DSLs

시작에 앞서

- Spring Security 는 사용자 정의 DSL 을 구현할 수 있도록 지원한다.
- DSL 을 구성하면 필터, 핸들러, 메서드, 속성 등을 한 곳에 정의하여 처리할 수 있는 편리함을 제공한다.

### AbstractHttpConfigurer<AbstractHttpConfigurer, HttpSecurityBuilder>

- 사용자 DSL 을 구현하기 위해서 상속받는 추상 클래스로서 구현 클래스는 두 개의 메서드를 오버라이딩 한다.
    - `init(B builder)` HttpSecurity 의 구성요소를 설정 및 공유하는 작업 등
    - `configure(B builder)`  공통 클래스를 구성 하거나 사용자 정의 필터를 생성하는 작업 등

→ builder는 HttpSecurity 값이 들어간다.

### API

- `HttpSecurity.with(C configurer, Customizer<C> customizer)`
    - 기존 6.x 버전 이하는 `apply()` 를 사용했는데 삭제 예정 되는 메서드가 되었다.
    - configurer 는 AbstractHttpConfigurer 을 상속하고 DSL 을 구현한 클래스가 들어간다.
    - customizer 는 DSL 구현 클래스에서 정의한 여러 API 를 커스트 마이징한다.
    - 동일한 클래스를 여러 번 설정하더라도 한번 만 적용 된다.

### 실습

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
	http
		.authorizeHttpRequests(authorize -> authorize
			.anyRequest().authenticated()
	)
	.formLogin(withDefaults());
	
	http.with(new MyCustomDsl(), dsl -> dsl.flag(true));
	
	// MyCustomDsl 을 비활성화 한다
	http.with(new MyCustomDsl(), dsl -> dsl.disabled());
	
	return http.build();
}

```

```java
public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
	
	private boolean flag;
	
	@Override
	public void init(HttpSecurity http) throws Exception {
		super.init(http);
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);
		
		MyAuthFilter myAuthFilter = new MyAuthFilter(manager);
		myAuthFilter.setFlag(flag);
		
		http.addFilterBefore(myAuthFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	public MyCustomDsl flag(boolean value) {
		this.flag = value;
		return this;
	}
	
	public static MyCustomDsl customDsl() {
		return new MyCustomDsl();
	}
}
```

---

## 이중화 설정

시작에 앞서

- 이중화는 **여러 개의 서버를 둔다는 것을 의미**한다. 보통으로는 이중화 된 서버 앞 단엔 로드밸런서가 있고 로드밸런서에서 이중화 된 서버에 요청을 전달한다. 각 각의 서버는 sessionId를 공유할 수 없기에 이중화에 대한 설정이 필요하다. 때문에 세션 서버가 필요하다.
- 이중화는 **시스템의 부하를 분산**하고, **단일 실패 지점(Single Point of Failure, SPOF) 없이 서비스를 지속적으로 제공하는 아키텍처를 구현하는 것을 목표**로 하며 스프링 시큐리티는 이러한 이중화 환경에서 인증, 권한 부여, 세션 관리 등의 보안 기능을 제공한다.
- 스프링 시큐리티는 **사용자 세션을 안전하게 관리하며 이중화된 환경에서 세션 정보를 공유할 수 있는 메커니즘을 제공**하며 대표적으로 **레디스 같은 분산 캐시를 사용하여 세션 정보를 여러 서버 간에 공유할 수 있다.**

### 레디스 세션 서버

- **로컬 환경 (Linux 기준)**
    - 대부분의 Linux 에서 **apt 또는 yum**을 사용하여 레디스를 설치할 수 있다.
        - ex) `sudo apt-get install redis-server, sudo yum install redis`
    - 설치 후 `sudo service redis-server start` 명령어로 레디스 서버를 시작한다.

- **Docker를 사용한 설치**
    - Docker 가 설치된 환경에서 다음 명령어로 레디스 컨테이너를 실행할 수 있다
        - `docker run --name redis -p 6379:6379 -d redis`
    - 이 명령어는 레디스 이미지를 다운로드하고, 이름이 redis인 컨테이너를 백그라운드에서 실행한다
    - 포트 6379를 사용하여 localhost와 연결한다.

### 실습

- **의존성 주입**

```groovy
implementation 'org.springframework.session:spring-session-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

- **xml 설정**

```xml
 spring.data.redis.host=localhost
 spring.data.redis.port= 6379
```

- **config**

```java
@Configuration
@EnableRedisHttpSession
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;
	
	@Value("${spring.data.redis.port}")
	private int port;
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}
}

```

위 작업이 진행됐다면 원래 JSESSIONID라는 세션 명이지만 spring security에서 관리하는 SESSION이라는 명으로 바뀌게 된다. **JSESSIONID는 톰캣 컨테이너에서 세션을 유지하기 위해 발급하는 키이다.**

원래는 **각 서버마다 Tomcat에 의해 각 서버마다 생긴 SESSION이 `spring-session-data-redis` 라이브러리에 의해 모든 세션ID를 관리하는 곳은 redis가 되는 것이다.**
