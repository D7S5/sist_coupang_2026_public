# Vendor 서블릿 분석

`com.goodpang.servlet` 패키지의 `Vendor`로 시작하는 서블릿 4개를 분석한 내용입니다.
판매자(셀러)의 **가입 → 로그인 → 추가 서류제출(입점심사) → 로그아웃** 흐름을 담당합니다.


## 1. `VendorSignupServlet` — `/vendor/signup`

**역할**: 판매자 회원가입(1단계). `vendor-signup.jsp`(`WEB-INF/views/`) 폼을 받아서 `SELLER` 테이블에 신규 행을 만듭니다.

- `doGet`: 그냥 가입 폼으로 forward
- `doPost` 처리 순서:
  1. 필수값 전부 입력됐는지 확인
  2. 비밀번호 확인 일치 + 8자 이상 검사
  3. `bizType`("individual"/"corporate") → 한글("개인사업자"/"법인사업자")로 변환
  4. **이메일 중복 검사** (`dao.existsByEmail`)
  5. **사업자등록번호 중복 검사** (`dao.existsByBusinessNo`) — 한 사업자번호로 중복 입점을 막는 용도
  6. `BCrypt.hashpw()`로 비밀번호 해시 후 `SellerDAO.insertSeller()` 호출
  7. 성공하면 `index.jsp`로 리다이렉트, 실패하면 에러 메시지와 함께 폼으로 돌아감

이 단계에서 만들어지는 판매자는 아직 "입점 대기" 상태로, 사업장 주소/서류 등은 이 시점엔 안 받고 다음 서블릿(`VendorBusinessInfoServlet`)에서 추가로 받습니다.


## 2. `VendorLoginServlet` — `/vendor/login`

**역할**: 판매자 로그인. `vendor-login.jsp`로 forward하고, 로그인 성공 시 세션에 판매자 정보를 담습니다.

핵심 로직:
- `SellerDAO.findByEmail()`로 조회 후 `BCrypt.checkpw()`로 비밀번호 검증 — 평문 비교가 아니라 해시 비교라 안전한 방식입니다
- **승인 상태(입점대기/심사중/승인/반려)와 무관하게 로그인 자체는 허용** — 상태별 안내는 로그인 시점이 아니라 `vendor_dashboard.jsp`에서 분기 처리하도록 역할을 나눠놨어요. (로그인은 "신원 확인"만, "권한/상태별 화면 분기"는 대시보드 몫 — 관심사 분리가 잘 되어 있습니다)
- 세션에 `loginSeller`(DTO 전체), `sellerNo`, `storeName`을 각각 담고, 세션 만료를 30분으로 설정
- **로그인 후 리다이렉트 처리가 눈에 띕니다**: `session.getAttribute("vendorRedirectAfterLogin")`가 있으면 거기로, 없으면 기본값으로 `vendor_dashboard.jsp`로 보냅니다. 이건 "로그인 안 된 채로 어떤 보호된 페이지에 접근했다가, 로그인 화면으로 튕겨나갔다가, 로그인하면 원래 가려던 페이지로 다시 보내주는" 패턴이에요 — 실제로 `VendorBusinessInfoServlet.doGet()`이 미로그인 시 `/vendor/login`으로 그냥 리다이렉트만 하고 있어서, 이 값을 실제로 세팅해주는 부분은 아직 다른 곳(로그인 필요 페이지들)에 구현돼 있어야 완성되는 구조로 보입니다.


## 3. `VendorLogoutServlet` — `/vendor/logout`

**역할**: 가장 단순한 서블릿. `session.invalidate()`로 세션을 통째로 없애고 `/vendor/login`으로 리다이렉트. `doPost`는 `doGet`을 그대로 호출해서 GET/POST 둘 다 동일하게 동작합니다.


## 4. `VendorBusinessInfoServlet` — `/vendor/business-info`

**역할**: 가입 직후 "입점 대기" 상태인 판매자가 **사업장주소, 통신판매업신고번호, 대표카테고리, 정산계좌, 서류(사업자등록증/통신판매신고증)**를 추가 입력하는 2단계 페이지. 제출하면 `SELLER.approval_status`가 "심사 중"으로 바뀝니다.

주목할 점들:
- `@MultipartConfig(maxFileSize = 5MB, maxRequestSize = 15MB)` — 파일 업로드를 받는 서블릿이라 별도 설정 필요. "파일 업로드는 `<input type="file">` + `multipart/form-data` + `@MultipartConfig`가 필요하다"는 내용이 실제로 구현되어 있는 예시입니다.
- 로그인 여부를 `doGet`/`doPost` **둘 다에서** 각각 검사 (`session.getAttribute("loginSeller") == null`이면 로그인 페이지로) — 로그인 안 하고 이 URL에 직접 접근하는 걸 막음
- `saveUploadedFile()` 메서드가 실제 파일 업로드 처리를 담당:
  - `part.getSize() == 0`이면(파일 첨부 안 함) **기존 URL을 그대로 반환** — "재제출할 때 서류를 다시 안 올려도 이전에 올린 서류가 유지되게" 하는 배려
  - 원본 파일명 대신 `UUID.randomUUID() + 확장자`로 새 이름 생성 — "다른 판매자가 같은 파일명 올려도 안 덮어써지게" 하는 방식
  - `getServletContext().getRealPath("/upload")`로 실제 저장 경로를 구해서 저장 (`pds/` 대신 여기선 `upload/` 폴더를 씀)
- **최초 제출 시엔 서류 첨부가 둘 다 필수**(주석에 명시됨) — `businessCertUrl`/`mailOrderCertUrl` 둘 중 하나라도 null이면 에러 처리
- 저장 성공하면 `dao.findByEmail()`로 **세션의 로그인 정보를 최신 상태로 다시 조회해서 갱신** — `approval_status`가 바뀐 걸 세션에도 반영해야 대시보드에서 최신 상태를 보여줄 수 있기 때문 (이걸 안 하면 세션엔 여전히 "입점대기" 상태로 남아서 화면이 안 맞게 됨 — 흔히 놓치기 쉬운 부분인데 잘 처리돼 있음)


## 전체 흐름 정리

```
VendorSignupServlet (가입, 기본정보만)
        ↓ 성공
   index.jsp로 이동, 이후 로그인
        ↓
VendorLoginServlet (로그인, 세션 생성)
        ↓
VendorBusinessInfoServlet (사업자 서류·계좌 추가 입력 → 심사 중 상태로)
        ↓
   vendor_dashboard.jsp (승인 상태별로 화면 분기)

VendorLogoutServlet (세션 종료, 아무 때나 호출 가능)
```

가입 때는 계정 정보만 받고, 로그인 후 별도 단계에서 서류/계좌 등 민감한 추가 정보를 받는 **2단계 온보딩 구조**이고, 비밀번호는 BCrypt로 해시, 파일은 UUID 이름으로 저장하는 등 실무에서 흔히 쓰는 패턴들이 잘 적용되어 있습니다.
