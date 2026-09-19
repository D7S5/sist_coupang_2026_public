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





        