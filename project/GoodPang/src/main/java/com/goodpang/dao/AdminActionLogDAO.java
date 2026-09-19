package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AdminActionLogDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 관리자가 운영툴에서 수행한 액션(승인/반려/정지/삭제 등)을 ADMIN_ACTION_LOG에 남긴다.
 * target_no는 target_type(SELLER/PRODUCT/DELIVERY/NOTICE 등)에 따라 다른 테이블의 PK를 가리키는
 * 폴리모픽 연관이라 FK가 없다 - docs/admin_action_log_table_create.sql 참고.
 */
public class AdminActionLogDAO {

    public void log(int adminNo, String actionType, String targetType, int targetNo, String reason) {

        String sql = """
            INSERT INTO ADMIN_ACTION_LOG (
                ACTION_LOG_NO, ADMIN_NO, ACTION_TYPE, TARGET_TYPE, TARGET_NO, REASON, ACTION_DATE
            ) VALUES (
                SEQ_ADMIN_ACTION_LOG.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE
            )
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, adminNo);
            pstmt.setString(2, actionType);
            pstmt.setString(3, targetType);
            pstmt.setInt(4, targetNo);
            pstmt.setString(5, reason);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 목록 - 최신순. page는 1부터
    public List<AdminActionLogDTO> findAll(int page, int pageSize) {

        List<AdminActionLogDTO> list = new ArrayList<>();

        String sql = """
            SELECT L.ACTION_LOG_NO, L.ADMIN_NO, A.ADMIN_NAME,
                   L.ACTION_TYPE, L.TARGET_TYPE, L.TARGET_NO, L.REASON, L.ACTION_DATE
            FROM ADMIN_ACTION_LOG L
                JOIN ADMIN A ON L.ADMIN_NO = A.ADMIN_NO
            ORDER BY L.ACTION_LOG_NO DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    AdminActionLogDTO dto = new AdminActionLogDTO();
                    dto.setActionLogNo(rs.getInt("ACTION_LOG_NO"));
                    dto.setAdminNo(rs.getInt("ADMIN_NO"));
                    dto.setAdminName(rs.getString("ADMIN_NAME"));
                    dto.setActionType(rs.getString("ACTION_TYPE"));
                    dto.setTargetType(rs.getString("TARGET_TYPE"));
                    dto.setTargetNo(rs.getInt("TARGET_NO"));
                    dto.setReason(rs.getString("REASON"));
                    dto.setActionDate(rs.getTimestamp("ACTION_DATE"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 페이지네이션용 총 개수
    public int countAll() {

        String sql = "SELECT COUNT(*) FROM ADMIN_ACTION_LOG";

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
