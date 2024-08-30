### Skill.

- Java - 17 version
- Spring MVC - 5.3.24 version
- Spring Boot - 2.7.7 version
- build tool - gradle

Spring에서는 일정 시간에 작업을 수행하는 스케줄러 기능을 지원한다. 사용 방법은 아주 간단하다. 아래와 같이 Application Class에 `@EnableScheduling` 어노테이션을 추가해서 사용하면 된다.

```java
@EnableScheduling
@SpringBootApplication
public class SchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}
}

@Slf4j
@Service
class SchedulerService{

	@Scheduled(cron = "0/10 * * * * *")
	public void run(){
		log.info("10초 마다 실행 합니다.");
	}
}
```

실행 결과

```java
2024-02-13 09:12:40.001  INFO 20288 --- [   scheduling-1] com.study.scheduler.SchedulerService     : 10초 마다 실행 합니다.
2024-02-13 09:12:50.005  INFO 20288 --- [   scheduling-1] com.study.scheduler.SchedulerService     : 10초 마다 실행 합니다.
2024-02-13 09:13:00.005  INFO 20288 --- [   scheduling-1] com.study.scheduler.SchedulerService     : 10초 마다 실행 합니다.
2024-02-13 09:13:10.014  INFO 20288 --- [   scheduling-1] com.study.scheduler.SchedulerService     : 10초 마다 실행 합니다.
```

위와 같이 `@Component`와 같이 Bean factory에 등록된 Bean 클래스들어야 한다. 간단한 이야기는 이쯤에서 마무리 하고자 한다. 이제부터 내가 직접 겪어 보았던 이야기를 풀어보고자 한다.

Scheduler는 별 다른 설정이 없다면 기본적으로 하나의 쓰레드를 가지고 스케줄링 작업을 실행한다.

```java
@Slf4j
@Service
class SchedulerService{

	String mainThread = Thread.currentThread().getName();

	@Scheduled(cron = "0/10 * * * * *")
	public void run1(){
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run1에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
	}

	@Scheduled(cron = "0/10 * * * * *")
	public void run2(){
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run2에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
	}

	@Scheduled(cron = "0/10 * * * * *")
	public void run3(){
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run3에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
	}
}
```

실행 결과

```java
2024-02-13 09:34:10.005  INFO 19672 --- [   scheduling-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduling-1 run2에서 10초 마다 실행 합니다.
2024-02-13 09:34:10.007  INFO 19672 --- [   scheduling-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduling-1 run1에서 10초 마다 실행 합니다.
2024-02-13 09:34:10.007  INFO 19672 --- [   scheduling-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduling-1 run3에서 10초 마다 실행 합니다.
2024-02-13 09:34:20.005  INFO 19672 --- [   scheduling-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduling-1 run3에서 10초 마다 실행 합니다.
2024-02-13 09:34:20.006  INFO 19672 --- [   scheduling-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduling-1 run2에서 10초 마다 실행 합니다.
2024-02-13 09:34:20.006  INFO 19672 --- [   scheduling-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduling-1 run1에서 10초 마다 실행 합니다.
```

코드에서 보듯 세개의 task가 같은 스레드[scheduling-1]에서 처리되고 있다. 결국 하나의 task가 끝나야 다음 task를 이어가는 것이다. 스케줄링 작업은 Spring 내부의 TaskScheduler 인터페이스 구현체에 의해 관리된다. **`ThreadPoolTaskScheduler`** 또는 **`ConcurrentTaskScheduler`** 를 상속 받아서 추가 설정이 가능하며, **이러한 TaskScheduler 구현체들은 기본적으로 Thread Pool을 사용하여 스케줄링 작업을 처리한다.**

메서드 밖에서 `Thread.currentThread().getName()` 를 변수로 만들어 본 것도 **주 메인 쓰레드와 스케줄링 작업은 별도로 동작**한다는 것을 확인 해보기 위한 것이다.

