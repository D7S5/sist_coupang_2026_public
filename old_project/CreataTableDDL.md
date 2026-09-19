# 상품 등록 화면 기준 PRODUCT 테이블 설계

`vendor_product_write.jsp`(판매자 상품등록 화면)에 있는 입력 항목을 그대로 컬럼으로 옮긴 버전입니다.
`최종_쿼리_취합.md`의 기존 `PRODUCT`(7개 컬럼)/`PRODUCT_OPTION`은 데모용으로 너무 단순해서
실제 화면의 입력값을 다 못 담기 때문에, 이 파일에서 더 완전한 버전으로 새로 설계했습니다.

## ⚠ 기존 테이블과의 관계

- `PRODUCT`, `PRODUCT_OPTION`은 기존 스키마에 이미 있는 테이블과 **같은 이름**으로 다시 만듭니다(컬럼이 늘어난 버전).
  기존 DB에 이미 두 테이블이 있다면 아래 "초기화" 절의 DROP문으로 지우고 다시 만들어야 합니다.
- `STOCK_HISTORY.OPTION_ID`가 기존 `PRODUCT_OPTION.OPTION_ID`를 참조하고 있습니다.
  `PRODUCT_OPTION`을 지웠다가 다시 만들면 `STOCK_HISTORY`도 같이 정리하거나, 최소한 데이터가 비어있는 상태에서 진행하세요.
- 카테고리는 `SUB_CATEGORY`(기존 3단계 고정 구조)가 아니라, **자기참조 `CATEGORY` 테이블**(2번 절에 DDL 포함)을 참조합니다.
  (`CategoryInfo` 서블릿이 이미 이 `CATEGORY` 테이블 기준으로 동작하고 있어서 맞췄습니다. 이미 만들어 두셨다면 2번 절은 건너뛰세요.)

## 화면 항목 → 컬럼 매핑 요약

| 화면 항목 | 테이블.컬럼 |
|---|---|
| 판매방식 선택 (판매자배송/로켓그로스) | PRODUCT.sale_method |
| 브랜드 / 브랜드 없음 | PRODUCT.brand_name / no_brand_yn |
| 노출상품명 / 등록상품명(판매자관리용) | PRODUCT.display_name / internal_name |
| 카테고리 | PRODUCT.category_no (FK → CATEGORY) |
| 옵션 설정함/설정안함 | PRODUCT.option_yn |
| 제조사 / 상품구성 / 인증정보 / 병행수입 | PRODUCT.manufacturer / composition_type / certification_type / parallel_import_yn |
| 미성년자 구매 가능여부 | PRODUCT.minor_purchase_yn |
| 인당 최대구매수량 | PRODUCT.max_purchase_yn / max_purchase_qty |
| 판매기간 | PRODUCT.sale_period_yn / sale_start_date / sale_end_date |
| 부가세 | PRODUCT.vat_type |
| 상세설명 작성방식 / 내용 | PRODUCT.detail_type / product_desc |
| 출고지 / 제주도서산간 / 택배사 / 배송방법 / 묶음배송 / 배송비 / 출고소요일 / 당일출고 | PRODUCT.shipping_* , delivery_* , bundle_shipping_yn , lead_time_* , same_day_* |
| 반품/교환지 / 초도배송비 / 반품배송비 | PRODUCT.return_* |
| 판매상태(판매중/품절/판매중지/승인대기) | PRODUCT.sale_status |
| 옵션 목록 테이블(옵션명, 정상가, 판매가, 자동가격조정, 재고, 판매자상품코드, 모델번호, 바코드) | PRODUCT_OPTION (전체) |
| 상품이미지(대표/추가, 기본등록·옵션별등록) | PRODUCT_IMAGE |
| 검색어 태그 | PRODUCT_TAG |
| 상품정보제공고시 (제품소재/색상/치수 …) | PRODUCT_NOTICE |

