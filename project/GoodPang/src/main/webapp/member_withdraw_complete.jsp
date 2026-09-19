<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
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

                <p>그동안 GoodPang을 이용해주셔서 감사합니다.</p>
                <p>회원님의 계정은 탈퇴 처리되었으며 더 이상 로그인할 수 없습니다.</p>

                <div class="complete-notice">
                    <strong>회원 탈퇴 안내</strong>
                    <ul>
                        <li>탈퇴한 계정의 회원 상태는 비활성화 처리됩니다.</li>
                        <li>기존 주문 및 결제 내역은 서비스 운영 정책에 따라 보관될 수 있습니다.</li>
                        <li>탈퇴한 계정으로 로그인할 경우 탈퇴 회원 안내 페이지가 표시됩니다.</li>
                    </ul>
                </div>

                <div class="complete-buttons">
                    <a href="${pageContext.request.contextPath}/" class="btn-home">GoodPang 홈으로</a>
                    <a href="${pageContext.request.contextPath}/signup" class="btn-signup">회원가입</a>
                </div>

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