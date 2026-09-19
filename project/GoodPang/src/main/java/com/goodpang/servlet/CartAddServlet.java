package com.goodpang.servlet;

import java.io.IOException;
import java.util.HashMap;
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

@WebServlet("/cart/add")
public class CartAddServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CartDAO cartDAO = new CartDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		int optionId;
		int quantity;

		try {
			optionId = Integer.parseInt(request.getParameter("optionId"));
			quantity = Integer.parseInt(request.getParameter("quantity"));
			
			System.out.println("optionId = " + optionId);
			System.out.println("quantity = " + quantity);
		} catch (Exception e) {
			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 상품 정보입니다."
			);
			return;
		}

		if (quantity < 1) {
			quantity = 1;
		}

		HttpSession session = request.getSession();
		MemberDTO loginMember =
				(MemberDTO) session.getAttribute("loginMember");

		int cartCount;

		// 로그인 사용자
		if (loginMember != null) {
			int memberNo = loginMember.getMemberNo();

			cartDAO.addCart(memberNo, optionId, quantity);
			CartUtil.refreshCartSession(request, memberNo);

			cartCount =
					(Integer) session.getAttribute("cartCount");
		}

		// 비로그인 사용자
		else {
			@SuppressWarnings("unchecked")
			Map<Integer, Integer> guestCart =
					(Map<Integer, Integer>) session.getAttribute("guestCart");

			if (guestCart == null) {
				guestCart = new HashMap<>();
				session.setAttribute("guestCart", guestCart);
			}

			guestCart.merge(optionId, quantity, Integer::sum);
			

		    session.setAttribute(
		            "guestCart",
		            guestCart
		    );

			CartUtil.refreshGuestCartSession(
					request,
					guestCart
			);

			cartCount = guestCart.size();
		}

		boolean ajax =
				"XMLHttpRequest".equals(
						request.getHeader("X-Requested-With")
				);

		if (ajax) {
			response.setContentType(
					"application/json; charset=UTF-8"
			);

			response.getWriter().write(
					"{\"success\":true,\"cartCount\":" + cartCount + "}"
			);

			return;
		}

		response.sendRedirect(
				request.getContextPath() + "/cart"
		);
	}
}