옵션을 "설정 안 함"으로 고른 상품은 `PRODUCT_OPTION` 행이 없는 대신
`PRODUCT.base_price` / `base_quantity`를 단일 SKU 가격·재고로 사용합니다.


## 1. 시퀀스

```sql
CREATE SEQUENCE SEQ_PRODUCT_IMAGE
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE SEQUENCE SEQ_PRODUCT_TAG
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE SEQUENCE SEQ_PRODUCT_NOTICE
START WITH 1
INCREMENT BY 1
NOCACHE;
```

기존 `SEQ_PRODUCT`(product_no용), `SEQ_OPTION`(OPTION_ID용)은 그대로 재사용합니다.


## 2. CATEGORY (선행 테이블 — PRODUCT.category_no가 참조)

`카테고리_재귀테이블_테스트.md`에서 만든 자기참조(재귀) 카테고리 테이블과 동일합니다.
이미 DB에 만들어 두셨다면 이 절은 건너뛰어도 됩니다. `admin_no`는 `ADMIN` 테이블에 있는 값을 참조하므로,
`ADMIN`에 데이터가 먼저 있어야 합니다.

```sql
CREATE TABLE CATEGORY (
    category_no          NUMBER(10)    NOT NULL, /* 카테고리 번호 (PK) */
    category_name        VARCHAR2(50)  NOT NULL, /* 카테고리명 */
    parent_category_no   NUMBER(10),             /* 상위 카테고리 번호. 최상위(대분류)는 NULL */
    category_level       NUMBER(1)     NOT NULL, /* 깊이: 1=대분류, 2=중분류, 3=소분류, 4=세분류 ... */
    created_date          DATE          NOT NULL, /* 등록일 */
    admin_no              NUMBER(10)    NOT NULL  /* 등록한 관리자 번호 */
);

CREATE UNIQUE INDEX PK_CATEGORY
    ON CATEGORY (category_no ASC);

ALTER TABLE CATEGORY
    ADD CONSTRAINT PK_CATEGORY
        PRIMARY KEY (category_no);

-- 자기참조 FK: 내 부모 카테고리도 결국 CATEGORY 테이블의 한 행
ALTER TABLE CATEGORY
    ADD CONSTRAINT FK_CATEGORY_TO_CATEGORY
        FOREIGN KEY (parent_category_no)
        REFERENCES CATEGORY (category_no);

ALTER TABLE CATEGORY
    ADD CONSTRAINT FK_ADMIN_TO_CATEGORY
        FOREIGN KEY (admin_no)
        REFERENCES ADMIN (admin_no);

-- 같은 부모 밑에 같은 이름의 카테고리가 중복 생성되는 것 방지
ALTER TABLE CATEGORY
    ADD CONSTRAINT UQ_CATEGORY_NAME
        UNIQUE (parent_category_no, category_name);
```

카테고리는 지금까지 `카테고리_메뉴_데이터_Insert.md` 등에서 category_no를 직접 지정해서 넣어왔기 때문에
시퀀스 없이도 동작합니다. 그래도 앞으로 애플리케이션에서 카테고리를 직접 등록하게 하려면 시퀀스를 하나 두는 게 편합니다.

```sql
CREATE SEQUENCE SEQ_CATEGORY
START WITH 1000
INCREMENT BY 1
NOCACHE;
```


## 3. PRODUCT

