package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.MemberDTO;
import com.goodpang.util.ConnectionProvider;

public class MemberDAO {

    // 회원가입
	public int insertMember(MemberDTO dto) {

	    String sql = """
	        INSERT INTO MEMBER (
	            MEMBER_NO,
	            MEMBER_ID,
	            MEMBER_PW,
	            MEMBER_NAME,
	            PHONE,
	            EMAIL,
	            RANK
	        )
	        VALUES (
	            SEQ_MEMBER_NO.NEXTVAL,
	            ?,
	            ?,
	            ?,
	            ?,
	            ?,
	            ?
	        )
	        """;

	    int rowCount = 0;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, dto.getMemberId());
	        pstmt.setString(2, dto.getMemberPw());
	        pstmt.setString(3, dto.getMemberName());
	        pstmt.setString(4, dto.getPhone());
	        pstmt.setString(5, dto.getEmail());
	        pstmt.setString(6, dto.getRank());

	        rowCount = pstmt.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return rowCount;
	}
	
	public boolean existsByEmail(String email) {

	    String sql = """
	        SELECT COUNT(*)
	        FROM MEMBER
	        WHERE EMAIL = ?
	        """;

	    try (
	        Connection conn =
	            ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	            conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, email);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public MemberDTO login(String memberId) {

	    String sql = """
	        SELECT
	            MEMBER_NO,
	            MEMBER_ID,
	            MEMBER_PW,
	            MEMBER_NAME,
	            PHONE,
	            EMAIL,
	            RANK
	        FROM MEMBER
	        WHERE MEMBER_ID = ?
	        """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, memberId);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                MemberDTO dto = new MemberDTO();

	                dto.setMemberNo(
	                    rs.getInt("MEMBER_NO")
	                );

	                dto.setMemberId(
	                    rs.getString("MEMBER_ID")
	                );

	                dto.setMemberPw(
	                    rs.getString("MEMBER_PW")
	                );

	                dto.setMemberName(
	                    rs.getString("MEMBER_NAME")
	                );

	                dto.setPhone(
	                    rs.getString("PHONE")
	                );

	                dto.setEmail(
	                    rs.getString("EMAIL")
	                );

	                dto.setRank(
	                    rs.getString("RANK")
	                );

	                return dto;
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	
	public boolean existsByMemberId(String memberId) {

	    String sql = """
	        SELECT COUNT(*)
	        FROM MEMBER
	        WHERE MEMBER_ID = ?
	        """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, memberId);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public MemberDTO findByEmail(String email) {

	    MemberDTO dto = null;

	    String sql = """
	            SELECT
	                MEMBER_NO,
	                MEMBER_ID,
	                MEMBER_PW,
	                MEMBER_NAME,
	                PHONE,
	                EMAIL,
	                RANK,
	                STATUS
	            FROM MEMBER
	            WHERE EMAIL = ?
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {

	        pstmt.setString(1, email);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                dto = new MemberDTO();

	                dto.setMemberNo(
	                    rs.getInt("MEMBER_NO")
	                );

	                dto.setMemberId(
	                    rs.getString("MEMBER_ID")
	                );

	                dto.setMemberPw(
	                    rs.getString("MEMBER_PW")
	                );

	                dto.setMemberName(
	                    rs.getString("MEMBER_NAME")
	                );

	                dto.setPhone(
	                    rs.getString("PHONE")
	                );

	                dto.setEmail(
	                    rs.getString("EMAIL")
	                );

	                dto.setRank(
	                    rs.getString("RANK")
	                );
	                
	                dto.setStatus(
	                	rs.getInt("STATUS")
	                	);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return dto;
	}
	
	
	public MemberDTO getMember(int memberNo) throws Exception {

        MemberDTO member = null;

        String sql = """
                SELECT
                    MEMBER_NO,
                    EMAIL,
                    MEMBER_NAME,
                    MEMBER_PW,
                    PHONE
                FROM MEMBER
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

                    member = new MemberDTO();

                    member.setMemberNo(
                            rs.getInt("MEMBER_NO")
                    );

                    member.setEmail(
                            rs.getString("EMAIL")
                    );
                    
                    member.setMemberPw(
                            rs.getString("MEMBER_PW")
                    );

                    member.setMemberName(
                            rs.getString("MEMBER_NAME")
                    );

                    member.setPhone(
                            rs.getString("PHONE")
                    );
                }
            }
        }

        return member;
    }
	
	public int updateEmail(
	        int memberNo,
	        String newEmail) throws Exception {

	    String sql = """
	            UPDATE MEMBER
	            SET EMAIL = ?
	            WHERE MEMBER_NO = ?
	            """;

	    int result = 0;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setString(
	                1,
	                newEmail
	        );

	        pstmt.setInt(
	                2,
	                memberNo
	        );

	        result =
	                pstmt.executeUpdate();
	    }

	    return result;
	}
	
	public int updatePhone(
	        int memberNo,
	        String newPhone) throws Exception {

	    String sql = """
	            UPDATE MEMBER
	            SET PHONE = ?
	            WHERE MEMBER_NO = ?
	            """;

	    int result = 0;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setString(
	                1,
	                newPhone
	        );

	        pstmt.setInt(
	                2,
	                memberNo
	        );

	        result =
	                pstmt.executeUpdate();
	    }

	    return result;
	}
	
	public int updatePassword(
	        int memberNo,
	        String newPassword) throws Exception {

	    String sql = """
	            UPDATE MEMBER
	            SET MEMBER_PW = ?
	            WHERE MEMBER_NO = ?
	            """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setString(
	                1,
	                newPassword
	        );

	        pstmt.setInt(
	                2,
	                memberNo
	        );

	        return pstmt.executeUpdate();
	    }
	}
	
	public MemberDTO findByMemberNo(int memberNo) {

	    String sql = """
	            SELECT MEMBER_NO, MEMBER_ID, MEMBER_PW, MEMBER_NAME, PHONE, EMAIL, RANK
	            FROM MEMBER
	            WHERE MEMBER_NO = ?
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {
	                MemberDTO member = new MemberDTO();

	                member.setMemberNo(rs.getInt("MEMBER_NO"));
	                member.setMemberId(rs.getString("MEMBER_ID"));
	                member.setMemberPw(rs.getString("MEMBER_PW"));
	                member.setMemberName(rs.getString("MEMBER_NAME"));
	                member.setPhone(rs.getString("PHONE"));
	                member.setEmail(rs.getString("EMAIL"));
	                member.setRank(rs.getString("RANK"));

	                return member;
	            }
	        }

	    } catch (Exception e) {
	        throw new RuntimeException("회원 조회 중 오류", e);
	    }

	    return null;
	}
	
	public boolean withdrawMember(int memberNo) {

	    String sql = """
	            UPDATE MEMBER
	            SET STATUS = 0
	            WHERE MEMBER_NO = ?
	            AND STATUS = 1
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, memberNo);

	        return pstmt.executeUpdate() > 0;

	    } catch (Exception e) {
	        throw new RuntimeException("회원 탈퇴 처리 중 오류", e);
	    }
	}
}