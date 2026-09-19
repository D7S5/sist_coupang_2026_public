package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.VendorActionLogDAO;
import com.goodpang.dao.VendorProductOptionDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 옵션 관리 - 옵션 하나의 판매가/정상가/재고수량/상태 수정.
 */
@WebServlet("/vendor/product/option/update")
public class VendorProductOptionUpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorProductOptionDAO optionDAO = new VendorProductOptionDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		try {
			int optionId = Integer.parseInt(request.getParameter("optionId"));
			int price = Integer.parseInt(request.getParameter("price"));
			int quantity = Integer.parseInt(request.getParameter("quantity"));
			String status = request.getParameter("status");

			String normalPriceParam = request.getParameter("normalPrice");
			Integer normalPrice = (normalPriceParam != null && !normalPriceParam.isBlank())
					? Integer.parseInt(normalPriceParam)
					: null;

			if (!"Y".equals(status) && !"N".equals(status)) {
				status = "Y";
			}

			if (optionDAO.updateOption(optionId, loginSeller.getSellerNo(), price, normalPrice, quantity, status)) {
				String detail = "판매가 " + price + "원, 재고 " + quantity + "개, 상태 " + ("Y".equals(status) ? "정상" : "품절");
				new VendorActionLogDAO().log(loginSeller.getSellerNo(), "옵션 수정", "PRODUCT_OPTION", optionId, detail);
			}

		} catch (NumberFormatException e) {
			// 값이 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		response.sendRedirect(request.getContextPath() + "/vendor/product/options");
	}

}
