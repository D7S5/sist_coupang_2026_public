# CATEGORY 테이블 — 뷰티(2) 하위 데이터

`docs/capture/메뉴2/스크린샷 2026-08-20 181454.png`를 보면 뷰티(2)의 2단계 목록은 전부 보이지만
3단계(하위 세부메뉴)는 `스킨케어`에 마우스를 올린 상태만 캡처돼 있습니다.

- **`스킨케어`의 8개 하위 항목만 캡처에서 그대로 확인한 값**입니다 (스킨, 로션, 에센스/세럼/앰플, 미스트, 오일, 크림/올인원, 기초세트, 마스크/팩).
- 나머지 15개 2단계 항목의 하위 3단계는 캡처에 없어서, **실제 쿠팡 뷰티 카테고리 구조를 참고해 제가 추측**해서 채운 것입니다.
  실제 화면과 다를 수 있으니, 정확도가 중요하면 나중에 각 항목에 마우스를 올린 캡처를 추가해서 검증/수정하는 걸 추천합니다.
- 목록 끝의 `더보기` 뒤에 더 있을 수 있는 2단계 항목(캡처에 안 보이는 것)은 넣지 않았습니다.
- `신상관` / `로드샵`은 실제 쿠팡에서는 상품 분류라기보다 "신상품 모아보기", "브랜드관 모음" 같은 **기획전 성격의 코너**에 가깝습니다.
  그래도 요청하신 대로 3단계까지 일관되게 채우기 위해 임의로 하위 분류를 만들어 넣었습니다 — 실제로는 카테고리 자체를 안 만들고
  상품 태그/기획전 페이지로 처리하는 게 더 정확할 수 있습니다.

뷰티(2)는 이미 이전 INSERT에서 들어가 있는 상태를 전제로, 2단계·3단계 행만 추가합니다.

## INSERT문

```sql
-- =========================================================
-- 2단계 (중분류) — 뷰티(2) 하위, 캡처에 보이는 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (201, '신상관',       2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (202, '럭셔리뷰티',    2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (203, '스킨케어',      2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (204, '클렌징/필링',   2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (205, '선케어/태닝',   2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (206, '더마코스메틱',  2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (207, '메이크업',      2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (208, '향수',         2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (209, '남성화장품',    2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (210, '네일',         2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (211, '뷰티소품',      2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (212, '어린이화장품',  2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (213, '로드샵',        2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (214, '클린/비건뷰티', 2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (215, '헤어',         2, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (216, '바디',         2, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 스킨케어(203) 하위 — 캡처 원본 그대로
INSERT INTO CATEGORY VALUES (20301, '스킨',           203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20302, '로션',           203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20303, '에센스/세럼/앰플', 203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20304, '미스트',          203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20305, '오일',           203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20306, '크림/올인원',      203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20307, '기초세트',        203, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20308, '마스크/팩',        203, 3, SYSDATE, 1);

-- 신상관(201) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (20101, '신상 스킨케어',  201, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20102, '신상 메이크업',  201, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20103, '신상 향수',      201, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20104, '신상 헤어/바디',  201, 3, SYSDATE, 1);

-- 럭셔리뷰티(202) 하위 — 추측
INSERT INTO CATEGORY VALUES (20201, '럭셔리 스킨케어',  202, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20202, '럭셔리 메이크업',  202, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20203, '럭셔리 향수',      202, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20204, '럭셔리 헤어/바디',  202, 3, SYSDATE, 1);

-- 클렌징/필링(204) 하위 — 추측
INSERT INTO CATEGORY VALUES (20401, '클렌징오일',      204, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20402, '클렌징폼',        204, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20403, '클렌징워터',      204, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20404, '클렌징밀크/크림',  204, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20405, '필링/스크럽',      204, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20406, '클렌징티슈/시트',  204, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20407, '클렌징기기',      204, 3, SYSDATE, 1);

-- 선케어/태닝(205) 하위 — 추측
INSERT INTO CATEGORY VALUES (20501, '선크림',        205, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20502, '선쿠션/선스틱',   205, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20503, '선스프레이',     205, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20504, '애프터선',       205, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20505, '태닝오일/크림',   205, 3, SYSDATE, 1);

-- 더마코스메틱(206) 하위 — 추측
INSERT INTO CATEGORY VALUES (20601, '더마스킨케어',    206, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20602, '더마바디케어',    206, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20603, '더마헤어케어',    206, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20604, '트러블/여드름케어', 206, 3, SYSDATE, 1);

-- 메이크업(207) 하위 — 추측
INSERT INTO CATEGORY VALUES (20701, '베이스메이크업',  207, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20702, '립메이크업',      207, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20703, '아이메이크업',    207, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20704, '메이크업소품',    207, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20705, '메이크업픽서',    207, 3, SYSDATE, 1);

-- 향수(208) 하위 — 추측
INSERT INTO CATEGORY VALUES (20801, '여성향수',       208, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20802, '남성향수',       208, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20803, '유니섹스향수',    208, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20804, '바디미스트/코롱', 208, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20805, '디퓨저/캔들',    208, 3, SYSDATE, 1);

-- 남성화장품(209) 하위 — 추측
INSERT INTO CATEGORY VALUES (20901, '남성스킨케어',     209, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20902, '남성쉐이빙',       209, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20903, '남성바디/헤어케어', 209, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (20904, '남성메이크업',     209, 3, SYSDATE, 1);

-- 네일(210) 하위 — 추측
INSERT INTO CATEGORY VALUES (21001, '네일컬러',       210, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21002, '네일아트용품',    210, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21003, '네일케어',       210, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21004, '인조손톱/네일팁', 210, 3, SYSDATE, 1);

-- 뷰티소품(211) 하위 — 추측
INSERT INTO CATEGORY VALUES (21101, '메이크업브러시',  211, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21102, '퍼프/스펀지',     211, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21103, '미용기기',       211, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21104, '화장솜/면봉',     211, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21105, '헤어소품',       211, 3, SYSDATE, 1);

-- 어린이화장품(212) 하위 — 추측
INSERT INTO CATEGORY VALUES (21201, '어린이스킨케어',   212, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21202, '어린이메이크업',   212, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21203, '어린이향수',       212, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21204, '어린이목욕용품',   212, 3, SYSDATE, 1);

-- 로드샵(213) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (21301, '로드샵 스킨케어', 213, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21302, '로드샵 메이크업', 213, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21303, '로드샵 클렌징',   213, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21304, '로드샵 바디/헤어', 213, 3, SYSDATE, 1);

-- 클린/비건뷰티(214) 하위 — 추측
INSERT INTO CATEGORY VALUES (21401, '비건 스킨케어',   214, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21402, '비건 메이크업',   214, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21403, '비건 헤어/바디',  214, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21404, '크루얼티프리',    214, 3, SYSDATE, 1);

-- 헤어(215) 하위 — 추측
INSERT INTO CATEGORY VALUES (21501, '샴푸/린스',        215, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21502, '헤어트리트먼트/팩', 215, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21503, '두피케어',        215, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21504, '헤어스타일링',     215, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21505, '염색/펌',         215, 3, SYSDATE, 1);

-- 바디(216) 하위 — 추측
INSERT INTO CATEGORY VALUES (21601, '바디워시',       216, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21602, '바디로션/크림',   216, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21603, '핸드/풋케어',     216, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21604, '데오드란트',      216, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (21605, '입욕제',         216, 3, SYSDATE, 1);

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
START WITH category_no = 2          -- 뷰티
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;
```

총 16(2단계) + 77(3단계: 스킨케어 8 + 나머지 15개 × 4~7개) = **93건**이 추가로 들어갑니다.
