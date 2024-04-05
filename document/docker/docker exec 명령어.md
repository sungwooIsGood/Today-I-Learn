먼저, 아파치 서버인 httpd 컨테이너를 실행시켜 보자.

```sql
docker run -d -p 8080:80 --name myhttpd httpd
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/835df6e4-d297-45b4-ac5d-aa1c55ef2696/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/0d088f64-2c5b-4f25-91b9-6deb8cae37af/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/74a5c845-e10e-4ae5-991c-482ac2d7d050/Untitled.png)

localhost:8080으로 접속했을 때 httpd가 정상적으로 작동하는 것을 눈으로 확인했다.

만약 httpd 서버에 접속해서 **‘It wordks!’** 문구를 변경하고 싶다면 어떡해야할까? 

‘서버에 접속’이란 단어를 보자.`attach`라는 명령어가 기억날 것이다.

하지만 `attach` 는 `-it` 상태여야 한다. 즉, 터미널과 상호작용할 수 있는 모드에서만 가능하다. httpd의 `COMMAND`는 `httpd-foreground`이다.

우린 이럴 때 `exec` 명령어를 이용하면 된다.

### exec

`exec` 명령어는 아래 코드를 보면서 알아보자.

```sql
docker exec -it myhttpd bash 
[or]
docker exec -it myhttpd /bin/bash
```

- `exec` :  `COMMAND` 를 변경해서 서버에 접속을 하려면 `exec`를 사용하면 된다.
- `-it` : 앞에 d를 붙이지 않는 것은 우린 백그라운드로 접속하지 않을 것이기 때문이다.
- `bash` : 리눅스 및 유닉스 기반 시스템에서 사용되는 Bourne Again Shell (Bash)의 실행 파일 경로, Bash는 가장 일반적으로 사용되는 셸(shell) 중 하나로, 사용자와 시스템 간의 상호 작용을 가능하게 한다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/8fea963c-6f01-4e2a-aa54-d1fe5ae94f53/Untitled.png)

‘winpty’는 window에서 git bash를 열고 쳤을 때 입력 해주어야 한다. 여하튼, 잘 접속이 되는 것을 확인했다.

---

그럼 애초에 아래와 같이 `-dit` 를 사용했으면 됐지 않았나? 라고 생각할 수 있다. 직접 한번 해보자.

```sql
docker run -dit -p 8080:80 httpd
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/40621da3-1e24-4618-9865-f1f98baec956/Untitled.png)

httpd container가 정상적으로 작동하는 것을 볼 수 있다. 또한, `COMMAND` 가 `/bin/bash` 가 아닌 `httpd-foreground` 로 동작 되었다.그래도 한번 `attach`를 통해 접속 해보자.

```sql
docker attach {container name}
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/0c62a652-afe2-46e2-9857-cc3bd303a0be/Untitled.png)

이상하게 container가 바로 꺼져 버렸다. 왜그런 것일까?

무언가를 다운 받아 실행시키려면 `docker run -d` 를 입력해주자. 그 이유는 무언가 서버든 프로그램이든 이미지를 동작 시킬 때, OS를 설치하고 서버를 설치하고 실행시킨 이미지를 동작 시키기 때문이다.

반대로, 내부적으로 프로세스가 돌고 있는게 아닌 것들은
`docker run -dit` 을 통해 /bash 모드로 해야지 `attach` 명령어를 통해 서버 내부를 제어할 수 있다.

그러니 `docker run -d -p 8080:80 httpd` 는 OS도 설치하고 OS안에서 서버가 동작 하게 만든 것이다.
그래서 `docker run -d -p 8080:80 httpd bash` 로 입력하면 httpd가 bash로 실행되기 때문에 아파치 서버가 정상적으로 실행이 되지 않기 때문에 바로 죽어버리는 것이다.

**우린 특정 프로그램을 다운 받아 사용할 때 OS도 같이 설치 된 후 그 위에 프로그램이 돌아간다는 것을 늘 기억하자.**

결론, docker -dit로 해도 OS컨테이너, 프로그램 컨테이너가 정상적으로 동작하기 **때문에 통상 `-dit` 옵션을 사용하면 된다. 단, OS는 `attach`, 프로그램 컨테이너는 `exec -it`로 접속해야 한다는 것은 꼭 알아야 한다.**

```sql
docker run -dit ...
```