package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.DeliveryLogDTO;
import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.DBConn;

public class TrackingDAO {

    /**
     * 특정 주문의 배송지 기본 정보 + 운송장번호 조회
     */
    public OrderDetailDTO getTrackingHeaderInfo(int orderNo) {
        OrderDetailDTO dto = null;

        String sql = """
            SELECT 
                o.order_no,
                o.order_date,
                o.order_status,
                oa.receiver_name AS memberName,
                oa.tel AS phone,
                oa.address AS address,
                oa.detail_address AS detailAddress,
                oa.request_msg AS requestMsg,
                d.invoice_no,
                oa.receive_location AS receiveLocation -- 추가된 수령방법 컬럼
            FROM ORDERS o
            JOIN ORDER_ADDRESS oa ON o.order_address_no = oa.order_address_no
            LEFT JOIN DELIVERY d ON o.order_no = d.order_no
            WHERE o.order_no = ?
            """;

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto = new OrderDetailDTO();
                    dto.setOrderNo(rs.getInt("order_no"));
                    dto.setOrderDate(rs.getTimestamp("order_date"));
                    dto.setOrderStatus(rs.getString("order_status"));
                    dto.setMemberName(rs.getString("memberName"));
                    dto.setPhone(rs.getString("phone"));
                    dto.setAddress(rs.getString("address"));
                    dto.setDetailAddress(rs.getString("detailAddress"));
                    dto.setRequestMsg(rs.getString("requestMsg"));
                    dto.setInvoiceNo(rs.getString("invoice_no"));
                    dto.setReceiveLocation(rs.getString("receiveLocation")); // ★ DTO 바인딩 추가
                    
                 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConn.close();
        }

        return dto;
    }

    /**
     * 시간대별 배송 위치 이력 목록 조회 (최신순)
     */
    public List<DeliveryLogDTO> getDeliveryLogList(int orderNo) {
        List<DeliveryLogDTO> list = new ArrayList<>();

        String sql = """
            SELECT log_no, order_no, log_time, current_location, delivery_status
              FROM DELIVERY_LOG
             WHERE order_no = ?
             ORDER BY log_time DESC
            """;
        
        System.out.println(sql);

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DeliveryLogDTO log = new DeliveryLogDTO();
                    log.setLogNo(rs.getLong("log_no"));
                    log.setOrderNo(rs.getInt("order_no"));
                    log.setLogTime(rs.getTimestamp("log_time"));
                    log.setCurrentLocation(rs.getString("current_location"));
                    log.setDeliveryStatus(rs.getString("delivery_status"));

                    list.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConn.close();
        }

        return list;
    }
}