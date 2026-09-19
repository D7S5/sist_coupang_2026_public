package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.VendorProductDetailDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorProductDetailDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 상세정보 (상품 목록에서 상품을 클릭했을 때).
 */
@WebServlet("/vendor/product/detail")
public class VendorProductDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final VendorProductDetailDAO productDetailDAO = new VendorProductDetailDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		int productNo;

		try {
			productNo = Integer.parseInt(request.getParameter("productNo"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/vendor/product");
			return;
		}

		VendorProductDetailDTO product = productDetailDAO.findByProductNo(productNo, loginSeller.getSellerNo());

		if (product == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/product");
			return;
		}

		request.setAttribute("product", product);

		request.getRequestDispatcher("/WEB-INF/views/vendor-product-detail.jsp").forward(request, response);
	}

}
