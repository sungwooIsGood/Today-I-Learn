## 동시 세션 제어

주 메서드: `sessionManagement().maximumSessions()`

**⇒ 해당 메서드를 통해 설정하지 않으면 세션 관리 필터자체를 만들지 않는다. 반드시 명시적으로 적어야한다.**

시작에 앞서, **동시 세션 제어는 사용자가 동시에 여러 세션을 생성하는 것을 관리하는 전략이다.** 이 전략은 사용자의 인증 후에  활성화된 세션의 수가 설정된 maximumSessions 값과 비교하여 제어 여부를 결정한다.

→ 이 전략이 생겨난 이유는 컴퓨터에서 인증, 모바일에서 인증, 패드에서 인증 등 똑같은 사용자지만 sessionId는 다 다르다고 모두 로그인이 되어있을 수 있다.

**하지만 시큐리티에서는 `maximumSessions()` 개수를 통해 한 군데서만 로그인이 가능하게 한다던지 할 수 있다. 예를 들어 컴퓨터에 로그인 했다가 모바일로 로그인 하면 컴퓨터로 로그인 했던 정보를 session에서 지울 수 있다.**

### 동시 세션 제어 2 가지 유형

![Untitled](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/d4457a10-036a-4239-af24-e23c50752a4b)
1. 최대 허용 개수만큼 동시 인증이 가능하고 그 외 이전 사용자의 세션을 만료 시키는 기능
2. 최대 허용 개수만큼 동시 인증이 가능하고 그 외 사용자의 인증 시도를 차단 기능

### `sessionManagement()` API 동시 세션 제어

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	
	http.sessionManagement(session -> session
		.invalidSessionUrl(“/invalidSessionUrl”) 
		.maximumSessions(1) 
		.maxSessionsPreventsLogin(true)
		.expiredUrl("/expired"));
	
	return http.build();
}
```

- `invalidSessionUrl(“/invalidSessionUrl”)`
    - **이미 만료된 세션으로 요청을 하는 사용자**를 특정 엔드포인트로 리다이렉션 할 Url 을 지정한다.
- `maximumSessions(1)`
    - /사용자 당 최대 세션 수를 제어한다. 기본값은 무제한 세션을 허용한다.
- `maxSessionsPreventsLogin(true)`
    - `true`이면 최대 세션 수(maximumSessions(int))에 도달했을 때 사용자의 인증을 방지한다.
        - **‘동시 세션 제어 2 가지 유형’ 중 두 번째 유형**
    - `false`(기본 설정)이면 인증하는 사용자에게 접근을 허용하고 기존 사용자의 session은 만료된다.
        - **‘동시 세션 제어 2 가지 유형’ 중 첫 번째 유형**
- `expiredUrl("/expired")`
    - **세션을 만료하고 나서 리다이렉션 할 URL 을 지정한다.**

---

## 세션 고정 보호

### 세션 고정 보호 전략

- **세션 고정 공격은 악의적인 공격자가 사이트에 접근하여 세션을 생성한 다음 다른 사용자가 같은 세션으로 로그인하도록 유도하는 위험을 말한다.**
    - 제 3자가 서버에서 받은 쿠키 값을 클라이언트에게 심어서 공격자의 쿠키를 가지고 서버에 접속을 하는 것이다.
- 스프링 시큐리티는 사용자가 로그인할 때 새로운 세션을 생성하거나 세션 ID를 변경함으로써 이러한 공격에 자동으로 대응한다.

### 세션 고정 공격

![Untitled (1)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/f3e8ddbb-bd32-4703-9cb3-9ee8d0b28eef)
1. 공격자가 먼저 사이트에 접속한다. 그럼 세션이 생성 되었을 것이다.
2. 공격자에게 세션 쿠키를 전송한다.
3. 공격자가 공격할 사용자에게 세션 쿠키를 사용자에게 심는다.
    1. 사용자는 공격자의 쿠키를 가지고 있는 것이다.
4. 공격 받은 사용자가 로그인 해서 인증에 성공 한다면 공격자가 사이트의 인증 절차도 없이 클라이언트의 모든 정보를 공유하며 볼 수 있게 된다.

### `sessionManagement()` API 세션 고정 보호

- 사용자가 로그인 할 때 공격자의 쿠키가 사이트에 들어가지만 않으면 된다. ⇒ 사용자가 로그인 할 때, 새로운 sessionId를 생성하거나, sessionId를 변경하면 된다.

```java
@Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	 http.sessionManagement((session) -> session
		 .sessionFixation(sessionFixation -> sessionFixation.newSession())
	 );
	 
	 return http.build();
 }
