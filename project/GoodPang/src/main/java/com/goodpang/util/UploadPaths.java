package com.goodpang.util;

import jakarta.servlet.ServletContext;

/*
 * 업로드 파일의 실제 저장 위치(디스크 경로).
 *
 * - 환경변수 UPLOAD_BASE_DIR가 설정돼 있으면 그 고정 경로를 쓴다 (운영 서버용).
 *   배포된 webapp 디렉토리 밖의 고정 경로라서, WAR 재배포·재익스플로드로 webapp이
 *   통째로 새로 풀려도 업로드된 파일은 그대로 남는다.
 *   (Tomcat이 이 경로를 정적으로 서빙하려면 webapp의 /upload가 이 디렉토리를
 *    가리키는 심볼릭 링크여야 한다 — 서버 설정 1회 필요)
 *
 * - 설정 안 돼 있으면 배포된 webapp 안의 /upload를 쓴다 (로컬 개발용 기본값 —
 *   Eclipse 등에서 별도 설정 없이 바로 동작).
 */
public final class UploadPaths {

    public static String resolveBaseDir(ServletContext servletContext) {
        String configured = System.getenv("UPLOAD_BASE_DIR");

        if (configured != null && !configured.isBlank()) {
            return configured;
        }

        return servletContext.getRealPath("/upload");
    }

    private UploadPaths() {
    }
}