```sql
CREATE TABLE PRODUCT (

    /* 기본 정보 */
    product_no                 NUMBER(10) NOT NULL, /* 상품번호 (PK) */
    seller_no                  NUMBER(10) NOT NULL, /* 판매자번호 */
    category_no                NUMBER(10) NOT NULL, /* 카테고리번호 (CATEGORY, 소분류 리프) */

    sale_method                 VARCHAR2(20) NOT NULL, /* 판매방식: 판매자배송 / 로켓그로스 */
    brand_name                  VARCHAR2(100), /* 브랜드 (브랜드 없음이면 NULL) */
    no_brand_yn                 CHAR(1) NOT NULL, /* 브랜드 없음(자체제작) 여부 Y/N */
    display_name                VARCHAR2(100) NOT NULL, /* 노출상품명 */
    internal_name               VARCHAR2(100), /* 등록상품명(판매자관리용) */

    /* 옵션 */
    option_yn                   CHAR(1) NOT NULL, /* 옵션 설정함(Y)/설정안함(N) */
    base_price                  NUMBER(10), /* 옵션 미설정시 판매가 */
    base_quantity                NUMBER(10), /* 옵션 미설정시 재고수량 */

    /* 상품 주요 정보 */
    manufacturer                 VARCHAR2(100), /* 제조사 */
    composition_type             VARCHAR2(30) NOT NULL, /* 상품구성 */
    certification_type           VARCHAR2(30) NOT NULL, /* 인증정보 */
    parallel_import_yn           CHAR(1) NOT NULL, /* 병행수입 여부 */
    minor_purchase_yn            CHAR(1) NOT NULL, /* 미성년자 구매 가능여부 */
    max_purchase_yn               CHAR(1) NOT NULL, /* 인당 최대구매수량 설정여부 */
    max_purchase_qty              NUMBER(6), /* 인당 최대구매수량 */
    sale_period_yn                 CHAR(1) NOT NULL, /* 판매기간 설정여부 */
    sale_start_date                DATE, /* 판매 시작일 */
    sale_end_date                  DATE, /* 판매 종료일 */
    vat_type                       VARCHAR2(10) NOT NULL, /* 부가세: 과세/면세 */

    /* 상세설명 */
    detail_type                 VARCHAR2(20) NOT NULL, /* 이미지 업로드/에디터 작성/HTML 작성 */
    product_desc                CLOB, /* 에디터·HTML 작성 내용 */

    /* 배송 */
    shipping_zipcode              VARCHAR2(10) NOT NULL, /* 출고지 우편번호 */
    shipping_address               VARCHAR2(200) NOT NULL, /* 출고지 기본주소 */
    shipping_detail_address        VARCHAR2(200), /* 출고지 상세주소 */
    jeju_shipping_yn               CHAR(1) NOT NULL, /* 제주/도서산간 배송여부 */
    delivery_service_code          VARCHAR2(20) NOT NULL, /* 택배사 */
    delivery_method                VARCHAR2(30) NOT NULL, /* 배송방법 */
    bundle_shipping_yn             CHAR(1) NOT NULL, /* 묶음배송 가능여부 */
    shipping_fee_type              VARCHAR2(30) NOT NULL, /* 배송비 종류 */
    shipping_fee                   NUMBER(10) DEFAULT 0, /* 배송비 금액 */
    lead_time_input_type           VARCHAR2(20) NOT NULL, /* 출고소요일 입력방식 */
    lead_time_days                 NUMBER(3), /* 출고 소요일(일) */
    same_day_ship_yn                CHAR(1) NOT NULL, /* 당일출고 여부 */
    same_day_cutoff_time            VARCHAR2(5), /* 당일출고 마감시각 (예: 12:00) */

    /* 반품/교환 */
    return_zipcode               VARCHAR2(10) NOT NULL, /* 반품/교환지 우편번호 */
    return_address               VARCHAR2(200) NOT NULL, /* 반품/교환지 기본주소 */
    return_detail_address        VARCHAR2(200), /* 반품/교환지 상세주소 */
    initial_shipping_fee         NUMBER(10) NOT NULL, /* 초도배송비(편도) */
    return_shipping_fee          NUMBER(10) NOT NULL, /* 반품배송비(편도) */

    /* 상태/메타 */
    sale_status                 VARCHAR2(20) NOT NULL, /* 판매중/품절/판매중지/승인대기 */
    created_date                 DATE NOT NULL, /* 등록일 */
    updated_date                 DATE NOT NULL  /* 수정일 */
);

CREATE UNIQUE INDEX PK_PRODUCT
    ON PRODUCT (
        product_no ASC
    );

ALTER TABLE PRODUCT
    ADD
        CONSTRAINT PK_PRODUCT
        PRIMARY KEY (
            product_no
        );

ALTER TABLE PRODUCT
    ADD
        CONSTRAINT FK_SELLER_TO_PRODUCT
        FOREIGN KEY (
            seller_no
        )
        REFERENCES SELLER (
            seller_no
        );

ALTER TABLE PRODUCT
    ADD
        CONSTRAINT FK_CATEGORY_TO_PRODUCT
        FOREIGN KEY (
            category_no
        )
        REFERENCES CATEGORY (
            category_no
        );

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_SALE_METHOD
    CHECK (sale_method IN ('판매자배송', '로켓그로스'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_NO_BRAND_YN
    CHECK (no_brand_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_BRAND_REQUIRED
    CHECK (no_brand_yn = 'Y' OR brand_name IS NOT NULL);

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_OPTION_YN
    CHECK (option_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_COMPOSITION_TYPE
    CHECK (composition_type IN ('동일한 상품으로 구성됨', '다양한 상품이 혼합되어 구성됨'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_CERTIFICATION_TYPE
    CHECK (certification_type IN ('인증·신고 대상', '상세페이지 별도표기', '인증·신고 대상 아님'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_PARALLEL_IMPORT_YN
    CHECK (parallel_import_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_MINOR_PURCHASE_YN
    CHECK (minor_purchase_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_MAX_PURCHASE_YN
    CHECK (max_purchase_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_SALE_PERIOD_YN
    CHECK (sale_period_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_VAT_TYPE
    CHECK (vat_type IN ('과세', '면세'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_DETAIL_TYPE
    CHECK (detail_type IN ('이미지 업로드', '에디터 작성', 'HTML 작성'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_JEJU_SHIPPING_YN
    CHECK (jeju_shipping_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_DELIVERY_METHOD
    CHECK (delivery_method IN ('일반배송', '신선냉동', '주문제작', '구매대행', '설치배송 또는 판매자 직접전달'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_BUNDLE_SHIPPING_YN
    CHECK (bundle_shipping_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_SHIPPING_FEE_TYPE
    CHECK (shipping_fee_type IN (
        '무료배송', '유료배송', '조건부무료배송',
        '9,800원 이상 무료배송', '19,800원 이상 무료배송', '30,000원 이상 무료배송',
        '착불배송'
    ));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_LEAD_TIME_TYPE
    CHECK (lead_time_input_type IN ('기본 입력', '구매 옵션별로 입력'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_SAME_DAY_SHIP_YN
    CHECK (same_day_ship_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT
    ADD CONSTRAINT CK_PRODUCT_SALE_STATUS
    CHECK (sale_status IN ('판매 중', '품절', '판매 중지', '승인 대기'));
```


