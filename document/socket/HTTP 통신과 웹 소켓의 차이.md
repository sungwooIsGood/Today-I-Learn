# HTTP

## HTTP 특징

### 무상태성

HTTP만을 사용하면 서버는 클라이언트가 누구인지 식별하지 못한다. 또한, 서버는 이전 요청을 기억하지 못하기 때문에 매 요청마다 필요한 모든 정보를 포함하고 있다.

### 비 연결성

무상태성과 비슷한 맥락으로 서버와 클라이언트 통신할 때 연결을 맺었고 클라이언트의 요청 건에 대해서 서버가 응답을 했다면 연결을 끊게 되어있다. 이렇게 된 이유를 간단하게 추측해보면 클라이언트의 수많은 요청에 대한 connection을 끊지 않고 가지고 있다면 엄청난 서버에 부하가 갔기 때문에 요청/응답 후 연결을 끊는 것으로 설계 되지 않았나 싶다.

대표적으로 위 두가지 특징만을 담았지만 두 가지 특징만들 가지고 온 이유는 바로 **HTTP로 실시간 통신을 구현하기에는 부적합 하다는 것을 말하고 싶었다.**

> 예를 들어, HTTP는 비 연결성이기 때문에 채팅과 같은 서비스를 구축하기 부적합하다. 채팅은 실시간성으로 연결을 계속 맺었다 끊었다 할 수 없다. 결국 맺고 끊음도 서버의 역할이기에 부하가 심할 것이다.

이를 대체하기 위해 약간의 꼼수? 같은 방식들이 있다.

**Polling, Long Polling**

- Polling
  HTTP 요청을 통해 주기적으로 상태를 검사하는 방식이다. A가 B에게 문자를 보냈다고 가정해보면, 서버에서는 이 문자열을 A가 보냈다는것을 인지할 수 있고 이것을 B에게 보내야 한다는 것을 알고 있다. 하지만 B는 나에게 온 문자가 있는지 주기적으로 확인을 해야한다. 다시 생각해보면 HTTP는 기본적으로 요청을 해야 응답을 해주는 구조이다. 그말은 즉, B가 서버에 “나에게 온 메세지가 있어?”라고 계속해서 물어봐야  한다. 이것이 Polling 기법이다.

- Long Polling
  위 Polling 방식을 보완한 것으로 B가 서버에게 처음 요청한 후 B와 서버는 연결을 일정 시간동안 유지하고 있다가 응답이 오면 연결을 종료하는 방식이다.
  이 방식도 HTTP 방식이기 때문에 요청 시 데이터가 방대하다. 또한, 많은 양의 메세지가 쏟아질 경우 Poillng과 같아진다. 그렇기 때문에 서버에 부담이 가는 것도 마찬가지이다.

둘 다 HTTP 헤더가 불필요하게 크다는 단점을 가지고 있다.
>

---

# 웹 소켓

위에서 소개한 HTTP의 특징들에 대한 문제점이 있기 때문에 HTML5에서 웹소켓이 등장했다.

## 웹 소켓의 동작 원리

![Untitled](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/a458aa57-1abd-45dd-9c53-a56bd7294550)

웹 소켓 동작 과정은 크게 세가지로 나눌 수 있다.

**순서대로,Opening Handshake, Data transfer, Closing Handshake**

Opening Handshake 와 Closing Handshake 는 일반적인 HTTP TCP 통신의 과정을 거친다. HTTP 통신을 한 후 웹 소켓 프로토콜로 변경된다. → WS

## **Opening Handshake, Closing Handshake**

### ws://localhost:8080/chat 요청 헤더

```html
GET /chat HTTP/1.1
Host: localhost:8080
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==
Sec-WebSocket-Protocol: chat, superchat
Sec-WebSocket-Version: 13
Origin: http://localhost:9000
```

하나씩 보면

- **GET /chat HTTP/1.1**

웹소켓의 통신 요청에서, HTTP 버전은 1.1 이상이어야 하고 `GET` 메서드를 사용을 해야한다.

