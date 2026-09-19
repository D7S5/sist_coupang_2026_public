package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorDeliveryDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 배송 관리(vendor-delivery.jsp) 조회.
 * 이 판매자의 상품이 포함된 배송중 주문만 보여준다 (AdminDeliveryDAO.findShipping()과 같은
 * 구조지만 판매자 기준으로 스코프를 좁히고, 다른 판매자 상품이 섞인 주문에서도 이 판매자
 * 몫만 상품정보로 보여준다).
 */
public class VendorDeliveryDAO {

    public List<VendorDeliveryDTO> findShippingBySellerNo(int sellerNo) {

        List<VendorDeliveryDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                D.DELIVERY_NO, D.ORDER_NO, D.DELIVERY_SERVICE_CODE, D.INVOICE_NO,
                D.DELIVERY_START_DATE,
                M.MEMBER_NAME, M.PHONE,
                PN.PRODUCT_NAME, PN.ITEM_COUNT
            FROM DELIVERY D
                JOIN ORDERS O ON D.ORDER_NO = O.ORDER_NO
                JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
                JOIN (
                    SELECT
                        OD.ORDER_NO,
                        MIN(P.PRODUCT_NAME) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS PRODUCT_NAME,
                        COUNT(*) AS ITEM_COUNT
                    FROM ORDER_DETAIL OD
                        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                    WHERE P.SELLER_NO = ?
                    GROUP BY OD.ORDER_NO
                ) PN ON PN.ORDER_NO = D.ORDER_NO
            WHERE D.DELIVERY_STATUS = '배송중'
            ORDER BY D.DELIVERY_START_DATE DESC
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

    private VendorDeliveryDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorDeliveryDTO dto = new VendorDeliveryDTO();

        dto.setDeliveryNo(rs.getInt("DELIVERY_NO"));
        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setDeliveryServiceCode(rs.getString("DELIVERY_SERVICE_CODE"));
        dto.setInvoiceNo(rs.getString("INVOICE_NO"));
        dto.setDeliveryStartDate(rs.getTimestamp("DELIVERY_START_DATE"));

        dto.setBuyerName(rs.getString("MEMBER_NAME"));
        dto.setBuyerPhone(rs.getString("PHONE"));

        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setItemCount(rs.getInt("ITEM_COUNT"));

        return dto;
    }
}
