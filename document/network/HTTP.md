## HTTP(HyperText Transfer Protocol)

- 텍스트 기반의 통신 규약으로 **인터넷에서 데이터를 주고받을 수 있는 프로토콜**
- HyperText - 문서간의 링크로써의 의미로 시작했지만
- 현재 모든 것이 HTTP이다.
    - HTML, TEXT
    - IMAGE, 음성, 영상, 파일
    - JSON, XML(API)
    - 거의 모든 형태의 데이터 전송가능
    - 서버간에 데이터를 주고 받을 때도 대부분 HTTP
    - 즉, **모든 것을 HTTP 메시지로 전송한다.**

<aside>
💡 **프로토콜(Protocol)이란?**

- 통신 규약 및 약속으로 컴퓨터나 원거리 통신 장비 사이에서 메시지를 주고 받는 양식과 규칙의 체계**

</aside>

### **HTTP 역사**

- HTTP/0.9 1991년: **GET 메서드만 지원**, HTTP 헤더는 없음
- HTTP/1.0 1996년: 메서드, 헤더 추가
- **HTTP/1.1 1997년: 가장 많이 사용, 우리에게 가장 중요한 버전**

  REC2068 (1997) → REC2616 (1999) → REC7230~7235 (2014)

- HTTP/2 2015년: 성능 개선
- HTTP/3 진행중: TCP 대신에 UDP 사용, 성능 개선
- HTTP/2,3은 주로 성능 개선에 초점

### 기반 프로토콜

- TCP : HTTP/1.1, HTTP/2
- UDP : HTTP/3
    - TCP는 3wayHandshake 해놔야 하고, 가지고 있는 데이터도 많음, 기본적인 메커니즘 자체가 속도가 빠르지는 않음. UDP프로토콜 위에 애플리케이션 계층에서 성능 최적화 설계
- 현재 HTTP/1.1 주로 사용
- HTTP/2, HTTP/3도 점점 증가

### HTTP 특징

- 클라이언트 - 서버 구조
- 무상태 프로토콜(스테이스리스),비연결성
- **HTTP메시지 통신**
- 단순함, 확장 가능

---

## 클라이언트 서버 구조

### 서버 구조

- Request(요청), Response(응답) 구조
- 클라이언트는 서버에 요청을 보내고, 응답을 대기
- 서버가 요청에 대한 결과를 만들어서 응답
    - **클라이언트와 서버가 분리**
    - * **비즈니스 로직, 데이터는 서버 집중, UI-사용성 클라이언트 집중**
    - 클라이언트-서버 독립적 진화가 가능

