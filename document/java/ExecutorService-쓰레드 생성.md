java에서 Thread를 구현할 때 Runnable 인터페이스를 구현하거나 Thread 클래스를 상속하여 구현한다. 이렇게 직접적으로 실행 할수도 있지만 `java.util.concurrent` 패키지의 `ExecutorService`를 이용할  수도 있다.

![Untitled (2)](https://github.com/user-attachments/assets/bef865f2-19a0-4f88-92e1-09a68d42d5b9)

`ExecutorService` 는 재사용이 가능한 ThreadPool로 Executor 인터페이스를 확장하여 Thread의 라이프 사이클을 제어한다. `ExecutorService`에 Task(작업)를 지정해주면 가진 ThreadPool을 이용하여 Task를 실행한다. Task는 큐(Queue)로 관리되기 때문에 ThreadPool의 Thread 갯수보다 실행할 Task가 많은 경우 미 실행된 Task는 큐에 저장되어 실행을 마친 Thread가 생길 때까지 기다린다.

![Untitled (3)](https://github.com/user-attachments/assets/43729732-1362-4fe0-b96e-3bdfbe071cc5)

### ExecutorService 생성

ExecutorService 생성은 객체를 통해 생성해주면 된다. 아래와 같은 메서드를 이용하면 쓰레드 풀 개수와 종류를 정할 수 있다.

- **`newFixedThreadPool(int)`** : 인자 개수만큼 고정된 쓰레드풀을 만든다.
- **`newCachedThreadPool()`**: 필요할 때, 필요한 만큼 쓰레드풀을 생성, 이미 생성된 쓰레드를 재활용할 수 있기 때문에 성능상의 이점이 있을 수 있다.
- **`newScheduledThreadPool(int)`**: 일정 시간 뒤에 실행되는 작업이나, 주기적으로 수행되는 작업이 있다면 ScheduledThreadPool을 고려해볼 수 있다.
- **`newSingleThreadExecutor()`**: 쓰레드 1개인 ExecutorService를 리턴합니다. 싱글 쓰레드에서 동작해야 하는 작업을 처리할 때 사용된다.

```java
ExecutorService executor = Executors.newFixedThreadPool(3); // 3개의 쓰레드가 생성된다.
```

위에서 정의한 작업(Task)을 실행하기 위한 메서드들도 함께 제공한다.

- `execute()`: 리턴타입이 void로 Task의 실행결과나 Task의 상태(실행중, 실행완료)를 알 수 없다.
- `submit()`: Task를 할당하고 `Future` 타입의 결과 값을 받는다. 결과가 리턴 되어야 하므로 주로 Callable을 구현한 Task를 인자로 준다. 그리고 `submit()` **메서드는 작업을 큐에 추가**하고, 사용 가능한 쓰레드가 존재할 때 해당 쓰레드가 작업을 가져와 실행한다.

```java
@Test
void threadTest(){
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    executorService.submit(() ->{
        System.out.println(Thread.currentThread().getName());
    });
}
```

![Untitled (4)](https://github.com/user-attachments/assets/8220d1ad-db30-46c1-ad3b-0b226f988f8b)

예를 들어, **`ExecutorService`**에 최대 10개의 스레드를 가진 쓰레드 풀을 사용한다고 가정해보자. 만약 10개의 작업을 **`submit()`**으로 큐에 추가하면, 쓰레드 풀에서는 최대 10개의 작업을 동시에 실행할 수 있는 경우에는 이 작업들이 동시에 실행될 수 있다. 그러나 만약 사용 가능한 쓰레드가 5개만 있다면, 처음 5개 작업이 동시에 실행되고, 나머지 5개 작업은 앞선 작업이 완료될 때까지 대기하게 된다.

결론적으로, **`submit()`** 메서드로 큐에 작업을 추가하더라도 실제로 동시에 실행되는 작업의 수는 쓰레드 풀의 크기와 사용 가능한 쓰레드 자원에 따라 달라진다.

```java
@Test
void threadTest(){
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    executorService.submit(() ->{
        System.out.println(Thread.currentThread().getName());
    });

    System.out.println("1번 submit");

    executorService.submit(() ->{
        System.out.println(Thread.currentThread().getName());
    });

    System.out.println("2번 submit");

    executorService.submit(() ->{
        System.out.println(Thread.currentThread().getName());
    });

    System.out.println("3번 submit");

    executorService.submit(() ->{
        System.out.println(Thread.currentThread().getName());
    });

    System.out.println("4번 submit");
}
```

![Untitled (5)](https://github.com/user-attachments/assets/defaad18-096d-461b-a686-c3876adc1352)

- 쓰레드간 경쟁을 표현한 예시

```java
@Test
    void threadTest() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // 코어 갯수
    System.out.println("local core count: " + Runtime.getRuntime().availableProcessors());
    final List<Future<String>> futures = new ArrayList<>();

    for (int i = 1; i < 5; i++) {
        final int index = i;
        futures.add(executorService.submit(() -> {
            System.out.println("finished job" + index);
            return "job" + index + " " + Thread.currentThread().getName();
        }));
    }

    for (Future<String> future : futures) {
        String result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    executorService.shutdownNow();
    System.out.println("end");
}
```

![Untitled (6)](https://github.com/user-attachments/assets/c4845218-959e-496f-a218-70ce8a9189e6)

작업은 순서대로 처리되지 않을 수 있지만, 로그는 순차적으로 출력 된다.

### ExecutorService 종료

실행 명령한 Task가 모두 수행되어도 `ExecutorService`는 자동으로 종료되지 않는다. 앞으로 들어올 Task를 처리하기 위해 **Thread는 wait 상태로 대기한다.** 그러므로 종료를 위해서는 제공되는 `shutdown()` 이나 `shutdownNow()`  메서드를 사용해야 한다.

- `executorService.shutdown()` : 실행중인 모든 Task가 수행되면 종료한다.
- `executorService.shutDownNow()` **: 실행중인 Thread들을 즉시 종료**시키려고 하지만 모든 Thread가 **동시에 종료되는 것을 보장하지는 않고 실행되지 않은 Task를 반환한다.**
- `executorService.awaitTermination()` : 위에서 설명한 두 개의 shutdown 메서드가 합쳐져서 만들어진 `awaitTermination()` 방법도 있다. 이 메서드는 먼저 새로운 Task가 실행되는 것을 막고, 일정 시간 동안 실행중인 Task가 완료되기를 기다린다. 만일 일정 시간동안 처리되지 않은 Task에 대해서는 강제로 종료 시킨다.

### **Future 인터페이스**

`submit()` 메서드를 호출할 때 반환되는 `Future` 객체는 **Task의 결과값이나 상태를 알 수 있다.** 더 나아가 `Future` 인터페이스는 Blocking method인 `get()`를 제공하는데 Task 실행 결과를 얻을 수 있다. Blocking이기 때문에 실행 중에 `get()`이 호출되는 경우 실행이 끝날 때까지 대기한다. 이는 성능저하를 불러올 수 있으므로 Timeout을 설정하여 일정 시간이 지나면 `TimeoutException`이 발생하도록 유도할 수 있다.

```java
Future<String> future = executorService.submit(callableTask);
String result = null;
try {
    result = future.get(); // Task가 실행중이면 여기서 대기한다.
} catch (InterruptedException || ExecutionException e) {
    e.printStackTrace();
}

// Timeout 설정, 지정된 시간이 지나면 TimeoutException이 발생한다.
String result = future.get(200, TimeUnit.MILLISECONDS);
boolean isDone = future.isDone(); // Task가 실행되었는지?
boolean canceled = future.cancel(true); // Task를 취소
boolean isCancelled = future.isCancelled(); // Task가 취소되었는지?
```

참고자료

https://yangbox.tistory.com/28

https://byul91oh.tistory.com/246
