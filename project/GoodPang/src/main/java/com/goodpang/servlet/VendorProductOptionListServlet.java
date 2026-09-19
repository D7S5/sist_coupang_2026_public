package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.goodpang.dao.VendorProductOptionDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorProductOptionDTO;
import com.goodpang.dto.VendorProductOptionGroupDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 옵션 관리(vendor-product-options.jsp) 화면 진입.
 */
@WebServlet("/vendor/product/options")
public class VendorProductOptionListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorProductOptionDAO optionDAO = new VendorProductOptionDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		Integer productNo = parseProductNo(request.getParameter("productNo"));

		List<VendorProductOptionDTO> optionList = optionDAO.findBySellerNo(loginSeller.getSellerNo(), productNo);
		List<VendorProductOptionDTO> productFilterOptions = optionDAO.findDistinctProductsBySellerNo(loginSeller.getSellerNo());

		request.setAttribute("optionList", optionList);
		request.setAttribute("groupedOptions", groupByProduct(optionList));
		request.setAttribute("productFilterOptions", productFilterOptions);
		request.setAttribute("selectedProductNo", productNo);

		request.getRequestDispatcher("/WEB-INF/views/vendor-product-options.jsp").forward(request, response);
	}

	private Integer parseProductNo(String productNoParam) {
		if (productNoParam == null || productNoParam.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(productNoParam.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// 상품번호, 옵션번호 순으로 이미 정렬된 optionList를 상품 단위로 묶는다 (테이블 rowspan 렌더링용)
	private List<VendorProductOptionGroupDTO> groupByProduct(List<VendorProductOptionDTO> optionList) {

		Map<Integer, VendorProductOptionGroupDTO> groupsByProductNo = new LinkedHashMap<>();

		for (VendorProductOptionDTO option : optionList) {
			VendorProductOptionGroupDTO group = groupsByProductNo.computeIfAbsent(option.getProductNo(), no -> {
				VendorProductOptionGroupDTO g = new VendorProductOptionGroupDTO();
				g.setProductNo(option.getProductNo());
				g.setProductName(option.getProductName());
				g.setThumbnailUrl(option.getThumbnailUrl());
				g.setOptions(new ArrayList<>());
				return g;
			});
			group.getOptions().add(option);
		}

		return new ArrayList<>(groupsByProductNo.values());
	}

}
