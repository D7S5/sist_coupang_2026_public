package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.VendorOrderDetailDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorOrderDetailDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 주문 상세정보 (주문 목록에서 "상세보기"를 눌렀을 때).
 */
@WebServlet("/vendor/order/detail")
public class VendorOrderDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final VendorOrderDetailDAO orderDetailDAO = new VendorOrderDetailDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		int orderNo;

		try {
			orderNo = Integer.parseInt(request.getParameter("orderNo"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/vendor/order");
			return;
		}

		VendorOrderDetailDTO order = orderDetailDAO.findByOrderNo(orderNo, loginSeller.getSellerNo());

		if (order == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/order");
			return;
		}

		request.setAttribute("order", order);

		request.getRequestDispatcher("/WEB-INF/views/vendor-order-detail.jsp").forward(request, response);
	}

}
