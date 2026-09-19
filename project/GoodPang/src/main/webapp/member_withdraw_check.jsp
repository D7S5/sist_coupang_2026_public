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
<title>GoodPang | 회원 탈퇴</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/member_withdraw_check.css">
</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="withdraw-page">
		<div class="withdraw-layout">

			<main class="withdraw-container">

				<div class="withdraw-header">
					<h1>회원 탈퇴</h1>
					<div class="withdraw-step">
						<span>01 본인 인증</span> <span class="arrow">›</span> <strong>02
							GoodPang 이용내역 확인</strong> <span class="arrow">›</span> <span>03
							회원탈퇴 완료</span>
					</div>
				</div>

				<section class="withdraw-guide">
					<h2>아래 내용 확인 후 탈퇴를 진행해주세요.</h2>
					<p>GoodPang 회원 탈퇴 시 진행 중인 주문 및 이용 중인 서비스가 있는 경우 탈퇴가 제한될 수 있습니다.</p>
				</section>

				<section class="history-section">
					<h3>진행 중인 주문</h3>

					<div class="history-table">
						<div class="table-head order-grid">
							<span>주문번호</span> <span>주문일</span> <span>주문상태</span> <span>결제금액</span>
						</div>

						<c:choose>
							<c:when test="${not empty activeOrders}">
								<c:forEach var="order" items="${activeOrders}">
									<div class="table-row order-grid">
										<span>${order.orderNo}</span> <span><fmt:formatDate
												value="${order.orderDate}" pattern="yyyy.MM.dd" /></span> <span>${order.orderStatus}</span>
										<span><fmt:formatNumber value="${order.totalPrice}"
												pattern="#,###" />원</span>
									</div>
								</c:forEach>
							</c:when>

							<c:otherwise>
								<div class="empty-row">진행 중인 주문이 없습니다.</div>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

				<section class="history-section">
					<h3>진행 중인 취소 / 반품 / 환불</h3>

					<div class="history-table">
						<div class="table-head cancel-grid">
							<span>구분</span> <span>접수일</span> <span>상품명</span> <span>주문번호</span>
						</div>

						<c:choose>
							<c:when test="${not empty refundList}">
								<c:forEach var="refund" items="${refundList}">
									<div class="table-row cancel-grid">
										<span>${refund.type}</span> <span>${refund.requestDate}</span>
										<span>${refund.productName}</span> <span>${refund.orderNo}</span>
									</div>
								</c:forEach>
							</c:when>

							<c:otherwise>
								<div class="empty-row">진행 중인 취소, 반품 또는 환불 건이 없습니다.</div>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

				<section class="history-section">
					<h3>와우 멤버십</h3>

					<div class="history-table">
						<div class="table-head membership-grid">
							<span>멤버십 이용 상태</span> <span>다음 결제일</span>
						</div>

						<div class="table-row membership-grid">
							<span> <c:choose>
									<c:when test="${wowCancelPending}">
										<strong>해지 신청 완료</strong>
									</c:when>

									<c:when test="${wowActive}">
										<strong class="status-active">이용 중</strong>
									</c:when>

									<c:otherwise>
                        이용 중인 멤버십 없음
                    </c:otherwise>
								</c:choose>
							</span> <span> <c:choose>
									<c:when test="${wowCancelPending}">
                        -
                    </c:when>

									<c:when test="${wowActive}">
                        ${nextPaymentDate}
                    </c:when>

									<c:otherwise>
                        -
                    </c:otherwise>
								</c:choose>
							</span>
						</div>
					</div>

					<c:choose>
						<c:when test="${wowCancelPending}">
							<p class="warning-text">와우 멤버십 해지 신청이 완료되었습니다. 회원 탈퇴를 진행할 수
								있습니다.</p>
						</c:when>

						<c:when test="${wowActive}">
							<p class="warning-text">와우 멤버십을 이용 중입니다. 해지 신청 후 회원 탈퇴가
								가능합니다.</p>

							<a href="${pageContext.request.contextPath}/wow/membership"
								class="btn-wow-cancel"> 와우 멤버십 해지하기 </a>
						</c:when>
					</c:choose>
				</section>


				<section class="history-section">
					<h3>GoodPay 머니</h3>

					<div class="history-table">
						<div class="money-row">
							<span>보유 GoodPay 머니</span> <strong><fmt:formatNumber
									value="${goodPayBalance}" pattern="#,###" />원</strong>
						</div>
					</div>

					<c:if test="${goodPayBalance > 0}">
						<p class="warning-text">보유하고 있는 GoodPay 머니가 있습니다. 탈퇴 전 잔액을
							확인해주세요.</p>
					</c:if>
				</section>

				<section class="history-section">
					<h3>환불 예정 금액</h3>

					<div class="history-table">
						<div class="money-row">
							<span>환불 처리 예정 금액</span> <strong><fmt:formatNumber
									value="${refundAmount}" pattern="#,###" />원</strong>
						</div>
					</div>
				</section>

				<section class="history-section">
					<h3>소멸 예정 금액</h3>
					<p class="section-description">무료로 지급된 쿠폰 및 혜택은 회원 탈퇴 시 즉시 소멸되며
						복구되지 않습니다.</p>

					<div class="history-table">
						<div class="money-row">
							<span>GoodPang 쿠폰</span> <strong>${couponCount}개</strong>
						</div>
					</div>
				</section>

				<form id="withdrawCheckForm"
					action="${pageContext.request.contextPath}/member/withdraw/complete"
					method="post">

					<label class="final-agree"> <input type="checkbox"
						id="finalAgree"> <span>위 이용내역과 소멸 예정 혜택을 확인하였으며 회원
							탈퇴에 동의합니다.</span>
					</label>

					<c:if test="${not empty withdrawError}">
						<p class="withdraw-error">${withdrawError}</p>
					</c:if>

					<div class="withdraw-buttons">
						<a href="${pageContext.request.contextPath}/member/withdraw"
							class="btn-prev">이전</a>
						<button type="submit" class="btn-withdraw"
						    ${(withdrawBlocked or (wowActive and not wowCancelPending)) ? 'disabled' : ''}>
						    탈퇴하기
						</button>
					</div>
				</form>

			</main>

			<div class="withdraw-right-banner">
				<jsp:include page="/inc/right_banner.jsp" />
			</div>

		</div>
	</div>

	<jsp:include page="/inc/footer.jsp" />

<script>
const form = document.getElementById("withdrawCheckForm");
const agree = document.getElementById("finalAgree");
const wowBlocked = ${wowActive and not wowCancelPending};

form.addEventListener("submit", function(e) {
    if (wowBlocked) {
        alert("와우 멤버십 해지 신청 후 회원 탈퇴가 가능합니다.");
        e.preventDefault();
        return;
    }

    if (!agree.checked) {
        alert("회원 탈퇴에 동의해주세요.");
        e.preventDefault();
    }
});
</script>

</body>
</html>