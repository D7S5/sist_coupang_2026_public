package com.goodpang.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorOrderListDTO;
import com.goodpang.dto.VendorOrderStatSummaryDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 주문 목록(vendor_orders.jsp) 조회.
 * 판매자의 상품이 포함된 주문 라인(ORDER_DETAIL)을 기준으로 한 건씩 보여준다.
 * 배송상태(출고대기/배송중 등)는 별도 컬럼이 없어서 ORDER_STATUS만 다룬다.
 */
public class VendorOrderListDAO {

    /*
     * 판매자(sellerNo)의 상품이 포함된 주문 목록 - 최근 주문순.
     * 검색 필터는 전부 선택사항(null/빈 문자열이면 조건 안 붙임).
     *
     * orderStatus / deliveryStatus / paymentStatus 세 필터는 결국 같은 컬럼(ORDER_STATUS)을
     * 서로 다른 관점(주문 전체 상태 / 배송 진행도 / 결제 여부)으로 보여주는 것이라, 셋 다
     * 이 컬럼 하나로 매핑해서 조건을 만든다 - 화면에 이 세 필터를 동시에 다르게 걸면 결과가
     * 안 겹쳐서 0건이 될 수 있는데, 그건 원래 서로 다른 값을 요구했으니 정상 동작이다.
     *   orderStatus:    결제완료 / 배송중 / 배송완료 / 주문취소 (실제 ORDER_STATUS 값 그대로)
     *   deliveryStatus: 출고대기(=결제완료) / 배송중 / 배송완료 (주문취소 제외한 배송 관점)
     *   paymentStatus:  결제완료(=주문취소 아님) / 결제취소(=주문취소) ("결제대기"는 시스템에 없는
     *                   상태라 옵션 자체를 안 둠 - VendorDashboardStatDTO 관련 논의 참고)
     */
    public List<VendorOrderListDTO> findBySellerNo(int sellerNo, LocalDate startDate, LocalDate endDate,
            String orderStatus, String deliveryStatus, String paymentStatus, int page, int pageSize) {

        List<VendorOrderListDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                O.ORDER_NO,
                OD.ORDER_DETAIL_NO,
                P.PRODUCT_NAME,
                PO.OPTION1_VALUE,
                PO.OPTION2_VALUE,
                PO.OPTION3_VALUE,
                OD.ORDER_QTY,
                OD.PRICE,
                O.ORDER_STATUS,
                O.ORDER_DATE,
                M.MEMBER_NAME,
                M.PHONE,
                IMG.IMAGE_URL AS THUMBNAIL_URL
            FROM ORDER_DETAIL OD
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
            """);

        List<Object> params = new ArrayList<>();
        params.add(sellerNo);

        appendFilters(sql, params, startDate, endDate, orderStatus, deliveryStatus, paymentStatus);

        sql.append(" ORDER BY O.ORDER_DATE DESC, O.ORDER_NO DESC");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
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

    // 페이지네이션용 총 개수 - 필터 조건은 findBySellerNo와 반드시 같아야 함(appendFilters 공유)
    public int countBySellerNo(int sellerNo, LocalDate startDate, LocalDate endDate,
            String orderStatus, String deliveryStatus, String paymentStatus) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
            WHERE P.SELLER_NO = ?
            """);

        List<Object> params = new ArrayList<>();
        params.add(sellerNo);

        appendFilters(sql, params, startDate, endDate, orderStatus, deliveryStatus, paymentStatus);

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
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

