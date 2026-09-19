# 😊 기본 Data Insert

## 상품등록

```sql
-- 상품등록
INSERT INTO PRODUCT VALUES (SEQ_PRODUCT.NEXTVAL, '남성 긴팔티 골프 이너웨어 쿨링 기능성 티셔츠', '기본 코튼 소재의 남성 반팔 티셔츠,여름 작업복 스포츠', 19000, 0, 5, 100301);

-- 상품옵션등록
INSERT INTO PRODUCT VALUES (SEQ_PRODUCT.NEXTVAL, '남성 긴팔티 골프 이너웨어 쿨링 기능성 티셔츠', '기본 코튼 소재의 남성 반팔 티셔츠,여름 작업복 스포츠', 19000, 0, 5, 100301);
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, SEQ_PRODUCT.CURRVAL, '사이즈','95', '색상','블랙', NULL,NULL, 29000, 10, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, SEQ_PRODUCT.CURRVAL, '사이즈','95', '색상','화이트', NULL,NULL, 29000, 10, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, SEQ_PRODUCT.CURRVAL, '사이즈','100', '색상','블랙', NULL,NULL, 29000, 10, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, SEQ_PRODUCT.CURRVAL, '사이즈','100', '색상','화이트', NULL,NULL, 29000, 10, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, SEQ_PRODUCT.CURRVAL, '사이즈','105', '색상','블랙', NULL,NULL, 29000, 10, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, SEQ_PRODUCT.CURRVAL, '사이즈','105', '색상','화이트', NULL,NULL, 29000, 10, 'N');
```

## 관리자

```sql
-- 관리자 인서트 문
INSERT INTO ADMIN (
    admin_no,
    admin_id,
    admin_pw,
    admin_name,
    email,
    tel
) VALUES (
    1,
    'admin',
    'admin1234',
    '이창익',
    'admin@coupang.com',
    '010-3434-5998'
);

COMMIT;

--확인
SELECT *
FROM ADMIN;
```

## 상품 분류

```sql
-- <상품 대분류>
INSERT INTO MAIN_CATEGORY
(main_category_no, main_category_name, created_date, admin_no)
VALUES
(10, '패션의류/잡화', SYSDATE, 1);

INSERT INTO MAIN_CATEGORY
(main_category_no, main_category_name, created_date, admin_no)
VALUES
(11, '식품', SYSDATE, 1);

--확인
SELECT *
FROM MAIN_CATEGORY;

--<상품 중분류>
-- 패션의류/잡화 (10)
INSERT INTO MID_CATEGORY
(mid_category_no, mid_category_name, created_date, main_category_no)
VALUES
(1001, '여성패션', SYSDATE, 10);

INSERT INTO MID_CATEGORY
(mid_category_no, mid_category_name, created_date, main_category_no)
VALUES
(1002, '남성패션', SYSDATE, 10);

INSERT INTO MID_CATEGORY
(mid_category_no, mid_category_name, created_date, main_category_no)
VALUES
(1003, '남녀 공용 의류', SYSDATE, 10);

-- 식품 (11)
INSERT INTO MID_CATEGORY
(mid_category_no, mid_category_name, created_date, main_category_no)
VALUES
(1101, '건강식품', SYSDATE, 11);

INSERT INTO MID_CATEGORY
(mid_category_no, mid_category_name, created_date, main_category_no)
VALUES
(1102, '과일', SYSDATE, 11);

-- <상품 소분류>

-- 여성패션 (1001)
INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100101, '의류', SYSDATE, 1001);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100102, '신발', SYSDATE, 1001);

-- 남성패션 (1002)
INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100201, '의류', SYSDATE, 1002);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100202, '신발', SYSDATE, 1002);

-- 남녀공용 (1003)
INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100301, '티셔츠', SYSDATE, 1003);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100302, '셔츠', SYSDATE, 1003);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(100303, '바지', SYSDATE, 1003);

-- 건강식품 (1101)
INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(110101, '영양식/선식', SYSDATE, 1101);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(110102, '어린이 건강식품', SYSDATE, 1101);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(110103, '꿀/프로폴리스', SYSDATE, 1101);

-- 과일 (1102)
INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(110201, '사과/배', SYSDATE, 1102);

INSERT INTO SUB_CATEGORY
(sub_category_no, sub_category_name, created_date, mid_category_no)
VALUES
(110202, '과일선물세트', SYSDATE, 1102);

-- 확인
SELECT *
FROM MID_CATEGORY;

SELECT *
FROM SUB_CATEGORY;
```

## 판매자

