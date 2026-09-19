package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AdminProductApprovalDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 관리자용 상품 승인 화면(admin-product-list.jsp) 조회/처리.
 * '승인 대기' 상품이 위로 오도록 정렬해서 전체 상품을 보여주고, 승인/반려 처리를 담당한다.
 */
public class AdminProductDAO {

    public List<AdminProductApprovalDTO> findAll() {

        List<AdminProductApprovalDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                P.PRODUCT_NO,
                P.PRODUCT_NAME,
                P.SELLER_NO,
                S.STORE_NAME,
                P.SALE_STATUS,
                C1.CATEGORY_NAME AS MAIN_CATEGORY_NAME,
                C2.CATEGORY_NAME AS MID_CATEGORY_NAME,
                C3.CATEGORY_NAME AS SUB_CATEGORY_NAME,
                NVL(OPT.MIN_PRICE, 0) + P.PRODUCT_PRICE AS MIN_PRICE,
                NVL(OPT.MAX_PRICE, 0) + P.PRODUCT_PRICE AS MAX_PRICE,
                IMG.IMAGE_URL AS THUMBNAIL_URL,
                P.CREATED_DATE
            FROM PRODUCT P
                JOIN SELLER S ON S.SELLER_NO = P.SELLER_NO
                JOIN CATEGORY C3 ON P.SUB_CATEGORY_NO = C3.CATEGORY_NO
                LEFT JOIN CATEGORY C2 ON C2.CATEGORY_NO = C3.PARENT_CATEGORY_NO
                LEFT JOIN CATEGORY C1 ON C1.CATEGORY_NO = C2.PARENT_CATEGORY_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO, MIN(PRICE) AS MIN_PRICE, MAX(PRICE) AS MAX_PRICE
                    FROM PRODUCT_OPTION
                    GROUP BY PRODUCT_NO
                ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO, IMAGE_URL,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            ORDER BY
                CASE WHEN P.SALE_STATUS = '승인 대기' THEN 0 ELSE 1 END,
                P.CREATED_DATE DESC
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 상품 승인/반려. '승인 대기' 상태인 상품만 대상으로 한다.
    public boolean updateApprovalStatus(int productNo, String saleStatus) {

        String sql = """
            UPDATE PRODUCT
            SET SALE_STATUS = ?,
                UPDATED_DATE = SYSDATE
            WHERE PRODUCT_NO = ?
              AND SALE_STATUS = '승인 대기'
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, saleStatus);
            pstmt.setInt(2, productNo);

            return pstmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private AdminProductApprovalDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        AdminProductApprovalDTO dto = new AdminProductApprovalDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setSellerNo(rs.getInt("SELLER_NO"));
        dto.setStoreName(rs.getString("STORE_NAME"));
        dto.setSaleStatus(rs.getString("SALE_STATUS"));

        dto.setMainCategoryName(rs.getString("MAIN_CATEGORY_NAME"));
        dto.setMidCategoryName(rs.getString("MID_CATEGORY_NAME"));
        dto.setSubCategoryName(rs.getString("SUB_CATEGORY_NAME"));

        int minPrice = rs.getInt("MIN_PRICE");
        dto.setMinPrice(rs.wasNull() ? null : minPrice);

        int maxPrice = rs.getInt("MAX_PRICE");
        dto.setMaxPrice(rs.wasNull() ? null : maxPrice);

        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));
        dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));

        return dto;
    }
}
