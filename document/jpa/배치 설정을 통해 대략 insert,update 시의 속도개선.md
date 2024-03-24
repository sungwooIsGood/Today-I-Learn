- 이벤트 페이지를 맡아 진행하다 보니 대용량까지는 아니지만 다량의 update문이 발생하면서, 경험한 일을 더 깊게 공부해보고자 적어본 글이다.

## **하이버네이트 배치**

- Batch는 이름에서 보이다시피 ‘일괄’이라는 의미를 내포한다. 때문에 개발자들에게는 **Batch라는 작업은 보통 대량의 작업을 한번에 처리하는 경우를 말한다. 하이버네이트에서도 Batch를 제공한다.**
- **실행 순서**
    1. 하이버네이트 배치기능을 적용하게 되면 설정한 배치 갯수에 도달할 때까지`PreparedStatement.addBatch()`를 호출 하여 실행할 쿼리를 추가
    2. 설정한 배치 갯수에 도달하게 되면 `PreparedStatement.executeBatch()`를 호출한다.
    3. DB 드라이버에서는 `PreparedStatement.addBatch()`를 통해 추가된 쿼리를 재조합하여 DB 로 한번에 전송한다.
- **여러개의 쿼리를 한번에 모아서 처리하기 때문에 단건씩 쿼리를 수행할 때에 비해 DB 와 통신하는 횟수도 줄어들고, DB 에서도 락을 잡는 횟수가 줄어들어 실행 속도가 향상된다.**
- 여기서 기억해야 할 건, ‘**배치 갯수에 도달할 때 까지 실행할 쿼리를 추가’한다는 점이다. 이 문장으로부터 이 글은 시작된 것이라 해도 과언이 아니다.**

---

## 설정

- 설정은 간단하다 application.yml을 통해 아래와 같이 추가해주면 된다.

```
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 100
```

> **적당한 Batch size**
>
>
> 100~1000개 사이 권장한다고 한다.
>
> Batch size의 크기가 크면 순간적으로 DB와 애플리케이션에 순간 부하가 증가할 수 있다.
>
> DB와 애플리케이션이 순간 부하를 어디까지 견딜 수 있는 지에 따라 결정하면 된다.
>
> 100개든 1000개든 메모리 사용량은 거의 똑같다.
>

---

## SAVE

### ID값이 자동증가면 안된다.

- **GenerationType.IDENTITY 전략을 사용하면 Batch insert가 되지 않는다.**
- 이는 굉장히 중요한 문제이다.
    - saveAll()을 통해 bulk성으로 DB에 한번에 전달해주는 메서드가 있다. 이 기능을 활용하지 못한다는 것이다.
    - 아래는 우아한형제들 기술블로그를 가져온 예시이다.
- 엔티티

```java
public class NonIdentityEntity {

    @Id
    @Column(name = "id", updatable = false)
    @EqualsAndHashCode.Include
    private Long id;
    //...    
}
```

- saveAll() 처리 메서드

```java
void saveAll() throws Exception {
    final int size = 3;
    final List<NonIdentityEntity> nonIdentityEntities = createNonIdentityEntities(size);

    for (NonIdentityEntity nonIdentityEntity : nonIdentityEntities) {
        nonIdentityEntityRepository.save(nonIdentityEntity);
    }
    flush();
}
```

- 우리의 기대는 아래와 같이 한번의 insert문이어야 했다. 하지만 더 아래 호출된 sql문을 보자.

```java
[main] MySQL : [QUERY] insert into non_identity (c1, c2, c3, c4, c5, version, id) values (1, 'c2-1', 'c3-1', 'c4-1', 'c5-1', 0, 1),(1, 'c2-1', 'c3-1', 'c4-1', 'c5-1', 0, 2),(1, 'c2-1', 'c3-1', 'c4-1', 'c5-1', 0, 3) [Created on: Mon Sep 21 00:15:45 KST 2020, duration: 3, connection-id: 305, statement-id: 0, resultset-id: 0,    at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
```

