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
 * 공지사항 삭제 (관리자용).
 */
@WebServlet("/admin/notice/delete")
public class AdminNoticeDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			int noticeNo = Integer.parseInt(request.getParameter("noticeNo"));

			if (new NoticeDAO().delete(noticeNo)) {
				HttpSession session = request.getSession(false);
				Integer adminNo = (session != null) ? (Integer) session.getAttribute("adminNo") : null;
				if (adminNo != null) {
					new AdminActionLogDAO().log(adminNo, "공지 삭제", "NOTICE", noticeNo, null);
				}
			}
		} catch (NumberFormatException e) {
			// noticeNo가 없거나 숫자가 아니면 아무 것도 지우지 않고 목록으로 돌려보낸다.
		}

		response.sendRedirect(request.getContextPath() + "/admin/notices");
	}

}
