package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorProductOptionDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 상품 옵션 관리(vendor-product-options.jsp) 조회/수정.
 * 판매자의 상품 전체를 가로질러 옵션(PRODUCT_OPTION) 단위로 다룬다.
 */
public class VendorProductOptionDAO {

    // 판매자(sellerNo)의 상품 옵션 - 상품번호, 옵션번호 순. productNo가 null이면 전체 상품 대상
    public List<VendorProductOptionDTO> findBySellerNo(int sellerNo, Integer productNo) {

        List<VendorProductOptionDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                PO.OPTION_ID, PO.PRODUCT_NO, P.PRODUCT_NAME,
                PO.OPTION1_VALUE, PO.OPTION2_VALUE, PO.OPTION3_VALUE,
                NVL(PO.PRICE, 0) AS PRICE, PO.NORMAL_PRICE, PO.QUANTITY, PO.STATUS,
                IMG.IMAGE_URL AS THUMBNAIL_URL
            FROM PRODUCT_OPTION PO
                JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO, IMAGE_URL,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            WHERE P.SELLER_NO = ?
              AND (? IS NULL OR P.PRODUCT_NO = ?)
            ORDER BY P.CREATED_DATE DESC, P.PRODUCT_NO DESC, PO.OPTION_ID
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            if (productNo != null) {
                pstmt.setInt(2, productNo);
                pstmt.setInt(3, productNo);
            } else {
                pstmt.setNull(2, Types.INTEGER);
                pstmt.setNull(3, Types.INTEGER);
            }

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

    // 옵션 관리 화면 상단 "상품 선택" 필터 드롭다운용 - 옵션이 하나라도 있는 상품만, 상품번호순
    public List<VendorProductOptionDTO> findDistinctProductsBySellerNo(int sellerNo) {

        List<VendorProductOptionDTO> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT P.PRODUCT_NO, P.PRODUCT_NAME, P.CREATED_DATE
            FROM PRODUCT_OPTION PO
                JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
            WHERE P.SELLER_NO = ?
            ORDER BY P.CREATED_DATE DESC, P.PRODUCT_NO DESC
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    VendorProductOptionDTO dto = new VendorProductOptionDTO();
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 옵션 하나의 판매가/정상가/재고수량/상태(품절여부) 수정.
     * PRODUCT_OPTION 에는 SELLER_NO 가 없어서, PRODUCT 를 통해 이 옵션이 이 판매자 소유인지
     * 확인한 뒤에만 UPDATE 되게 EXISTS 서브쿼리로 소유권을 검사한다.
     */
    public boolean updateOption(int optionId, int sellerNo, int price, Integer normalPrice, int quantity, String status) {

        String sql = """
            UPDATE PRODUCT_OPTION
            SET PRICE = ?,
                NORMAL_PRICE = ?,
                QUANTITY = ?,
                STATUS = ?
            WHERE OPTION_ID = ?
              AND EXISTS (
                    SELECT 1 FROM PRODUCT P
                    WHERE P.PRODUCT_NO = PRODUCT_OPTION.PRODUCT_NO
                      AND P.SELLER_NO = ?
              )
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, price);

            if (normalPrice != null) {
                pstmt.setInt(2, normalPrice);
            } else {
                pstmt.setNull(2, Types.NUMERIC);
            }

            pstmt.setInt(3, quantity);
            pstmt.setString(4, status);
            pstmt.setInt(5, optionId);
            pstmt.setInt(6, sellerNo);

            return pstmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private VendorProductOptionDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorProductOptionDTO dto = new VendorProductOptionDTO();

        dto.setOptionId(rs.getInt("OPTION_ID"));
        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

        dto.setOption1Value(rs.getString("OPTION1_VALUE"));
        dto.setOption2Value(rs.getString("OPTION2_VALUE"));
        dto.setOption3Value(rs.getString("OPTION3_VALUE"));

        dto.setPrice(rs.getInt("PRICE"));
        dto.setNormalPrice(rs.getObject("NORMAL_PRICE", Integer.class));
        dto.setQuantity(rs.getInt("QUANTITY"));
        dto.setStatus(rs.getString("STATUS"));

        return dto;
    }
}
