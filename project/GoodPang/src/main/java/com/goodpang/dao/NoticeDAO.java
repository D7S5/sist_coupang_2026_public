package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.NoticeDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 공지사항(NOTICE) 게시판 조회/등록/수정/삭제.
 * 등록/수정/삭제는 관리자 화면(admin-notice-*.jsp)에서만, 조회는 관리자·판매자 화면 양쪽에서 쓴다.
 */
public class NoticeDAO {

    // 목록 - '공지'가 항상 '안내'보다 위, 그 안에서는 최신순. page는 1부터
    public List<NoticeDTO> findAll(int page, int pageSize) {

        List<NoticeDTO> list = new ArrayList<>();

        String sql = """
            SELECT N.NOTICE_NO, N.TITLE, N.NOTICE_TYPE, N.ADMIN_NO, A.ADMIN_NAME, N.CREATED_DATE, N.UPDATED_DATE
            FROM NOTICE N
                JOIN ADMIN A ON N.ADMIN_NO = A.ADMIN_NO
            ORDER BY CASE WHEN N.NOTICE_TYPE = '공지' THEN 0 ELSE 1 END, N.NOTICE_NO DESC
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
                    list.add(mapListRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 페이지네이션용 총 개수
    public int countAll() {

        String sql = "SELECT COUNT(*) FROM NOTICE";

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

    // 판매자 대시보드 위젯용 - '공지'가 항상 '안내'보다 위, 그 안에서는 최신순으로 limit건만
    public List<NoticeDTO> findRecent(int limit) {

        List<NoticeDTO> list = new ArrayList<>();

        String sql = """
            SELECT N.NOTICE_NO, N.TITLE, N.NOTICE_TYPE, N.ADMIN_NO, A.ADMIN_NAME, N.CREATED_DATE, N.UPDATED_DATE
            FROM NOTICE N
                JOIN ADMIN A ON N.ADMIN_NO = A.ADMIN_NO
            ORDER BY CASE WHEN N.NOTICE_TYPE = '공지' THEN 0 ELSE 1 END, N.NOTICE_NO DESC
            FETCH FIRST ? ROWS ONLY
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapListRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private NoticeDTO mapListRow(ResultSet rs) throws java.sql.SQLException {

        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeNo(rs.getInt("NOTICE_NO"));
        dto.setTitle(rs.getString("TITLE"));
        dto.setNoticeType(rs.getString("NOTICE_TYPE"));
        dto.setAdminNo(rs.getInt("ADMIN_NO"));
        dto.setAdminName(rs.getString("ADMIN_NAME"));
        dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        dto.setUpdatedDate(rs.getTimestamp("UPDATED_DATE"));
        return dto;
    }

    // 상세 - 본문(CONTENT) 포함
    public NoticeDTO findByNoticeNo(int noticeNo) {

        String sql = """
            SELECT N.NOTICE_NO, N.TITLE, N.CONTENT, N.NOTICE_TYPE, N.ADMIN_NO, A.ADMIN_NAME, N.CREATED_DATE, N.UPDATED_DATE
            FROM NOTICE N
                JOIN ADMIN A ON N.ADMIN_NO = A.ADMIN_NO
            WHERE N.NOTICE_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, noticeNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    NoticeDTO dto = mapListRow(rs);
                    dto.setContent(rs.getString("CONTENT"));
                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 등록. 액션 로그(ADMIN_ACTION_LOG)에 target_no로 남길 수 있도록 생성된 notice_no를 돌려준다 - 실패하면 0
    public int insert(String title, String content, String noticeType, int adminNo) {

        String sql = """
            INSERT INTO NOTICE (NOTICE_NO, TITLE, CONTENT, NOTICE_TYPE, ADMIN_NO, CREATED_DATE, UPDATED_DATE)
            VALUES (?, ?, ?, ?, ?, SYSDATE, SYSDATE)
            """;

        try (Connection conn = ConnectionProvider.getConnection()) {

            int noticeNo = nextVal(conn, "SEQ_NOTICE");

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, noticeNo);
                pstmt.setString(2, title);
                pstmt.setString(3, content);
                pstmt.setString(4, noticeType);
                pstmt.setInt(5, adminNo);

                return pstmt.executeUpdate() == 1 ? noticeNo : 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int nextVal(Connection conn, String sequenceName) throws Exception {
        try (
            PreparedStatement pstmt = conn.prepareStatement("SELECT " + sequenceName + ".NEXTVAL FROM DUAL");
            ResultSet rs = pstmt.executeQuery()
        ) {
            rs.next();
            return rs.getInt(1);
        }
    }

    // 수정 - 제목/본문/구분
    public boolean update(int noticeNo, String title, String content, String noticeType) {

        String sql = """
            UPDATE NOTICE
            SET TITLE = ?, CONTENT = ?, NOTICE_TYPE = ?, UPDATED_DATE = SYSDATE
            WHERE NOTICE_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, noticeType);
            pstmt.setInt(4, noticeNo);

            return pstmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 삭제
    public boolean delete(int noticeNo) {

        String sql = "DELETE FROM NOTICE WHERE NOTICE_NO = ?";

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, noticeNo);

            return pstmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
