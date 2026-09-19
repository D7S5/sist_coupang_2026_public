package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.CheckoutDAO;
import com.goodpang.dao.OrderDAO;
import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.CheckoutDTO;
import com.goodpang.dto.CheckoutItemDTO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/payment")
// 예:
// http://localhost:8080/GoodPang/order/payment?checkoutNo=1

public class OrderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final OrderDAO orderDAO = new OrderDAO();
	private final CheckoutDAO checkoutDAO = new CheckoutDAO();

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		MemberDTO member =
				LoginUtil.requireLogin(
						request,
						response
						);

		if (member == null) {
			return;
		}

		int memberNo =
				member.getMemberNo();

		String checkoutNoParam = request.getParameter("checkoutNo");

		if (checkoutNoParam == null
				|| checkoutNoParam.isBlank()) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"결제 정보가 없습니다." 
					);

			return;
		}
		int checkoutNo;
		try {

			checkoutNo =
					Integer.parseInt(checkoutNoParam);

		} catch (NumberFormatException e) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 결제번호입니다."
					);

			return;
		}

		CheckoutDTO checkout =
				checkoutDAO.getCheckout(
						checkoutNo,
						memberNo
						);

		if (checkout == null) {

			response.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					"결제 정보를 찾을 수 없습니다."
					);

			return;
		}


		List<CheckoutItemDTO> checkoutItems =
		        checkoutDAO.getCheckoutItemsPRODUCT(
		                checkoutNo
		        );

		AddressDTO address =
				orderDAO.getAddress(
						memberNo
						);

		List<AddressDTO> addressList =
				orderDAO.getAddressList(
						memberNo
						);

		PaymentMethodDAO paymentMethodDAO =
				new PaymentMethodDAO();

		List<PaymentMethodDTO> paymentMethods =
				paymentMethodDAO.getBankAccounts(
						memberNo
						);

		List<PaymentMethodDTO> cardMethods =
				paymentMethodDAO.getCards(memberNo);

		request.setAttribute(
				"checkout",
				checkout
				);

		request.setAttribute(
				"checkoutItems",
				checkoutItems
				);

		request.setAttribute(
				"address",
				address
				);

		request.setAttribute(
				"addressList",
				addressList
				);

		request.setAttribute(
				"checkoutNo",
				checkoutNo
				);

		request.setAttribute(
				"paymentMethods",
				paymentMethods
				);
		
		request.setAttribute(
				"cardMethods",
				cardMethods
		);

		request.getRequestDispatcher(
				"/goodpang_order_payment.jsp"
				).forward(
						request,
						response
						);
	}
}