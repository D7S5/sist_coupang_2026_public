<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>GoodPang | 탈퇴 회원</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_withdrawn.css">
</head>
<body>

<jsp:include page="/inc/header.jsp" />

<div class="withdrawn-page">

    <div class="withdrawn-box">

        <div class="withdrawn-icon">!</div>

        <h1>이미 탈퇴된 회원입니다.</h1>

        <p>
            입력하신 계정은 회원 탈퇴가 완료된 계정입니다.
        </p>

        <p>
            GoodPang 서비스를 다시 이용하시려면 새로 회원가입해주세요.
        </p>

        <div class="withdrawn-buttons">
            <a href="${pageContext.request.contextPath}/login" class="btn-login">
                로그인으로
            </a>

            <a href="${pageContext.request.contextPath}/signup" class="btn-signup">
                회원가입
            </a>
        </div>

    </div>

</div>

<jsp:include page="/inc/footer.jsp" />

</body>
</html>