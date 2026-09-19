# SAMPLE_PRODUCT 테이블 (Oracle)

`search-products.json` 의 필드를 그대로 오라클 컬럼으로 매핑한 샘플 테이블.
Oracle은 boolean 타입이 없어서 `rocket`/`ad`/`coupon`/`freeShipping` 은
`CHAR(1)` `'Y'/'N'` 으로 변환함.

## DDL

```sql
CREATE TABLE SAMPLE_PRODUCT (
  ID                NUMBER(10)      NOT NULL,
  BRAND             VARCHAR2(100)   NOT NULL,
  NAME              VARCHAR2(200)   NOT NULL,
  IMAGE             VARCHAR2(300),
  HUE               NUMBER(3)       DEFAULT 0     NOT NULL,
  PRICE             NUMBER(10)      NOT NULL,
  ORIGINAL_PRICE    NUMBER(10),
  DISCOUNT_RATE     NUMBER(3),
  RATING            NUMBER(2,1)     DEFAULT 0     NOT NULL,
  REVIEW_COUNT      NUMBER(10)      DEFAULT 0     NOT NULL,
  ROCKET_YN         CHAR(1)         DEFAULT 'N'   NOT NULL,
  AD_YN             CHAR(1)         DEFAULT 'N'   NOT NULL,
  COUPON_YN         CHAR(1)         DEFAULT 'N'   NOT NULL,
  FREE_SHIPPING_YN  CHAR(1)         DEFAULT 'Y'   NOT NULL,
  CONSTRAINT PK_SAMPLE_PRODUCT PRIMARY KEY (ID),
  CONSTRAINT CK_SAMPLE_PRODUCT_ROCKET CHECK (ROCKET_YN IN ('Y','N')),
  CONSTRAINT CK_SAMPLE_PRODUCT_AD     CHECK (AD_YN IN ('Y','N')),
  CONSTRAINT CK_SAMPLE_PRODUCT_COUPON CHECK (COUPON_YN IN ('Y','N')),
  CONSTRAINT CK_SAMPLE_PRODUCT_FREE   CHECK (FREE_SHIPPING_YN IN ('Y','N'))
);

COMMENT ON TABLE  SAMPLE_PRODUCT              IS '검색결과 페이지용 샘플 상품';
COMMENT ON COLUMN SAMPLE_PRODUCT.ID           IS '상품 ID (PK)';
COMMENT ON COLUMN SAMPLE_PRODUCT.BRAND        IS '브랜드명';
COMMENT ON COLUMN SAMPLE_PRODUCT.NAME         IS '상품명';
COMMENT ON COLUMN SAMPLE_PRODUCT.IMAGE        IS '상품 이미지 경로';
COMMENT ON COLUMN SAMPLE_PRODUCT.HUE          IS '자리표시자 배경 색상값(0~360)';
COMMENT ON COLUMN SAMPLE_PRODUCT.PRICE        IS '판매가';
COMMENT ON COLUMN SAMPLE_PRODUCT.ORIGINAL_PRICE IS '할인 전 정가 (할인 없으면 NULL)';
COMMENT ON COLUMN SAMPLE_PRODUCT.DISCOUNT_RATE  IS '할인율(%) (할인 없으면 NULL)';
COMMENT ON COLUMN SAMPLE_PRODUCT.RATING       IS '평점 (0.0~5.0)';
COMMENT ON COLUMN SAMPLE_PRODUCT.REVIEW_COUNT IS '리뷰 개수';
COMMENT ON COLUMN SAMPLE_PRODUCT.ROCKET_YN    IS '로켓배송 여부 (Y/N)';
COMMENT ON COLUMN SAMPLE_PRODUCT.AD_YN        IS '광고(AD) 상품 여부 (Y/N)';
COMMENT ON COLUMN SAMPLE_PRODUCT.COUPON_YN    IS '쿠폰할인 적용 여부 (Y/N)';
COMMENT ON COLUMN SAMPLE_PRODUCT.FREE_SHIPPING_YN IS '무료배송 여부 (Y/N)';
```

## Sample INSERT

