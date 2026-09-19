package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.VendorActionLogDAO;
import com.goodpang.dao.VendorOrderListDAO;
import com.goodpang.dao.VendorOrderListDAO.ShipResult;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 주문/배송 관리 - '결제완료' 주문을 '배송중'으로 전환.
 */
@WebServlet("/vendor/order/ship")
public class VendorOrderShipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final VendorOrderListDAO orderListDAO = new VendorOrderListDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		String invoiceNo = request.getParameter("invoiceNo");
		String redirectTo = resolveRedirectTo(request);

		if (invoiceNo == null || invoiceNo.isBlank()) {
			response.sendRedirect(request.getContextPath() + redirectTo);
			return;
		}

		ShipResult result = ShipResult.FAILED;

		try {
			int orderNo = Integer.parseInt(request.getParameter("orderNo"));
			result = orderListDAO.shipOrder(orderNo, loginSeller.getSellerNo(), invoiceNo.trim());

			if (result == ShipResult.SUCCESS) {
				new VendorActionLogDAO().log(loginSeller.getSellerNo(), "배송 처리", "ORDERS", orderNo, "송장번호 " + invoiceNo.trim());
			}
		} catch (NumberFormatException e) {
			// orderNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		String shipErrorParam = (result == ShipResult.INVOICE_DUPLICATE) ? "?shipError=duplicateInvoice" : "";

		response.sendRedirect(request.getContextPath() + redirectTo + shipErrorParam);
	}

	// "주문 목록"/"출고·운송장 관리" 등 여러 화면에서 같은 출고 처리 액션을 재사용하기 위해,
	// 처리 후 돌아갈 화면을 폼에서 지정할 수 있게 함. 이 서블릿이 관리하는 두 경로 외에는
	// 무시하고 기존 기본값(/vendor/order)으로 보낸다 - 오픈 리다이렉트 방지.
	private String resolveRedirectTo(HttpServletRequest request) {

		String redirectTo = request.getParameter("redirectTo");

		if ("/vendor/shipping".equals(redirectTo)) {
			return redirectTo;
		}

		return "/vendor/order";
	}

}
