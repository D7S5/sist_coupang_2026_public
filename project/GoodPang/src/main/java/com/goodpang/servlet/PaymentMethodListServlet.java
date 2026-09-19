package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/payment-method/list")
public class PaymentMethodListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        List<PaymentMethodDTO> paymentMethods =
                paymentMethodDAO.getPaymentMethods(loginMember.getMemberNo());

        request.setAttribute("paymentMethods", paymentMethods);

        request.getRequestDispatcher("/WEB-INF/views/payment_method_list.jsp")
               .forward(request, response);
    }
}