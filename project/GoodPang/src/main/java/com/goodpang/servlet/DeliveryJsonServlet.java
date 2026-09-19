package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import com.goodpang.dao.AdminDeliveryDAO;
import com.goodpang.dto.AdminDeliveryDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 배송중인 상품 목록(admin-delivery-list.jsp와 동일한 데이터)을 JSON으로 제공.
 * /admin 밖에 있어서 AdminAuthFilter를 타지 않고, 로그인 없이 누구나 조회 가능하다.
 */
@WebServlet("/deliveries/json")
public class DeliveryJsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Gson gson = new Gson();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		AdminDeliveryDAO dao = new AdminDeliveryDAO();
		List<AdminDeliveryDTO> deliveryList = dao.findShipping();

		for (AdminDeliveryDTO delivery : deliveryList) {
			delivery.setBuyerName(maskName(delivery.getBuyerName()));
			delivery.setBuyerPhone(maskPhone(delivery.getBuyerPhone()));
		}

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(gson.toJson(deliveryList));
	}

	// "홍길동" -> "홍*동", "이연" -> "이*" (ReviewDAO.maskName()과 동일한 규칙)
	private String maskName(String name) {
		if (name == null || name.length() <= 1) {
			return name;
		}
		if (name.length() == 2) {
			return name.charAt(0) + "*";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name.charAt(0));
		for (int i = 1; i < name.length() - 1; i++) {
			sb.append("*");
		}
		sb.append(name.charAt(name.length() - 1));
		return sb.toString();
	}

	// 로그인 없이 열리는 API라 뒷자리만 마스킹해서 노출 ("010-1234-5678" -> "010-1234-****")
	private String maskPhone(String phone) {
		if (phone == null || phone.isBlank()) {
			return phone;
		}
		int maskLength = Math.min(4, phone.length());
		String visible = phone.substring(0, phone.length() - maskLength);
		return visible + "*".repeat(maskLength);
	}

}
