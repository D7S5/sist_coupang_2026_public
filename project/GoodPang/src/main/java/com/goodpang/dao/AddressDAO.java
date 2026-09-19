package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import com.goodpang.dto.AddressDTO;
import com.goodpang.util.ConnectionProvider;

public class AddressDAO {
	
	public AddressDTO editGetAddress(
	        int addressNo,
	        int memberNo) {

	    AddressDTO dto = null;

	    String sql = """
	            SELECT
	                ADDRESS_NO,
	                MEMBER_NO,
	                RECEIVER_NAME,
	                TEL,
	                ZIPCODE,
	                ADDRESS,
	                DETAIL_ADDRESS,
	                REQUEST_MSG,
	                ADDRESS_DEFAULT
	            FROM DELIVERY_ADDRESS
	            WHERE ADDRESS_NO = ?
	              AND MEMBER_NO = ?
	            """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(1, addressNo);
	        pstmt.setInt(2, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                dto = new AddressDTO();

	                dto.setAddressNo(
	                        rs.getInt("ADDRESS_NO")
	                );

	                dto.setMemberNo(
	                        rs.getInt("MEMBER_NO")
	                );

	                dto.setReceiverName(
	                        rs.getString("RECEIVER_NAME")
	                );

	                dto.setTel(
	                        rs.getString("TEL")
	                );

	                dto.setZipcode(
	                        rs.getString("ZIPCODE")
	                );

	                dto.setAddress(
	                        rs.getString("ADDRESS")
	                );

	                dto.setDetailAddress(
	                        rs.getString("DETAIL_ADDRESS")
	                );

	                dto.setRequestMsg(
	                        rs.getString("REQUEST_MSG")
	                );

	                dto.setAddressDefault(
	                        rs.getString("ADDRESS_DEFAULT")
	                );
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return dto;
	}
	
	public int insertAddress(AddressDTO dto) throws SQLException, NamingException {

	    String resetSql = """
	            UPDATE DELIVERY_ADDRESS
	            SET ADDRESS_DEFAULT = 'N'
	            WHERE MEMBER_NO = ?
	              AND ADDRESS_DEFAULT = 'Y'
	            """;

	    String insertSql = """
	            INSERT INTO DELIVERY_ADDRESS (
	                ADDRESS_NO,
	                MEMBER_NO,
	                RECEIVER_NAME,
	                TEL,
	                ZIPCODE,
	                ADDRESS,
	                DETAIL_ADDRESS,
	                REQUEST_MSG,
	                ADDRESS_DEFAULT
	            )
	            VALUES (
	                SEQ_ADDRESS_NO.NEXTVAL,
	                ?, ?, ?, ?, ?, ?, ?, ?
	            )
	            """;

	    try (Connection conn =
	                 ConnectionProvider.getConnection()) {

	        try {

	            conn.setAutoCommit(false);

	            // 기본배송지로 추가하는 경우
	            if ("Y".equals(dto.getAddressDefault())) {

	                try (PreparedStatement pstmt =
	                             conn.prepareStatement(resetSql)) {

	                    pstmt.setInt(
	                            1,
	                            dto.getMemberNo()
	                    );

	                    pstmt.executeUpdate();
	                }
	            }

	            int result;

	            try (PreparedStatement pstmt =
	                         conn.prepareStatement(insertSql)) {

	                pstmt.setInt(1, dto.getMemberNo());
	                pstmt.setString(2, dto.getReceiverName());
	                pstmt.setString(3, dto.getTel());
	                pstmt.setString(4, dto.getZipcode());
	                pstmt.setString(5, dto.getAddress());
	                pstmt.setString(6, dto.getDetailAddress());
	                pstmt.setString(7, dto.getRequestMsg());
	                pstmt.setString(8, dto.getAddressDefault());

	                result =
	                        pstmt.executeUpdate();
	            }

	            conn.commit();

	            return result;

	        } catch (SQLException e) {

	            conn.rollback();

	            throw e;

	        } finally {

	            conn.setAutoCommit(true);
	        }
	    }
	}
	
	public int updateAddress(AddressDTO dto)
	        throws SQLException, NamingException {

	    String resetSql = """
	        UPDATE DELIVERY_ADDRESS
	        SET ADDRESS_DEFAULT = 'N'
	        WHERE MEMBER_NO = ?
	          AND ADDRESS_DEFAULT = 'Y'
	          AND ADDRESS_NO <> ?
	        """;

	    String updateSql = """
	        UPDATE DELIVERY_ADDRESS
	        SET
	            RECEIVER_NAME = ?,
	            TEL = ?,
	            ZIPCODE = ?,
	            ADDRESS = ?,
	            DETAIL_ADDRESS = ?,
	            REQUEST_MSG = ?,
	            ADDRESS_DEFAULT = ?
	        WHERE ADDRESS_NO = ?
	          AND MEMBER_NO = ?
	        """;

	    try (Connection conn = ConnectionProvider.getConnection()) {

	        try {
	            conn.setAutoCommit(false);

	            if ("Y".equals(dto.getAddressDefault())) {
	                try (PreparedStatement pstmt =
	                        conn.prepareStatement(resetSql)) {

	                    pstmt.setInt(1, dto.getMemberNo());
	                    pstmt.setInt(2, dto.getAddressNo());
	                    pstmt.executeUpdate();
	                }
	            }

	            int result;

	            try (PreparedStatement pstmt =
	                    conn.prepareStatement(updateSql)) {

	                pstmt.setString(1, dto.getReceiverName());
	                pstmt.setString(2, dto.getTel());
	                pstmt.setString(3, dto.getZipcode());
	                pstmt.setString(4, dto.getAddress());
	                pstmt.setString(5, dto.getDetailAddress());
	                pstmt.setString(6, dto.getRequestMsg());
	                pstmt.setString(7, dto.getAddressDefault());
	                pstmt.setInt(8, dto.getAddressNo());
	                pstmt.setInt(9, dto.getMemberNo());

	                result = pstmt.executeUpdate();
	            }

	            conn.commit();
	            return result;

	        } catch (SQLException e) {
	            conn.rollback();
	            throw e;

	        } finally {
	            conn.setAutoCommit(true);
	        }
	    }
	}
}