```sql
-- 판매자 정보 20건 INSERT --
INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '서울시 강남구 테헤란로 123', '110-234-567890', '110-22-33331', '청담클로짓', '국민은행', 'seller01', 'pw1234', '김민준', '010-1111-2222', 'seller01@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '경기도 성남시 분당구 판교로 45', '302-567890-123', '220-33-44442', '미니멀무드', '농협은행', 'seller02', 'dskdjk', '이서연', '010-2222-3333', 'seller02@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '서울시 송파구 올림픽로 300', '112-345678-90', '330-44-55553', '데일리핏', '신한은행', 'seller03', 'sfjier', '박지훈', '010-3333-4444', 'seller03@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '인천시 연수구 송도동 12', '556-01-234567', '440-55-66664', '송도뷰티', '우리은행', 'seller04', 'tueoriw12', '최유진', '010-4444-5555', 'seller04@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '대구시 수성구 범어동 88', '765-432-109876', '550-66-77775', '수성패션', '하나은행', 'seller05', 'weriopjo145', '정하늘', '010-5555-6666', 'seller05@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '경기도 수원시 영통구 광교로 5', '901-234-567890', '770-88-99997', '어반라인', '카카오뱅크', 'seller06', 'vmk4589', '오지우', '010-7777-8888', 'seller06@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '부산시 해운대구 센텀로 99', '123-456-789012', '660-77-88886', '센텀키친', '기업은행', 'seller07', 'etjk9090', '한도윤', '010-6666-7777', 'seller07@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '서울시 마포구 홍대입구역 22', '234-567-890123', '880-99-10008', '우리농산', '토스뱅크', 'seller08', 'erk3590s', '윤서준', '010-8888-9999', 'seller08@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '광주시 서구 상무대로 77', '345-678-901234', '990-10-11119', '로컬푸드팜', '광주은행', 'seller09', 'njk687', '장하윤', '010-9999-0000', 'seller09@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '대전시 유성구 대학로 33', '456-789-012345', '111-22-12220', '엄마밥상', '대전은행', 'seller10', 'lko090', '임채원', '010-1010-2020', 'seller10@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '울산시 남구 삼산로 15', '567-890-123456', '222-33-13331', '삼산푸드', '농협은행', 'seller11', '3490wrdf', '강은우', '010-1111-3333', 'seller11@test.com');

INSERT INTO SELLER (seller_no, business_address, settlement_account, business_no, store_name, bank_name, seller_id, seller_pw, ceo_name, tel, email)
VALUES (SEQ_SELLER.NEXTVAL, '경남 창원시 성산구 중앙대로 20', '901-234-567891', '666-77-17775', '창원식품', '경남은행', 'seller12', 'po189989', '문지안', '010-1515-7777', 'seller12@test.com');

COMMIT;
```

## 회원 (MEMBER)

```sql
-- 회원 ( MEMBER )
CREATE SEQUENCE SEQ_MEMBER_NO
START WITH 1
INCREMENT BY 1;

ALTER TABLE MEMBER
MODIFY MEMBER_NO
DEFAULT SEQ_MEMBER_NO.NEXTVAL;

INSERT INTO MEMBER (member_id, member_pw, member_name, phone, email, rank)
VALUES ('member1', 'AGeklJKKLJAGKLAklegjKL', '홍길동', '010-3222-1780', 'hong@gmail.com', '일반회원');

INSERT INTO MEMBER (member_id, member_pw, member_name, phone, email, rank)
VALUES ('member2', 'FAwAGeklJKKLJAGKLAklegjKL', '이상섭', '010-5272-3320', 'sangsub@gmail.com', '일반회원');

INSERT INTO MEMBER (member_id, member_pw, member_name, phone, email, rank)
VALUES ('member3', 'Ss1AGeklJKKLJAGKLAklegjKL', '박지호', '010-3155-5580', 'jiho123@gmail.com', '와우회원');
```

## 배송지

```sql
CREATE SEQUENCE SEQ_DELIVERY_ADDRESS_NO
START WITH 1
INCREMENT BY 1;

ALTER TABLE DELIVERY_ADDRESS
MODIFY ADDRESS_NO
DEFAULT SEQ_DELIVERY_ADDRESS_NO.NEXTVAL;

INSERT INTO DELIVERY_ADDRESS (member_no, receiver_name, tel, zipcode, address, detail_address, request_msg, address_default)
VALUES (1, '홍길동', '010-3222-1780', 06678, '서울특별시 서초구 청두곶10길 15-13', '101호', '배송요청사항', 'Y');

INSERT INTO DELIVERY_ADDRESS (member_no, receiver_name, tel, zipcode, address, detail_address, request_msg, address_default)
VALUES (2, '이상섭', '010-5272-3320', 135010, '서울특별시 강남구 논현동 106-7', '501호', '배송요청사항', 'Y');

INSERT INTO DELIVERY_ADDRESS (member_no, receiver_name, tel, zipcode, address, detail_address, request_msg, address_default)
VALUES (3, '박지호', '010-3155-5580', 06136, '서울특별시 강남구 논현로106길 26-4', '702호', '배송요청사항', 'Y');
```

## 환불

```sql
CREATE SEQUENCE SEQ_RETURN_NO
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- 테이블 자동증가
ALTER TABLE PRODUCT_RETURN
MODIFY return_no DEFAULT SEQ_RETURN_NO.NEXTVAL;

INSERT INTO PRODUCT_RETURN (request_date, return_qty, return_reason, return_status, refund_amount, order_detail_no)
VALUES (SYSDATE, 5, '제품 불량', '환불 완료', 65000, 22);

INSERT INTO PRODUCT_RETURN (request_date, return_qty, return_reason, return_status, refund_amount, order_detail_no)
VALUES (SYSDATE, 5, '제품 불량', '환불 완료', 94000, 29);

INSERT INTO PRODUCT_RETURN (request_date, return_qty, return_reason, return_status, refund_amount, order_detail_no)
VALUES (SYSDATE, 1, '단순 변심 환불', '환불 완료', 15000, 27);
```
