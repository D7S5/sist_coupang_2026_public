package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.AdminDeliveryDAO;
import com.goodpang.dto.AdminDeliveryDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 배송중인 상품 목록 조회 (관리자가 배송완료 처리를 하기 위한 화면).
 */
@WebServlet("/admin/deliveries")
public class AdminDeliveryListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		AdminDeliveryDAO dao = new AdminDeliveryDAO();
		List<AdminDeliveryDTO> deliveryList = dao.findShipping();

		request.setAttribute("deliveryList", deliveryList);

		request.getRequestDispatcher("/WEB-INF/views/admin-delivery-list.jsp")
			   .forward(request, response);
	}

}
