## MGET

레디스에서는 `MGET`이라는 키워드를 제공한다. `MGET` 은 **여러 개의 데이터를 한번에 조회할 때 사용하는 명령어이다.**

- **Redis commands**

```
MGET key [key ...]
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/3f2a9cc1-7f3f-49c7-9551-323392b92f76/Untitled.png)

- **Java**
    - java에서는 `multiGet()` 메서드를 이용하면 된다.

```java
private final RedisTemplate<String, String> redisTemplate; // 의존성 주입

// MGET key [key ...] -> return List<String>
public void mget(){
      List<String> keys = Arrays.asList("key1", "key2", "key3");

      ValueOperations<String, String> stringObjectValueOperations = redisTemplate.opsForValue();
      stringObjectValueOperations.set("key1", keys.get(0));
      stringObjectValueOperations.set("key2", keys.get(1));
      stringObjectValueOperations.set("key3", keys.get(2));

      List<String> mget = stringObjectValueOperations.multiGet(keys);
      mget.forEach(System.out::println);
}
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/9b38446f-e7d2-442f-b570-9d544934c6f9/Untitled.png)

사용법은 어디서든 찾아볼 수 있지만 위에 있는 `MGET` 명령어가 과연 효과가 있는지, 성능이 있는지, 동작 방식이 어떻게 되는지 좀 더 살펴보고 싶다. 그래야 우린 실제로 사용할지 말지를 결정하니까..

### GET vs MGET 성능 비교

**결론적으로 말하면 `Get` 키워드를 사용하면 비용이 많이 들 수 있다. (특히 클라우드의 네트워크 I/O)** `GET` 키워드는 Redis에서 가장 간단하게 Key에 대한 value를 가져올 수 있다.

하지만 Redis는 단일 물리적인 서버라는 것을 명심하자. 간단하게 가져올 수 있지만 한번 호출할 때마다 네트워크를 요청/응답을 받아온다.

수 많은 key 값을 찾기 위해 계속 `GET` 명령어를 사용한다면 네트워크 I/O가 올라 갈 수 밖에 없다. 반대로 `MGET` 대량의 데이터를 한 번에 요청하기 때문에 네트워크 오버헤드가 감소하여 성능상 이점을 얻을 수 있다.

```java
private final RedisTemplate<String, Integer> redisTemplate; // 의존성 주입

public void test(){

    ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
    List<String> keys = new ArrayList<>();
    
    set(operations,keys);
    get(operations);
    mget(operations,keys);
}

private void set(ValueOperations<String, Object> operations,List<String> keys) {

    for(int i = 0; i < 100000; i++){
        operations.set("key"+i,i);
        keys.add("key"+i);
    }
}

private void mget(ValueOperations<String, Object> operations,List<String> keys) {

    long beforeTime = System.currentTimeMillis();

    operations.multiGet(keys);

    long afterTime = System.currentTimeMillis();
    long secDiffTime = (afterTime - beforeTime);
    System.out.println("MGET 걸린 시간(ms) : "+secDiffTime);
}

private void get(ValueOperations<String, Object> operations){
    long beforeTime = System.currentTimeMillis();

    for(int i = 0; i < 100000; i++){
        operations.get("key"+i);
    }

    long afterTime = System.currentTimeMillis();
    long secDiffTime = (afterTime - beforeTime);
    System.out.println("GET 걸린 시간(ms) : "+secDiffTime);
}
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/a90b220c-ea75-468e-a12c-99a4b4b51947/Untitled.png)

월등한 성능차이를 볼 수있었다. 이처럼 여러개의 `key`를 찾을 때 `GET`보다는 `MGET`을 사용해보는 어떨까 하는 마음이고 실제로 써볼 상황이 오면 좋겠다.

---

**출처**

https://medium.com/@jychen7/redis-get-pipeline-vs-mget-6e41aeaecef

https://redis.io/docs/manual/pipelining/

[https://velog.io/@jsb100800/redis-pipelining](https://tjdrnr05571.tistory.com/7)

https://docs.spring.io/spring-data/redis/reference/redis/pipelining.html#page-title