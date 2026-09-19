package com.goodpang.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String referer =
                request.getHeader("Referer");

        HttpSession session =
                request.getSession(false);

        // 로그인 세션 완전히 삭제
        if (session != null) {
            session.invalidate();
        }

        // product 같은 일반 페이지라면
        // 보고 있던 페이지로 돌아가기
        if (referer != null
                && !referer.isBlank()) {

            response.sendRedirect(referer);

        } else {

            response.sendRedirect(
                request.getContextPath() + "/"
            );
        }
    }
}