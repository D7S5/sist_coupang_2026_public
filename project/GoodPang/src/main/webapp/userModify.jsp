<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>GoodPang - 회원정보 수정</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/userModify.css">
</head>

<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="user-modify-page">
		<div class="user-modify-layout">

			<main class="member-container">
				<h1 class="member-title">회원정보 수정</h1>
				<div class="title-line"></div>

				<!-- 오류 메시지 -->
				<c:if test="${not empty error}">
					<div class="error-message">
						<c:out value="${error}" />
					</div>
				</c:if>

				<!-- 아이디(이메일) -->
				<div class="info-row">
					<div class="info-title">아이디(이메일)</div>

					<div class="info-content">
						<strong class="member-value"> <c:out
								value="${member.email}" />
						</strong>

						<button type="button" class="small-btn"
							onclick="toggleForm('emailChangeForm')">이메일 변경</button>

						<div id="emailChangeForm" class="hidden-form">
							<form action="${pageContext.request.contextPath}/member/update"
								method="post">

								<input type="hidden" name="type" value="email"> <input
									type="email" id="newEmail" name="newEmail" placeholder="새 이메일"
									required>

								<button type="submit" class="small-btn">변경</button>
							</form>
						</div>
					</div>
				</div>

				<!-- 이름 -->
				<div class="info-row">
					<div class="info-title">이름</div>

					<div class="info-content">
						<strong class="member-value"> <c:out
								value="${member.memberName}" />
						</strong>

						<button type="button" class="small-btn">개명하셨다면? 이름변경 &gt;
						</button>
					</div>
				</div>

				<!-- 휴대폰 번호 -->
				<div class="info-row">
					<div class="info-title">휴대폰 번호</div>

					<div class="info-content">
						<strong class="member-value"> <c:out
								value="${member.phone}" />
						</strong>

						<button type="button" class="small-btn"
							onclick="toggleForm('phoneChangeForm')">휴대폰 번호 변경</button>

						<div id="phoneChangeForm" class="hidden-form">
							<form action="${pageContext.request.contextPath}/member/update"
								method="post">

								<input type="hidden" name="type" value="phone"> <input
									type="text" id="newPhone" name="newPhone"
									placeholder="010-0000-0000" required>

								<button type="submit" class="small-btn">휴대폰 번호 변경</button>
							</form>
						</div>
					</div>
				</div>

				<!-- 비밀번호 변경 -->
				<div class="info-row password-row">
					<div class="info-title">비밀번호변경</div>

					<div class="info-content password-content">
						<div class="password-notice">비밀번호 변경 시, 로그인된 다른 기기에서 로그아웃이
							반영되기까지 최대 3분 소요됩니다.</div>

						<form action="${pageContext.request.contextPath}/member/password"
							method="post" class="password-form" id="passwordForm">

							<!-- 현재 비밀번호 -->
							<div class="password-input-row">
								<label for="currentPassword">현재 비밀번호</label> <input
									type="password" id="currentPassword" name="currentPassword"
									autocomplete="current-password" required>
							</div>

							<!-- 새 비밀번호 -->
							<div class="password-input-row">
								<label for="newPassword">새 비밀번호</label> <input type="password"
									id="newPassword" name="newPassword" minlength="8"
									autocomplete="new-password" required>

								<p class="password-guide">비밀번호는 8자리 이상 입력해주세요.</p>
							</div>

							<!-- 비밀번호 확인 -->
							<div class="password-input-row">
								<label for="confirmPassword">비밀번호 다시 입력</label> <input
									type="password" id="confirmPassword" name="confirmPassword"
									minlength="8" autocomplete="new-password" required>

								<p id="passwordMatchMessage" class="password-guide"></p>
							</div>

							<div class="password-button-row">
								<button type="submit" class="small-btn password-btn">
									비밀번호 변경</button>
							</div>
						</form>
					</div>
				</div>

				<!-- 배송지 -->
				<div class="info-row">
					<div class="info-title">배송지</div>

					<div class="info-content">
						배송지 주소 관리는 <a
							href="${pageContext.request.contextPath}/address/list"
							class="blue-link"> [배송지 관리] </a> 에서 수정, 등록 합니다.
					</div>
				</div>

				<!-- 수신설정 -->
				<div class="info-row receive-row">
					<div class="info-title">수신설정</div>

					<div class="info-content receive-content">

						<div class="setting-section">
							<label class="checkbox-label"> <input type="checkbox"
								name="marketingAgree" checked> 마케팅 목적의 개인정보 수집 및 이용 동의 <span
								class="date">26.05.01</span>
							</label>

							<button type="button" class="detail-view">전문보기 &gt;</button>
						</div>

						<div class="setting-section advertising">
							<div class="advertising-line">

								<label class="checkbox-label"> <input type="checkbox"
									name="advertisingAgree" checked> 광고성 정보 수신 동의 <span
									class="date">26.05.01</span>
								</label> <span class="sub-options"> ( <label> <input
										type="checkbox"> SMS
								</label> <label> <input type="checkbox"> SNS
								</label> <label> <input type="checkbox"> 이메일
								</label> <label> <input type="checkbox" checked disabled>
										푸시 알림
								</label> )
								</span>
							</div>

							<button type="button" class="detail-view">전문보기 &gt;</button>

							<p class="setting-guide">*푸시 알림을 받으려면 고객님 기기에서 알림을 허용해주세요.</p>

							<p class="setting-guide">*위 항목을 모두 동의하셔야 GoodPang 맞춤형
								쇼핑혜택(광고)을 받으실 수 있습니다.</p>
						</div>
					</div>
				</div>

				<div class="member-bottom">
					<button type="button" class="withdraw-btn">회원탈퇴</button>
				</div>
			</main>

			<!-- 오른쪽 배너 -->
			<jsp:include page="/inc/right_banner.jsp" />
		</div>
	</div>

	<jsp:include page="/inc/footer.jsp" />

	<script>