- **Host**

서버의 주소를 의미한다.

- **Upgrade**

프로토콜을 전환하기 위해 사용하는 헤더로, 웹소켓 요청시에는 websocket 이라는 값을 가진다. 이 값이 없거나 다른 값이면 `cross-protocol attack` 이라고 간주하여 웹 소켓 접속을 끊는다.

- **Connection**

현재의 전송이 완료된 후 네트워크 접속을 유지할 것인가에 대한 정보로, 웹 소켓 요청 시에는 반드시 Upgrade 라는 값을 가지게 된다. 또한 위에서 Upgrade값과 마친가지로 다른 값이 있거나 비어있으면 연결을 끊는다.

- **Sec-WebSocket-Key**

유효한 요청인지 확인하기 위해 사용하는 키 값, 요청과 응답 둘다 반환되며 유효한 값이 있어야지만 소켓이 연결된다. (인증)

- **Sec-WebSocket-Protocol**

사용하고자 하는 하나 이상의 웹 소켓 프로토콜 지정하는 것으로 필요한 경우에만 사용하게 된다.

- **Sec-WebSocket-Version**

클라이언트가 사용하고자 하는 웹소켓 프로토콜 버전 정보를 담고 있다.

- **Origin**

CORS 정책으로 만들어진 헤더이다. 기본적으로 SOP 정책을 따르며, 클라이언트의 주소를 가르킨다.

### 응답 헤더

```html
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=
Sec-WebSocket-Protocol: chat
```

- **HTTP/1.1 101 Switching Protocols**

101은 HTTP에서 WS로 프로토콜 전환이 승인 되었다는 응답코드이다.

- **Sec-WebSocket-Accept**

요청 헤더의 Sec-WebSocket-Key에 유니크 아이디를 더해서 SHA-1로 해싱한 후 base64로 인코딩한 결과로 웹 소켓 연결이 되었다는 것을 알려준다.

## 2. Data Transfer

Opening HandShake를 통해 연결이 되었다면, 해당 영역이 진행되게 된다. 해당 영역은 데이터를 **메시지**라는 단위의 데이터로 통신한다.

- **메시지란 여러 프레임(frame)이 모여서 구성되는 하나의 논리적인 메시지 단위**
    - 프레임이란 통신에서 가장 작은 단위의 데이터 → (HTTP 상에서 전 네트워크 통신 과정에서 가장 작은 단위의 데이터를 뜻이 ‘**패킷’**)
      하지만 여기서 프레임이라고 부르는 이유는 데이터 링크 계층(이더넷)에서 주고 받는 가장 작은 단위를 의미하기 때문에 용어가 틀리다. 작은 헤더 + payload로 구성되어 있다.

---

### 웹 소켓의 한계와 STOMP의 등장

1. 웹 소켓은 문자열들을 주고 받을 수 있을 뿐 그 이상의 동작은 하지 않는다. 주고 받는 문자열의 해독은 온전히 어플리케이션에 맡긴다.
2. HTTP는 형식이 있어 모두가 약속을 따르기만 하면 해석할 수 있지만 웹소켓은 형식이 없다. 때문에 어플리케이션에서 해석하기 힘들 수가 있다.
    1. 그렇기 때문에 서브 프로토콜을 통해 주고 받는 메시지의 규격을 정하여 통신한다 → STMOP가 때문에 등장하게 된다.

- STOMP
    - 채팅 통신을 하기 위한 형식을 정의
    - HTTP와 유사하게 간단히 정의되어 해석하기 편한 프로토콜이다. 프레임 기반의 프로토콜이다.
    - 일반적으로 웹소켓 위에서 사용된다.
    - 프레임 구조
        - COMMAND
        - header
            - 헤더와 바디는 빈 라인으로 구분하여, 바디의 끝은 Null 문자로 설정한다.
        - Body

간략하게 STOMP에 대해 알아본 것이고 다음 장에서 STOMP에 대해 공부해보고자 한다.