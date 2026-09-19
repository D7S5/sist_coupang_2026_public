package com.goodpang.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CommandHandler {
    // 요청을 처리한 뒤 이동할 JSP 경로(View)를 반환하는 메서드
    String process(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