여기서 내가 하고 싶은 말은 스케줄링 작업은 **동기**로 작업한다는 것이다. 어떠한 작업이냐에 따라 이러한 **스케줄링 작업을 비동기로 처리할 수도 스케줄링 쓰레드의 개수를 늘리는 방법**도 있다. 하나씩 보자.

먼저, 동기로 작업하는 쓰레드의 개수를 늘려보고 다시 한번 빌드시켜 보자.

```java
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setThreadGroupName("scheduler thread pool");
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
```

- `threadPoolTaskScheduler.setPoolSize` : scheduled 전용 쓰레드 풀사이즈 설정
- `threadPoolTaskScheduler.setThreadGroupName("scheduler thread pool")` : scheduled 전용 쓰레드 그룹 네이밍
- `threadPoolTaskScheduler.setThreadNamePrefix("scheduler-")` : 쓰레드 prefix 이름

실행 결과

```java
2024-02-13 10:13:20.014  INFO 21180 --- [    scheduler-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-2 run2에서 10초 마다 실행 합니다.
2024-02-13 10:13:30.012  INFO 21180 --- [    scheduler-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-1 run1에서 10초 마다 실행 합니다.
2024-02-13 10:13:30.012  INFO 21180 --- [    scheduler-3] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-3 run2에서 10초 마다 실행 합니다.
2024-02-13 10:13:30.012  INFO 21180 --- [    scheduler-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-2 run3에서 10초 마다 실행 합니다.
2024-02-13 10:13:40.011  INFO 21180 --- [    scheduler-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-1 run2에서 10초 마다 실행 합니다.
2024-02-13 10:13:40.011  INFO 21180 --- [    scheduler-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-2 run3에서 10초 마다 실행 합니다.
2024-02-13 10:13:40.011  INFO 21180 --- [    scheduler-3] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: scheduler-3 run1에서 10초 마다 실행 합니다.
```

Application Class에  `@EnableAsync` 를 달아주고, `@Async` 를 각 메서드에 추가 해주자. 이 때, Thread Pool에는 별다른 설정을 하지 않았다.

실행 결과

```java
2024-02-13 10:21:30.032  INFO 15316 --- [         task-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: task-2 run2에서 10초 마다 실행 합니다.
2024-02-13 10:21:30.032  INFO 15316 --- [         task-3] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: task-3 run3에서 10초 마다 실행 합니다.
2024-02-13 10:21:30.032  INFO 15316 --- [         task-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: task-1 run1에서 10초 마다 실행 합니다.
2024-02-13 10:21:40.007  INFO 15316 --- [         task-4] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: task-4 run2에서 10초 마다 실행 합니다.
2024-02-13 10:21:40.007  INFO 15316 --- [         task-5] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: task-5 run3에서 10초 마다 실행 합니다.
2024-02-13 10:21:40.007  INFO 15316 --- [         task-6] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: task-6 run1에서 10초 마다 실행 합니다.
```

위 로그를 보고 의아한 점이 하나 있다. 병렬 작업을 위해 Scheduler Config class에서 scheduler Tread 개수 및 Thread name을 설정했는데 로그에서는 **‘task-’** 로 찍히는 것을 알 수 있다.

`@Async` 어노테이션을 사용하여 비동기로 실시한다. **`@Async`가 달린 Sheduled는 ScheduledConfig로 설정한 쓰레드에서 동작되지 않는다. 그 이유는 java에서 제공되는 쓰레드에서 관리하면 task를 처리한다.**

위에서 언급했던 직접 겪어 보았던 이야기가 바로 `@Scheduled` + `@Async` 두 어노테이션을 혼합함으로써 서버 장애를 경험한 것이다.

### **Executor**

