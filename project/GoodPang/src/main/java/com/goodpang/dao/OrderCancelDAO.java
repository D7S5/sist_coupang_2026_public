package com.goodpang.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.DBConn;

public class OrderCancelDAO {
	
	// =========================================================
	// [추가 1] 회원의 취소/반품 내역 전체 개수 조회
	// =========================================================
	public int getCancelHistoryCount(int memberNo) {
	    int count = 0;
	    String sql = """
	        SELECT COUNT(*)
	        FROM ORDERS o
	        JOIN ORDER_DETAIL od ON o.order_no = od.order_no
	        JOIN PRODUCT_RETURN pr ON od.order_detail_no = pr.order_detail_no
	        WHERE o.member_no = ?
	        """;

	    try (Connection conn = ConnectionProvider.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                count = rs.getInt(1);
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("[ERROR OrderCancelDAO] 취소 내역 카운트 조회 실패");
	        e.printStackTrace();
	    } finally {
	        DBConn.close();
	    }
	    return count;
	}

	// =========================================================
	// [추가 2] 회원의 취소/반품 내역 페이지당 5개씩 페이징 조회
	// =========================================================
	public List<OrderDetailDTO> getCancelHistoryPaged(int memberNo, int page, int pageSize) {
	    List<OrderDetailDTO> list = new ArrayList<>();
	    int startRow = (page - 1) * pageSize + 1;
	    int endRow = page * pageSize;

	    String sql = """
	        SELECT * FROM (
	            SELECT ROWNUM AS rnum, T.* FROM (
	                SELECT 
	                    o.order_no,
	                    o.order_date,
	                    o.order_status,
	                    o.delivery_fee,
	                    od.order_detail_no,
	                    od.order_qty AS quantity,
	                    od.price AS total_price,
	                    p.product_no,
	                    p.product_name,
	                    po.option1_type, po.option1_value,
	                    po.option2_type, po.option2_value,
	                    pr.return_no,
	                    pr.request_date,
	                    pr.return_reason,
	                    pr.refund_amount,
	                    pr.expected_cancel_date
	                FROM ORDERS o
	                JOIN ORDER_DETAIL od ON o.order_no = od.order_no
	                JOIN PRODUCT p ON od.product_no = p.product_no
	                LEFT JOIN PRODUCT_OPTION po ON od.option_id = po.option_id
	                JOIN PRODUCT_RETURN pr ON od.order_detail_no = pr.order_detail_no
	                WHERE o.member_no = ?
	                ORDER BY pr.request_date DESC
	            ) T WHERE ROWNUM <= ?
	        ) WHERE rnum >= ?
	        """;

	    try (Connection conn = ConnectionProvider.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, memberNo);
	        pstmt.setInt(2, endRow);
	        pstmt.setInt(3, startRow);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                OrderDetailDTO dto = new OrderDetailDTO();

	                // 주문 기본 정보
	                dto.setOrderNo(rs.getInt("order_no"));
	                dto.setOrderDate(rs.getTimestamp("order_date"));
	                dto.setOrderStatus(rs.getString("order_status"));
	                dto.setDeliveryFee(rs.getInt("delivery_fee"));

	                // 주문 상세 및 상품 정보
	                dto.setOrderDetailNo(rs.getLong("order_detail_no"));
	                dto.setQuantity(rs.getInt("quantity"));
	                dto.setTotalPrice(rs.getInt("total_price"));
	                dto.setProductNo(rs.getLong("product_no"));
	                dto.setProductName(rs.getString("product_name"));

	                // 옵션 정보
	                dto.setOption1Type(rs.getString("option1_type"));
	                dto.setOption1Value(rs.getString("option1_value"));
	                dto.setOption2Type(rs.getString("option2_type"));
	                dto.setOption2Value(rs.getString("option2_value"));

	                // 취소/반품(PRODUCT_RETURN) 상세 정보
	                dto.setReturnNo(rs.getLong("return_no"));
	                dto.setRequestDate(rs.getTimestamp("request_date"));
	                dto.setReturnReason(rs.getString("return_reason"));
	                dto.setRefundAmount(rs.getInt("refund_amount"));
	                dto.setExpectedCancelDate(rs.getTimestamp("expected_cancel_date"));

	                list.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("[ERROR OrderCancelDAO] 취소 내역 페이징 조회 실패");
	        e.printStackTrace();
	    } finally {
	        DBConn.close();
	    }

