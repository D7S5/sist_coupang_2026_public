# CATEGORY 테이블 — 식품(4) 하위 데이터

`docs/capture/메뉴2/스크린샷 2026-08-20 181512.png`를 보면 식품(4)의 2단계 목록은 전부 보이지만
3단계는 `식품 선물세트`에 마우스를 올린 상태만 캡처돼 있습니다.

- **`식품 선물세트`의 9개 하위 항목만 캡처에서 그대로 확인한 값**입니다 (과일, 나물/버섯, 견과, 정육, 수산/건어물, 커피/차/음료, 과자/간식, 즉석/가공식품, 건강).
- 나머지 15개 2단계 항목의 하위 3단계는 캡처에 없어서, **실제 쿠팡 식품 카테고리 구조를 참고해 제가 추측**해서 채운 것입니다.
- 목록 끝의 `더보기` 뒤에 더 있을 수 있는 2단계 항목은 넣지 않았습니다.
- `추천 기획전` / `프리미엄 푸드` / `수입식품관`은 앞서 뷰티의 `신상관`·`로드샵`과 마찬가지로 실제로는 상품 분류가 아니라
  **기획전/큐레이션 코너**에 가깝습니다. 요청하신 대로 3단계까지 채우긴 했지만, 이 세 개는 특히 실제 화면과 다를 가능성이 큽니다.

## INSERT문

```sql
-- =========================================================
-- 2단계 (중분류) — 식품(4) 하위, 캡처에 보이는 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (401, '추천 기획전',        4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (402, '프리미엄 푸드',       4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (403, '수입식품관',         4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (404, '식품 선물세트',       4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (405, '건강식품',           4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (406, '생수/음료',          4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (407, '커피/원두/차',        4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (408, '과자/초콜릿/시리얼',   4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (409, '건과/견과',          4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (410, '반찬/간편식/대용식',   4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (411, '면/통조림/가공식품',   4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (412, '가루/조미료/오일',     4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (413, '장/소스/드레싱/식초',  4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (414, '냉장/냉동/간편요리',   4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (415, '과일',              4, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (416, '축산/계란/식용곤충',   4, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 식품 선물세트(404) 하위 — 캡처 원본 그대로
INSERT INTO CATEGORY VALUES (40401, '과일',        404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40402, '나물/버섯',    404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40403, '견과',        404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40404, '정육',        404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40405, '수산/건어물',  404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40406, '커피/차/음료', 404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40407, '과자/간식',    404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40408, '즉석/가공식품', 404, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40409, '건강',        404, 3, SYSDATE, 1);

-- 추천 기획전(401) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (40101, '신상 식품',    401, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40102, '특가 식품',    401, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40103, '베스트 식품',  401, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40104, '시즌 한정 식품', 401, 3, SYSDATE, 1);

-- 프리미엄 푸드(402) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (40201, '프리미엄 과일',   402, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40202, '프리미엄 정육',   402, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40203, '프리미엄 수산',   402, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40204, '프리미엄 가공식품', 402, 3, SYSDATE, 1);

-- 수입식품관(403) 하위 — 추측
INSERT INTO CATEGORY VALUES (40301, '수입 과자/초콜릿', 403, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40302, '수입 음료',       403, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40303, '수입 소스/조미료', 403, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40304, '수입 커피/차',    403, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40305, '수입 가공식품',   403, 3, SYSDATE, 1);

-- 건강식품(405) 하위 — 추측
INSERT INTO CATEGORY VALUES (40501, '홍삼/인삼',     405, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40502, '비타민/미네랄',  405, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40503, '유산균',       405, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40504, '오메가3',      405, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40505, '다이어트식품',  405, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40506, '단백질보충제',  405, 3, SYSDATE, 1);

-- 생수/음료(406) 하위 — 추측
INSERT INTO CATEGORY VALUES (40601, '생수',    406, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40602, '탄산음료', 406, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40603, '주스',    406, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40604, '이온음료', 406, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40605, '커피음료', 406, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40606, '차음료',   406, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40607, '건강음료', 406, 3, SYSDATE, 1);

-- 커피/원두/차(407) 하위 — 추측
INSERT INTO CATEGORY VALUES (40701, '원두커피',    407, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40702, '인스턴트커피', 407, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40703, '캡슐커피',    407, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40704, '티백/차',     407, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40705, '전통차',      407, 3, SYSDATE, 1);

-- 과자/초콜릿/시리얼(408) 하위 — 추측
INSERT INTO CATEGORY VALUES (40801, '스낵/과자',      408, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40802, '초콜릿',        408, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40803, '캔디/젤리',      408, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40804, '시리얼/그래놀라', 408, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40805, '비스킷/쿠키',    408, 3, SYSDATE, 1);

-- 건과/견과(409) 하위 — 추측
INSERT INTO CATEGORY VALUES (40901, '견과류',   409, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40902, '건과일',   409, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40903, '견과가공품', 409, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (40904, '씨앗류',   409, 3, SYSDATE, 1);

-- 반찬/간편식/대용식(410) 하위 — 추측
INSERT INTO CATEGORY VALUES (41001, '즉석반찬',      410, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41002, '국/탕/찌개',     410, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41003, '도시락/삼각김밥', 410, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41004, '다이어트대용식',  410, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41005, '죽/스프',        410, 3, SYSDATE, 1);

-- 면/통조림/가공식품(411) 하위 — 추측
INSERT INTO CATEGORY VALUES (41101, '라면',        411, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41102, '국수/파스타',  411, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41103, '통조림',      411, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41104, '냉동식품',    411, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41105, '가공육',      411, 3, SYSDATE, 1);

-- 가루/조미료/오일(412) 하위 — 추측
INSERT INTO CATEGORY VALUES (41201, '설탕/소금',   412, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41202, '밀가루/전분', 412, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41203, '식용유',     412, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41204, '향신료',     412, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41205, '천연조미료',  412, 3, SYSDATE, 1);

-- 장/소스/드레싱/식초(413) 하위 — 추측
INSERT INTO CATEGORY VALUES (41301, '고추장/된장',   413, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41302, '간장',         413, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41303, '소스/드레싱',   413, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41304, '식초',         413, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41305, '마요네즈/케찹', 413, 3, SYSDATE, 1);

-- 냉장/냉동/간편요리(414) 하위 — 추측
INSERT INTO CATEGORY VALUES (41401, '냉동간편식',   414, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41402, '냉장간편식',   414, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41403, '밀키트',      414, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41404, '냉동만두/피자', 414, 3, SYSDATE, 1);

-- 과일(415) 하위 — 추측
INSERT INTO CATEGORY VALUES (41501, '사과/배',       415, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41502, '감귤류',        415, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41503, '포도/베리류',    415, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41504, '바나나/열대과일', 415, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41505, '멜론/수박',      415, 3, SYSDATE, 1);

-- 축산/계란/식용곤충(416) 하위 — 추측
INSERT INTO CATEGORY VALUES (41601, '소고기',    416, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41602, '돼지고기',  416, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41603, '닭고기',    416, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41604, '계란/난류',  416, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (41605, '식용곤충',  416, 3, SYSDATE, 1);

COMMIT;
```

## 확인용 조회

```sql
SELECT LPAD(' ', (LEVEL - 1) * 2) || category_name AS category_tree,
       LEVEL                                        AS depth,
       SUBSTR(SYS_CONNECT_BY_PATH(category_name, '>'), 2) AS full_path,
       category_no,
       parent_category_no
FROM CATEGORY
START WITH category_no = 4          -- 식품
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;
```

총 16(2단계) + 83(3단계) = **99건**이 추가로 들어갑니다.
