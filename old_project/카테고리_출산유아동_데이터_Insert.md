# CATEGORY 테이블 — 출산/유아동(3) 하위 데이터

`docs/capture/메뉴2/스크린샷 2026-08-20 181504.png`를 보면 출산/유아동(3)의 2단계 목록은 전부 보이지만
3단계는 `유아동패션`에 마우스를 올린 상태만 캡처돼 있습니다.

- **`유아동패션`의 3개 하위 항목(베이비, 여아/주니어, 남아/주니어)만 캡처에서 그대로 확인한 값**입니다.
- 나머지 15개 2단계 항목의 하위 3단계는 캡처에 없어서, **실제 쿠팡 출산/유아동 카테고리 구조를 참고해 제가 추측**해서 채운 것입니다.
- 목록 끝의 `더보기` 뒤에 더 있을 수 있는 2단계 항목은 넣지 않았습니다.

## ⚠ 참고: `유아동패션`은 사실 두 군데(패션의류/잡화, 출산/유아동)에 동시에 노출되는 메뉴입니다

캡처 속 `유아동패션 > 베이비/여아·주니어/남아·주니어`는 이전에 `패션의류/잡화(1)` 하위에 넣은
`유아동패션(105) > 베이비(10501)/여아·주니어(10502)/남아·주니어(10503)`와 **이름이 완전히 같습니다.**
실제 쿠팡 사이트는 같은 카테고리를 여러 메뉴 경로에서 접근하게 해주지만(다대다 관계),
지금 만든 `CATEGORY` 테이블은 `parent_category_no` 하나만 갖는 **트리 구조**라서 한 행이 부모를 두 개 가질 수 없습니다.

그래서 여기서는 `출산/유아동` 밑에 **새 category_no로 별도 행을 복제**해서 넣었습니다(아래 301, 30101~30103).
나중에 "이 두 유아동패션이 사실 같은 상품 집합을 가리켜야 한다"는 요구가 생기면, 트리가 아니라
`CATEGORY_MAP(category_no, menu_category_no)` 같은 다대다 매핑 테이블을 추가하는 방향으로 가야 합니다
(지금 당장 필요한 리팩터링은 아니라서 일단은 단순 복제로 처리했습니다).

## INSERT문

