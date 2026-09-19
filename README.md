# GoodPang

쿠팡의 주요 쇼핑 경험을 참고해 구현한 이커머스 웹 프로젝트입니다.  
이 문서는 프로젝트 전체 개요와 함께 **주문·결제·개인정보 관리** 담당 기능을 중심으로 정리합니다.

## 프로젝트 소개

GoodPang은 회원이 상품을 장바구니에 담고 주문·결제한 뒤, 주문 내역과 개인정보를 관리할 수 있는 쇼핑몰 서비스입니다. 구매자 기능 외에도 상품, 판매자, 배송, 관리자 기능을 함께 제공합니다.

## 담당 기능

### 1. 주문

- 장바구니 상품을 결제 화면으로 전달
- 주문서에서 상품, 수량, 배송지, 할인 및 최종 결제 금액 확인
- 주문과 주문 상세 데이터 생성
- 동일한 결제 요청의 중복 처리 방지
- 주문 완료 화면 및 주문 내역 조회
- 주문 상세 정보와 배송 현황 조회

### 2. 결제

- 카드와 계좌 결제수단 등록
- 등록된 결제수단 목록 조회
- 기본 결제수단 지정
- 결제수단 입력값 검증
- 회원 소유 결제수단 여부 확인
- 주문 금액과 결제 정보 검증 후 결제 처리
- 결제 완료 주문의 중복 결제 차단

실제 PG사 연동 대신 등록된 결제수단을 이용해 결제 흐름을 구현했습니다. 카드번호와 계좌번호는 화면에서 마스킹하여 표시합니다.

### 3. 개인정보

- 회원 이메일 및 휴대전화 번호 수정
- 로그인 세션의 회원정보 동기화
- 배송지 목록 조회, 추가 및 수정
- 본인 확인 후 회원 탈퇴
- 와우 멤버십 상태에 따른 탈퇴 제한
- 로그인 사용자 기준의 데이터 접근 제어

## 주요 화면 및 URL

| 구분 | Method | URL | 설명 |
|---|---|---|---|
| 주문서 | GET | `/order/payment?checkoutNo={번호}` | 결제할 상품과 배송지, 금액 확인 |
| 결제 처리 | POST | `/order/checkout` | 주문·주문상세 생성 |
| 주문 완료 | GET | `/order/complete?orderNo={번호}` | 완료된 주문 정보 표시 |
| 주문 목록 | GET | `/order/order_list` | 로그인 회원의 주문 내역 조회 |
| 주문 상세 | GET | `/order/order_detail?orderNo={번호}` | 주문·배송·결제 상세 조회 |
| 결제수단 목록 | GET | `/payment-method/list` | 등록된 결제수단 조회 |
| 결제수단 등록 | GET/POST | `/payment-method/add` | 카드 또는 계좌 등록 |
| 기본 결제수단 | POST | `/payment-method/default` | 기본 결제수단 변경 |
| 회원정보 수정 | POST | `/member/update` | 이메일 또는 휴대전화 번호 변경 |
| 배송지 목록 | GET | `/address/list` | 회원 배송지 조회 |
| 배송지 추가 | GET/POST | `/address/add` | 신규 배송지 등록 |
| 배송지 수정 | GET/POST | `/address/edit` | 기존 배송지 변경 |
| 회원 탈퇴 | GET | `/member/withdraw` | 탈퇴 전 본인 확인 |
| 탈퇴 완료 | POST | `/member/withdraw/complete` | 회원 상태 변경 및 세션 종료 |

> 모든 URL 앞에는 배포된 애플리케이션의 컨텍스트 경로가 붙습니다. 로컬 기본 경로는 `/GoodPang`입니다.

## 주문·결제 처리 흐름

```text
장바구니
   ↓
결제 준비(CHECKOUT)
   ↓
주문서 조회 및 배송지·결제수단 선택
   ↓
결제 정보와 중복 주문 검증
   ↓
주문 생성 → 주문 상세 저장
   ↓             
COMMIT
   ↓
주문 완료 및 주문 내역 조회
```

