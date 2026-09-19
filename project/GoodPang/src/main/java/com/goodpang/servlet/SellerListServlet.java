package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.SellerDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 판매자 목록 조회 (관리자가 입점심사 상태를 확인하기 위한 화면).
 */
@WebServlet("/admin/sellers")
public class SellerListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		SellerDAO dao = new SellerDAO();
		List<SellerDTO> sellerList = dao.findAll();

		request.setAttribute("sellerList", sellerList);

		request.getRequestDispatcher("/WEB-INF/views/admin-seller-list.jsp")
			   .forward(request, response);
	}

}
