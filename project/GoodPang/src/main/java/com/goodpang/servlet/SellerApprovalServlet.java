package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AdminActionLogDAO;
import com.goodpang.dao.SellerDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자 입점심사 승인/반려 + 계정 정지/정지 해제 처리 (관리자 계정이 아직 없어서, 상세 페이지에서 바로 처리).
 */
@WebServlet("/admin/seller-approve")
public class SellerApprovalServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String sellerNoParam = request.getParameter("sellerNo");
		String action = request.getParameter("action");

		int sellerNo;

		try {
			sellerNo = Integer.parseInt(sellerNoParam);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/sellers");
			return;
		}

		SellerDAO dao = new SellerDAO();
		AdminActionLogDAO logDao = new AdminActionLogDAO();

		HttpSession session = request.getSession(false);
		Integer adminNo = (session != null) ? (Integer) session.getAttribute("adminNo") : null;

		if ("approve".equals(action)) {

			dao.updateApprovalStatus(sellerNo, "승인", null);
			if (adminNo != null) {
				logDao.log(adminNo, "판매자 승인", "SELLER", sellerNo, null);
			}

		} else if ("reject".equals(action)) {

			String rejectReason = request.getParameter("rejectReason");

			if (rejectReason == null || rejectReason.isBlank()) {
				rejectReason = "사유가 입력되지 않았습니다.";
			}

			dao.updateApprovalStatus(sellerNo, "반려", rejectReason);
			if (adminNo != null) {
				logDao.log(adminNo, "판매자 반려", "SELLER", sellerNo, rejectReason);
			}

		} else if ("suspend".equals(action)) {

			// REJECT_REASON 컬럼을 정지 사유도 같이 담는 용도로 재사용 (반려/정지 상태일 때만 값이 있음)
			String suspendReason = request.getParameter("suspendReason");

			if (suspendReason == null || suspendReason.isBlank()) {
				suspendReason = "사유가 입력되지 않았습니다.";
			}

			dao.updateApprovalStatus(sellerNo, "정지", suspendReason);
			if (adminNo != null) {
				logDao.log(adminNo, "판매자 정지", "SELLER", sellerNo, suspendReason);
			}

		} else if ("reactivate".equals(action)) {

			dao.updateApprovalStatus(sellerNo, "승인", null);
			if (adminNo != null) {
				logDao.log(adminNo, "판매자 정지해제", "SELLER", sellerNo, null);
			}
		}

		response.sendRedirect(request.getContextPath() + "/admin/seller-detail?sellerNo=" + sellerNo);
	}

}
