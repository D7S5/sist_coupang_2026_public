package com.goodpang.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.OrderCompleteDTO;
import com.goodpang.dto.OrderSummaryDTO;
import com.goodpang.util.ConnectionProvider;

public class OrderDAO {

	/*
	 * 기본 배송지 조회
	 */
	public AddressDTO getAddress(int memberNo) {

		AddressDTO address = null;

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
				WHERE MEMBER_NO = ?
				  AND ADDRESS_DEFAULT = 'Y'
				""";

		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					address = new AddressDTO();

					address.setAddressNo(rs.getInt("ADDRESS_NO"));

					address.setMemberNo(rs.getInt("MEMBER_NO"));

					address.setReceiverName(rs.getString("RECEIVER_NAME"));

					address.setTel(rs.getString("TEL"));

					address.setZipcode(rs.getString("ZIPCODE"));

					address.setAddress(rs.getString("ADDRESS"));

					address.setDetailAddress(rs.getString("DETAIL_ADDRESS"));

					address.setRequestMsg(rs.getString("REQUEST_MSG"));

					address.setAddressDefault(rs.getString("ADDRESS_DEFAULT"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return address;
	}

	public List<AddressDTO> getAddressList(int memberNo) {

		List<AddressDTO> list = new ArrayList<>();

		String sql = """
				SELECT
				    ADDRESS_NO,
				    RECEIVER_NAME,
				    TEL,
				    ZIPCODE,
				    ADDRESS,
				    DETAIL_ADDRESS,
				    REQUEST_MSG,
				    ADDRESS_DEFAULT
				FROM DELIVERY_ADDRESS
				WHERE MEMBER_NO = ?
				ORDER BY
				    ADDRESS_DEFAULT DESC,
				    ADDRESS_NO DESC
				""";

		try (Connection conn = ConnectionProvider.getConnection();

				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					AddressDTO dto = new AddressDTO();

					dto.setAddressNo(rs.getInt("ADDRESS_NO"));

					dto.setReceiverName(rs.getString("RECEIVER_NAME"));

					dto.setTel(rs.getString("TEL"));

					dto.setZipcode(rs.getString("ZIPCODE"));

					dto.setAddress(rs.getString("ADDRESS"));

					dto.setDetailAddress(rs.getString("DETAIL_ADDRESS"));

					dto.setRequestMsg(rs.getString("REQUEST_MSG"));

					dto.setAddressDefault(rs.getString("ADDRESS_DEFAULT"));

					list.add(dto);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/*
	 * 주문 상품 조회
	 */
	/*
	 * public List<OrderItemDTO> getOrderItems(int orderNo) {
	 * 
	 * List<OrderItemDTO> orderItems = new ArrayList<>();
	 * 
	 * String sql = """ SELECT P.PRODUCT_NAME, OI.OPTION_NAME, OI.SALE_PRICE,
	 * OI.QUANTITY, OI.FREE_DELIVERY FROM ORDER_ITEM OI JOIN PRODUCT P ON
	 * OI.PRODUCT_NO = P.PRODUCT_NO WHERE OI.ORDER_NO = ? ORDER BY OI.ORDER_ITEM_NO
	 * """;
	 * 
	 * try ( Connection conn = ConnectionProvider.getConnection(); PreparedStatement
	 * pstmt = conn.prepareStatement(sql) ) {
	 * 
	 * pstmt.setInt(1, orderNo);
	 * 
	 * try (ResultSet rs = pstmt.executeQuery()) {
	 * 
	 * while (rs.next()) {
	 * 
	 * OrderItemDTO item = new OrderItemDTO();
	 * 
	 * item.setProductName( rs.getString("PRODUCT_NAME") );
	 * 
	 * item.setOptionName( rs.getString("OPTION_NAME") );
	 * 
	 * item.setSalePrice( rs.getInt("SALE_PRICE") );
	 * 
	 * item.setQuantity( rs.getInt("QUANTITY") );
	 * 
	 * item.setFreeDelivery( rs.getBoolean("FREE_DELIVERY") );
	 * 
	 * orderItems.add(item); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return orderItems; }
	 */

	/*
	 * 주문 금액 조회
	 */
	public OrderSummaryDTO getOrderSummary(int orderNo) {

		OrderSummaryDTO summary = null;

		String sql = """
				SELECT
				    o.order_no,

				    NVL(o.product_amount, 0)
				        AS total_product_price,

				    NVL(o.delivery_fee, 0)
				        AS delivery_fee,

				    NVL(o.instant_discount, 0)
				        AS instant_discount,

				    NVL(o.coupon_discount, 0)
				        AS coupon_discount,

				    NVL(o.cash_used, 0)
				        AS cash_used,

				    GREATEST(
				        0,
				        NVL(o.product_amount, 0)
				        - NVL(o.instant_discount, 0)
				        - NVL(o.coupon_discount, 0)
				        - NVL(o.cash_used, 0)
				        + NVL(o.delivery_fee, 0)
				    ) AS final_price

				FROM orders o
				WHERE o.order_no = ?
				""";

		try (Connection conn = ConnectionProvider.getConnection();

				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, orderNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					summary = new OrderSummaryDTO();

					summary.setTotalProductPrice(rs.getInt("total_product_price"));

					summary.setDeliveryFee(rs.getInt("delivery_fee"));

					summary.setInstantDiscount(rs.getInt("instant_discount"));

					summary.setCouponDiscount(rs.getInt("coupon_discount"));

					summary.setCashUsed(rs.getInt("cash_used"));

					summary.setFinalPrice(rs.getInt("final_price"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return summary;
	}

	public OrderCompleteDTO getOrderComplete(int orderNo) {

		OrderCompleteDTO dto = null;

		String sql = """
				SELECT
				    o.ORDER_NO,
				    o.MEMBER_NO,
				    o.ORDER_DATE,
				    o.DELIVERY_FEE,
				    o.TOTAL_PRICE,
				    o.ORDER_STATUS,
				    o.PRODUCT_AMOUNT,
				    o.INSTANT_DISCOUNT,
				    o.COUPON_DISCOUNT,
				    o.CASH_USED,

				    d.RECEIVER_NAME,
				    d.TEL,
				    d.ZIPCODE,
				    d.ADDRESS,
				    d.DETAIL_ADDRESS,
				    d.REQUEST_MSG

				FROM ORDERS o

				JOIN ORDER_ADDRESS d
				  ON o.ORDER_ADDRESS_NO
				   = d.ORDER_ADDRESS_NO

				WHERE o.ORDER_NO = ?
				""";

		try (Connection conn = ConnectionProvider.getConnection();

				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, orderNo);
			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					dto = new OrderCompleteDTO();

					dto.setReceiverName(rs.getString("RECEIVER_NAME"));

					dto.setReceiverPhone(rs.getString("TEL"));

					dto.setZipcode(rs.getString("ZIPCODE"));

					String address = rs.getString("ADDRESS");

					String detailAddress = rs.getString("DETAIL_ADDRESS");

					if (address == null) {
						address = "";
					}

					if (detailAddress != null && !detailAddress.isBlank()) {

						address += " " + detailAddress;
					}

					dto.setAddress(address);

					dto.setRequestMsg(rs.getString("REQUEST_MSG"));

					if (rs.getDate("ORDER_DATE") != null) {

						dto.setArrivalDate(rs.getDate("ORDER_DATE").toLocalDate().plusDays(1).toString());
					}

					dto.setSellerName("주식회사 회사이름");

					dto.setOrderAmount(rs.getInt("PRODUCT_AMOUNT"));

					int discountAmount =

							rs.getInt("INSTANT_DISCOUNT")

									+

									rs.getInt("COUPON_DISCOUNT");

					dto.setDiscountAmount(discountAmount);

					dto.setShippingFee(rs.getInt("DELIVERY_FEE"));

					dto.setPaymentAmount(rs.getInt("TOTAL_PRICE"));
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return dto;
	}

	public int insertOrderDelivery(Connection conn, int orderNo, int addressNo) throws Exception {

		String sql = """
				INSERT INTO ORDER_ADDRESS (
				    ORDER_NO,
				    RECEIVER_NAME,
				    TEL,
				    ZIPCODE,
				    ADDRESS,
				    DETAIL_ADDRESS,
				    REQUEST_MSG
				)
				SELECT
				    ?,
				    RECEIVER_NAME,
				    TEL,
				    ZIPCODE,
				    ADDRESS,
				    DETAIL_ADDRESS,
				    REQUEST_MSG
				FROM DELIVERY_ADDRESS
				WHERE ADDRESS_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, orderNo);
			pstmt.setInt(2, addressNo);

