package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.SellerDTO;
import com.goodpang.util.ConnectionProvider;

public class SellerDAO {

    // 판매자 목록 조회 (관리자용) - 최근 가입순
    public List<SellerDTO> findAll() {

        List<SellerDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                SELLER_NO, EMAIL, SELLER_PW, MANAGER_NAME, PHONE,
                BUSINESS_NO, BUSINESS_TYPE, CEO_NAME, STORE_NAME,
                ZIPCODE, BUSINESS_ADDRESS, BUSINESS_DETAIL_ADDRESS,
                MAIL_ORDER_NO, CATEGORY_NO,
                BANK_NAME, ACCOUNT_NO, ACCOUNT_HOLDER,
                BUSINESS_CERT_URL, MAIL_ORDER_CERT_URL,
                APPROVAL_STATUS, REJECT_REASON,
                CREATED_DATE, UPDATED_DATE
            FROM SELLER
            ORDER BY SELLER_NO DESC
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                SellerDTO dto = mapRow(rs);

                dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
                dto.setUpdatedDate(rs.getTimestamp("UPDATED_DATE"));

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 판매자 상세 조회 (관리자용)
    public SellerDTO findBySellerNo(int sellerNo) {

        SellerDTO dto = null;

        String sql = """
            SELECT
                SELLER_NO, EMAIL, SELLER_PW, MANAGER_NAME, PHONE,
                BUSINESS_NO, BUSINESS_TYPE, CEO_NAME, STORE_NAME,
                ZIPCODE, BUSINESS_ADDRESS, BUSINESS_DETAIL_ADDRESS,
                MAIL_ORDER_NO, CATEGORY_NO,
                BANK_NAME, ACCOUNT_NO, ACCOUNT_HOLDER,
                BUSINESS_CERT_URL, MAIL_ORDER_CERT_URL,
                APPROVAL_STATUS, REJECT_REASON,
                CREATED_DATE, UPDATED_DATE
            FROM SELLER
            WHERE SELLER_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    dto = mapRow(rs);

                    dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
                    dto.setUpdatedDate(rs.getTimestamp("UPDATED_DATE"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    // 판매자 회원가입 (vendor-signup.jsp 1단계 항목만 채워서 INSERT, 승인상태는 '입점 대기'로 시작)
    public int insertSeller(SellerDTO dto) {

        String sql = """
            INSERT INTO SELLER (
                SELLER_NO,
                EMAIL,
                SELLER_PW,
                MANAGER_NAME,
                PHONE,
                BUSINESS_NO,
                BUSINESS_TYPE,
                CEO_NAME,
                STORE_NAME,
                APPROVAL_STATUS,
                CREATED_DATE,
                UPDATED_DATE
            )
            VALUES (
                SEQ_SELLER.NEXTVAL,
                ?, ?, ?, ?,
                ?, ?, ?, ?,
                '입점 대기',
                SYSDATE, SYSDATE
            )
            """;

        int rowCount = 0;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, dto.getEmail());
            pstmt.setString(2, dto.getSellerPw());
            pstmt.setString(3, dto.getManagerName());
            pstmt.setString(4, dto.getPhone());
            pstmt.setString(5, dto.getBusinessNo());
            pstmt.setString(6, dto.getBusinessType());
            pstmt.setString(7, dto.getCeoName());
            pstmt.setString(8, dto.getStoreName());

            rowCount = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowCount;
    }

    public boolean existsByEmail(String email) {

        String sql = """
            SELECT COUNT(*)
            FROM SELLER
            WHERE EMAIL = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
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

    public boolean existsByBusinessNo(String businessNo) {

        String sql = """
            SELECT COUNT(*)
            FROM SELLER
            WHERE BUSINESS_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, businessNo);

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

    public SellerDTO findByEmail(String email) {

        SellerDTO dto = null;

        String sql = """
            SELECT
                SELLER_NO, EMAIL, SELLER_PW, MANAGER_NAME, PHONE,
                BUSINESS_NO, BUSINESS_TYPE, CEO_NAME, STORE_NAME,
                ZIPCODE, BUSINESS_ADDRESS, BUSINESS_DETAIL_ADDRESS,
                MAIL_ORDER_NO, CATEGORY_NO,
                BANK_NAME, ACCOUNT_NO, ACCOUNT_HOLDER,
                BUSINESS_CERT_URL, MAIL_ORDER_CERT_URL,
                APPROVAL_STATUS, REJECT_REASON
            FROM SELLER
            WHERE EMAIL = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto = mapRow(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    // 사업자 추가정보 입력/수정 (사업장주소·통신판매업신고번호·대표카테고리·정산계좌·서류첨부)
    // '입점 대기'/'반려' 상태에서 제출하면 (신규 제출·재제출) 심사 대상이므로 '심사 중'으로 바꾸지만,
    // 이미 '승인'된 판매자가 계좌번호 등을 단순 수정하는 경우까지 재심사로 되돌리면 안 되므로
    // 그 외 상태는 건드리지 않는다.
    public int updateBusinessInfo(SellerDTO dto) {

        String sql = """
            UPDATE SELLER
            SET
                ZIPCODE = ?,
                BUSINESS_ADDRESS = ?,
                BUSINESS_DETAIL_ADDRESS = ?,
                MAIL_ORDER_NO = ?,
                CATEGORY_NO = ?,
                BANK_NAME = ?,
                ACCOUNT_NO = ?,
                ACCOUNT_HOLDER = ?,
                BUSINESS_CERT_URL = ?,
                MAIL_ORDER_CERT_URL = ?,
                APPROVAL_STATUS = CASE WHEN APPROVAL_STATUS IN ('입점 대기', '반려') THEN '심사 중' ELSE APPROVAL_STATUS END,
                UPDATED_DATE = SYSDATE
            WHERE SELLER_NO = ?
            """;

        int rowCount = 0;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, dto.getZipcode());
            pstmt.setString(2, dto.getBusinessAddress());
            pstmt.setString(3, dto.getBusinessDetailAddress());
            pstmt.setString(4, dto.getMailOrderNo());

            if (dto.getCategoryNo() != null) {
                pstmt.setInt(5, dto.getCategoryNo());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            pstmt.setString(6, dto.getBankName());
            pstmt.setString(7, dto.getAccountNo());
            pstmt.setString(8, dto.getAccountHolder());
            pstmt.setString(9, dto.getBusinessCertUrl());
            pstmt.setString(10, dto.getMailOrderCertUrl());
            pstmt.setInt(11, dto.getSellerNo());

            rowCount = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowCount;
    }

    // 입점심사 승인/반려 처리 (관리자용). 승인 시에는 기존 반려사유를 지운다.
    public int updateApprovalStatus(int sellerNo, String approvalStatus, String rejectReason) {

        String sql = """
            UPDATE SELLER
            SET APPROVAL_STATUS = ?,
                REJECT_REASON = ?,
                UPDATED_DATE = SYSDATE
            WHERE SELLER_NO = ?
            """;

        int rowCount = 0;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, approvalStatus);
            pstmt.setString(2, rejectReason);
            pstmt.setInt(3, sellerNo);

            rowCount = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowCount;
    }

    private SellerDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        SellerDTO dto = new SellerDTO();

        dto.setSellerNo(rs.getInt("SELLER_NO"));
        dto.setEmail(rs.getString("EMAIL"));
        dto.setSellerPw(rs.getString("SELLER_PW"));
        dto.setManagerName(rs.getString("MANAGER_NAME"));
        dto.setPhone(rs.getString("PHONE"));
        dto.setBusinessNo(rs.getString("BUSINESS_NO"));
        dto.setBusinessType(rs.getString("BUSINESS_TYPE"));
        dto.setCeoName(rs.getString("CEO_NAME"));
        dto.setStoreName(rs.getString("STORE_NAME"));

        dto.setZipcode(rs.getString("ZIPCODE"));
        dto.setBusinessAddress(rs.getString("BUSINESS_ADDRESS"));
        dto.setBusinessDetailAddress(rs.getString("BUSINESS_DETAIL_ADDRESS"));
        dto.setMailOrderNo(rs.getString("MAIL_ORDER_NO"));

        int categoryNo = rs.getInt("CATEGORY_NO");
        dto.setCategoryNo(rs.wasNull() ? null : categoryNo);

        dto.setBankName(rs.getString("BANK_NAME"));
        dto.setAccountNo(rs.getString("ACCOUNT_NO"));
        dto.setAccountHolder(rs.getString("ACCOUNT_HOLDER"));
        dto.setBusinessCertUrl(rs.getString("BUSINESS_CERT_URL"));
        dto.setMailOrderCertUrl(rs.getString("MAIL_ORDER_CERT_URL"));

        dto.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
        dto.setRejectReason(rs.getString("REJECT_REASON"));

        return dto;
    }
}
