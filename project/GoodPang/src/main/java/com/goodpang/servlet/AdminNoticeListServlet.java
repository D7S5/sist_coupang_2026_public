package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.NoticeDAO;
import com.goodpang.dto.NoticeDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 공지사항 목록 조회 (관리자용).
 */
@WebServlet("/admin/notices")
public class AdminNoticeListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int PAGE_SIZE = 20;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int page = parsePage(request.getParameter("page"));

		NoticeDAO dao = new NoticeDAO();
		List<NoticeDTO> noticeList = dao.findAll(page, PAGE_SIZE);
		int totalCount = dao.countAll();
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		request.setAttribute("noticeList", noticeList);
		request.setAttribute("page", page);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("totalCount", totalCount);

		request.getRequestDispatcher("/WEB-INF/views/admin-notice-list.jsp")
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
