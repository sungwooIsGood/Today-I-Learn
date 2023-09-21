아래 공식문서를 가지고 하나씩 시도해보았다.

[Try Docker Compose](https://docs.docker.com/compose/gettingstarted/)

### Docker compose란? - docker 공식 문서 중

> Compose는 다중 컨테이너 Docker 애플리케이션을 정의하고 실행하기 위한 도구입니다. Compose에서는 YAML 파일을 사용하여 애플리케이션 서비스를 구성합니다. 그런 다음 단일 명령을 사용하여 구성에서 모든 서비스를 생성하고 시작합니다.
> 
> - 서비스 시작, 중지 및 재구축
> - 실행 중인 서비스 상태 보기
> - 실행 중인 서비스의 로그 출력 스트리밍
> - 서비스에서 일회성 명령 실행

지금까지 Dockerfile을 통해 build하고 컨테이너를 동작 시켰었다. 컨테이너끼리 요청/응답을 하려면 IP, PORT 등 여러 정보들이 필요하다. 이는 되게 번거로운 작업이다.
그렇기 때문에 Docker compose는 이 번거로움을 해결하기 위해 나왔다. compose는 컨테이너들이 안에서 유기적으로 연결이 되어 실행할 수 있게 만든다. 이 compose 파일은 YAML로 작성할 수 있다.

---

### 실습

먼저 시작하기 전에 Docker compose를 설치 해주어야 한다. 설치 조건은 두 가지가 있다.

1. Docker Engine과 Docker Compose가 모두 포함된 [Docker Desktop](https://docs.docker.com/desktop/) 설치
2. [Docker 엔진](https://docs.docker.com/get-docker/) 및 [Docker Compose를](https://docs.docker.com/compose/install/) 독립 실행형 바이너리로 각각 설치

현재 로컬 환경은 Window와 MAC을 번갈아 쓰고 있기 때문에 Docker Desktop을 설치하여 사용 중이다. 

오늘은 docker compose안에 한 개의 docker 컨테이너만 동작 시켜 볼 것이다.

- docker-compose.yml

```yaml
services: # 키워드
	mysql-db: # name 내 마음대로 해도 됨
		image: mysql
		restart: always # 옵션, 종료 시 자동 시작
		volumes:
			- mysql-compose-volume:/var/lib/mysql
		environment: # 환경변수
      - MYSQL_ROOT_PASSWORD=root1234
      - MYSQL_DATABASE=rootdb
    ports: # 포트포워딩
      - "3306:3306"

volumes:
  mysql-compose-volume:
```

위 compose 내용은 Dockerfile이 없다고 가정한 것이다. 그렇기 때문에 `image: mysql`을 작성했다.

**`services`**: 이 부분은 Docker Compose 파일에서 컨테이너를 정의하는 부분이다.

**`mysql-db`**: 이 부분은 컨테이너의 이름을 정의합니다. 이 경우, **mysql-db**라는 이름의 서비스를 정의한 것이다.

**`image: mysql`**: 이 부분은 사용할 도커 이미지를 지정한다. 여기에서는 공식 MySQL 도커 이미지를 사용했다.

**`restart: always`**: 이 부분은 컨테이너가 종료될 때 자동으로 재시작하도록 설정한 것이다. **`always`** 옵션은 컨테이너가 종료 되더라도 항상 다시 시작된다.

**`volumes`**: 이 부분은 볼륨을 마운트할 때 사용된다. 여기에서는 `mysql-compose-volume`이라는 볼륨을 **`/var/lib/mysql`** 경로에 마운트한다는 것이다.

**`environment`**: 이 부분은 컨테이너 내에서 사용할 환경 변수를 설정한다. 여기에서는 MySQL의 root 패스워드(**`MYSQL_ROOT_PASSWORD`**)와 데이터베이스 이름(**`MYSQL_DATABASE`**)을 설정했다.

**`ports`**: 이 부분은 호스트와 컨테이너 간의 포트 포워딩을 설정한다. MySQL 컨테이너의 포트 3306을 호스트의 포트 3306으로 포워딩합니다. 이렇게 하면 호스트에서 MySQL 서버에 접속할 수 있습니다.

맨 아래 **volumes**는 Docker Compose 파일에서 마운트 할 volume을 정의하는 곳이다.

추가로, YAML에 따라 tab이 아닌 스페이스로 블록을 구성해야 한다.

다 작성 했다면 아래 명령어로 실행해보면 된다.

```bash
$ docker compose up -d # 백그라운드 환경
$ docker ps
```

정상적으로 띄워진 것을 확인 할 수 있다.

```
CONTAINER ID   IMAGE     COMMAND                   CREATED         STATUS         PORTS
                NAMES
ea6e2f9c2f96   mysql     "docker-entrypoint.s…"   6 minutes ago   Up 6 minutes   0.0.0.0:3306->3306/tcp, 33060/tcp   ex06-mysql-db-1
PS C:\Users\Ian\Desktop\ex06>
```