<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">
</head>
<head>
<meta charset="UTF-8">
<title>GoodPang | 회원 탈퇴</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member_withdraw.css">
</head>
<body>

<jsp:include page="/inc/header.jsp" />

<div class="withdraw-page">

    <div class="withdraw-layout">

        <main class="withdraw-container">

            <div class="withdraw-header">
                <h1>회원 탈퇴</h1>

                <div class="withdraw-step">
                    <strong>01 본인 인증</strong>
                    <span>›</span>
                    <span>02 GoodPang 이용내역 확인</span>
                    <span>›</span>
                    <span>03 회원탈퇴 완료</span>
                </div>
            </div>

            <section class="withdraw-section">
                <h2>GoodPang 서비스를 이용하시는데 불편함이 있으셨나요?</h2>
                <p>GoodPang 서비스 이용 중 불편한 사항이 있으셨다면 고객센터를 통해 문의해주세요.</p>
                <p>회원탈퇴 전 아래 내용을 반드시 확인해주세요.</p>
            </section>

            <section class="withdraw-section">
                <h2>회원 탈퇴 시 유의사항을 확인해주세요</h2>

                <div class="notice-box">
                    <ul>
                        <li>회원 탈퇴 시 GoodPang의 회원 전용 서비스를 이용할 수 없습니다.</li>
                        <li>진행 중인 주문이나 배송 상품이 있는 경우 회원 탈퇴가 제한될 수 있습니다.</li>
                        <li>회원 탈퇴 후 보유하고 있던 GoodPay 머니, 쿠폰 및 적립금은 소멸될 수 있습니다.</li>
                        <li>와우 멤버십을 이용 중인 경우 멤버십 해지 후 회원 탈퇴를 진행해주세요.</li>
                        <li>작성한 상품 리뷰 및 문의글은 탈퇴 후에도 남아 있을 수 있습니다.</li>
                        <li>탈퇴 후 동일한 계정 정보로 재가입이 제한될 수 있습니다.</li>
                    </ul>
                </div>

                <label class="agree-box">
                    <input type="checkbox" id="withdrawAgree">
                    <span>상기 회원탈퇴 시 처리사항 안내를 확인하였으며 이에 동의합니다.</span>
                </label>
            </section>

            <section class="withdraw-section">
                <h2>보안을 위해 비밀번호를 입력해주세요</h2>

                <div class="password-box">

                    <div class="member-info">
                        <div>
                            <span class="label">이름</span>
                            <span>${sessionScope.loginMember.memberName}</span>
                        </div>

                        <div>
                            <span class="label">이메일</span>
                            <span>${sessionScope.loginMember.email}</span>
                        </div>
                    </div>

                    <form id="withdrawForm"
                          action="${pageContext.request.contextPath}/member/withdraw"
                          method="post">

                        <input type="hidden" name="agree" id="agreeValue" value="N">

                        <input type="password"
                               name="password"
                               id="password"
                               class="password-input"
                               placeholder="비밀번호 입력"
                               autocomplete="current-password">

                        <c:if test="${not empty errorMessage}">
                            <p class="error-message">${errorMessage}</p>
                        </c:if>

                        <div class="password-help">
                            비밀번호를 잊으셨나요?
                            <a href="${pageContext.request.contextPath}/member/password">비밀번호 변경</a>
                        </div>

                        <div class="withdraw-buttons">
                            <a href="${pageContext.request.contextPath}/" class="btn-cancel">취소</a>
                            <button type="submit" class="btn-next">다음</button>
                        </div>

                    </form>

                </div>
            </section>

        </main>

        <div class="withdraw-right-banner">
            <jsp:include page="/inc/right_banner.jsp" />
        </div>

    </div>

</div>

<jsp:include page="/inc/footer.jsp" />

<script>
const form = document.getElementById("withdrawForm");
const agree = document.getElementById("withdrawAgree");
const agreeValue = document.getElementById("agreeValue");
const password = document.getElementById("password");

form.addEventListener("submit", function(e) {
    if (!agree.checked) {
        alert("회원 탈퇴 유의사항에 동의해주세요.");
        e.preventDefault();
        return;
    }

    if (password.value.trim() === "") {
        alert("비밀번호를 입력해주세요.");
        password.focus();
        e.preventDefault();
        return;
    }

    agreeValue.value = "Y";
});
</script>

</body>
</html>