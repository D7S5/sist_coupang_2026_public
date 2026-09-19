package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.goodpang.util.ConnectionProvider;


public class WowPaymentDAO {
	
	

    public int insertPayment(
            Connection conn,
            int wowMembershipNo,
            int memberNo,
            int paymentMethodNo,
            int amount,
            String paymentType,
            String paymentStatus)
            throws Exception {

        String sql = """
                INSERT INTO WOW_PAYMENT (
                    WOW_PAYMENT_NO,
                    WOW_MEMBERSHIP_NO,
                    MEMBER_NO,
                    PAYMENT_METHOD_NO,
                    PAYMENT_AMOUNT,
                    PAYMENT_DATE,
                    PAYMENT_STATUS,
                    PAYMENT_TYPE
                )
                VALUES (
                    SEQ_WOW_PAYMENT_NO.NEXTVAL,
                    ?,
                    ?,
                    ?,
                    ?,
                    SYSDATE,
                    ?,
                    ?
                )
                """;

        try (
            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(
                    1,
                    wowMembershipNo
            );

            pstmt.setInt(
                    2,
                    memberNo
            );

            pstmt.setInt(
                    3,
                    paymentMethodNo
            );

            pstmt.setInt(
                    4,
                    amount
            );

            pstmt.setString(
                    5,
                    paymentStatus
            );

            pstmt.setString(
                    6,
                    paymentType
            );

            return pstmt.executeUpdate();
        }
    }
    
    public int cancelMembership(int memberNo) {

        String sql = """
                UPDATE WOW_MEMBERSHIP
                   SET STATUS = 'CANCEL_PENDING',
                       AUTO_PAYMENT_YN = 'N',
                       CANCEL_DATE = SYSDATE
                 WHERE MEMBER_NO = ?
                   AND STATUS = 'ACTIVE'
                """;

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(
                    1,
                    memberNo
            );

            return pstmt.executeUpdate();

        } catch (Exception e) {

            throw new RuntimeException(
                    "와우 멤버십 해지 처리 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}