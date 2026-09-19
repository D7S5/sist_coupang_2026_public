package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.NoticeDAO;
import com.goodpang.dto.NoticeDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 판매자센터 공지사항 상세(vendor-notice-detail.jsp) — 조회 전용.
 */
@WebServlet("/vendor/notice/detail")
public class VendorNoticeDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int noticeNo;

		try {
			noticeNo = Integer.parseInt(request.getParameter("noticeNo"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/vendor/notice");
			return;
		}

		NoticeDTO notice = new NoticeDAO().findByNoticeNo(noticeNo);

		if (notice == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/notice");
			return;
		}

		request.setAttribute("notice", notice);

		request.getRequestDispatcher("/WEB-INF/views/vendor-notice-detail.jsp")
			   .forward(request, response);
	}

}
