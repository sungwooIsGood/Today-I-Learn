앞선 장에서는 WebSocket의 특징을 알아봤다면 이번 장에서는 서브 프로토콜인 STOMP를 알아보고자 한다. STOMP(Simple Text Oriented Message Protocol)는 기존 WebSocket 통신 방식을 **좀 더 효율적으로, 조금 더 쉽게 다룰 수 있게 해주는 프로토콜이다.**

해당 프로토콜은 Pub/Sub 패턴을 사용한다.

<aside>
💡 **Pub/Sub 패턴

Pub/Sub (Publish/Subscribe)는 메시지 브로커 패턴 중 하나로, 메시지 송신자(publisher)와 수신자(subscriber)를 분리하여 효율적인 메시지 전달 하는 패턴으로,** 메시지 브로커란 메시지(데이터) ****기반 통신에서 중개 역할을 하는 중간 소프트웨어 또는 서비스이다.

**Publish (pub)**: 메시지를 발행하는 역할, 메시지 송신자가 특정 주제(topic) 또는 채널로 **메시지를 송신**

**Subscribe (sub)**: 메시지를 구독하는 역할, 메시지 수신자는 특정 주제(topic) 또는 채널을 구독(subscribe)하여 해당 주제의 **메시지를 수신**

</aside>

---

## Spring STOMP

위에서 STOMP는 메시지 브로커 역할을 하는 서브 프로토콜이라고 소개했다. 그 말은 즉, Spring 뿐만 아니라 외부 메시지 브로커들을 사용할 수 있다는 것이다. (예: RabbitMQ, ActiveMQ 등)

- **Spring 내장 브로커**

Spring은 내장된 심플 브로커를 제공하는데 이 브로커는 메모리 내에서 작동한다. 그렇기 때문에 메모리 내에서 작동하기 때문에 성능이 제한적이며, 확장성이 제한된다는 단점이 있지만 구현하기 가장 간단한 방법이기도 하다.

- **외부 메시지 브로커 (RabbitMQ 등)**

더 높은 성능과 확장성을 제공하며, 복잡한 메시징 패턴을 지원한다. 또,외부 브로커의 관리 툴 및 모니터링 시스템을 사용하여 관리가 가능하다. 그렇기 때문에 설정이 복잡하여 대규모 애플리케이션에 적합하다.

spring에서 외부 메시지 브로커 연결(Rabbit MQ) 예시

1. **RabbitMQ 설치했다는 가정 - 포트 번호는 61613**
2. Spring boot에서 의존성을 주입

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

1. WebSocket 설정

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic", "/queue")
              .setRelayHost("localhost")
              .setRelayPort(61613)
              .setClientLogin("guest")
              .setClientPasscode("guest");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
```

### **Spring STOMP 구조**

![img1 daumcdn](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/7487ea7e-ecc6-4c95-9e02-b9c78fc55f51)


- STOMP의 형식은 HTTP와 닮아있다.

```bash
COMMAND
header1:value1
header2:value2

Body^@
```

클라이언트는 메시지를 전송하기 위해 COMMAND로 SEND 또는 SUBSCRIBE 명령을 사용하며, header와 value로 메시지의 수신 대상과 메시지에 대한 정보를 설명할 수 있다.

- SUBSCRIBE를 통해 구독하고 있는 정보를 메모리에 유지시킬 수 있다.

```bash
SUBSCRIBE
destination:/subscribe/chat
```

- 다음과 같이 어떤 유저가 메시지를 보내면, **메시지 브로커는 SUBSCRIBE 중인 다른 유저들에게 메시지를 전달한다.**

```bash
SEND
destination:/queue/a
content-type:text/plain

hello queue a
^@
```

1. **클라이언트에서 destination에 /app 이라는 prefix를 주어졌을 때**

클라이언트에서 destination에 /app 이라는 prefix를 주었을 때 클라이언트가 보낸 Request 값은 **@messagemapping된 스프링 컨트롤러로 흘러가고 컨트롤러**에서 메세지를 수신한 후 여러 작업들을 처리한다.

그 후 /topic이라는 prefix를 통해 브로커에게 전달하면 브로커는  STOMP MESSAGE 메소드를 이용해서 특정 topic을 구독하는 subscriber들에게 reponse를 보낸다.

1. **클라이언트에서 destination에 /topic이라는 prefix를 주어졌을 때**

스프링 컨트롤러를 거치지 않고 브로커에게 직접 전달되어 브로커가 메세지를 직접 받아 subscriber들에게 값을 전달한다.

---

- Server는 아래와 같은 규격으로 값을 subscriber들에게 보낸다. → 더 많은 규격들이 많지만 일단 하나로 예시를 든 것

```bash
MESSAGE
subscription:0
message-id:007
destination:/queue/a
content-type:text/plain

hello queue a^@
```

MESSAGE 프레임에는 메시지가 전송된 목적지를 나타내는 **destination 헤더가 반드시 포함되어야** **한다.** 메시지가 STOMP를 사용하여 전송된 경우, 이 destination 헤더는 해당 SEND 프레임에서 사용된 것과 동일해야 한다.

또한, MESSAGE 프레임에는 또한 해당 메시지에 대한 고유 식별자(unique identifier)를 가진 message-id 헤더와 메시지를 수신하는 구독의 식별자와 일치하는 subscription 헤더가 반드시 포함되어야 한다.

---

**Reference**

https://tecoble.techcourse.co.kr/post/2021-09-05-web-socket-practice/

https://github.com/spring-guides/gs-messaging-stomp-websocket

https://docs.spring.io/spring-framework/reference/web/websocket/stomp.html

https://stomp.github.io/stomp-specification-1.2.html
