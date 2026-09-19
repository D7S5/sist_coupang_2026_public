package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;

import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/payment-method/default")
public class PaymentMethodDefaultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        int memberNo = loginMember.getMemberNo();
        String paymentMethodNoParam = request.getParameter("paymentMethodNo");

        if (paymentMethodNoParam == null || paymentMethodNoParam.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "결제수단 번호가 없습니다."
            );
            return;
        }

        int paymentMethodNo;

        try {
            paymentMethodNo = Integer.parseInt(paymentMethodNoParam);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 결제수단 번호입니다."
            );
            return;
        }

        PaymentMethodDTO paymentMethod =
                paymentMethodDAO.findPaymentMethod(memberNo, paymentMethodNo);

        if (paymentMethod == null) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "사용할 수 없는 결제수단입니다."
            );
            return;
        }

        Connection conn = null;

        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            paymentMethodDAO.clearDefault(
                    conn,
                    memberNo,
                    paymentMethod.getPaymentType()
            );

            int result = paymentMethodDAO.setDefault(
                    conn,
                    memberNo,
                    paymentMethodNo
            );

            if (result != 1) {
                throw new Exception("기본 결제수단 설정 실패");
            }

            conn.commit();

        } catch (Exception e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            throw new ServletException(
                    "기본 결제수단 설정 중 오류가 발생했습니다.",
                    e
            );

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        response.sendRedirect(
                request.getContextPath() + "/payment-method/list"
        );
    }
}