## 4. PRODUCT_OPTION (옵션 목록 테이블 컬럼 반영)

```sql
CREATE TABLE PRODUCT_OPTION (
    OPTION_ID              NUMBER(10)     NOT NULL, /* 옵션번호 (PK) */
    product_no             NUMBER(10)     NOT NULL, /* 상품번호 */
    OPTION1_TYPE           VARCHAR2(50),             /* 옵션타입1 (예: 사이즈) */
    OPTION1_VALUE          VARCHAR2(100),            /* 옵션값1 (예: Free) */
    OPTION2_TYPE           VARCHAR2(50),             /* 옵션타입2 (예: 색상) */
    OPTION2_VALUE          VARCHAR2(100),            /* 옵션값2 (예: 화이트) */
    OPTION3_TYPE           VARCHAR2(50),             /* 옵션타입3 */
    OPTION3_VALUE          VARCHAR2(100),            /* 옵션값3 */
    normal_price           NUMBER(10),               /* 정상가(원) */
    sale_price             NUMBER(10)     NOT NULL, /* 판매가(원) */
    auto_price_adjust_yn   CHAR(1)        NOT NULL, /* 판매자 자동가격조정 여부 */
    quantity               NUMBER(10)     NOT NULL, /* 재고수량 */
    seller_product_code    VARCHAR2(50),             /* 판매자상품코드 */
    model_no               VARCHAR2(50),             /* 모델번호 */
    barcode                VARCHAR2(50),             /* 상품바코드 */
    STATUS                 CHAR(1)        NOT NULL  /* 상태 */
);

CREATE UNIQUE INDEX PK_PRODUCT_OPTION
    ON PRODUCT_OPTION (
        OPTION_ID ASC
    );

ALTER TABLE PRODUCT_OPTION
    ADD
        CONSTRAINT PK_PRODUCT_OPTION
        PRIMARY KEY (
            OPTION_ID
        );

ALTER TABLE PRODUCT_OPTION
    ADD
        CONSTRAINT FK_PRODUCT_TO_PRODUCT_OPTION
        FOREIGN KEY (
            product_no
        )
        REFERENCES PRODUCT (
            product_no
        );

ALTER TABLE PRODUCT_OPTION
    ADD CONSTRAINT CK_PRODUCT_OPTION_AUTO_PRICE_YN
    CHECK (auto_price_adjust_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT_OPTION
    ADD CONSTRAINT CK_PRODUCT_OPTION_QUANTITY
    CHECK (quantity >= 0);
```


