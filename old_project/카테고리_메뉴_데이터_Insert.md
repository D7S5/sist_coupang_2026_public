# CATEGORY 테이블 — 실제 메뉴 스크린샷 기반 데이터

`docs/capture/메뉴/` 폴더의 쿠팡 카테고리 메뉴 캡처를 보고 재구성한 데이터입니다.
`CATEGORY` 테이블(자기참조 구조, `category_no` / `category_name` / `parent_category_no` / `category_level`)이
이미 만들어져 있고 기존 데이터는 삭제했다고 하셨으니, 아래는 INSERT문만 담았습니다.

## 캡처에서 확인한 내용

- 좌측 대분류 아이콘 메뉴 15개 (스크린샷 1)
- `패션의류/잡화` → `여성패션` → `의류/신발/가방·잡화` (스크린샷 2, 3)
- `패션의류/잡화` → `남성패션` (스크린샷 4)
- `패션의류/잡화` → `남녀 공용 의류` → 티셔츠 등 12개 (스크린샷 5)
- `패션의류/잡화` → `속옷/잠옷` → 4개 (스크린샷 6)
- `패션의류/잡화` → `유아동패션` → 3개 (스크린샷 7)
- 상품 목록 페이지의 브레드크럼: `쿠팡 홈 > 패션의류/잡화 > 남성패션 > 가방/잡화 > 가방 > 백팩` 및
  좌측 카테고리 필터의 `가방` 하위 14개 항목 (스크린샷 8) → **5단계 깊이**가 실제로 존재함을 확인

## 캡처에 없어서 추정/생략한 부분

- `여성패션 > 가방/잡화 > 가방`의 하위 14개 항목은 캡처에 없지만, 남성패션 쪽과 동일 구조일 가능성이 높아
  **동일하게 미러링**해서 넣었습니다. 실제 화면과 다르면 나중에 캡처 추가하고 수정하면 됩니다.
- `여성패션 > 의류`, `여성패션 > 신발`, `남성패션 > 의류`, `남성패션 > 신발`은 3단계까지만 확인되고
  그 하위(4단계)는 캡처가 없어서 **넣지 않았습니다.** (리프가 아니지만 일단 3단계 리프로 둠)
- `럭셔리패션`과 `패션의류/잡화`를 제외한 나머지 14개 대분류(뷰티, 출산/유아동, 식품 …)는
  하위 메뉴를 캡처하지 않아서 **1단계(대분류) 행만** 넣었습니다. 나중에 각 메뉴를 펼친 캡처를 추가하면
  같은 패턴으로 하위 INSERT문을 이어서 만들면 됩니다.

## INSERT문

