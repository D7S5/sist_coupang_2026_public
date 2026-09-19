package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dto.MemberDTO;
import com.goodpang.util.CartUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart/preview")
public class CartPreviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession();

        MemberDTO loginMember =
                (MemberDTO) session.getAttribute(
                        "loginMember"
                );

        /*
         * 최신 장바구니 정보를 세션에 다시 반영
         */
        if (loginMember != null) {

            CartUtil.refreshCartSession(
                    request,
                    loginMember.getMemberNo()
            );

        } else {

            @SuppressWarnings("unchecked")
            java.util.Map<Integer, Integer> guestCart =
                    (java.util.Map<Integer, Integer>)
                            session.getAttribute(
                                    "guestCart"
                            );

            if (guestCart != null) {

                CartUtil.refreshGuestCartSession(
                        request,
                        guestCart
                );
            }
        }

        request.getRequestDispatcher(
                "/cart_preview.jsp"
        ).forward(
                request,
                response
        );
    }
}