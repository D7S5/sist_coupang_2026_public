<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>GoodPang 로그인</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/login.css">
</head>

<body>

<div class="login-wrapper">

    <!-- =========================
         상단 로고
    ========================== -->
    <header class="login-header">

        <h1 class="logo">

            <a href="${pageContext.request.contextPath}/"
               title="GoodPang 홈으로">

                <span class="brand-goodpang">
                    GoodPang
                </span>

            </a>

        </h1>

    </header>


    <!-- =========================
         로그인 탭
    ========================== -->
    <nav class="login-tabs">

        <button
            type="button"
            class="tab-btn active"
            data-tab="email">

            이메일 로그인

        </button>

        <button
            type="button"
            class="tab-btn"
            data-tab="phone">

            휴대전화번호 로그인

            <span class="badge-n">
                N
            </span>

        </button>

        <button
            type="button"
            class="tab-btn"
            data-tab="qr">

            QR코드 로그인

        </button>

    </nav>


    <!-- =========================
         로그인 콘텐츠
    ========================== -->
    <main class="login-content">


        <!-- =========================
             이메일 로그인
        ========================== -->
        <form
            id="form-email"
            class="auth-form active-form"
            method="post"
            action="${pageContext.request.contextPath}/login">


            <!-- 로그인 오류 메시지 -->
            <% if (request.getAttribute("error") != null) { %>

                <div class="login-error">

                    <%= request.getAttribute("error") %>

                </div>

            <% } %>


            <!-- 이메일 -->
            <div class="input-group">

                <span class="input-icon icon-mail"></span>

                <input
                    type="email"
                    name="email"
                    id="email"
                    placeholder="아이디(이메일)"
                    required>

                <button
                    type="button"
                    class="btn-clear"
                    tabindex="-1">

                    &times;

                </button>

            </div>


            <!-- 비밀번호 -->
            <div class="input-group">

                <span class="input-icon icon-lock"></span>

                <input
                    type="password"
                    name="password"
                    id="password"
                    placeholder="비밀번호"
                    required>

                <button
                    type="button"
                    class="btn-toggle-pw"
                    tabindex="-1">
                </button>

            </div>


            <!-- 자동 로그인 / 비밀번호 찾기 -->
            <div class="form-options">

                <label class="auto-login-label">

                    <input
                        type="checkbox"
                        name="autoLogin">

                    <span class="checkbox-custom"></span>

                    자동 로그인

                </label>


                <a href="#"
                   class="find-link">

                    아이디·비밀번호 찾기

                </a>

            </div>


            <!-- 로그인 버튼 -->
            <button
                type="submit"
                class="btn-submit btn-blue">

                로그인

            </button>


            <!-- 회원가입 -->
            <a
                href="${pageContext.request.contextPath}/signup.jsp"
                class="btn-submit btn-outline">

                회원가입

            </a>


            <div class="divider">

                <span>
                    패스키를 GoodPang에 이미 등록했다면
                </span>

            </div>


            <button
                type="button"
                class="btn-submit btn-passkey">

                패스키 로그인

            </button>

        </form>



        <!-- =========================
             휴대전화 로그인
        ========================== -->
        <form
            id="form-phone"
            class="auth-form"
            method="post"
            action="#">

            <p class="form-desc">
                GoodPang 계정에 등록된 휴대전화번호를 입력해주세요.
            </p>


            <div class="input-group">

                <span class="input-icon icon-phone"></span>

                <input
                    type="tel"
                    name="userPhone"
                    placeholder="휴대전화번호"
                    required>

            </div>


            <button
                type="submit"
                class="btn-submit btn-blue">

                인증번호 발송

            </button>


            <a
                href="${pageContext.request.contextPath}/signup.jsp"
                class="btn-submit btn-outline">

                회원가입

            </a>


            <div class="divider">

                <span>
                    패스키를 GoodPang에 이미 등록했다면
                </span>

            </div>


            <button
                type="button"
                class="btn-submit btn-passkey">

                패스키 로그인

            </button>

        </form>



        <!-- =========================
             QR 로그인
        ========================== -->
        <div
            id="form-qr"
            class="auth-form">

            <div class="qr-container">


                <div class="qr-info">

                    <h3>
                        GoodPang 앱을 통해 바로 로그인하려면
                        <br>
                        다음 단계에 따라 진행해주세요
                    </h3>


                    <ol class="qr-steps">

                        <li>
                            휴대폰 카메라로 QR코드를 스캔하세요.
                        </li>

                        <li>
                            화면에서 아래의 숫자를 선택하면
                            로그인됩니다.
                        </li>

                    </ol>


                    <div class="qr-number-box">
                        71
                    </div>


                    <p class="qr-notice">
                        ⓘ 최신 버전의 앱에서만
                        QR 로그인이 가능합니다.
                    </p>

                </div>


                <div class="qr-code-area">

                    <img
                        src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=GoodPangLogin"
                        alt="QR Code"
                        class="qr-img">

                    <span class="qr-timer">

                        남은시간

                        <strong id="timer">
                            2:57
                        </strong>

                    </span>

                </div>

            </div>


            <a
                href="${pageContext.request.contextPath}/signup.jsp"
                class="btn-submit btn-outline">

                회원가입

            </a>

        </div>

    </main>



    <!-- =========================
         푸터
    ========================== -->
    <footer class="login-footer">

        <p>
            &copy; GoodPang Corp.
            All rights reserved.
        </p>

    </footer>

</div>

<script>
window.addEventListener("pageshow", function(event) {
    const navigation = performance.getEntriesByType("navigation")[0];

    if (event.persisted || (navigation && navigation.type === "back_forward")) {
        window.location.replace(
            "${pageContext.request.contextPath}/login?t=" + Date.now()
        );
    }
});
</script>
<!-- =========================
     로그인 JS
========================== -->
<script
    src="${pageContext.request.contextPath}/js/login.js">
</script>


<!-- =========================
     QR 타이머
========================== -->
<script>

(function () {

    // 2분 57초
    let totalSeconds = 2 * 60 + 57;

    const timerElement =
        document.getElementById("timer");

    if (!timerElement) {
        return;
    }

    const intervalId = setInterval(function () {

        totalSeconds--;

        if (totalSeconds <= 0) {

            clearInterval(intervalId);

            timerElement.textContent = "0:00";

            location.reload();

            return;
        }

        const minutes =
            Math.floor(totalSeconds / 60);

        const seconds =
            totalSeconds % 60;

        const formattedSeconds =
            seconds < 10
                ? "0" + seconds
                : seconds;

        timerElement.textContent =
            minutes + ":" + formattedSeconds;

    }, 1000);

})();

</script>

</body>
</html>