	    return list;
	}
	
	
	
	

	    // [기존 cancelOrder 메서드는 그대로 유지]

	    /**
	     * 특정 회원의 취소/반품 내역 목록 조회
	     * @throws NamingException 
	     */
	    public List<OrderDetailDTO> getCancelHistoryList(int memberNo) throws NamingException {
	        List<OrderDetailDTO> list = new ArrayList<>();

	        String sql = """
	            SELECT 
	                o.order_no,
	                o.order_date,
	                o.order_status,
	                o.delivery_fee,
	                od.order_detail_no,
	                od.order_qty AS quantity,
	                od.price AS total_price,
	                p.product_no,
	                p.product_name,
	                po.option1_type, po.option1_value,
	                po.option2_type, po.option2_value,
	                pr.return_no,
	                pr.request_date,
	                pr.return_reason,
	                pr.refund_amount,
	                pr.expected_cancel_date
	                
	            FROM ORDERS o
	            JOIN ORDER_DETAIL od ON o.order_no = od.order_no
	            JOIN PRODUCT p ON od.product_no = p.product_no
	            LEFT JOIN PRODUCT_OPTION po ON od.option_id = po.option_id
	            JOIN PRODUCT_RETURN pr ON od.order_detail_no = pr.order_detail_no
	            
	            WHERE o.member_no = ?
	            ORDER BY pr.request_date DESC
	            """;

	        try (Connection conn = ConnectionProvider.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setInt(1, memberNo);

	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    OrderDetailDTO dto = new OrderDetailDTO();

	                    // 주문 정보
	                    dto.setOrderNo(rs.getInt("order_no"));
	                    dto.setOrderDate(rs.getTimestamp("order_date"));
	                    dto.setOrderStatus(rs.getString("order_status"));
	                    dto.setDeliveryFee(rs.getInt("delivery_fee"));

	                    // 주문 상세 및 상품 정보
	                    dto.setOrderDetailNo(rs.getLong("order_detail_no"));
	                    dto.setQuantity(rs.getInt("quantity"));
	                    dto.setTotalPrice(rs.getInt("total_price"));
	                    dto.setProductNo(rs.getLong("product_no"));
	                    dto.setProductName(rs.getString("product_name"));

	                    // 옵션 정보
	                    dto.setOption1Type(rs.getString("option1_type"));
	                    dto.setOption1Value(rs.getString("option1_value"));
	                    dto.setOption2Type(rs.getString("option2_type"));
	                    dto.setOption2Value(rs.getString("option2_value"));

	                    // 취소/반품(PRODUCT_RETURN) 정보
	                    dto.setReturnNo(rs.getLong("return_no"));
	                    dto.setReturnReason(rs.getString("return_reason"));
	                    dto.setRefundAmount(rs.getInt("refund_amount"));
	                    dto.setRequestDate(rs.getTimestamp("request_date"));
	                    dto.setExpectedCancelDate(rs.getTimestamp("expected_cancel_date"));
	                    
	                    // 대표 이미지 URL 바인딩
	                    dto.setImageUrl(rs.getString("IMAGE_URL"));

	                    list.add(dto);
	                }
	            }
	        } catch (SQLException e) {
	            System.err.println("[ERROR OrderCancelDAO] 취소 내역 목록 조회 실패");
	            e.printStackTrace();
	        } finally {
	            DBConn.close();
	        }

	        return list;
	    }
	

    /**
     * 주문 취소 처리
     * 1) ORDERS 테이블: order_status = '주문취소' UPDATE
     * 2) ORDER_DETAIL 조회: order_detail_no, order_qty, price 추출
     * 3) PRODUCT_RETURN 테이블: 정확한 컬럼(order_detail_no)으로 취소 내역 INSERT
     */
    public boolean cancelOrder(int orderNo, int memberNo, String cancelReason) {
        /*
         * ★ WHERE 조건 세 개가 각각 다른 사고를 막는다.
         *
         *  order_no = ?            어떤 주문인지
         *
         *  member_no = ?           남의 주문 취소 방지.
         *                          취소는 GET 링크(/order/cancel_action?orderNo=87)라
         *                          주소창에 남의 주문번호만 넣으면 취소가 되던 상태였음.
         *
         *  order_status IN (...)   ① 이미 '주문취소'면 0행 → 재고 복원이 두 번 실행되지 않음
         *                             (GET 이라 F5 한 번으로 재실행되기 쉬움)
         *                          ② '배송완료'된 주문은 취소가 아니라 반품으로 처리해야 함
         *
         * ★ 왜 자바에서 미리 SELECT 로 확인하지 않고 WHERE 에 넣는가
         *   취소 버튼을 따닥 눌러 요청이 동시에 두 개 들어오면, 둘 다 '결제완료'를 읽고
         *   둘 다 통과해버린다(경쟁 조건). WHERE 안에 넣으면 오라클이 그 행을 잠그고
         *   판단까지 한 문장에서 하기 때문에 두 번째는 반드시 0행이 된다.
         */
    	
    	
        // 1. ORDERS 테이블 상태 변경 SQL
        String sqlOrderUpdate = """
            UPDATE ORDERS 
               SET order_status = '주문취소' 
             WHERE order_no = ?
             AND member_no = ?
             AND order_status IN ('주문접수', '결제완료')
            """;

        // 2. ORDER_DETAIL 정보 조회 SQL (order_detail_no 및 수량/금액 추출)
        String sqlSelectDetail = """
            SELECT order_detail_no, order_qty, price 
              FROM ORDER_DETAIL 
             WHERE order_no = ?
            """;

     // 기존: SEQ_PRODUCT_RETURN.NEXTVAL 대신 (SELECT NVL(MAX(return_no), 0) + 1 FROM PRODUCT_RETURN) 사용
        String sqlReturnInsert = """
            INSERT INTO PRODUCT_RETURN (
                return_no, request_date, return_qty, return_reason, return_status, refund_amount, order_detail_no
            ) VALUES (
                (SELECT NVL(MAX(return_no), 0) + 1 FROM PRODUCT_RETURN), 
                SYSDATE, ?, ?, '취소완료', ?, ?
            )
            """;

        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtReturn = null;
        ResultSet rs = null;
        boolean isSuccess = false;

        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // [Step 1] ORDERS 상태 업데이트
            pstmtOrder = conn.prepareStatement(sqlOrderUpdate);
            pstmtOrder.setInt(1, orderNo);
            pstmtOrder.setInt(2, memberNo);
            int orderResult = pstmtOrder.executeUpdate();

            // [Step 2 & 3] ORDER_DETAIL 조회 후 PRODUCT_RETURN에 등록
            if (orderResult > 0) {
                pstmtSelect = conn.prepareStatement(sqlSelectDetail);
                pstmtSelect.setInt(1, orderNo);
                rs = pstmtSelect.executeQuery();

                pstmtReturn = conn.prepareStatement(sqlReturnInsert);

                int returnResultCount = 0;
                while (rs.next()) {
                    long orderDetailNo = rs.getLong("order_detail_no");
                    int returnQty = rs.getInt("order_qty");
                    int refundAmount = rs.getInt("price");

                    pstmtReturn.setInt(1, returnQty);
                    pstmtReturn.setString(2, cancelReason);
                    pstmtReturn.setInt(3, refundAmount);
                    pstmtReturn.setLong(4, orderDetailNo);

                    returnResultCount += pstmtReturn.executeUpdate();
                }

                if (returnResultCount > 0) {
                	// ★ 재고 복원 — 커밋 전에 한다. 실패하면 취소 전체가 롤백돼야 하므로.
                    restoreStock(conn, orderNo);
                	
                	conn.commit(); // 성공 시 DB 적용
                    isSuccess = true;
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
            }

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (pstmtOrder != null) try { pstmtOrder.close(); } catch (Exception e) {}
            if (pstmtSelect != null) try { pstmtSelect.close(); } catch (Exception e) {}
            if (pstmtReturn != null) try { pstmtReturn.close(); } catch (Exception e) {}
            DBConn.close();
        }

        return isSuccess;
    }

   
    /**
     * 취소 상세 페이지 정보 조회 (PAYMENT_METHOD 조인 수정)
     */
    public List<OrderDetailDTO> getCancelDetailList(int orderNo) {
        List<OrderDetailDTO> list = new ArrayList<>();
        
        
   
        String sql = """
            SELECT 
                o.ORDER_NO,
                o.TOTAL_PRICE,
                o.ORDER_DATE,
                o.ORDER_STATUS,
                o.DELIVERY_FEE,
                p.PRODUCT_NO,
                p.PRODUCT_NAME,
                od.ORDER_DETAIL_NO,
                od.ORDER_QTY AS QUANTITY,
                od.PRICE AS ITEM_PRICE,
                po.OPTION1_TYPE, po.OPTION1_VALUE,
                po.OPTION2_TYPE, po.OPTION2_VALUE,
                pr.RETURN_NO,
                pr.REQUEST_DATE,
                pr.EXPECTED_CANCEL_DATE,
                pr.RETURN_REASON,
                pr.REFUND_AMOUNT,
                pay.PAYMENT_METHOD,
                img.IMAGE_URL,
                
                /* PAYMENT_METHOD 테이블 내 CARD_COMPANY 한글 변환 */
                CASE pm.CARD_COMPANY
                    WHEN 'BC'      THEN '비씨카드'
                    WHEN 'SHINHAN' THEN '신한카드'
                    WHEN 'KB'      THEN 'KB국민카드'
                    WHEN 'SAMSUNG' THEN '삼성카드'
                    WHEN 'HYUNDAI' THEN '현대카드'
                    WHEN 'LOTTE'   THEN '롯데카드'
                    WHEN 'HANA'    THEN '하나카드'
                    WHEN 'WOORI'   THEN '우리카드'
                    WHEN 'NH'      THEN 'NH농협카드'
                    ELSE pm.CARD_COMPANY
                END AS CARD_COMPANY_NAME,

                /* PAYMENT_METHOD 테이블 내 BANK_CODE 한글 변환 */
                CASE pm.BANK_CODE
                    WHEN 'SHINHAN' THEN '신한은행'
                    WHEN 'KB'      THEN 'KB국민은행'
                    WHEN 'WOORI'   THEN '우리은행'
                    WHEN 'NH'      THEN 'NH농협은행'
                    WHEN 'HANA'    THEN '하나은행'
                    WHEN 'KAKAO'   THEN '카카오뱅크'
                    WHEN 'TOSS'    THEN '토스뱅크'
                    ELSE pm.BANK_CODE
                END AS BANK_NAME

            FROM ORDERS o
            JOIN ORDER_DETAIL od ON o.ORDER_NO = od.ORDER_NO
            JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO
            LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID
            LEFT JOIN PRODUCT_RETURN pr ON od.ORDER_DETAIL_NO = pr.ORDER_DETAIL_NO
            LEFT JOIN PAYMENT pay ON o.ORDER_NO = pay.ORDER_NO
            LEFT JOIN PAYMENT_METHOD pm ON pay.PAYMENT_METHOD_NO = pm.PAYMENT_METHOD_NO
            LEFT JOIN (
                SELECT product_no, image_url 
                FROM (
                    SELECT product_no, image_url, 
                           ROW_NUMBER() OVER(PARTITION BY product_no ORDER BY image_no ASC) as rn 
                    FROM PRODUCT_IMAGE 
                    WHERE image_purpose = '대표'
                ) WHERE rn = 1
            ) img ON p.PRODUCT_NO = img.PRODUCT_NO
            WHERE o.ORDER_NO = ?
            """;

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetailDTO dto = new OrderDetailDTO();

                    // 주문 기본 정보
                    dto.setOrderNo(rs.getInt("ORDER_NO"));
                    dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));
                    dto.setOrderStatus(rs.getString("ORDER_STATUS"));
                 

                    // 상품 및 옵션 정보
                    dto.setProductNo(rs.getLong("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setOrderDetailNo(rs.getLong("ORDER_DETAIL_NO"));
                    dto.setQuantity(rs.getInt("QUANTITY"));
                    dto.setItemPrice(rs.getInt("ITEM_PRICE"));

                    dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                    dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                    dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                    dto.setOption2Value(rs.getString("OPTION2_VALUE"));

                    // 취소/반품 상세 정보
                    dto.setReturnNo(rs.getLong("RETURN_NO"));
                    dto.setRequestDate(rs.getTimestamp("REQUEST_DATE"));
                    dto.setExpectedCancelDate(rs.getTimestamp("EXPECTED_CANCEL_DATE"));
                    dto.setReturnReason(rs.getString("RETURN_REASON"));
                    dto.setRefundAmount(rs.getInt("REFUND_AMOUNT"));

                    // 결제 수단 및 카드사/은행명 매핑 (PAYMENT_METHOD 테이블 참조)
                    dto.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
                    dto.setCardCompanyName(rs.getString("CARD_COMPANY_NAME"));
                    dto.setBankName(rs.getString("BANK_NAME"));
                    
                    dto.setImageUrl(rs.getString("IMAGE_URL"));
                    
                    dto.setTotalPrice(rs.getInt("TOTAL_PRICE"));
                    dto.setDeliveryFee(rs.getInt("DELIVERY_FEE"));

                    list.add(dto);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR OrderCancelDAO] 취소 상세 정보 조회 실패");
            e.printStackTrace();
        } finally {
            DBConn.close();
        }

        return list;
    }


private boolean isWowMember(int memberNo) {
		// TODO Auto-generated method stub
		return false;
	}

/*
 * 재고 복원 — DB 프로시저 PRC_ORDER_STOCK_IN 을 부른다.
 *
 * ★ 이 메서드는 "이미 취소가 확정된 주문"에만 불러야 한다.
 *   호출하는 자리가 UPDATE ORDERS ... 가 1행을 바꾼 경우 안쪽이라 그 조건이 지켜진다.
 *   (이미 취소된 주문은 UPDATE 가 0행이 되어 여기까지 오지 않음 → 재고 2배 방지)
 */
		private void restoreStock(
		        Connection conn,
		        int orderNo)
		        throws SQLException {

    String sql = "{ call PRC_ORDER_STOCK_IN(?, ?) }";

    try (CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setInt(1, orderNo);
        cstmt.registerOutParameter(2, Types.NUMERIC);

        cstmt.execute();

        System.out.println(
                "[DEBUG OrderCancelDAO] 재고 복원 완료 - orderNo: "
                + orderNo + ", 복원 줄 수: " + cstmt.getInt(2));
    }
}

}