-- =========================================================
-- product_table_create.sql
-- 상품(PRODUCT) 관련 테이블/시퀀스/프로시저/트리거 생성 스크립트
-- 출처: docs/상품.md (CreataTableDDL.md + 테이블_생성_및_상품등록_신창만.md 정리본)
--
-- ⚠ 이 파일에서 뺀 것 (다른 곳에서 이미 처리됨/이 문서 범위 밖):
--   - CATEGORY 테이블/SEQ_CATEGORY : docs/category_table_create.sql에 이미 있음. 여기서 또 만들면
--     ORA-00955(name is already used) 에러가 나므로 절대 다시 만들지 않음
--   - SELLER, ADMIN 테이블            : 이 문서 범위 밖. 미리 존재해야 함 (FK 대상)
--   - PRODUCT_INQUIRY, PRODUCT_INQUIRY_ANSWER : 상품.md 13절 프로시저가 참조하지만
--     테이블 DDL은 다른 팀원 문서에 있음. 그 테이블이 없으면 그 프로시저 컴파일이 실패하므로
--     이 스크립트엔 포함하지 않음 (필요하면 그 테이블을 먼저 만든 뒤 상품.md 13절 참고)
--
-- 실행 순서: 1) 기존 객체 정리(있으면 삭제, 없으면 조용히 넘어감) → 2) 시퀀스 생성
--          → 3) 테이블 생성 → 4) 프로시저/트리거 생성
-- 이 스크립트는 몇 번을 다시 실행해도 안전합니다 (기존 테이블이 있든 없든 에러 없이 끝까지 돎).
-- =========================================================


-- =========================================================
-- 0. 기존 객체 정리 (있으면 삭제, 없으면 조용히 넘어감)
--    STOCK_HISTORY가 기존 PRODUCT_OPTION.OPTION_ID를 참조하고 있다면
--    PRODUCT_OPTION보다 먼저 같이 정리해야 합니다 (여기선 주석 처리해뒀으니 필요시 해제)
-- =========================================================

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PRODUCT_NOTICE CASCADE CONSTRAINTS PURGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; -- -942 = table or view does not exist
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PRODUCT_TAG CASCADE CONSTRAINTS PURGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PRODUCT_IMAGE CASCADE CONSTRAINTS PURGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

-- STOCK_HISTORY가 PRODUCT_OPTION을 참조 중이면 주석 해제해서 같이 정리
-- BEGIN
--    EXECUTE IMMEDIATE 'DROP TABLE STOCK_HISTORY CASCADE CONSTRAINTS PURGE';
-- EXCEPTION
--    WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
-- END;
-- /

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PRODUCT_OPTION CASCADE CONSTRAINTS PURGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PRODUCT CASCADE CONSTRAINTS PURGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PRODUCT_IMAGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; -- -2289 = sequence does not exist
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PRODUCT_TAG';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PRODUCT_NOTICE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PRODUCT';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_OPTION';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PRODUCT_IN';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_STOCK_HISTORY';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF;
END;
/


-- =========================================================
-- 1. 시퀀스
-- =========================================================

CREATE SEQUENCE SEQ_PRODUCT       START WITH 1 INCREMENT BY 1 NOCACHE; -- product_no
CREATE SEQUENCE SEQ_OPTION        START WITH 1 INCREMENT BY 1 NOCACHE; -- OPTION_ID
CREATE SEQUENCE SEQ_PRODUCT_IN    START WITH 1 INCREMENT BY 1 NOCACHE; -- 입고 이력
CREATE SEQUENCE SEQ_STOCK_HISTORY START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_PRODUCT_IMAGE  START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_PRODUCT_TAG    START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_PRODUCT_NOTICE START WITH 1 INCREMENT BY 1 NOCACHE;


-- =========================================================
-- 2. PRODUCT
-- =========================================================