## 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Java 21, Jakarta Servlet |
| Frontend | JSP, JSTL, HTML, CSS, JavaScript |
| Database | Oracle Database, JDBC |
| Web Server | Apache Tomcat 10.1 |
| IDE / Project | Eclipse Dynamic Web Project |
| Version Control | Git, GitHub |

## 프로젝트 구조

```text
sist_coupang_2026/
├── project/GoodPang/                 # 메인 웹 애플리케이션
│   ├── src/main/java/com/goodpang/
│   │   ├── servlet/                  # 요청 처리 및 화면 이동
│   │   ├── dao/                      # 주문·회원·결제 DB 처리
│   │   ├── dto/                      # 계층 간 데이터 전달 객체
│   │   └── util/                     # DB 연결, 로그인, 입력값 검증
│   └── src/main/webapp/
│       ├── WEB-INF/views/            # 직접 접근을 제한한 JSP
│       ├── css/                      # 화면별 스타일
│       ├── js/                       # 화면 동작 및 입력 검증
│       └── *.jsp                     # 사용자 화면
├── GoodPang/                         # 배송 조회 Android 앱
├── docs/                             # 설계 문서와 SQL, 화면 자료
└── cicd/                             # 배포 관련 문서 및 스크립트
```

## 핵심 구현 파일

### 주문

- `servlet/OrderServlet.java`: 주문서 조회
- `servlet/OrderPaymentServlet.java`: 결제 검증 및 주문 트랜잭션 처리
- `servlet/OrderListServlet.java`: 회원별 주문 목록
- `dao/OrderDAO.java`: 주문 생성, 주문 상세 저장

### 결제

- `servlet/PaymentMethodListServlet.java`: 결제수단 목록
- `servlet/PaymentMethodAddServlet.java`: 결제수단 등록
- `servlet/PaymentMethodDefaultServlet.java`: 기본 결제수단 설정
- `dao/PaymentMethodDAO.java`: 결제수단 CRUD
- `util/PaymentMethodValidator.java`: 결제수단 입력값 검증

### 개인정보

- `servlet/MemberUpdateServlet.java`: 이메일·휴대전화 번호 수정
- `servlet/MemberWithdrawServlet.java`: 회원 탈퇴 진입
- `servlet/MemberWithdrawCheckServlet.java`: 탈퇴 전 본인 확인
- `servlet/MemberWithdrawCompleteServlet.java`: 탈퇴 처리 및 세션 종료
- `servlet/AddressListServlet.java`: 배송지 목록
- `servlet/AddressAddServlet.java`: 배송지 추가
- `servlet/AddressEditServlet.java`: 배송지 수정
- `dao/MemberDAO.java`: 회원정보 수정 및 탈퇴
- `dao/AddressDAO.java`: 배송지 데이터 처리

## 실행 환경

1. Eclipse에서 `project/GoodPang`을 **Existing Projects into Workspace**로 가져옵니다.
2. Java 21과 Apache Tomcat 10.1 Runtime을 연결합니다.
3. Oracle JDBC 드라이버를 Tomcat 또는 프로젝트 빌드 경로에 등록합니다.
4. `META-INF/context.xml`의 `jdbc/myoracle` JNDI DataSource를 실행할 Oracle 환경에 맞게 설정합니다.
5. Tomcat에 프로젝트를 추가하고 서버를 실행합니다.
6. 브라우저에서 `http://localhost:8080/GoodPang/`으로 접속합니다.

## 보안 및 데이터 정합성

- 로그인하지 않은 사용자는 주문·결제·개인정보 기능을 사용할 수 없습니다.
- 주문과 결제수단 조회 시 로그인 회원 번호를 함께 검증합니다.
- 주문 생성, 주문 상세 저장, 동일한 DB 연결과 트랜잭션을 사용합니다.
- 회원 탈퇴 전 본인 확인 결과를 세션으로 검증합니다.
- 개인정보와 결제정보는 화면 노출 시 필요한 범위만 표시합니다.

## 향후 개선 사항

- PG사 테스트 결제 API 연동
- 비밀번호 및 민감 결제정보 암호화 정책 강화
- CSRF 토큰 적용과 서버 측 입력 검증 확대
- 주문·결제 단위 테스트 및 통합 테스트 추가
- 환경별 DB 설정 분리 및 비밀정보 환경변수 관리
