### EC2의 구조

![Image](https://github.com/user-attachments/assets/a3c50802-4503-4123-b031-2b1626be1b8d)
### EC2 인스턴스란?

- EC2에서 컴퓨팅을 담당
    - 다양한 유형과 크기로 구성
    - 저장을 담당하는 EBS와 네트워크로 연결
- 저장 방법에 따라 두 가지로 분류
    - **EBS 연동**

      ![Image](https://github.com/user-attachments/assets/4d2df944-025c-48bb-850a-afdeb03eae26)
        - 하드디스크 연동
    - **인스턴스 스토어**

      ![Image](https://github.com/user-attachments/assets/e962cddd-486c-43b7-b7e5-1dd39c2f18cc)
        - 인스턴스 안 스토리지 활용
    - **하나의** 가용영역에 존재한다.

### 인스턴스 유형(패밀리)

- 인스턴스의 역할에 따라 CPU, 메모리, 스토리지, 네트워크 등을 조합한 구성
- 각 인스턴스 유형 별로 사용 목적에 따라 최적화
    - ex) 메모리 위주, CPU위주, 그래픽 카드 위주 등
- 유형 별로 이름 존재
    - ex) t유형, m유형, inf유형 등
        - 같은 유형의 인스턴스들을 인스턴스 패밀리라 부름
    - 타입 별 세대별로 숫자 부여
        - ex) m5 = m인스턴스의 5번째 세대
- 아키텍쳐 및 프로세서/추가기술에 따라 접미사가 붙는다.
    - ex) c7gn = c인스턴스 중 AWS Graviton 프로세서를 사용(g) + Network Optimized(n) ⇒ c7gn

![Image](https://github.com/user-attachments/assets/7a98eaa0-0585-4806-8495-3db289dd64e8)
### 인스턴스 크기

- 같은 인스턴스 패밀리에서 다양한 크기가 존재한다.
- 인스턴스의 cpu갯수, 메모리 크기, 성능 등으로 크기 결정
- 크기가 클 수록
    - 더 많은 메모리
    - 더 많은 CPU
    - 더 많은 네트워크 대역폭
    - EBS와의 통신 가능한 대역폭

![Image](https://github.com/user-attachments/assets/882526cb-db9f-47f9-a608-4498133ad28e)