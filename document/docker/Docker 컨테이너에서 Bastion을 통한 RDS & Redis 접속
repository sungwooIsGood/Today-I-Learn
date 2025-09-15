## 1. 문제 상황

- 로컬 PC에서 Docker 컨테이너를 실행하여 FAST API 서버를 구동
- 로컬 PC에서 AWS VPC 안에 있는 **RDS(Postgres)** 및 **Redis**에 접근해야 함
- VPC 내부망은 외부에서 직접 접속 불가 → **Bastion 서버를 통한 SSH 터널링** 필요
- 초기 환경에서 컨테이너 내에서 DB 접속 시 다음과 같은 오류 발생:

```
ConnectionRefusedError: [Errno 111] Connect call failed ('172.17.0.1', 15432)
```

---

## 원인을 분석해보면…

1. **컨테이너에서 localhost ≠ 호스트**
    - Docker 브리지 네트워크에서 컨테이너의 `localhost`는 **컨테이너 자기 자신이다.**
    - 로컬 PC 호스트 머신은 **172.17.0.1**이다. (Linux 기준)
    - 따라서 컨테이너에서 `localhost:15432`를 사용하면 SSH 터널이 열려 있어도 접근 불가능 했었던 것이다.
2. **SSH 터널 기본 바인딩**
    - SSH 포트포워딩을 기본 설정(`L 15432:db:15432`)으로 실행하면 **127.0.0.1(localhost)에서만 수락**
    - 컨테이너에서 172.17.0.1:15432로 연결 시 연결 거부(`ConnectionRefusedError`) 발생
3. **포트 확인**
    - RDS와 Redis가 실제 사용하는 포트와 SSH 터널링 포트가 일치해야 함
    - 본 사례에서는 RDS=15432, Redis=16379로 이미 맞춰져 있음

---

## 해결 방법

컨테이너에서 Bastion을 통해 RDS/Redis에 연결하기 위해 두 가지 방법을 사용할 수 있었다.

## 방법 1: 브리지 네트워크 + SSH 터널 0.0.0.0 바인딩

SSH 터널을 컨테이너에서도 접근 가능하도록 모든 인터페이스(`0.0.0.0`)에 바인딩할 수 있다.

- **0.0.0.0 바인딩**을 사용하여 브리지 네트워크 컨테이너에서 호스트로 접근 가능하게 sh 파일 설정
    - 로컬 PC의 포트 15432 → Bastion → RDS
    - 로컬 PC의 포트 16379 → Bastion → Redis
- **bash**

```bash
ssh -i /home/ian/aws_pem/gs-bastion-key.pem -N \
  -L 0.0.0.0:15432:gs-cms-postgres-db.crm4ywueazx8.ap-northeast-2.rds.amazonaws.com:15432 \
  -L 0.0.0.0:16379:gs-cms-redis-cluster.gksydc.0001.apn2.cache.amazonaws.com:16379 \
  ec2-user@52.xx.xx.xxx

```

- **.env**
    - 컨테이너 환경 변수에서 호스트 IP(172.17.0.1) 사용:

```
POSTGRES_SERVER= 
POSTGRES_PORT=15432
POSTGRES_USER=gs_cms_user
POSTGRES_PASSWORD=비밀번호
POSTGRES_DB=gs_cms_db
```

- **docker-compose.yml**
    - docker-compose는 브리지 네트워크(default) 사용:는 브리지 네트워크(default) 사용:

```yaml
services:
  web:
    container_name: cms-api-server
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8000:8000"
    env_file:
      - .env

```

### 방법 2: network_mode="host"

- Linux 환경에서는 Docker 컨테이너를 **호스트 네트워크 모드**로 실행하면, 컨테이너가 호스트와 동일한 네트워크를 사용할 수 있다는 것을 파악했다.
- 이 경우 컨테이너에서 `localhost:15432`를 그대로 사용해도 SSH 터널을 통해 RDS/Redis에 접근할 수 있습니다.

- **docker-compose.yml**

```yaml
services:
  web:
    container_name: cms-api-server
    build:
      context: .
      dockerfile: Dockerfile
    network_mode: "host"
    env_file:
      - .env
```

- **.env**

```bash
POSTGRES_USER=gs_cms_user
POSTGRES_PASSWORD=gscmsuser#$#$%
POSTGRES_SERVER=localhost
POSTGRES_PORT=15432
POSTGRES_DB=gs_cms_db
POSTGRES_ASYNC_PREFIX=postgresql+asyncpg://
```

- 단, 이 방법은 브리지간의 격리 기능이 사라지고, 호스트 네트워크를 그대로 사용하기에 호스트에서 열린 포트와 docker에서 열린 포트끼리 충돌 할 수 있다.

---

## 추가로, Docker 브리지 네트워크란?

- Docker 브리지 네트워크는 **컨테이너끼리 통신할 수 있는 가상 네트워크**이다.
- 기본 브리지(`bridge`)를 사용하면 컨테이너들은 서로 IP로 통신할 수 있다. 예를 들어, 컨테이너1에서 컨테이너2의 IP(`172.17.0.3`)로 ping을 날려 통신할 수 있다.
- 컨테이너는 독립된 네트워크 네임스페이스를 가지므로 **각 컨테이너마다 고유한 가상 IP를 부여받는다.**
- 컨테이너에서 호스트로 접근할 때는 브리지 인터페이스 IP(`172.17.0.1`)를 사용해야 한다.
- 호스트에서 컨테이너로 접근할 때는 포트포워딩(`p <호스트포트>:<컨테이너포트>`)을 설정해야 한다.

---

## 즉, 정리해보면…

### 컨테이너 vs 로컬 PC에서 localhost와 172.17.0.1

- **로컬 PC 입장**:
    - `localhost` = 127.0.0.1, PC 자체를 의미
    - `172.17.0.1` = Docker 브리지 인터페이스(docker0) IP, 브리지 통신용
- **컨테이너 입장**:
    - `localhost` = 컨테이너 자기 자신
    - `172.17.0.1` = 호스트 PC
    - 따라서 컨테이너에서 로컬 PC에 열려 있는 SSH 터널이나 서비스에 접근하려면 **172.17.0.1을 사용해야 한다.**
- 요약: 컨테이너 입장과 호스트 입장이 다르므로, **컨테이너에서 바라볼 때 localhost는 자기 자신, 호스트는 172.17.0.1**이다.

| 항목 | 설명 |
| --- | --- |
| Docker에서 바라보는 localhost (컨테이너) | 컨테이너 자기 자신 |
| 172.17.0.1 | 호스트 머신(Linux, 브리지 네트워크) |
| SSH 터널 바인딩 | Docker에서 바라보는 localhost ****→ 접근 불가**,** 0.0.0.0 → 컨테이너 접근 가능 |
| docker compose.yml 속성: `network_mode`  | 브리지(default) 사용, host 모드 불필요 |

## 결론적으로…

- 로컬 Docker 컨테이너에서 Bastion과 RDS/Redis를 연결할 때는 **컨테이너 입장에서 호스트가 localhost가 아님**을 이해해야 한다.
- SSH 포트포워딩을 **0.0.0.0**으로 바인딩하고, 컨테이너에서 **172.17.0.1**을 바라보도록 설정
