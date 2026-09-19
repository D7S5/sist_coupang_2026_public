package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import com.goodpang.util.ConnectionProvider;

/*
 * 상품 상세페이지 조회 로그(PRODUCT_VIEW_LOG) 적재.
 * 판매자센터 대시보드의 "오늘 방문자수" / "오늘 상품 노출수" 집계 기준이 되는 원본 로그.
 */
public class ProductViewLogDAO {

    public void logView(int productNo, Integer memberNo, String sessionId) {

        String sql = """
            INSERT INTO PRODUCT_VIEW_LOG (
                VIEW_LOG_NO, PRODUCT_NO, MEMBER_NO, SESSION_ID, VIEW_DATE
            ) VALUES (
                SEQ_PRODUCT_VIEW_LOG.NEXTVAL, ?, ?, ?, SYSDATE
            )
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, productNo);

            if (memberNo != null) {
                pstmt.setInt(2, memberNo);
            } else {
                pstmt.setNull(2, Types.NUMERIC);
            }

            pstmt.setString(3, sessionId);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
