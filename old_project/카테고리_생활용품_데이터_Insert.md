# CATEGORY 테이블 — 생활용품(6) 하위 데이터

`docs/capture/메뉴2/스크린샷 2026-08-20 181529.png`를 보면 생활용품(6)의 2단계 목록은
`더보기` 전까지 16개가 보이고, 3단계는 `헤어`에 마우스를 올린 상태만 캡처돼 있습니다.

- **`헤어`의 4개 하위 항목만 캡처에서 그대로 확인한 값**입니다 (샴푸/린스, 트리트먼트/팩/앰플, 스타일링/케어/세트, 염색/파마).
- 나머지 15개 2단계 항목의 하위 3단계는 캡처에 없어서, **실제 쿠팡 생활용품 카테고리 구조를 참고해 제가 추측**해서 채운 것입니다.
- `더보기`에 화살표(▶)가 있어서 이 뒤로 2단계 항목이 더 있을 가능성이 높지만, 캡처가 없어 넣지 않았습니다.
- `신상관`은 이전과 마찬가지로 기획전 성격의 코너라 3단계가 특히 실제와 다를 수 있습니다.

## ⚠ 참고: `기저귀`도 다른 대분류와 이름이 겹칩니다

여기 2단계의 `기저귀`(607)는 이전에 넣은 `출산/유아동(3) > 기저귀(302)`와 이름이 같습니다.
`유아동패션` 때와 같은 이유로, 트리 구조상 한 행이 부모를 두 개 가질 수 없어서 여기서도 **새 번호로 별도 복제**했습니다.
(실제로 두 메뉴가 같은 상품을 가리켜야 한다면 나중에 매핑 테이블이 필요하다는 점은 이전 파일과 동일합니다.)

## INSERT문

```sql
-- =========================================================
-- 2단계 (중분류) — 생활용품(6) 하위, 캡처에 보이는 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (601, '신상관',           6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (602, '헤어',            6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (603, '바디/세안',        6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (604, '구강/면도',        6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (605, '화장지/물티슈',     6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (606, '생리대/성인용기저귀', 6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (607, '기저귀',           6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (608, '세탁세제',         6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (609, '청소/주방세제',     6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (610, '탈취/방향/살충',    6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (611, '건강/의료용품',     6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (612, '성인용품(19)',      6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (613, '세탁/청소용품',     6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (614, '욕실용품',         6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (615, '생활전기용품',      6, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (616, '수납/정리',        6, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 헤어(602) 하위 — 캡처 원본 그대로
INSERT INTO CATEGORY VALUES (60201, '샴푸/린스',       602, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60202, '트리트먼트/팩/앰플', 602, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60203, '스타일링/케어/세트', 602, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60204, '염색/파마',       602, 3, SYSDATE, 1);

-- 신상관(601) 하위 — 추측 (기획전 성격)
INSERT INTO CATEGORY VALUES (60101, '신상 생활용품', 601, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60102, '신상 헤어/바디', 601, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60103, '신상 세제',    601, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60104, '신상 수납용품', 601, 3, SYSDATE, 1);

-- 바디/세안(603) 하위 — 추측
INSERT INTO CATEGORY VALUES (60301, '바디워시',     603, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60302, '폼클렌징',     603, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60303, '비누',        603, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60304, '바디로션/오일', 603, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60305, '핸드워시',     603, 3, SYSDATE, 1);

-- 구강/면도(604) 하위 — 추측
INSERT INTO CATEGORY VALUES (60401, '칫솔/칫솔모', 604, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60402, '치약',       604, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60403, '구강청결제',  604, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60404, '면도기/면도날', 604, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60405, '면도용품',    604, 3, SYSDATE, 1);

-- 화장지/물티슈(605) 하위 — 추측
INSERT INTO CATEGORY VALUES (60501, '두루마리화장지', 605, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60502, '각티슈',        605, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60503, '물티슈',        605, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60504, '키친타월',      605, 3, SYSDATE, 1);

-- 생리대/성인용기저귀(606) 하위 — 추측
INSERT INTO CATEGORY VALUES (60601, '생리대',         606, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60602, '팬티라이너',     606, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60603, '생리컵',        606, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60604, '성인용기저귀/패드', 606, 3, SYSDATE, 1);

-- 기저귀(607) 하위 — 추측
INSERT INTO CATEGORY VALUES (60701, '신생아기저귀', 607, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60702, '팬티기저귀',   607, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60703, '밴드기저귀',   607, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60704, '수영장기저귀', 607, 3, SYSDATE, 1);

-- 세탁세제(608) 하위 — 추측
INSERT INTO CATEGORY VALUES (60801, '액체세제',   608, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60802, '가루세제',   608, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60803, '섬유유연제',  608, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60804, '표백제',     608, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60805, '세탁볼/망',   608, 3, SYSDATE, 1);

-- 청소/주방세제(609) 하위 — 추측
INSERT INTO CATEGORY VALUES (60901, '주방세제',     609, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60902, '욕실세제',     609, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60903, '락스/살균세제', 609, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (60904, '다목적세제',   609, 3, SYSDATE, 1);

-- 탈취/방향/살충(610) 하위 — 추측
INSERT INTO CATEGORY VALUES (61001, '탈취제', 610, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61002, '방향제', 610, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61003, '살충제', 610, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61004, '방충제', 610, 3, SYSDATE, 1);

-- 건강/의료용품(611) 하위 — 추측
INSERT INTO CATEGORY VALUES (61101, '마스크',        611, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61102, '손소독제',      611, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61103, '밴드/거즈',      611, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61104, '체온계',        611, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61105, '파스/찜질용품',  611, 3, SYSDATE, 1);

-- 성인용품(19)(612) 하위 — 추측
INSERT INTO CATEGORY VALUES (61201, '콘돔',       612, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61202, '러브젤',     612, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61203, '성인장난감',  612, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61204, '성인용품기타', 612, 3, SYSDATE, 1);

-- 세탁/청소용품(613) 하위 — 추측
INSERT INTO CATEGORY VALUES (61301, '청소솔/브러시', 613, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61302, '대걸레/밀대',   613, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61303, '고무장갑',      613, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61304, '청소포/걸레',   613, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61305, '빨래건조대',    613, 3, SYSDATE, 1);

-- 욕실용품(614) 하위 — 추측
INSERT INTO CATEGORY VALUES (61401, '샤워커튼',   614, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61402, '욕실화',     614, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61403, '수납선반',   614, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61404, '변기용품',   614, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61405, '발매트',     614, 3, SYSDATE, 1);

-- 생활전기용품(615) 하위 — 추측
INSERT INTO CATEGORY VALUES (61501, '건전지',       615, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61502, '멀티탭/충전기', 615, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61503, '손전등',       615, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61504, '소형가전',     615, 3, SYSDATE, 1);

-- 수납/정리(616) 하위 — 추측
INSERT INTO CATEGORY VALUES (61601, '수납박스',   616, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61602, '옷걸이',     616, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61603, '압축팩',     616, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61604, '서랍정리함',  616, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (61605, '선반/렉',    616, 3, SYSDATE, 1);

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
START WITH category_no = 6          -- 생활용품
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;
```

총 16(2단계) + 71(3단계) = **87건**이 추가로 들어갑니다.
