# CATEGORY 테이블 — 홈인테리어(7) 하위 데이터

`docs/capture/메뉴2/스크린샷 2026-08-20 181539.png`를 보면 홈인테리어(7)의 2단계 목록은
`더보기` 전까지 16개가 보이고, 3단계는 `여름 침구샵`에 마우스를 올린 상태만 캡처돼 있습니다.

- **`여름 침구샵`의 10개 하위 항목만 캡처에서 그대로 확인한 값**입니다 (이불/침구세트, 쿨매트/패드, 침구커버류, 베개, 대자리/러그/거실화, 커튼, 방석/쿠션, 모기장/안전망, 유아동침구, 문발/블라인드).
- 나머지 15개 2단계 항목의 하위 3단계는 캡처에 없어서, **실제 쿠팡 홈인테리어 카테고리 구조를 참고해 제가 추측**해서 채운 것입니다.
- `더보기`에 화살표(▶)가 있어서 2단계 항목이 더 있을 가능성이 높지만, 캡처가 없어 넣지 않았습니다.
- `프리미엄 조명` / `로켓설치가구`는 이전과 마찬가지로 기획전·서비스 성격의 코너라 3단계가 특히 실제와 다를 수 있습니다.

## ⚠ 참고: 여기도 이름이 겹치는 카테고리가 3개 있습니다

`생활전기용품`(714), `청소/세탁용품`(715), `욕실용품`(716)은 이미 `생활용품(6)` 밑에
`생활전기용품(615)`, `세탁/청소용품(613)`, `욕실용품(614)`으로 들어가 있는 것과 사실상 같은 카테고리입니다.
`유아동패션`, `기저귀` 때와 같은 이유로 트리 구조상 한 행이 부모를 두 개 가질 수 없어서 여기서도 **새 번호로 복제**했습니다.

## INSERT문