```java
[main] org.hibernate.SQL                        : 
    insert 
    into
        non_identity
        (c1, c2, c3, c4, c5, version, id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
[main] org.hibernate.SQL                        : 
    insert 
    into
        non_identity
        (c1, c2, c3, c4, c5, version, id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
[main] org.hibernate.SQL                        : 
    insert 
    into
        non_identity
        (c1, c2, c3, c4, c5, version, id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
```

- 3개의 insert문이 나간 것을 확인했다. 이상하지 않은가??? 분명 batch를 설정하면 단 한번의 쿼리문이 나가는데 3개의 쿼리문이 나간 것이다. 아래와 같은 블로그를 보면 알 수 있다.

![Untitled](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/0dfa8c91-dd9a-4a01-9cb4-8176a76ca01c)


- **batch 옵션은 Hibernate가 직접 insert문을 합쳐주지는 않고 addBatch할 뿐이다.**
- **MySQL JDBC의 경우 JDBC URL에 rewriteBatchedStatements=true 옵션을 추가해주면 된다.**
- **MySQL의 경우 실제로 생성된 쿼리는 logger=com.mysql.jdbc.log.Slf4JLogger&profileSQL=true 옵션으로 로그를 통해 확인할 수 있다.**

- 적용한 yml

```java
spring:
  datasource:
    hikari:
      jdbc-url: jdbc:mariadb://192.168.10.6:3306/dev?profileSQL=true&maxQuerySizeToLog=0
```

- 테스트해본 DB는 mariaDB여서  **rewriteBatchedStatements=true은 빼주었다.**
- `profileSQL=true` 과 `logger=Slf4JLogger` 에 더불어 `maxQuerySizeToLog=999999` 를 추가로 더 설정할 수 있다.
    - `profileSQL=true` : **Driver 에서 전송하는 쿼리를 출력한다.**
    - `logger=Slf4JLogger` : **Driver 에서 쿼리 출력시 사용할 로거를 설정한다.**
        - MySQL 드라이버 : 기본값은 `System.err` 로 출력하도록 설정되어 있기 때문에 필수로 지정해 줘야한다.
        - **MariaDB 드라이버 : Slf4j 를 이용하여 로그를 출력하기 때문에 설정할 필요가 없다.**
- `maxQuerySizeToLog=999999` : 출력할 쿼리 길이
    - MySQL 드라이버 : 기본값이 0 으로 지정되어 있어 값을 설정하지 않을 경우 아래처럼 쿼리가 출력되지 않을 수 있다.

        ```
        [main] MySQL : [QUERY]  ... (truncated) [Created on: Mon Sep 21 01:03:10 KST 2020, duration: 3, connection-id: 325, statement-id: 0, resultset-id: 0,   at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
        ```

    - **MariaDB 드라이버 : 기본값이 1024 로 지정되어 있습니다. MySQL 드라이버와는 달리 0으로 지정시 쿼리의 글자 제한이 무제한으로 설정됩니다.**

**설정 관련 출처**

**mysql:** https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
**mariaDB:** https://mariadb.com/kb/en/about-mariadb-connector-j/

- 위에와같은 설정을 진행 한 후에야 아래와 같이 sql문 나가고 직접 눈으로 batch가 정상적으로 작동한 것을 볼 수 있다.

```java
[main] org.hibernate.SQL                        : 
    insert 
    into
        non_identity
        (c1, c2, c3, c4, c5, version, id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
[main] org.hibernate.SQL                        : 
    insert 
    into
        non_identity
        (c1, c2, c3, c4, c5, version, id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
[main] org.hibernate.SQL                        : 
    insert 
    into
        non_identity
        (c1, c2, c3, c4, c5, version, id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
[main] MySQL : [QUERY] insert into non_identity (c1, c2, c3, c4, c5, version, id) values (1, 'c2-1', 'c3-1', 'c4-1', 'c5-1', 0, 1),(1, 'c2-1', 'c3-1', 'c4-1', 'c5-1', 0, 2),(1, 'c2-1', 'c3-1', 'c4-1', 'c5-1', 0, 3) [Created on: Mon Sep 21 00:15:45 KST 2020, duration: 3, connection-id: 305, statement-id: 0, resultset-id: 0,    at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
```

