package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.goodpang.dao.VendorActionLogDAO;
import com.goodpang.dto.VendorActionLogDTO;
import com.goodpang.dto.VendorActionLogSearchDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 판매자 액션 로그 목록 조회 (관리자 전용 - 판매자센터에는 이 화면이 없음).
 */
@WebServlet("/admin/vendor-action-logs")
public class AdminVendorActionLogListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int PAGE_SIZE = 20;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int page = parsePage(request.getParameter("page"));

		VendorActionLogSearchDTO search = new VendorActionLogSearchDTO();
		search.setStoreName(request.getParameter("storeName"));
		search.setActionType(request.getParameter("actionType"));
		search.setTargetType(request.getParameter("targetType"));
		search.setStartDate(parseDate(request.getParameter("startDate")));
		search.setEndDate(parseDate(request.getParameter("endDate")));

		VendorActionLogDAO dao = new VendorActionLogDAO();
		List<VendorActionLogDTO> logList = dao.findAll(page, PAGE_SIZE, search);
		int totalCount = dao.countAll(search);
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		request.setAttribute("logList", logList);
		request.setAttribute("page", page);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("totalCount", totalCount);

		// 검색폼에 입력값을 그대로 남겨두고, 페이지네이션 링크에도 검색조건을 이어붙이기 위함
		request.setAttribute("searchStoreName", search.getStoreName());
		request.setAttribute("searchActionType", search.getActionType());
		request.setAttribute("searchTargetType", search.getTargetType());
		request.setAttribute("searchStartDate", request.getParameter("startDate"));
		request.setAttribute("searchEndDate", request.getParameter("endDate"));

		request.getRequestDispatcher("/WEB-INF/views/admin-vendor-action-log-list.jsp")
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

	private LocalDate parseDate(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(value);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

}
