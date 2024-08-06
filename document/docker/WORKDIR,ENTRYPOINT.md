이번엔 ENTRYPOINT를 사용해보는 시간을 가질 것이다.

openjdk를 다운받아보자. 11-jdk-slim 버전으로 dockerFile을 통해 받아보자.

dockerFile의 위치는 `/Users/anseong-u/Desktop/docker_lab/ex01/build/Dockerfile` 이다. 또한, build 폴더 안에는 아래 깃헙 주소에서 release 브랜치에서 aws-v3를 다운받아 저장시켜주었다.

[](https://github.com/codingspecialist/aws-v3/blob/release/aws-v3-0.0.3.jar)

본격적으로 dockerFIle을 작성해보자.

```jsx
FROM openjdk:11-jdk-slim

WORKDIR /app

COPY ./aws-v3-0.0.3.jar ./application.jar

ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=dev", "application.jar"]
```

`FROM`을 통해 openjdk를 받아주고 `:` 뒤에는 태그를 사용해 받아올 버전을 적어준 것이다.

`WORKDIR` : 작업 디렉토리를 설정하는 지시자로써, docker 컨테이너 내에서 작업 디렉토리를 설정하고, 이 디렉토리에서 명령을 실행할 때 경로를 상대적으로 지정하는 것이다. 

docker 컨테이너에 진입하게 되면 리눅스 서버안에서 `/` 경로부터 시작하게 된다. 
ex) `/bin` , `/sbin`, `/app` 

그렇기 때문에 `WORKDIR`명령어를 통해 docker 컨테이너에 접근하게 되면  `/app` 디렉토리에 접근하게 된다.

`WORKDIR`를 통해서 `/app` 디렉토리에 접근 한 후

`COPY` 를 통해 호스트 시스템 서버에 현재 경로부터 경로를 지정하여 지정한 파일을 `./application.jar` 로 파일명을 `aws-v3-0.0.3.jar` → `application.jar` 바꿔서 복사 시킨 것이다.  `./` 는 현재 위치를 말한다. `WORKDIR`에서 `/app` 으로 설정 했기 때문에. `/app/application.jar` 위치인 것이다. 그래서 `WORKDIR`을 사용하는 것이다.

> `COPY` 와 비슷하게 `ADD`가 있는데 파일이나 디렉토리를 Docker 이미지 내의 파일 시스템에 추가하는 역할을 한다.
> 
> 1. **복사의 단순성**:
>     - **`COPY`**: **`COPY`** 명령어는 파일 및 디렉토리를 Docker 이미지로 복사하는 데 사용된다. 단순하게 파일을 복사하는 역할을 수행하며, **압축 해제나 URL 다운로드와 같은 추가 작업을 수행하지 않는다.**
>     - **`ADD`**: **`ADD`** 명령어는 **`COPY`**와 비슷하게 파일을 Docker 이미지로 복사할 수 있지만, 추가적으로 압축 해제 및 원격 URL 다운로드와 같은 기능을 수행할 수 있습니다.
> 2. **자동 압축 해제**:
>     - **`COPY`**: **`COPY`**는 파일을 그대로 복사하므로, 압축된 파일은 압축 해제되지 않고 그대로 이미지에 포함한다.
>     - **`ADD`**: **`ADD`**는 압축된 파일(예: **`.tar`**, **`.gz`**, **`.zip`** 등)을 복사할 때 자동으로 압축을 해제하려고 시도한다.
> 3. **URL 다운로드**:
>     - **`COPY`**: **`COPY`**는 로컬 파일 시스템에서만 파일을 복사할 수 있다.
>     - **`ADD`**: **`ADD`**는 로컬 파일 시스템 뿐만 아니라 원격 URL에서 파일을 다운로드하여 복사할 수 있다. 이를 통해 원격 리소스를 복사할 때 유용하다.

`ENTRYPOINT` : Docker 컨테이너가 시작될 때 특정 명령어나 스크립트가 자동으로 실행되므로 컨테이너가 실행될 때 필요한 프로세스를 정의하는 데 사용된다.

호스트 시스템 서버로 예를 들면, 

`/Users/anseong-u/Desktop/docker_lab/ex01/build` 안에서 `java -jar -Dspring.profiles.active=dev aws-0.0.3.jar`를 실행하라는 스크립트인 것이다.

```jsx
ENTRYPOINT ["executable", "param1", "param2"]
```

- **`executable`**: 컨테이너가 시작될 때 실행할 실행 파일 또는 스크립트의 경로 또는 이름을 지정한다.
- **`param1`**, **`param2`**: 실행 파일에 전달할 매개변수나 인자를 나타낸다. 이들은 선택사항이다.

> **CMD와 차이점**

**`CMD`**는 **컨테이너가 실행될 때 기본 명령을 제공**하는 데 사용되며, **`ENTRYPOINT`**는 컨테이너의 핵심 실행 파일 또는 스크립트를 지정하는 데 사용된다. 일반적으로 **`CMD`**를 사용하여 컨테이너의 **기본 동작을 정의**하고, 필요한 경우 **`ENTRYPOINT`**를 사용하여 실행 파일 또는 스크립트를 더 정확하게 제어한다. 즉, 목적 → `ENTRYPOINT`,  목적을 실행하기 위한 옵션은 `CMD`로 이해해보자.
> 

DockerFile을 빌드 시켜보자.

`/Users/anseong-u/Desktop/docker_lab/ex01/build` 경로에서 시작했다.

```docker
docker build -t java.server .
```

<img width="703" alt="스크린샷 2023-09-17 오후 5 35 34" src="https://github.com/user-attachments/assets/95fb7a72-2e8a-4552-a0ce-557890d4a1a8">

정상적으로 image가 생성되었는지 확인해보자.

```docker
docker images
```

<img width="709" alt="스크린샷 2023-09-17 오후 5 37 18" src="https://github.com/user-attachments/assets/3a9e35c5-2b41-4a80-8021-faeb2a80d32d">

그 후 컨테이너로 만들자.

```docker
docker run -dit -p 8080:8081 --name java-server java-server
```

<img width="548" alt="스크린샷 2023-09-17 오후 5 38 53" src="https://github.com/user-attachments/assets/dc64f9d7-4b62-4dba-9b12-f57d54155e86">

정상적으로 동작하여 `number` 에다 여러 값을 줘보자.

<img width="698" alt="스크린샷 2023-09-17 오후 5 36 02" src="https://github.com/user-attachments/assets/c8a8915b-edbc-47dd-899b-2b90ab6d64b0">

그 후 docker에서 log를 볼 수 있다.

`--tail` 옵션을 통해 1000개의 로그를 볼 수 있고. 추가로, `-f` 을 통해 실시간으로 로그들을 볼 수 있다. `docker logs java-server -f`

```docker
docker logs java-server --tail 1000
```

<img width="1319" alt="스크린샷 2023-09-17 오후 5 40 18" src="https://github.com/user-attachments/assets/9444363a-f092-4d21-9487-1e88819f4e24">
