# Gunicorn과 Uvicorn 정리

## Gunicorn

- Python WSGI/ASGI 애플리케이션을 실행하기 위한 웹 서버
- 멀티 프로세스 기반
- 각 워커(worker)는 독립된 프로세스
  - GIL 때문에 python 스레드를 여러 개 만들어도 CPU 코어를 동시에 쓰지는 못함
- Uvicorn과 함께 사용할 때는 UvicornWorker를 통해 ASGI 실행을 Gunicorn이 감싼 구조 즉, Gunicorn이 마스터 프로세스 역할을 함

### 특징
- 프로세스 단위 확장 (CPU 코어 수만큼 병렬 처리 가능)
- 안정성 중심
- 동시성은 워커 수에 의해 결정됨
- 비동기 기능은 없고, ASGI 실행은 UvicornWorker에게 위임

---

## Uvicorn

- ASGI 서버
- 이벤트 루프 기반 비동기 처리
- 1개의 워커는 1개의 프로세스이며, 내부에 이벤트 루프 1개가 존재
  - 때문에, 이벤트 루프를 통해 여러 코루틴을 스케줄링하여 동시성 처리
- 네트워크 I/O 완료 이벤트는 OS 커널이 이벤트 루프에 전달하여 suspend 상태의 메서드를 다시 실행하도록 함

### 특징

- 비동기(AsyncIO) 기반 동시성 처리
- CPU 병렬성은 없음 (GIL 영향)
- 동시성은 코루틴 수에 의해 결정됨
- 프로세스 관리 기능은 없음(워커 모니터링, 재시작 등)

---

## Gunicorn + Uvicorn 조합 구조 -> 실제 FastAPI로 프로젝트를 운영하면서 두 가지를 조합해서 사용 중

- Gunicorn이 멀티 프로세스 관리 담당
- 각 프로세스(worker)는 Uvicorn을 실행
- Uvicorn이 ASGI 애플리케이션(FastAPI 등)을 실제로 처리
  
---

# 요청 시 흐름

## 단독 Uvicorn 구조

```
[Client Request]
        ↓
[Uvicorn Worker Process]
        ↓
[Event Loop]
        ↓
[Coroutine Task 실행]
        ↓
I/O 발생 시 → OS 커널에 I/O 요청
I/O 완료 시 → 커널이 이벤트 루프에 이벤트 전달
        ↓
[코루틴 재개 후 응답 반환]

```

---

## Gunicorn + Uvicorn 구조

```
                ┌──────── Worker 1 (Uvicorn)
                │        └─ Event Loop
[Gunicorn] ─────┼──────── Worker 2 (Uvicorn)
  (Process      │        └─ Event Loop
  Manager)      └──────── Worker 3 (Uvicorn)
                         └─ Event Loop

```
