* 아래 명령어들은 docker images와 docker container가 없는 상태에서 공부해본 것이다.

---

docker hub 사이트에 들어가서 매번 docker image를 pull해오는 것은 여간 귀찮은 일이다. 그치만 docker에서는 image의 이름만 알면 run명령어를 입력 시켜주면 바로 pull 한 후 실행 시켜준다.

```sql
docker run -d --name myubuntu ubuntu
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/88e33c2e-3ce6-4fe6-8180-b2de7792d2ab/Untitled.png)

이 때, docker가 제대로 돌아가는지 확인해보기 위해 아래 명령어를 입력해주었다.

```sql
docker ps
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/a12b9a79-13f6-4370-a13b-9ded26790866/Untitled.png)

없다? 그럼 이미지는 제대로 받아졌는지 확인해보자.

```sql
docker images
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/96c28801-ae4d-4970-8ba5-6bf08071d02e/Untitled.png)

image는 있는데 왜 컨테이너 상태로 동작 되지 않은 것일까?

그럼 아파치 서버를 위와 같이 받아보자.

```sql
docker run -d --name myhttpd httpd
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/071fb385-52b7-457f-8c92-c6899fdd6ec0/Untitled.png)

```sql
docker ps
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/e7196cd4-b1ea-48f3-9ccb-590720514f78/Untitled.png)

아파치 서버는 `docker ps` 명령어를 통해 정상적으로 run 되어있는 것을 볼 수 있다. ‘ubuntu’ 와 ‘httpd’ 이미지 간의 차이점은 무엇일까???

### 이유는?

먼저, 아파치 서버 프로그램인 httpd를 pull 할 때, image안에 OS가 같이 있는 설치되어 있는 상태이다. 반대로, ubuntu는 docker hub에서 pull 받은 깡통 OS이다.

이 둘의 차이점은 OS 안에서 프로세스 상태인 단 하나의 프로그램이라도 있으면 docker가 정상적으로 하게 되는데, OS안에 하나의 프로그램이라도 동작하고 있지 않으면 docker안에서 run 명령어가 실행 되었다 바로 죽어버리는 상태로 바뀐다. 때문에, ubuntu를 pull 받은 후 바로 run 했을 때 docker가 정상적으로 run이 되고 있지 않았던 것이다.

아래 이미지는 위에 말을 뒷받침 해주는 이미지이다. container가 정적인 상태이다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/b8327339-db85-4f18-983a-3b799b50d7d4/Untitled.png)

### 그럼 깡통 OS를 run 시키는 방법은 아예 없나?

다른 방법이 있다.

### -dit

```sql
(변경전) docker run -d --name myubuntu ubuntu
(변경후) docker run -dit --name myubuntu ubuntu
```

1. **`d`** (detach): 이 명령어는 컨테이너를 백그라운드에서 실행하도록 지시한다. 컨테이너가 실행될 때 터미널을 차지하지 않고 동작하도록 한다. 이렇게 하면 컨테이너가 실행 중일 때 다른 작업이 가능하다.
2. **`i`** (interactive): 이 명령어는 표준 입력을 컨테이너에 연결한다. 이렇게 하면 컨테이너와 상호작용할 수 있으며, 컨테이너 내에서 명령을 입력하고 출력을 확인할 수 있다. 즉, 터미널과 컨테이너 간에 양방향 통신을 가능하게 하는 것이다.
3. **`t`** (tty-터미널): 이 플래그는 유사한 터미널을 제공하여 컨테이너 내에서 명령을 입력하고 출력을 보다 쉽게 확인할 수 있도록 한다. **`t`** 플래그를 사용하지 않으면 컨테이너 내의 출력이 혼란스럽게 표시될 수 있다.

따라서 `-dit`명령어를 사용하면 컨테이너를 백그라운드에서 실행하고, 표준 입력을 연결하여 상호작용 가능한 터미널 환경을 제공한다는 것이다. 이는 일반적으로 백그라운드에서 실행되는 서비스 컨테이너나 계속해서 실행 중인 프로세스를 실행하는 데 사용된다.

> -d 와 -dit 차이점.
**`-d`**는 컨테이너를 백그라운드에서 실행하고 터미널과의 연결을 유지하지 않는 반면, **`-it`**는 컨테이너를 대화형 모드로 실행하고 터미널과 상호작용할 수 있게 한다. 즉, -dit는 백그라운드로 실행되면서 터미널과 상호작용하게 만들어 주는 것이고, -d는 단순히 백그라운드로 실행시키는 것이다.
>

위에서 run 명령어를 한번 썼기 때문에 docker가 죽어있는 상태이다. 그래서 `docker rm myubuntu ubuntu` 를 입력해서 정적인 컨테이너를 삭제한 후 입력해주자.

```sql
docker run -dit --name myubuntu ubuntu
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/5661fe53-d936-4e1f-82a5-34ff984ec876/Untitled.png)