```

- `changeSessionId()`
    - 기존 session을 유지하면서 sessionID만 변경하여, 인증 과정에서 세션 고정 공격을 방지하는 방식이다. 기본 값으로 설정되어 있다.
    - 특별한 이유가 없다면 기본 값인 `changeSessionId()` 전략 사용이 권장된다.
- `newSession()`
    - 새로운 session을 생성하고 기존 session 데이터를 복사하지 않는 방식이다(SPRING_SECURITY_ 로 시작하는 속성은 복사한다.)
- `migrateSession()`
    - 새로운 session을 생성하고 모든 기존 session 속성을 새 session으로 복사한다.
- `none()`
    - 기존 session을 그대로 사용한다.
    - 권장 x, 절대로 사용 x, 보안에 취약하다.

---

## 세션 생성 정책

- 스프링 시큐리티에서는 인증된 사용자에 대한 세션 생성 정책을 설정하여 어떻게 세션을 관리할지 결정할 수 있으며 이 정책은 `SessionCreationPolicy`로 설정하면 된다.

### 세션 생성 정책 전략

- **`SessionCreationPolicy. ALWAYS`**
    - 인증 여부에 상관없이 항상 세션을 생성한다.
    - `ForceEagerSessionCreationFilter`클래스를 추가 구성하고 **세션을 강제로 생성시킨다.**
- **`SessionCreationPolicy. NEVER`**
    - 스프링 시큐리티가 세션을 생성하지 않지만애플리케이션이 **이미 생성한 세션은 사용할 수 있다.**
- **`SessionCreationPolicy. IF_REQUIRED`**
    - **필요한 경우에만 세션을 생성한다.** 예를 들어 인증이 필요한 자원에 접근할 때 세션을 생성한다.
- **`SessionCreationPolicy. STATELESS`**
    - **세션을 전혀 생성하거나 사용하지 않는다.**
    - 인증 필터는 인증 완료 후 SecurityContext 를 세션에 저장하지 않으며 **JWT와 같이 세션을 사용하지 않는 방식으로 인증을 관리할 때 유용할 수 있다.**
    - **`SecurityContextHolderFilter`는 세션 단위가 아닌 요청 단위로 항상 새로운 SecurityContext 객체를 생성하므로 컨텍스트 영속성이 유지되지 않는다.**

### `sessionManagement()` API 세션 생성 정책

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http.sessionManagement((session) -> session
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  );
  
  return http.build();
}
```

### 추가로, STATELESS설정에도 세션이 생성될 수 있다.

- 스프링 시큐리티에서 기본적으로 CSRF 기능이 활성화 되어 있고 **CSRF 기능이 수행 될 경우 사용자의 세션을 생성해서 CSRF 토큰을 저장하게 된다.**
    - CSRF는 공격자에 공격에 방어하기 위한 토큰으로 세션 작업이 필요한 것 때문에 세션을 STATELESS로 해도 세션이 만들어져 있다.
- 세션은 생성되지만 CSRF 기능을 위해서 사용될 뿐 인증 프로세스의 SecurityContext 영속성에 영향을 미치지는 않는다.

---

## SessionManagementFilter & ConcurrentSessionFilter

### SessionManagementFilter

- **요청이 시작된 이후 사용자가 인증되었는지 감지하고, 인증된 경우에는 세션 고정 보호 메커니즘을 활성화하거나 동시 다중 로그인을 확인하는 등 세션 관련 활동을 수행하기 위해 설정된 세션 인증 전략(`SessionAuthenticationStrategy`)을 호출하는 필터 클래스이다.**
- 스프링 시큐리티6 이상에서는 SessionManagementFilter가 기본적으로 설정 되지 않으며 `SessionManagement` API 를 설정을 통해 생성할 수 있다. 이전 버전에서는 Default였다.



### 세션 구성 요소

![Untitled (2)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/e59f369d-c930-448e-b0ac-15f4655ae7b8)

- `SessionManageMentFilter`는 `SessionAUthenticationStrategy` 인터페이스를 구현하는 구현체가 4개 있다.
    - **`ChangeSessionIdAuthenticationStrategy`**
        - 세션 아이디를 변경 → 세션 고정 공격에 대해 방어하고 보호하는 역할
    - **`ConcurrentSessionControlAuthenticationStrategy`**
        - 동시 세션 제어 → 기존 세션 만료 처리, 세션이 max상태일 시 로그인 차단
    - **`RegisterSessionAuthenticationStrategy`**
        - 세션 정보 관리 → 사용자가 인증 후, 세션 관련 정보 CRUD 작업을 수행
    - **`SessionFixationProtectionStrategy`**
        - 세션 고정 보호 → `ChangeSessionIdAuthenticationStrategy` 와 동일한 역할을 한다. 세션 고정 보호 역할