---

## Update

### ID가 자동증가 값일 때도 batch update는 가능하다.

먼저 `GenerationType.IDENTITY`를 적용한 `IdentityEntity`를 업데이트 해보자.

```java
List<SignPickEventUserInfo> all = signPickEventUserInfoRepository.findAll(); // select * from test
int temp = 0;
for(SignPickEventUserInfo info : all){
    info.update(temp); // update문
    temp++;
}
```

```java
Hibernate: 
    update
        sign_pick_event_user_info 
    set
        updated_at=?,
        even_today_remain_pick=?,
        odd_today_remain_pick=?,
        rank_1_week=?,
        rank_2_week=?,
        total_1_week_pick_count=?,
        total_2_week_pick_count=?,
        total_earning_rate1=?,
        total_earning_rate2=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        sign_pick_event_user_info 
    set
        updated_at=?,
        even_today_remain_pick=?,
        odd_today_remain_pick=?,
        rank_1_week=?,
        rank_2_week=?,
        total_1_week_pick_count=?,
        total_2_week_pick_count=?,
        total_earning_rate1=?,
        total_earning_rate2=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        sign_pick_event_user_info 
    set
        updated_at=?,
        even_today_remain_pick=?,
        odd_today_remain_pick=?,
        rank_1_week=?,
        rank_2_week=?,
        total_1_week_pick_count=?,
        total_2_week_pick_count=?,
        total_earning_rate1=?,
        total_earning_rate2=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        sign_pick_event_user_info 
    set
        updated_at=?,
        even_today_remain_pick=?,
        odd_today_remain_pick=?,
        rank_1_week=?,
        rank_2_week=?,
        total_1_week_pick_count=?,
        total_2_week_pick_count=?,
        total_earning_rate1=?,
        total_earning_rate2=?,
        user_id=? 
    where
        id=?
[2023-03-29 05:36:30:90932][http-nio-20002-exec-2] INFO  o.m.j.i.logging.ProtocolLoggingProxy - conn=4727496(M) - 5.456 ms - Query: update sign_pick_event_user_info set updated_at=?, even_today_remain_pick=?, odd_today_remain_pick=?, rank_1_week=?, rank_2_week=?, total_1_week_pick_count=?, total_2_week_pick_count=?, total_earning_rate1=?, total_earning_rate2=?, user_id=? where id=?, parameters ['2023-03-29 05:36:30.2394401',2,0,1,0,8,0,1.54,0.0,'69c4c3ea-7b55-49e1-bdd4-a00af437029c',59],['2023-03-29 05:36:30.2514396',0,1,2,0,10,0,1.09,0.0,'c43a0158-f9c8-4a80-897c-7838769cf574',60],['2023-03-29 05:36:30.2514396',0,2,3,0,10,0,0.4,0.0,'3b08cc76-37c5-4f20-867a-67483b1dd58c',61],['2023-03-29 05:36:30.2514396',6,3,4,0,4,0,0.34,0.0,'b94ffaa2-770e-4b0c-ba62-51fcd47664ed',62]
[2023-03-29 05:36:30:90940][http-nio-20002-exec-2] INFO  o.m.j.i.logging.ProtocolLoggingProxy - conn=4727496(M) - 5.827 ms - Query: COMMIT
[2023-03-29 05:36:30:90946][http-nio-20002-exec-2] INFO  o.m.j.i.logging.ProtocolLoggingProxy - conn=4727496(M) - 5.862 ms - Query: set autocommit=1
```

- update할 엔티티가 3개 이하일 때

```java
void test(){
    List<SignPickEventUserInfo> all = signPickEventUserInfoRepository.findAll(); // select * from test
    int temp = 0;
    for(SignPickEventUserInfo info : all){
        info.update(temp); // update문
        temp++;
        if(temp == 2){
            break;
        }
    }
}
```