    // findBySellerNo/countBySellerNo가 공유하는 검색 필터. orderStatus/deliveryStatus/paymentStatus
    // 세 필터가 결국 같은 컬럼(ORDER_STATUS)을 다른 관점으로 보여준다는 설명은 findBySellerNo 상단 주석 참고.
    private void appendFilters(StringBuilder sql, List<Object> params, LocalDate startDate, LocalDate endDate,
            String orderStatus, String deliveryStatus, String paymentStatus) {

        if (startDate != null) {
            sql.append(" AND TRUNC(O.ORDER_DATE) >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null) {
            sql.append(" AND TRUNC(O.ORDER_DATE) <= ?");
            params.add(Date.valueOf(endDate));
        }

        if (orderStatus != null && !orderStatus.isBlank()) {
            sql.append(" AND O.ORDER_STATUS = ?");
            params.add(orderStatus);
        }

        if (deliveryStatus != null && !deliveryStatus.isBlank()) {
            sql.append(" AND O.ORDER_STATUS = ?");
            params.add("출고대기".equals(deliveryStatus) ? "결제완료" : deliveryStatus);
        }

        if ("결제완료".equals(paymentStatus)) {
            sql.append(" AND O.ORDER_STATUS != '주문취소'");
        } else if ("결제취소".equals(paymentStatus)) {
            sql.append(" AND O.ORDER_STATUS = '주문취소'");
        }
    }

    /*
     * 주문/배송 관리 화면 상단 통계 카드 + 우측 배송현황 도넛용 집계.
     * ORDER_STATUS로 실제 존재하는 상태(결제완료/배송중/배송완료)만 센다 - 신규주문/결제대기/
     * 상품준비중/취소반품교환에 대응하는 상태값은 아직 시스템에 없어서 집계 대상에서 제외.
     * DELIVERY_END_DATE 기준으로 "오늘" 배송완료된 건수를 판단한다(AdminDeliveryDAO가
     * 배송완료 처리할 때 그 컬럼에 SYSDATE를 넣음).
     */
    public VendorOrderStatSummaryDTO countStats(int sellerNo) {

        VendorOrderStatSummaryDTO dto = new VendorOrderStatSummaryDTO();

        String sql = """
            SELECT
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '결제완료' THEN O.ORDER_NO END) AS WAITING_COUNT,
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송중' THEN O.ORDER_NO END) AS SHIPPING_COUNT,
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송완료' THEN O.ORDER_NO END) AS DELIVERED_COUNT,
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송완료'
                                      AND D.DELIVERY_END_DATE IS NOT NULL
                                      AND TRUNC(D.DELIVERY_END_DATE) = TRUNC(SYSDATE)
                                 THEN O.ORDER_NO END) AS DELIVERED_TODAY_COUNT
            FROM ORDERS O
                JOIN ORDER_DETAIL OD ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
            WHERE P.SELLER_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto.setWaitingCount(rs.getInt("WAITING_COUNT"));
                    dto.setShippingCount(rs.getInt("SHIPPING_COUNT"));
                    dto.setDeliveredCount(rs.getInt("DELIVERED_COUNT"));
                    dto.setDeliveredTodayCount(rs.getInt("DELIVERED_TODAY_COUNT"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    // shipOrder()의 처리 결과. 화면에 실패 사유를 구분해서 보여주기 위해 boolean 대신 사용.
    public enum ShipResult {
        SUCCESS,
        INVOICE_DUPLICATE,   // 같은 택배사에 이미 등록된 송장번호
        FAILED               // 그 외(주문을 찾을 수 없음 등)
    }

    /*
     * 결제완료 -> 배송중 전환 + DELIVERY 행 생성(송장번호 저장), 하나의 트랜잭션으로 처리.
     * ORDERS에는 seller_no가 없어서, 이 주문에 이 판매자의 상품이 실제로 포함돼 있는지
     * ORDER_DETAIL/PRODUCT로 확인한 뒤에만 처리한다.
     */
    public ShipResult shipOrder(int orderNo, int sellerNo, String invoiceNo) {

        try (Connection conn = ConnectionProvider.getConnection()) {

            conn.setAutoCommit(false);

            try {
                String deliveryServiceCode = findDeliveryServiceCode(conn, orderNo, sellerNo);

                if (deliveryServiceCode == null) {
                    conn.rollback();
                    return ShipResult.FAILED;
                }

                if (invoiceNoInUse(conn, deliveryServiceCode, invoiceNo)) {
                    conn.rollback();
                    return ShipResult.INVOICE_DUPLICATE;
                }

                insertDelivery(conn, orderNo, deliveryServiceCode, invoiceNo);

                boolean updated = updateStatusToShipping(conn, orderNo, sellerNo);

                if (!updated) {
                    conn.rollback();
                    return ShipResult.FAILED;
                }

                conn.commit();
                return ShipResult.SUCCESS;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ShipResult.FAILED;
    }

    // 이 판매자가 이 주문에 등록한 상품 중 하나의 택배사 코드를 대표로 사용
    private String findDeliveryServiceCode(Connection conn, int orderNo, int sellerNo) throws Exception {

        String sql = """
            SELECT P.DELIVERY_SERVICE_CODE
            FROM ORDER_DETAIL OD
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
            WHERE OD.ORDER_NO = ?
              AND P.SELLER_NO = ?
              AND ROWNUM = 1
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("DELIVERY_SERVICE_CODE") : null;
            }
        }
    }

    // 같은 택배사에 이미 등록된 송장번호인지 확인 (택배사가 다르면 번호가 겹쳐도 서로 다른 운송장이라 허용)
    private boolean invoiceNoInUse(Connection conn, String deliveryServiceCode, String invoiceNo) throws Exception {

        String sql = """
            SELECT 1
            FROM DELIVERY
            WHERE DELIVERY_SERVICE_CODE = ?
              AND INVOICE_NO = ?
              AND ROWNUM = 1
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, deliveryServiceCode);
            pstmt.setString(2, invoiceNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertDelivery(Connection conn, int orderNo, String deliveryServiceCode, String invoiceNo) throws Exception {

        String sql = """
            INSERT INTO DELIVERY (
                DELIVERY_NO, ORDER_NO, DELIVERY_SERVICE_CODE, INVOICE_NO,
                DELIVERY_STATUS, DELIVERY_START_DATE, DELIVERY_END_DATE,
                CREATED_DATE, UPDATED_DATE
            ) VALUES (
                SEQ_DELIVERY.NEXTVAL, ?, ?, ?,
                '배송중', SYSDATE, NULL,
                SYSDATE, SYSDATE
            )
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.setString(2, deliveryServiceCode);
            pstmt.setString(3, invoiceNo);

            pstmt.executeUpdate();
        }
    }

    private boolean updateStatusToShipping(Connection conn, int orderNo, int sellerNo) throws Exception {

        String sql = """
            UPDATE ORDERS
            SET ORDER_STATUS = '배송중'
            WHERE ORDER_NO = ?
              AND ORDER_STATUS = '결제완료'
              AND EXISTS (
                    SELECT 1
                    FROM ORDER_DETAIL OD
                        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                    WHERE OD.ORDER_NO = ORDERS.ORDER_NO
                      AND P.SELLER_NO = ?
              )
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, sellerNo);

            return pstmt.executeUpdate() == 1;
        }
    }

    private VendorOrderListDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorOrderListDTO dto = new VendorOrderListDTO();

        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));
        dto.setOptionLabel(buildOptionLabel(rs.getString("OPTION1_VALUE"), rs.getString("OPTION2_VALUE"), rs.getString("OPTION3_VALUE")));
        dto.setOrderQty(rs.getInt("ORDER_QTY"));
        dto.setPrice(rs.getInt("PRICE"));
        dto.setOrderStatus(rs.getString("ORDER_STATUS"));
        dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));
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
