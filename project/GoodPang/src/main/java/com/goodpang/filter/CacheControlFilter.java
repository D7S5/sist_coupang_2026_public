package com.goodpang.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class CacheControlFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        // 브라우저가 이전 페이지를 캐시하지 않도록 설정
        httpResponse.setHeader(
            "Cache-Control",
            "no-cache, no-store, must-revalidate"
        );

        httpResponse.setHeader(
            "Pragma",
            "no-cache"
        );

        httpResponse.setDateHeader(
            "Expires",
            0
        );

        chain.doFilter(request, response);
    }
}