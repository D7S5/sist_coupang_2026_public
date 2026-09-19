package com.goodpang.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.goodpang.command.CommandHandler;
import com.goodpang.command.NullHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 커맨드 패턴의 진입점(컨트롤러) 서블릿.
 * web.xml 에서 /product, /category, /option 3개 URL 을 이 서블릿 하나로 매핑함.
 *
 * 흐름: URL -> commandHandler.properties 에서 Handler 클래스 찾기 -> 리플렉션으로 생성(1회, init 시점) ->
 *       CommandHandler.process() 실행 -> 리턴받은 View(jsp 경로)로 forward
 */
public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Map<String, CommandHandler> commandHandlerMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();

        // 1. web.xml 의 <init-param> 에서 mappingPath 경로 추출 (학원 방식)
        String urlMappingPath = this.getInitParameter("mappingPath");

        Properties p = new Properties();

        // 2. commandHandler.properties 는 webapp/WEB-INF 밑에 둠 -> 클래스로더가 아니라
        //    ServletContext 로 읽어야 함 (getRealPath() 는 WAR 배포 시 null 나올 수 있어서 안 씀)
        try (InputStream is = this.getServletContext().getResourceAsStream(urlMappingPath)) {
            if (is == null) {
                throw new ServletException("경로에서 프로퍼티 파일을 찾을 수 없습니다: " + urlMappingPath);
            }
            p.load(is);
        } catch (IOException e) {
            throw new ServletException("설정 파일 로딩 실패", e);
        }

        // 3. 인스턴스 1회 생성 후 Map 에 보관 (학원 방식 - 요청마다 리플렉션 안 함)
        Set<Map.Entry<Object, Object>> set = p.entrySet();
        Iterator<Map.Entry<Object, Object>> ir = set.iterator();

        while (ir.hasNext()) {
            Map.Entry<Object, Object> entry = ir.next();
            String url = (String) entry.getKey();
            String fullName = ((String) entry.getValue()).trim();

            try {
                Class<?> commandHandlerClass = Class.forName(fullName);
                CommandHandler handler = (CommandHandler) commandHandlerClass
                        .getDeclaredConstructor()
                        .newInstance();

                commandHandlerMap.put(url, handler);

                // 개발용 로그 — properties 를 실제로 읽어서 Handler 를 만들었는지 확인용
                System.out.println("[Dispatcher] 매핑 등록: " + url + " -> " + fullName);
            } catch (Exception e) {
                throw new ServletException("핸들러 등록 실패 (" + fullName + ")", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 학원 방식 URL 분석 (컨텍스트 패스 절삭) - 예: "/product", "/category", "/option"
        // String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        // String path = requestURI.substring(contextPath.length());   // ← 2026-09-06 아래 줄로 교체

        // ★ 2026-09-06 — getServletPath() 로 바꾼 이유:
        //    옛 방식은 주소 전체에서 컨텍스트만 뗐기 때문에 /product/131 이 들어오면 path 가
        //    "/product/131" 이 되어 Map 에서 핸들러를 못 찾고 NullHandler 로 빠졌음.
        //    getServletPath() 는 web.xml 의 <url-pattern> 에 걸린 부분("/product")까지만 돌려주고,
        //    뒤에 붙은 "/131" 은 request.getPathInfo() 로 따로 꺼낼 수 있음.
        //    옛 주소(/product?productNo=131)일 때도 "/product" 라서 둘 다 그대로 동작함
        String path = request.getServletPath();

        // 2. Map 에서 핸들러 탐색 (없으면 NullHandler 대체 - 학원 방식)
        CommandHandler handler = this.commandHandlerMap.get(path);
        if (handler == null) {
            handler = new NullHandler();
        }

        // 개발용 로그 — 어떤 URL 이 어떤 Handler 로 갔는지 톰캣 콘솔에서 눈으로 확인하려고 넣음.
        // (커맨드 패턴이 실제로 타는지 확인하는 용도. 필요 없어지면 이 줄만 지우면 됨)
        System.out.println("[Dispatcher] " + path + " -> " + handler.getClass().getSimpleName());

        // 3. 핸들러 실행
        String viewName = null;
        try {
            viewName = handler.process(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        if (viewName == null) {
            // Handler 안에서 이미 응답 처리를 끝낸 경우 (redirect, sendError 등)
            return;
        }

        // 4. redirect: 접두사 판별 (학원 방식)
        if (viewName.startsWith("redirect:")) {
            String location = viewName.substring("redirect:".length());
            response.sendRedirect(contextPath + location);
        } else {
            // 5. 포워딩
            RequestDispatcher dispatcher = request.getRequestDispatcher(viewName);
            dispatcher.forward(request, response);
        }
    }
}
