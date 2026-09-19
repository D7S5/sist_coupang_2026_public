package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.CheckoutDTO;
import com.goodpang.dto.CheckoutItemDTO;
import com.goodpang.util.ConnectionProvider;

public class CheckoutDAO {

	public CheckoutDTO getCheckout(
			int checkoutNo,
			int memberNo) {

		CheckoutDTO dto = null;

		String sql = """
				SELECT
				    CHECKOUT_NO,
				    MEMBER_NO,
				    PRODUCT_AMOUNT,
				    INSTANT_DISCOUNT,
				    COUPON_DISCOUNT,
				    CASH_USED,
				    DELIVERY_FEE,
				    TOTAL_PRICE
				FROM CHECKOUT
				WHERE CHECKOUT_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql);
				) {

			pstmt.setInt(1, checkoutNo);
			pstmt.setInt(2, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					dto = new CheckoutDTO();

					dto.setCheckoutNo(
							rs.getInt("CHECKOUT_NO")
							);

					dto.setMemberNo(
							rs.getInt("MEMBER_NO")
							);

					dto.setProductAmount(
							rs.getInt("PRODUCT_AMOUNT")
							);

					dto.setInstantDiscount(
							rs.getInt("INSTANT_DISCOUNT")
							);

					dto.setCouponDiscount(
							rs.getInt("COUPON_DISCOUNT")
							);

					dto.setCashUsed(
							rs.getInt("CASH_USED")
							);

					dto.setDeliveryFee(
							rs.getInt("DELIVERY_FEE")
							);

					dto.setTotalPrice(
							rs.getInt("TOTAL_PRICE")
							);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dto;
	}

	public List<CheckoutItemDTO> getCheckoutItems(
			int checkoutNo) {

		List<CheckoutItemDTO> list =
				new ArrayList<>();

		String sql = """
				SELECT
				    CHECKOUT_ITEM_NO,
				    CHECKOUT_NO,
				    PRODUCT_NO,
				    OPTION_ID,
				    ORDER_QTY,
				    PRICE
				FROM CHECKOUT_ITEM
				WHERE CHECKOUT_NO = ?
				ORDER BY CHECKOUT_ITEM_NO
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql);
				) {

			pstmt.setInt(1, checkoutNo);

			try (ResultSet rs =
					pstmt.executeQuery()) {

				while (rs.next()) {

					CheckoutItemDTO dto =
							new CheckoutItemDTO();

					dto.setCheckoutItemNo(
							rs.getInt(
									"CHECKOUT_ITEM_NO"
									)
							);

					dto.setCheckoutNo(
							rs.getInt(
									"CHECKOUT_NO"
									)
							);

					dto.setProductNo(
							rs.getInt(
									"PRODUCT_NO"
									)
							);

					int optionId =
							rs.getInt(
									"OPTION_ID"
									);

					if (!rs.wasNull()) {
						dto.setOptionId(
								optionId
								);
					}

					dto.setOrderQty(
							rs.getInt(
									"ORDER_QTY"
									)
							);

					dto.setPrice(
							rs.getInt(
									"PRICE"
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

	
	public List<CheckoutItemDTO> getCheckoutItemsPRODUCT(
			int checkoutNo) {

		List<CheckoutItemDTO> list =
				new ArrayList<>();

		String sql = """
		        SELECT
		            ci.CHECKOUT_ITEM_NO,
		            ci.CHECKOUT_NO,
		            ci.PRODUCT_NO,
		            ci.OPTION_ID,
		            ci.ORDER_QTY,
		            ci.PRICE,
		            p.PRODUCT_NAME,
		            pi.IMAGE_URL,
		            po.OPTION1_TYPE,
		            po.OPTION1_VALUE,
		            po.OPTION2_TYPE,
		            po.OPTION2_VALUE,
		            po.OPTION3_TYPE,
		            po.OPTION3_VALUE
		        FROM CHECKOUT_ITEM ci

		        JOIN PRODUCT p
		          ON ci.PRODUCT_NO = p.PRODUCT_NO

		        LEFT JOIN PRODUCT_OPTION po
		          ON ci.OPTION_ID = po.OPTION_ID

		        LEFT JOIN (
		            SELECT
		                PRODUCT_NO,
		                MIN(IMAGE_URL) AS IMAGE_URL
		            FROM PRODUCT_IMAGE
		            WHERE IMAGE_PURPOSE = '대표'
		            GROUP BY PRODUCT_NO
		        ) pi
		          ON p.PRODUCT_NO = pi.PRODUCT_NO

		        WHERE ci.CHECKOUT_NO = ?

		        ORDER BY ci.CHECKOUT_ITEM_NO
		        """;
		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql);
				) {

			pstmt.setInt(
					1,
					checkoutNo
					);

			try (ResultSet rs =
					pstmt.executeQuery()) {

				while (rs.next()) {

					CheckoutItemDTO dto =
							new CheckoutItemDTO();

					dto.setCheckoutItemNo(
							rs.getInt(
									"CHECKOUT_ITEM_NO"
									)
							);

					dto.setCheckoutNo(
							rs.getInt(
									"CHECKOUT_NO"
									)
							);

					dto.setProductNo(
							rs.getInt(
									"PRODUCT_NO"
									)
							);

					int optionId =
							rs.getInt(
									"OPTION_ID"
									);

					if (!rs.wasNull()) {
						dto.setOptionId(
								optionId
								);
					}

					dto.setOrderQty(
							rs.getInt(
									"ORDER_QTY"
									)
							);

					dto.setPrice(
							rs.getInt(
									"PRICE"
									)
							);

					dto.setProductName(
							rs.getString(
									"PRODUCT_NAME"
									)
							);
					
					String option1Type = rs.getString("OPTION1_TYPE");
					String option1Value = rs.getString("OPTION1_VALUE");
					String option2Type = rs.getString("OPTION2_TYPE");
					String option2Value = rs.getString("OPTION2_VALUE");
					String option3Type = rs.getString("OPTION3_TYPE");
					String option3Value = rs.getString("OPTION3_VALUE");

					StringBuilder option = new StringBuilder();

					if (option1Value != null && !option1Value.isBlank()) {
					    if (option1Type != null && !option1Type.isBlank()) {
					        option.append(option1Type).append(" : ");
					    }
					    option.append(option1Value);
					}

					if (option2Value != null && !option2Value.isBlank()) {
					    if (option.length() > 0) {
					        option.append(" / ");
					    }

					    if (option2Type != null && !option2Type.isBlank()) {
					        option.append(option2Type).append(" : ");
					    }

					    option.append(option2Value);
					}

					if (option3Value != null && !option3Value.isBlank()) {
					    if (option.length() > 0) {
					        option.append(" / ");
					    }

					    if (option3Type != null && !option3Type.isBlank()) {
					        option.append(option3Type).append(" : ");
					    }

					    option.append(option3Value);
					}

					dto.setOptionName(option.toString());

					dto.setProductImage( rs.getString( "IMAGE_URL" ) );
					 

					list.add(dto);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int getProductPrice(
			Connection conn,
			int productNo)
					throws Exception {

		String sql = """
				SELECT PRODUCT_PRICE
				FROM PRODUCT
				WHERE PRODUCT_NO = ?
				""";

		try (
				PreparedStatement pstmt =
				conn.prepareStatement(sql)
				) {

			pstmt.setInt(
					1,
					productNo
					);

			try (
					ResultSet rs =
					pstmt.executeQuery()
					) {

				if (rs.next()) {

					return rs.getInt(
							"PRODUCT_PRICE"
							);
				}
			}
		}

		return -1;
	}

	public int insertCheckout(
			Connection conn,
			int memberNo,
			int productAmount,
			int instantDiscount,
			int couponDiscount,
			int cashUsed,
			int deliveryFee,
			int totalPrice)
					throws Exception {

		int checkoutNo;

		String seqSql = """
				SELECT SEQ_CHECKOUT_NO.NEXTVAL
				FROM DUAL
				""";

		try (
				PreparedStatement pstmt =
				conn.prepareStatement(seqSql);

				ResultSet rs =
						pstmt.executeQuery()
				) {

			if (!rs.next()) {

				throw new Exception(
						"CHECKOUT_NO 생성 실패"
						);
			}

			checkoutNo =
					rs.getInt(1);
		}

		String sql = """
				INSERT INTO CHECKOUT (
				    CHECKOUT_NO,
				    MEMBER_NO,
				    CREATED_AT,
				    PRODUCT_AMOUNT,
				    INSTANT_DISCOUNT,
				    COUPON_DISCOUNT,
				    CASH_USED,
				    DELIVERY_FEE,
				    TOTAL_PRICE
				)
				VALUES (
				    ?,
				    ?,
				    SYSDATE,
				    ?,
				    ?,
				    ?,
				    ?,
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
					checkoutNo
					);

			pstmt.setInt(
					2,
					memberNo
					);

			pstmt.setInt(
					3,
					productAmount
					);

			pstmt.setInt(
					4,
					instantDiscount
					);

			pstmt.setInt(
					5,
					couponDiscount
					);

			pstmt.setInt(
					6,
					cashUsed
					);

			pstmt.setInt(
					7,
					deliveryFee
					);

			pstmt.setInt(
					8,
					totalPrice
					);


			int result =
					pstmt.executeUpdate();


			if (result != 1) {

				throw new Exception(
						"CHECKOUT INSERT 실패"
						);
			}
		}
		return checkoutNo;
	}

	public int insertCheckoutItem(
			Connection conn,
			int checkoutNo,
			int productNo,
			Integer optionId,
			int quantity,
			int price)
					throws Exception {

		String sql = """
				INSERT INTO CHECKOUT_ITEM (
				    CHECKOUT_ITEM_NO,
				    CHECKOUT_NO,
				    PRODUCT_NO,
				    OPTION_ID,
				    ORDER_QTY,
				    PRICE
				)
				VALUES (
				    SEQ_CHECKOUT_ITEM_NO.NEXTVAL,
				    ?,
				    ?,
				    ?,
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
					checkoutNo
					);

			pstmt.setInt(
					2,
					productNo
					);

			if (optionId == null) {

				pstmt.setNull(
						3,
						java.sql.Types.NUMERIC
						);

			} else {

				pstmt.setInt(
						3,
						optionId
						);
			}

			pstmt.setInt(
					4,
					quantity
					);

			pstmt.setInt(
					5,
					price
					);
			return pstmt.executeUpdate();
		}
	}

	public int createCheckout(int memberNo) {

		int checkoutNo = 0;

		String seqSql = """
				SELECT SEQ_CHECKOUT_NO.NEXTVAL
				FROM DUAL
				""";

		String insertSql = """
				INSERT INTO CHECKOUT (
				CHECKOUT_NO,
				MEMBER_NO
				)
				VALUES (
				?,
				?
				)
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection()
				) {

			try (
					PreparedStatement pstmt =
					conn.prepareStatement(seqSql);
					ResultSet rs =
							pstmt.executeQuery()
					) {

				if (rs.next()) {
					checkoutNo = rs.getInt(1);
				}

			}

			try (
					PreparedStatement pstmt =
					conn.prepareStatement(insertSql)
					) {

				pstmt.setInt(1, checkoutNo);
				pstmt.setInt(2, memberNo);

				int rowCount =
						pstmt.executeUpdate();

				if (rowCount == 0) {
					throw new RuntimeException(
							"CHECKOUT 생성 실패"
							);
				}

			}

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException(
					"CHECKOUT 생성 중 오류가 발생했습니다.",
					e
					);

		}

		return checkoutNo;
	}

	public int addCheckoutItem(
			int checkoutNo,
			int optionId,
			int quantity) {

		String sql = """
				INSERT INTO CHECKOUT_ITEM (
				CHECKOUT_ITEM_NO,
				CHECKOUT_NO,
				PRODUCT_NO,
				OPTION_ID,
				ORDER_QTY,
				PRICE
				)
				SELECT
				SEQ_CHECKOUT_ITEM_NO.NEXTVAL,
				?,
				p.PRODUCT_NO,
				po.OPTION_ID,
				?,
				NVL(p.PRODUCT_PRICE, 0) + NVL(po.PRICE, 0)
				FROM PRODUCT_OPTION po
				JOIN PRODUCT p
				ON po.PRODUCT_NO = p.PRODUCT_NO
				WHERE po.OPTION_ID = ?
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql)
				) {

			pstmt.setInt(1, checkoutNo);
			pstmt.setInt(2, quantity);
			pstmt.setInt(3, optionId);

			return pstmt.executeUpdate();

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException(
					"CHECKOUT_ITEM 등록 실패",
					e
					);

		}
	}

	
	public int updateCheckoutAmount(int checkoutNo) {

	    String sql = """
	            UPDATE CHECKOUT c
	            SET
	                PRODUCT_AMOUNT = (
	                    SELECT NVL(
	                        SUM(ci.PRICE * ci.ORDER_QTY),
	                        0
	                    )
	                    FROM CHECKOUT_ITEM ci
	                    WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                ),

	                DELIVERY_FEE =
	                    CASE
	                        WHEN (
	                            SELECT NVL(
	                                SUM(ci.PRICE * ci.ORDER_QTY),
	                                0
	                            )
	                            FROM CHECKOUT_ITEM ci
	                            WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                        ) >= 19800
	                        THEN 0
	                        ELSE 3000
	                    END,

	                TOTAL_PRICE =
	                    GREATEST(
	                        0,

	                        (
	                            SELECT NVL(
	                                SUM(ci.PRICE * ci.ORDER_QTY),
	                                0
	                            )
	                            FROM CHECKOUT_ITEM ci
	                            WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                        )

	                        +

	                        CASE
	                            WHEN (
	                                SELECT NVL(
	                                    SUM(ci.PRICE * ci.ORDER_QTY),
	                                    0
	                                )
	                                FROM CHECKOUT_ITEM ci
	                                WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                            ) >= 19800
	                            THEN 0
	                            ELSE 3000
	                        END

	                        - NVL(c.INSTANT_DISCOUNT, 0)
	                        - NVL(c.COUPON_DISCOUNT, 0)
	                        - NVL(c.CASH_USED, 0)
	                    )

	            WHERE c.CHECKOUT_NO = ?
	            """;
	    try (
	            Connection conn =
	                    ConnectionProvider.getConnection();
	            PreparedStatement pstmt =
	                    conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, checkoutNo);
	        return pstmt.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "CHECKOUT 결제금액 계산 실패",
	                e
	        );
	    }
	}
	
	public int updateCheckoutAmountv2(int checkoutNo) {

	    String sql = """
	            UPDATE CHECKOUT c
	            SET
	                PRODUCT_AMOUNT = (
	                    SELECT NVL(
	                        SUM(ci.PRICE * ci.ORDER_QTY),
	                        0
	                    )
	                    FROM CHECKOUT_ITEM ci
	                    WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                ),
	                DELIVERY_FEE =
	                    CASE
	                        -- 와우 회원이면 무조건 무료배송
	                        WHEN EXISTS (
	                            SELECT 1
	                            FROM WOW_MEMBERSHIP wm
	                            WHERE wm.MEMBER_NO = c.MEMBER_NO
	                            AND wm.STATUS IN ('ACTIVE', 'CANCEL_PENDING')
	                            AND wm.END_DATE >= TRUNC(SYSDATE)
	                        )
	                        THEN 0
	                        -- 일반 회원은 19,800원 이상 무료배송
	                        WHEN (
	                            SELECT NVL(
	                                SUM(ci.PRICE * ci.ORDER_QTY),
	                                0
	                            )
	                            FROM CHECKOUT_ITEM ci
	                            WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                        ) >= 19800
	                        THEN 0
	                        ELSE 3000
	                    END,
	                TOTAL_PRICE =
	                    GREATEST(
	                        0,
	                        (
	                            SELECT NVL(
	                                SUM(ci.PRICE * ci.ORDER_QTY),
	                                0
	                            )
	                            FROM CHECKOUT_ITEM ci
	                            WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                        )
	                        +
	                        CASE
	                            -- 와우 회원이면 배송비 0원
	                            WHEN EXISTS (
	                                SELECT 1
	                                FROM WOW_MEMBERSHIP wm
	                                WHERE wm.MEMBER_NO = c.MEMBER_NO
	                                AND wm.STATUS IN ('ACTIVE','CANCEL_PENDING')
	    							AND wm.END_DATE >= TRUNC(SYSDATE)
	                            )
	                            THEN 0
	                            -- 일반 회원 19,800원 이상
	                            WHEN (
	                                SELECT NVL(
	                                    SUM(ci.PRICE * ci.ORDER_QTY),
	                                    0
	                                )
	                                FROM CHECKOUT_ITEM ci
	                                WHERE ci.CHECKOUT_NO = c.CHECKOUT_NO
	                            ) >= 19800
	                            THEN 0
	                            ELSE 3000
	                        END
	                        - NVL(c.INSTANT_DISCOUNT, 0)
	                        - NVL(c.COUPON_DISCOUNT, 0)
	                        - NVL(c.CASH_USED, 0)
	                    )
	            WHERE c.CHECKOUT_NO = ?
	            """;
	    try (
	            Connection conn =
	                    ConnectionProvider.getConnection();
	            PreparedStatement pstmt =
	                    conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, checkoutNo);
	        return pstmt.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "CHECKOUT 결제금액 계산 실패",
	                e
	        );
	    }
	}
	
	public int getOptionPrice(
	        Connection conn,
	        int productNo,
	        int optionId) {

	    String sql = """
	            SELECT NVL(PRICE, 0) AS OPTION_PRICE
	            FROM PRODUCT_OPTION
	            WHERE PRODUCT_NO = ?
	              AND OPTION_ID = ?
	            """;
	    try (
	            PreparedStatement pstmt =
	                    conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, productNo);
	        pstmt.setInt(2, optionId);
	        try (ResultSet rs =
	                pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(
	                        "OPTION_PRICE"
	                );
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "옵션 가격 조회 실패",
	                e
	        );
	    }
	    throw new RuntimeException(
	            "존재하지 않는 상품 옵션입니다."
	    );
	}
}
