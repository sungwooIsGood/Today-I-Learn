### 사건 발생

 어느날 갑자기 서버에 AI 페르소나 데이터가 저장되지 않아 에러를 반환한다고 AI 개발자 분이 찾아왔습니다. 저는 AI 페르소나 데이터와 관련해서 매핑이 제대로 되지 않아 데이터가 저장되고 있지 않다고 생각했습니다. 
 별 문제 아니네….라고 생각하며 로그를 들여다 본 순간… 5초간 벙찌게 되었습니다. 아래와 같은 로그 발생 했기 때문입니다.

```
o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 0, SQLState: null
o.h.engine.jdbc.spi.SqlExceptionHelper   : Hikari - Connection is not available, request timed out after 5000ms.
o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.dao.DataAccessResourceFailureException: Unable to acquire JDBC Connection; nested exception is org.hibernate.exception.JDBCConnectionException: Unable to acquire JDBC Connection] with root cause
```

 위 에러는 데이터베이스 connection pool에서 사용 가능한 connection을 가져올 수 없을 때 발생하는 오류 메세지 입니다. 

처음 대면하니 생소하고 당황스러운 로그였습니다… 하지만 정신 차리고 HikariCP 관련 설정을 보면서 원인을 분석해보고자 했습니다. 

1. **yml로 설정해둔 HikariCP의 설정에서 실질적인 pool size가 부족한 것인지?**
2. **DB에 insert 되면서 connection을 오래 물고 있다 보니, connection-timeout이 난 것인지?**

 위 두 가지 의문을 가지며 HikariCP의 HouseKeeper가 생성하는 ‘**pool stats**’를 통해 단서를 얻고자 했습니다.

---

### HouseKeeper의 ‘pool stats’란?

HikariCP의 HouseKeeper가 생성하는 ‘pool stats’ 로그는 HikariCP pool에서 생성된 connection에 대한 통계 정보를 제공하는 로그입니다. 그렇기 때문에 HikariCP관련해서 모니터링하고 디버그하는 데 유용할 수 있습니다.

HouseKeeper는 주기적으로 pool 내의 connection을 검사하고 active connection, idle connection을 정리하는 작업을 수행합니다.

- ‘pool stats’로그의 예시

```
Hikari - Pool stats (total=5, active=2, idle=3, waiting=6)
```

이 로그의 각 부분은 다음과 같은 의미를 가집니다:

- **`Pool stats`**: 로그 메시지의 시작 부분으로, 이 로그가 pool 관련 정보를 보여준다는 것을 나타냅니다.
- **`total=5`**: 현재 풀에 있는 전체 connection의 개수 입니다.
- **`active=2`**: 현재 활성 상태인 connection의 개수 입니다.
- **`idle=8`**: 현재 대기 상태인 connection의 개수 입니다. 이 conneciton의 개수를 통해 현재 사용되지 않는 것을 알 수 있습니다.
- **`waiting=6`**: 연결을 기다리는 클라이언트의 수를 나타냅니다. 이 값은 대기 중인 요청이 있는 경우에만 표시됩니다.

위 로그를 보는 방법은 properties 혹은 yml 설정을 통해 볼 수 있습니다. 저희는 yml를 사용하기 때문에 아래와 같이 작성했습니다.

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG # SQL 쿼리 로그를 활성화하고 로깅 레벨을 DEBUG로 설정
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE # 바인딩된 파라미터 로그를 활성화하고 로깅 레벨을 TRACE로 설정
```

- HikariCP 관련 설정

```yaml
hikari:
  poolName: Hikari
  maximum-pool-size: xx
  max-lifetime: xxx 
  connection-timeout: xxx