```java
Hibernate: 
    update
        sign_pick_event_user_info 
    set
        updated_at=?,
        even_today_remain_pick=?,
        odd_today_remain_pick=?,
        rank_1_week=?,
        rank_2_week=?,
        total_1_week_pick_count=?,
        total_2_week_pick_count=?,
        total_earning_rate1=?,
        total_earning_rate2=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        sign_pick_event_user_info 
    set
        updated_at=?,
        even_today_remain_pick=?,
        odd_today_remain_pick=?,
        rank_1_week=?,
        rank_2_week=?,
        total_1_week_pick_count=?,
        total_2_week_pick_count=?,
        total_earning_rate1=?,
        total_earning_rate2=?,
        user_id=? 
    where
        id=?
[2023-03-29 06:38:45:17114][http-nio-20002-exec-2] INFO  o.m.j.i.logging.ProtocolLoggingProxy - conn=4734472(M) - 10.795 ms - Query: update sign_pick_event_user_info set updated_at=?, even_today_remain_pick=?, odd_today_remain_pick=?, rank_1_week=?, rank_2_week=?, total_1_week_pick_count=?, total_2_week_pick_count=?, total_earning_rate1=?, total_earning_rate2=?, user_id=? where id=?, parameters ['2023-03-29 06:38:45.5024886',0,0,1,0,10,0,2.7199999999999998,0.0,'69c4c3ea-7b55-49e1-bdd4-a00af437029c',59],['2023-03-29 06:38:45.5144879',0,1,4,0,10,0,1.09,0.0,'c43a0158-f9c8-4a80-897c-7838769cf574',60]
```

- 우아한 형제들 기술블로그에서는 MySQL 드라이버는 배치 업데이트 시 실행할 쿼리가 3개 이하인 경우 한건씩 쿼리가 발생한다고 한다. **하지만 MariaDB는 3건 이하도 batch로 실행한 것을 볼 수 있다.**

### **하나의 트랜잭션에서 동일한 종류의 엔티티를 여러번 SELECT 하며 업데이트**

```java
void updateAll3() throws Exception {
    final int insertSize = 10;
    final int pageSize = 5;
    insertTestValues(INSERT_IDENTITY, identityParameters(insertSize));

    Pageable pageable = PageRequest.of(0, pageSize);
    while (true) {
        final Slice<IdentityEntity> slice = identityEntityRepository.findAllIdentityEntities(pageable); // select
        final List<IdentityEntity> identityEntities = slice.getContent();
        identityEntities.forEach(IdentityEntity::plus); // update

        if (slice.isLast()) {
            break;
        }
        pageable = slice.nextPageable();
    }

    flush();
}
```

```java
[main] MySQL : [QUERY] select identityen0_.id as id1_1_, identityen0_.c1 as c2_1_, identityen0_.c2 as c3_1_, identityen0_.c3 as c4_1_, identityen0_.c4 as c5_1_, identityen0_.c5 as c6_1_, identityen0_.version as version7_1_ from identity identityen0_ order by identityen0_.id limit 6 [Created on: Mon Sep 21 02:32:54 KST 2020, duration: 2, connection-id: 415, statement-id: 0, resultset-id: 0,    at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeQuery(ProxyPreparedStatement.java:52)]
[main] MySQL : [QUERY] update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=1 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=2 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=3 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=4 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=5 and version=0 [Created on: Mon Sep 21 02:32:54 KST 2020, duration: 3, connection-id: 415, statement-id: 0, resultset-id: 0,   at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
[main] MySQL : [QUERY] select identityen0_.id as id1_1_, identityen0_.c1 as c2_1_, identityen0_.c2 as c3_1_, identityen0_.c3 as c4_1_, identityen0_.c4 as c5_1_, identityen0_.c5 as c6_1_, identityen0_.version as version7_1_ from identity identityen0_ order by identityen0_.id limit 5, 6 [Created on: Mon Sep 21 02:32:54 KST 2020, duration: 2, connection-id: 415, statement-id: 0, resultset-id: 0, at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeQuery(ProxyPreparedStatement.java:52)]
[main] MySQL : [QUERY] update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=6 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=7 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=8 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=9 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=10 and version=0 [Created on: Mon Sep 21 02:32:54 KST 2020, duration: 4, connection-id: 415, statement-id: 0, resultset-id: 0,  at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
```

