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
 * 판매자센터 상품 판매중지/판매재개 토글.
 * '승인 대기'/'품절' 상태는 건드리지 않고, '판매 중' <-> '판매 중지'만 전환한다.
 */
@WebServlet("/vendor/product/status")
public class VendorProductSaleStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ProductListDAO productListDAO = new ProductListDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		String saleStatus = request.getParameter("saleStatus");

		if (!"판매 중".equals(saleStatus) && !"판매 중지".equals(saleStatus)) {
			response.sendRedirect(request.getContextPath() + "/vendor/product");
			return;
		}

		try {
			int productNo = Integer.parseInt(request.getParameter("productNo"));

			if (productListDAO.updateSaleStatus(productNo, loginSeller.getSellerNo(), saleStatus)) {
				String actionType = "판매 중".equals(saleStatus) ? "판매 재개" : "판매 중지";
				new VendorActionLogDAO().log(loginSeller.getSellerNo(), actionType, "PRODUCT", productNo, null);
			}
		} catch (NumberFormatException e) {
			// productNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		response.sendRedirect(request.getContextPath() + "/vendor/product");
	}

}
