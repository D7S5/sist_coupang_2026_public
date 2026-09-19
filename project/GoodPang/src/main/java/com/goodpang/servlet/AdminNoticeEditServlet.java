package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AdminActionLogDAO;
import com.goodpang.dao.NoticeDAO;
import com.goodpang.dto.NoticeDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 공지사항 수정 (관리자용).
 */
@WebServlet("/admin/notice/edit")
public class AdminNoticeEditServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final NoticeDAO noticeDAO = new NoticeDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int noticeNo;

		try {
			noticeNo = Integer.parseInt(request.getParameter("noticeNo"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/notices");
			return;
		}

		NoticeDTO notice = noticeDAO.findByNoticeNo(noticeNo);

		if (notice == null) {
			response.sendRedirect(request.getContextPath() + "/admin/notices");
			return;
		}

		request.setAttribute("notice", notice);

		request.getRequestDispatcher("/WEB-INF/views/admin-notice-edit.jsp")
			   .forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String noticeType = request.getParameter("noticeType");

		int noticeNo;

		try {
			noticeNo = Integer.parseInt(request.getParameter("noticeNo"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/notices");
			return;
		}

		boolean validType = "공지".equals(noticeType) || "안내".equals(noticeType);

		if (title == null || title.isBlank() || content == null || content.isBlank() || !validType) {
			request.setAttribute("error", "제목, 내용, 구분을 모두 입력해주세요.");
			request.setAttribute("notice", noticeDAO.findByNoticeNo(noticeNo));
			request.getRequestDispatcher("/WEB-INF/views/admin-notice-edit.jsp")
				   .forward(request, response);
			return;
		}

		if (noticeDAO.update(noticeNo, title.trim(), content, noticeType)) {
			HttpSession session = request.getSession(false);
			Integer adminNo = (session != null) ? (Integer) session.getAttribute("adminNo") : null;
			if (adminNo != null) {
				new AdminActionLogDAO().log(adminNo, "공지 수정", "NOTICE", noticeNo, null);
			}
		}

		response.sendRedirect(request.getContextPath() + "/admin/notices");
	}

}
