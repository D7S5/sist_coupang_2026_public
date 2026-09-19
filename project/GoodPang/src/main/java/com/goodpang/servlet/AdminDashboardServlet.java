package com.goodpang.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 관리자 화면 진입점. admin/products, admin/sellers 등 흩어져 있던 관리자 기능들의
 * 링크를 한 곳에 모아서, URL을 직접 입력하지 않고도 이동할 수 있게 한다.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp")
			   .forward(request, response);
	}

}
