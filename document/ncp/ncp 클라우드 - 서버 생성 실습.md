# ncp 클라우드 - 서버 생성 실습

지난 4월 5일 NCP Hands ON 교육 과정을 다녀오고 다시 실습 해보면서 적은 내용이다. 작은 스타트업 개발자로써 성장해가며 회사에서 인프라까지 슬슬 넓혀가며 공부해보고 싶어 다녀오게 되었다. 

---

클라우드에 대해 먼저 용어 정리가 필요하기에 몇가지 용어를 먼저 간단하게 짚고 가보자.

- **VPN**
    - 가상 사설망으로 사용자가 사설망에 연결된 것처럼 인터넷에 액세스할 수 있도록 하는 인터넷 보안 서비스이다. 예를 들어, 네트워크A와 네트워크B가 실제로 같은 네트워크상에 있지만 논리적으로 다른 네트워크인 것처럼 동작한다.
- **VPC**
    - 기업이 공유된 퍼블릭 클라우드 인프라에 프라이빗 클라우드와 같은 자체 컴퓨팅 환경을 구축할 수 있는 퍼블릭 클라우드 서비스이다.
        - 퍼블릭 클라우드란 클라우드 제공업체가 여러 고객에게 제공하는 클라우드 서비스를 말한다.
        - 프라이빗 클라우드란 다른 조직과 공유되지 않는 클라우드 서비스이다.
- **Tenant(테넌트)**
    - 서비스 제공자의 클라우드 리소스를 사용하는 자를 테넌트라고 한다.
- **Subnet**
    - 서브 네트워크는 네트워크 내부의 네트워크로 네트워크를 운영중인 서비스의 규모에 맞게 분할하여 사용하기 위한 기술이다.
- **ACG**
    - 서버 간 네트워크의 접근을 제어하고 관리할 수 있는 IP/Port 기반 필터링 방화벽 서비스이다. ACG를 이용하면 기존 방화벽(iptables, ufw, Windows 방화벽)을 개별적으로 관리할 필요 없이 서버 그룹에 대한 ACG 규칙을 손쉽게 설정하고 관리할 수 있다.
    - **Inbound 규칙** : 서버로 들어오는 트래픽(외부→내부)에 대한 규칙
    - **Outbound 규칙** : 서버에서 나가는 트래픽(내부→외부)에 대한 규칙
- **NACL**
    - 서브넷 단위 보안 그룹처럼 인바운드 및 아웃바운드 트래픽을 제어하는 가상 방화벽 역할을 담당한다. 인스턴스 단위로는 제어가 불가능하며 다양한 서브넷에 연동이 가능하다. 또, Stateless 성질을 가지고 있어 요청 정보를 따로 저장하지 않기 때문에 응답하는 트래픽에 대한 필터링을 설정 해주어야 한다.
    -

이제 직접 서버를 만들어보면서 인프라에 대해 조금씩 알아보자.

---

### 서버 생성 및 접속

<img width="268" alt="스크린샷 2024-04-05 오후 1 40 44" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/957036f8-ad0e-4e78-9a1b-4ca162633ddc">

1. 웹 서버를 위한 VPC
    1. Product & Services → VPC → VPC Management → VPC 생성

       <img width="698" alt="스크린샷 2024-04-08 오후 7 52 24" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/e7b342cd-4dba-4044-b4eb-95db36141a3b">

    2. Product & Services → VPC → Network ACL →  ACL Rule → Network ACL 생성

       <img width="799" alt="스크린샷 2024-04-08 오후 7 54 49" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/71cab18f-7fa1-45df-b265-6ce34abc3747">

    3. NACL을 생성했다면 다음 NACL의 보안의 Rule을 정해야한다.
        1. 만든 NACL을 클릭한 후 ‘Rule 설정’을 진행한다.
            1. ip대역은 아래와 같이 세가지 중 쓰면 되고, 16~28번 범위 내로 설정하면 된다.
            2. (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16)
        2. Inbound 규칙
            1. 우선순위: 0, 프로토콜: ICMP, 접근 소스: 0.0.0.0/0, 허용여부: 허용 선택 후, “+추가” 클릭
            2. 우선순위: 1, 프로토콜: TCP, 접근 소스: 0.0.0.0/0, 포트: 80, 허용여부: 허용 선택 후, “+추가” 클릭
            3. 우선순위: 2, 프로토콜: TCP, 접근 소스: myIp, 포트: 22, 허용여부: 허용 선택 후, “+추가” 클릭
            4. 우선순위: 197, 프로토콜: TCP, 접근소스: 0.0.0.0/0, 포트: 22, 허용여부: 차단 선택 후, “+추가” 클릭

       <img width="963" alt="스크린샷 2024-04-08 오후 7 57 52" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/2df78e14-03a1-4caf-aed0-f5b8e7bffcc3">

        1. Outbound 규칙
            1. 우선순위: 0, 프로토콜: ICMP, 접근 소스: 0.0.0.0/0, 허용여부: 허용 선택 후, “+추가” 클릭
            2. 우선순위: 1, 프로토콜: TCP, 접근 소스: 0.0.0.0/0, 포트: 1-65535, 허용여부: 허용 선택 후, “+추가” 클릭
        2. 1-65535 처럼 포트의 범위를 지정할 수 있다.
    4. 우선순위: 2, 프로토콜: UDP, 접근 소스: 0.0.0.0/0, 포트:1-65535, 허용여부: 허용 선택 후, “+추가” 클릭

       <img width="969" alt="스크린샷 2024-04-08 오후 7 59 04" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/0d9e12da-25e8-49a2-9292-629f77d112c5">


