# 관리자(/admin) URL 목록

프로젝트 전체에서 `/admin`으로 시작하는 URL을 정리한 문서입니다. (2026-08-31 갱신)

## 관리자 로그인 (2026-08-31 추가)

`AdminAuthFilter`(`urlPatterns = "/admin/*"`)가 `/admin/login`을 제외한 모든 `/admin` 하위 URL을
세션의 `loginAdmin` 속성으로 가드합니다. 로그인 안 된 상태로 접근하면 요청 URL을
`adminRedirectAfterLogin`에 저장해두고 `/admin/login`으로 리다이렉트하며, 로그인 성공 시 그 URL로
돌아갑니다. 회원(`LoginUtil`/`redirectAfterLogin`)·판매자(`VendorAuthFilter`/`vendorRedirectAfterLogin`)
로그인 흐름과 동일한 패턴이고, 세션 키만 겹치지 않게 분리했습니다.

- 계정은 기존에 만들어져 있던 `ADMIN` 테이블(`admin_no`/`admin_id`/`admin_pw`/`admin_name`/`email`/`tel`)을
  그대로 사용합니다. 로그인 아이디는 이메일이 아니라 `admin_id`입니다.
- ⚠ `old_project/기본_Data_Insert.md`에 있던 시드 데이터(`admin_id='admin'`, `admin_pw='admin1234'`)는
  평문으로 들어가 있어서 `BCrypt.checkpw`로 검증이 안 됩니다. 아래 마이그레이션 SQL로 먼저
  BCrypt 해시로 바꿔야 로그인이 됩니다. (해시값은 `admin1234`를 그대로 해시한 것이라 로그인 자격
  증명은 바뀌지 않습니다.)

```sql
UPDATE ADMIN
SET admin_pw = '$2a$10$Qb/sAIeeYPChj0ZI0CSjkOwscE29NNExUkEaTFrWYiUjx.jb0COnO'
WHERE admin_id = 'admin';

COMMIT;
```

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/login` | GET | `AdminLoginServlet` | `admin-login.jsp` | 로그인 폼 표시 (이미 로그인된 상태여도 `AdminAuthFilter` 검사 대상에서 제외되므로 그대로 표시됨) |
| `/admin/login` | POST | `AdminLoginServlet` | `admin-login.jsp` (실패 시) | 로그인 처리 — `admin_id` + 비밀번호를 `AdminDAO.findByAdminId` + `BCrypt.checkpw`로 검증, 성공 시 세션에 `loginAdmin`/`adminNo`/`adminName` 저장 후 `adminRedirectAfterLogin`(없으면 `/admin/dashboard`)로 이동 |
| `/admin/logout` | GET/POST | `AdminLogoutServlet` | - | 세션 invalidate 후 `/admin/login`으로 리다이렉트 |

⚠ 아래 표(판매자/상품/배송 관련)의 URL들은 이전에는 관리자 로그인/인증 체크가 없었지만, 이제
`AdminAuthFilter`를 통해 전부 보호됩니다.

## 판매자 관련

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/sellers` | GET | `SellerListServlet` | `admin-seller-list.jsp` | 판매자 목록 조회 (입점심사 상태 확인용) |
| `/admin/seller-detail` | GET | `SellerDetailServlet` | `admin-seller-detail.jsp` | 판매자 상세정보 (목록에서 클릭 시) |
| `/admin/seller-approve` | POST | `SellerApprovalServlet` | - | 판매자 입점심사 승인/반려 처리 (`action=approve`/`reject`, 반려 시 `rejectReason` 저장) |

## 상품 관련

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/products` | GET | `AdminProductListServlet` | `admin-product-list.jsp` | 상품 목록 조회 ('승인 대기' 상품이 위로 정렬됨) |
| `/admin/product-approve` | POST | `AdminProductApprovalServlet` | - | 상품 승인/반려 처리 (`action=approve`→`판매 중`, `action=reject`→`판매 중지`) |

## 배송 관련

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/deliveries` | GET | `AdminDeliveryListServlet` | `admin-delivery-list.jsp` | 배송중(`DELIVERY_STATUS='배송중'`)인 상품 목록 조회 |
| `/admin/delivery-complete` | POST | `AdminDeliveryCompleteServlet` | - | 배송완료 처리 — `DELIVERY.DELIVERY_STATUS`를 `배송완료`로, `DELIVERY_END_DATE`를 `SYSDATE`로, `ORDERS.ORDER_STATUS`도 `배송완료`로 함께 갱신 |

### 상품 승인/반려 관련 스키마 제약

`PRODUCT.SALE_STATUS`는 CHECK 제약상 `'판매 중'/'품절'/'판매 중지'/'승인 대기'`만 허용되고, 판매자(`SELLER`)와 달리 별도의 `'반려'` 상태값이나 반려사유 컬럼(`REJECT_REASON`)이 없습니다. 그래서 상품 "반려"는 기존에 허용된 `'판매 중지'`로 전환하는 것으로 대신하며, 반려 사유 입력란은 없습니다.

## 관련 소스 파일

- `project/GoodPang/src/main/java/com/goodpang/filter/AdminAuthFilter.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminLoginServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminLogoutServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/dao/AdminDAO.java`
- `project/GoodPang/src/main/java/com/goodpang/dto/AdminDTO.java`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-login.jsp`
- `project/GoodPang/src/main/java/com/goodpang/servlet/SellerListServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/SellerDetailServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/SellerApprovalServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminProductListServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminProductApprovalServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/dao/AdminProductDAO.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminDeliveryListServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminDeliveryCompleteServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/dao/AdminDeliveryDAO.java`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-seller-list.jsp`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-seller-detail.jsp`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-product-list.jsp`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-delivery-list.jsp`
