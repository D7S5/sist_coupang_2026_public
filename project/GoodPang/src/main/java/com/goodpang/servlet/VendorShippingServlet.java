package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorShippingDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorShippingDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 출고/운송장 관리(vendor-shipping.jsp) 화면 진입.
 * '결제완료' 상태인 출고 대기 주문만 모아서 보여주고, 출고 지연 임박 건을 잡아낸다.
 * 실제 송장 등록/출고 처리는 여기서 새로 만들지 않고 기존 /vendor/order/ship 을 그대로 씀.
 */
@WebServlet("/vendor/shipping")
public class VendorShippingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorShippingDAO shippingDAO = new VendorShippingDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		List<VendorShippingDTO> shippingList = shippingDAO.findWaitingBySellerNo(loginSeller.getSellerNo());

		long delayedCount = shippingList.stream().filter(VendorShippingDTO::isDelayed).count();

		request.setAttribute("shippingList", shippingList);
		request.setAttribute("delayedCount", delayedCount);

		request.getRequestDispatcher("/WEB-INF/views/vendor-shipping.jsp").forward(request, response);
	}

}
