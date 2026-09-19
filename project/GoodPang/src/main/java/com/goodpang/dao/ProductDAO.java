package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.ProductDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductDAO {

    // 상품 하나 조회 (PRODUCT + SELLER + 카테고리 3단 JOIN)
    public ProductDTO selectProduct(int productNo) {

        String sql = """
			        SELECT
			        P.PRODUCT_NO,
			        P.PRODUCT_NAME,
			        P.PRODUCT_DESC,
			        P.PRODUCT_PRICE,
			        P.QUANTITY,
			        P.SELLER_NO,
			        S.STORE_NAME,
			        S.CEO_NAME,
			        S.BUSINESS_ADDRESS,
			        S.BUSINESS_DETAIL_ADDRESS,
			        S.EMAIL,
			        S.PHONE,
			        S.MAIL_ORDER_NO,
			        S.BUSINESS_NO,
			        P.SALE_STATUS,
			        P.SUB_CATEGORY_NO,
			        SC.CATEGORY_NAME    AS SUB_CATEGORY_NAME,
			        MC.CATEGORY_NO      AS MID_CATEGORY_NO,
			        MC.CATEGORY_NAME    AS MID_CATEGORY_NAME,
			        MAINC.CATEGORY_NO   AS MAIN_CATEGORY_NO,
			        MAINC.CATEGORY_NAME AS MAIN_CATEGORY_NAME
			        FROM PRODUCT P
			        JOIN SELLER S
			        ON P.SELLER_NO = S.SELLER_NO
			        JOIN CATEGORY SC
			        ON P.SUB_CATEGORY_NO = SC.CATEGORY_NO        
			        JOIN CATEGORY MC
			        ON SC.PARENT_CATEGORY_NO = MC.CATEGORY_NO   
			        JOIN CATEGORY MAINC
			        ON MC.PARENT_CATEGORY_NO = MAINC.CATEGORY_NO
			        WHERE P.PRODUCT_NO = ?
			          AND P.SALE_STATUS != '승인 대기'
			          AND P.DISPLAY_YN = 'Y'
        	    """;
        
        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    ProductDTO dto = new ProductDTO();

                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setProductDesc(rs.getString("PRODUCT_DESC"));
                    dto.setProductPrice(rs.getInt("PRODUCT_PRICE"));
                    dto.setQuantity(rs.getInt("QUANTITY"));

                    // 2026-09-06 추가 — 판매중지 상품을 화면에서 품절과 같은 모습으로 막기 위해 내려줌.
                    // 값은 '판매 중' / '품절' / '판매 중지' ('승인 대기' 는 위 WHERE 에서 이미 걸러짐)
                    dto.setSaleStatus(rs.getString("SALE_STATUS"));

                    dto.setSellerNo(rs.getInt("SELLER_NO"));
                    dto.setStoreName(rs.getString("STORE_NAME"));
                    dto.setCeoName(rs.getString("CEO_NAME"));
                    dto.setBusinessAddress(rs.getString("BUSINESS_ADDRESS"));
                    dto.setBusinessDetailAddress(rs.getString("BUSINESS_DETAIL_ADDRESS"));
                    dto.setEmail(rs.getString("EMAIL"));
                    dto.setPhone(rs.getString("PHONE"));
                    dto.setMailOrderNo(rs.getString("MAIL_ORDER_NO"));
                    dto.setBusinessNo(rs.getString("BUSINESS_NO"));

                    dto.setSubCategoryNo(rs.getInt("SUB_CATEGORY_NO"));
                    dto.setSubCategoryName(rs.getString("SUB_CATEGORY_NAME"));
                    dto.setMidCategoryNo(rs.getInt("MID_CATEGORY_NO"));
                    dto.setMidCategoryName(rs.getString("MID_CATEGORY_NAME"));
                    dto.setMainCategoryNo(rs.getInt("MAIN_CATEGORY_NO"));
                    dto.setMainCategoryName(rs.getString("MAIN_CATEGORY_NAME"));
                    
                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public Integer getDefaultOptionId(int productNo) {

        String sql = """
                SELECT OPTION_ID
                FROM PRODUCT_OPTION
                WHERE PRODUCT_NO = ?
                ORDER BY OPTION_ID
                FETCH FIRST 1 ROW ONLY
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("OPTION_ID");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("기본 옵션 조회 실패", e);
        }

        return null;
    }
}
