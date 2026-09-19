package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.goodpang.dto.OrderItemDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.DBConn;

public class OrderListDAO {

    public OrderListDAO() {
    }

    /**
     * 특정 회원의 전체 마이페이지 주문 내역 조회 (기존)
     */
    public List<OrderItemDTO> selectMyPageOrders(int orderNo, int memberNo) throws NamingException {
        List<OrderItemDTO> list = new ArrayList<>();

        String sql = "SELECT "
                + "  o.order_no, o.member_no, o.order_date, o.order_status, o.total_price AS order_total_price, "
                + "  od.order_detail_no, od.order_qty, od.price AS item_price, "
                + "  p.product_no, p.product_name, p.product_price, "
                + "  po.OPTION_ID, po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE "
                + "FROM ORDERS o "
                + "JOIN ORDER_DETAIL od ON o.order_no = od.order_no "
                + "JOIN PRODUCT p ON od.product_no = p.product_no "
                + "LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID "
                + "WHERE o.member_no = ? "
                + "ORDER BY o.order_date DESC, od.order_detail_no ASC";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setOrderNo(rs.getInt("order_no"));
                    dto.setOrderDate(rs.getTimestamp("order_date"));
                    dto.setOrderStatus(rs.getString("order_status"));
                    dto.setTotalPrice(rs.getInt("order_total_price"));
                    dto.setOrderDetailNo(rs.getLong("order_detail_no"));
                    dto.setQuantity(rs.getInt("order_qty"));
                    dto.setItemPrice(rs.getInt("item_price"));
                    dto.setProductNo(rs.getLong("product_no"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                    dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                    dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                    dto.setOption2Value(rs.getString("OPTION2_VALUE"));

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConn.close();
        }

        return list;
    }

    /**
     * 필터 조건(최근6개월 / 년도)에 따른 회원 총 주문 건수 조회
     */
    public int getOrderCount(int memberNo, String yearFilter) {
        int count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ORDERS o WHERE o.member_no = ? ");

        if ("recent".equals(yearFilter) || yearFilter == null || yearFilter.isEmpty()) {
            sql.append("AND o.order_date >= ADD_MONTHS(SYSDATE, -6) ");
        } else {
            sql.append("AND TO_CHAR(o.order_date, 'YYYY') = ? ");
        }

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            pstmt.setInt(1, memberNo);
            if (!"recent".equals(yearFilter) && yearFilter != null && !yearFilter.isEmpty()) {
                pstmt.setString(2, yearFilter);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConn.close();
        }
        return count;
    }

    /**
     * 연도 필터링 및 페이징 목록 조회 (OrderItemDTO 반환 - 대표 이미지 포함)
     */
	/*
	 * public List<OrderItemDTO> getOrderListPaged(int memberNo, String yearFilter,
	 * int page, int pageSize) { List<OrderItemDTO> list = new ArrayList<>(); int
	 * startRow = (page - 1) * pageSize + 1; int endRow = page * pageSize;
	 * 
	 * StringBuilder sql = new StringBuilder(); sql.append("SELECT * FROM ( "); sql.
	 * append("  SELECT o.order_no, o.order_date, o.order_status, o.total_price AS order_total_price, o.delivery_fee, "
	 * ); sql.
	 * append("         od.order_detail_no, od.order_qty, od.price AS item_price, "
	 * ); sql.append("         p.product_no, p.product_name, "); sql.
	 * append("         po.option1_type, po.option1_value, po.option2_type, po.option2_value, "
	 * ); sql.append("         img.IMAGE_URL "); // ★ 1. 대표 이미지 URL 컬럼 추가
	 * sql.append("  FROM ORDERS o ");
	 * sql.append("  JOIN ORDER_DETAIL od ON o.order_no = od.order_no ");
	 * sql.append("  JOIN PRODUCT p ON od.product_no = p.product_no ");
	 * sql.append("  LEFT JOIN PRODUCT_OPTION po ON od.option_id = po.option_id ");
	 * sql.append("  LEFT JOIN ( "); // ★ 2. 대표 이미지 1건을 가져오는 서브쿼리 조인
	 * sql.append("      SELECT product_no, image_url ");
	 * sql.append("      FROM ( ");
	 * sql.append("          SELECT product_no, image_url, "); sql.
	 * append("                 ROW_NUMBER() OVER(PARTITION BY product_no ORDER BY image_no ASC) as rn "
	 * ); sql.append("          FROM PRODUCT_IMAGE ");
	 * sql.append("          WHERE image_purpose = '대표' ");
	 * sql.append("      ) WHERE rn = 1 ");
	 * sql.append("  ) img ON p.product_no = img.product_no ");
	 * sql.append("  WHERE o.member_no = ? ");
	 * 
	 * // 주문번호 기준 페이징을 위한 서브쿼리 sql.append("    AND o.order_no IN ( ");
	 * sql.append("        SELECT order_no FROM ( ");
	 * sql.append("            SELECT order_no, ROWNUM as rnum FROM ( "); sql.
	 * append("                SELECT DISTINCT order_no, order_date FROM ORDERS ");
	 * sql.append("                WHERE member_no = ? ");
	 * 
	 * if ("recent".equals(yearFilter) || yearFilter == null ||
	 * yearFilter.isEmpty()) {
	 * sql.append("                AND order_date >= ADD_MONTHS(SYSDATE, -6) "); }
	 * else { sql.append("                AND TO_CHAR(order_date, 'YYYY') = ? "); }
	 * 
	 * sql.append("                ORDER BY order_date DESC, order_no DESC ");
	 * sql.append("            ) WHERE ROWNUM <= ? ");
	 * sql.append("        ) WHERE rnum >= ? "); sql.append("    ) ");
	 * 
	 * sql.append(") ORDER BY order_date DESC, order_no DESC, order_detail_no ASC");
	 * 
	 * try (Connection conn = ConnectionProvider.getConnection(); PreparedStatement
	 * pstmt = conn.prepareStatement(sql.toString())) {
	 * 
	 * int paramIdx = 1; pstmt.setInt(paramIdx++, memberNo); // 메인 WHERE o.member_no
	 * pstmt.setInt(paramIdx++, memberNo); // 서브쿼리 WHERE member_no
	 * 
	 * if (!"recent".equals(yearFilter) && yearFilter != null &&
	 * !yearFilter.isEmpty()) { pstmt.setString(paramIdx++, yearFilter); // 서브쿼리 연도
	 * 조건 }
	 * 
	 * pstmt.setInt(paramIdx++, endRow); // 서브쿼리 endRow pstmt.setInt(paramIdx++,
	 * startRow); // 서브쿼리 startRow
	 * 
	 * try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) { OrderItemDTO
	 * dto = new OrderItemDTO(); dto.setOrderNo(rs.getInt("order_no"));
	 * dto.setOrderDate(rs.getTimestamp("order_date"));
	 * dto.setOrderStatus(rs.getString("order_status"));
	 * dto.setTotalPrice(rs.getInt("order_total_price"));
	 * dto.setDeliveryFee(rs.getInt("delivery_fee"));
	 * dto.setOrderDetailNo(rs.getLong("order_detail_no"));
	 * dto.setQuantity(rs.getInt("order_qty"));
	 * dto.setItemPrice(rs.getInt("item_price"));
	 * dto.setProductNo(rs.getLong("product_no"));
	 * dto.setProductName(rs.getString("product_name"));
	 * dto.setOption1Type(rs.getString("option1_type"));
	 * dto.setOption1Value(rs.getString("option1_value"));
	 * dto.setOption2Type(rs.getString("option2_type"));
	 * dto.setOption2Value(rs.getString("option2_value"));
	 * 
	 * // ★ 3. ResultSet에서 IMAGE_URL 세팅 dto.setImageUrl(rs.getString("IMAGE_URL"));
	 * 
	 * list.add(dto); } } } catch (Exception e) { e.printStackTrace(); } finally {
	 * DBConn.close(); } return list; }
	 */
    
    public List<OrderItemDTO> getOrderListPaged(
            int memberNo,
            String yearFilter,
            int page,
            int pageSize) {

        List<OrderItemDTO> list = new ArrayList<>();

        int startRow =
                (page - 1) * pageSize + 1;

        int endRow =
                page * pageSize;

        StringBuilder sql =
                new StringBuilder();

        sql.append("SELECT * FROM ( ");
        sql.append("  SELECT ");
        sql.append("      o.order_no, ");
        sql.append("      o.order_date, ");
        sql.append("      o.order_status, ");
        sql.append("      o.total_price AS order_total_price, ");
        sql.append("      o.delivery_fee, ");
        sql.append("      od.order_detail_no, ");
        sql.append("      od.order_qty, ");
        sql.append("      od.price AS item_price, ");
        sql.append("      p.product_no, ");
        sql.append("      p.product_name, ");
        sql.append("      po.option1_type, ");
        sql.append("      po.option1_value, ");
        sql.append("      po.option2_type, ");
        sql.append("      po.option2_value, ");

        sql.append("      ( ");
        sql.append("          SELECT pi.image_url ");
        sql.append("          FROM product_image pi ");
        sql.append("          WHERE pi.product_no = p.product_no ");
        sql.append("          ORDER BY ");
        sql.append("              CASE ");
        sql.append("                  WHEN pi.option_id = od.option_id ");
        sql.append("                       AND pi.image_purpose = '대표' THEN 0 ");
        sql.append("                  WHEN pi.option_id = od.option_id THEN 1 ");
        sql.append("                  WHEN pi.image_purpose = '대표' THEN 2 ");
        sql.append("                  ELSE 3 ");
        sql.append("              END, ");
        sql.append("              pi.image_no ASC ");
        sql.append("          FETCH FIRST 1 ROW ONLY ");
        sql.append("      ) AS image_url ");

        sql.append("  FROM orders o ");
        sql.append("  JOIN order_detail od ");
        sql.append("    ON o.order_no = od.order_no ");
        sql.append("  JOIN product p ");
        sql.append("    ON od.product_no = p.product_no ");
        sql.append("  LEFT JOIN product_option po ");
        sql.append("    ON od.option_id = po.option_id ");
        sql.append("  WHERE o.member_no = ? ");

        sql.append("    AND o.order_no IN ( ");
        sql.append("        SELECT order_no ");
        sql.append("        FROM ( ");
        sql.append("            SELECT order_no, ROWNUM AS rnum ");
        sql.append("            FROM ( ");
        sql.append("                SELECT DISTINCT order_no, order_date ");
        sql.append("                FROM orders ");
        sql.append("                WHERE member_no = ? ");

        if ("recent".equals(yearFilter)
                || yearFilter == null
                || yearFilter.isEmpty()) {

            sql.append(
                    "AND order_date >= ADD_MONTHS(SYSDATE, -6) "
            );

        } else {

            sql.append(
                    "AND TO_CHAR(order_date, 'YYYY') = ? "
            );
        }

        sql.append("                ORDER BY order_date DESC, order_no DESC ");
        sql.append("            ) ");
        sql.append("            WHERE ROWNUM <= ? ");
        sql.append("        ) ");
        sql.append("        WHERE rnum >= ? ");
        sql.append("    ) ");
        sql.append(") ");
        sql.append("ORDER BY order_date DESC, order_no DESC, order_detail_no ASC");

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(
                            sql.toString()
                    )
        ) {

            int paramIdx = 1;

            pstmt.setInt(
                    paramIdx++,
                    memberNo
            );

            pstmt.setInt(
                    paramIdx++,
                    memberNo
            );

            if (!"recent".equals(yearFilter)
                    && yearFilter != null
                    && !yearFilter.isEmpty()) {

                pstmt.setString(
                        paramIdx++,
                        yearFilter
                );
            }

            pstmt.setInt(
                    paramIdx++,
                    endRow
            );

            pstmt.setInt(
                    paramIdx++,
                    startRow
            );

            try (
                ResultSet rs =
                        pstmt.executeQuery()
            ) {

                while (rs.next()) {

                    OrderItemDTO dto =
                            new OrderItemDTO();

                    dto.setOrderNo(
                            rs.getInt("order_no")
                    );

                    dto.setOrderDate(
                            rs.getTimestamp("order_date")
                    );

                    dto.setOrderStatus(
                            rs.getString("order_status")
                    );

                    dto.setTotalPrice(
                            rs.getInt("order_total_price")
                    );

                    dto.setDeliveryFee(
                            rs.getInt("delivery_fee")
                    );

                    dto.setOrderDetailNo(
                            rs.getLong("order_detail_no")
                    );

                    dto.setQuantity(
                            rs.getInt("order_qty")
                    );

                    dto.setItemPrice(
                            rs.getInt("item_price")
                    );

                    dto.setProductNo(
                            rs.getLong("product_no")
                    );

                    dto.setProductName(
                            rs.getString("product_name")
                    );

                    dto.setOption1Type(
                            rs.getString("option1_type")
                    );

                    dto.setOption1Value(
                            rs.getString("option1_value")
                    );

                    dto.setOption2Type(
                            rs.getString("option2_type")
                    );

                    dto.setOption2Value(
                            rs.getString("option2_value")
                    );

                    dto.setImageUrl(
                            rs.getString("image_url")
                    );

                    list.add(dto);
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