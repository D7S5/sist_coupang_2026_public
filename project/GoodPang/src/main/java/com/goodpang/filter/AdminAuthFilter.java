package com.goodpang.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * /admin 이하 전체를 관리자 로그인 여부로 가드한다.
 * 로그인 페이지(/admin/login) 자체는 검사 대상에서 제외해야 리다이렉트 무한루프가 안 생긴다.
 */
@WebFilter(urlPatterns = { "/admin/*" })
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        if (uri.equals(contextPath + "/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        boolean loggedIn = (session != null) && (session.getAttribute("loginAdmin") != null);

        if (loggedIn) {
            chain.doFilter(request, response);
            return;
        }

        session = req.getSession();

        String redirectUrl = uri;
        String queryString = req.getQueryString();

        if (queryString != null && !queryString.isBlank()) {
            redirectUrl += "?" + queryString;
        }

        session.setAttribute("adminRedirectAfterLogin", redirectUrl);

        res.sendRedirect(contextPath + "/admin/login");
    }
}
