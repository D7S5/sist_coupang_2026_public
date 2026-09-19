-- =========================================================
-- product_view_log_table_create.sql
-- 상품 상세페이지 조회 로그(PRODUCT_VIEW_LOG) 테이블/시퀀스 생성 스크립트
--
-- 목적: 판매자센터 대시보드(vendor-dashboard.jsp)의 "오늘 방문자수" / "오늘 상품 노출수"가
--   지금까지 하드코딩된 값이었던 걸 실데이터로 바꾸기 위한 로그 테이블.
--   상품 상세페이지(ProductServlet)에 진입할 때마다 한 행씩 쌓는다.
--     - 오늘 방문자수      = COUNT(DISTINCT SESSION_ID)  (같은 세션의 재방문은 1명으로 처리)
--     - 오늘 상품 노출수    = COUNT(*)                    (= 상품 상세 조회수. 검색결과 리스트
--       노출 자체를 세는 게 아니라 상세페이지 진입을 센 것 — 이 규모 프로젝트에서 현실적인 근사치)
--
-- 전제: PRODUCT, MEMBER 테이블이 이미 존재해야 함 (FK 대상)
-- 이 스크립트는 몇 번을 다시 실행해도 안전합니다.
-- =========================================================


-- =========================================================
-- 0. 기존 객체 정리 (있으면 삭제, 없으면 조용히 넘어감)
-- =========================================================

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE PRODUCT_VIEW_LOG CASCADE CONSTRAINTS PURGE';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; -- -942 = table or view does not exist
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PRODUCT_VIEW_LOG';
EXCEPTION
   WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; -- -2289 = sequence does not exist
END;
/


-- =========================================================
-- 1. 시퀀스
-- =========================================================

CREATE SEQUENCE SEQ_PRODUCT_VIEW_LOG START WITH 1 INCREMENT BY 1 NOCACHE;


-- =========================================================
-- 2. PRODUCT_VIEW_LOG
-- =========================================================

CREATE TABLE PRODUCT_VIEW_LOG (
    view_log_no   NUMBER(10)     NOT NULL, /* 조회로그번호 (PK) */
    product_no    NUMBER(10)     NOT NULL, /* 상품번호 */
    member_no     NUMBER(10),               /* 로그인 회원번호 (비로그인 조회면 NULL) */
    session_id    VARCHAR2(100)  NOT NULL, /* 방문자 구분용 세션ID (방문자수 중복제거 기준) */
    view_date     DATE           NOT NULL  /* 조회 일시 */
);

CREATE UNIQUE INDEX PK_PRODUCT_VIEW_LOG
    ON PRODUCT_VIEW_LOG (view_log_no ASC);

ALTER TABLE PRODUCT_VIEW_LOG
    ADD CONSTRAINT PK_PRODUCT_VIEW_LOG PRIMARY KEY (view_log_no);

ALTER TABLE PRODUCT_VIEW_LOG
    ADD CONSTRAINT FK_PRODUCT_TO_PRODUCT_VIEW_LOG
        FOREIGN KEY (product_no) REFERENCES PRODUCT (product_no);

ALTER TABLE PRODUCT_VIEW_LOG
    ADD CONSTRAINT FK_MEMBER_TO_PRODUCT_VIEW_LOG
        FOREIGN KEY (member_no) REFERENCES MEMBER (member_no);

-- 대시보드 집계(오늘/어제, 상품→판매자 조인) 성능용
CREATE INDEX IDX_PRODUCT_VIEW_LOG_PRODUCT_DATE
    ON PRODUCT_VIEW_LOG (product_no, view_date);
