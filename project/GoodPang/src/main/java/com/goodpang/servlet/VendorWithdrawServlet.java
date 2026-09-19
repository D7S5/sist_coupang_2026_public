package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.ProductListDAO;
import com.goodpang.dao.SellerDAO;
import com.goodpang.dao.VendorActionLogDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자 자진 탈퇴. 비밀번호 재확인 후 처리한다.
 * 승인 대기 중인 주문/정산 미지급 여부는 확인하지 않음(2026-09-05 확정, 지금 단계에서는 막지 않기로 함).
 * 탈퇴 시 로그인만 막는 게 아니라, 이 판매자의 상품을 전부 숨김 처리해서 고객 화면에서도 즉시 사라지게 한다.
 */
@WebServlet("/vendor/withdraw")
public class VendorWithdrawServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		String password = request.getParameter("password");

		if (password == null || !BCrypt.checkpw(password, loginSeller.getSellerPw())) {
			request.setAttribute("error", "비밀번호가 일치하지 않습니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-business-info.jsp")
					.forward(request, response);
			return;
		}

		int sellerNo = loginSeller.getSellerNo();

		new SellerDAO().updateApprovalStatus(sellerNo, "탈퇴", null);
		new ProductListDAO().hideAllBySeller(sellerNo);
		new VendorActionLogDAO().log(sellerNo, "판매자 탈퇴", "SELLER", sellerNo, null);

		session.invalidate();

		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}

}
