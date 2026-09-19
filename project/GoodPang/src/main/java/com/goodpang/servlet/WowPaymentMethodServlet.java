package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.PaymentMethodValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/wow/payment-method")
public class WowPaymentMethodServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PaymentMethodDAO paymentMethodDAO =
            new PaymentMethodDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        MemberDTO loginMember =
                session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

		/*
		 * List<PaymentMethodDTO> paymentMethods = paymentMethodDAO.getBankAccounts(
		 * loginMember.getMemberNo() );
		 */
        
        List<PaymentMethodDTO> paymentMethods =
                paymentMethodDAO.getPaymentMethods(
                        loginMember.getMemberNo()
                );

        request.setAttribute(
                "paymentMethods",
                paymentMethods
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/wow_payment_methods.jsp"
        ).forward(request, response);
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        
        
        HttpSession session = request.getSession(false);

        MemberDTO loginMember =
                session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String paymentType = request.getParameter("paymentType");

        String error =
                PaymentMethodValidator.validatePaymentType(paymentType);

        if (error != null) {
            sendValidationError(response, error);
            return;
        }

        PaymentMethodDTO dto = new PaymentMethodDTO();

        dto.setMemberNo(loginMember.getMemberNo());
        dto.setPaymentType(paymentType);

        boolean paymentDefault =
                "Y".equals(request.getParameter("paymentDefault"));

        dto.setPaymentDefault(paymentDefault);


        if ("BANK".equals(paymentType)) {

            String bankCode =
                    request.getParameter("bankCode");

            String accountNumber =
                    request.getParameter("accountNumber");

            String accountHolder =
                    request.getParameter("accountHolder");

            error =
                    PaymentMethodValidator.validateBank(
                            bankCode,
                            accountNumber,
                            accountHolder
                    );

            if (error != null) {
                sendValidationError(response, error);
                return;
            }

            accountNumber =
                    accountNumber.replaceAll("[^0-9]", "");

            String accountLast4 =
                    accountNumber.substring(
                            accountNumber.length() - 4
                    );

            dto.setBankCode(bankCode);
            dto.setAccountLast4(accountLast4);
            dto.setAccountHolder(accountHolder.trim());


        } else if ("CARD".equals(paymentType)) {

            String cardCompany =
                    request.getParameter("cardCompany");

            String cardNumber =
                    request.getParameter("cardNumber");

            error =
                    PaymentMethodValidator.validateCard(
                            cardCompany,
                            cardNumber
                    );

            if (error != null) {
                sendValidationError(response, error);
                return;
            }

            cardNumber =
                    cardNumber.replaceAll("[^0-9]", "");

            String cardLast4 =
                    cardNumber.substring(
                            cardNumber.length() - 4
                    );

            dto.setCardCompany(cardCompany);
            dto.setCardLast4(cardLast4);
        }


        int result =
                paymentMethodDAO.insertPaymentMethod(dto);

        if (result != 1) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "결제수단 등록에 실패했습니다."
            );
            return;
        }


        List<PaymentMethodDTO> paymentMethods =
                paymentMethodDAO.getPaymentMethods(
                        loginMember.getMemberNo()
                );

        request.setAttribute(
                "paymentMethods",
                paymentMethods
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/wow_payment_methods.jsp"
        ).forward(request, response);
    }
    
    private void sendValidationError(
            HttpServletResponse response,
            String message)
            throws IOException {

        response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST
        );

        response.setContentType(
                "text/plain; charset=UTF-8"
        );

        response.getWriter().write(message);
    }
}