			return pstmt.executeUpdate();
		}
	}
	/*
	 * public List<OrderItemDTO> getOrderListByMemberNo(int memberNo) {
	 * 
	 * List<OrderItemDTO> list = new ArrayList<>();
	 * 
	 * String sql = """ SELECT o.ORDER_NO, o.ORDER_DATE, o.TOTAL_PRICE,
	 * o.ORDER_STATUS,
	 * 
	 * FROM ORDERS o JOIN PRODUCT p ON o.ORDER_NO = p.ORDER_NO WHERE o.MEMBER_NO = ?
	 * ORDER BY o.ORDER_NO DESC """;
	 * 
	 * 
	 * 
	 * try ( Connection conn = ConnectionProvider.getConnection(); PreparedStatement
	 * pstmt = conn.prepareStatement(sql) ) { System.out.println(sql);
	 * 
	 * pstmt.setInt(1, memberNo);
	 * 
	 * try (ResultSet rs = pstmt.executeQuery()) {
	 * 
	 * while (rs.next()) {
	 * 
	 * OrderItemDTO item = new OrderItemDTO();
	 * 
	 * item.setOrderNo( rs.getInt("ORDER_NO") );
	 * 
	 * item.setProductName( "주문번호 " + rs.getInt("ORDER_NO") + "번 (" +
	 * rs.getString("ORDER_STATUS") + ")" );
	 * 
	 * item.setOptionName( "결제수단: " + rs.getString("PAYMENT_METHOD") );
	 * 
	 * item.setSalePrice( rs.getInt("TOTAL_PRICE") );
	 * 
	 * item.setQuantity(1);
	 * 
	 * item.setFreeDelivery(true);
	 * 
	 * list.add(item); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return list; }
	 */

	public int updateTotalPrice(Connection conn, int orderNo) throws Exception {

		String sql = """
				UPDATE orders
				SET total_price =
				    GREATEST(
				        0,
				        NVL(product_amount, 0)
				        - NVL(instant_discount, 0)
				        - NVL(coupon_discount, 0)
				        - NVL(cash_used, 0)
				        + NVL(delivery_fee, 0)
				    )
				WHERE order_no = ?
				""";

		int rowCount = 0;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, orderNo);

			rowCount = pstmt.executeUpdate();
		}

		return rowCount;
	}

	public int updateOrderStatus(Connection conn, int orderNo, String orderStatus) throws Exception {

		String sql = """
				UPDATE orders
				SET order_status = ?
				WHERE order_no = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, orderStatus);

			pstmt.setInt(2, orderNo);

			return pstmt.executeUpdate();
		}
	}

	public int updateOrderAddress(Connection conn, int orderNo, int addressNo) throws Exception {

		String sql = """
				UPDATE orders
				SET order_address_no = ?
				WHERE order_no = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, addressNo);
			pstmt.setInt(2, orderNo);

			return pstmt.executeUpdate();
		}
	}

	public boolean existsCheckout(Connection conn, int checkoutNo, int memberNo) {

		String sql = """
				SELECT COUNT(*)
				FROM CHECKOUT
				WHERE CHECKOUT_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, checkoutNo);
			pstmt.setInt(2, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return rs.getInt(1) == 1;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean existsAddress(Connection conn, int addressNo, int memberNo) {

		String sql = """
				SELECT COUNT(*)
				FROM DELIVERY_ADDRESS
				WHERE ADDRESS_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, addressNo);
			pstmt.setInt(2, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return rs.getInt(1) == 1;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public int insertOrderAddress(Connection conn, int addressNo, int memberNo) throws Exception {

		/*
		 * 먼저 생성할 ORDER_ADDRESS_NO 확보
		 */
		int orderAddressNo = 0;

		String seqSql = """
				SELECT SEQ_ORDER_ADDRESS_NO.NEXTVAL
				FROM DUAL
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(seqSql);

				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next()) {
				orderAddressNo = rs.getInt(1);
			}
		}

		if (orderAddressNo == 0) {
			throw new Exception("ORDER_ADDRESS_NO 생성 실패");
		}

		/*
		 * DELIVERY_ADDRESS 데이터를 복사
		 */
		String sql = """
				INSERT INTO ORDER_ADDRESS (
				    ORDER_ADDRESS_NO,
				    MEMBER_NO,
				    RECEIVER_NAME,
				    TEL,
				    ZIPCODE,
				    ADDRESS,
				    DETAIL_ADDRESS,
				    REQUEST_MSG
				)
				SELECT
				    ?,
				    MEMBER_NO,
				    RECEIVER_NAME,
				    TEL,
				    ZIPCODE,
				    ADDRESS,
				    DETAIL_ADDRESS,
				    REQUEST_MSG
				FROM DELIVERY_ADDRESS
				WHERE ADDRESS_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, orderAddressNo);

			pstmt.setInt(2, addressNo);

			pstmt.setInt(3, memberNo);

			int result = pstmt.executeUpdate();

			if (result != 1) {

				throw new Exception("ORDER_ADDRESS 저장 실패");
			}
		}

		return orderAddressNo;
	}

	/*
	 * public int insertOrderFromCheckoutv2(Connection conn, int checkoutNo, int
	 * memberNo, int orderAddressNo, String paymentMethod, Integer paymentMethodNo)
	 * throws Exception {
	 * 
	 * 
	 * ORDER_NO 생성
	 * 
	 * int orderNo = 0;
	 * 
	 * String seqSql = """ SELECT SEQ_ORDER_NO.NEXTVAL FROM DUAL """;
	 * 
	 * try (PreparedStatement pstmt = conn.prepareStatement(seqSql); ResultSet rs =
	 * pstmt.executeQuery()) {
	 * 
	 * if (rs.next()) { orderNo = rs.getInt(1); } }
	 * 
	 * if (orderNo == 0) { throw new Exception("ORDER_NO 생성 실패"); }
	 * 
	 * 
	 * CHECKOUT -> ORDERS
	 * 
	 * String orderSql = """ INSERT INTO ORDERS ( ORDER_NO, MEMBER_NO, ORDER_DATE,
	 * DELIVERY_FEE, TOTAL_PRICE, ORDER_STATUS, ORDER_ADDRESS_NO, PRODUCT_AMOUNT,
	 * INSTANT_DISCOUNT, COUPON_DISCOUNT, CASH_USED ) SELECT ?, MEMBER_NO, SYSDATE,
	 * DELIVERY_FEE, TOTAL_PRICE, '결제완료', ?, PRODUCT_AMOUNT, INSTANT_DISCOUNT,
	 * COUPON_DISCOUNT, CASH_USED FROM CHECKOUT WHERE CHECKOUT_NO = ? AND MEMBER_NO
	 * = ? """;
	 * 
	 * try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
	 * 
	 * pstmt.setInt(1, orderNo);
	 * 
	 * pstmt.setInt(2, orderAddressNo);
	 * 
	 * pstmt.setInt(3, checkoutNo);
	 * 
	 * pstmt.setInt(4, memberNo);
	 * 
	 * int result = pstmt.executeUpdate();
	 * 
	 * if (result != 1) { throw new Exception("ORDERS 생성 실패"); } }
	 * 
	 * String paymentSql = """ INSERT INTO PAYMENT ( PAYMENT_NO, ORDER_NO,
	 * PAYMENT_METHOD, PAYMENT_METHOD_NO, PAYMENT_AMOUNT, PAYMENT_STATUS,
	 * PAYMENT_DATE ) SELECT SEQ_PAYMENT_NO.NEXTVAL, ?, ?, ?, TOTAL_PRICE, 'PAID',
	 * SYSDATE FROM CHECKOUT WHERE CHECKOUT_NO = ? AND MEMBER_NO = ? """;
	 * 
	 * try (PreparedStatement pstmt = conn.prepareStatement(paymentSql)) {
	 * 
	 * pstmt.setInt(1, orderNo);
	 * 
	 * pstmt.setString(2, paymentMethod);
	 * 
	 * if (paymentMethodNo == null) {
	 * 
	 * pstmt.setNull(3, java.sql.Types.NUMERIC);
	 * 
	 * } else {
	 * 
	 * pstmt.setInt(3, paymentMethodNo); }
	 * 
	 * pstmt.setInt(4, checkoutNo);
	 * 
	 * pstmt.setInt(5, memberNo);
	 * 
	 * int result = pstmt.executeUpdate();
	 * 
	 * if (result != 1) { throw new Exception("PAYMENT 생성 실패"); } }
	 * 
	 * return orderNo; }
	 */

	public int insertOrderFromCheckout(Connection conn, int checkoutNo, int memberNo, int orderAddressNo,
			String paymentMethod, Integer paymentMethodNo) throws Exception {

		/*
		 * ===================================== 1. ORDER_NO 생성
		 * =====================================
		 */
		int orderNo = 0;

		String seqSql = """
				SELECT SEQ_ORDER_NO.NEXTVAL
				FROM DUAL
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(seqSql);

				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next()) {
				orderNo = rs.getInt(1);
			}
		}

		if (orderNo <= 0) {
			throw new Exception("ORDER_NO 생성 실패");
		}

		/*
		 * ===================================== 2. CHECKOUT -> ORDERS
		 *
		 * CHECKOUT_NO도 ORDERS에 저장 -> 중복결제 확인용 =====================================
		 */
		String orderSql = """
				INSERT INTO ORDERS (
				    ORDER_NO,
				    MEMBER_NO,
				    CHECKOUT_NO,
				    ORDER_DATE,
				    DELIVERY_FEE,
				    TOTAL_PRICE,
				    ORDER_STATUS,
				    ORDER_ADDRESS_NO,
				    PRODUCT_AMOUNT,
				    INSTANT_DISCOUNT,
				    COUPON_DISCOUNT,
				    CASH_USED
				)
				SELECT
				    ?,
				    MEMBER_NO,
				    CHECKOUT_NO,
				    SYSDATE,
				    DELIVERY_FEE,
				    TOTAL_PRICE,
				    '결제완료',
				    ?,
				    PRODUCT_AMOUNT,
				    INSTANT_DISCOUNT,
				    COUPON_DISCOUNT,
				    CASH_USED
				FROM CHECKOUT
				WHERE CHECKOUT_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {

			pstmt.setInt(1, orderNo);

			pstmt.setInt(2, orderAddressNo);

			pstmt.setInt(3, checkoutNo);

			pstmt.setInt(4, memberNo);

			int result = pstmt.executeUpdate();

			if (result != 1) {

				throw new Exception("ORDERS 생성 실패");
			}
		}

		/*
		 * ===================================== 3. PAYMENT 생성
		 * =====================================
		 */
		String paymentSql = """
				INSERT INTO PAYMENT (
				    PAYMENT_NO,
				    ORDER_NO,
				    PAYMENT_METHOD,
				    PAYMENT_METHOD_NO,
				    PAYMENT_AMOUNT,
				    PAYMENT_STATUS,
				    PAYMENT_DATE
				)
				SELECT
				    SEQ_PAYMENT_NO.NEXTVAL,
				    ?,
				    ?,
				    ?,
				    TOTAL_PRICE,
				    'PAID',
				    SYSDATE
				FROM CHECKOUT
				WHERE CHECKOUT_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(paymentSql)) {

			/*
			 * ORDER_NO
			 */
			pstmt.setInt(1, orderNo);

			/*
			 * CARD BANK_TRANSFER COUPAY_MONEY
			 */
			pstmt.setString(2, paymentMethod);

			/*
			 * 등록 카드/계좌 번호
			 *
			 * COUPAY_MONEY이면 null
			 */
			if (paymentMethodNo == null) {

				pstmt.setNull(3, java.sql.Types.NUMERIC);

			} else {

				pstmt.setInt(3, paymentMethodNo);
			}

			/*
			 * CHECKOUT_NO
			 */
			pstmt.setInt(4, checkoutNo);

			/*
			 * MEMBER_NO
			 */
			pstmt.setInt(5, memberNo);

			int result = pstmt.executeUpdate();

			if (result != 1) {

				throw new Exception("PAYMENT 생성 실패");
			}
		}

		/*
		 * 주문 생성 완료
		 */
		return orderNo;
	}

	public int insertOrderDetailsFromCheckout(Connection conn, int checkoutNo, int orderNo) throws Exception {

		String sql = """
				INSERT INTO ORDER_DETAIL (
				    ORDER_DETAIL_NO,
				    ORDER_NO,
				    PRODUCT_NO,
				    ORDER_QTY,
				    PRICE,
				    OPTION_ID
				)
				SELECT
				    SEQ_ORDER_DETAIL_NO.NEXTVAL,
				    ?,
				    PRODUCT_NO,
				    ORDER_QTY,
				    PRICE,
				    OPTION_ID
				FROM CHECKOUT_ITEM
				WHERE CHECKOUT_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, orderNo);

			pstmt.setInt(2, checkoutNo);

			return pstmt.executeUpdate();
		}
	}

	public int deleteCheckoutItems(Connection conn, int checkoutNo) throws Exception {

		String sql = """
				DELETE FROM CHECKOUT_ITEM
				WHERE CHECKOUT_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, checkoutNo);

			return pstmt.executeUpdate();
		}
	}

	public int deleteCheckout(Connection conn, int checkoutNo, int memberNo) throws Exception {

		String sql = """
				DELETE FROM CHECKOUT
				WHERE CHECKOUT_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, checkoutNo);

			pstmt.setInt(2, memberNo);

			return pstmt.executeUpdate();
		}
	}

	public Integer findOrderNoByCheckout(Connection conn, int checkoutNo, int memberNo) throws Exception {

		String sql = """
				SELECT ORDER_NO
				FROM ORDERS
				WHERE CHECKOUT_NO = ?
				  AND MEMBER_NO = ?
				""";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, checkoutNo);
			pstmt.setInt(2, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return rs.getInt("ORDER_NO");
				}
			}
		}

		return null;
	}

	/*
	 * 재고 부족 정보를 담는 작은 그릇.
	 * 상품명과 남은 수량 두 개를 같이 돌려줘야 하는데, 이거 하나 때문에
	 * DTO 파일을 새로 만들기는 아까워서 OrderDAO 안에 넣어둠.
	 */
	public static class StockFail {

		public final String productName;   // 재고가 모자란 상품 이름
		public final int    left;          // 그 상품의 남은 재고

		public StockFail(String productName, int left) {
			this.productName = productName;
			this.left = left;
		}
	}

	/*
	 * 재고 차감 — DB 프로시저 PRC_ORDER_STOCK_OUT 을 부른다.
	 *
	 * ★ 반드시 주문 트랜잭션이 쓰고 있는 conn 을 그대로 받아서 쓴다.
	 *   여기서 ConnectionProvider.getConnection() 으로 새 커넥션을 따면
	 *   완전히 다른 트랜잭션이 되어버려서, 주문이 롤백돼도 재고는 안 돌아온다.
	 *
	 * 반환값
	 *   null        → 차감 성공
	 *   StockFail   → 재고 부족 (어느 상품이 몇 개 남았는지 들어있음)
	 *
	 * ★ 프로시저가 예외를 던지지 않고 결과값으로 알려주는 이유
	 *   PL/SQL 에서 RAISE_APPLICATION_ERROR 로 예외를 던지면 OUT 파라미터 값이
	 *   자바로 넘어오지 않는다(호출 자체가 실패로 끝나서 값이 안 채워짐).
	 *   화면에 "○○ 재고가 부족합니다(남은 수량 3개)" 를 띄우려면 상품명이 필요하므로
	 *   예외 대신 o_result(0/1)로 돌려받고, 롤백 여부는 자바가 판단한다.
	 */
	public StockFail stockOut(
			Connection conn,
			int orderNo)
			throws Exception {

		String sql = "{ call PRC_ORDER_STOCK_OUT(?, ?, ?, ?) }";

		try (CallableStatement cstmt = conn.prepareCall(sql)) {

			// 1번은 넣는 값(IN), 2~4번은 되받을 자리(OUT)
			cstmt.setInt(1, orderNo);

			cstmt.registerOutParameter(2, Types.NUMERIC);   // o_result   0=성공 1=재고부족
			cstmt.registerOutParameter(3, Types.VARCHAR);   // o_fail_product
			cstmt.registerOutParameter(4, Types.NUMERIC);   // o_fail_left

			cstmt.execute();

			int result = cstmt.getInt(2);

			if (result == 0) {
				return null;   // 성공
			}

			return new StockFail(
					cstmt.getString(3),
					cstmt.getInt(4)
					);
		}
	}	
	
	
	
}