Spring에서 비동기 작업을 수행하면 보통 Application class에 `@EnableAsync` 어노테이션을 추가 하며 비동기로 실행할 메서드에 `@Async` 어노테이션을 달아준다. 이 때, 별다른 설정이 없다면 `CustomizableThreadCreator`클래스를 상속 받은`SimpleAsyncTaskExecutor` 클래스를 사용하게 된다. 위 클래스는 **비동기 작업마다 새로운 쓰레드를 생성하는 쓰레드풀로, 쓰레드 제한 없이 무한으로 처리하여 무서운 장애를 불러온다. 그렇기 때문에 별도로 Async 설정을 해주어야 한다.**

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
	executor.setThreadNamePrefix("비동기Thread-");
        executor.initialize();
        return executor;
    }
}
```

- **`setCorePoolSize()`**: 쓰레드 풀의 기본 사이즈를 정의
- **`setMaxPoolSize()`**: 쓰레드 풀이 가질 수 있는 스레드의 최대 개수를 정의. 만약 쓰레드 풀이 최대 개수에 도달하면, 더 이상 새로운 Task Thread를 만들지 않는다.
- **`setQueueCapacity()`**: 쓰레드 풀의 작업 대기열 크기를 설정 메서드로, 해당 Queue에 size가 꽉 차면 에러 발생.

실행 결과

```java
2024-02-13 10:53:20.008  INFO 11916 --- [    비동기Thread-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-1 run1에서 10초 마다 실행 합니다.
2024-02-13 10:53:20.008  INFO 11916 --- [    비동기Thread-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-2 run2에서 10초 마다 실행 합니다.
2024-02-13 10:53:20.009  INFO 11916 --- [    비동기Thread-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-1 run3에서 10초 마다 실행 합니다.
2024-02-13 10:53:30.013  INFO 11916 --- [    비동기Thread-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-2 run2에서 10초 마다 실행 합니다.
2024-02-13 10:53:30.013  INFO 11916 --- [    비동기Thread-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-1 run1에서 10초 마다 실행 합니다.
2024-02-13 10:53:30.013  INFO 11916 --- [    비동기Thread-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-2 run3에서 10초 마다 실행 합니다
```

그럼 Queue size 도달하도록 코드를 수정해보고 어떤 에러가 반환 되는지 확인해보자.

```java
@EnableScheduling
@SpringBootApplication
public class SchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}
}

@Slf4j
@Service
class SchedulerService{

	String mainThread = Thread.currentThread().getName();

	@Async
	@Scheduled(cron = "0/1 * * * * *")
	public void run1() throws InterruptedException {
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run1에서 1초 마다 실행 합니다.",mainThread,schedulerThreadName);
		Thread.sleep(100000);
	}

//	@Async
//	@Scheduled(cron = "0/10 * * * * *")
//	public void run2(){
//		String schedulerThreadName = Thread.currentThread().getName();
//		log.info("main Thread name: {}, scheduler Thread name: {} run2에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
//	}
//
//	@Async
//	@Scheduled(cron = "0/10 * * * * *")
//	public void run3(){
//		String schedulerThreadName = Thread.currentThread().getName();
//		log.info("main Thread name: {}, scheduler Thread name: {} run3에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
//	}
}
```

```java
2024-02-13 10:55:21.018  INFO 4740 --- [    비동기Thread-1] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-1 run1에서 1초 마다 실행 합니다.
2024-02-13 10:55:22.002  INFO 4740 --- [    비동기Thread-2] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-2 run1에서 1초 마다 실행 합니다.
2024-02-13 10:55:33.007  INFO 4740 --- [    비동기Thread-3] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-3 run1에서 1초 마다 실행 합니다.
2024-02-13 10:55:34.004  INFO 4740 --- [    비동기Thread-4] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-4 run1에서 1초 마다 실행 합니다.
2024-02-13 10:55:35.018  INFO 4740 --- [    비동기Thread-5] com.study.scheduler.SchedulerService     : main Thread name: main, scheduler Thread name: 비동기Thread-5 run1에서 1초 마다 실행 합니다.
Caused by: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@416899ad[Not completed, task = org.springframework.aop.interceptor.AsyncExecutionInterceptor$$Lambda$640/0x000002ab1134c168@5b828449] rejected from java.util.concurrent.ThreadPoolExecutor@780c4fd8[Running, pool size = 5, active threads = 5, queued tasks = 10, completed tasks = 0]
```

### 원인 분석
run1 메서드는 1초마다 실행되도록 설정되어 있지만, Thread.sleep(100000)으로 인해 100초 동안 블록되는 상황을 만들어주었다. 이는 스레드가 작업을 완료하지 못하고 대기하게 만들어 새로운 작업이 쌓게 만들어 `AsyncConfig` 클래스에서 설정한 `executor.setQueueCapacity(10);`
Queue 수용을 꽉 채웠을 때 에러 반환을 확인하는 목적이다. 

파악한 바로는, 1초 마다 쓰레드가 생성되어 21초에 한번 22초에 한번 총 2개의 Thread가 생겼다. 이는 기본 Thread 수 만큼 생겼다. 그 후 `executor.setQueueCapacity(10);` 설정한 Queue만큼 작업이 쌓였다. 그 후 `executor.setMaxPoolSize(5);`로 최대 5개의 Thread가 대기 큐만큼 기다렸다가 Max로 설정한 값만큼 Thread가 생성된다. 수용 가능 대기 Queue 크기는 10개 즉, 10초 있다가 Max로 설정한 Queue만큼 Thread가 증가 된다.
그렇기 때문에 33초에 비동기Thread-3, 비동기Thread-4, 비동기Thread-5가 생기고 최대 Queue 개수까지 도달하고 대기 Queue 사이즈만큼의 공간도 없으면 아래와 같이 에러를 반환하게 된다.
```java
Caused by: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@416899ad[Not completed, task = org.springframework.aop.interceptor.AsyncExecutionInterceptor$$Lambda$640/0x000002ab1134c168@5b828449] rejected from java.util.concurrent.ThreadPoolExecutor@780c4fd8[Running, pool size = 5, active threads = 5, queued tasks = 10, completed tasks = 0]
```

위와 같이 실험 한 이유는 바로 데이터를 수집하면서 직접 마주한 에러이기 때문이다.
처음에는 굉장히 당황스러웠지만, 이 계기로 스케줄러의 비동기 동작 방식을 이해하게 된 좋은 기회였다.

---

### 결론

Spring에서 지원하는 Scheduler는 비동기로도 작업할 수 있고, 동기로써 Scheduler Thread 개수를 늘려 주어 여러 작업을 한번에 처리하는 방법도 있다. 우리가 생각해봐야 할 점은 특정한 Task를 수행해야 할 때, 기능과 요구 사항에 따라 잘 구성해야 한다는 것이다.

- **비동기 처리**: I/O 작업이 많은 경우 비동기 처리를 사용하면 더 효율적이나 I/O 작업을 위해 모든 쓰레드가 대기하는 시간이 점차 길어진다면 Dead Lock 현상으로 서버가 죽는 현상까지 발생할 수 있다.
- **쓰레드 개수 조절**: 쓰레드를 생성하여 작업을 병렬로 처리하는 경우 작업에 유용하나, 쓰레드 개수를 무작정 늘리게 되면 메모리가 점차 늘어나 마찬가지로 서버가 예상치 못한 에러를 경험할 수 있다.

**참고 사항**

https://spring.io/guides/gs/scheduling-tasks

[https://www.blog.ecsimsw.com/entry/Scheduler-적용-배경과-구조-Spring-Scheduler](https://www.blog.ecsimsw.com/entry/Scheduler-%EC%A0%81%EC%9A%A9-%EB%B0%B0%EA%B2%BD%EA%B3%BC-%EA%B5%AC%EC%A1%B0-Spring-Scheduler)

https://dkswnkk.tistory.com/706

**code repository link**

https://github.com/sungwooIsGood/Today-I-Learn/tree/main/code/spring/spring-scheduler
