package com.goodpang.servlet;

import java.io.IOException;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.CartUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart/delete")
public class CartDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CartDAO cartDAO =
			new CartDAO();

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session =
				request.getSession();

		MemberDTO loginMember =
				(MemberDTO)
				session.getAttribute("loginMember");

		String optionIdParam =
				request.getParameter("optionId");

		if (optionIdParam == null
				|| optionIdParam.isBlank()) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"삭제할 상품 정보가 없습니다."
					);

			return;
		}

		int optionId;

		try {

			optionId =
					Integer.parseInt(optionIdParam);

		} catch (NumberFormatException e) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 상품 정보입니다."
					);
			return;
		}
		
		if (loginMember != null) {

			int memberNo =
					loginMember.getMemberNo();
			
			cartDAO.deleteCart(
					memberNo,
					optionId
					);

			// 헤더 장바구니 상품 종류 수 갱신
			int cartCount =
					cartDAO.getCartCount(
							memberNo
							);
			
			CartUtil.refreshCartSession(
					request,
					memberNo
			);
			session.setAttribute(
					"cartCount",
					cartCount
					);
		}
		else {

			/*
			 * @SuppressWarnings("unchecked") Map<Integer, Integer> guestCart =
			 * (Map<Integer, Integer>) session.getAttribute("guestCart");
			 * 
			 * if (guestCart != null) {
			 * 
			 * guestCart.remove(optionId);
			 * 
			 * // 상품 종류 수 session.setAttribute( "cartCount", guestCart.size() );
			 * 
			 * }
			 */
			@SuppressWarnings("unchecked")
			Map<Integer, Integer> guestCart =
					(Map<Integer, Integer>) session.getAttribute("guestCart");

			if (guestCart != null) {
				guestCart.remove(optionId);
				
				CartUtil.refreshGuestCartSession(request, guestCart);
			}
		}
		// 다시 장바구니 페이지로 이동
		response.sendRedirect(
				request.getContextPath()
				+ "/cart"
				);
	}
}