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

@WebServlet("/payment-method/add")
public class PaymentMethodAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        String redirect = request.getParameter("redirect");

        if (redirect != null && !redirect.isBlank()) {
            request.getSession().setAttribute("paymentMethodRedirect", redirect);
        }

        request.getRequestDispatcher("/WEB-INF/views/payment_method_add.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO member = LoginUtil.requireLogin(request, response);

        if (member == null) {
            return;
        }

        int memberNo = member.getMemberNo();

        String checkoutNoParam = request.getParameter("checkoutNo");
        Integer checkoutNo = null;

        if (checkoutNoParam != null && !checkoutNoParam.isBlank()) {
            try {
                checkoutNo = Integer.parseInt(checkoutNoParam);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "잘못된 checkout 번호입니다."
                );
                return;
            }
        }

        String redirect = request.getParameter("redirect");
        String paymentType = request.getParameter("paymentType");
        String bankCode = request.getParameter("bankCode");
        String accountNumber = request.getParameter("accountNumber");
        String accountHolder = request.getParameter("accountHolder");
        String cardCompany = request.getParameter("cardCompany");
        String cardNumber1 = request.getParameter("cardNumber1");
        String cardNumber2 = request.getParameter("cardNumber2");
        String cardNumber3 = request.getParameter("cardNumber3");
        String cardNumber4 = request.getParameter("cardNumber4");
        String paymentDefaultParam = request.getParameter("paymentDefault");

        if (paymentType == null || paymentType.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "결제수단 종류가 없습니다."
            );
            return;
        }

        boolean paymentDefault = "Y".equals(paymentDefaultParam);

        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setMemberNo(memberNo);
        dto.setPaymentType(paymentType);
        dto.setPaymentDefault(paymentDefault);

        if ("BANK".equals(paymentType)) {

            if (bankCode == null || bankCode.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "은행을 선택해주세요."
                );
                return;
            }

            if (accountNumber == null || accountNumber.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "계좌번호를 입력해주세요."
                );
                return;
            }

            if (accountHolder == null || accountHolder.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "예금주를 입력해주세요."
                );
                return;
            }

            accountNumber = accountNumber.replace("-", "").trim();

            if (!accountNumber.matches("[0-9]{10,14}")) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "계좌번호는 숫자 10~14자리로 입력해주세요."
                );
                return;
            }

            accountHolder = accountHolder.trim();

            if (accountHolder.length() > 10) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "예금주는 최대 10자리까지 입력할 수 있습니다."
                );
                return;
            }

            String accountLast4 =
                    accountNumber.substring(accountNumber.length() - 4);

            dto.setBankCode(bankCode);
            dto.setAccountLast4(accountLast4);
            dto.setAccountHolder(accountHolder);

        } else if ("CARD".equals(paymentType)) {

            if (cardCompany == null || cardCompany.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "카드사를 선택해주세요."
                );
                return;
            }

            if (!value(cardNumber1).matches("[0-9]{4}")
                    || !value(cardNumber2).matches("[0-9]{4}")
                    || !value(cardNumber3).matches("[0-9]{4}")
                    || !value(cardNumber4).matches("[0-9]{4}")) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "카드번호는 각 칸에 숫자 4자리씩 입력해주세요."
                );
                return;
            }

            String cardNumber =
                    value(cardNumber1)
                    + value(cardNumber2)
                    + value(cardNumber3)
                    + value(cardNumber4);

            String cardLast4 = cardNumber.substring(12);

            dto.setCardCompany(cardCompany);
            dto.setCardLast4(cardLast4);

        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "지원하지 않는 결제수단입니다."
            );
            return;
        }

        Connection conn = null;

        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            if (paymentDefault) {
                paymentMethodDAO.clearDefault(conn, memberNo, paymentType);
            }

            int result;

            if ("BANK".equals(paymentType)) {
                result = paymentMethodDAO.insertBankAccount(conn, dto);
            } else {
                result = paymentMethodDAO.insertCard(conn, dto);
            }

            if (result <= 0) {
                throw new Exception("결제수단 등록 실패");
            }

            conn.commit();

            if (redirect != null
                    && !redirect.isBlank()
                    && redirect.startsWith("/")) {

                response.sendRedirect(
                        request.getContextPath() + redirect
                );
                return;
            }

            String sessionRedirect =
                    (String) request.getSession()
                            .getAttribute("paymentMethodRedirect");

            if (sessionRedirect != null
                    && !sessionRedirect.isBlank()
                    && sessionRedirect.startsWith("/")) {

                request.getSession()
                       .removeAttribute("paymentMethodRedirect");

                response.sendRedirect(
                        request.getContextPath() + sessionRedirect
                );
                return;
            }

            if (checkoutNo != null) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/order/payment?checkoutNo="
                        + checkoutNo
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/payment-method/list"
            );

        } catch (Exception e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            throw new ServletException(
                    "결제수단 등록 중 오류가 발생했습니다.",
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
    }

    private String value(String value) {
        return value == null ? "" : value.trim();
    }
}