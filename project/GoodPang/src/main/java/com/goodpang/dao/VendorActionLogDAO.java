package com.goodpang.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorActionLogDTO;
import com.goodpang.dto.VendorActionLogSearchDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자가 판매자센터에서 수행한 액션(상품 등록/노출전환/판매중지/옵션수정/배송처리 등)을
 * VENDOR_ACTION_LOG에 남긴다. target_no는 target_type(PRODUCT/PRODUCT_OPTION/ORDERS 등)에
 * 따라 다른 테이블의 PK를 가리키는 폴리모픽 연관이라 FK가 없다 - docs/vendor_action_log_table_create.sql 참고.
 */
public class VendorActionLogDAO {

    public void log(int sellerNo, String actionType, String targetType, int targetNo, String detail) {

        String sql = """
            INSERT INTO VENDOR_ACTION_LOG (
                ACTION_LOG_NO, SELLER_NO, ACTION_TYPE, TARGET_TYPE, TARGET_NO, DETAIL, ACTION_DATE
            ) VALUES (
                SEQ_VENDOR_ACTION_LOG.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE
            )
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);
            pstmt.setString(2, actionType);
            pstmt.setString(3, targetType);
            pstmt.setInt(4, targetNo);
            pstmt.setString(5, detail);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 전체 판매자의 작업 로그 - 최신순. page는 1부터. 조회는 관리자 전용(판매자센터에는 노출 안 함).
     * search가 null이거나 필드가 비어있으면 그 조건은 안 붙는다.
     */
    public List<VendorActionLogDTO> findAll(int page, int pageSize, VendorActionLogSearchDTO search) {

        List<VendorActionLogDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT L.ACTION_LOG_NO, L.SELLER_NO, S.STORE_NAME,
                   L.ACTION_TYPE, L.TARGET_TYPE, L.TARGET_NO, L.DETAIL, L.ACTION_DATE
            FROM VENDOR_ACTION_LOG L
                JOIN SELLER S ON L.SELLER_NO = S.SELLER_NO
            """);

        List<Object> params = new ArrayList<>();
        appendSearchCondition(sql, params, search);

        sql.append(" ORDER BY L.ACTION_LOG_NO DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 페이지네이션용 총 개수 (findAll()과 같은 검색조건 적용)
    public int countAll(VendorActionLogSearchDTO search) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM VENDOR_ACTION_LOG L
                JOIN SELLER S ON L.SELLER_NO = S.SELLER_NO
            """);

        List<Object> params = new ArrayList<>();
        appendSearchCondition(sql, params, search);

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void appendSearchCondition(StringBuilder sql, List<Object> params, VendorActionLogSearchDTO search) {

        if (search == null) {
            return;
        }

        List<String> conditions = new ArrayList<>();

        if (search.getStoreName() != null && !search.getStoreName().isBlank()) {
            conditions.add("S.STORE_NAME LIKE ?");
            params.add("%" + search.getStoreName().trim() + "%");
        }

        if (search.getActionType() != null && !search.getActionType().isBlank()) {
            conditions.add("L.ACTION_TYPE = ?");
            params.add(search.getActionType());
        }

        if (search.getTargetType() != null && !search.getTargetType().isBlank()) {
            conditions.add("L.TARGET_TYPE = ?");
            params.add(search.getTargetType());
        }

        if (search.getStartDate() != null) {
            conditions.add("TRUNC(L.ACTION_DATE) >= ?");
            params.add(Date.valueOf(search.getStartDate()));
        }

        if (search.getEndDate() != null) {
            conditions.add("TRUNC(L.ACTION_DATE) <= ?");
            params.add(Date.valueOf(search.getEndDate()));
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
    }

    private VendorActionLogDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorActionLogDTO dto = new VendorActionLogDTO();
        dto.setActionLogNo(rs.getInt("ACTION_LOG_NO"));
        dto.setSellerNo(rs.getInt("SELLER_NO"));
        dto.setStoreName(rs.getString("STORE_NAME"));
        dto.setActionType(rs.getString("ACTION_TYPE"));
        dto.setTargetType(rs.getString("TARGET_TYPE"));
        dto.setTargetNo(rs.getInt("TARGET_NO"));
        dto.setDetail(rs.getString("DETAIL"));
        dto.setActionDate(rs.getTimestamp("ACTION_DATE"));
        return dto;
    }
}
