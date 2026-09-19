package com.goodpang.servlet;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.goodpang.dao.AdminDeliveryDAO;
import com.goodpang.dao.AdminDeliveryDAO.CompleteResult;
import com.goodpang.dao.VendorActionLogDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 배송완료 처리를 JSON으로 응답. deliveries/json과 짝을 이루는 API로,
 * /admin 밖에 있어서 AdminAuthFilter를 타지 않고 로그인 없이 호출 가능하다.
 */
@WebServlet("/delivery-complete/json")
public class DeliveryCompleteJsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Gson gson = new Gson();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json; charset=UTF-8");

		JsonObject result = new JsonObject();

		try {
			int deliveryNo = Integer.parseInt(request.getParameter("deliveryNo"));

			AdminDeliveryDAO dao = new AdminDeliveryDAO();
			CompleteResult completeResult = dao.completeDelivery(deliveryNo);
			boolean success = completeResult != null;

			result.addProperty("success", success);
			if (!success) {
				result.addProperty("message", "이미 처리되었거나 존재하지 않는 배송번호입니다.");
			} else {
				VendorActionLogDAO vendorActionLogDAO = new VendorActionLogDAO();
				for (int sellerNo : completeResult.getSellerNos()) {
					vendorActionLogDAO.log(sellerNo, "배송 완료", "ORDERS", completeResult.getOrderNo(), null);
				}
			}

		} catch (NumberFormatException e) {
			result.addProperty("success", false);
			result.addProperty("message", "deliveryNo가 올바르지 않습니다.");
		}

		response.getWriter().write(gson.toJson(result));
	}

}
