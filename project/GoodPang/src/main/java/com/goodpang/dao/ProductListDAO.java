package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorProductListDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 상품 목록(vendor_products.jsp) 조회.
 * PRODUCT_OPTION 가격/재고를 상품 하나로 요약하고, 대표이미지 1장만 같이 가져온다.
 */
public class ProductListDAO {

    // 판매자(sellerNo)가 등록한 상품 목록(노출 중) - 최근 등록순. page는 1부터
    public List<VendorProductListDTO> findBySellerNo(int sellerNo, int page, int pageSize) {
        return findBySellerNoAndDisplayYn(sellerNo, "Y", page, pageSize);
    }

    // 판매자(sellerNo)가 숨긴 상품 목록 - 최근 등록순. page는 1부터
    public List<VendorProductListDTO> findHiddenBySellerNo(int sellerNo, int page, int pageSize) {
        return findBySellerNoAndDisplayYn(sellerNo, "N", page, pageSize);
    }

    private List<VendorProductListDTO> findBySellerNoAndDisplayYn(int sellerNo, String displayYn, int page, int pageSize) {

        List<VendorProductListDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                P.PRODUCT_NO,
                P.PRODUCT_NAME,
                P.SALE_METHOD,
                P.SALE_STATUS,
                C1.CATEGORY_NAME AS MAIN_CATEGORY_NAME,
                C2.CATEGORY_NAME AS MID_CATEGORY_NAME,
                C3.CATEGORY_NAME AS SUB_CATEGORY_NAME,
                NVL(OPT.MIN_PRICE, 0) + P.PRODUCT_PRICE  AS MIN_PRICE,
                NVL(OPT.MAX_PRICE, 0) + P.PRODUCT_PRICE  AS MAX_PRICE,
                NVL(OPT.TOTAL_QUANTITY, P.QUANTITY)  AS TOTAL_QUANTITY,
                NVL(OPT.OPTION_COUNT, 0)             AS OPTION_COUNT,
                IMG.IMAGE_URL                        AS THUMBNAIL_URL,
                P.CREATED_DATE,
                P.UPDATED_DATE
            FROM PRODUCT P
                JOIN CATEGORY C3 ON P.SUB_CATEGORY_NO = C3.CATEGORY_NO
                LEFT JOIN CATEGORY C2 ON C2.CATEGORY_NO = C3.PARENT_CATEGORY_NO
                LEFT JOIN CATEGORY C1 ON C1.CATEGORY_NO = C2.PARENT_CATEGORY_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO,
                           MIN(PRICE)    AS MIN_PRICE,
                           MAX(PRICE)    AS MAX_PRICE,
                           SUM(QUANTITY) AS TOTAL_QUANTITY,
                           COUNT(*)      AS OPTION_COUNT
                    FROM PRODUCT_OPTION
                    GROUP BY PRODUCT_NO
                ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO, IMAGE_URL,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            WHERE P.SELLER_NO = ?
              AND P.DISPLAY_YN = ?
            ORDER BY P.CREATED_DATE DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);
            pstmt.setString(2, displayYn);
            pstmt.setInt(3, (page - 1) * pageSize);
            pstmt.setInt(4, pageSize);

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

    // 페이지네이션용 총 개수. saleStatus가 null이면 노출여부(displayYn)만으로 세고,
    // 값이 있으면(판매 중/품절/판매 중지/승인 대기) 통계 카드용 상태별 개수로 쓴다.
    public int countBySellerNo(int sellerNo, String displayYn, String saleStatus) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM PRODUCT
            WHERE SELLER_NO = ?
              AND DISPLAY_YN = ?
            """);

        if (saleStatus != null) {
            sql.append(" AND SALE_STATUS = ?");
        }

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {

            pstmt.setInt(1, sellerNo);
            pstmt.setString(2, displayYn);
            if (saleStatus != null) {
                pstmt.setString(3, saleStatus);
            }

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

    // 판매자 상품목록 노출여부 변경 (소프트 삭제/복원). seller_no까지 확인해서 다른 판매자 상품은 못 바꾸게 막는다.
    public boolean updateDisplayYn(int productNo, int sellerNo, String displayYn) {

        String sql = """
            UPDATE PRODUCT
            SET DISPLAY_YN = ?,
                UPDATED_DATE = SYSDATE
            WHERE PRODUCT_NO = ?
              AND SELLER_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, displayYn);
            pstmt.setInt(2, productNo);
            pstmt.setInt(3, sellerNo);

            return pstmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * 판매자 탈퇴 시 그 판매자의 상품을 전부 숨김 처리(DISPLAY_YN='N').
     * 카테고리/검색 목록 쿼리(CategoryProductDAO/SearchDAO의 BASE_FROM_HEAD)가 전부
     * DISPLAY_YN='Y'만 보여주므로, 이 한 줄이면 판매 상태와 무관하게 고객 화면에서 즉시 사라진다.
     */
    public int hideAllBySeller(int sellerNo) {

        String sql = """
            UPDATE PRODUCT
            SET DISPLAY_YN = 'N',
                UPDATED_DATE = SYSDATE
            WHERE SELLER_NO = ?
              AND DISPLAY_YN = 'Y'
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
     * 판매중지/판매재개 토글. '승인 대기'나 '품절' 상태인 상품은 이 토글 대상이 아니므로
     * 현재 상태가 '판매 중'/'판매 중지'인 행만 바꾸도록 WHERE에 조건을 건다.
     */
    public boolean updateSaleStatus(int productNo, int sellerNo, String saleStatus) {

        String sql = """
            UPDATE PRODUCT
            SET SALE_STATUS = ?,
                UPDATED_DATE = SYSDATE
            WHERE PRODUCT_NO = ?
              AND SELLER_NO = ?
              AND SALE_STATUS IN ('판매 중', '판매 중지')
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, saleStatus);
            pstmt.setInt(2, productNo);
            pstmt.setInt(3, sellerNo);

            return pstmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private VendorProductListDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorProductListDTO dto = new VendorProductListDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setSaleMethod(rs.getString("SALE_METHOD"));
        dto.setSaleStatus(rs.getString("SALE_STATUS"));

        dto.setMainCategoryName(rs.getString("MAIN_CATEGORY_NAME"));
        dto.setMidCategoryName(rs.getString("MID_CATEGORY_NAME"));
        dto.setSubCategoryName(rs.getString("SUB_CATEGORY_NAME"));

        int minPrice = rs.getInt("MIN_PRICE");
        dto.setMinPrice(rs.wasNull() ? null : minPrice);

        int maxPrice = rs.getInt("MAX_PRICE");
        dto.setMaxPrice(rs.wasNull() ? null : maxPrice);

        int totalQuantity = rs.getInt("TOTAL_QUANTITY");
        dto.setTotalQuantity(rs.wasNull() ? null : totalQuantity);

        dto.setOptionCount(rs.getInt("OPTION_COUNT"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

        dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        dto.setUpdatedDate(rs.getTimestamp("UPDATED_DATE"));

        return dto;
    }
}
