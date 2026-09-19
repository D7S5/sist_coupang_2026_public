<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor-login.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">	
  <title>굿팡 비즈니스 판매자 로그인</title>

</head>

<body>

  <div class="wing-bg">

    <!-- 뒤로가기 -->
    <a href="${pageContext.request.contextPath}/index.jsp" class="back-button" aria-label="이전으로">
      <span class="back-arrow"></span>
    </a>

    <!-- 배경 장식 아이콘 -->
    <div class="wing-deco" aria-hidden="true"></div>

    <main class="vendor-login">

      <!-- 로고 -->
      
      <div class="logo-area">
		<a href="${pageContext.request.contextPath}/index.jsp" class="brand-goodpang">GoodPang</a>
      </div>

      <h1 class="title">
        판매자 로그인
      </h1>

      <form class="form" id="loginForm" novalidate method="post"
        action="${pageContext.request.contextPath}/vendor/login">

        <% if (request.getAttribute("error") != null) { %>
          <p class="message error show"><%= request.getAttribute("error") %></p>
        <% } %>

        <div class="field">
          <input class="input" id="email" name="email" type="email" placeholder="아이디(이메일)를 입력해주세요"
            autocomplete="username" value="modernwear@naver.com">
        </div>

        <div class="field">
          <input class="input" id="password" name="password" type="password" placeholder="비밀번호를 입력해주세요"
            autocomplete="current-password" value="abcd1234">
        </div>

        <p id="loginMessage" class="message error"></p>

        <button class="submit" id="submitButton" type="submit">
          로그인
        </button>

        <div class="find-links">
          <a href="#" class="find-link">아이디 찾기</a>
          <span class="find-divider">|</span>
          <a href="#" class="find-link">비밀번호 찾기</a>
        </div>

      </form>

      <div class="divider">
        <span>판매자가 아니신가요?</span>
      </div>

      <a href="${pageContext.request.contextPath}/vendor/signup" class="signup-button">
        판매자 회원가입
      </a>

      <footer class="footer">

        <p class="cs-line">
          판매자 콜센터 <strong>1600-9879</strong>
        </p>

        <a href="${pageContext.request.contextPath}/sc-ui/account/privacy/agreePi.html" class="privacy-link" target="_blank" rel="noopener">
          판매자 개인정보 처리방침
        </a>

        <div class="lang-select">
          <span class="lang-icon" aria-hidden="true">&#127760;</span>
          한국어
          <span class="lang-caret" aria-hidden="true"></span>
        </div>

      </footer>

    </main>

  </div>


  <script>

    const form = document.getElementById("loginForm");
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const loginMessage = document.getElementById("loginMessage");


    function showError(message) {
      loginMessage.textContent = message;
      loginMessage.classList.add("show");
    }

    function clearError() {
      loginMessage.textContent = "";
      loginMessage.classList.remove("show");
    }

    [email, password].forEach(function (input) {
      input.addEventListener("input", function () {
        input.classList.remove("invalid");
        clearError();
      });
    });


    form.addEventListener("submit", function (event) {

      if (!email.value.trim()) {
        event.preventDefault();
        email.classList.add("invalid");
        showError("아이디를 입력해주세요.");
        email.focus();
        return;
      }

      if (!password.value) {
        event.preventDefault();
        password.classList.add("invalid");
        showError("비밀번호를 입력해주세요.");
        password.focus();
        return;
      }

      // 유효성 검사 통과 시 폼이 그대로 서버(/vendor/login)로 제출됨
    });

  </script>

</body>

</html>