![Untitled](https://github.com/user-attachments/assets/c2ea2881-820f-41d7-b386-9db8bbf1982f)

---

## Stateful, Stateless

### 무상태 프로토콜 (Stateless)

- 서버가 클라이언트의 상태를 보존x
- 장점 : 서버 확장성 높음(스케일 아웃)
- 단점 : 클라이언트가 추가 데이터를 전송해야 한다.
- 무상태는 응답 서버를 쉽게 바꿀 수 있다. →**무한한 서버 증설 가능**
- 갑자기 클라이언트 요청이 증가해도 서버를 대거 투입할 수 있다.

### 상태 유지 - Stateful

- 항상 같은 서버가 유지 되어야 한다.
- 클라이언트는 계속 서버1과 통신해야한다.

![Untitled (1)](https://github.com/user-attachments/assets/aed25bb3-8656-4444-93b6-17d2954578bc)

- 중간에 서버가 장애가 나면 처음부터 다시 해야한다.

![Untitled (2)](https://github.com/user-attachments/assets/f9ef4758-eedb-478a-8625-e4780748c0b6)

### 무상태 - Stateless

- 클라이언트가 처음부터 필요한 데이터를 다 담아서 요청한다.
- 정보를 보관하지 않아도(상태를 보관하지 않아도) 서버는 요청에 대한 응답을 해줄 수 있다.

![Untitled (3)](https://github.com/user-attachments/assets/0f84a1b9-b76f-4257-98d6-85fd4dc995de)

- 중간에 장애가 되어도 중계서버가 다른 서버로 연결시켜준다.

![Untitled (4)](https://github.com/user-attachments/assets/a74207c0-543d-43a5-a5fa-7854ec2a6860)

### 무상태 - Stateless의 스케일 아웃(수평 확장 유리)

- ex) 이벤트 페이지 등 큰 이벤트가 있을 경우 백엔드에서 똑같은 기능을 하는 서버를 많이 늘릴 수 있어 트래픽 대처가 가능

![Untitled (5)](https://github.com/user-attachments/assets/c5c44454-bfc3-44f6-8190-2592278dab2b)

### Stateless - 실무 한계

- 모든 것을 무상태로 설계 할 수 있는 경우도 있고 없는 경우도 있다.
- 무상태
    - 예) 로그인이 필요 없는 단순한 서비스 소개 화면
- 상태 유지
    - 예) 로그인
- 로그인한 사용자의 경우 **로그인 했다는 상태를 서버에 유지**
- 일반적으로 브라우저 **쿠키와 서버 세션** 등을 사용해서 상태 유지
- **상태 유지**는 **최소한**만 사용
- 클라이언트 쪽에서 데이터를 너무 많이 보낸다.

---

## 비 연결성(connectionless)

![Untitled (6)](https://github.com/user-attachments/assets/b85ed239-dae9-4ac8-86a6-2eb990e51236)

- 단점: 클라이언트 2와 3이 서버를 이용하지 않아도 연결을 유지해야 한다. 이는 연결을 유지하는 서버의 자원은 계속해서 소모되고 있다.

![Untitled (7)](https://github.com/user-attachments/assets/3dd4b448-d4f0-4d2a-97f4-c65736cced43)

- 클라이언트가 필요할 때 서버와 연결 시켜주며, 사용하지 않을 때는 연결을 끊어버린다. 요청을 주고 받을 때만 연결 시키는 것, 서버의 자원을 최소한으로 소모한다.

### 비 연결성

- HTTP는 기본이 **연결을 유지하지 않는 모델**
- 일반적으로 초 단위의 이하의 빠른 속도로 응답
- 1시간 동안 수천명이 서비스를 사용해도 실제 서버에서 동시에 처리하는 요청은 수십개 이하로 매우 작음
    - 예) 웹 브라우저에서 계속 연결해서 검색 버튼을 누르지 않는다.
- 서버 자원을 매우 효율적으로 사용할 수 있음

### 비 연결성 단점- 한계와 극복



- TCP/IP 연결을 새로 맺어야 함 - 3 way handshake 시간 추가
    - 예) 다른 웹페이지를 이용해야 한다면 다시 연결해야 한다.
- 웹 브라우저로 사이트를 요청하면 HTML 뿐만 아니라 자바스크립트, css, 추가 이미지 등 수 많은 자원이 함께 다운로드
    - 예)

  ![Untitled (8)](https://github.com/user-attachments/assets/6050bdf8-5ff6-4d88-8fcf-9ff16a71bbc3)

- 지금은 HTTP 지속 연결(Persistent Connections)로 문제 해결
- HTTP/2, HTTP/3에서 더 많은 최적화

### HTTP 초기 - 연결, 종료 낭비

![Untitled (9)](https://github.com/user-attachments/assets/87b88ff3-cf9a-4e82-b4e8-6edbe2bf51ca)

- 새로운 요청을 할 때마다 연결을 해준다.

### HTTP 지속 연결(Persistent Connections)

![Untitled (10)](https://github.com/user-attachments/assets/84bb6ca8-ee91-478c-8cea-9b1c349e88ec)

- 모든 요청/응답이 끝날 때 까지 연결을 유지시킨다.

### * 스테이스리스(무상태)를 기억하자.

- 서버 개발자들이 어려워 하는 업무

- 같은 시간에 맞추어 발생하는 대용량 트래픽
    - 예) 선착순, 명절 KTX 예약, 학과 수업 등록

---

## HTTP 메시지

- HTTP 메시지는 요청과 응답에 따라 구조가 다르다.

### HTTP 메시지 구조

![Untitled (11)](https://github.com/user-attachments/assets/88c8acd5-5061-43dc-8df5-e379aa44822d)

### HTTP 요청 메시지

- ex) [https://www.google.com:443/search?q=hello&hl=ko](https://www.google.com/search?q=hello&hl=ko)
- start-line : [/search?q=hello&hl=ko](https://www.google.com/search?q=hello&hl=ko)
- header : [www.google.com](https://www.google.com/search?q=hello&hl=ko)

![Untitled (12)](https://github.com/user-attachments/assets/d41bc908-e7bd-488e-a1d7-a839a2a131fa)

### HTTP 응답 메시지

![Untitled (13)](https://github.com/user-attachments/assets/49fb7d5b-f85b-4496-9014-85f3679a12a9)

- message body : 보통 HTML이나 자료같은 것들이 날라온다.

### 시작 라인 - 요청 메시지

- start-line = **request-line**(요청메시지) / status-line

![Untitled (14)](https://github.com/user-attachments/assets/f1f355d8-fa14-4a8f-a602-94b61342f0a3)

- **request-line** = (get, post 등 HTTP메서드)method SP(공백) request-target(path) SP HTTP-version CRLF(엔터)

- HTTP 메서드
    - 종류 : GET(조회), POST(요청 내역 처리), PUT, DELETE
- 요청 대상(/search?q=hello&hl=ko) **절대경로로 처리**
    - absolute-path[?query](절대경로[?쿼리])
    - 절대경로= “/”로 시작하는 경로
- HTTP Version

### 시작 라인 - 응답 메시지

- start-line = request-line / **status-line**(응답메시지)
- **status-line** = HTTP-version SP status-code SP reason-phrase CRLF

![Untitled (15)](https://github.com/user-attachments/assets/cea94fd3-9a53-4cb4-af78-63d131dc95b7)

- HTTP version
- HTTP 상태 코드 : 요청 성공, 실패를 나타냄
    - 200~ : 성공
    - 400~ : 클라이언트 요청 오류
    - 500~ : 서버 내부 오류
- 이유 문구 : 사람이 이해할 수 있는 짧은 상태 코드 설명 글

### HTTP 헤더

- header-field = field-name “:” OWS field-value OWS (OWS: 띄어쓰기 허용한다는 뜻)
- field-name은 대소문자 구문 없음

![Untitled (16)](https://github.com/user-attachments/assets/afd9ac82-a639-43d7-9123-e888c3ddf78e)

### HTTP 헤더 용도

- HTTP 전송에 필요한 모든 부가정보
    - 예) 메시지 바디의 내용, 메시지 바디의 크기, 압축, 인증, 요청 클라이언트(브라우저) 정보, 서버 애플리케이션 정보, 캐시 관리 정보...

  ![Untitled (17)](https://github.com/user-attachments/assets/c63253ba-2339-439d-bc4c-af7ab2fb1486)

- 메시지 바디 빼고 필요한 메타데이터 정보다 다 들어가 있다.
- 필요시 임의의 헤더 추가 가능
    - 예) helloworld: hihi


### HTTP 메시지 바디 - 용도

- 실제 전송할 데이터
- HTML 문서, 이미지, 영상, JSON 등 byte로 표현할 수 있는 모든 데이터 전송 가능

### 정리하자면

- HTTP는 단순하다.
- HTTP 메시지도 매우 단순