- **`SessionCreationPolicy`**
    - 세션 생성 정책을 담당하는 구현체이다.

### ConcurrentSessionFilter

- 각 요청에 대해 SessionRegistry에서 SessionInformation 을 검색하고 **세션이 만료로 표시되었는지 확인하고 만료로 표시된 경우 로그아웃 처리를 수행한다.**(세션 무효화)
    - **즉, 세션 만료 여부 확인, 만료 시 로그아웃 처리 수행 → 동시 세션 제어와 밀접한 관계가 있는 Filter이다.**
- 각 요청에 대해 `SessionRegistry.refreshLastRequest(String)`를 호출하여 **등록된 세션들이 항상 '마지막 업데이트' 날짜/시간을 가지도록 한다.**
    - 시간 정보들을 최신으로 업데이트

**⇒ 세션 만료 체크, 시간 정보 업데이트**

### 흐름도

![Untitled (3)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/1bcf745c-0986-45ec-aa77-3655e1a1b192)

1. 사용자가 인증 요청을 보낸다.
2. SessionManagementFilter에서 세션 1개 추가
    1. 만약 세션 허용 개수가 초과 되었다면 사용자의 세션을 만료 시켜야한다. → 세션 제어 정책에 의해 사용자 세션을 만료 시킨다.`session.expireNow()`
        1. 아직 만료된 것은 아님. 설정만 한 것
3. 그 다음 사용자는 재접속을 진행한다.
4. ConcurrentSessionFilter를 호출한다.
    1. `session.isExpired()`를 통해 세션이 만료되었는지 확인한다. → SessionManagementFilter에서 설정한(`session,expireNow()`) 정보를 체크해본다.
    2. 만료 되었다면 로그아웃 처리를 해버린다.

### 시퀀스 다이어그램

![Untitled (4)](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/0459af7a-1534-4170-9c52-ce478068ff7d)

- **사용자는 2명 - user1, user2로 동일한 계정으로 로그인한다고 가정, 최대 세션 허용 개수는 1개**
1. **user1이 로그인 인증 요청을 보낸다.**
2. **UsernamePasswordAuthenticationFilter가 인증 관련** 처리를 진행 하며, 인증에 성공하면서 후속 처리 작업으로 세션 관련 작업을 수행해야 한다.
    1. SessionManagementFilter가 세션 관리 필터이지만, 인증 처리를 진행 하면서 UsernamePasswordAuthenticationFilter안에서도 SessionManagementFilter의 구현체들을 호출하여 세션 관련 작업들이 수행되어야 한다.
    2. 그렇기 때문에 `ConcurrentSessionControlAuthenticationStrategy`구현체(동시 세션 제어)를 통해 현재 세션의 개수를 알 수 있다. → 0개이니 로그인 성공
3. **로그인 성공이 되면서 하는 작업들을 수행**
    1. `ChangeSessionIdAuthenticationStrategy` 구현체가 세션 고정 공격에 대해 방어하고 보호를 위해 세션 아이디를 변경한다.
    2. session count가 1로 업데이트 된다. 세션 정보를 등록 시키기 위해`RegisterSessionAuthenticationStrategy` 구현체가 세션 정보를 저장
4. **user2가 로그인 인증 요청을 보낸다.**
5. **현재 이미 세션 개수가 1이기 때문에 SessionManagementFilter에서  `ConcurrentSessionControlAuthenticationStrategy` 구현체에서 user2에 대한 처리가 필요하다.**
    1. 동시 세션 제어에서 인증 시도 전략이 차단하는 전략 시 `sessionAuthenticationException`예외가 발생 → 인증 실패
    2. 동시 세션 제어에서 세션 만료 전략 시 user1의 세션을 `session.expireNow()`를 시킨다.
        1. 최대 세션 정책은 1개지만 현재 user1의 세션을 `session.expireNow()` 통해 만료 시켰다고하여 세션이 현재는 없어진게 아니다. 즉, 현재 세션은 2개이다. user1이 재접속 할 때 세션 삭제가 진행된다.
6. **user2 로그인 성공 되면서 세션 고정 보호 전략에 의해(`session.changeSessionId()`) 세션 아이디가 변경하고 저장한다.**
7. **user1 다시 재접속을 진행한다.**
8. **user1은 ConcurrentSessionFilter에 의해 현재 세션이 만료 되었는지 확인한다. → 위에서 `session.expireNow()` 처리를 했기에 true로 만료 됨.**
9. **만료 되었다면 로그아웃을 진행한다.**