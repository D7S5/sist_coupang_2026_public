package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorDeliveryDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorDeliveryDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 배송 관리(vendor-delivery.jsp) 화면 진입.
 * 배송중인 주문을 모니터링하고 지연 건을 잡아내는 용도 - 배송완료 처리는 여기서 하지 않음
 * (실제 배송완료는 배송기사가 처리해야 할 일이라, 지금은 관리자가 /admin/deliveries 에서 대행 중).
 */
@WebServlet("/vendor/delivery")
public class VendorDeliveryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorDeliveryDAO deliveryDAO = new VendorDeliveryDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		List<VendorDeliveryDTO> deliveryList = deliveryDAO.findShippingBySellerNo(loginSeller.getSellerNo());

		long delayedCount = deliveryList.stream().filter(VendorDeliveryDTO::isDelayed).count();

		request.setAttribute("deliveryList", deliveryList);
		request.setAttribute("delayedCount", delayedCount);

		request.getRequestDispatcher("/WEB-INF/views/vendor-delivery.jsp").forward(request, response);
	}

}