```sql
-- =========================================================
-- 2단계 (중분류) — 출산/유아동(3) 하위, 캡처에 보이는 순서 그대로
-- =========================================================
INSERT INTO CATEGORY VALUES (301, '유아동패션',       3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (302, '기저귀',           3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (303, '물티슈',           3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (304, '분유/어린이식품',   3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (305, '어린이 건강식품',   3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (306, '수유용품',         3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (307, '이유용품/유아식기', 3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (308, '아기띠/외출용품',   3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (309, '유모차/웨건',       3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (310, '카시트',           3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (311, '욕실용품/스킨케어', 3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (312, '위생/건강/세제',    3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (313, '유아가구/인테리어', 3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (314, '유아동침구',       3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (315, '매트/안전용품',     3, 2, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (316, '완구/교구',        3, 2, SYSDATE, 1);


-- =========================================================
-- 3단계 (소분류)
-- =========================================================

-- 유아동패션(301) 하위 — 캡처 원본 그대로
INSERT INTO CATEGORY VALUES (30101, '베이비',     301, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30102, '여아/주니어', 301, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30103, '남아/주니어', 301, 3, SYSDATE, 1);

-- 기저귀(302) 하위 — 추측
INSERT INTO CATEGORY VALUES (30201, '신생아기저귀',     302, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30202, '팬티기저귀',       302, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30203, '밴드기저귀',       302, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30204, '수영장기저귀',     302, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30205, '기저귀케이스/파우치', 302, 3, SYSDATE, 1);

-- 물티슈(303) 하위 — 추측
INSERT INTO CATEGORY VALUES (30301, '아기물티슈',   303, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30302, '캡형물티슈',   303, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30303, '리필물티슈',   303, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30304, '휴대용물티슈', 303, 3, SYSDATE, 1);

-- 분유/어린이식품(304) 하위 — 추측
INSERT INTO CATEGORY VALUES (30401, '조제분유',      304, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30402, '유아식/이유식',  304, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30403, '어린이간식',    304, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30404, '어린이음료',    304, 3, SYSDATE, 1);

-- 어린이 건강식품(305) 하위 — 추측
INSERT INTO CATEGORY VALUES (30501, '유산균',       305, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30502, '종합영양제',    305, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30503, '홍삼/면역',     305, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30504, '키건강식품',    305, 3, SYSDATE, 1);

-- 수유용품(306) 하위 — 추측
INSERT INTO CATEGORY VALUES (30601, '젖병',           306, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30602, '젖꼭지',         306, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30603, '유축기',         306, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30604, '수유쿠션',       306, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30605, '분유제조기/소독기', 306, 3, SYSDATE, 1);

-- 이유용품/유아식기(307) 하위 — 추측
INSERT INTO CATEGORY VALUES (30701, '이유식조리기',       307, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30702, '유아식기/식습관용품', 307, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30703, '빨대컵/트레이닝컵',   307, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30704, '턱받이',            307, 3, SYSDATE, 1);

-- 아기띠/외출용품(308) 하위 — 추측
INSERT INTO CATEGORY VALUES (30801, '아기띠/힙시트', 308, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30802, '웨건/왜건',     308, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30803, '유아배낭',      308, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30804, '외출가방',      308, 3, SYSDATE, 1);

-- 유모차/웨건(309) 하위 — 추측
INSERT INTO CATEGORY VALUES (30901, '절충형유모차',   309, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30902, '디럭스유모차',   309, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30903, '휴대용유모차',   309, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30904, '쌍둥이유모차',   309, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (30905, '유모차용품',     309, 3, SYSDATE, 1);

-- 카시트(310) 하위 — 추측
INSERT INTO CATEGORY VALUES (31001, '신생아카시트',   310, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31002, '컨버터블카시트', 310, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31003, '주니어카시트',   310, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31004, '카시트용품',     310, 3, SYSDATE, 1);

-- 욕실용품/스킨케어(311) 하위 — 추측
INSERT INTO CATEGORY VALUES (31101, '유아세정제',   311, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31102, '유아로션/오일', 311, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31103, '유아선케어',   311, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31104, '목욕용품',     311, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31105, '헤어용품',     311, 3, SYSDATE, 1);

-- 위생/건강/세제(312) 하위 — 추측
INSERT INTO CATEGORY VALUES (31201, '유아세제',       312, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31202, '살균/소독용품',   312, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31203, '체온계/건강용품', 312, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31204, '마스크',         312, 3, SYSDATE, 1);

-- 유아가구/인테리어(313) 하위 — 추측
INSERT INTO CATEGORY VALUES (31301, '아기침대',       313, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31302, '범퍼침대',       313, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31303, '안전문/게이트',   313, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31304, '러그/매트',      313, 3, SYSDATE, 1);

-- 유아동침구(314) 하위 — 추측
INSERT INTO CATEGORY VALUES (31401, '이불/베개',     314, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31402, '속싸개/겉싸개', 314, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31403, '매트리스커버',  314, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31404, '침구세트',     314, 3, SYSDATE, 1);

-- 매트/안전용품(315) 하위 — 추측
INSERT INTO CATEGORY VALUES (31501, '놀이방매트',        315, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31502, '안전커버/코너가드', 315, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31503, '안전벨트',         315, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31504, '콘센트커버',       315, 3, SYSDATE, 1);

-- 완구/교구(316) 하위 — 추측
INSERT INTO CATEGORY VALUES (31601, '원목완구',     316, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31602, '촉감놀이완구', 316, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31603, '교육완구',     316, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31604, '캐릭터완구',   316, 3, SYSDATE, 1);
INSERT INTO CATEGORY VALUES (31605, '야외놀이완구', 316, 3, SYSDATE, 1);

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
START WITH category_no = 3          -- 출산/유아동
CONNECT BY PRIOR category_no = parent_category_no
ORDER SIBLINGS BY category_no;
```

총 16(2단계) + 68(3단계) = **84건**이 추가로 들어갑니다.
