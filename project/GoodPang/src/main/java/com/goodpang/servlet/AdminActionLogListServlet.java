package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.AdminActionLogDAO;
import com.goodpang.dto.AdminActionLogDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 관리자 액션 로그 목록 조회.
 */
@WebServlet("/admin/action-logs")
public class AdminActionLogListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int PAGE_SIZE = 20;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int page = parsePage(request.getParameter("page"));

		AdminActionLogDAO dao = new AdminActionLogDAO();
		List<AdminActionLogDTO> logList = dao.findAll(page, PAGE_SIZE);
		int totalCount = dao.countAll();
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		request.setAttribute("logList", logList);
		request.setAttribute("page", page);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("totalCount", totalCount);

		request.getRequestDispatcher("/WEB-INF/views/admin-action-log-list.jsp")
			   .forward(request, response);
	}

	private int parsePage(String pageParam) {
		try {
			int page = Integer.parseInt(pageParam);
			return Math.max(1, page);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
