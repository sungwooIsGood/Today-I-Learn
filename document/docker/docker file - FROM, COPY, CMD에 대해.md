Dockerfile(도커 파일)은 Docker 컨테이너 이미지를 빌드하기 위한 텍스트 파일이다.

간단한 명령어들로부터 시작해보자.

Dockerfile

```sql
FROM httpd
COPY ./webapp /usr/local/apache2/htdocs
CMD ["httpd-foreground"]
```

webapp 디렉토리 index.html 파일이 들어있다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/a6ad3c4f-9e93-443a-9c2b-a377055fafe9/Untitled.png)

**FROM**: 사용할 기본(base) 이미지를 지정하는 지시문이다. **`FROM`** 지시문은 Docker 컨테이너 이미지를 빌드할 때 가장 먼저 나타나야 한다. 기본 이미지는 컨테이너를 시작하는 데 사용되며, 해당 이미지를 기반으로 컨테이너 이미지를 빌드한다.

```sql
FROM 이미지명:태그
```

**COPY**: Dockerfile에서 사용되는 지시문 중 하나로, 호스트 파일 시스템에서 파일 또는 디렉토리를 Docker 이미지 내의 파일 시스템으로 복사하는 역할을 한다. docker의 volumn을 생각 해보면 된다.

```sql
COPY <소스 경로> <대상 경로>
```

**CMD**: 컨테이너가 시작될 때 실행할 명령을 지정하는 역할을 한다. 즉, 실행할 프로세스를 지정해주는 것이다. 컨테이너로 시작될 때 실행할 기본 명령 또는 명령 및 인수를 정의한다.

```sql
CMD ["executable", "param1", "param2", ...]
```

- **`"executable"`**: 컨테이너가 시작될 때 실행할 실행 파일 또는 명령을 지정한다. 이것은 컨테이너에서 실행될 기본 명령한다.
- **`"param1", "param2", ...`**: 명령 실행에 필요한 매개변수나 옵션을 나타낸다.

**`CMD`** 지시문은 Dockerfile에서 한 번만 사용된다.

Dockerfile을 완성했다면 우린 빌드를 시켜주어야 한다. 이전까지는 run으로 동작시켰다면 이번엔 build 옵션을 활용해야 한다.

```sql
docker build -t <이미지_이름:태그> <Dockerfile_경로_또는_URL>
docker build -t webserver ./

```

* `./` 를 한 이유는 ‘현재 디렉토리’라는 의미로 현재 디렉토리에서 알아서 Dokerfile을 찾는다. Dockerfile은 키워드이기 때문이다.

컨테이너 실행

```sql
docker run -dit -p 8080:80 --name webserver webserver
```

- 정상적으로 컨테이너가 작동

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/0293dc5b-afbd-4606-8112-4fc8d0106649/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/5dbc7ffd-1d30-494f-af62-382e6c67c7cc/Untitled.png)

원래대로라면 `docker run -dit -p 8080:80 webserver` 명령어대로라면 8080에는 ‘**it works!’가 떠야하지만 COPY 때문에** htdocs 디렉토리안에 index.html 파일이 들어있는 것을 눈으로 확인해볼 수 있었다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/1b22554b-7dec-4fd1-8ba9-2226bb3801f6/Untitled.png)