# CATEGORY 테이블 — 주방용품(5) 하위 데이터

`docs/capture/메뉴2/스크린샷 2026-08-20 181520.png`를 보면 주방용품(5)의 2단계 목록은 전부 보이지만
3단계는 `주방조리도구`에 마우스를 올린 상태만 캡처돼 있습니다.

- **`주방조리도구`의 12개 하위 항목만 캡처에서 그대로 확인한 값**입니다 (조리도구, 조리도구세트, 가위/슬라이서/스퀴저, 믹싱볼/대야, 채반/소쿠리, 다지기/절구/밀대, 석쇠/버너/화로, 야채탈수기, 간식/도시락조리도구, 베이킹용품, 칼, 도마).
- 나머지 14개 2단계 항목의 하위 3단계는 캡처에 없어서, **실제 쿠팡 주방용품 카테고리 구조를 참고해 제가 추측**해서 채운 것입니다.
- 이 캡처에는 `더보기`가 안 보여서 2단계 15개가 전부라고 보고 처리했습니다.
- `프리미엄 키친` / `브랜드 스토어`는 앞서 나온 `신상관`·`로드샵`·`프리미엄 푸드`처럼 상품 분류가 아니라
  **기획전/브랜드관 성격의 코너**라, 이 둘의 3단계는 특히 실제 화면과 다를 가능성이 큽니다.

## INSERT문

```sql
-- =========================================================
-- 2단계 (중분류) — 주방용품(5) 하위, 캡처에 보이는 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (501, '프리미엄 키친',       5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (502, '브랜드 스토어',        5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (503, '냄비/프라이팬',        5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (504, '주방조리도구',         5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (505, '그릇/홈세트',          5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (506, '컵/텀블러/와인용품',    5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (507, '밀폐저장/도시락',       5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (508, '수저/커트러리',        5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (509, '주방잡화',            5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (510, '주방수납/정리',        5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (511, '주전자/커피/티용품',    5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (512, '일회용품/종이컵',      5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (513, '주방가전',            5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (514, '1인가구 주방용품',     5, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (515, '보온/보냉용품',        5, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 주방조리도구(504) 하위 — 캡처 원본 그대로
INSERT INTO CATEGORY VALUES (50401, '조리도구',          504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50402, '조리도구세트',       504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50403, '가위/슬라이서/스퀴저', 504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50404, '믹싱볼/대야',       504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50405, '채반/소쿠리',       504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50406, '다지기/절구/밀대',   504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50407, '석쇠/버너/화로',     504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50408, '야채탈수기',        504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50409, '간식/도시락조리도구', 504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50410, '베이킹용품',        504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50411, '칼',               504, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50412, '도마',             504, 3, SYSDATE, 1);

-- 프리미엄 키친(501) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (50101, '프리미엄 냄비/팬',   501, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50102, '프리미엄 칼',       501, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50103, '프리미엄 식기',     501, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50104, '프리미엄 소형가전',  501, 3, SYSDATE, 1);

-- 브랜드 스토어(502) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (50201, '국내브랜드관',       502, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50202, '해외브랜드관',       502, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50203, '셰프 컬래버레이션',  502, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50204, '신규입점브랜드',     502, 3, SYSDATE, 1);

-- 냄비/프라이팬(503) 하위 — 추측
INSERT INTO CATEGORY VALUES (50301, '냄비',           503, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50302, '프라이팬',        503, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50303, '궁중팬/웍',       503, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50304, '압력솥',         503, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50305, '냄비세트',        503, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50306, '뚝배기/전골냄비',  503, 3, SYSDATE, 1);

-- 그릇/홈세트(505) 하위 — 추측
INSERT INTO CATEGORY VALUES (50501, '밥그릇/국그릇',   505, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50502, '접시',          505, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50503, '볼/찬기',        505, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50504, '홈세트/식기세트', 505, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50505, '유리/내열용기',   505, 3, SYSDATE, 1);

-- 컵/텀블러/와인용품(506) 하위 — 추측
INSERT INTO CATEGORY VALUES (50601, '머그컵',        506, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50602, '텀블러',        506, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50603, '유리컵',        506, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50604, '와인잔/디캔터',  506, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50605, '와인오프너/용품', 506, 3, SYSDATE, 1);

-- 밀폐저장/도시락(507) 하위 — 추측
INSERT INTO CATEGORY VALUES (50701, '밀폐용기',   507, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50702, '지퍼백/랩',   507, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50703, '도시락통',   507, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50704, '보관용기세트', 507, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50705, '진공용기',   507, 3, SYSDATE, 1);

-- 수저/커트러리(508) 하위 — 추측
INSERT INTO CATEGORY VALUES (50801, '수저세트',    508, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50802, '젓가락',      508, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50803, '스푼/포크',    508, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50804, '커트러리세트', 508, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50805, '아기수저',    508, 3, SYSDATE, 1);

-- 주방잡화(509) 하위 — 추측
INSERT INTO CATEGORY VALUES (50901, '행주/수세미',   509, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50902, '고무장갑',      509, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50903, '앞치마',       509, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50904, '냄비받침/집게', 509, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (50905, '주방소품',      509, 3, SYSDATE, 1);

-- 주방수납/정리(510) 하위 — 추측
INSERT INTO CATEGORY VALUES (51001, '수납선반',       510, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51002, '식기건조대',     510, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51003, '냉장고정리용품',  510, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51004, '수납바구니',     510, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51005, '씽크대정리대',    510, 3, SYSDATE, 1);

-- 주전자/커피/티용품(511) 하위 — 추측
INSERT INTO CATEGORY VALUES (51101, '주전자',         511, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51102, '커피포트/드립용품', 511, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51103, '티포트',         511, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51104, '커피메이커',      511, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51105, '티/커피소품',     511, 3, SYSDATE, 1);

-- 일회용품/종이컵(512) 하위 — 추측
INSERT INTO CATEGORY VALUES (51201, '종이컵',        512, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51202, '일회용접시',     512, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51203, '나무젓가락/포크', 512, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51204, '위생장갑',       512, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51205, '호일/랩',        512, 3, SYSDATE, 1);

-- 주방가전(513) 하위 — 추측
INSERT INTO CATEGORY VALUES (51301, '전자레인지',    513, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51302, '에어프라이어',   513, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51303, '믹서기/블렌더',  513, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51304, '전기포트',      513, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51305, '토스터',        513, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51306, '식기세척기',    513, 3, SYSDATE, 1);

-- 1인가구 주방용품(514) 하위 — 추측
INSERT INTO CATEGORY VALUES (51401, '미니냄비/팬',    514, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51402, '1인용식기',     514, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51403, '소형밥솥',      514, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51404, '1인용조리도구',  514, 3, SYSDATE, 1);

-- 보온/보냉용품(515) 하위 — 추측
INSERT INTO CATEGORY VALUES (51501, '보온병',        515, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51502, '보냉백/아이스박스', 515, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51503, '보온텀블러',     515, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (51504, '보온도시락',     515, 3, SYSDATE, 1);

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
START WITH category_no = 5          -- 주방용품
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;
```

총 15(2단계) + 80(3단계) = **95건**이 추가로 들어갑니다.
