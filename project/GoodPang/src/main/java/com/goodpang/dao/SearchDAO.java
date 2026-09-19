package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dao.CategoryProductDAO.Sort;
import com.goodpang.dto.CategoryProductDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 검색 결과 페이지(/search?keyword=...) 조회 전용 DAO.
 * CategoryProductDAO의 상품 카드 조립 방식(대표이미지/판매가/평점/판매량 등 조인)을 그대로 재사용하고,
 * 카테고리 조건 대신 상품명 LIKE 검색으로 대상만 좁힌다. 결과 카드 모양이 카테고리 목록과 완전히 같아서
 * DTO도 CategoryProductDTO를 그대로 쓴다. 정렬도 같은 Sort enum을 재사용하되, 검색에서는
 * '쿠팡랭킹순'(RANKING)은 지원하지 않는다(요청 범위 밖) — 그 값이 오면 최신순으로 대체.
 */
public class SearchDAO {

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
            WHERE P.PRODUCT_NAME LIKE '%' || ? || '%'
              AND P.SALE_STATUS != '승인 대기'
              AND P.DISPLAY_YN = 'Y'
            """;

    // 목록. page는 1부터
    public List<CategoryProductDTO> findByKeyword(String keyword, Sort sort, int page, int pageSize) {

        List<CategoryProductDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    P.PRODUCT_NO,
                    P.PRODUCT_NAME,
                    IMG.IMAGE_URL AS THUMBNAIL_URL,
                    P.PRODUCT_PRICE + NVL(OPT.PRICE, 0) AS SALE_PRICE,
                    CASE WHEN OPT.NORMAL_PRICE IS NOT NULL
                         THEN P.PRODUCT_PRICE + OPT.NORMAL_PRICE END AS NORMAL_PRICE,
                    CASE WHEN P.SALE_STATUS = '품절' THEN 'Y' ELSE 'N' END AS SOLD_OUT,
                    NVL(RV.AVG_RATING, 0) AS AVG_RATING,
                    NVL(RV.REVIEW_COUNT, 0) AS REVIEW_COUNT,
                    NVL(SC.SALE_COUNT, 0) AS SALE_COUNT
                """
                + BASE_FROM_HEAD
                + "ORDER BY " + orderByClause(sort) + "\n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY\n";

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            int i = 1;
            pstmt.setString(i++, keyword);
            pstmt.setInt(i++, (page - 1) * pageSize);
            pstmt.setInt(i++, pageSize);

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

    // 페이지네이션용 총 개수 — 필터 조건은 findByKeyword와 반드시 같아야 함
    public int countByKeyword(String keyword) {

        String sql = "SELECT COUNT(*) " + BASE_FROM_HEAD;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, keyword);

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

    // SQL 인젝션 방지 — 사용자 입력을 ORDER BY 에 직접 이어붙이지 않고 화이트리스트에서만 고름
    // (CategoryProductDAO.orderByClause() 와 동일한 4개만 지원. RANKING은 검색 요청 범위 밖이라 최신순으로 대체)
    private String orderByClause(Sort sort) {
        return switch (sort) {
            case PRICE_ASC  -> "(P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) ASC";
            case PRICE_DESC -> "(P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) DESC";
            case SALE_COUNT -> "NVL(SC.SALE_COUNT, 0) DESC";
            default         -> "P.CREATED_DATE DESC";   // LATEST, RANKING
        };
    }

    private CategoryProductDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        CategoryProductDTO dto = new CategoryProductDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

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
