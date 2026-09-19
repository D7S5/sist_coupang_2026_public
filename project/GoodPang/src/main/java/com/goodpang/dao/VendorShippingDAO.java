package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorShippingDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 출고/운송장 관리(vendor-shipping.jsp) 조회.
 * '결제완료' 상태(아직 송장 등록/배송중 전환 전)인, 이 판매자 상품이 포함된 주문만 보여준다.
 * 송장 등록 자체는 기존 VendorOrderShipServlet(POST /vendor/order/ship)을 그대로 재사용한다.
 */
public class VendorShippingDAO {

    public List<VendorShippingDTO> findWaitingBySellerNo(int sellerNo) {

        List<VendorShippingDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                O.ORDER_NO, O.ORDER_DATE,
                M.MEMBER_NAME, M.PHONE,
                PN.PRODUCT_NAME, PN.ITEM_COUNT, PN.TOTAL_AMOUNT
            FROM ORDERS O
                JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
                JOIN (
                    SELECT
                        OD.ORDER_NO,
                        MIN(P.PRODUCT_NAME) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS PRODUCT_NAME,
                        COUNT(*) AS ITEM_COUNT,
                        SUM(OD.PRICE * OD.ORDER_QTY) AS TOTAL_AMOUNT
                    FROM ORDER_DETAIL OD
                        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                    WHERE P.SELLER_NO = ?
                    GROUP BY OD.ORDER_NO
                ) PN ON PN.ORDER_NO = O.ORDER_NO
            WHERE O.ORDER_STATUS = '결제완료'
            ORDER BY O.ORDER_DATE ASC
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

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

    private VendorShippingDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorShippingDTO dto = new VendorShippingDTO();

        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));

        dto.setBuyerName(rs.getString("MEMBER_NAME"));
        dto.setBuyerPhone(rs.getString("PHONE"));

        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setItemCount(rs.getInt("ITEM_COUNT"));
        dto.setTotalAmount(rs.getLong("TOTAL_AMOUNT"));

        return dto;
    }
}
