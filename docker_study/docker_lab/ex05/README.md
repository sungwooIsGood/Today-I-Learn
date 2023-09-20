# Dockerfile - ENV, -v

이번 시간에는 환경변수 세팅에 관련해서 공부해볼 것이다.

**환경 변수란?**

운영 체제에서 제공하는 설정 정보를 저장하는 변수를 환경 변수라고 한다. 이 변수는 OS 안에서 어디서든 사용할 수 있다.

**Docker컨테이너에서 환경변수**는 컨테이너 내부에서 사용되는 환경 설정 정보를 정의하는 데 사용된다. 이러한 환경 변수는 컨테이너 이미지 내부에서 정적으로 설정되거나, 컨테이너가 실행될 때 동적으로 지정될 수 있다. 환경 변수는 애플리케이션에서 구성 값을 전달하거나 컨테이너 동작을 제어하는 데 사용된다.
만약 Docker안에 MySQL을 설치했다고 가정해보자. 서버는 ubuntu이다. 이 ubuntu안에 환경 변수를 생성한 후 이 환경 변수를 MySQL에서 가져다 쓰는 것을 해볼 것이다.

docker hub에서 mysql문서를 찾아보자.

> Docker hub 공식 문서를 가져왔다. 환경변수 관련된 문서이다.
> 
> 
> [mysql - Official Image | Docker Hub](https://hub.docker.com/_/mysql)
> 
> ## Environment Variables
> 
> When you start the `mysql` image, you can adjust the configuration of the MySQL instance by passing one or more environment variables on the `docker run` command line. Do note that none of the variables below will have any effect if you start the container with a data directory that already contains a database: any pre-existing database will always be left untouched on container startup.
> 
> See also https://dev.mysql.com/doc/refman/5.7/en/environment-variables.html for documentation of environment variables which MySQL itself respects (especially variables like `MYSQL_HOST`, which is known to cause issues when used with this image).
> 
> ### `MYSQL_ROOT_PASSWORD`
> 
> This variable is mandatory and specifies the password that will be set for the MySQL `root` superuser account. In the above example, it was set to `my-secret-pw`.
> 
> ### `MYSQL_DATABASE`
> 
> This variable is optional and allows you to specify the name of a database to be created on image startup. If a user/password was supplied (see below) then that user will be granted superuser access ([corresponding to `GRANT ALL`](http://dev.mysql.com/doc/en/adding-users.html)) to this database.
> 
> ### `MYSQL_USER`, `MYSQL_PASSWORD`
> 
> These variables are optional, used in conjunction to create a new user and to set that user's password. This user will be granted superuser permissions (see above) for the database specified by the `MYSQL_DATABASE` variable. Both variables are required for a user to be created.
> 
> Do note that there is no need to use this mechanism to create the root superuser, that user gets created by default with the password specified by the `MYSQL_ROOT_PASSWORD` variable.
> 
> ### `MYSQL_ALLOW_EMPTY_PASSWORD`
> 
> This is an optional variable. Set to a non-empty value, like `yes`, to allow the container to be started with a blank password for the root user. *NOTE*: Setting this variable to `yes` is not recommended unless you really know what you are doing, since this will leave your MySQL instance completely unprotected, allowing anyone to gain complete superuser access.
> 
> ### `MYSQL_RANDOM_ROOT_PASSWORD`
> 
> This is an optional variable. Set to a non-empty value, like `yes`, to generate a random initial password for the root user (using `pwgen`). The generated root password will be printed to stdout (`GENERATED ROOT PASSWORD: .....`).
> 
> ### `MYSQL_ONETIME_PASSWORD`
> 
> Sets root (*not* the user specified in `MYSQL_USER`!) user as expired once init is complete, forcing a password change on first login. Any non-empty value will activate this setting. *NOTE*: This feature is supported on MySQL 5.6+ only. Using this option on MySQL 5.5 will throw an appropriate error during initialization.
> 
> ### `MYSQL_INITDB_SKIP_TZINFO`
> 
> By default, the entrypoint script automatically loads the timezone data needed for the `CONVERT_TZ()` function. If it is not needed, any non-empty value disables timezone loading.
> 

위 문서를 토대로 Dockerfile을 통해 MySQL 설치함과 동시에 환경변수를 설정 해보자.

```sh
FROM mysql

ENV MYSQL_USER=ian
ENV MYSQL_PASSWORD=ian1234
ENV MYSQL_ROOT_PASSWORD=root1234
ENV MYSQL_DATABASE=iandb

CMD ["--character-set-server=utf8mb4","--collation-server=utf8mb4_unicode_ci"]
```

위와 같이 Dockerfile로 작성했다면 docker run 할 때 `-e` 변수 설정을 안해 주어도 된다.

```sh
$ docker run -d --name mysql -e MYSQL_USER=ian -e MYSQL_PASSWORD=ian1234 mys
```

```sh
$ docker build -t mysql-image .
$ docker run -d --name mysql-test mysql-image
```

정상적으로 동작하는 것을 확인 해볼 수 있다. 기본포트는 3306이다.

또 서버에 안 들어가볼 수 없다.

```sh
$ docker exec -it mysql bash
```

리눅스 환경에서 환경변수를 읽어보자.

```sh
$ echo $MYSQL_USER
$ echo $MYSQL_DATABASE
```

`echo`는 터미널 또는 명령 프롬프트에서 사용되는 명령어 중 하나로, 주어진 텍스트를 화면에 출력하는 명령어이다.

환경 변수를 읽을 때는 `$` 를 사용하면 된다.

MySQL에 Volume이란 개념을 도입해보자.

Docker 컨테이너는 일반적으로 격리된 환경에서 실행되며 컨테이너가 종료되면 컨테이너 내부의 파일 시스템 변경 사항은 삭제된다. 그러나 Volume을 사용하면 데이터를 컨테이너와 분리된 영구 스토리지 영역에 저장하고 여러 컨테이너 간에 데이터를 공유할 수 있다. 즉, Volume은 컨테이너와 호스트 서버 간 데이터를 공유하고 저장한다.

공식문서를 보자.

> volume과 관련된 문서이다.
> 
> 
> [mysql - Official Image | Docker Hub](https://hub.docker.com/_/mysql)
> 
> # 주의사항
> 
> ## 데이터 저장 위치
> 
> 중요 참고 사항: Docker 컨테이너에서 실행되는 애플리케이션에서 사용되는 데이터를 저장하는 방법에는 여러 가지가 있습니다. `mysql`이미지 사용자가 다음을 포함하여 사용 가능한 옵션에 익숙해지도록 권장합니다 .
> 
> - [Docker가 자체 내부 볼륨 관리를 사용하여 호스트 시스템의 디스크에 데이터베이스 파일을 기록함으로써 데이터베이스](https://docs.docker.com/engine/tutorials/dockervolumes/#adding-a-data-volume)
>     
>     데이터의 저장을 관리하도록 합니다 . 이는 기본값이며 사용자에게 쉽고 투명합니다. 단점은 호스트 시스템, 즉 컨테이너 외부에서 직접 실행되는 도구 및 응용 프로그램의 경우 파일을 찾기가 어려울 수 있다는 것입니다.
>     
> - 호스트 시스템(컨테이너 외부)에 데이터 디렉토리를 생성하고 [이를 컨테이너 내부에서 볼 수 있는 디렉토리에 마운트합니다](https://docs.docker.com/engine/tutorials/dockervolumes/#mount-a-host-directory-as-a-data-volume)
>     
>     . 이렇게 하면 데이터베이스 파일이 호스트 시스템의 알려진 위치에 배치되고 호스트 시스템의 도구 및 응용 프로그램이 파일에 쉽게 액세스할 수 있습니다. 단점은 사용자가 디렉토리가 존재하는지, 예를 들어 호스트 시스템의 디렉토리 권한 및 기타 보안 메커니즘이 올바르게 설정되었는지 확인해야 한다는 것입니다.
>     
> 
> Docker 문서는 다양한 스토리지 옵션과 변형을 이해하기 위한 좋은 출발점이며, 이 영역에 대해 논의하고 조언을 제공하는 여러 블로그와 포럼 게시물이 있습니다. 위의 후자 옵션에 대한 기본 절차를 간단히 보여 드리겠습니다.
> 
> 1. **호스트 시스템의 적절한 볼륨에 데이터 디렉터리를 만듭니다(예: `/my/own/datadir`.**
> 2. 다음과 같이 컨테이너를 시작하세요 `mysql`.
>     
>     ```
>     $ docker run --name some-mysql -v /my/own/datadir:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag
>     
>     ```
>     
> - `v /my/own/datadir:/var/lib/mysql/my/own/datadir/var/lib/mysql`
>     
>     명령의 일부는 컨테이너 내부와 같이 기본 호스트 시스템의 디렉터리를 마운트합니다 . 여기서 MySQL은 기본적으로 데이터 파일을 작성합니다.
>     
> 
> ## MySQL 초기화가 완료될 때까지 연결이 없습니다.
> 
> 컨테이너가 시작될 때 초기화된 데이터베이스가 없으면 기본 데이터베이스가 생성됩니다. 이는 예상되는 동작이지만 초기화가 완료될 때까지 들어오는 연결을 수락하지 않음을 의미합니다. 이로 인해 여러 컨테이너를 동시에 시작하는 자동화 도구(예: )를 사용할 때 문제가 발생할 수 있습니다 `docker-compose`.
> 
> MySQL에 연결하려는 애플리케이션이 MySQL 가동 중지 시간을 처리하지 않거나 MySQL이 정상적으로 시작될 때까지 기다리지 않는 경우 서비스가 시작되기 전에 연결-재시도 루프를 배치해야 할 수 있습니다. 공식 이미지의 이러한 구현 예는 [WordPress](https://github.com/docker-library/wordpress/blob/1b48b4bccd7adb0f7ea1431c7b470a40e186f3da/docker-entrypoint.sh#L195-L235) 또는 [Bonita를](https://github.com/docker-library/docs/blob/9660a0cccb87d8db842f33bc0578d769caaf3ba9/bonita/stack.yml#L28-L44) 참조하세요 .
> 
> ## 기존 데이터베이스에 대한 사용량
> 
> `mysql`이미 데이터베이스가 포함된 데이터 디렉터리(특히 `mysql`하위 디렉터리) 를 사용하여 컨테이너 인스턴스를 시작하는 경우 `$MYSQL_ROOT_PASSWORD`실행 명령줄에서 변수를 생략해야 합니다. 어떤 경우에도 무시되며 기존 데이터베이스는 어떤 식으로든 변경되지 않습니다.
> 
> ## 임의의 사용자로 실행
> 
> `mysqld`디렉터리의 권한이 이미 적절하게 설정되어 있음을 알고 있거나(예: 위에서 설명한 기존 데이터베이스에 대해 실행) 특정 UID/GID로 실행해야 하는 경우 `--user`임의의 값으로 설정 하여 이 이미지를 호출할 수 있습니다. ( `root`/ 제외 `0`) 원하는 액세스/구성을 달성하기 위해:
> 
> ```
> $ mkdir data
> $ ls -lnd data
> drwxr-xr-x 2 1000 1000 4096 Aug 27 15:54 data
> $ docker run -v "$PWD/data":/var/lib/mysql --user 1000:1000 --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag
> ```
> 
> ## 데이터베이스 덤프 생성
> 
> 대부분의 일반 도구는 작동하지만 경우에 따라 서버에 대한 액세스 권한을 보장하기 위해 사용법이 약간 복잡해질 수 있습니다 `mysqld`. 이를 확인하는 간단한 방법은 `docker exec`다음과 유사하게 동일한 컨테이너에서 도구를 사용하고 실행하는 것입니다.
> 
> ```
> $ docker exec some-mysql sh -c 'exec mysqldump --all-databases -uroot -p"$MYSQL_ROOT_PASSWORD"' > /some/path/on/your/host/all-databases.sql
> 
> ```
> 
> ## 덤프 파일에서 데이터 복원
> 
> 데이터를 복원합니다. 다음과 유사하게 플래그 `docker exec`와 함께 명령을 사용할 수 있습니다 .`-i`
> 
> ```
> $ docker exec -i some-mysql sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD"' < /some/path/on/your/host/all-databases.sql
> ```
> 

---

## volume 실습

### volume의 종류

- Bind Mount: 호스트 환경의 특정 경로를 컨테이너 내부 볼륨 경로와 연결하여 마운트한다. 이 방법은 보안에 영향을 미칠 수도 있다고, 홈페이지에서 경고하고 있긴 하다.
- Volume (가장 일반적): 도커 볼륨은 도커 컨테이너에서 도커 내부에 도커 엔진이 관리하는 볼륨을 생성하는 것이다. 생성된 볼륨은 호스트 디렉터리의 `/var/lib/docker/volumes` 경로에 저장되며, 도커를 사용하여 관리가 용이하다.
- tmpfs Mount: 이 방법은 리눅스에서 도커를 실행하는 경우에만 사용할 수 있는 기능이라고 한다. 호스트의 파일 시스템이 아닌, 메모리에 저장하는 방식을 사용한다.

우린 Bind Mount와 Volume을 사용해보자.

위 문서에 따르면 내부에 데이터가 저장되는 경로는 아래와 같다.

→ `/var/lib/mysql`

### Bind Mount

현재 경로와 마운트해보자.

→ `C:\Users\Ian\Desktop\ex05\mysql-volume`

위에서 했던 컨테이너를 종류하고 `-v` 옵션을 통해 volume을 가지고 동작 시켜보자.

```sh
docker run -d -v C:\Users\Ian\Desktop\ex05\mysql-volume:/var/lib/mysql --name mysql mysql-image
```

Volume이 정상적으로 되었을 것이다.

추가로, 위에는 호스트 서버의 폴더와 연결하는 방법이다. 이 외에 volume 디렉토리를 생성하여 만드는 방법도 있다. 이 방법은 특정 호스트 시스템 서버와 연결하는 것이 아닌 volume 폴더를 만들어 사용할 수도 있다. 그렇기 때문에 이 volume을 사용하면 해당 컨테이너가 종료되어도 연결했었던 volume과 다시 연결하면 데이터는 삭제되지 않은 상태에서 다시 사용할 수 있다.

### Volume

실습을 해보자. 

- -v 옵션 뒤에 생성할 volume이름을 작성하면 된다.

```sh
docker run -d -v mysql-volume:/var/lib/mysql --name mysql mysql-iib/mysql --name mysql mysql-image
```

- docker desktop일 경우 - volume 탭 or docker명령어 `docker volume ls`
    - volume이 생성된 것을 알 수 있다.

DB에서 데이터를 생성해보고 docker 컨테이너를 종료 시켜보자.

- DB

```sql
use iandb

create table person(
	id int primary key,
	name varchar(100)
);

insert into person(id, name) values(1, ssar);
```

- docker stop

```sh
$ docker stop mysql
$ docker rm mysql
```
- docker desktop 에서 volume이 안 없어진 것을 볼 수 있다.

volume을 설정 안한 상태에서 docker 컨테이너가 꺼졌다면 데이터도 다 날라가는게 정상이다. 하지만 다시 연결해서 어떻게 되었나 보자.

```sh
$ docker run -d -v mysql-volume:/var/lib/mysql --name mysql mysql-image
```

- DB

```sql
select * from ian
```

데이터가 그대로 남아 있는 것을 볼 수 있다.

---

volume에 대해 간략하게 정리 해보자면,

- `-v` **옵션 뒤에 통해 호스트 서버 주소가 써있는 경우**
    - 호스트 서버의 디렉토리와 연결하여 호스트 서버의 데이터를 저장하는 것이다.
- `-v` **옵션 뒤에 생성할 volume 이름이 써있는 경우**
    - volume를 생성하는 것으로, 해당 volume과 마운트만 해놓았다면 컨테이너가 꺼져도 -v 옵션을 통해 다시 volume과 연결 시킨다면 데이터는 보존된다.