CREATE TABLE PRODUCT (

    /* 기본 정보 */
    product_no                 NUMBER(10) NOT NULL, /* 상품번호 (PK) */
    seller_no                  NUMBER(10) NOT NULL, /* 판매자번호 */
    sub_category_no             NUMBER(10) NOT NULL, /* 카테고리번호 (CATEGORY, 소분류 리프) */

    sale_method                 VARCHAR2(20) NOT NULL, /* 판매방식: 판매자배송 / 로켓그로스 */
    brand_name                  VARCHAR2(100), /* 브랜드 (브랜드 없음이면 NULL) */
    no_brand_yn                 CHAR(1) NOT NULL, /* 브랜드 없음(자체제작) 여부 Y/N */
    product_name                 VARCHAR2(150 CHAR) NOT NULL, /* 노출상품명 */
    internal_name               VARCHAR2(150 CHAR) NOT NULL, /* 등록상품명(판매자관리용) */

    /* 옵션 */
    option_yn                   CHAR(1) NOT NULL, /* 옵션 설정함(Y)/설정안함(N) */
    product_price                NUMBER(10), /* 옵션 미설정시 판매가 */
    quantity                     NUMBER(10), /* 옵션 미설정시 재고수량 */

    /* 상품 주요 정보 */

    /* 상세설명 */
    detail_type                 VARCHAR2(30) NOT NULL, /* 이미지 업로드/에디터 작성/HTML 작성 */
    product_desc                CLOB, /* 에디터·HTML 작성 내용 */

    /* 배송 */
    shipping_zipcode              VARCHAR2(10) NOT NULL, /* 출고지 우편번호 */
    shipping_address               VARCHAR2(200) NOT NULL, /* 출고지 기본주소 */
    shipping_detail_address        VARCHAR2(200), /* 출고지 상세주소 */
    jeju_shipping_yn               CHAR(1) NOT NULL, /* 제주/도서산간 배송여부 */
    delivery_service_code          VARCHAR2(50 CHAR) NOT NULL, /* 택배사 */
    delivery_method                VARCHAR2(40) NOT NULL, /* 배송방법 */
    bundle_shipping_yn             CHAR(1) NOT NULL, /* 묶음배송 가능여부 */
    shipping_fee_type              VARCHAR2(40) NOT NULL, /* 배송비 종류 */
    shipping_fee                   NUMBER(10) DEFAULT 0, /* 배송비 금액 */
    lead_time_input_type           VARCHAR2(40) NOT NULL, /* 출고소요일 입력방식 */
    lead_time_days                 NUMBER(3), /* 출고 소요일(일) */
    same_day_ship_yn                CHAR(1) NOT NULL, /* 당일출고 여부 */
    same_day_cutoff_time            VARCHAR2(5), /* 당일출고 마감시각 (예: 12:00) */

    /* 상태/메타 */
    sale_status                 VARCHAR2(30) NOT NULL, /* 판매중/품절/판매중지/승인대기 */
    display_yn                  CHAR(1) DEFAULT 'Y' NOT NULL, /* 판매자 상품목록 노출 여부 - 소프트 삭제(N이면 숨김) */
    created_date                 DATE NOT NULL, /* 등록일 */
    updated_date                 DATE NOT NULL  /* 수정일 */
);

CREATE UNIQUE INDEX PK_PRODUCT
    ON PRODUCT (product_no ASC);

ALTER TABLE PRODUCT
    ADD CONSTRAINT PK_PRODUCT PRIMARY KEY (product_no);

ALTER TABLE PRODUCT
    ADD CONSTRAINT FK_SELLER_TO_PRODUCT
        FOREIGN KEY (seller_no) REFERENCES SELLER (seller_no);

