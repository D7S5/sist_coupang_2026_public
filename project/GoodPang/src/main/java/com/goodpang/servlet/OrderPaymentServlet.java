package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;

import com.goodpang.dao.CartDAO;
import com.goodpang.dao.OrderDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/order/checkout")
public class OrderPaymentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		MemberDTO member =
				LoginUtil.requireLogin(
						request,
						response
						);

		if (member == null) {
			return;
		}

		int memberNo =
				member.getMemberNo();

		String checkoutNoParam =
				request.getParameter("checkoutNo");

		String addressNoParam =
				request.getParameter("addressNo");

		String paymentMethod =
				request.getParameter("paymentMethod");

		String bankCode =
				request.getParameter("bankCode");

		String cardCompany =
				request.getParameter("cardCompany");
		
		
		Integer paymentMethodNo = null;


		if ("BANK_TRANSFER".equals(paymentMethod)) {

		    String paymentMethodNoParam =
		            request.getParameter(
		                    "paymentMethodNo"
		            );

		    if (paymentMethodNoParam == null
		            || paymentMethodNoParam.isBlank()) {

		        response.sendError(
		                HttpServletResponse.SC_BAD_REQUEST,
		                "계좌를 선택해주세요."
		        );

		        return;
		    }
		
		    paymentMethodNo =
		            Integer.valueOf(
		                    paymentMethodNoParam
		            );

		} else if ("CARD".equals(paymentMethod)) {

		    String paymentMethodNoParam =
		            request.getParameter(
		                    "cardPaymentMethodNo"
		            );

		    if (paymentMethodNoParam == null
		            || paymentMethodNoParam.isBlank()) {

		        response.sendError(
		                HttpServletResponse.SC_BAD_REQUEST,
		                "카드를 선택해주세요."
		        );

		        return;
		    }

		    paymentMethodNo =
		            Integer.valueOf(
		                    paymentMethodNoParam
		            );

		} else if ("COUPAY_MONEY".equals(
		        paymentMethod)) {

		    // 쿠페이머니는 PAYMENT_METHOD 테이블의
		    // 등록 카드/계좌가 아니므로 null
		    paymentMethodNo = null;

		} else {

		    response.sendError(
		            HttpServletResponse.SC_BAD_REQUEST,
		            "지원하지 않는 결제수단입니다."
		    );

		    return;
		}


		if (checkoutNoParam == null
				|| checkoutNoParam.isBlank()
				|| addressNoParam == null
				|| addressNoParam.isBlank()
				|| paymentMethod == null || paymentMethod.isBlank()
				) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"결제 정보가 올바르지 않습니다."
					);

			return;
		}

		Connection conn = null;
		try {
			int checkoutNo =
					Integer.parseInt(
							checkoutNoParam
							);
			int addressNo =
					Integer.parseInt(
							addressNoParam
							);
			conn =
					ConnectionProvider
					.getConnection();
			conn.setAutoCommit(false);

			OrderDAO dao =
					new OrderDAO();
			
			 // 중복 결제 확인

			Integer existingOrderNo =
			        dao.findOrderNoByCheckout(
			                conn,
			                checkoutNo,
			                memberNo
			        );

			if (existingOrderNo != null) {

			    conn.rollback();

			    response.sendRedirect(
			            request.getContextPath()
			            + "/order/already-completed"
			    );

			    return;
			}

			boolean checkoutExists =
			        dao.existsCheckout(
			                conn,
			                checkoutNo,
			                memberNo
			        );

			if (!checkoutExists) {

			    throw new Exception(
			            "존재하지 않거나 접근할 수 없는 checkout입니다."
			    );
			}

			boolean addressExists =
					dao.existsAddress(
							conn,
							addressNo,
							memberNo
							);
			if (!addressExists) {

				throw new Exception(
						"잘못된 배송지입니다."
						);
			}
			int orderAddressNo =
					dao.insertOrderAddress(
							conn,
							addressNo,
							memberNo
							);
			int orderNo =
			        dao.insertOrderFromCheckout(
			                conn,
			                checkoutNo,
			                memberNo,
			                orderAddressNo,
			                paymentMethod,
			                paymentMethodNo
			        );


			if (orderNo <= 0) {

				throw new Exception(
						"주문 생성 실패"
						);
			}

			int detailCount =
					dao.insertOrderDetailsFromCheckout(
							conn,
							checkoutNo,
							orderNo
							);
			if (detailCount <= 0) {

				throw new Exception(
						"주문 상품 저장 실패"
						);
			}
			
			/*
			 * ★ 재고 차감 — 주문 상세를 넣은 직후, 커밋 전에 한다.
			 *
			 * 위치가 여기인 이유:
			 *   프로시저가 ORDER_DETAIL 을 읽어서 무엇을 몇 개 깎을지 정하므로
			 *   insertOrderDetailsFromCheckout() 뒤여야 한다.
			 *   그리고 conn.commit() 전이어야 재고 부족일 때 주문까지 통째로 되돌릴 수 있다.
			 */
			OrderDAO.StockFail stockFail =
					dao.stockOut(
							conn,
							orderNo
							);

			if (stockFail != null) {

				/*
				 * 재고 부족은 "에러"가 아니라 흔히 일어나는 정상 상황이다.
				 * 아래 catch 로 보내면 톰캣 500 화면이 떠버리므로,
				 * 여기서 직접 롤백하고 주문서 페이지로 되돌려 보낸다.
				 *
				 * 롤백하면 CHECKOUT / CHECKOUT_ITEM 이 그대로 살아있으므로
				 * 같은 checkoutNo 로 주문서 페이지를 다시 열 수 있다.
				 */
				conn.rollback();

				response.sendRedirect(
						request.getContextPath()
						+ "/order/payment?checkoutNo=" + checkoutNo
						+ "&stockFail="
						+ java.net.URLEncoder.encode(
								stockFail.productName, "UTF-8")
						+ "&stockLeft=" + stockFail.left
						);

				return;
			}
					
			dao.deleteCheckoutItems(
					conn,
					checkoutNo
					);
			dao.deleteCheckout(
					conn,
					checkoutNo,
					memberNo
					);
			
			
			HttpSession session =
			        request.getSession();

			Integer cartCheckoutNo =
			        (Integer) session.getAttribute(
			                "cartCheckoutNo"
			        );
			
			boolean cartOrder =
			        cartCheckoutNo != null
			        && cartCheckoutNo.intValue()
			                == checkoutNo;

			if (cartOrder) {

			    CartDAO cartDAO =
			            new CartDAO();

			    cartDAO.clearCart(
			            conn,
			            memberNo
			    );
			}

			conn.commit();
			
			if (cartOrder) {

				session.setAttribute(
				        "cartPreviewItems",
				        Collections.emptyList()
				);
			    
			    session.setAttribute(
			            "cartCount",
			            0
			    );

			    session.removeAttribute(
			            "cartCheckoutNo"
			    );
			}
			response.sendRedirect(
					request.getContextPath()
					+ "/order/complete?orderNo="
					+ orderNo
					);

		} catch (NumberFormatException e) {

			rollback(conn);

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 checkout 번호 또는 배송지 번호입니다."
					);


		} catch (Exception e) {

			rollback(conn);

			e.printStackTrace();

			throw new ServletException(
					"결제 처리 중 오류가 발생했습니다.",
					e
					);
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void rollback(
			Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