- 예상한 결과는 flush() 를 호출한 시점에 업데이트된 엔티티의 쿼리를 한번에 실행 할 것이라 예상했지만, **실제 결과는 아래와 같이 두번째 select 쿼리 실행전에 update 쿼리가 실행된 것을 확인할 수 있습다. 중요한 힌트이다.**
    - **두번째 select 쿼리 실행전에 update 쿼리가 먼저 실행된 이유는, 하이버네이트는 select 를 하기 전에 select 대상 엔티티에 대해 flush() 를 먼저 호출하기 때문이라고 한다. 중요한 힌트라고 한 이유는 영속성 컨텍스트에서 엔티티를 관리하기 때문에 영속성이 깨지는 것을 방지해줄 수 있다.**

### 그렇다면 하나의 트랜잭션에서 다른 종류의 엔티티를 select를 해도 update 쿼리가 먼저 나갈까?

```java
void updateAllIdentityEntityAndNonIdentityEntity() throws Exception {
    final int size = 4;
    insertTestValues(INSERT_IDENTITY, identityParameters(size));
    insertTestValues(INSERT_NON_IDENTITY, nonIdentityParameters(size));

    final List<IdentityEntity> identityEntities = identityEntityRepository.findAll(); // select
    identityEntities.forEach(IdentityEntity::plus); // update

    final List<NonIdentityEntity> nonIdentityEntities = nonIdentityEntityRepository.findAll(); // select
    nonIdentityEntities.forEach(NonIdentityEntity::plus); // update

    flush();
}
```

```java
[main] MySQL : [QUERY] select identityen0_.id as id1_1_, identityen0_.c1 as c2_1_, identityen0_.c2 as c3_1_, identityen0_.c3 as c4_1_, identityen0_.c4 as c5_1_, identityen0_.c5 as c6_1_, identityen0_.version as version7_1_ from identity identityen0_ [Created on: Mon Sep 21 02:47:55 KST 2020, duration: 2, connection-id: 425, statement-id: 0, resultset-id: 0, at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeQuery(ProxyPreparedStatement.java:52)]
[main] MySQL : [QUERY] select nonidentit0_.id as id1_2_, nonidentit0_.c1 as c2_2_, nonidentit0_.c2 as c3_2_, nonidentit0_.c3 as c4_2_, nonidentit0_.c4 as c5_2_, nonidentit0_.c5 as c6_2_, nonidentit0_.version as version7_2_ from non_identity nonidentit0_ [Created on: Mon Sep 21 02:47:55 KST 2020, duration: 2, connection-id: 425, statement-id: 0, resultset-id: 0, at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeQuery(ProxyPreparedStatement.java:52)]
[main] MySQL : [QUERY] update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=1 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=2 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=3 and version=0;update identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=4 and version=0 [Created on: Mon Sep 21 02:47:55 KST 2020, duration: 2, connection-id: 425, statement-id: 0, resultset-id: 0,    at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
[main] MySQL : [QUERY] update non_identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=1 and version=0;update non_identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=2 and version=0;update non_identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=3 and version=0;update non_identity set c1=2, c2='c2-2', c3='c3-2', c4='c4-2', c5='c5-2', version=1 where id=4 and version=0 [Created on: Mon Sep 21 02:47:55 KST 2020, duration: 2, connection-id: 425, statement-id: 0, resultset-id: 0,    at com.zaxxer.hikari.pool.ProxyStatement.executeBatch(ProxyStatement.java:128)]
```

- 동일한 엔티티를 조회했을 때와는 달리 조회가 끝나고 flush() 를 호출했을 때 update 쿼리가 실행된 것을 확인할 수 있다.

**출처**

https://techblog.woowahan.com/2695/