function toggleForm(id) {
    const form = document.getElementById(id);

    if (form) {
        form.classList.toggle("show");
    }
}

// ===============================
// 비밀번호 유효성 검사
// ===============================

const passwordForm = document.getElementById("passwordForm");
const newPassword = document.getElementById("newPassword");
const confirmPassword = document.getElementById("confirmPassword");
const passwordMatchMessage = document.getElementById("passwordMatchMessage");

// 비밀번호 확인 입력 시 실시간 검사
confirmPassword.addEventListener("input", function () {
    if (confirmPassword.value === "") {
        passwordMatchMessage.textContent = "";
        return;
    }

    if (newPassword.value === confirmPassword.value) {
        passwordMatchMessage.textContent = "비밀번호가 일치합니다.";
    } else {
        passwordMatchMessage.textContent = "비밀번호가 일치하지 않습니다.";
    }
});

// 새 비밀번호가 변경됐을 때도 확인
newPassword.addEventListener("input", function () {
    if (confirmPassword.value === "") {
        return;
    }

    if (newPassword.value === confirmPassword.value) {
        passwordMatchMessage.textContent = "비밀번호가 일치합니다.";
    } else {
        passwordMatchMessage.textContent = "비밀번호가 일치하지 않습니다.";
    }
});

// 제출 직전 최종 검사
passwordForm.addEventListener("submit", function (event) {
    const password = newPassword.value;

    // 8자리 이상 검사
    if (password.length < 8) {
        event.preventDefault();
        alert("새 비밀번호는 8자리 이상 입력해주세요.");
        newPassword.focus();
        return;
    }

    // 비밀번호 일치 검사
    if (newPassword.value !== confirmPassword.value) {
        event.preventDefault();
        alert("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        confirmPassword.focus();
        return;
    }
});
</script>

</body>
</html>