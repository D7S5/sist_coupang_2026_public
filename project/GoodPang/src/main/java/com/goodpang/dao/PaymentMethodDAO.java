package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.ConnectionProvider;

public class PaymentMethodDAO {

	/*
	 * 회원 계좌 목록
	 */
	public List<PaymentMethodDTO> getBankAccounts(
			int memberNo) {

		List<PaymentMethodDTO> list =
				new ArrayList<>();

		String sql = """
				SELECT
				    PAYMENT_METHOD_NO,
				    MEMBER_NO,
				    PAYMENT_TYPE,
				    BANK_CODE,

				    CASE BANK_CODE
				        WHEN 'SHINHAN' THEN '신한은행'
				        WHEN 'KB'     THEN 'KB국민은행'
				        WHEN 'WOORI'  THEN '우리은행'
				        WHEN 'NH'     THEN 'NH농협은행'
				        WHEN 'HANA'   THEN '하나은행'
				        WHEN 'KAKAO'  THEN '카카오뱅크'
				        WHEN 'TOSS'   THEN '토스뱅크'
				        ELSE BANK_CODE
				    END AS BANK_NAME,

				    ACCOUNT_LAST4,
				    ACCOUNT_HOLDER,
				    PAYMENT_DEFAULT

				FROM PAYMENT_METHOD

				WHERE MEMBER_NO = ?
				  AND PAYMENT_TYPE = 'BANK'

				ORDER BY
				    CASE
				        WHEN PAYMENT_DEFAULT = 'Y'
				        THEN 0
				        ELSE 1
				    END,
				    CREATED_AT DESC
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql)
				) {

			pstmt.setInt(1, memberNo);

			try (ResultSet rs =
					pstmt.executeQuery()) {

				while (rs.next()) {

					PaymentMethodDTO dto =
							new PaymentMethodDTO();

					dto.setPaymentMethodNo(
							rs.getInt(
									"PAYMENT_METHOD_NO"
									)
							);

					dto.setMemberNo(
							rs.getInt("MEMBER_NO")
							);

					dto.setPaymentType(
							rs.getString("PAYMENT_TYPE")
							);

					dto.setBankCode(
							rs.getString("BANK_CODE")
							);

					dto.setBankName(
							rs.getString("BANK_NAME")
							);

					dto.setAccountLast4(
							rs.getString("ACCOUNT_LAST4")
							);

					dto.setAccountHolder(
							rs.getString("ACCOUNT_HOLDER")
							);

					dto.setPaymentDefault(
							"Y".equals(
									rs.getString(
											"PAYMENT_DEFAULT"
											)
									)
							);

					list.add(dto);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}


	/*
	 * 기존 기본 결제수단 해제
	 */
	public int clearDefault(
			Connection conn,
			int memberNo)
					throws Exception {

		String sql = """
				UPDATE PAYMENT_METHOD
				SET PAYMENT_DEFAULT = 'N'
				WHERE MEMBER_NO = ?
				  AND PAYMENT_TYPE = 'BANK'
				  AND PAYMENT_DEFAULT = 'Y'
				""";

		try (PreparedStatement pstmt =
				conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberNo);

			return pstmt.executeUpdate();
		}
	}


	/*
	 * 계좌 등록
	 */
	public int insertBankAccount(
			Connection conn,
			PaymentMethodDTO dto)
					throws Exception {

		String sql = """
				INSERT INTO PAYMENT_METHOD (
					PAYMENT_METHOD_NO,
					MEMBER_NO,
					PAYMENT_TYPE,
					BANK_CODE,
					ACCOUNT_LAST4,
					ACCOUNT_HOLDER,
					PAYMENT_DEFAULT,
					CREATED_AT
				)
				VALUES (
					SEQ_PAYMENT_METHOD_NO.NEXTVAL,
					?,
					'BANK',
					?,
					?,
					?,
					?,
					SYSDATE
				)
				""";

		try (PreparedStatement pstmt =
				conn.prepareStatement(sql)) {

			pstmt.setInt(
					1,
					dto.getMemberNo()
					);

			pstmt.setString(
					2,
					dto.getBankCode()
					);

			pstmt.setString(
					3,
					dto.getAccountLast4()
					);

			pstmt.setString(
					4,
					dto.getAccountHolder()
					);

			pstmt.setString(
					5,
					dto.isPaymentDefault()
					? "Y"
							: "N"
					);

			return pstmt.executeUpdate();
		}
	}
	
	public int insertBankAccount(PaymentMethodDTO dto) {

	    String sql = """
	        INSERT INTO PAYMENT_METHOD (
	            PAYMENT_METHOD_NO,
	            MEMBER_NO,
	            PAYMENT_TYPE,
	            BANK_CODE,
	            ACCOUNT_LAST4,
	            ACCOUNT_HOLDER,
	            PAYMENT_DEFAULT,
	            CREATED_AT
	        )
	        VALUES (
	            SEQ_PAYMENT_METHOD_NO.NEXTVAL,
	            ?, ?, ?, ?, ?, ?, SYSDATE
	        )
	        """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection()
	    ) {

	        if (dto.isPaymentDefault()) {
	            clearDefault(
	                    conn,
	                    dto.getMemberNo()
	            );
	        }

	        try (
	            PreparedStatement pstmt =
	                    conn.prepareStatement(sql)
	        ) {

	            pstmt.setInt(1, dto.getMemberNo());
	            pstmt.setString(2, dto.getPaymentType());
	            pstmt.setString(3, dto.getBankCode());
	            pstmt.setString(4, dto.getAccountLast4());
	            pstmt.setString(5, dto.getAccountHolder());

	            pstmt.setString(
	                    6,
	                    dto.isPaymentDefault()
	                        ? "Y"
	                        : "N"
	            );

	            return pstmt.executeUpdate();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();

	        throw new RuntimeException(
	                "결제수단 등록 중 오류가 발생했습니다.",
	                e
	        );
	    }
	}

	public int insertCard(
			Connection conn,
			PaymentMethodDTO dto)
					throws Exception {

		String sql = """
				INSERT INTO PAYMENT_METHOD (
					PAYMENT_METHOD_NO,
					MEMBER_NO,
					PAYMENT_TYPE,
					CARD_COMPANY,
					CARD_LAST4,
					PAYMENT_DEFAULT,
					CREATED_AT
				)
				VALUES (
					SEQ_PAYMENT_METHOD_NO.NEXTVAL,
					?,
					'CARD',
					?,
					?,
					?,
					SYSDATE
				)
				""";

		try (PreparedStatement pstmt =
				conn.prepareStatement(sql)) {

			pstmt.setInt(
					1,
					dto.getMemberNo()
					);

			pstmt.setString(
					2,
					dto.getCardCompany()
					);

			pstmt.setString(
					3,
					dto.getCardLast4()
					);

			pstmt.setString(
					4,
					dto.isPaymentDefault()
					? "Y"
							: "N"
					);

			return pstmt.executeUpdate();
		}
	}

	public int clearDefault(
			Connection conn,
			int memberNo,
			String paymentType)
					throws Exception {

		String sql = """
				UPDATE PAYMENT_METHOD
				SET PAYMENT_DEFAULT = 'N'
				WHERE MEMBER_NO = ?
				  AND PAYMENT_TYPE = ?
				  AND PAYMENT_DEFAULT = 'Y'
				""";

		try (PreparedStatement pstmt =
				conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberNo);
			pstmt.setString(2, paymentType);

			return pstmt.executeUpdate();
		}
	}

	public List<PaymentMethodDTO> getCards(int memberNo) {

		List<PaymentMethodDTO> list = new ArrayList<>();

		String sql = """
				SELECT
				PAYMENT_METHOD_NO,
				MEMBER_NO,
				PAYMENT_TYPE,
				CARD_COMPANY,

				CASE CARD_COMPANY
				WHEN 'BC' THEN '비씨카드'
				WHEN 'KB' THEN 'KB국민카드'
				WHEN 'SHINHAN' THEN '신한카드'
				WHEN 'SAMSUNG' THEN '삼성카드'
				WHEN 'HYUNDAI' THEN '현대카드'
				WHEN 'LOTTE' THEN '롯데카드'
				WHEN 'HANA' THEN '하나카드'
				WHEN 'WOORI' THEN '우리카드'
				WHEN 'NH' THEN 'NH농협카드'
				ELSE CARD_COMPANY
				END AS CARD_COMPANY_NAME,

				CARD_LAST4,
				PAYMENT_DEFAULT

				FROM PAYMENT_METHOD

				WHERE MEMBER_NO = ?
				AND PAYMENT_TYPE = 'CARD'

				ORDER BY
				CASE
				WHEN PAYMENT_DEFAULT = 'Y' THEN 0
				ELSE 1
				END,
				CREATED_AT DESC
				""";

		try (
				Connection conn = ConnectionProvider.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)
				) {

			pstmt.setInt(1, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					PaymentMethodDTO dto = new PaymentMethodDTO();

					dto.setPaymentMethodNo(
							rs.getInt("PAYMENT_METHOD_NO")
							);

					dto.setMemberNo(
							rs.getInt("MEMBER_NO")
							);

					dto.setPaymentType(
							rs.getString("PAYMENT_TYPE")
							);

					// 비씨카드, 신한카드 등 한글명 저장
					dto.setCardCompany(
							rs.getString("CARD_COMPANY_NAME")
							);

					dto.setCardLast4(
							rs.getString("CARD_LAST4")
							);

					dto.setPaymentDefault(
							"Y".equals(
									rs.getString("PAYMENT_DEFAULT")
									)
							);

					list.add(dto);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public List<PaymentMethodDTO> getPaymentMethods(int memberNo) {

	    List<PaymentMethodDTO> list =
	            new ArrayList<>();

	    String sql = """
	        SELECT
	            PAYMENT_METHOD_NO,
	            MEMBER_NO,
	            PAYMENT_TYPE,
	            BANK_CODE,

	            CASE BANK_CODE
	                WHEN 'SHINHAN' THEN '신한은행'
	                WHEN 'KB' THEN 'KB국민은행'
	                WHEN 'WOORI' THEN '우리은행'
	                WHEN 'NH' THEN 'NH농협은행'
	                WHEN 'HANA' THEN '하나은행'
	                WHEN 'KAKAO' THEN '카카오뱅크'
	                WHEN 'TOSS' THEN '토스뱅크'
	                ELSE BANK_CODE
	            END AS BANK_NAME,

	            ACCOUNT_LAST4,
	            ACCOUNT_HOLDER,
	            CARD_COMPANY,
	            CARD_LAST4,
	            PAYMENT_DEFAULT

	        FROM PAYMENT_METHOD

	        WHERE MEMBER_NO = ?

	        ORDER BY
	            CASE
	                WHEN PAYMENT_DEFAULT = 'Y'
	                THEN 0
	                ELSE 1
	            END,
	            PAYMENT_METHOD_NO DESC
	        """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();
	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, memberNo);
	        try (ResultSet rs =
	                pstmt.executeQuery()) {

	            while (rs.next()) {

	                PaymentMethodDTO dto =
	                        new PaymentMethodDTO();

	                dto.setPaymentMethodNo(
	                        rs.getInt("PAYMENT_METHOD_NO")
	                );

	                dto.setMemberNo(
	                        rs.getInt("MEMBER_NO")
	                );

	                dto.setPaymentType(
	                        rs.getString("PAYMENT_TYPE")
	                );

	                dto.setBankCode(
	                        rs.getString("BANK_CODE")
	                );

	                dto.setBankName(
	                        rs.getString("BANK_NAME")
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
	                dto.setPaymentDefault(
	                        "Y".equals(
	                                rs.getString(
	                                        "PAYMENT_DEFAULT"
	                                )
	                        )
	                );
	                list.add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "결제수단 조회 중 오류가 발생했습니다.",
	                e
	        );
	    }
	    return list;
	}
	
	public boolean existsPaymentMethod(
	        int memberNo,
	        int paymentMethodNo) {

	    String sql = """
	        SELECT 1
	        FROM PAYMENT_METHOD
	        WHERE MEMBER_NO = ?
	          AND PAYMENT_METHOD_NO = ?
	        """;

	    try (
	        Connection conn =
	            ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	            conn.prepareStatement(sql);
	    ) {

	        pstmt.setInt(
	                1,
	                memberNo
	        );

	        pstmt.setInt(
	                2,
	                paymentMethodNo
	        );

	        try (ResultSet rs =
	                pstmt.executeQuery()) {

	            return rs.next();
	        }

	    } catch (Exception e) {

	        e.printStackTrace();

	        throw new RuntimeException(
	                "결제수단 확인 중 오류가 발생했습니다.",
	                e
	        );
	    }
	}
	
	public int insertPaymentMethod(PaymentMethodDTO dto) {

	    String sql = """
	        INSERT INTO PAYMENT_METHOD (
	            PAYMENT_METHOD_NO,
	            MEMBER_NO,
	            PAYMENT_TYPE,
	            BANK_CODE,
	            ACCOUNT_LAST4,
	            ACCOUNT_HOLDER,
	            CARD_COMPANY,
	            CARD_LAST4,
	            PAYMENT_DEFAULT
	        )
	        VALUES (
	            SEQ_PAYMENT_METHOD_NO.NEXTVAL,
	            ?, ?, ?, ?, ?, ?, ?, ?
	        )
	        """;

	    try (Connection conn = ConnectionProvider.getConnection()) {

	        if (dto.isPaymentDefault()) {
	            clearDefault(conn, dto.getMemberNo());
	        }

	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setInt(1, dto.getMemberNo());
	            pstmt.setString(2, dto.getPaymentType());
	            pstmt.setString(3, dto.getBankCode());
	            pstmt.setString(4, dto.getAccountLast4());
	            pstmt.setString(5, dto.getAccountHolder());
	            pstmt.setString(6, dto.getCardCompany());
	            pstmt.setString(7, dto.getCardLast4());
	            pstmt.setString(8, dto.isPaymentDefault() ? "Y" : "N");

	            return pstmt.executeUpdate();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "결제수단 등록 중 오류가 발생했습니다.",
	                e
	        );
	    }
	}
	
	public PaymentMethodDTO findPaymentMethod(
	        int memberNo,
	        int paymentMethodNo) {

	    String sql = """
	            SELECT PAYMENT_METHOD_NO,
	                   MEMBER_NO,
	                   PAYMENT_TYPE,
	                   PAYMENT_DEFAULT
	            FROM PAYMENT_METHOD
	            WHERE PAYMENT_METHOD_NO = ?
	              AND MEMBER_NO = ?
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, paymentMethodNo);
	        pstmt.setInt(2, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                PaymentMethodDTO dto = new PaymentMethodDTO();

	                dto.setPaymentMethodNo(
	                        rs.getInt("PAYMENT_METHOD_NO")
	                );
	                dto.setMemberNo(
	                        rs.getInt("MEMBER_NO")
	                );
	                dto.setPaymentType(
	                        rs.getString("PAYMENT_TYPE")
	                );
	                dto.setPaymentDefault(
	                        "Y".equals(rs.getString("PAYMENT_DEFAULT"))
	                );

	                return dto;
	            }
	        }

	    } catch (Exception e) {
	        throw new RuntimeException(
	                "결제수단 조회 중 오류가 발생했습니다.",
	                e
	        );
	    }

	    return null;
	}
	
	public int setDefault(
	        Connection conn,
	        int memberNo,
	        int paymentMethodNo) throws Exception {

	    String sql = """
	            UPDATE PAYMENT_METHOD
	               SET PAYMENT_DEFAULT = 'Y'
	             WHERE PAYMENT_METHOD_NO = ?
	               AND MEMBER_NO = ?
	            """;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, paymentMethodNo);
	        pstmt.setInt(2, memberNo);

	        return pstmt.executeUpdate();
	    }
	}

}