## 5. PRODUCT_IMAGE (상품이미지 — 기본등록/옵션별등록 공통)

```sql
CREATE TABLE PRODUCT_IMAGE (
    image_no        NUMBER(10)     NOT NULL, /* 이미지번호 (PK) */
    product_no      NUMBER(10)     NOT NULL, /* 상품번호 */
    OPTION_ID       NUMBER(10),               /* 옵션번호 (옵션별 등록일 때만, 기본등록이면 NULL) */
    image_purpose   VARCHAR2(20)   NOT NULL, /* 대표/추가/상세설명 */
    image_order     NUMBER(3)      NOT NULL, /* 노출 순서 (추가이미지 최대 9장) */
    image_url       VARCHAR2(500)  NOT NULL, /* 이미지 경로 */
    created_date    DATE           NOT NULL  /* 등록일 */
);

CREATE UNIQUE INDEX PK_PRODUCT_IMAGE
    ON PRODUCT_IMAGE (
        image_no ASC
    );

ALTER TABLE PRODUCT_IMAGE
    ADD
        CONSTRAINT PK_PRODUCT_IMAGE
        PRIMARY KEY (
            image_no
        );

ALTER TABLE PRODUCT_IMAGE
    ADD
        CONSTRAINT FK_PRODUCT_TO_PRODUCT_IMAGE
        FOREIGN KEY (
            product_no
        )
        REFERENCES PRODUCT (
            product_no
        );

ALTER TABLE PRODUCT_IMAGE
    ADD
        CONSTRAINT FK_OPTION_TO_PRODUCT_IMAGE
        FOREIGN KEY (
            OPTION_ID
        )
        REFERENCES PRODUCT_OPTION (
            OPTION_ID
        );

ALTER TABLE PRODUCT_IMAGE
    ADD CONSTRAINT CK_PRODUCT_IMAGE_PURPOSE
    CHECK (image_purpose IN ('대표', '추가', '상세설명'));
```


## 6. PRODUCT_TAG (검색어 태그, 최대 20개는 애플리케이션 단에서 체크)

