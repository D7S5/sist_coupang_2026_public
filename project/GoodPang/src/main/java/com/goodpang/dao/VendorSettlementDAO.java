package com.goodpang.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorSettlementDTO;
import com.goodpang.dto.VendorSettlementDetailDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 정산관리(vendor-settlement.jsp) 조회.
 * 배송완료된 주문을, 그 주문의 DELIVERY_END_DATE(배송완료일) 기준 1주 단위(월요일 시작)로
 * 묶어서 매출액을 집계한다. 수수료율/정산주기는 VendorSettlementDTO 상단 주석 참고 - 실제
 * 값이 아니라 가정치다.
 */
public class VendorSettlementDAO {

    public List<VendorSettlementDTO> findBySellerNo(int sellerNo) {

        List<VendorSettlementDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                TRUNC(D.DELIVERY_END_DATE, 'IW') AS PERIOD_START,
                COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT,
                SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
            WHERE P.SELLER_NO = ?
              AND O.ORDER_STATUS = '배송완료'
              AND D.DELIVERY_END_DATE IS NOT NULL
            GROUP BY TRUNC(D.DELIVERY_END_DATE, 'IW')
            ORDER BY PERIOD_START DESC
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

    private VendorSettlementDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorSettlementDTO dto = new VendorSettlementDTO();

        dto.setPeriodStart(rs.getDate("PERIOD_START").toLocalDate());
        dto.setOrderCount(rs.getInt("ORDER_COUNT"));
        dto.setSalesAmount(rs.getLong("SALES_AMOUNT"));

        return dto;
    }

    /*
     * 정산상세(vendor-settlement-detail.jsp) - 특정 정산기간(월요일 시작일)에 포함된
     * 주문라인을 하나씩 그대로 보여준다. findBySellerNo()가 GROUP BY로 합친 걸 풀어서 보여주는 것.
     */
    public List<VendorSettlementDetailDTO> findDetailBySellerNo(int sellerNo, LocalDate periodStart) {

        List<VendorSettlementDetailDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                O.ORDER_NO,
                P.PRODUCT_NAME,
                PO.OPTION1_VALUE, PO.OPTION2_VALUE, PO.OPTION3_VALUE,
                OD.ORDER_QTY, OD.PRICE,
                D.DELIVERY_END_DATE
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
                LEFT JOIN PRODUCT_OPTION PO ON OD.OPTION_ID = PO.OPTION_ID
            WHERE P.SELLER_NO = ?
              AND O.ORDER_STATUS = '배송완료'
              AND TRUNC(D.DELIVERY_END_DATE, 'IW') = ?
            ORDER BY D.DELIVERY_END_DATE, O.ORDER_NO
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);
            pstmt.setDate(2, Date.valueOf(periodStart));

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    list.add(mapDetailRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private VendorSettlementDetailDTO mapDetailRow(ResultSet rs) throws java.sql.SQLException {

        VendorSettlementDetailDTO dto = new VendorSettlementDetailDTO();

        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setOptionLabel(buildOptionLabel(
                rs.getString("OPTION1_VALUE"), rs.getString("OPTION2_VALUE"), rs.getString("OPTION3_VALUE")));
        dto.setQuantity(rs.getInt("ORDER_QTY"));
        dto.setPrice(rs.getInt("PRICE"));
        dto.setDeliveryEndDate(rs.getTimestamp("DELIVERY_END_DATE"));

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
