이번 장에서는 클라우드 서비스를 이용하면서 보안과 관련해서 몇 가지 용어들이 등장 하는데, 이번 기회에 알아보고자 한다.

---

### VPC(Virtual Private Cloud)

- 퍼블릭 클라우드 상에서 제공되는 고객 전용 사설 네트워크로, **각각의 VPC는 논리적으로 완벽하게 분리되어 있어 다른 사용자의 네트워크와 상호 간섭이 발생하는 영역없이 사용 가능하다.** 예를 들어 아파트라는 네트워크 안에서 각각의 집을 VPC로 보면 된다.
- VPC의 IP 범위를 RFC1918에 명시된 사설 IP 주소 대역을 사용하여 구축해야한다. 사설 IP란 인터넷을 위해 사용하는 것이 아닌 Private하게 사용하는 아이피 주소를 말한다. 더 쉽게 이해해보면 옆집도 안방이 있고 우리집도 안방이 있다. 이 ‘안방’이라는 개념이 RFC1918에서 정의한 대역이라고 보면 된다.
    - **10.0.0.0/8 (10.0.0.0~10.255.255.255)** 내에서 /16~/28 범위
    - **172.16.0.0/12 (172.16.0.0~172.31.255.255)** 내에서 /16~/28 범위
    - **192.168.0.0/16 (192.168.0.0~192.179.255.255)** 내에서 /16~/28 범위
- VPC가 만들어 진 후 서브넷을 이용하여 VPC 내 네트워크 공간을 세분화 하여 사용이 가능하다. 뒤에서 설명하겠지만 서브넷은 VPC안에 있는 VPC보다 더 작은 단위이며, 각각의 서브넷은 가용영역안에 존재하며 서브넷안에 RDS, EC2와같은 리소스들을 위치시킬 수 있다.
- NACL을 이용하여 inbound/outbound 네트워크 트래픽을 Subnet 단위로 제어 가능하다.
- 한번 설정된 아이피대역은 수정할 수 없다.

---

### 서브넷(Subnet)

![스크린샷 2024-04-11 오후 10 18 13](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/5782ae99-b6a4-462c-bb31-3354eb9db5e0)

- VPC 네트워크 대역 공간을 세분화하여 사용한다.
- **인터넷 게이트웨이, NAT 게이트웨이용 서브넷을 별도로 생성하여 외부 인터넷과의 통신을 조절할 수 있다.**
    - **Public Subnet**
        - **VPC는 외부 서비스와의 통신이 기본적으로 안되는데 → 사설 IP 주소 대역을 사용하기 때문에, Public Subnet은 Internet gateway를 통해서 외부와의 통신이 가능하다.**

          ![스크린샷 2024-04-11 오후 10 29 27](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/4831788b-7e96-41ef-831b-d06989b20c2e)

    - **Private Subnet**
        - 기본적으로 외부와 차단되어 있다.
    - **인터넷 게이트웨이**
        - **인터넷 게이트웨이는 수평 확장되고 가용성이 높은 중복 VPC 구성 요소**로, VPC와 인터넷 간에 통신할 수 있게 해준다.
- NACL을 이용하여 inbound/outbound 네트워크 트래픽을 Subnet 단위로 제어가 가능하다.
    - **NACL**
        - Network Access Control List의 약자로 **VPC의 보안을 강화시키는 요소이다.**
        - 하나의 서브넷은 하나의 NACL만 연동 가능, 단 하나의 NACL은 여러 서브넷에 연동 가능하다.(1:N)

  | ACG (Access Control Group) | NACL (Network ACL) |
  | --- | --- |
  | 서버 NIC 단위로 적용 | Subnet 단위로 적용 |
  | Allow 규칙에 한하여 지원 | Allow, Deny 규칙 모두 지원 |
  | Stateful: Response 트래픽 자동 허용 | Stateless: Response 트래픽에 대한 Allow 규칙이 추가적으로 필요, 들어오는 트래픽과 나가는 트래픽을 구분하지 않는다. 들어오는 트래픽과 나가는 트래픽을 구분하지 않음, 즉 일반적으로 Outbound에 임시 포트 범위를 열어 주어야 정상적으로 통신 가능하다. |
  | 모든 규칙을 확인하여 판단 | 우선순위에 따라 규칙을 반영 |

쉽게 말해, 아래 사진을 보면 된다.

![스크린샷 2024-04-11 오후 10 31 19](https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/ab081a5b-a04c-4336-ab9a-e22bd4085eca)

---

### ACG(Access Control Group)

ACG는 **서버 간** 네트워크의 접근을 제어하고 관리할 수 있는 IP/Port 기반 필터링 방화벽 서비스이다.
**즉, 서버의 방화벽 역할을 한다.** 그래서 서버로 접근하는 IP/Port에 대해 Inbound/Outbound 룰 설정 가능하며,  NCP 같은 경우 default rule은 Inbound deny, Outbound Allow이다. 또, 프로토콜 별로 보안 설정이 가능하다.( TCP, UDP, ICMP 중)

<img width="267" alt="스크린샷 2024-04-13 오후 4 50 06" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/43e4e228-0875-4fbf-a12b-bc254a237f9f">

위 그림에서 보다시피 Subnet을 통해 네트워크 레벨에서 NACL을 통해 보안을 1차적으로 걸어 준 후 서버로 Inbound/OutBound 할 때 추가로 ACG 보안을 걸어 두번의 보안 절차를 거치게 되는 것이다.

---

### VGW(Virtual Private Gateway)

간단하게 VGW는 VPC에 위치하여 On-premise 네트워크를 Cloud Connect 또는 IPsec VPN으로 연결하는 접점이다.

다시 말해, 프라이빗 가상 인터페이스를 통해 같은 리전 또는 다른 리전에 있는 계정의 VPC(하나 이상)에 연결할 수 있다.

네트워크 간 보안이 확보된 경로로 통신할 수 있고, 하이브리드 클라우드도 구성할 수 있다.

통신 회선의 이중화가 가능하여 2개의 IPsec VPN 터널 또는 IPsec VPN과 Cloud Connect 터널 조합을 이용할 수 있다.

<img width="479" alt="스크린샷 2024-04-13 오후 4 54 53" src="https://github.com/sungwooIsGood/Today-I-Learn/assets/98163632/027b50a8-f9f8-4904-92b5-7c5ba37f7260">