```sql
CREATE TABLE PRODUCT_TAG (
    tag_no        NUMBER(10)    NOT NULL, /* 태그번호 (PK) */
    product_no    NUMBER(10)    NOT NULL, /* 상품번호 */
    tag_name      VARCHAR2(50)  NOT NULL  /* 검색어 태그 */
);

CREATE UNIQUE INDEX PK_PRODUCT_TAG
    ON PRODUCT_TAG (
        tag_no ASC
    );

ALTER TABLE PRODUCT_TAG
    ADD
        CONSTRAINT PK_PRODUCT_TAG
        PRIMARY KEY (
            tag_no
        );

ALTER TABLE PRODUCT_TAG
    ADD
        CONSTRAINT FK_PRODUCT_TO_PRODUCT_TAG
        FOREIGN KEY (
            product_no
        )
        REFERENCES PRODUCT (
            product_no
        );

ALTER TABLE PRODUCT_TAG
    ADD
        CONSTRAINT UQ_PRODUCT_TAG
        UNIQUE (product_no, tag_name);
```


## 7. PRODUCT_NOTICE (상품정보제공고시 — 제품소재/색상/치수 등 반복 행)

```sql
CREATE TABLE PRODUCT_NOTICE (
    notice_no        NUMBER(10)     NOT NULL, /* 고시정보번호 (PK) */
    product_no       NUMBER(10)     NOT NULL, /* 상품번호 */
    notice_name      VARCHAR2(50)   NOT NULL, /* 고시정보명 (예: 제품 소재, 색상, 치수 ...) */
    notice_content   VARCHAR2(1000),           /* 내용 (참조 체크 시 NULL 가능) */
    refer_page_yn    CHAR(1)        NOT NULL  /* 상품 상세페이지 참조 여부 */
);

CREATE UNIQUE INDEX PK_PRODUCT_NOTICE
    ON PRODUCT_NOTICE (
        notice_no ASC
    );

ALTER TABLE PRODUCT_NOTICE
    ADD
        CONSTRAINT PK_PRODUCT_NOTICE
        PRIMARY KEY (
            notice_no
        );

ALTER TABLE PRODUCT_NOTICE
    ADD
        CONSTRAINT FK_PRODUCT_TO_PRODUCT_NOTICE
        FOREIGN KEY (
            product_no
        )
        REFERENCES PRODUCT (
            product_no
        );

ALTER TABLE PRODUCT_NOTICE
    ADD CONSTRAINT CK_PRODUCT_NOTICE_REFER_YN
    CHECK (refer_page_yn IN ('Y', 'N'));
```


## 8. 초기화 (다시 만들 때만 실행)

자식 테이블부터 지워야 FK 에러가 안 납니다. `STOCK_HISTORY`가 기존 `PRODUCT_OPTION.OPTION_ID`를 참조하고 있다면
`PRODUCT_OPTION`보다 먼저 같이 정리해야 합니다.

```sql
DROP TABLE PRODUCT_NOTICE   CASCADE CONSTRAINTS PURGE;
DROP TABLE PRODUCT_TAG      CASCADE CONSTRAINTS PURGE;
DROP TABLE PRODUCT_IMAGE    CASCADE CONSTRAINTS PURGE;
-- STOCK_HISTORY가 PRODUCT_OPTION을 참조 중이면 여기서 같이 정리
-- DROP TABLE STOCK_HISTORY CASCADE CONSTRAINTS PURGE;
DROP TABLE PRODUCT_OPTION   CASCADE CONSTRAINTS PURGE;
DROP TABLE PRODUCT          CASCADE CONSTRAINTS PURGE;
-- CATEGORY는 PRODUCT가 참조하므로 PRODUCT보다 먼저 지우면 안 됨
-- 이미 만들어 둔 CATEGORY 데이터를 유지하고 싶다면 아래 두 줄은 실행하지 마세요
DROP TABLE CATEGORY         CASCADE CONSTRAINTS PURGE;

DROP SEQUENCE SEQ_CATEGORY;
DROP SEQUENCE SEQ_PRODUCT_IMAGE;
DROP SEQUENCE SEQ_PRODUCT_TAG;
DROP SEQUENCE SEQ_PRODUCT_NOTICE;
```