```sql
-- =========================================================
-- 1단계 (대분류) — 좌측 아이콘 메뉴 15개, 스크린샷 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (1,  '패션의류/잡화', NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (2,  '뷰티',          NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (3,  '출산/유아동',    NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (4,  '식품',          NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (5,  '주방용품',       NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (6,  '생활용품',       NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (7,  '홈인테리어',     NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (8,  '가전디지털',     NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (9,  '스포츠/레저',    NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10, '자동차용품',     NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (11, '도서/음반/DVD',  NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (12, '완구/취미',      NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (13, '문구/오피스',    NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (14, '반려동물용품',   NULL, 1, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (15, '헬스/건강식품',  NULL, 1, SYSDATE, 1);


-- =========================================================
-- 2단계 (중분류) — 패션의류/잡화(1) 하위
-- =========================================================
INSERT INTO CATEGORY VALUES (101, '여성패션',      1, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102, '남성패션',      1, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (103, '남녀 공용 의류', 1, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (104, '속옷/잠옷',      1, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (105, '유아동패션',     1, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (106, '럭셔리패션',     1, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 여성패션(101) 하위
INSERT INTO CATEGORY VALUES (10101, '의류',      101, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10102, '신발',      101, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10103, '가방/잡화',  101, 3, SYSDATE, 1);

-- 남성패션(102) 하위
INSERT INTO CATEGORY VALUES (10201, '의류',      102, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10202, '신발',      102, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10203, '가방/잡화',  102, 3, SYSDATE, 1);

-- 남녀 공용 의류(103) 하위 — 전부 리프
INSERT INTO CATEGORY VALUES (10301, '티셔츠',        103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10302, '맨투맨/후드티',  103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10303, '셔츠',          103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10304, '바지',          103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10305, '트레이닝복',     103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10306, '후드집업/집업류', 103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10307, '니트류/조끼',    103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10308, '아우터',        103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10309, '테마의류',      103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10310, '커플룩/패밀리룩', 103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10311, '빅사이즈',      103, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10312, '스포츠의류',     103, 3, SYSDATE, 1);

-- 속옷/잠옷(104) 하위 — 전부 리프
INSERT INTO CATEGORY VALUES (10401, '여성 속옷',      104, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10402, '남성 속옷',      104, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10403, '기타 속옷 용품', 104, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10404, '잠옷/파자마',    104, 3, SYSDATE, 1);

-- 유아동패션(105) 하위 — 전부 리프
INSERT INTO CATEGORY VALUES (10501, '베이비',     105, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10502, '여아/주니어', 105, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (10503, '남아/주니어', 105, 3, SYSDATE, 1);


-- =========================================================
-- 4단계 (세분류) — 가방/잡화 하위의 '가방'
-- =========================================================
INSERT INTO CATEGORY VALUES (1010301, '가방', 10103, 4, SYSDATE, 1);  -- 여성패션 > 가방/잡화 > 가방
INSERT INTO CATEGORY VALUES (1020301, '가방', 10203, 4, SYSDATE, 1);  -- 남성패션 > 가방/잡화 > 가방


-- =========================================================
-- 5단계 — '가방' 하위 14개 (여성/남성 동일 구성으로 미러링)
-- =========================================================

-- 여성패션 > 가방/잡화 > 가방(1010301) 하위
INSERT INTO CATEGORY VALUES (101030101, '백팩',        1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030102, '크로스백',     1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030103, '숄더백',       1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030104, '토트백',       1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030105, '서류가방',     1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030106, '노트북가방',   1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030107, '클러치백',     1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030108, '힙색/슬링백',   1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030109, '캔버스/에코백', 1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030110, '기타캐주얼가방', 1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030111, '파우치/이너백', 1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030112, '스포츠가방',    1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030113, '가방액세서리',  1010301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (101030114, '여행가방/소품', 1010301, 5, SYSDATE, 1);

-- 남성패션 > 가방/잡화 > 가방(1020301) 하위 (캡처 원본)
INSERT INTO CATEGORY VALUES (102030101, '백팩',        1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030102, '크로스백',     1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030103, '숄더백',       1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030104, '토트백',       1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030105, '서류가방',     1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030106, '노트북가방',   1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030107, '클러치백',     1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030108, '힙색/슬링백',   1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030109, '캔버스/에코백', 1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030110, '기타캐주얼가방', 1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030111, '파우치/이너백', 1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030112, '스포츠가방',    1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030113, '가방액세서리',  1020301, 5, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (102030114, '여행가방/소품', 1020301, 5, SYSDATE, 1);

COMMIT;
```

## 확인용 조회

전에 만든 트리 조회 쿼리(3-1)를 그대로 돌려서 잘 들어갔는지 확인하면 됩니다.

```sql
SELECT LPAD(' ', (LEVEL - 1) * 2) || category_name AS category_tree,
       LEVEL                                        AS depth,
       SUBSTR(SYS_CONNECT_BY_PATH(category_name, '>'), 2) AS full_path,
       category_no,
       parent_category_no,
       CONNECT_BY_ISLEAF                             AS is_leaf
FROM CATEGORY
START WITH parent_category_no IS NULL
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;

-- 캡처에 나온 브레드크럼과 일치하는지 확인
-- 기대값: 패션의류/잡화>남성패션>가방/잡화>가방>백팩
SELECT full_path
FROM (
    SELECT SUBSTR(SYS_CONNECT_BY_PATH(category_name, '>'), 2) AS full_path,
           category_no
    FROM CATEGORY
    START WITH parent_category_no IS NULL
    CONNECT BY PRIOR category_no = parent_category_no
)
WHERE category_no = 102030101;   -- 백팩(남성)
```

총 76건(1단계 15 + 2단계 6 + 3단계 25 + 4단계 2 + 5단계 28)이 들어갑니다.
