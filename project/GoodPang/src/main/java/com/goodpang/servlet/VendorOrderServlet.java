package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.goodpang.dao.VendorOrderListDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorOrderListDTO;
import com.goodpang.dto.VendorOrderStatSummaryDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 주문/배송 관리(vendor_orders.jsp) 화면 진입.
 */
@WebServlet("/vendor/order")
public class VendorOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int PAGE_SIZE = 20;

	private final VendorOrderListDAO orderListDAO = new VendorOrderListDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		String startDateParam = request.getParameter("startDate");
		String endDateParam = request.getParameter("endDate");
		String orderStatus = request.getParameter("orderStatus");
		String deliveryStatus = request.getParameter("deliveryStatus");
		String paymentStatus = request.getParameter("paymentStatus");

		LocalDate startDate = parseDate(startDateParam);
		LocalDate endDate = parseDate(endDateParam);
		int page = parsePage(request.getParameter("page"));

		List<VendorOrderListDTO> orderList = orderListDAO.findBySellerNo(
				loginSeller.getSellerNo(), startDate, endDate, orderStatus, deliveryStatus, paymentStatus,
				page, PAGE_SIZE);
		int totalCount = orderListDAO.countBySellerNo(
				loginSeller.getSellerNo(), startDate, endDate, orderStatus, deliveryStatus, paymentStatus);
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		VendorOrderStatSummaryDTO orderStat = orderListDAO.countStats(loginSeller.getSellerNo());

		request.setAttribute("orderList", orderList);
		request.setAttribute("orderStat", orderStat);
		request.setAttribute("page", page);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("totalCount", totalCount);

		// 검색폼에 입력값을 그대로 남겨두기 위해 원본 파라미터를 그대로 되돌려준다
		request.setAttribute("searchStartDate", startDateParam);
		request.setAttribute("searchEndDate", endDateParam);
		request.setAttribute("searchOrderStatus", orderStatus);
		request.setAttribute("searchDeliveryStatus", deliveryStatus);
		request.setAttribute("searchPaymentStatus", paymentStatus);

		request.getRequestDispatcher("/WEB-INF/views/vendor-order.jsp").forward(request, response);
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

	private int parsePage(String pageParam) {
		try {
			return Math.max(1, Integer.parseInt(pageParam));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