위에 이미지에서 살펴볼 점은 `COMMAND` 와 `NAMES` 이다.

- `NAMES` : 현재 우린 container의 이름을 myubuntu로 지어줬다. 이 `NAMES`는 docker compose를 통해 다른 container를 실행할 때 참고 할 수 있다.
  `—rink myubuntu` 를 통해 다른 컨테이너를 실행시킬 때 myubuntu를 참고해서 실행시킨다는 의미이다.
- `COMMAND` :  터미널모드로 `/bin/bash` 를 커맨드로 실행하여 죽지 않고 돌아가는 것이다. **이로 인해 MAC or Window or Linux에서 `bash` 라는 프로그램을 통해서 메세지를 전송할 수 있다. 즉, bash가 os에게 명령어를 내릴 수 있는 shell의 하나의 종류**

> **`/bin/bash`**는 리눅스 및 유닉스 기반 시스템에서 사용되는 Bourne Again Shell (Bash)의 실행 파일 경로, Bash는 가장 일반적으로 사용되는 셸(shell) 중 하나로, 사용자와 시스템 간의 상호 작용을 가능하게 한다.
>
>
> Bash는 다음과 같은 기능을 제공한다:
>
> 1. 명령어 해석: Bash는 사용자가 입력한 명령어를 해석하고 실행하는 역할을 한다. 사용자는 명령어를 입력하여 파일을 조작하거나 시스템 설정을 변경할 수 있다.
> 2. 스크립트 언어: Bash는 스크립트 언어로 사용될 수 있으며, 복잡한 작업을 자동화하거나 스크립트 파일을 작성하여 특정 작업을 수행할 수 있다.
> 3. 변수 및 제어 구조: Bash는 변수를 정의하고 사용하며, 조건문과 반복문과 같은 제어 구조를 제공하여 스크립트 작성을 지원한다.
> 4. 편리한 기능: Bash는 명령어 히스토리 관리, 자동 완성, 환경 변수 설정 및 사용자 정의 기능을 제공한다.
>
> **`/bin/bash`**는 시스템에서 실행되는 기본 shell로 사용되며, 사용자가 명령어를 입력하고 스크립트를 실행할 때 사용된다. Bash는 다양한 리눅스 및 유닉스 시스템에서 표준 shell로 사용된다.
>

그럼 진짜인지 확인 해봐야겠지? image를 통해 container에 접근해보자.

### attach

```sql
(linux)docker attach myubuntu
(window)winpty docker attach myubuntu
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/6ca3af20-0e26-4116-8d7a-78e9018b0367/Untitled.png)

* window 환경에서 진행했었음.

ubuntu OS에 접속한 것이다. 즉, docker 명령어는 사용하지 못하고 리눅스의 다양한 명령어나 스크립트를 작성하면 된다.

- 추가로 다시 나오려면 일반적으로 종료 명령은 **`Ctrl`** + **`P`**를 누르고, 그 다음 **`Ctrl`** + **`Q`**를 눌러서 컨테이너 내의 셸에서 빠져나올 수 있다.