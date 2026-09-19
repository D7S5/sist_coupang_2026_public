package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.goodpang.dto.CategoryDTO;
import com.goodpang.dto.CategoryProductDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 카테고리 목록 페이지(/category?categoryNo=...) 조회 전용 DAO.
 */
public class CategoryProductDAO {

    // 정렬 화이트리스트
    public enum Sort {
        LATEST, PRICE_ASC, PRICE_DESC, SALE_COUNT, RANKING
    }
    
    /*
     * "이 카테고리 + 그 밑에 딸린 카테고리 전부" 를 뜻하는 조건 (2026-09-03 추가).
     *
     * 중분류 페이지(예: 남녀 공용 의류)는 자기 밑의 소분류(티셔츠·바지…) 상품까지 다 보여줘야 하는데,
     * 예전 조건인 `P.SUB_CATEGORY_NO = ?` 는 딱 한 카테고리만 골라서 중분류로 열면 상품이 0개가 됩니다.
     *
     * START WITH / CONNECT BY 는 오라클이 "부모-자식으로 이어진 줄기"를 따라가는 문법입니다.
     *   START WITH CATEGORY_NO = ?                        → 여기서 출발해서
     *   CONNECT BY PRIOR CATEGORY_NO = PARENT_CATEGORY_NO → 자식 방향으로 계속 내려가며 모아라
     * 그래서 103(남녀 공용 의류)을 넣으면 103, 10301, 10302 … 10312 가 전부 나옵니다.
     *
     * ★ 소분류(10301 등)를 넣으면 자기 밑에 자식이 없으니 자기 자신 하나만 나옵니다.
     *   = 예전 `= ?` 와 결과가 완전히 같아서, 소분류 페이지는 아무것도 안 바뀝니다(그래서 분기 없이 이걸로 통일).
     * ★ 물음표(?) 개수도 예전과 똑같이 1개라 자바 쪽 setInt 순서는 손댈 필요가 없습니다.
     */
    private static final String CATEGORY_TREE_CONDITION = """
            P.SUB_CATEGORY_NO IN (
                    SELECT CATEGORY_NO FROM CATEGORY
                    START WITH CATEGORY_NO = ?
                    CONNECT BY PRIOR CATEGORY_NO = PARENT_CATEGORY_NO
                  )""";

    /*
     * "손님 화면에 보여도 되는 상품" 조건 (2026-09-06 신설).
     *
     * 전에는 이 두 줄이 목록 SQL 안에만 직접 적혀 있었고, 색상 필터 목록을 뽑는
     * findColorOptionsByCategory() 에는 아예 없었음 → 목록에 안 나오는 상품의 색상이 필터에 떠서
     * 고르면 결과가 0개가 되는 문제가 있었음(27번으로 실제로 겪음). 그래서 상수로 빼서 두 곳이 같은
     * 기준을 쓰게 함.
     *
     * 2026-09-06 이전에는 `SALE_STATUS != '승인 대기'` 뿐이라 '판매 중지' 상품(29번)이 목록에 그대로
     * 나왔음. '품절'은 일부러 안 뺌 — 품절은 목록에서 빼지 않고 회색 처리해서 보여주기로 확정된 사항.
     */
    private static final String VISIBLE_PRODUCT_CONDITION = """
              AND P.SALE_STATUS NOT IN ('승인 대기', '판매 중지')
              AND P.DISPLAY_YN = 'Y'
            """;

    /* ===== 2026-09-06 이전 버전 — 색상 필터와 상관없이 항상 "첫 옵션"을 대표로 쓰던 것. 되돌릴 때를 위해 남겨둠 =====
    // 상품 목록 하나의 SELECT 본문 + FROM/JOIN 은 count 조회와 겹쳐서 재사용.
    // 색상 조건은 다중선택(2026-09-02)이라 자리표시자 개수가 매번 달라져서 상수에서 뺐음 — buildColorCondition() 참고
    private static final String BASE_FROM_HEAD = """
            FROM PRODUCT P
            LEFT JOIN (
                SELECT PRODUCT_NO, PRICE, NORMAL_PRICE
                FROM (
                    SELECT PRODUCT_NO, PRICE, NORMAL_PRICE,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID ASC) AS RN
                    FROM PRODUCT_OPTION
                )
                WHERE RN = 1
            ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
            LEFT JOIN (
                SELECT PRODUCT_NO, IMAGE_URL,
                       ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                FROM PRODUCT_IMAGE
                WHERE IMAGE_PURPOSE = '대표'
            ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            ... (아래 RV/SC/OC 조인과 WHERE 절은 지금 buildFromHead() 안에 그대로 들어있음)
    ===== 여기까지 옛 버전 ===== */

