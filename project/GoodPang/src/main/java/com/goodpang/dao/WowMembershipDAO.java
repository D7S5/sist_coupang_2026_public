package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.goodpang.dto.WowMembershipDTO;
import com.goodpang.util.ConnectionProvider;

public class WowMembershipDAO {

    /*
     * 현재 와우 회원인지 확인
     */
    public boolean isWowMember(int memberNo) {

        String sql = """
                SELECT COUNT(*)
                FROM WOW_MEMBERSHIP
                WHERE MEMBER_NO = ?
                  AND STATUS IN ('ACTIVE', 'CANCEL_PENDING')
                """;

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "와우 회원 확인 중 오류가 발생했습니다.",
                    e
            );
        }

        return false;
    }
    
    public boolean isWithdrawalBlocked(int memberNo) {
        String sql = """
            SELECT COUNT(*)
            FROM WOW_MEMBERSHIP
            WHERE MEMBER_NO = ?
              AND STATUS = 'ACTIVE'
            """;

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Date nextPaymentDate(int memberNo) {

        String sql = """
                SELECT NEXT_PAYMENT_DATE
                FROM WOW_MEMBERSHIP
                WHERE MEMBER_NO = ?
                """;

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getDate("NEXT_PAYMENT_DATE");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "와우 회원 확인 중 오류가 발생했습니다.",
                    e
            );
        }
		return null;

    }
    
    public boolean isCancelPending(int memberNo) {
        String sql = """
            SELECT COUNT(*)
            FROM WOW_MEMBERSHIP
            WHERE MEMBER_NO = ?
              AND STATUS = 'CANCEL_PENDING'
            """;

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
     * 회원의 와우 멤버십 정보 조회
     */
    public WowMembershipDTO findByMemberNo(
            int memberNo) {

        String sql = """
                SELECT
                    WOW_MEMBERSHIP_NO,
                    MEMBER_NO,
                    STATUS,
                    START_DATE,
                    NEXT_PAYMENT_DATE,
                    CANCEL_DATE,
                    END_DATE,
                    AUTO_PAYMENT_YN,
                    PAYMENT_METHOD_NO
                FROM WOW_MEMBERSHIP
                WHERE MEMBER_NO = ?
                """;

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                WowMembershipDTO dto =
                        new WowMembershipDTO();

                dto.setWowMembershipNo(
                        rs.getInt("WOW_MEMBERSHIP_NO")
                );

                dto.setMemberNo(
                        rs.getInt("MEMBER_NO")
                );

                dto.setStatus(
                        rs.getString("STATUS")
                );

                dto.setStartDate(
                        rs.getDate("START_DATE")
                );

                dto.setNextPaymentDate(
                        rs.getDate("NEXT_PAYMENT_DATE")
                );

                dto.setCancelDate(
                        rs.getDate("CANCEL_DATE")
                );

                dto.setEndDate(
                        rs.getDate("END_DATE")
                );

                dto.setAutoPaymentYn(
                        rs.getString("AUTO_PAYMENT_YN")
                );

                int paymentMethodNo =
                        rs.getInt("PAYMENT_METHOD_NO");

                if (!rs.wasNull()) {
                    dto.setPaymentMethodNo(
                            paymentMethodNo
                    );
                }

                return dto;
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "와우 멤버십 조회 중 오류가 발생했습니다.",
                    e
            );
        }
    }
    
    
    
    public int insertMembership(
            int memberNo,
            int paymentMethodNo,
            Connection conn)
            throws Exception {

        String sql = """
                INSERT INTO WOW_MEMBERSHIP (
                    WOW_MEMBERSHIP_NO,
                    MEMBER_NO,
                    STATUS,
                    START_DATE,
                    NEXT_PAYMENT_DATE,
                    END_DATE,
                    AUTO_PAYMENT_YN,
                    PAYMENT_METHOD_NO
                )
                VALUES (
                    SEQ_WOW_MEMBERSHIP_NO.NEXTVAL,
                    ?,
                    'ACTIVE',
                    SYSDATE,
                    ADD_MONTHS(SYSDATE, 1),
                    ADD_MONTHS(SYSDATE, 1),
                    'Y',
                    ?
                )
                """;

        try (
            PreparedStatement pstmt =
                    conn.prepareStatement(
                            sql,
                            new String[]{
                                "WOW_MEMBERSHIP_NO"
                            }
                    )
        ) {

            pstmt.setInt(1, memberNo);
            pstmt.setInt(
                    2,
                    paymentMethodNo
            );

            pstmt.executeUpdate();

            try (
                ResultSet rs =
                        pstmt.getGeneratedKeys()
            ) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new Exception(
                "와우 멤버십 번호 생성 실패"
        );
    }
    
    public int insertMembership(
            Connection conn,
            int wowMembershipNo,
            int memberNo,
            int paymentMethodNo) throws Exception {

        String sql = """
                INSERT INTO WOW_MEMBERSHIP (
                    WOW_MEMBERSHIP_NO,
                    MEMBER_NO,
                    STATUS,
                    START_DATE,
                    NEXT_PAYMENT_DATE,
                    END_DATE,
                    AUTO_PAYMENT_YN,
                    PAYMENT_METHOD_NO
                )
                VALUES (
                    ?,
                    ?,
                    'ACTIVE',
                    SYSDATE,
                    ADD_MONTHS(SYSDATE, 1),
                    ADD_MONTHS(SYSDATE, 1),
                    'Y',
                    ?
                )
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, wowMembershipNo);
            pstmt.setInt(2, memberNo);
            pstmt.setInt(3, paymentMethodNo);

            return pstmt.executeUpdate();
        }
    }
    
    
    public int getNextMembershipNo(
            Connection conn)
            throws Exception {

        String sql = """
                SELECT
                    SEQ_WOW_MEMBERSHIP_NO.NEXTVAL
                FROM DUAL
                """;

        try (
            PreparedStatement pstmt =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pstmt.executeQuery()
        ) {

            rs.next();

            return rs.getInt(1);
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
    
    public int reactivateMembership(
            Connection conn,
            int memberNo,
            int paymentMethodNo)
            throws Exception {

        String sql = """
                UPDATE WOW_MEMBERSHIP
                   SET STATUS = 'ACTIVE',
                       START_DATE = SYSDATE,
                       NEXT_PAYMENT_DATE = ADD_MONTHS(SYSDATE, 1),
                       END_DATE = ADD_MONTHS(SYSDATE, 1),
                       CANCEL_DATE = NULL,
                       AUTO_PAYMENT_YN = 'Y',
                       PAYMENT_METHOD_NO = ?
                 WHERE MEMBER_NO = ?
                   AND STATUS IN ('EXPIRED', 'SUSPENDED')
                """;

        try (
            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(
                    1,
                    paymentMethodNo
            );

            pstmt.setInt(
                    2,
                    memberNo
            );

            return pstmt.executeUpdate();
        }
    }
    
    public WowMembershipDTO getMembershipDetail(int memberNo) {

        String sql = """
            SELECT
                WM.WOW_MEMBERSHIP_NO,
                WM.MEMBER_NO,
                WM.STATUS,
                WM.START_DATE,
                WM.NEXT_PAYMENT_DATE,
                WM.CANCEL_DATE,
                WM.END_DATE,
                WM.AUTO_PAYMENT_YN,
                WM.PAYMENT_METHOD_NO,
                PM.PAYMENT_TYPE,
                PM.BANK_CODE,
                PM.ACCOUNT_LAST4,
                PM.ACCOUNT_HOLDER,
                PM.CARD_COMPANY,
                PM.CARD_LAST4
            FROM WOW_MEMBERSHIP WM
            LEFT JOIN PAYMENT_METHOD PM
                   ON WM.PAYMENT_METHOD_NO = PM.PAYMENT_METHOD_NO
            WHERE WM.MEMBER_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                WowMembershipDTO dto = new WowMembershipDTO();

                dto.setWowMembershipNo(
                        rs.getInt("WOW_MEMBERSHIP_NO")
                );

                dto.setMemberNo(
                        rs.getInt("MEMBER_NO")
                );

                dto.setStatus(
                        rs.getString("STATUS")
                );

                dto.setStartDate(
                        rs.getDate("START_DATE")
                );

                dto.setNextPaymentDate(
                        rs.getDate("NEXT_PAYMENT_DATE")
                );

                dto.setCancelDate(
                        rs.getDate("CANCEL_DATE")
                );

                dto.setEndDate(
                        rs.getDate("END_DATE")
                );

                dto.setAutoPaymentYn(
                        rs.getString("AUTO_PAYMENT_YN")
                );

                int paymentMethodNo =
                        rs.getInt("PAYMENT_METHOD_NO");

                if (!rs.wasNull()) {
                    dto.setPaymentMethodNo(paymentMethodNo);
                }

                dto.setPaymentType(
                        rs.getString("PAYMENT_TYPE")
                );

                dto.setBankCode(
                        rs.getString("BANK_CODE")
                );

                dto.setAccountLast4(
                        rs.getString("ACCOUNT_LAST4")
                );

                dto.setAccountHolder(
                        rs.getString("ACCOUNT_HOLDER")
                );

                dto.setCardCompany(
                        rs.getString("CARD_COMPANY")
                );

                dto.setCardLast4(
                        rs.getString("CARD_LAST4")
                );

                return dto;
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "와우 멤버십 상세 조회 중 오류",
                    e
            );
        }
    }
}