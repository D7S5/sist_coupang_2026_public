package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.goodpang.dto.CartItemDTO;
import com.goodpang.util.ConnectionProvider;

public class CartDAO {

	
	public List<CartItemDTO> getCartItems(int memberNo) {

	    List<CartItemDTO> list = new ArrayList<>();

	    String sql = """
	            SELECT
	                c.MEMBER_NO,
	                c.OPTION_ID,
	                c.QUANTITY,
	                p.PRODUCT_NO,
	                p.PRODUCT_NAME,
	                p.PRODUCT_PRICE,
	                po.OPTION1_TYPE,
	                po.OPTION1_VALUE,
	                po.OPTION2_TYPE,
	                po.OPTION2_VALUE,
	                po.OPTION3_TYPE,
	                po.OPTION3_VALUE,
	                NVL(po.PRICE, 0) AS OPTION_PRICE,
	                (
	                    SELECT pi.IMAGE_URL
	                    FROM PRODUCT_IMAGE pi
	                    WHERE pi.PRODUCT_NO = p.PRODUCT_NO
	                    ORDER BY
	                        CASE
	                            WHEN pi.OPTION_ID = po.OPTION_ID
	                                 AND pi.IMAGE_PURPOSE = '대표' THEN 0
	                            WHEN pi.OPTION_ID = po.OPTION_ID THEN 1
	                            WHEN pi.IMAGE_PURPOSE = '대표' THEN 2
	                            ELSE 3
	                        END
	                    FETCH FIRST 1 ROW ONLY
	                ) AS IMAGE_URL
	            FROM CART c
	            JOIN PRODUCT_OPTION po
	              ON c.OPTION_ID = po.OPTION_ID
	            JOIN PRODUCT p
	              ON po.PRODUCT_NO = p.PRODUCT_NO
	            WHERE c.MEMBER_NO = ?
	            ORDER BY p.PRODUCT_NO DESC
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {

	                CartItemDTO dto = new CartItemDTO();

	                int productPrice =
	                        rs.getInt("PRODUCT_PRICE");

	                int optionPrice =
	                        rs.getInt("OPTION_PRICE");

	                dto.setMemberNo(
	                        rs.getInt("MEMBER_NO")
	                );

	                dto.setOptionId(
	                        rs.getInt("OPTION_ID")
	                );

	                dto.setQuantity(
	                        rs.getInt("QUANTITY")
	                );

	                dto.setProductNo(
	                        rs.getInt("PRODUCT_NO")
	                );

	                dto.setProductName(
	                        rs.getString("PRODUCT_NAME")
	                );

	                dto.setProductPrice(
	                        productPrice
	                );

	                dto.setOptionPrice(
	                        optionPrice
	                );

	                dto.setUnitPrice(
	                        productPrice + optionPrice
	                );

	                dto.setImageUrl(
	                        rs.getString("IMAGE_URL")
	                );

	                dto.setOption1Type(
	                        rs.getString("OPTION1_TYPE")
	                );

	                dto.setOption1Value(
	                        rs.getString("OPTION1_VALUE")
	                );

	                dto.setOption2Type(
	                        rs.getString("OPTION2_TYPE")
	                );

	                dto.setOption2Value(
	                        rs.getString("OPTION2_VALUE")
	                );

	                dto.setOption3Type(
	                        rs.getString("OPTION3_TYPE")
	                );

	                dto.setOption3Value(
	                        rs.getString("OPTION3_VALUE")
	                );

	                list.add(dto);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "장바구니 조회 실패",
	                e
	        );
	    }

	    return list;
	}

	public int addCart(
			int memberNo,
			int optionId,
			int quantity) {

		String sql = """
				MERGE INTO CART c

				USING (
				    SELECT
				        ? AS MEMBER_NO,
				        ? AS OPTION_ID,
				        ? AS QUANTITY
				    FROM DUAL
				) src

				ON (
				    c.MEMBER_NO = src.MEMBER_NO
				    AND c.OPTION_ID = src.OPTION_ID
				)

				WHEN MATCHED THEN
				    UPDATE SET
				        c.QUANTITY =
				            c.QUANTITY + src.QUANTITY

				WHEN NOT MATCHED THEN
				    INSERT (
				        MEMBER_NO,
				        OPTION_ID,
				        QUANTITY
				    )
				    VALUES (
				        src.MEMBER_NO,
				        src.OPTION_ID,
				        src.QUANTITY
				    )
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql);
				) {

			pstmt.setInt(1, memberNo);
			pstmt.setInt(2, optionId);
			pstmt.setInt(3, quantity);

			return pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"장바구니 등록 실패",
					e
					);
		}
	}

	public int updateQuantity(
			int memberNo,
			int optionId,
			int quantity) {

		String sql = """
				UPDATE CART
				SET QUANTITY = ?
				WHERE MEMBER_NO = ?
				AND OPTION_ID = ?
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql)
				) {

			pstmt.setInt(1, quantity);
			pstmt.setInt(2, memberNo);
			pstmt.setInt(3, optionId);

			return pstmt.executeUpdate();

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException(
					"장바구니 수량 변경 실패",
					e
					);

		}
	}
	public int deleteCart(
			int memberNo,
			int optionId) {

		String sql = """
				DELETE FROM CART
				WHERE MEMBER_NO = ?
				  AND OPTION_ID = ?
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql);
				) {

			pstmt.setInt(1, memberNo);
			pstmt.setInt(2, optionId);

			return pstmt.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException(
					"장바구니 삭제 실패",
					e
					);
		}
	}

	public CartItemDTO getCartItemByOptionId(int optionId) {

	    CartItemDTO dto = null;

	    String sql = """
	            SELECT
	                p.PRODUCT_NO,
	                p.PRODUCT_NAME,
	                p.PRODUCT_PRICE,
	                po.OPTION_ID,
	                po.OPTION1_TYPE,
	                po.OPTION1_VALUE,
	                po.OPTION2_TYPE,
	                po.OPTION2_VALUE,
	                po.OPTION3_TYPE,
	                po.OPTION3_VALUE,
	                NVL(po.PRICE, 0) AS OPTION_PRICE,
	                (
	                    SELECT pi.IMAGE_URL
	                    FROM PRODUCT_IMAGE pi
	                    WHERE pi.PRODUCT_NO = p.PRODUCT_NO
	                    ORDER BY
	                        CASE
	                            WHEN pi.OPTION_ID = po.OPTION_ID
	                                 AND pi.IMAGE_PURPOSE = '대표' THEN 0
	                            WHEN pi.OPTION_ID = po.OPTION_ID THEN 1
	                            WHEN pi.IMAGE_PURPOSE = '대표' THEN 2
	                            ELSE 3
	                        END
	                    FETCH FIRST 1 ROW ONLY
	                ) AS IMAGE_URL
	            FROM PRODUCT_OPTION po
	            JOIN PRODUCT p
	              ON po.PRODUCT_NO = p.PRODUCT_NO
	            WHERE po.OPTION_ID = ?
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setInt(1, optionId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                dto = new CartItemDTO();

	                dto.setOptionId(rs.getInt("OPTION_ID"));
	                dto.setProductNo(rs.getInt("PRODUCT_NO"));
	                dto.setProductName(rs.getString("PRODUCT_NAME"));
	                dto.setProductPrice(rs.getInt("PRODUCT_PRICE"));
	                dto.setOptionPrice(rs.getInt("OPTION_PRICE"));

	                dto.setOption1Type(rs.getString("OPTION1_TYPE"));
	                dto.setOption1Value(rs.getString("OPTION1_VALUE"));
	                dto.setOption2Type(rs.getString("OPTION2_TYPE"));
	                dto.setOption2Value(rs.getString("OPTION2_VALUE"));
	                dto.setOption3Type(rs.getString("OPTION3_TYPE"));
	                dto.setOption3Value(rs.getString("OPTION3_VALUE"));

	                dto.setImageUrl(rs.getString("IMAGE_URL"));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(
	                "장바구니 상품 조회 실패",
	                e
	        );
	    }

	    return dto;
	}

	public int getCartCount(int memberNo) {

		String sql = """
				SELECT COUNT(*) AS CART_COUNT
				FROM CART
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

					return rs.getInt("CART_COUNT");

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

			throw new RuntimeException(
					"장바구니 개수 조회 실패",
					e
					);

		}

		return 0;
	}
	
	public int deleteSelected(
	        int memberNo,
	        int[] optionIds)
	        throws Exception {

	    String sql = """
	            DELETE FROM CART
	            WHERE MEMBER_NO = ?
	              AND OPTION_ID = ?
	            """;
	    int deletedCount = 0;
	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();
	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        try {
	            conn.setAutoCommit(false);
	            for (int optionId : optionIds) {
	                pstmt.setInt(
	                        1,
	                        memberNo
	                );
	                pstmt.setInt(
	                        2,
	                        optionId
	                );
	                pstmt.addBatch();
	            }
	            int[] results =
	                    pstmt.executeBatch();
	            for (int result : results) {
	                if (result > 0) {
	                    deletedCount += result;
	                }
	            }
	            conn.commit();
	        } catch (Exception e) {
	            conn.rollback();
	            throw e;
	        } finally {

	            conn.setAutoCommit(true);
	        }
	    }
	    return deletedCount;
	}
	
	public List<CartItemDTO> getGuestCartItems(Map<Integer, Integer> guestCart) {

	    List<CartItemDTO> list = new ArrayList<>();

	    if (guestCart == null || guestCart.isEmpty()) {
	        return list;
	    }

	    String sql = """
	            SELECT
	                po.OPTION_ID,
	                p.PRODUCT_NO,
	                p.PRODUCT_NAME,
	                p.PRODUCT_PRICE,
	                NVL(po.PRICE, 0) AS OPTION_PRICE,
	                po.OPTION1_TYPE,
	                po.OPTION1_VALUE,
	                po.OPTION2_TYPE,
	                po.OPTION2_VALUE,
	                po.OPTION3_TYPE,
	                po.OPTION3_VALUE,
	                (
	                    SELECT pi.IMAGE_URL
	                    FROM PRODUCT_IMAGE pi
	                    WHERE pi.PRODUCT_NO = p.PRODUCT_NO
	                    ORDER BY
	                        CASE
	                            WHEN pi.OPTION_ID = po.OPTION_ID
	                                 AND pi.IMAGE_PURPOSE = '대표' THEN 0
	                            WHEN pi.OPTION_ID = po.OPTION_ID THEN 1
	                            WHEN pi.IMAGE_PURPOSE = '대표' THEN 2
	                            ELSE 3
	                        END
	                    FETCH FIRST 1 ROW ONLY
	                ) AS IMAGE_URL
	            FROM PRODUCT_OPTION po
	            JOIN PRODUCT p
	              ON p.PRODUCT_NO = po.PRODUCT_NO
	            WHERE po.OPTION_ID = ?
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {

	        for (Map.Entry<Integer, Integer> entry : guestCart.entrySet()) {

	            int optionId = entry.getKey();
	            int quantity = entry.getValue();

	            pstmt.setInt(1, optionId);

	            try (ResultSet rs = pstmt.executeQuery()) {

	                if (rs.next()) {

	                    CartItemDTO item = new CartItemDTO();

	                    item.setOptionId(rs.getInt("OPTION_ID"));
	                    item.setProductNo(rs.getInt("PRODUCT_NO"));
	                    item.setProductName(rs.getString("PRODUCT_NAME"));

	                    item.setProductPrice(rs.getInt("PRODUCT_PRICE"));
	                    item.setOptionPrice(rs.getInt("OPTION_PRICE"));

	                    item.setQuantity(quantity);

	                    item.setOption1Type(rs.getString("OPTION1_TYPE"));
	                    item.setOption1Value(rs.getString("OPTION1_VALUE"));
	                    item.setOption2Type(rs.getString("OPTION2_TYPE"));
	                    item.setOption2Value(rs.getString("OPTION2_VALUE"));
	                    item.setOption3Type(rs.getString("OPTION3_TYPE"));
	                    item.setOption3Value(rs.getString("OPTION3_VALUE"));

	                    item.setImageUrl(rs.getString("IMAGE_URL"));

	                    list.add(item);
	                }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("비회원 장바구니 조회 실패", e);
	    }

	    return list;
	}
	
	public int clearCart(
	        Connection conn,
	        int memberNo)
	        throws Exception {

	    String sql = """
	            DELETE FROM CART
	            WHERE MEMBER_NO = ?
	            """;

	    try (
	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(
	                1,
	                memberNo
	        );

	        return pstmt.executeUpdate();
	    }
	}
	
	
}