    /*
     * FROM/JOIN + WHERE 앞부분 (2026-09-06 부터 상수가 아니라 메서드).
     *
     * ★ 왜 메서드로 바꿨나 — 색상 필터를 걸면 "카드의 대표 옵션"이 달라져야 하기 때문.
     *   원본 쿠팡을 실제로 재보니(2026-09-06, Playwright) 색상 필터를 건 목록의 카드는
     *   상품이 아니라 **그 색상 옵션 하나**를 가리키고 있었음:
     *     - 같은 상품인데 블랙/화이트에서 링크의 itemId 가 서로 다름
     *     - 사진도 다르고, 가격도 다를 수 있음(블랙 14,410원 / 화이트 16,900원인 상품이 실제로 있었음)
     *     - 그 색상이 품절이면 목록에서 빼지 않고 "일시품절"로 회색 처리해서 남겨둠
     *   그래서 대표 옵션을 고르는 창(OPT 서브쿼리) 안에 색상 조건을 넣어, 필터에 맞는 옵션 중
     *   첫 번째(OPTION_ID 가 가장 작은 것)가 대표가 되게 함.
     *
     * ★ EXISTS 색상 조건이 사라진 이유 — 위처럼 거르면 "그 색상 옵션이 없는 상품"은 OPT 가 통째로
     *   비게 되므로, 아래 WHERE 의 `OPT.OPTION_ID IS NOT NULL` 한 줄이 예전 EXISTS 와 같은 일을 함.
     *   색상 조건을 두 군데 쓰지 않게 돼서 자리표시자(?) 개수도 예전과 같음.
     */
    private String buildFromHead(int colorCount) {
        return """
            FROM PRODUCT P
            LEFT JOIN (
                SELECT PRODUCT_NO, OPTION_ID, PRICE, NORMAL_PRICE, QUANTITY, OPT_STOCK
                FROM (
                    SELECT PRODUCT_NO, OPTION_ID, PRICE, NORMAL_PRICE, QUANTITY,
                           SUM(NVL(QUANTITY, 0)) OVER (PARTITION BY PRODUCT_NO) AS OPT_STOCK,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID ASC) AS RN
                    FROM PRODUCT_OPTION
            """
            + buildOptionColorCondition(colorCount)
            + """
                )
                WHERE RN = 1
            ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
            LEFT JOIN (
                SELECT PRODUCT_NO, OPTION_ID, IMAGE_URL,
                       ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO, OPTION_ID ORDER BY IMAGE_ORDER) AS RN
                FROM PRODUCT_IMAGE
                WHERE IMAGE_PURPOSE = '대표'
            ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
                 AND IMG.OPTION_ID = OPT.OPTION_ID
            LEFT JOIN (
                SELECT PRODUCT_NO, IMAGE_URL,
                       ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                FROM PRODUCT_IMAGE
                WHERE IMAGE_PURPOSE = '대표'
            ) IMG_ANY ON IMG_ANY.PRODUCT_NO = P.PRODUCT_NO AND IMG_ANY.RN = 1
            LEFT JOIN (
                SELECT od.PRODUCT_NO,
                       ROUND(AVG(r.PRODUCT_RATING), 1) AS AVG_RATING,
                       COUNT(*) AS REVIEW_COUNT
                FROM REVIEW r
                JOIN ORDER_DETAIL od ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
                GROUP BY od.PRODUCT_NO
            ) RV ON RV.PRODUCT_NO = P.PRODUCT_NO
            LEFT JOIN (
                SELECT od.PRODUCT_NO, SUM(od.ORDER_QTY) AS SALE_COUNT
                FROM ORDER_DETAIL od
                JOIN ORDERS o ON od.ORDER_NO = o.ORDER_NO
                WHERE o.ORDER_STATUS != '주문취소'
                GROUP BY od.PRODUCT_NO
            ) SC ON SC.PRODUCT_NO = P.PRODUCT_NO
            LEFT JOIN (
                SELECT PRODUCT_NO, COUNT(*) AS OPTION_COUNT
                FROM PRODUCT_OPTION
                GROUP BY PRODUCT_NO
            ) OC ON OC.PRODUCT_NO = P.PRODUCT_NO
            WHERE
            """
            + CATEGORY_TREE_CONDITION
            + """

            """
            + VISIBLE_PRODUCT_CONDITION
            + """
              AND (NVL(P.PRODUCT_PRICE, 0) + NVL(OPT.PRICE, 0)) BETWEEN ? AND ?
              AND NVL(RV.AVG_RATING, 0) >= ?
            """
            // 색상 필터를 걸었으면 "그 색상 옵션이 있는 상품"만 — 위 OPT 서브쿼리가 이미 색상으로 걸러졌으므로
            // 대표 옵션이 안 잡힌 상품(=그 색상이 없는 상품)을 여기서 떨어뜨리면 예전 EXISTS 와 결과가 같음
            + (colorCount > 0 ? "  AND OPT.OPTION_ID IS NOT NULL\n" : "");
    }

