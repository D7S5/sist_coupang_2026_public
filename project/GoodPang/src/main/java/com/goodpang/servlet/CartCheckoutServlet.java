package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.CheckoutDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/cart/checkout")
public class CartCheckoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CheckoutDAO checkoutDAO =
			new CheckoutDAO();

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// 결제부터 로그인 필요
		MemberDTO loginMember =
				LoginUtil.requireLogin(
						request,
						response
						);

		if (loginMember == null) {
			return;
		}

		String[] optionIdParams =
				request.getParameterValues("optionId");

		String[] quantityParams =
				request.getParameterValues("quantity");

		if (optionIdParams == null
				|| quantityParams == null
				|| optionIdParams.length == 0
				|| optionIdParams.length != quantityParams.length) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"구매할 상품이 없습니다."
					);

			return;
		}

		try {

			int memberNo =
					loginMember.getMemberNo();

			int checkoutNo =
					checkoutDAO.createCheckout(
							memberNo
							);
			
			// 장바구니에서 만든 checkout이라는 표시
			request.getSession()
		       .setAttribute(
		               "cartCheckoutNo",
		               checkoutNo
		       );

			int itemCount = 0;

			for (int i = 0;
					i < optionIdParams.length;
					i++) {

				int optionId =
						Integer.parseInt(
								optionIdParams[i]
								);

				int quantity =
						Integer.parseInt(
								quantityParams[i]
								);

				if (quantity < 1) {
					continue;
				}

				int result =
						checkoutDAO.addCheckoutItem(
								checkoutNo,
								optionId,
								quantity
								);

				if (result > 0) {
					itemCount++;
				}

			}

			if (itemCount == 0) {

				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"구매할 수 있는 상품이 없습니다."
						);

				return;
			}

			checkoutDAO.updateCheckoutAmountv2(
					checkoutNo
					);

			response.sendRedirect(
					request.getContextPath()
					+ "/order/payment?checkoutNo="
					+ checkoutNo
					);

		} catch (NumberFormatException e) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 상품 정보입니다."
					);

		} catch (Exception e) {

			e.printStackTrace();

			throw new ServletException(
					"장바구니 결제 준비 중 오류가 발생했습니다.",
					e
					);

		}

	}

}