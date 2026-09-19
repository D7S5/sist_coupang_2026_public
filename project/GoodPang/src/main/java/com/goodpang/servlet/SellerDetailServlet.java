package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.SellerDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 판매자 상세정보 (관리자가 목록에서 판매자를 클릭했을 때).
 */
@WebServlet("/admin/seller-detail")
public class SellerDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sellerNoParam = request.getParameter("sellerNo");

		if (sellerNoParam == null || sellerNoParam.isBlank()) {
			response.sendRedirect(request.getContextPath() + "/admin/sellers");
			return;
		}

		int sellerNo;

		try {
			sellerNo = Integer.parseInt(sellerNoParam);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/sellers");
			return;
		}

		SellerDAO dao = new SellerDAO();
		SellerDTO seller = dao.findBySellerNo(sellerNo);

		if (seller == null) {
			response.sendRedirect(request.getContextPath() + "/admin/sellers");
			return;
		}

		request.setAttribute("seller", seller);

		request.getRequestDispatcher("/WEB-INF/views/admin-seller-detail.jsp")
			   .forward(request, response);
	}

}
