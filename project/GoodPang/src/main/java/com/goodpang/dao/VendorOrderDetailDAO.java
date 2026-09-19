package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorOrderDetailDTO;
import com.goodpang.dto.VendorOrderItemDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 주문 상세(vendor-order-detail.jsp) 조회.
 * seller_no까지 같이 확인해서, 다른 판매자의 주문번호를 URL에 넣어도 조회되지 않게 한다.
 */
public class VendorOrderDetailDAO {

    public VendorOrderDetailDTO findByOrderNo(int orderNo, int sellerNo) {

        VendorOrderDetailDTO dto = selectOrder(orderNo, sellerNo);

        if (dto == null) {
            return null;
        }

        dto.getItems().addAll(selectItems(orderNo, sellerNo));
        applyDelivery(orderNo, dto);

        return dto;
    }

    private VendorOrderDetailDTO selectOrder(int orderNo, int sellerNo) {

        String sql = """
            SELECT
                O.ORDER_NO, O.ORDER_DATE, O.ORDER_STATUS,
                O.DELIVERY_FEE, O.TOTAL_PRICE, O.PRODUCT_AMOUNT,
                O.INSTANT_DISCOUNT, O.COUPON_DISCOUNT, O.CASH_USED,
                M.MEMBER_NAME, M.PHONE,
                DA.RECEIVER_NAME, DA.TEL AS RECEIVER_TEL, DA.ZIPCODE,
                DA.ADDRESS, DA.DETAIL_ADDRESS, DA.REQUEST_MSG
            FROM ORDERS O
                JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
                JOIN ORDER_ADDRESS DA ON O.ORDER_ADDRESS_NO = DA.ORDER_ADDRESS_NO
            WHERE O.ORDER_NO = ?
              AND EXISTS (
                    SELECT 1
                    FROM ORDER_DETAIL OD
                        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                    WHERE OD.ORDER_NO = O.ORDER_NO
                      AND P.SELLER_NO = ?
              )
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return mapOrderRow(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<VendorOrderItemDTO> selectItems(int orderNo, int sellerNo) {

        List<VendorOrderItemDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                OD.ORDER_DETAIL_NO, P.PRODUCT_NAME,
                PO.OPTION1_VALUE, PO.OPTION2_VALUE, PO.OPTION3_VALUE,
                OD.ORDER_QTY, OD.PRICE
            FROM ORDER_DETAIL OD
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN PRODUCT_OPTION PO ON OD.OPTION_ID = PO.OPTION_ID
            WHERE OD.ORDER_NO = ?
              AND P.SELLER_NO = ?
            ORDER BY OD.ORDER_DETAIL_NO
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    VendorOrderItemDTO item = new VendorOrderItemDTO();

                    item.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
                    item.setProductName(rs.getString("PRODUCT_NAME"));
                    item.setOptionLabel(buildOptionLabel(
                            rs.getString("OPTION1_VALUE"),
                            rs.getString("OPTION2_VALUE"),
                            rs.getString("OPTION3_VALUE")));
                    item.setOrderQty(rs.getInt("ORDER_QTY"));
                    item.setPrice(rs.getInt("PRICE"));

                    list.add(item);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 가장 최근 배송 이력 1건 (아직 배송 시작 전이면 없음)
    private void applyDelivery(int orderNo, VendorOrderDetailDTO dto) {

        String sql = """
            SELECT DELIVERY_SERVICE_CODE, INVOICE_NO, DELIVERY_STATUS, DELIVERY_START_DATE, DELIVERY_END_DATE
            FROM DELIVERY
            WHERE ORDER_NO = ?
            ORDER BY DELIVERY_NO DESC
            FETCH FIRST 1 ROWS ONLY
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto.setDeliveryServiceCode(rs.getString("DELIVERY_SERVICE_CODE"));
                    dto.setInvoiceNo(rs.getString("INVOICE_NO"));
                    dto.setDeliveryStatus(rs.getString("DELIVERY_STATUS"));
                    dto.setDeliveryStartDate(rs.getTimestamp("DELIVERY_START_DATE"));
                    dto.setDeliveryEndDate(rs.getTimestamp("DELIVERY_END_DATE"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VendorOrderDetailDTO mapOrderRow(ResultSet rs) throws java.sql.SQLException {

        VendorOrderDetailDTO dto = new VendorOrderDetailDTO();

        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));
        dto.setOrderStatus(rs.getString("ORDER_STATUS"));

        dto.setDeliveryFee(rs.getInt("DELIVERY_FEE"));
        dto.setTotalPrice(rs.getInt("TOTAL_PRICE"));
        dto.setProductAmount(rs.getInt("PRODUCT_AMOUNT"));
        dto.setInstantDiscount(rs.getInt("INSTANT_DISCOUNT"));
        dto.setCouponDiscount(rs.getInt("COUPON_DISCOUNT"));
        dto.setCashUsed(rs.getInt("CASH_USED"));

        dto.setBuyerName(rs.getString("MEMBER_NAME"));
        dto.setBuyerPhone(rs.getString("PHONE"));

        dto.setReceiverName(rs.getString("RECEIVER_NAME"));
        dto.setReceiverTel(rs.getString("RECEIVER_TEL"));
        dto.setZipcode(rs.getString("ZIPCODE"));
        dto.setAddress(rs.getString("ADDRESS"));
        dto.setDetailAddress(rs.getString("DETAIL_ADDRESS"));
        dto.setRequestMsg(rs.getString("REQUEST_MSG"));

        return dto;
    }

    private String buildOptionLabel(String option1Value, String option2Value, String option3Value) {

        StringBuilder sb = new StringBuilder();

        for (String value : new String[] { option1Value, option2Value, option3Value }) {
            if (value != null && !value.isBlank()) {
                if (sb.length() > 0) {
                    sb.append(" / ");
                }
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
