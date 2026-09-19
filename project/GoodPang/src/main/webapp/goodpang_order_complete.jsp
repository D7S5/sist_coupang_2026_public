<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>GoodPang 주문완료</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/goodpang_order_complete.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
</head>
<body>

	<c:set var="receiverName"
		value="${empty orderComplete.receiverName ? '홍길동' : orderComplete.receiverName}" />
	<c:set var="receiverPhone"
		value="${empty orderComplete.receiverPhone ? '010****3333' : orderComplete.receiverPhone}" />
	<c:set var="zipcode"
		value="${empty orderComplete.zipcode ? '06678' : orderComplete.zipcode}" />
	<c:set var="address"
		value="${empty orderComplete.address ? '서울특별시 서초구' : orderComplete.address}" />
	<c:set var="requestMsg"
		value="${empty orderComplete.requestMsg ? '문 앞' : orderComplete.requestMsg}" />
	<c:set var="arrivalDate"
		value="${empty orderComplete.arrivalDate ? '8/21(금)' : orderComplete.arrivalDate}" />
	<c:set var="sellerName"
		value="${empty orderComplete.sellerName ? '주식회사 회사이름' : orderComplete.sellerName}" />
	<c:set var="orderAmount"
		value="${empty orderComplete.orderAmount ? 37700 : orderComplete.orderAmount}" />
	<c:set var="discountAmount"
		value="${empty orderComplete.discountAmount ? 17500 : orderComplete.discountAmount}" />
	<c:set var="shippingFee"
		value="${empty orderComplete.shippingFee ? 0 : orderComplete.shippingFee}" />
	<c:set var="paymentAmount"
		value="${empty orderComplete.paymentAmount ? 20200 : orderComplete.paymentAmount}" />

	<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>

	<div class="page-bg">
		<main class="content-wrap">
			<section class="order-card">
				<div class="title-row">
					<h1>주문완료</h1>
					<ol class="order-steps">
						<li>01 옵션 선택</li>
						<li>02 장바구니</li>
						<li>03 주문/결제</li>
						<li class="active">04 주문완료</li>
					</ol>
				</div>

				<div class="complete-message">주문이 완료되었습니다. 감사합니다!</div>

				<section class="delivery-section">
					<h2>상품배송 정보</h2>

					<button class="delivery-summary" type="button"
						onclick="toggleDelivery()">
						<span> <strong>${arrivalDate} 도착 예정 (상품 1개)</strong> <small>판매자
								: ${sellerName}</small>
						</span> <span id="deliveryArrow" class="arrow">⌄</span>
					</button>

					<div id="deliveryDetail" class="info-grid">
						<section class="receiver-info">
							<h3>받는사람 정보</h3>
							<dl>
								<div>
									<dt>받는사람</dt>
									<dd>
										<strong>${receiverName}</strong> / ${receiverPhone}
									</dd>
								</div>
								<div>
									<dt>받는주소</dt>
									<dd>
										<strong>${zipcode} ${address}</strong> <a href="#">변경하기 〉</a>
									</dd>
								</div>
								<div>
									<dt>배송요청사항</dt>
									<dd>
										<strong>${requestMsg}</strong> <a href="#">변경하기 〉</a>
									</dd>
								</div>
							</dl>
						</section>

						<section class="payment-info">
							<h3>결제 정보</h3>
							<dl>
								<div>
									<dt>주문금액</dt>
									<dd>
										<fmt:formatNumber value="${orderAmount}" pattern="#,##0" />
										원
									</dd>
								</div>
								<div>
									<dt>할인금액</dt>
									<dd>
										-
										<fmt:formatNumber value="${discountAmount}" pattern="#,##0" />
										원
									</dd>
								</div>
								<div>
									<dt>배송비</dt>
									<dd>
										+
										<fmt:formatNumber value="${shippingFee}" pattern="#,##0" />
										원
									</dd>
								</div>
							</dl>

							<div class="payment-total">
								<span>총 결제금액</span> <strong><em>쿠페이머니</em> <fmt:formatNumber
										value="${paymentAmount}" pattern="#,##0" /><small>원</small></strong>
							</div>
						</section>
					</div>
				</section>

				<div class="button-row">
					<a class="btn btn-outline"
						href="${pageContext.request.contextPath}/order/order_detail?orderNo=${orderNo}">주문 상세보기</a>
					<a class="btn btn-primary"
						href="${pageContext.request.contextPath}/">쇼핑 계속하기</a>
				</div>

				<a class="bottom-banner"
					href="${pageContext.request.contextPath}/wow/join">

					<div class="bottom-banner-icon">
						<span class="rocket">🚀</span>
					</div>

					<div class="banner-copy">
						<span class="banner-label">WOW MEMBERSHIP</span> <strong>
							와우회원이라면 배송비 걱정 없이! </strong> <span class="banner-description">
							무료배송부터 다양한 회원 전용 혜택까지 </span>
					</div>

					<div class="banner-benefit">
						<strong>첫 30일</strong> <span>무료 체험</span>
					</div>

					<div class="banner-arrow">›</div>

				</a>
			</section>

			<aside class="order-side-banner">
				<ul class="order-promotion-banner">
					<li><a href="${pageContext.request.contextPath}/"
						class="order-ad-link"> <img
							src="${pageContext.request.contextPath}/images/ads/beauty.png"
							alt="오늘의 뷰티 특가">
					</a></li>

					<li><a href="${pageContext.request.contextPath}/"
						class="order-ad-link"> <img
							src="${pageContext.request.contextPath}/images/ads/fresh.png"
							alt="신선식품 로켓프레시">
					</a></li>

					<li><a href="${pageContext.request.contextPath}/"
						class="order-ad-link"> <img
							src="${pageContext.request.contextPath}/images/ads/only.png"
							alt="GoodPang 단독 상품">
					</a></li>

					<li><a href="${pageContext.request.contextPath}/"
						class="order-ad-link"> <img
							src="${pageContext.request.contextPath}/images/ads/tech.png"
							alt="디지털 인기상품">
					</a></li>

					<li><a href="${pageContext.request.contextPath}/wow/join"
						class="order-ad-link"> <img
							src="${pageContext.request.contextPath}/images/ads/wow.png"
							alt="와우회원 전용 혜택">
					</a></li>

					<li><a href="${pageContext.request.contextPath}/"
						class="order-ad-link"> <img
							src="${pageContext.request.contextPath}/images/ads/seller.png"
							alt="GoodPang 판매자 모집">
					</a></li>
				</ul>
			</aside>
			
		</main>
	</div>

	<jsp:include page="/inc/footer.jsp" />

	<script>
function toggleDelivery() {
    const detail = document.getElementById('deliveryDetail');
    const arrow = document.getElementById('deliveryArrow');

    const hidden = detail.classList.toggle('hidden');
    arrow.textContent = hidden ? '⌃' : '⌄';
}
</script>

</body>
</html>
