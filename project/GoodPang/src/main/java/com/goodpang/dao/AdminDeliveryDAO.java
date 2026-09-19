package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AdminDeliveryDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 관리자용 배송중 상품 목록(admin-delivery-list.jsp) 조회/처리.
 */
public class AdminDeliveryDAO {

    public List<AdminDeliveryDTO> findShipping() {

        List<AdminDeliveryDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                D.DELIVERY_NO, D.ORDER_NO, D.DELIVERY_SERVICE_CODE, D.INVOICE_NO,
                D.DELIVERY_STATUS, D.DELIVERY_START_DATE,
                M.MEMBER_NAME, M.PHONE,
                DA.ZIPCODE, DA.ADDRESS, DA.DETAIL_ADDRESS,
                PN.PRODUCT_NAME, PN.STORE_NAME, PN.ITEM_COUNT, IMG.IMAGE_URL AS PRODUCT_IMAGE_URL
            FROM DELIVERY D
                JOIN ORDERS O ON D.ORDER_NO = O.ORDER_NO
                JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
                LEFT JOIN ORDER_ADDRESS DA ON O.ORDER_ADDRESS_NO = DA.ORDER_ADDRESS_NO
                JOIN (
                    SELECT
                        OD.ORDER_NO,
                        MIN(OD.PRODUCT_NO) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS PRODUCT_NO,
                        MIN(P.PRODUCT_NAME) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS PRODUCT_NAME,
                        MIN(S.STORE_NAME) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS STORE_NAME,
                        COUNT(*) AS ITEM_COUNT
                    FROM ORDER_DETAIL OD
                        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                        JOIN SELLER S ON P.SELLER_NO = S.SELLER_NO
                    GROUP BY OD.ORDER_NO
                ) PN ON PN.ORDER_NO = D.ORDER_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO, IMAGE_URL,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                ) IMG ON IMG.PRODUCT_NO = PN.PRODUCT_NO AND IMG.RN = 1
            WHERE D.DELIVERY_STATUS = '배송중'
            ORDER BY D.DELIVERY_START_DATE DESC
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

    /*
     * 배송완료 처리. DELIVERY와 ORDERS를 함께 갱신하고, 이 주문에 상품이 포함된
     * 판매자 번호 목록을 돌려준다(판매자 로그 기록용 - 한 주문에 여러 판매자 상품이
     * 섞여 있을 수 있어 판매자별로 로그를 남겨야 한다).
     */
    public CompleteResult completeDelivery(int deliveryNo) {

        try (Connection conn = ConnectionProvider.getConnection()) {

            conn.setAutoCommit(false);

            try {
                Integer orderNo = updateDeliveryDone(conn, deliveryNo);

                if (orderNo == null) {
                    conn.rollback();
                    return null;
                }

                updateOrderDone(conn, orderNo);
                List<Integer> sellerNos = findSellerNosByOrderNo(conn, orderNo);

                conn.commit();
                return new CompleteResult(orderNo, sellerNos);

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Integer> findSellerNosByOrderNo(Connection conn, int orderNo) throws Exception {

        List<Integer> sellerNos = new ArrayList<>();

        String sql = """
            SELECT DISTINCT P.SELLER_NO
            FROM ORDER_DETAIL OD
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
            WHERE OD.ORDER_NO = ?
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sellerNos.add(rs.getInt("SELLER_NO"));
                }
            }
        }

        return sellerNos;
    }

    public static class CompleteResult {

        private final int orderNo;
        private final List<Integer> sellerNos;

        public CompleteResult(int orderNo, List<Integer> sellerNos) {
            this.orderNo = orderNo;
            this.sellerNos = sellerNos;
        }

        public int getOrderNo() {
            return orderNo;
        }

        public List<Integer> getSellerNos() {
            return sellerNos;
        }
    }

    private Integer updateDeliveryDone(Connection conn, int deliveryNo) throws Exception {

        String sql = """
            UPDATE DELIVERY
            SET DELIVERY_STATUS = '배송완료',
                DELIVERY_END_DATE = SYSDATE,
                UPDATED_DATE = SYSDATE
            WHERE DELIVERY_NO = ?
              AND DELIVERY_STATUS = '배송중'
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deliveryNo);

            if (pstmt.executeUpdate() != 1) {
                return null;
            }
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT ORDER_NO FROM DELIVERY WHERE DELIVERY_NO = ?")) {
            pstmt.setInt(1, deliveryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("ORDER_NO") : null;
            }
        }
    }

    private void updateOrderDone(Connection conn, int orderNo) throws Exception {

        String sql = """
            UPDATE ORDERS
            SET ORDER_STATUS = '배송완료'
            WHERE ORDER_NO = ?
              AND ORDER_STATUS = '배송중'
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.executeUpdate();
        }
    }

    private AdminDeliveryDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        AdminDeliveryDTO dto = new AdminDeliveryDTO();

        dto.setDeliveryNo(rs.getInt("DELIVERY_NO"));
        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setDeliveryServiceCode(rs.getString("DELIVERY_SERVICE_CODE"));
        dto.setInvoiceNo(rs.getString("INVOICE_NO"));
        dto.setDeliveryStatus(rs.getString("DELIVERY_STATUS"));
        dto.setDeliveryStartDate(rs.getTimestamp("DELIVERY_START_DATE"));

        dto.setBuyerName(rs.getString("MEMBER_NAME"));
        dto.setBuyerPhone(rs.getString("PHONE"));

        dto.setZipcode(rs.getString("ZIPCODE"));
        dto.setAddress(rs.getString("ADDRESS"));
        dto.setDetailAddress(rs.getString("DETAIL_ADDRESS"));

        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setProductImageUrl(rs.getString("PRODUCT_IMAGE_URL"));
        dto.setStoreName(rs.getString("STORE_NAME"));
        dto.setItemCount(rs.getInt("ITEM_COUNT"));

        return dto;
    }
}