    /* ===== 2026-09-06 이전 버전 — 색상 조건을 바깥 WHERE 의 EXISTS 로 걸던 것. 되돌릴 때를 위해 남겨둠 =====
    private String buildColorCondition(int colorCount) {
        if (colorCount == 0) return "";
        String placeholders = String.join(",", Collections.nCopies(colorCount, "?"));
        return "  AND EXISTS (\n"
             + "        SELECT 1 FROM PRODUCT_OPTION PO2\n"
             + "        WHERE PO2.PRODUCT_NO = P.PRODUCT_NO\n"
             + "          AND (PO2.OPTION1_VALUE IN (" + placeholders + ")\n"
             + "            OR PO2.OPTION2_VALUE IN (" + placeholders + ")\n"
             + "            OR PO2.OPTION3_VALUE IN (" + placeholders + "))\n"
             + "      )\n";
    }
    ===== 여기까지 옛 버전 ===== */

    /*
     * 색상 다중선택 조건 (2026-09-02 추가, 2026-09-06 위치만 옮김) — 같은 색상 그룹 안에서는 OR
     * ("Black 또는 White 를 가진 옵션"), 다른 필터 그룹(가격/평점 등)과는 AND.
     * 이제는 바깥 WHERE 가 아니라 대표 옵션을 고르는 OPT 서브쿼리 안에 들어감(buildFromHead 주석 참고).
     * IN() 자리표시자는 고른 색상 개수만큼 동적으로 만들지만 값은 PreparedStatement 로만 바인딩하니
     * SQL 인젝션 위험은 없음(사용자 입력이 SQL 문자열에 직접 안 들어감).
     */
    private String buildOptionColorCondition(int colorCount) {
        if (colorCount == 0) return "";
        String placeholders = String.join(",", Collections.nCopies(colorCount, "?"));
        return "                    WHERE (OPTION1_VALUE IN (" + placeholders + ")\n"
             + "                        OR OPTION2_VALUE IN (" + placeholders + ")\n"
             + "                        OR OPTION3_VALUE IN (" + placeholders + "))\n";
    }

    /* ===== 2026-09-06 이전 버전 — pstmt 에 직접 순서를 세어 넣던 것. 되돌릴 때를 위해 남겨둠 =====
    private int bindColors(PreparedStatement pstmt, int startIndex, List<String> colors) throws java.sql.SQLException {
        int i = startIndex;
        if (colors == null || colors.isEmpty()) return i;
        for (int rep = 0; rep < 3; rep++) {          // OPTION1_VALUE, OPTION2_VALUE, OPTION3_VALUE 순서
            for (String color : colors) {
                pstmt.setString(i++, color);
            }
        }
        return i;
    }
    ===== 여기까지 옛 버전 ===== */

    /*
     * 색상 값을 파라미터 목록에 담기 — OPTION1/2/3_VALUE 의 IN() 세 개에 같은 목록을 세 번 넣음.
     *
     * ★ "몇 번째 ? 인지"를 사람이 세지 않는 이유(2026-09-06):
     *   PreparedStatement 의 ? 는 이름이 없고 SQL 글자 순서로만 짝이 맞습니다. 그래서 SQL 조각을
     *   붙이는 자리에서 값도 같은 순서로 목록에 담아두고, 마지막에 목록 순서대로 한 번에 넣습니다
     *   (findByCategory 참고). 이러면 나중에 조건이 하나 더 늘어도 번호를 다시 셀 일이 없습니다.
     */
    private void addColors(List<Object> params, List<String> colors) {
        if (colors == null || colors.isEmpty()) return;
        for (int rep = 0; rep < 3; rep++) {          // OPTION1_VALUE, OPTION2_VALUE, OPTION3_VALUE 순서
            params.addAll(colors);
        }
    }