```

- **`poolName`:** 이 옵션은 HikariCP 풀의 이름을 설정합니다. 풀의 이름은 로깅 및 모니터링 목적으로 사용됩니다.
- **`maximum-pool-size`**: 이 옵션은 pool이 가질 수 있는 최대 connection 개수를 지정합니다. 이 값은 pool 내의 동시에 연결할 수 있는 connection의 개수를 제한합니다.
- **`max-lifetime`**: 이 옵션은 커넥션의 생명 시간?을 지정합니다. 즉, 커넥션이 pool에서 유지 될 수 있는 최대 시간을 나타냅니다.
- **`connection-timeout`**: 이 옵션은 데이터베이스 연결을 얻는 데 시도하는 최대 시간을 나타냅니다. 만약 연결을 얻는 데 지정된 시간 내에 실패하면 예외가 발생하고, 실패한 요청은 거부됩니다.

---

위 설정을 통해 로그를 더 자세하게 보게 되었습니다. 그럼 본격적으로 로그를 파헤쳐 보겠습니다.

### 원인 분석

1. HikariCP maximum pool size 갯수 = 5개 (좀 더 타이트하게 테스트 한다는 것이 서버에 그대로 올라갔었습니다. default = 10개)
    
    ```
    com.zaxxer.hikari.pool.HikariPool        : Hikari - Pool stats (total=5, active=5, idle=0, waiting=11)
    com.zaxxer.hikari.pool.HikariPool        : Hikari - Fill pool skipped, pool is at sufficient level.
    ```
    

사실 위 에러 로그만으로 원인을 잡아내기는 힘들었습니다. pool size가 꽉 찼을 때에 대한 원인은 너무나도 많기 때문입니다. 그렇기 때문에 주변 로그들을 더 분석해봐야 했습니다.

1. 저희는 AOP를 활용해 API들이 호출되었을 때 raw하게 로그를 찍어 놓고 있었습니다. 그래서 1번 에러가 발생 할 때, 주변 로그들을 살펴보면 답이 나올 것 같아 두 눈 크게 뜨고 분석하기 시작했습니다. 

1. AI 자동 매매 실험실 관련 API가 호출될 때, HikariCP의 connection이 꽉 찼다는 것을 알게 되었습니다. 그래서 해당 API가 insert 될 때 connection을 오래 물고 있는건 아닌가? 하는 의구심이 들게 되었습니다. 자동 매매 실험실 관련하여 담당하시는 개발자분에게 찾아가니 10초에 한번씩 자동 매매 모델 별로 API를 호출해 insert한다는 것을 알게 되었습니다.  

```
DEBUG 1 --- [io-20003-exec-1] com.zaxxer.hikari.pool.HikariPool        : Hikari - Timeout failure stats (total=5, active=5, idle=0, waiting=4)
WARN 1 --- [io-20003-exec-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 0, SQLState: null
ERROR 1 --- [io-20003-exec-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : Hikari - Connection is not available, request timed out after 5001ms.
INFO 1 --- [o-20003-exec-17] xxxService                        : [hidden-data] averageCosts:
```

insert 시 connection을 오래물고 있을 것이라는 의구심을 가지고 호출되는 API가 몇초가 걸리는지 확인해보기 위해 추가로 로그를 찍어보게 되었습니다.

```
INFO 1 --- [io-20003-exec-3] xxxService                        : [hidden-data] averageCosts:
INFO 1 --- [io-20003-exec-3] xxx.xxx.xxxController       : API: hidden-data/{hidden-data}, 걸린 시간:16.523
```

의구심은 확신으로!!!

API하나당 insert하는 시간이 대략  10~20초 사이였던 것이었습니다.

---

### 문제 해결

문제 해결에 앞서 저는 두 가지 방법이 떠올랐습니다.

1. connection의 개수를 늘린다.
2. time out 설정을 늘린다.
3. DB의 데이터가 히스토리성으로서 정말 필요한지 파악한 후 필요가 없다면, hard delete 후 API 리팩토링 진행을 한다.

일단 connection 개수를 늘려 여유 분의 쓰레드를 만들어 주어 해결할까 했지만, 원인 분석과 거리가 멀고 또 언젠가 connection의 개수가 꽉 찰 것으로 생각하게 되어 2번과 3번을 함께 진행하게 되었습니다.

 **그렇다면 insert 하는데 왜 시간이 많이 걸리는지 DB를 보지 않을 수 없었습니다.  API를 통해 insert 되는 테이블을 보니 277만개의 데이터들이 있었습니다. DB에서 이 데이터들이 어떻게 활용 되는지 정말 필요한 데이터들인지 팀원들과 얘기가 이루어졌고 히스토리성으로 가지고 있을 데이터가 아니라고 의견이 모여 지운 후 리팩토링을 진행하게 되었습니다.**
 또한 connection의 개수를 default의 값이 10개로 수정하기로 했습니다.

```
DEBUG 1 --- [ari housekeeper] com.zaxxer.hikari.pool.HikariPool        : Hikari - Pool stats (total=5, active=0, idle=5, waiting=0)
DEBUG 1 --- [ari housekeeper] com.zaxxer.hikari.pool.HikariPool        : Hikari - Fill pool skipped, pool is at sufficient level.
INFO 1 --- [io-20003-exec-3] xxx.xxx.xxxController       : API: hidden-data/{hidden-data}, 걸린 시간:0.007
INFO 1 --- [io-20003-exec-3] xxx.xxx.xxxController       : API: hidden-data/{hidden-data}, 걸린 시간:0.006
INFO 1 --- [io-20003-exec-3] xxx.xxx.xxxController       : API: hidden-data/{hidden-data}, 걸린 시간:0.013
INFO 1 --- [io-20003-exec-3] xxx.xxx.xxxController       : API: hidden-data/{hidden-data}, 걸린 시간:0.016
```

---

### 마치며...

HikariCP 관련 에러를 해결하는 방법은 참 많다고 느꼈습니다. pool size의 개수를 늘리는 방법, conneciton timeout을 늘리는 방법 등 다양하게 존재합니다. 그렇기 때문에 HikariCP 관련 설정을 서버 성능과 상황에 알맞게 유기적으로 잘 가져가야 합니다. 더 나아가 오늘 에러를 해결하면서 아키텍쳐 설계 또한 참 중요하다고 느꼈습니다.

또한, API가 어떤 식으로 호출 되는지 이번 기회에 협업이 중요하다고 생각이 든 시간이었습니다.

감사합니다.
