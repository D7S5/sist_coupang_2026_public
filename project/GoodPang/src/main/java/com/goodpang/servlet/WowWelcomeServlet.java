package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/wow/welcome")
public class WowWelcomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember =
                LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        HttpSession session =
                request.getSession(false);

        boolean wowMember =
                session != null
                && Boolean.TRUE.equals(
                        session.getAttribute("wowMember")
                );

        if (!wowMember) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/wow/join"
            );
            return;
        }

        String productNo =
                request.getParameter("productNo");

        request.setAttribute(
                "returnProductNo",
                productNo
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/wow_welcome.jsp"
        ).forward(request, response);
    }
}