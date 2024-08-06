먼저, `RUN` 명령어는 **리눅스에서 실행되는 명령어를 작성할 때 쓴다.**

실습으로 바로 배워보자.

docker hub에 접속 한 후 nginx를 실행시켜보자. 

아래 이미지는 nginx에서 어떻게 실행 시키는지 알려주는 문서이다.

<img width="666" alt="스크린샷 2023-09-17 오후 5 52 27" src="https://github.com/user-attachments/assets/3093281e-1f06-4f8d-a54a-91e3d0f33a92">

```jsx
docker run -dit -p 8080:80 --name nginx nginx
```

<img width="508" alt="스크린샷 2023-09-17 오후 5 54 56" src="https://github.com/user-attachments/assets/c4ddcef2-477d-41eb-a27a-9ea4122b5862">

docker 컨테이너에 접속한 후 nginx가 어떻게 동작 하는지 알아보자. 알아야 하는 이유는 바로 Dockerfile을 작성하기 위해서이다. 어느 디렉토리에서 이 nginx가 동작하는지 파헤쳐보자.

```jsx
docker exec -it nginx /bin/bash
```

<img width="666" alt="스크린샷 2023-09-17 오후 5 57 15" src="https://github.com/user-attachments/assets/10837fd0-c755-4030-bc39-53740ac8a997">

root에 들어온 것을 확인해 볼 수 있다.

맨 위에 nginx 문서를 보면 `/usr/share/nginx/html` 에서 동작 하는 것을 확인해볼 수 있다. 직접 디렉토리로 들어가보자.

<img width="372" alt="스크린샷 2023-09-17 오후 5 58 07" src="https://github.com/user-attachments/assets/aa21c958-363b-4ff0-91d4-9ef575d2085b">

<img width="431" alt="스크린샷 2023-09-17 오후 5 59 16" src="https://github.com/user-attachments/assets/3e8bfb9b-9f7a-4285-a2fc-bda4207c7245">

index.html을 수정해보고 싶다고 가정해보자. 근데 우린 편집기가 없다. 대신 `COPY` 명령어를 알고 있다. 구조도 알고 있고 그럼 본격적으로 Dockerfile을 run 해보자.

`/Users/anseong-u/Desktop/docker_lab/ex02` 폴더를 만들어주었다. 폴더안에는 index.html과 Dockerfile이 있다.

- Dockerfile
    - ubuntu에서 nginx를 설치한 후 호스트 서버(local)에서 index.html의 내용을 copy한 후 실행 시킬 것이다.

```jsx
FROM ubuntu

RUN apt-get update
RUN apt-get install -y nginx

# WORKDIR /usr/share/nginx/html -> 안된다. 아래 디렉토리에 파일이 있다. 공식 문서가 잘못된건가?
WORKDIR /var/www/html

# COPY ./ ./
COPY ./index.html ./index.nginx-debian.html

ENTRYPOINT ["nginx","-g","daemon off;"]
```

- index.html

```jsx
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>내가 만든 내 세상이야~~!!!</h1>
</body>
</html>
```

build시킨 후 컨테이너를 띄워보자.

```jsx
docker build -t nginx-server .
docker run -dit -p 8080:80 --name nginx-server nginx-server
```

<img width="698" alt="스크린샷 2023-09-17 오후 6 15 59" src="https://github.com/user-attachments/assets/93c19ab0-9251-4080-aab7-3672eb91343b">

<img width="697" alt="스크린샷 2023-09-17 오후 6 16 18" src="https://github.com/user-attachments/assets/a09d3211-d031-45e8-a299-f661c1bb704a">

<img width="505" alt="스크린샷 2023-09-17 오후 6 36 45" src="https://github.com/user-attachments/assets/2c7e18b4-0639-49ad-91de-63e85d60122d">

컨테이너 안에서 ./index.html ./index.nginx-debian.html 내용이 잘 카피 되었는지 보자.

<img width="599" alt="스크린샷 2023-09-17 오후 6 39 04" src="https://github.com/user-attachments/assets/7f1ac273-6db5-4e68-b2cd-a423426a3d82">

`WORKDIR` 때문에 컨테이너에 접속 하자마자 `/` 폴더로 접근한게 아닌 `/var/www/html` 디렉토리로 접속한 것을 볼 수 있다. `cat` 명령어를 통해 `./index.nginx-debian.html` 파일 내용이 복사된 것을 눈으로 확인 해보았다.

---

`RUN` 명령어의 역할이 감이 오는가?

`RUN` : **`RUN`** 명령어는 컨테이너 이미지를 빌드하는 과정 중에 실행될 명령어를 지정한다. 이 명령어는 Docker 이미지를 만들기 위해 호스트 시스템에서 실행되며, 이미지에 포함되는 파일 및 프로그램을 설치하거나 설정하는 데 사용된다.

그렇기 때문에 docker가 빌드 되기 전에 `FROM` 에서 ubuntu 이미지를 pull한 다음 `RUN` 명령어를 통해 `apt-get update` 패키지 업데이트 및 설치를 해준 후 apt-get을 통해 nginx를 ubuntu 서버에다가 설치해주었다. `-y`는 용량관련해서 동의하냐는 문구가 나오기 때문에 `-y`를 통해 동의해준다고 한 것이다.

`ENTRYPOINT ["nginx","-g","daemon off;"]` 이 명령어를 알아 볼 필요가 있다.

nginx를 실행할 것이고, `-g`는  옵션이다. `daemon off`는 도커 데몬을 꺼서 백그라운드가 아닌 포그라운드에서 실행시켜주었다. 그 이유는 `RUN` 을 실행할 때 컨테이너가 안 멈춘다. 반대로 백그라운드로 실행하면 서버가 바로 죽어버린다. 때문에 포그라운드로 실행해주어야 한다.
