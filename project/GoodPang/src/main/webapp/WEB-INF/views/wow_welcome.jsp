<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>와우 멤버십 가입 완료</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/wow_welcome.css">
</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="wow-welcome-page">
		<div class="wow-welcome-card">

			<div class="wow-welcome-logo">WOW!</div>

			<div class="wow-welcome-check">✓</div>

			<h1>와우 멤버십 가입을 환영합니다!</h1>

			<p class="wow-welcome-desc">이제부터 와우 회원만의 다양한 혜택을 이용하실 수 있어요.</p>

			<div class="wow-welcome-benefits">

				<div class="wow-welcome-benefit">
					<div class="wow-benefit-icon">🚚</div>
					<div>
						<strong>로켓배송 무료배송</strong>
						<p>금액 조건 없이 로켓배송 상품을 무료로 받아보세요.</p>
					</div>
				</div>

				<div class="wow-welcome-benefit">
					<div class="wow-benefit-icon">🌙</div>
					<div>
						<strong>새벽배송 · 당일배송</strong>
						<p>필요한 상품을 더욱 빠르게 받아보세요.</p>
					</div>
				</div>

				<div class="wow-welcome-benefit">
					<div class="wow-benefit-icon">↩</div>
					<div>
						<strong>30일 무료반품</strong>
						<p>와우 회원 전용 무료반품 혜택을 이용할 수 있어요.</p>
					</div>
				</div>

				<div class="wow-welcome-benefit">
					<div class="wow-benefit-icon">₩</div>
					<div>
						<strong>와우 회원 전용 할인</strong>
						<p>와우 회원만을 위한 특별 할인가를 만나보세요.</p>
					</div>
				</div>

			</div>

			<div class="wow-welcome-price">
				<span>와우 멤버십</span> <strong>월 7,890원</strong>
			</div>

			<div class="wow-welcome-actions">

				<a href="${pageContext.request.contextPath}/wow/membership"
					class="wow-welcome-benefit-btn"> 와우 멤버십 혜택 확인하기 </a>

				<c:choose>
					<c:when test="${not empty returnProductNo}">
						<a
							href="${pageContext.request.contextPath}/product?productNo=${returnProductNo}"
							class="wow-welcome-shopping-btn"> 쇼핑 계속하기 </a>
					</c:when>

					<c:otherwise>
						<a href="${pageContext.request.contextPath}/"
							class="wow-welcome-shopping-btn"> 쇼핑 계속하기 </a>
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</div>

</body>
</html>