    // 목록/개수 조회가 공통으로 쓰는 값 넣기 — params 에 담긴 순서 그대로 1번 자리부터 채움
    private void bindParams(PreparedStatement pstmt, List<Object> params) throws java.sql.SQLException {
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }
    }

    // 목록 (60개씩 고정, page 는 1부터). colors 가 비어있으면 색상 필터 안 함
    public List<CategoryProductDTO> findByCategory(
            int categoryNo, Sort sort, int minPrice, int maxPrice, int minRating, List<String> colors,
            int page, int pageSize) {

        List<CategoryProductDTO> list = new ArrayList<>();

        int colorCount = (colors == null ? 0 : colors.size());

        /* ===== 2026-09-06 이전 버전 — 대표 옵션이 항상 첫 옵션이라 색상 필터가 카드에 반영 안 됐음 =====
        String sql = """
                SELECT
                    P.PRODUCT_NO,
                    P.PRODUCT_NAME,
                    IMG.IMAGE_URL AS THUMBNAIL_URL,
                    P.PRODUCT_PRICE + NVL(OPT.PRICE, 0) AS SALE_PRICE,
                    CASE WHEN OPT.NORMAL_PRICE IS NOT NULL
                         THEN P.PRODUCT_PRICE + OPT.NORMAL_PRICE END AS NORMAL_PRICE,
                    CASE WHEN P.SALE_STATUS = '품절' THEN 'Y' ELSE 'N' END AS SOLD_OUT,
                    ...
                """
                + BASE_FROM_HEAD
                + buildColorCondition(colorCount)
                + "ORDER BY " + orderByClause(sort) + "\n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY\n";
        ===== 여기까지 옛 버전 ===== */

        /*
         * SELECT 절에서 달라진 것 (2026-09-06):
         *  - OPT.OPTION_ID : 카드 링크에 붙일 옵션 번호. 상세페이지가 /product?productNo=..&optionId=.. 를
         *                    이미 읽고 있어서(ProductHandler), 이것만 내려주면 고른 색상이 그대로 이어짐
         *  - THUMBNAIL_URL : 대표 옵션 전용 사진을 먼저 쓰고, 그 옵션에 사진이 없으면 예전처럼 상품의 첫 사진
         *                    (NVL 로 대체) — 색상만 바꿨는데 사진이 통째로 사라지는 일이 없게 하려는 것
         *  - SOLD_OUT      : 색상을 안 골랐으면 예전 그대로 PRODUCT.SALE_STATUS='품절'(=모든 옵션 재고 0).
         *                    색상을 골랐으면 "그 색상 옵션들의 재고 합(OPT_STOCK)이 0" 이어도 품절.
         *                    원본도 필터를 걸면 그 색상이 품절인 카드를 목록에 남긴 채 회색 처리함(2026-09-06 실측)
         */
        String sql = """
                SELECT
                    P.PRODUCT_NO,
                    P.PRODUCT_NAME,
                    OPT.OPTION_ID,
                    NVL(IMG.IMAGE_URL, IMG_ANY.IMAGE_URL) AS THUMBNAIL_URL,
                    NVL(P.PRODUCT_PRICE, 0) + NVL(OPT.PRICE, 0) AS SALE_PRICE,
                    CASE WHEN OPT.NORMAL_PRICE IS NOT NULL
                         THEN NVL(P.PRODUCT_PRICE, 0) + OPT.NORMAL_PRICE END AS NORMAL_PRICE,
                """
                + (colorCount > 0
                    ? "                    CASE WHEN P.SALE_STATUS = '품절' OR NVL(OPT.OPT_STOCK, 0) <= 0\n"
                    + "                         THEN 'Y' ELSE 'N' END AS SOLD_OUT,\n"
                    : "                    CASE WHEN P.SALE_STATUS = '품절' THEN 'Y' ELSE 'N' END AS SOLD_OUT,\n")
                + """
                    NVL(RV.AVG_RATING, 0) AS AVG_RATING,
                    NVL(RV.REVIEW_COUNT, 0) AS REVIEW_COUNT,
                    NVL(SC.SALE_COUNT, 0) AS SALE_COUNT
                """
                + buildFromHead(colorCount)
                + "ORDER BY " + orderByClause(sort) + "\n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY\n";

        // ★ SQL 조각을 붙인 순서와 똑같은 순서로 값을 담아둔다 — addColors 주석 참고.
        //   색상이 맨 앞인 이유: 색상 조건이 FROM 안(OPT 서브쿼리)에 있어서 SQL 글자 순서상 제일 먼저 나옴
        List<Object> params = new ArrayList<>();
        addColors(params, colors);
        params.add(categoryNo);
        params.add(minPrice);
        params.add(maxPrice);
        params.add(minRating);
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            bindParams(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 페이지네이션용 총 개수 — 필터 조건은 findByCategory 와 반드시 같아야 함
    public int countByCategory(int categoryNo, int minPrice, int maxPrice, int minRating, List<String> colors) {

        int colorCount = (colors == null ? 0 : colors.size());

        // ===== 2026-09-06 이전 버전 =====
        // String sql = "SELECT COUNT(*) " + BASE_FROM_HEAD + buildColorCondition(colorCount);
        String sql = "SELECT COUNT(*) " + buildFromHead(colorCount);

        // 값 담는 순서는 findByCategory 와 같아야 함(여기는 OFFSET/FETCH 가 없어서 맨 뒤 두 개만 빠짐)
        List<Object> params = new ArrayList<>();
        addColors(params, colors);
        params.add(categoryNo);
        params.add(minPrice);
        params.add(maxPrice);
        params.add(minRating);

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            bindParams(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
     * 색상 필터 항목 — 하드코딩 안 하고 실제 등록된 값만 보여줌(2026-08-30 확정).
     * OPTION1~3_TYPE 중 아무 축이나 '색상'으로 등록될 수 있어서 세 축을 UNION 으로 합침.
     * (처음엔 LATERAL JOIN 으로 짰다가 Oracle 버전 호환이 불확실해서 이 방식으로 바꿈)
     *
     * ★ 2026-09-06 — 세 갈래 전부에 VISIBLE_PRODUCT_CONDITION 추가.
     *   전에는 이 조회에만 상태 조건이 하나도 없어서, 목록에는 안 나오는 상품(승인 대기·판매 중지)의
     *   색상까지 필터에 떴고 그걸 고르면 결과가 0개였음. 목록 SQL 과 "보이는 상품"의 기준을 맞춘 것.
     *   ※ '품절'은 여기서도 안 뺌 — 품절 상품은 목록에 남아있으니 그 색상도 필터에 있어야 함
     */
    public List<String> findColorOptionsByCategory(int categoryNo) {

        List<String> colors = new ArrayList<>();

        String sql = """
                SELECT DISTINCT OPTION_VALUE FROM (
                    SELECT PO.OPTION1_VALUE AS OPTION_VALUE
                    FROM PRODUCT_OPTION PO
                    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                    WHERE
                """
                + CATEGORY_TREE_CONDITION
                + VISIBLE_PRODUCT_CONDITION
                + """
                 AND PO.OPTION1_TYPE = '색상'
                    UNION
                    SELECT PO.OPTION2_VALUE
                    FROM PRODUCT_OPTION PO
                    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                    WHERE
                """
                + CATEGORY_TREE_CONDITION
                + VISIBLE_PRODUCT_CONDITION
                + """
                 AND PO.OPTION2_TYPE = '색상'
                    UNION
                    SELECT PO.OPTION3_VALUE
                    FROM PRODUCT_OPTION PO
                    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                    WHERE
                """
                + CATEGORY_TREE_CONDITION
                + VISIBLE_PRODUCT_CONDITION
                + """
                 AND PO.OPTION3_TYPE = '색상'
                )
                WHERE OPTION_VALUE IS NOT NULL
                ORDER BY OPTION_VALUE
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);
            pstmt.setInt(2, categoryNo);
            pstmt.setInt(3, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    colors.add(rs.getString("OPTION_VALUE"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return colors;
    }

    /*
     * 브레드크럼 — 2026-09-03 수정: 레벨에 상관없이 "루트부터 현재 카테고리까지" 를 돌려준다.
     *
     * 예전엔 CATEGORY 를 3번 자기조인해서 소분류(레벨3)에서만 결과가 나왔고, 중분류(레벨2)로 열면
     * 한 줄도 안 나와서 브레드크럼이 통째로 사라졌음.
     *
     * 이번엔 CATEGORY_TREE_CONDITION 과 반대 방향으로, 자식에서 부모 쪽으로 거슬러 올라간다:
     *   CONNECT BY PRIOR PARENT_CATEGORY_NO = CATEGORY_NO
     * 그래서 10301(티셔츠)을 넣으면 [패션의류/잡화, 남녀 공용 의류, 티셔츠] 3칸,
     *       103(남녀 공용 의류)을 넣으면 [패션의류/잡화, 남녀 공용 의류] 2칸이 나온다.
     *
     * ORDER BY CATEGORY_LEVEL 로 항상 [대분류 → … → 현재] 순서가 보장됨.
     * ★ 칸 수가 레벨마다 달라지므로 화면에서 breadcrumb[2] 처럼 번호로 집어 쓰면 안 됨 —
     *   그래서 JSP 의 제목은 서블릿이 따로 내려주는 categoryName 을 쓴다.
     */
    public CategoryDTO[] findBreadcrumb(int categoryNo) {

        List<CategoryDTO> crumbs = new ArrayList<>();

        String sql = """
                SELECT CATEGORY_NO, CATEGORY_NAME, PARENT_CATEGORY_NO, CATEGORY_LEVEL
                FROM CATEGORY
                START WITH CATEGORY_NO = ?
                CONNECT BY PRIOR PARENT_CATEGORY_NO = CATEGORY_NO
                ORDER BY CATEGORY_LEVEL
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    crumbs.add(mapCategoryRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return crumbs.toArray(new CategoryDTO[0]);
    }

    /*
     * CATEGORY 한 줄 → CategoryDTO 로 바꾸는 공통 코드 (2026-09-03 추가).
     * 아래 세 곳(브레드크럼/자식목록/자기자신)이 같이 쓴다.
     * 대분류(레벨1)만 이미지 경로를 붙이는 규칙은 findSiblingCategories() 에 있던 걸 그대로 옮긴 것.
     */
    private CategoryDTO mapCategoryRow(ResultSet rs) throws java.sql.SQLException {
        long no = rs.getLong("CATEGORY_NO");
        int level = rs.getInt("CATEGORY_LEVEL");
        return new CategoryDTO(
            no,
            rs.getString("CATEGORY_NAME"),
            rs.getLong("PARENT_CATEGORY_NO"),
            level,
            level == 1 ? "./pds/category_" + no + ".png" : null,
            null
        );
    }

    /*
     * 자식 카테고리 목록 (2026-09-03 추가) — 중분류 페이지에서 두 군데에 쓴다.
     *   1) 제목 아래 원형 타일 그리드(티셔츠·맨투맨/후드티 …)
     *   2) 왼쪽 필터 사이드바의 "카테고리" 그룹
     * (소분류 페이지는 예전처럼 형제 목록인 findSiblingCategories() 를 쓴다)
     */
    public List<CategoryDTO> findChildCategories(int categoryNo) {

        List<CategoryDTO> list = new ArrayList<>();

        String sql = """
                SELECT CATEGORY_NO, CATEGORY_NAME, PARENT_CATEGORY_NO, CATEGORY_LEVEL
                FROM CATEGORY
                WHERE PARENT_CATEGORY_NO = ?
                ORDER BY CATEGORY_NO
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCategoryRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 현재 카테고리 자기 자신 (2026-09-03 추가) — 서블릿이 "이 페이지가 중분류인지 소분류인지"를
     * CATEGORY_LEVEL 로 판단하고, 화면 제목(h1)에 쓸 이름도 여기서 가져온다. 없는 번호면 null.
     */
    public CategoryDTO findCategory(int categoryNo) {

        String sql = """
                SELECT CATEGORY_NO, CATEGORY_NAME, PARENT_CATEGORY_NO, CATEGORY_LEVEL
                FROM CATEGORY
                WHERE CATEGORY_NO = ?
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapCategoryRow(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 형제 카테고리 목록 — 필터 사이드바의 "카테고리" 그룹(예: 티셔츠와 같은 레벨의 맨투맨/셔츠/바지 등)
    public List<CategoryDTO> findSiblingCategories(int categoryNo) {

        List<CategoryDTO> list = new ArrayList<>();

        // 2026-09-03 추가: 대분류(레벨1)는 PARENT_CATEGORY_NO 가 둘 다 NULL 이라
        // 원래 조건(NULL = NULL)이 SQL에서 거짓 취급돼 0건이었음(대분류의 "함께 본 카테고리"가
        // 텅 비던 원인). NULL-세이프 분기를 더해서 대분류일 땐 "다른 대분류들"이 형제로 잡히게 함.
        // 레벨2/3은 PARENT_CATEGORY_NO 가 NULL이 아니라 OR 뒤쪽은 안 타서 기존 동작 그대로.
        String sql = """
                SELECT SIB.CATEGORY_NO, SIB.CATEGORY_NAME, SIB.PARENT_CATEGORY_NO, SIB.CATEGORY_LEVEL
                FROM CATEGORY SELF
                JOIN CATEGORY SIB ON (SIB.PARENT_CATEGORY_NO = SELF.PARENT_CATEGORY_NO
                                       OR (SIB.PARENT_CATEGORY_NO IS NULL AND SELF.PARENT_CATEGORY_NO IS NULL))
                WHERE SELF.CATEGORY_NO = ?
                ORDER BY SIB.CATEGORY_NO
                """;
                /* 원래 줄(주석 보존):
                JOIN CATEGORY SIB ON SIB.PARENT_CATEGORY_NO = SELF.PARENT_CATEGORY_NO
                */

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long categoryNoResult = rs.getLong("CATEGORY_NO");
                    int categoryLevel = rs.getInt("CATEGORY_LEVEL");
                    list.add(new CategoryDTO(
                        categoryNoResult,
                        rs.getString("CATEGORY_NAME"),
                        rs.getLong("PARENT_CATEGORY_NO"),
                        categoryLevel,
                        categoryLevel == 1 ? "./pds/category_" + categoryNoResult + ".png" : null,
                        null
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // SQL 인젝션 방지 — 사용자 입력을 ORDER BY 에 직접 이어붙이지 않고 화이트리스트에서만 고름
    private String orderByClause(Sort sort) {
        return switch (sort) {
            case PRICE_ASC  -> "(P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) ASC";
            case PRICE_DESC -> "(P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) DESC";
            case SALE_COUNT -> "NVL(SC.SALE_COUNT, 0) DESC";
            case LATEST     -> "P.CREATED_DATE DESC";
            // 다단계 우선순위 정렬(2026-09-02) — 데이터 규모가 작아 가중합/베이지안 보정 대신 선택.
            // 판매량 → 평점 → 리뷰수 → 옵션수 순으로 동점 처리. 
            case RANKING    -> "NVL(SC.SALE_COUNT, 0) DESC, NVL(RV.AVG_RATING, 0) DESC, "
                              + "NVL(RV.REVIEW_COUNT, 0) DESC, NVL(OC.OPTION_COUNT, 0) DESC, "
                              + "P.PRODUCT_NO DESC";   // 동점일 때 항상 같은 순서가 나오도록 최종 기준 추가(2026-09-03)
        };
    }

    private CategoryProductDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        CategoryProductDTO dto = new CategoryProductDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

        // 카드가 가리키는 대표 옵션 번호(2026-09-06 추가). 옵션이 하나도 없는 상품이면 0
        // → JSP 에서 0 이면 링크에 optionId 를 안 붙임(예전과 똑같이 상세페이지가 첫 옵션을 고름)
        dto.setOptionId(rs.getInt("OPTION_ID"));

        dto.setSalePrice(rs.getInt("SALE_PRICE"));

        int normalPrice = rs.getInt("NORMAL_PRICE");
        dto.setNormalPrice(rs.wasNull() ? null : normalPrice);

        if (dto.getNormalPrice() != null && dto.getNormalPrice() > dto.getSalePrice()) {
            dto.setDiscountRate((int) Math.round((1 - (double) dto.getSalePrice() / dto.getNormalPrice()) * 100));
        }

        dto.setAvgRating(rs.getDouble("AVG_RATING"));
        dto.setReviewCount(rs.getInt("REVIEW_COUNT"));
        dto.setSaleCount(rs.getInt("SALE_COUNT"));
        dto.setSoldOut("Y".equals(rs.getString("SOLD_OUT")));
        dto.setCashReward((int) Math.floor(dto.getSalePrice() * 0.05));

        return dto;
    }
}
