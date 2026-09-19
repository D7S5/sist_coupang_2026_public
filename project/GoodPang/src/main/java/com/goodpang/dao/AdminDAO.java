package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.AdminDTO;
import com.goodpang.util.ConnectionProvider;

public class AdminDAO {

    // 관리자 로그인용 아이디 조회
    public AdminDTO findByAdminId(String adminId) {

        AdminDTO dto = null;

        String sql = """
            SELECT ADMIN_NO, ADMIN_ID, ADMIN_PW, ADMIN_NAME, EMAIL, TEL
            FROM ADMIN
            WHERE ADMIN_ID = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, adminId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    dto = new AdminDTO();

                    dto.setAdminNo(rs.getInt("ADMIN_NO"));
                    dto.setAdminId(rs.getString("ADMIN_ID"));
                    dto.setAdminPw(rs.getString("ADMIN_PW"));
                    dto.setAdminName(rs.getString("ADMIN_NAME"));
                    dto.setEmail(rs.getString("EMAIL"));
                    dto.setTel(rs.getString("TEL"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
