<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>리뷰관리</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/review_list.css">
</head>

<body>
<jsp:include page="/inc/header.jsp" />

<div class="mypang-layout">

	<!-- 왼쪽 MY쿠팡 -->
	<aside class="mypang-sidebar">
		<div class="mypang-logo">MY쿠팡</div>

		<div class="mypang-menu-group">
			<h3>MY 쇼핑</h3>
			<a href="${pageContext.request.contextPath}/order/order_list">주문목록/배송조회</a>
			<a href="#">취소/반품/교환/환불내역</a>
			<a href="#">로켓와우 멤버십 관리</a>
			<a href="#">영수증 조회/출력</a>
		</div>

		<div class="mypang-menu-group">
			<h3>MY 혜택</h3>
			<a href="#">할인쿠폰</a>
			<a href="#">쿠팡캐시</a>
		</div>

		<div class="mypang-menu-group">
			<h3>MY 활동</h3>
			<a href="#">문의하기</a>
			<a href="#">문의내역 확인</a>
			<a href="${pageContext.request.contextPath}/review/available" class="active">리뷰관리</a>
			<a href="#">찜 리스트</a>
		</div>

		<div class="mypang-menu-group">
			<h3>MY 정보</h3>
			<a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a>
			<a href="#">결제수단·쿠페이 관리</a>
			<a href="${pageContext.request.contextPath}/address/list">배송지 관리</a>
			<a href="#">회원 탈퇴</a>
		</div>

		<div class="mypang-help">
			<div>📋 쿠팡문의</div>
			<div>📢 고객의 소리</div>
			<div>📦 취소/반품안내</div>
		</div>
	</aside>

	<!-- 중앙 -->
	<main class="review-page">
		<h1 class="review-title">리뷰관리</h1>

		<!-- 회원 정보 -->
		<div class="review-member-box">
			<div class="member-profile">
				<div class="member-avatar">👤</div>
				<div class="member-name">
					<c:choose>
						<c:when test="${not empty sessionScope.loginMember}">
							${sessionScope.loginMember.memberName}
						</c:when>
						<c:otherwise>회원</c:otherwise>
					</c:choose>
				</div>
			</div>

			<div class="member-divider"></div>

			<div class="member-stat">
				<span class="member-stat-title">작성 가능한 리뷰</span>
				<div class="member-stat-value">
					<strong>${availableCount}</strong>
					<span>개</span>
				</div>
			</div>

			<div class="member-divider"></div>

			<div class="member-stat">
				<span class="member-stat-title">작성한 리뷰</span>
				<div class="member-stat-value">
					<strong>${writtenCount}</strong>
					<span>개</span>
				</div>
			</div>
		</div>

		<!-- 탭 -->
		<div class="review-tabs review-tab-buttons">
			<button type="button" class="review-tab-link active" id="availableTab"
				onclick="showReviewTab('available')">
				작성 가능한 리뷰 <span>${availableCount}</span>
			</button>

			<button type="button" class="review-tab-link" id="writtenTab"
				onclick="showReviewTab('written')">
				작성한 리뷰 <span>${writtenCount}</span>
			</button>
		</div>

		<!-- 작성 가능한 리뷰 -->
		<div id="availableReviewList" class="review-list">
			<c:set var="hasAvailable" value="false" />

			<c:forEach var="review" items="${reviewList}">
				<c:if test="${not review.reviewWritten}">
					<c:set var="hasAvailable" value="true" />

					<article class="review-item">
						<div class="review-product">
							<div class="available-product-image">
								<c:choose>
									<c:when test="${not empty review.productImage}">
										<img src="${pageContext.request.contextPath}${review.productImage}"
											alt="${review.productName}">
									</c:when>
									<c:otherwise>
										<img src="${pageContext.request.contextPath}/images/product/default-product.png"
											alt="${review.productName}">
									</c:otherwise>
								</c:choose>
							</div>

							<div class="product-info">
								<a href="${pageContext.request.contextPath}/product?productNo=${review.productNo}"
									class="product-name">${review.productName}</a>

								<c:if test="${not empty review.optionText}">
									<div class="product-option">${review.optionText}</div>
								</c:if>

								<div class="available-product-status">배송 완료</div>
							</div>
						</div>

						<div class="available-review-action">
							<p class="available-guide">상품은 어떠셨나요?</p>
							<a href="${pageContext.request.contextPath}/review/write?orderDetailNo=${review.orderDetailNo}&productNo=${review.productNo}"
								class="review-write-btn">리뷰 작성하기</a>
						</div>
					</article>
				</c:if>
			</c:forEach>

			<c:if test="${not hasAvailable}">
				<div class="review-empty">
					<div class="empty-icon">✎</div>
					<p>작성 가능한 리뷰가 없습니다.</p>
					<a href="${pageContext.request.contextPath}/order/order_list"
						class="order-btn">주문내역 확인하기</a>
				</div>
			</c:if>
		</div>

		<!-- 작성한 리뷰 -->
		<div id="writtenReviewList" class="review-list" style="display:none;">
			<c:set var="hasWritten" value="false" />

			<c:forEach var="review" items="${reviewList}">
				<c:if test="${review.reviewWritten}">
					<c:set var="hasWritten" value="true" />

					<article class="review-item">
						<div class="review-product">
							<div class="available-product-image">
								<c:choose>
									<c:when test="${not empty review.productImage}">
										<img src="${pageContext.request.contextPath}${review.productImage}"
											alt="${review.productName}">
									</c:when>
									<c:otherwise>
										<img src="${pageContext.request.contextPath}/images/product/default-product.png"
											alt="${review.productName}">
									</c:otherwise>
								</c:choose>
							</div>

							<div class="product-info">
								<a href="${pageContext.request.contextPath}/product?productNo=${review.productNo}"
									class="product-name">${review.productName}</a>

								<c:if test="${not empty review.optionText}">
									<div class="product-option">${review.optionText}</div>
								</c:if>

								<div class="available-product-status">리뷰 작성 완료</div>
							</div>
						</div>

						<div class="available-review-action">
							<a href="${pageContext.request.contextPath}/review/detail?orderDetailNo=${review.orderDetailNo}"
								class="review-write-btn">리뷰 보기</a>

							<a href="${pageContext.request.contextPath}/review/edit?orderDetailNo=${review.orderDetailNo}"
								class="review-edit-btn">수정하기</a>
						</div>
					</article>
				</c:if>
			</c:forEach>

			<c:if test="${not hasWritten}">
				<div class="review-empty">
					<div class="empty-icon">✎</div>
					<p>작성한 리뷰가 없습니다.</p>
				</div>
			</c:if>
		</div>
	</main>

	<aside class="address-right-area">
		<jsp:include page="/inc/right_banner.jsp" />
	</aside>
</div>

<jsp:include page="/inc/footer.jsp" />

<script>
function showReviewTab(tab) {
	const availableList = document.getElementById("availableReviewList");
	const writtenList = document.getElementById("writtenReviewList");
	const availableTab = document.getElementById("availableTab");
	const writtenTab = document.getElementById("writtenTab");

	if (tab === "available") {
		availableList.style.display = "block";
		writtenList.style.display = "none";
		availableTab.classList.add("active");
		writtenTab.classList.remove("active");
	} else {
		availableList.style.display = "none";
		writtenList.style.display = "block";
		availableTab.classList.remove("active");
		writtenTab.classList.add("active");
	}
}
</script>

</body>
</html>