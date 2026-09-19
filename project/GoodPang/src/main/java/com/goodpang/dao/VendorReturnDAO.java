package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorReturnDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 취소/반품/교환 관리(vendor-return.jsp) 조회.
 * PRODUCT_RETURN에는 취소/반품/교환을 구분하는 컬럼이 따로 없고 RETURN_STATUS(문자열)만 있어서,
 * '취소'/'반품'/'교환'으로 시작하는 상태값을 기준으로 유형을 나눠서 보여준다.
 * 지금은 주문취소(OrderCancelDAO.cancelOrder)만 실제로 이 테이블에 '취소완료' 상태로 데이터를
 * 쌓고 있고, 반품/교환 신청 화면은 아직 없어서 그 두 유형은 데이터가 비어있을 수 있다.
 */
public class VendorReturnDAO {

    // 판매자(sellerNo)의 상품이 포함된 취소/반품/교환 신청 목록 - 최근 신청순
    public List<VendorReturnDTO> findBySellerNo(int sellerNo) {

        List<VendorReturnDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                PR.RETURN_NO, PR.ORDER_DETAIL_NO, O.ORDER_NO,
                PR.RETURN_QTY, PR.REFUND_AMOUNT, PR.RETURN_REASON, PR.RETURN_STATUS, PR.REQUEST_DATE,
                CASE
                    WHEN PR.RETURN_STATUS LIKE '취소%' THEN '취소'
                    WHEN PR.RETURN_STATUS LIKE '반품%' THEN '반품'
                    WHEN PR.RETURN_STATUS LIKE '교환%' THEN '교환'
                    ELSE '기타'
                END AS RETURN_TYPE,
                P.PRODUCT_NAME,
                PO.OPTION1_VALUE, PO.OPTION2_VALUE, PO.OPTION3_VALUE,
                M.MEMBER_NAME, M.PHONE,
                IMG.IMAGE_URL AS THUMBNAIL_URL
            FROM PRODUCT_RETURN PR
                JOIN ORDER_DETAIL OD ON PR.ORDER_DETAIL_NO = OD.ORDER_DETAIL_NO
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
                LEFT JOIN PRODUCT_OPTION PO ON OD.OPTION_ID = PO.OPTION_ID
                LEFT JOIN (
                    SELECT PRODUCT_NO, IMAGE_URL,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            WHERE P.SELLER_NO = ?
            ORDER BY PR.REQUEST_DATE DESC
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

    private VendorReturnDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorReturnDTO dto = new VendorReturnDTO();

        dto.setReturnNo(rs.getLong("RETURN_NO"));
        dto.setOrderDetailNo(rs.getLong("ORDER_DETAIL_NO"));
        dto.setOrderNo(rs.getInt("ORDER_NO"));

        dto.setReturnQty(rs.getInt("RETURN_QTY"));
        dto.setRefundAmount(rs.getInt("REFUND_AMOUNT"));
        dto.setReturnReason(rs.getString("RETURN_REASON"));
        dto.setReturnStatus(rs.getString("RETURN_STATUS"));
        dto.setReturnType(rs.getString("RETURN_TYPE"));
        dto.setRequestDate(rs.getTimestamp("REQUEST_DATE"));

        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setOptionLabel(buildOptionLabel(
                rs.getString("OPTION1_VALUE"), rs.getString("OPTION2_VALUE"), rs.getString("OPTION3_VALUE")));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

        dto.setBuyerName(rs.getString("MEMBER_NAME"));
        dto.setBuyerPhone(rs.getString("PHONE"));

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