```sql
-- =========================================================
-- 2단계 (중분류) — 홈인테리어(7) 하위, 캡처에 보이는 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (701, '프리미엄 조명',   7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (702, '여름 침구샵',     7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (703, '로켓설치가구',    7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (704, '홈데코/디퓨저',   7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (705, '조명/스탠드',     7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (706, '커튼/블라인드',   7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (707, '카페트/쿠션',     7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (708, '가구',           7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (709, '수납/정리',       7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (710, '침대/매트리스',   7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (711, '침구',           7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (712, '수예/수선',       7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (713, '공구/철물/DIY',   7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (714, '생활전기용품',    7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (715, '청소/세탁용품',   7, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (716, '욕실용품',       7, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 여름 침구샵(702) 하위 — 캡처 원본 그대로
INSERT INTO CATEGORY VALUES (70201, '이불/침구세트',      702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70202, '쿨매트/패드',        702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70203, '침구커버류',         702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70204, '베개',              702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70205, '대자리/러그/거실화',  702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70206, '커튼',              702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70207, '방석/쿠션',          702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70208, '모기장/안전망',       702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70209, '유아동침구',         702, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70210, '문발/블라인드',       702, 3, SYSDATE, 1);

-- 프리미엄 조명(701) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (70101, '프리미엄 펜던트조명', 701, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70102, '프리미엄 스탠드조명', 701, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70103, '프리미엄 벽조명',    701, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70104, '디자이너조명',       701, 3, SYSDATE, 1);

-- 로켓설치가구(703) 하위 — 추측 (서비스 성격)
INSERT INTO CATEGORY VALUES (70301, '로켓설치 침대',       703, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70302, '로켓설치 소파',       703, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70303, '로켓설치 수납장',     703, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70304, '로켓설치 식탁/책상',   703, 3, SYSDATE, 1);

-- 홈데코/디퓨저(704) 하위 — 추측
INSERT INTO CATEGORY VALUES (70401, '디퓨저/캔들',   704, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70402, '조화/식물',     704, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70403, '액자/포스터',   704, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70404, '데코소품',      704, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70405, '홈프래그런스',   704, 3, SYSDATE, 1);

-- 조명/스탠드(705) 하위 — 추측
INSERT INTO CATEGORY VALUES (70501, '펜던트조명', 705, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70502, '천장등',    705, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70503, '스탠드조명', 705, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70504, '무드등',    705, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70505, '센서등',    705, 3, SYSDATE, 1);

-- 커튼/블라인드(706) 하위 — 추측
INSERT INTO CATEGORY VALUES (70601, '커튼',        706, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70602, '블라인드',     706, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70603, '롤스크린',     706, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70604, '커튼레일/용품', 706, 3, SYSDATE, 1);

-- 카페트/쿠션(707) 하위 — 추측
INSERT INTO CATEGORY VALUES (70701, '카펫/러그', 707, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70702, '방석',     707, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70703, '쿠션',     707, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70704, '매트',     707, 3, SYSDATE, 1);

-- 가구(708) 하위 — 추측
INSERT INTO CATEGORY VALUES (70801, '침대',        708, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70802, '소파',        708, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70803, '식탁/테이블',  708, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70804, '책상/의자',    708, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70805, '옷장/수납가구', 708, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70806, '화장대',      708, 3, SYSDATE, 1);

-- 수납/정리(709) 하위 — 추측
INSERT INTO CATEGORY VALUES (70901, '수납장',        709, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70902, '서랍장',        709, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70903, '선반/렉',       709, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70904, '정리함/바구니',  709, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (70905, '옷걸이/행거',    709, 3, SYSDATE, 1);

-- 침대/매트리스(710) 하위 — 추측
INSERT INTO CATEGORY VALUES (71001, '침대프레임', 710, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71002, '매트리스',   710, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71003, '토퍼',      710, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71004, '침대부속품', 710, 3, SYSDATE, 1);

-- 침구(711) 하위 — 추측
INSERT INTO CATEGORY VALUES (71101, '이불',          711, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71102, '베개',          711, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71103, '매트리스커버',   711, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71104, '침구세트',      711, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71105, '여름침구/쿨링침구', 711, 3, SYSDATE, 1);

-- 수예/수선(712) 하위 — 추측
INSERT INTO CATEGORY VALUES (71201, '재봉틀',      712, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71202, '원단/부자재',  712, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71203, '뜨개질용품',   712, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71204, '수선용품',     712, 3, SYSDATE, 1);

-- 공구/철물/DIY(713) 하위 — 추측
INSERT INTO CATEGORY VALUES (71301, '전동공구',      713, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71302, '수공구',        713, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71303, '철물/부속',     713, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71304, '접착/테이프',    713, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71305, '페인트/도장용품', 713, 3, SYSDATE, 1);

-- 생활전기용품(714) 하위 — 추측 (생활용품(6)의 615와 동일 계열, 별도 복제)
INSERT INTO CATEGORY VALUES (71401, '건전지',       714, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71402, '멀티탭/충전기', 714, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71403, '손전등',       714, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71404, '소형가전',     714, 3, SYSDATE, 1);

-- 청소/세탁용품(715) 하위 — 추측 (생활용품(6)의 613과 동일 계열, 별도 복제)
INSERT INTO CATEGORY VALUES (71501, '청소도구',   715, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71502, '세탁용품',   715, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71503, '빨래건조대', 715, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71504, '다림질용품', 715, 3, SYSDATE, 1);

-- 욕실용품(716) 하위 — 추측 (생활용품(6)의 614와 동일 계열, 별도 복제)
INSERT INTO CATEGORY VALUES (71601, '샤워커튼', 716, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71602, '욕실매트', 716, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71603, '수납선반', 716, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (71604, '변기용품', 716, 3, SYSDATE, 1);

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
START WITH category_no = 7          -- 홈인테리어
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;
```

총 16(2단계) + 77(3단계) = **93건**이 추가로 들어갑니다.
