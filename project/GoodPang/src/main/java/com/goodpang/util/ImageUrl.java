package com.goodpang.util;

/*
 * 상품 이미지 등 정적 리소스의 <img src="..."> 를 만들어주는 유틸.
 * JSP에서 EL 커스텀 함수(img:url, /WEB-INF/goodpang-functions.tld)로 호출한다:
 *   <img src="${img:url(item.thumbnailUrl)}" alt="...">
 *
 * - 로컬 PC(Windows)에서 실행 중이면 LOCAL_IMAGE_BASE_URL(절대 URL)을 붙인다.
 * - 그 외(Ubuntu 등 리눅스 운영 서버)에서는 contextPath를 붙인다 (지금까지 쓰던
 *   ${pageContext.request.contextPath}/... 와 동일한 결과).
 *
 * 환경변수를 따로 설정할 필요 없이 os.name으로 자동 판단한다 — 개발 PC는 Windows,
 * 운영 서버는 리눅스라는 전제(이 프로젝트의 실제 배포 환경)를 그대로 이용한 것.
 *
 * contextPath는 요청마다 바뀌지 않는 배포 고정값이라, 앱 시작 시 ImageBaseUrlListener가
 * 한 번만 init()으로 넣어준다.
 */
public final class ImageUrl {

    // 로컬(Windows)일 때 붙일 절대 URL. 주소가 바뀌면 여기만 고치면 됨
    private static final String LOCAL_IMAGE_BASE_URL = "http://scym3.cafe24.com:8080";

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name", "").toLowerCase().startsWith("windows");

    private static volatile String contextPath = "";

    public static void init(String servletContextPath) {
        contextPath = (servletContextPath != null) ? servletContextPath : "";
    }

    public static String resolve(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return "";
        }

        String base = IS_WINDOWS ? LOCAL_IMAGE_BASE_URL : contextPath;

        return base + "/" + imageUrl;
    }

    private ImageUrl() {
    }
}
