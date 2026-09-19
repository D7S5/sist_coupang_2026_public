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
 * 판매자센터 페이지(vendor_*.jsp)는 서블릿 없이 직접 열리는 JSP라서
 * LoginUtil.requireLogin() 같은 페이지별 호출 대신 필터로 한 번에 로그인 여부를 검사한다.
 */
@WebFilter(urlPatterns = {
        "/vendor/dashboard",
        "/vendor/product",
        "/vendor/product/write",
        "/vendor/product/detail",
        "/vendor/product/visibility",
        "/vendor/product/status",
        "/vendor/order",
        "/vendor/order/ship",
        "/vendor/order/detail",
        "/vendor/notice",
        "/vendor/notice/detail"
})
public class VendorAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        boolean loggedIn = (session != null) && (session.getAttribute("loginSeller") != null);

        if (loggedIn) {
            chain.doFilter(request, response);
            return;
        }

        session = req.getSession();

        String redirectUrl = req.getRequestURI();
        String queryString = req.getQueryString();

        if (queryString != null && !queryString.isBlank()) {
            redirectUrl += "?" + queryString;
        }

        // 회원(Member) 로그인 흐름의 redirectAfterLogin과 키가 겹치지 않도록 별도 키 사용
        session.setAttribute("vendorRedirectAfterLogin", redirectUrl);

        res.sendRedirect(req.getContextPath() + "/vendor/login");
    }
}
