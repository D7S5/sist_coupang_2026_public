package com.goodpang.listener;

import com.goodpang.util.ImageUrl;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/*
 * 앱 시작 시 이 배포의 contextPath를 ImageUrl 유틸에 한 번 넣어준다.
 * contextPath는 요청마다 바뀌지 않는 고정값이라 요청 시점마다 다시 구할 필요가 없다.
 * (리눅스 운영 서버에서 ImageUrl.resolve()가 이 값을 씀 - ImageUrl.java 주석 참고)
 */
@WebListener
public class ImageBaseUrlListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ImageUrl.init(event.getServletContext().getContextPath());
    }

}
