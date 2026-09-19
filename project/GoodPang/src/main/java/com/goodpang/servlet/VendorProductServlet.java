package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.ProductListDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorProductListDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 목록(vendor_products.jsp) 화면 진입.
 */
@WebServlet("/vendor/product")
public class VendorProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int PAGE_SIZE = 20;

	private final ProductListDAO productListDAO = new ProductListDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		boolean hiddenView = "hidden".equals(request.getParameter("view"));
		int page = parsePage(request.getParameter("page"));

		int sellerNo = loginSeller.getSellerNo();
		String displayYn = hiddenView ? "N" : "Y";

		List<VendorProductListDTO> productList = hiddenView
				? productListDAO.findHiddenBySellerNo(sellerNo, page, PAGE_SIZE)
				: productListDAO.findBySellerNo(sellerNo, page, PAGE_SIZE);

		// 통계 카드(전체/판매중/품절/판매중지/승인대기)는 현재 페이지가 아니라 이 탭(노출/숨김)
		// 전체 상품 기준이어야 하므로, 화면에 뿌리는 productList와 별개로 집계 쿼리를 따로 돌린다.
		int totalCount = productListDAO.countBySellerNo(sellerNo, displayYn, null);
		int saleCount = productListDAO.countBySellerNo(sellerNo, displayYn, "판매 중");
		int soldOutCount = productListDAO.countBySellerNo(sellerNo, displayYn, "품절");
		int stoppedCount = productListDAO.countBySellerNo(sellerNo, displayYn, "판매 중지");
		int pendingCount = productListDAO.countBySellerNo(sellerNo, displayYn, "승인 대기");
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		request.setAttribute("productList", productList);
		request.setAttribute("hiddenView", hiddenView);
		request.setAttribute("page", page);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("saleCount", saleCount);
		request.setAttribute("soldOutCount", soldOutCount);
		request.setAttribute("stoppedCount", stoppedCount);
		request.setAttribute("pendingCount", pendingCount);

		request.getRequestDispatcher("/WEB-INF/views/vendor-product.jsp").forward(request, response);
	}

	private int parsePage(String pageParam) {
		try {
			return Math.max(1, Integer.parseInt(pageParam));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
