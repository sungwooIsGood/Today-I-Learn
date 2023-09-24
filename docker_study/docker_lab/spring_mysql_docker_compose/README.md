# Docker compose spring & mysql

이번 시간에는 docker compose를 이용해 spring과 mysql를 연결하여 실행시켜 볼 것이다.

아래 깃허브 주소에서 클론을 받아오자.

[GitHub - codingspecialist/docker-test-server](https://github.com/codingspecialist/docker-test-server.git)

### 로컬환경

- spring_mysql_docker_compose
    - docker-test-server-main
    - docker-test-db

---

### docker-test-server-main

spring boot 서버이다. 하나씩 차근차근 알아보자.

- java/com/example/server/user
    - User
        - User 테이블과 매핑시켜 논 엔티티이다.
    - UserController
        - 클라이언트의 요청을 받는 곳이다.
    - UserRepository
        - User 테이블에 있는 데이터를 받아 올 수 있는 레파지토리이다.
- resource
    - application.yml
        - application-dev.yml를 연결하기 위한 yml
    - application-dev.yml
        - 로컬에서 돌릴 때 필요한 yml이다.
    - application-prod.yml (prod 운영환경)
        - docker compose를 통해 docker container에서 띄우기 위한 yml이다.

yml부터 하나씩 보자.

- application.yml

```jsx
spring:
  profiles:
    active:
      - dev
```

spring boot 애플리케이션 설정하기 위한 yml로 spring.profiles.active 속성을 활용하면 application-{prodifle}.yml 형식으로 사용할 수 있다. 고로 위에 의미는 **application-dev.yml을 사용하겠다는 의미다.** 곧 개발, 테스트 환경에서 사용하겠다는 것으로 알 수 있다.

- application-dev.yml

```jsx
server:
  servlet:
    encoding:
      charset: utf-8
      force: true
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://db:3306/metadb?useSSL=false&serverTimezone=UTC&useLegacyDatetime
    username: root
    password: root1234
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: none
    show-sql: true
    open-in-view: true

logging:
  level:
    "[com.example.server]": INFO
    "[org.hibernate.type]": TRACE
```

흔한 yml 을 그냥 작성해본 것이다.

- application-prod.yml

```jsx
server:
  servlet:
    encoding:
      charset: utf-8
      force: true
  port: 8080
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: ${SPRING_DATASOURCE_DRIVER}
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: none
    show-sql: true
    open-in-view: true

logging:
  level:
    '[com.example.server]': INFO
    '[org.hibernate.type]': TRACE
```

dev.yml과 다른것이 하나 있다. 바로 spring.datasource 블럭이다. 이 부분은 docker에서 환경변수를 설정하고 사용하는 것이다. 자세한건 뒤에서 다룰 것이다.

---

### 실습

docker-test-server-main 모듈 바로 아래 디렉토리에 Dockerfile을 만들어서 작성해보자.

- **Dockerfile**

```docker
FROM openjdk:11-jdk-slim

WORKDIR /app

# COPY만!! docker-compose 파일의 위치를 기반으로 작동함
COPY . .

# RUN은 현재 파일 위치(Dockerfile이 있는 디렉토리)를 기반으로 작동함
# 그냥 우린 쉽게 docker-compse위치와 Dockerfile 위치를 같은 디렉토리에 넣어두자.
RUN chmod +x ./gradlew
RUN ./gradlew clean build

ENV JAR_PATH=/app/build/libs
RUN mv ${JAR_PATH}/*.jar ${JAR_PATH}/app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

**`FROM openjdk:11-jdk-slim`**: `FROM`을 통해 이미지를 지정하는 곳이다.여기서는 OpenJDK 11의 "jdk-slim" 이미지를 사용하여 Java 환경을 설정한다는 뜻이다.

**`WORKDIR /app`**: 현재 작업 디렉토리를 **`/app`**로 설정한다는 의미이다. 이 `/app` 에서 이후 명령들이 실행된다.

**`COPY . .`**: 현재 디렉토리의 모든 파일과 폴더를 컨테이너 내부의 **`/app`** 디렉토리로 복사한다. → 이미지 빌드 시점

**`RUN chmod +x ./gradlew`**:  `RUN` 명령어는 쉘 명령을 실행하기 위한 명령어다. Docker 이미지를 빌드하는 동안 컨테이너 내부에서 이 명령어를 실행한다.
`chmod +x` 는 파일 권한을 변경하는 `chmod` 명령어를 사용하고, `+x` 는 ‘실행 권한 추가’를 나차낸다.
즉,  **`./gradlew`** 스크립트 파일에 실행 권한을 부여하는 것이다. 그래서 이 스크립트는 Gradle 빌드 도구를 실행하기 위한 스크립트이다.

**`RUN ./gradlew clean build`**: Gradle을 사용하여 애플리케이션을 빌드한다는 의미이다.

`**./gradlew**` 는 Gradle 빌드 도구를 실행하는 스크립트 파일인 ‘gradlew’을 가르킨다. 그렇기 때문에 gradle 빌드를 수행하는 역할이다.

**`clean`** 은 이전 빌드에서 생성된 빌드 아티팩트를 정리하고 이전 빌드에서 생성된 모든 파일을 삭제한다. 이렇게 함으로써 이전 빌드의 잔여 파일이 남아 있지 않아 현재 빌드에 영향을 미치지 않는다.

`**build**` 는 해당 프로젝트를 빌드하고 JAR 파일을 패키징하는 역할을 한다. 즉, 애플리케이션 코드를 컴파일하고 필요한 종속송을 다운로드 하여 빌드된 결과물을 생성한다.

> `RUN chmod +x ./gradlew
RUN ./gradlew clean build`
즉, 쉘 명령을 통해 gradlew라는 스크립트 파일을 실행할 수 있는 권한이 주어지고, gradlew라는 스크립트 파일에서 이전 빌드 파일을 삭제하면서 새롭게 빌드한다는 의미이다.
>

**`ENV JAR_PATH=/app/build/libs`**: 환경 변수 **`JAR_PATH`**를 설정하여 어플리케이션 JAR 파일의 경로를 지정한다. → 컨테이너 실행 시점

**`ARG JAR_FILE=build/lib/*.jar`**: jar**파일은**  build/libs/*.jar에 빌드된다. 빌드 된 jar파일의 경로를 지정하는 인자이다. `**ARG**`를 통해 변수를 생성한다. → **`ARG JAR_FILE=build/lib/*.jar`** 사용하지 않은 이유는 ****컨테이너가 실행 될 때 사용되지 않기 때문에 사용하지 않았다.

즉,  **`ENV`**는 컨테이너 실행 중에 사용 되는 환경 변수를 정의하고 설정하는 데 사용되며, **`ARG`**는 이미지 빌드 중에만 사용되는 빌드 타임 환경 변수를 정의하는 데 사용된다.

> 여담으로 원래는 spring boot안에서 jar 파일이 두 개 생긴다. 하지만 build.gradle에서
>
>
> `jar {enabled = false}`
> 스크립트를 통해 jar 파일을 build/lib/*.jar 파일로 하나만 생긴다.
>

**`RUN mv ${JAR_PATH}/*.jar ${JAR_PATH}/app.jar`**: 빌드 된 JAR 파일을 **`/app/build/libs`** 디렉터리에서 **`/app/build/libs/app.jar`**로 이동시킨다. 이후에 어플리케이션을 실행할 때 **`/app/build/libs/app.jar`**를 실행한다. **`mv`** 명령어를 사용하면 파일을 다른 위치로 이동하거나 파일 이름을 변경할 수 있다.

**`COPY ${JAR_FILE} app.jar`**: 이전 단계에서 정의한 **`JAR_FILE`** 경로에 있는 JAR 파일을 **`/app`** 디렉토리안에서 app.jar로 파일을 복사한다.

⇒ **`COPY build/lib/*jar app.jar`** 와 같은 의미이다.

`ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]`: 컨테이너가 시작될 때 실행될 명령을 정의하는 구간이다. 여기서는 Java로 JAR 파일을 실행하는 명령을 지정하는 것이다.

`-Dspring.profiles.active=prod` Java 가상 머신의 시스템 프로퍼티를 설정하는 옵션이다. "-D" 옵션 다음에 나오는 문자열은 Java 시스템 프로퍼티를 설정하는데 사용된다. 이 경우 "spring.profiles.active"라는 시스템 프로퍼티를 "prod" 값으로 설정하고 있다.

docker-test-db 모듈 바로 아래 디렉토리에 Dockerfile을 만들어서 작성해보자. 아, 그전에 init.sql을 만들어두자.

- init.sql

```bash
CREATE TABLE user_tb(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb6_unicode_ci;

INSERT INTO user_tb(name)
VALUES('John');

INSERT INTO user_tb(name)
VALUES('ian');
```

- Dockerfile

```docker
FROM mysql:8.0

COPY init.sql /docker-entrypoint-initdb.d

ENV NYSQL_ROOT_PASSWORD=root1234
ENV MYSQL_DATABASE=metadb
ENV MYSQL_HOST=%

CMD ["--character-set-server=utf8mb4","--collation-server=utf8mb4_unicode_ci"]
```

**`COPY init.sql /docker-entrypoint-initdb.d`**: `init.sql`이라는 SQL 스크립트 파일을 현재 디렉터리에서 컨테이너 내부의 **`/docker-entrypoint-initdb.d`** 디렉터리로 복사한다. 이렇게 하면 컨테이너가 시작될 때 MySQL 초기화 스크립트가 실행되게 된다.

**`/docker-entrypoint-initdb.d`** 는 컨테이너가 켜질 때 초기화 sql문을 실행하는 부분이다.

**`ENV MYSQL_HOST=%`**: 환경 변수 `MYSQL_HOST`를 `%`로 설정하면서, 모든 호스트로부터의 연결을 허용함을 의미이다.

**`CMD ["--character-set-server=utf8mb4","--collation-server=utf8mb4_unicode_ci"]`**: 이 부분은 컨테이너가 시작될 때 실행할 기본 명령어를 정의한다 **`-character-set-server`** 및 **`-collation-server`** 옵션은 MySQL의 문자 집합과 정렬 설정을 변경하기 위해사 용했다.

spring server의 Dockerfile과 MySQL의 Dockerfile이 완성이 되었을 것이다. 이 두개의 이미지를 docker-compose를 이용해 동시에 컨테이너로 실행시킬 것이다.

- docker-compose

```docker
version: '3'
services:
  db:
    build: 
      context: ./docker-test-db # 도커 file이 있는 디렉토리 경로
      dockerfile: Dockerfile
    ports:
      - 3306:3306
    volumes:
      - ./docker-test-db/store:/var/lib/mysql
    networks:
      - network
  server:
    build: 
      context: ./docker-test-server-main
      dockerfile: Dockerfile
    restart: always
    ports:
      - 8080:8080
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/metadb?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_DRIVER: com.mysql.cj.jdbc.Driver
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root1234
    networks:
      - network

networks:
  network:
```

`context` 및 `dockerfile` : 해당 값을 사용하여 `docker-test-server-main` 디렉터리에서 Dockerfile을 사용하여 이미지를 빌드를 하게 된다.

`**context**` 를 적어주지 않으면 docker-test-db가 build 될 때 제대로 되지 않을 수 있다. 왜냐하면 docker compose는 docker compose가 있는 디렉토리를 바라보게 된다. 하지만 현재 아래의 구조같이 동일 선상에 있다.

그렇기 때문에 Dockerfile을 찾지 못한다. 그렇기 때문에 `context` 를 넣어주어야 한다.

- spring_mysql_docker_compose
    - docker-test-db, docker-test-server-main,docker-compose.yml

`depend_on` : services.db 키워드를 의존받는 다는 의미로,  **`db`** 서비스가 먼저 시작되도록 지정 한 것이다.  어플리케이션 서버가 데이터베이스 서버에 의존하는 경우 데이터베이스 서버가 먼저 실행되어야 함을 의미하는 것이다.

**`environment`** : 어플리케이션의 환경 변수를 설정한다. 주로 데이터베이스 연결 정보가 여기에 포함되어 있다.

`SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/metadb?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true`
여기서 주목해야 할 것이 있다. 바로 `jdbc:mysql://db:3306/metadb`이 부분이다. `**depend_on**` 설정으로 **`db`** 를 의존하였기에 `db`가 먼저 생긴 후 `services`안에 `db`라는 키워드를 가져다 쓴 것이다. `db`라는 것을 어떻게 알았을까? networks에서 설정한 동일 네트워크 키워드를 사용했기 때문에 알 수 있다.
만약 환경변수를 활용하지 않고 application-prod.yml 안에 직접 적게 되면 `db`의 주소를 모른다. 즉, docker 내부안에 host 번호를 외부는 절대 모른다. 내부안에서의 연결은 내부안에서만 하자.

```docker
spring:
  datasource:
    url: jdbc:mysql://db:3306/metadb?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true
```

**`networks`** : **`network`**라는 이름의 Docker 네트워크에 서비스를 추가한 것이다. 이는 컨테이너들끼리 연결이 되게 만들어 서비스 간 통신을 수월하게 해주는 것이다. **`network`** 는 키워드 이름으로 아무 문자가 와도 상관 없다. 만약, 아래 코드와 같이 ‘hello’로 지정 했다면 `services.db`와 `services.server` 두 곳 다 `networks: - hello` 로 설정하면 연결이 된다.

```docker
networks:
	hello:
```

그럼 컨테이너로 만들어보자. **이때, docker-compose가 있는 경로에서 실행해야 한다.**

```bash
docker-compose up -d
```

아래는 컨테이너 실행 후 결과에 대한 로그이다.
```bash
$ docker-compose up
#1 [db internal] load build definition from Dockerfile
#1 transferring dockerfile: 261B 0.0s done
#1 ...

#2 [db internal] load .dockerignore
#2 transferring context: 2B 0.2s done
#2 DONE 1.1s

#1 [db internal] load build definition from Dockerfile
#1 DONE 1.3s

#3 [db internal] load metadata for docker.io/library/mysql:8.0
#3 ...

#4 [db auth] library/mysql:pull token for registry-1.docker.io
#4 DONE 0.0s

#3 [db internal] load metadata for docker.io/library/mysql:8.0
#3 DONE 4.4s

#5 [db 1/2] FROM docker.io/library/mysql:8.0@sha256:a7a96a9dbf6f310703c4e0c61086b23c5835c33a05544cdc952a7cd0b8feb675
#5 resolve docker.io/library/mysql:8.0@sha256:a7a96a9dbf6f310703c4e0c61086b23c5835c33a05544cdc952a7cd0b8feb675
#5 resolve docker.io/library/mysql:8.0@sha256:a7a96a9dbf6f310703c4e0c61086b23c5835c33a05544cdc952a7cd0b8feb675 0.9s done
#5 sha256:a7a96a9dbf6f310703c4e0c61086b23c5835c33a05544cdc952a7cd0b8feb675 549B / 549B done
#5 ...
```