```sql
-- id 1: 할인 + 로켓배송
INSERT INTO SAMPLE_PRODUCT
  (ID, BRAND, NAME, IMAGE, HUE, PRICE, ORIGINAL_PRICE, DISCOUNT_RATE, RATING, REVIEW_COUNT, ROCKET_YN, AD_YN, COUPON_YN, FREE_SHIPPING_YN)
VALUES
  (1, '베이직루트', '데일리핏 오버핏 반팔 티셔츠 박스핏 무지티', './pds/short_2.jpg', 200, 12900, 21500, 40, 4.8, 3210, 'Y', 'N', 'N', 'Y');

-- id 3: 할인 없음 + 로켓배송 아님
INSERT INTO SAMPLE_PRODUCT
  (ID, BRAND, NAME, IMAGE, HUE, PRICE, ORIGINAL_PRICE, DISCOUNT_RATE, RATING, REVIEW_COUNT, ROCKET_YN, AD_YN, COUPON_YN, FREE_SHIPPING_YN)
VALUES
  (3, '쿨텍스', '여름 냉감 쿨링 반팔 티셔츠', './pds/short_2.jpg', 160, 15900, NULL, NULL, 4.2, 542, 'N', 'N', 'N', 'Y');

-- id 8: 할인 + 로켓배송 + 쿠폰
INSERT INTO SAMPLE_PRODUCT
  (ID, BRAND, NAME, IMAGE, HUE, PRICE, ORIGINAL_PRICE, DISCOUNT_RATE, RATING, REVIEW_COUNT, ROCKET_YN, AD_YN, COUPON_YN, FREE_SHIPPING_YN)
VALUES
  (8, '베이직루트', '골지 반팔 티셔츠 남녀공용', './pds/short_2.jpg', 200, 11900, 15900, 25, 4.8, 6720, 'Y', 'N', 'Y', 'Y');

-- id 11: 광고(AD) 상품, 로켓배송 아님
INSERT INTO SAMPLE_PRODUCT
  (ID, BRAND, NAME, IMAGE, HUE, PRICE, ORIGINAL_PRICE, DISCOUNT_RATE, RATING, REVIEW_COUNT, ROCKET_YN, AD_YN, COUPON_YN, FREE_SHIPPING_YN)
VALUES
  (11, '스트릿모먼트', '프린팅 그래픽 반팔 티셔츠', './pds/short_2.jpg', 260, 19900, 27900, 29, 4.2, 430, 'N', 'Y', 'N', 'Y');

-- id 19: 할인/로켓배송/쿠폰 모두 없음 (가장 기본형)
INSERT INTO SAMPLE_PRODUCT
  (ID, BRAND, NAME, IMAGE, HUE, PRICE, ORIGINAL_PRICE, DISCOUNT_RATE, RATING, REVIEW_COUNT, ROCKET_YN, AD_YN, COUPON_YN, FREE_SHIPPING_YN)
VALUES
  (19, '액티브핏', '머슬핏 근육핏 반팔 티셔츠', './pds/short_2.jpg', 190, 19900, NULL, NULL, 4.2, 780, 'N', 'N', 'N', 'Y');

COMMIT;
```

## 필드 매핑 요약

| JSON 필드 | 컬럼 | 비고 |
|---|---|---|
| `id` | `ID` | PK |
| `brand`, `name`, `image` | `BRAND`, `NAME`, `IMAGE` | 그대로 |
| `hue` | `HUE` | 0~360 (자리표시자 색상용) |
| `price`, `originalPrice`, `discountRate` | `PRICE`, `ORIGINAL_PRICE`, `DISCOUNT_RATE` | 할인 없으면 뒤 둘은 NULL |
| `rating`, `reviewCount` | `RATING`, `REVIEW_COUNT` | |
| `rocket`, `ad`, `coupon`, `freeShipping` | `*_YN` | true→'Y', false→'N' |

이후 자바(서블릿/JSP) 쪽에서 이 테이블을 조회해 지금 `search-products.json`과 같은 모양의 JSON으로 응답하는 API를 만들면, `search.js`의 `fetch('data/search-products.json')` 한 줄만 실제 API 주소로 바꿔서 그대로 연동할 수 있음.

23c 이상이라면 `CHAR(1)` 'Y'/'N' 대신 네이티브 `BOOLEAN` 타입도 가능. 그 경우 JSON 응답 매핑을 `true/false` 로 맞추는 쪽이 프론트 코드와 더 잘 맞음.
