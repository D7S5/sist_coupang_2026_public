<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>GoodPang | 회원 탈퇴 완료</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_withdraw_complete.css">
</head>
<body>

<jsp:include page="/inc/header.jsp" />

<div class="withdraw-page">
    <div class="withdraw-layout">

        <main class="withdraw-container">

            <div class="withdraw-header">
                <h1>회원 탈퇴</h1>

                <div class="withdraw-step">
                    <span>01 본인 인증</span>
                    <span class="arrow">›</span>
                    <span>02 GoodPang 이용내역 확인</span>
                    <span class="arrow">›</span>
                    <strong>03 회원탈퇴 완료</strong>
                </div>
            </div>

            <div class="complete-content">

                <div class="complete-icon">✓</div>

                <h2>회원 탈퇴가 완료되었습니다.</h2>

                <p>
                    그동안 GoodPang 서비스를 이용해주셔서 감사합니다.
                </p>

                <p>
                    보다 좋은 서비스로 다시 만나뵐 수 있도록 노력하겠습니다.
                </p>

                <a href="${pageContext.request.contextPath}/" class="home-button">
                    GoodPang 홈으로
                </a>

            </div>

        </main>

        <div class="withdraw-right-banner">
            <jsp:include page="/inc/right_banner.jsp" />
        </div>

    </div>
</div>

<jsp:include page="/inc/footer.jsp" />

</body>
</html>