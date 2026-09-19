package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.ProductListDAO;
import com.goodpang.dao.VendorActionLogDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 노출여부 변경 (소프트 삭제/복원).
 * 실제 행을 지우지 않고 PRODUCT.DISPLAY_YN만 바꿔서 판매자 상품 목록에서만 안 보이게 한다.
 * 기존 구매자의 주문내역(ORDER_DETAIL)은 PRODUCT를 그대로 참조하므로 영향이 없다.
 */
@WebServlet("/vendor/product/visibility")
public class VendorProductVisibilityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ProductListDAO productListDAO = new ProductListDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		String displayYn = request.getParameter("displayYn");

		if (!"Y".equals(displayYn) && !"N".equals(displayYn)) {
			response.sendRedirect(request.getContextPath() + "/vendor/product");
			return;
		}

		try {
			int productNo = Integer.parseInt(request.getParameter("productNo"));

			if (productListDAO.updateDisplayYn(productNo, loginSeller.getSellerNo(), displayYn)) {
				String actionType = "Y".equals(displayYn) ? "상품 노출" : "상품 숨김";
				new VendorActionLogDAO().log(loginSeller.getSellerNo(), actionType, "PRODUCT", productNo, null);
			}
		} catch (NumberFormatException e) {
			// productNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		response.sendRedirect(request.getContextPath() + "/vendor/product");
	}

}