1. 웹 서버를 위한 Subnet
    1. Product & Services → VPC →  Subnet Management 선택 → Subnet 생성 선택

       <img width="681" alt="스크린샷 2024-04-08 오후 8 03 55" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/b2dd0fc2-577d-4d65-a2e6-6b2f7a25245c">


1. 웹 서버 ACG 만들기
    1. Product & Services → Server → ACG 선택 → ACG 생성 선택

       <img width="695" alt="스크린샷 2024-04-08 오후 8 06 34" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/649b1643-79b1-45d2-ab39-8d48de4ac7f5">

    2. ACG를 생성 해주었다면 ACG 설정을 추가로 해주어야 한다. → ACG를 선택한 후 ‘ACG 설정’ 선택
        1. **Subnet은 net에 접속하기 전 보안**으로 Inbound 및 Outbound 설정을 해주어서 우선순위, 프로토콜, 허용할 접근 IP, 포트, 허용 여부를 판단해서 적어 주면 된다.
            1. 우선 순위 설정을 통해 inbound 및 outbound 설정의 우선 접근 권한을 가질 수 있다.
        2. Inbound 규칙 설정
            1. 프로토콜 : ICMP, 접근 소스 : 0.0.0.0/0 입력 후, “+추가” 클릭
            2. 프로토콜 : TCP, 접근 소스 : 0.0.0.0/0 허용 포트 (서비스) : 80 입력 후, “+추가” 클릭
            3. 프로토콜 : TCP, 접근 소스 : myIp 허용 포트 : 22 선택 후, “+추가” 클릭

               <img width="949" alt="스크린샷 2024-04-08 오후 8 08 47" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/9177f11f-2714-40a2-b75c-990ca6946fc1">

        3. Outbound 규칙 설정
            1. 프로토콜 : ICMP, 목적지 : 0.0.0.0/0 입력 후, “+추가” 클릭
            2. 프로토콜 : TCP, 목적지 : 0.0.0.0/0 허용 포트 : 1-65535 입력 후, “+추가” 클릭
            3. 프로토콜 : UDP, 목적지 : 0.0.0.0/0 허용 포트 : 1-65535 입력 후, “+추가” 클릭

               <img width="942" alt="스크린샷 2024-04-08 오후 8 09 58" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/93e8e769-9dc2-4a2f-91ae-33262c47363b">


1. 초기화 스크립트 만들기
    1. Product & Services → Server → Init Script → Script 생성
        1. 아래 스크립트의 내용은 서버 부팅 후 아파치 웹서버와 PHP를 설치하고 테스트 페이지를 다운받은 후, 설정 내용을 수정 후 아파치 웹서버를 기동하는 스크립트이다.
        2. 스크립트를 잘 설정해두면 서버의 이미지라든지 서버 생성 후 초기화 작업을 쉽게 가져갈 수 있다.

       <img width="972" alt="스크린샷 2024-04-08 오후 8 11 18" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/287a1fd2-a245-4bb2-8741-8342ef234d60">


1. 웹서버 만들기
    1. Product & Services → Server → 서버생성 클릭
        1. OS타입 Rocky이며, 하이퍼바이저 KVM으로 선택 후 rocky linux-8.8을 선택

           <img width="1116" alt="스크린샷 2024-04-08 오후 8 13 57" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/db8343ba-df24-4b82-a173-d6f5354ff3d2">

    2. VPC와 Subnet은 앞에 만든 것으로 선택
    3. 서버 타입”은 Standard ,vCPU 2개, 메모리 8GB, 디스크 50GB
    4. 서버 개수는 1, 서버 이름은 lab-org 를 입력
    5. Network Interface는 new interface 선택, IP는 10.0.1.101 을 입력 후, +추가 클릭
    6. Script 선택에서 앞에서 저장한 lab-script 를 선택, 다음  클릭

       ![스크린샷 2024-04-08 오후 8 16 32](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/855ab936-83ff-430d-82ce-ae4608ceda09)

    7. 인증키 설정과 관련해서는 새로운 인증키를 받아야한다. ‘새로운 인증키 생성’을 클릭 `.pem` 파일을 받아볼 수 있다.

       ![스크린샷 2024-04-08 오후 8 18 35](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/2379d832-8b64-4f71-988c-10ff5fb01c5c)

    8. 네트워크 접근 설정에서 디바이스 eth0 에 lab-org를 선택, ACG는 네트워크 디바이스마다 할당이 가능하며, 1개의 네트워크 디바이스 당 최대 3개의 ACG 매핑 가능하다.

       <img width="638" alt="스크린샷 2024-04-08 오후 8 19 17" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/76c8cd64-6e62-4125-a556-498812483c75">

    9. 다음을 누른 후 ‘서버 생성’을 클릭하면 서버가 만들어 진다. 서버가 만들어지는데 시간이 꽤 걸린다.

1. 공인 IP 설정 (위에서 언급은 안했지만 서버 생성 시 할당할 수도 있음.)
    1. Server → Public IP 선택
        1. 이 때, 서버가 만들어져야지만 IP를 생성할 수있으니 좀 기다리다가 생성하면 된다.

           <img width="667" alt="스크린샷 2024-04-08 오후 8 41 26" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/34849969-baaa-4110-b569-380e228eaa10">

            <img width="1114" alt="스크린샷 2024-04-08 오후 8 41 43" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/27b1efa0-6142-4bcc-b241-b964f58e2252">
            
한번 제대로 만들어 졌는지 접속해보자.

<img width="375" alt="스크린샷 2024-04-08 오후 8 42 34" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/236a8da5-0620-40e1-b933-d7ce15df8640">

클라우드 서버를 직접 만들어 보았으며, 다음에는 로드밸런서, DNS 설정 오토 스케일링, Cloud for DB 중 하나를 만들어 볼 예정이다.

백엔드 개발자지만 인프라에 친숙해지는 시간이 되었기를…