ALTER TABLE PRODUCT
    ADD CONSTRAINT FK_CATEGORY_TO_PRODUCT
        FOREIGN KEY (sub_category_no) REFERENCES CATEGORY (category_no);

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_SALE_METHOD
    CHECK (sale_method IN ('판매자배송', '로켓그로스'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_NO_BRAND_YN
    CHECK (no_brand_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_BRAND_REQUIRED
    CHECK (no_brand_yn = 'Y' OR brand_name IS NOT NULL);

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_OPTION_YN
    CHECK (option_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_DETAIL_TYPE
    CHECK (detail_type IN ('이미지 업로드', '에디터 작성', 'HTML 작성'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_JEJU_SHIPPING_YN
    CHECK (jeju_shipping_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_DELIVERY_METHOD
    CHECK (delivery_method IN ('일반배송', '신선냉동', '주문제작', '구매대행', '설치배송 또는 판매자 직접전달'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_BUNDLE_SHIPPING_YN
    CHECK (bundle_shipping_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_SHIPPING_FEE_TYPE
    CHECK (shipping_fee_type IN (
        '무료배송', '유료배송', '조건부무료배송',
        '9,800원 이상 무료배송', '19,800원 이상 무료배송', '30,000원 이상 무료배송',
        '착불배송'
    ));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_LEAD_TIME_TYPE
    CHECK (lead_time_input_type IN ('기본 입력', '구매 옵션별로 입력'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_SAME_DAY_SHIP_YN
    CHECK (same_day_ship_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_SALE_STATUS
    CHECK (sale_status IN ('판매 중', '품절', '판매 중지', '승인 대기'));

ALTER TABLE PRODUCT ADD CONSTRAINT CK_PRODUCT_DISPLAY_YN
    CHECK (display_yn IN ('Y', 'N'));


-- =========================================================
-- 3. PRODUCT_OPTION
-- =========================================================

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
    PRICE                  NUMBER(10)     NOT NULL, /* 판매가(원) */
    auto_price_adjust_yn   CHAR(1)        NOT NULL, /* 판매자 자동가격조정 여부 */
    quantity               NUMBER(10)     NOT NULL, /* 재고수량 */
    seller_product_code    VARCHAR2(50),             /* 판매자상품코드 */
    model_no               VARCHAR2(50),             /* 모델번호 */
    barcode                VARCHAR2(50),             /* 상품바코드 */
    STATUS                 CHAR(1)        NOT NULL  /* 상태 */
);

CREATE UNIQUE INDEX PK_PRODUCT_OPTION
    ON PRODUCT_OPTION (OPTION_ID ASC);

ALTER TABLE PRODUCT_OPTION
    ADD CONSTRAINT PK_PRODUCT_OPTION PRIMARY KEY (OPTION_ID);

ALTER TABLE PRODUCT_OPTION
    ADD CONSTRAINT FK_PRODUCT_TO_PRODUCT_OPTION
        FOREIGN KEY (product_no) REFERENCES PRODUCT (product_no);

ALTER TABLE PRODUCT_OPTION ADD CONSTRAINT CK_PRODUCT_OPTION_AUTO_PRICE_YN
    CHECK (auto_price_adjust_yn IN ('Y', 'N'));

ALTER TABLE PRODUCT_OPTION ADD CONSTRAINT CK_PRODUCT_OPTION_QUANTITY
    CHECK (quantity >= 0);


-- =========================================================
-- 4. PRODUCT_IMAGE (상품이미지 — 기본등록/옵션별등록 공통)
-- =========================================================

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
    ON PRODUCT_IMAGE (image_no ASC);

ALTER TABLE PRODUCT_IMAGE
    ADD CONSTRAINT PK_PRODUCT_IMAGE PRIMARY KEY (image_no);

ALTER TABLE PRODUCT_IMAGE
    ADD CONSTRAINT FK_PRODUCT_TO_PRODUCT_IMAGE
        FOREIGN KEY (product_no) REFERENCES PRODUCT (product_no);

ALTER TABLE PRODUCT_IMAGE
    ADD CONSTRAINT FK_OPTION_TO_PRODUCT_IMAGE
        FOREIGN KEY (OPTION_ID) REFERENCES PRODUCT_OPTION (OPTION_ID);

ALTER TABLE PRODUCT_IMAGE ADD CONSTRAINT CK_PRODUCT_IMAGE_PURPOSE
    CHECK (image_purpose IN ('대표', '추가', '상세설명'));


-- =========================================================
-- 5. PRODUCT_TAG (검색어 태그, 최대 20개는 애플리케이션 단에서 체크)
-- =========================================================

CREATE TABLE PRODUCT_TAG (
    tag_no        NUMBER(10)    NOT NULL, /* 태그번호 (PK) */
    product_no    NUMBER(10)    NOT NULL, /* 상품번호 */
    tag_name      VARCHAR2(50)  NOT NULL  /* 검색어 태그 */
);

CREATE UNIQUE INDEX PK_PRODUCT_TAG
    ON PRODUCT_TAG (tag_no ASC);

ALTER TABLE PRODUCT_TAG
    ADD CONSTRAINT PK_PRODUCT_TAG PRIMARY KEY (tag_no);

ALTER TABLE PRODUCT_TAG
    ADD CONSTRAINT FK_PRODUCT_TO_PRODUCT_TAG
        FOREIGN KEY (product_no) REFERENCES PRODUCT (product_no);

ALTER TABLE PRODUCT_TAG
    ADD CONSTRAINT UQ_PRODUCT_TAG UNIQUE (product_no, tag_name);


-- =========================================================
-- 6. PRODUCT_NOTICE (상품정보제공고시 — 제품소재/색상/치수 등 반복 행)
-- =========================================================

CREATE TABLE PRODUCT_NOTICE (
    notice_no        NUMBER(10)     NOT NULL, /* 고시정보번호 (PK) */
    product_no       NUMBER(10)     NOT NULL, /* 상품번호 */
    notice_name      VARCHAR2(50)   NOT NULL, /* 고시정보명 (예: 제품 소재, 색상, 치수 ...) */
    notice_content   VARCHAR2(1000),           /* 내용 (참조 체크 시 NULL 가능) */
    refer_page_yn    CHAR(1)        NOT NULL  /* 상품 상세페이지 참조 여부 */
);

CREATE UNIQUE INDEX PK_PRODUCT_NOTICE
    ON PRODUCT_NOTICE (notice_no ASC);

ALTER TABLE PRODUCT_NOTICE
    ADD CONSTRAINT PK_PRODUCT_NOTICE PRIMARY KEY (notice_no);

ALTER TABLE PRODUCT_NOTICE
    ADD CONSTRAINT FK_PRODUCT_TO_PRODUCT_NOTICE
        FOREIGN KEY (product_no) REFERENCES PRODUCT (product_no);

ALTER TABLE PRODUCT_NOTICE ADD CONSTRAINT CK_PRODUCT_NOTICE_REFER_YN
    CHECK (refer_page_yn IN ('Y', 'N'));


-- =========================================================
-- 7. 프로시저 · 트리거
--
-- ⚠ 상품.md 13절의 PROUDCT_QUESTION / PROUDCT_ANSWER 프로시저는 여기 포함하지 않았습니다.
--   PRODUCT_INQUIRY / PRODUCT_INQUIRY_ANSWER 테이블(다른 팀원 문서 소관)이 먼저 있어야
--   컴파일되기 때문입니다. 그 테이블을 만든 뒤 상품.md 13절 코드를 그대로 실행하면 됩니다.
-- =========================================================

CREATE OR REPLACE PROCEDURE UP_PRODUCT_IN (
-- 신창만
-- 입고 프로시저. 옵션번호와 수량을 입력하면 해당 상품이 입고된다.
-- 사용법 : EXEC UP_PRODUCT_IN(옵션번호, 수량);
-- ⚠ OPTION_ID 기준이라 PRODUCT_OPTION 행이 없는 상품(option_yn='N')에는 못 씀.
--   그런 상품은 UPDATE PRODUCT SET quantity = quantity + 수량 WHERE product_no = ... 로 직접 처리
    P_OPTION_NO IN PRODUCT_IN.OPTION_ID%TYPE,
    P_QUANTITY  IN PRODUCT_IN.QUANTITY%TYPE
) IS
BEGIN
    INSERT INTO PRODUCT_IN
    VALUES (SEQ_PRODUCT_IN.NEXTVAL, P_QUANTITY, SYSDATE, P_OPTION_NO);

    COMMIT;
END;
/

CREATE OR REPLACE TRIGGER TRG_PRODUCT_IN_STOCK
AFTER INSERT ON PRODUCT_IN FOR EACH ROW
BEGIN
    UPDATE PRODUCT_OPTION
    SET QUANTITY = QUANTITY + :NEW.QUANTITY, STATUS = 'Y'
    WHERE OPTION_ID = :NEW.OPTION_ID;
END;
/
