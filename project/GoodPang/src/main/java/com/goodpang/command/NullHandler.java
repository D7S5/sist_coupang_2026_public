package com.goodpang.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * commandHandler.properties 에 매핑이 없는 URL 이 DispatcherServlet 으로 들어왔을 때 쓰는 대체 Handler.
 * (지금은 /product, /category, /option 3개만 web.xml 에 매핑할 예정이라 이 상황 자체가
 *  거의 안 생기겠지만, 방어적으로 만들어둠 — 없으면 DispatcherServlet 이 예외를 던짐)
 */
public class NullHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO: 404 전용 페이지가 생기면 그쪽으로 forward. 지금은 임시로 에러 응답만.
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }
}
