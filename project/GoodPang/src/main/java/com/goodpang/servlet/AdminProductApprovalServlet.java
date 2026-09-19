package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AdminActionLogDAO;
import com.goodpang.dao.AdminProductDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 상품 승인/반려 처리 (관리자 계정이 아직 없어서, 목록 화면에서 바로 처리).
 * PRODUCT.SALE_STATUS는 CHECK 제약상 '판매 중'/'품절'/'판매 중지'/'승인 대기'만 허용되고
 * 별도 '반려' 상태나 반려사유 컬럼이 없어서, 반려는 '판매 중지'로 전환하는 것으로 대신한다.
 */
@WebServlet("/admin/product-approve")
public class AdminProductApprovalServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String productNoParam = request.getParameter("productNo");
		String action = request.getParameter("action");

		int productNo;

		try {
			productNo = Integer.parseInt(productNoParam);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/products");
			return;
		}

		AdminProductDAO dao = new AdminProductDAO();

		HttpSession session = request.getSession(false);
		Integer adminNo = (session != null) ? (Integer) session.getAttribute("adminNo") : null;

		if ("approve".equals(action)) {
			dao.updateApprovalStatus(productNo, "판매 중");
			if (adminNo != null) {
				new AdminActionLogDAO().log(adminNo, "상품 승인", "PRODUCT", productNo, null);
			}
		} else if ("reject".equals(action)) {
			dao.updateApprovalStatus(productNo, "판매 중지");
			if (adminNo != null) {
				new AdminActionLogDAO().log(adminNo, "상품 반려", "PRODUCT", productNo, null);
			}
		}

		response.sendRedirect(request.getContextPath() + "/admin/products");
	}

}
