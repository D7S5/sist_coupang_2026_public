package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AdminActionLogDAO;
import com.goodpang.dao.NoticeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 공지사항 등록 (관리자용). AdminAuthFilter가 /admin/* 전체를 로그인 가드하고 있어서
 * 여기서는 등록 처리에만 집중한다.
 */
@WebServlet("/admin/notice/write")
public class AdminNoticeWriteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/admin-notice-write.jsp")
			   .forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String noticeType = request.getParameter("noticeType");

		HttpSession session = request.getSession(false);
		Integer adminNo = (session != null) ? (Integer) session.getAttribute("adminNo") : null;

		boolean validType = "공지".equals(noticeType) || "안내".equals(noticeType);

		if (title == null || title.isBlank() || content == null || content.isBlank() || !validType || adminNo == null) {
			request.setAttribute("error", "제목, 내용, 구분을 모두 입력해주세요.");
			request.getRequestDispatcher("/WEB-INF/views/admin-notice-write.jsp")
				   .forward(request, response);
			return;
		}

		NoticeDAO dao = new NoticeDAO();
		int noticeNo = dao.insert(title.trim(), content, noticeType, adminNo);

		if (noticeNo > 0) {
			new AdminActionLogDAO().log(adminNo, "공지 등록", "NOTICE", noticeNo, null);
		}

		response.sendRedirect(request.getContextPath() + "/admin/notices");
	}

}
