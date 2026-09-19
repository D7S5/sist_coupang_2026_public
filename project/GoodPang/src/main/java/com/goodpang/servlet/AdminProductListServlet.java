package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.AdminProductDAO;
import com.goodpang.dto.AdminProductApprovalDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 상품 목록 조회 (관리자가 승인 대기 상품을 확인하기 위한 화면).
 */
@WebServlet("/admin/products")
public class AdminProductListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		AdminProductDAO dao = new AdminProductDAO();
		List<AdminProductApprovalDTO> productList = dao.findAll();

		request.setAttribute("productList", productList);

		request.getRequestDispatcher("/WEB-INF/views/admin-product-list.jsp")
			   .forward(request, response);
	}

}
