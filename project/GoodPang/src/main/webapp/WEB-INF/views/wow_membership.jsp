<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<title>와우 멤버십 관리</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/wow_membership.css">
</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="wow-page">
				<!-- 개인정보확인/수정 메뉴 파란색 활성화 -->
		<jsp:include page="/inc/left_banner.jsp">
		    <jsp:param name="activeMenu" value="wow" />
		</jsp:include>

		<main class="wow-content">
			<div class="wow-summary-bar">
				<div class="wow-summary-item">
					<strong>쿠페이 머니</strong><span>0원</span>
				</div>
				<div class="wow-summary-item">
					<strong>쿠팡캐시</strong><span>0원</span>
				</div>
			</div>

			<c:choose>

				<c:when test="${sessionScope.wowMember}">
					<div class="wow-title-row">
						<h2>와우 멤버십 관리</h2>
						<div class="wow-title-actions">
							<button type="button" class="wow-outline-btn"
								onclick="location.href='${pageContext.request.contextPath}/payment-method/list'">결제수단
								관리</button>
							<c:choose>
								<c:when test="${membership.status eq 'ACTIVE'}">
									<button type="button" id="wowCancelOpenBtn"
										class="wow-outline-btn">해지하기</button>
								</c:when>
								<c:when test="${membership.status eq 'CANCEL_PENDING'}">
									<button type="button" class="wow-outline-btn" disabled>해지
										신청 완료</button>
								</c:when>
							</c:choose>
						</div>
					</div>

					<section class="wow-card">
						<div class="wow-card-header">
							<div class="wow-logo">WOW!</div>
							<div class="wow-name-row">
								<strong>와우 멤버십</strong>
								<c:choose>
									<c:when test="${membership.status eq 'ACTIVE'}">
										<span class="wow-badge">회원</span>
									</c:when>
									<c:when test="${membership.status eq 'CANCEL_PENDING'}">
										<span class="wow-badge">해지 예정</span>
									</c:when>
									<c:otherwise>
										<span class="wow-badge">${membership.status}</span>
									</c:otherwise>
								</c:choose>
							</div>

							<c:if test="${membership.status eq 'ACTIVE'}">
								<p class="wow-next-payment">
									다음 결제 예정일은 <strong><fmt:formatDate
											value="${membership.nextPaymentDate}" pattern="yyyy년 MM월 dd일" /></strong>
									입니다.
								</p>
							</c:if>

							<c:if test="${membership.status eq 'CANCEL_PENDING'}">
								<p class="wow-next-payment">
									멤버십 이용 종료 예정일은 <strong><fmt:formatDate
											value="${membership.endDate}" pattern="yyyy년 MM월 dd일" /></strong>
									입니다.
								</p>
							</c:if>
						</div>

						<div class="wow-divider"></div>

						<div class="wow-saving-title">멤버십 결제수단</div>
						<div class="wow-saving-box">
							<c:choose>
								<c:when test="${membership.paymentType eq 'BANK'}">
									<div class="wow-saving-row">
										<span>등록 계좌</span> <strong>${membership.bankCode}
											****${membership.accountLast4}</strong>
									</div>
									<c:if test="${not empty membership.accountHolder}">
										<div class="wow-saving-sub">예금주
											${membership.accountHolder}</div>
									</c:if>
								</c:when>

								<c:when test="${membership.paymentType eq 'CARD'}">
									<div class="wow-saving-row">
										<span>등록 카드</span> <strong>${membership.cardCompany}
											****${membership.cardLast4}</strong>
									</div>
								</c:when>

								<c:otherwise>
									<div class="wow-saving-row">
										<span>결제수단</span> <strong>등록된 결제수단이 없습니다.</strong>
									</div>
								</c:otherwise>
							</c:choose>
						</div>

						<div class="wow-divider"></div>

						<div class="wow-saving-title">와우 멤버십으로 절약한 금액</div>
						<div class="wow-saving-total">
							총 <strong><fmt:formatNumber value="${savingAmount}"
									pattern="#,##0" /></strong>원
						</div>

						<div class="wow-saving-box">
							<div class="wow-saving-row">
								<span>무료배송 (건당 3,000원)</span> <strong><fmt:formatNumber
										value="${deliverySaving}" pattern="#,##0" />원</strong>
							</div>

							<div class="wow-saving-sub">ㄴ 로켓배송 무조건 무료배송</div>
							<div class="wow-saving-sub">ㄴ 로켓와우 새벽배송/당일배송</div>
							<div class="wow-saving-sub">ㄴ 로켓프레시 무료배송</div>

							<div class="wow-saving-row">
								<span>쿠팡 할인</span> <strong><fmt:formatNumber
										value="${discountSaving}" pattern="#,##0" />원</strong>
							</div>

							<div class="wow-saving-row">
								<span>쿠팡이츠 혜택</span> <strong><fmt:formatNumber
										value="${eatsSaving}" pattern="#,##0" />원</strong>
							</div>

							<div class="wow-saving-row">
								<span>쿠팡캐시 적립</span> <strong><fmt:formatNumber
										value="${cashSaving}" pattern="#,##0" />원</strong>
							</div>

							<div class="wow-saving-row">
								<span>30일 무료반품 (건당 5,000원)</span> <strong><fmt:formatNumber
										value="${returnSaving}" pattern="#,##0" />원</strong>
							</div>

							<div class="wow-saving-row">
								<span>로켓직구 무조건 무료배송 (건당 2,500원)</span> <strong><fmt:formatNumber
										value="${globalSaving}" pattern="#,##0" />원</strong>
							</div>
						</div>

						<p class="wow-footnote">
							가입일(
							<fmt:formatDate value="${membership.startDate}"
								pattern="yyyy년 MM월 dd일" />
							)부터 누적 금액 기준, 금액으로 환산 가능한 혜택만 포함
						</p>
					</section>
				</c:when>

				<%-- 일반 회원 --%>
				<c:otherwise>
					<div class="wow-title-row">
						<h2>와우 멤버십</h2>
					</div>

					<section class="wow-join-card">
						<div class="wow-join-logo">WOW!</div>

						<h2 class="wow-join-title">
							와우 멤버십으로<br> 더 많은 혜택을 누려보세요
						</h2>

						<p class="wow-join-desc">
							로켓배송부터 무료반품까지<br> 와우 회원만을 위한 특별한 혜택을 만나보세요.
						</p>

						<div class="wow-benefit-list">
							<div class="wow-benefit-item">
								<div class="wow-benefit-icon">🚚</div>
								<div>
									<strong>로켓배송 무료배송</strong>
									<p>금액 조건 없이 로켓배송 상품을 무료배송으로 받아보세요.</p>
								</div>
							</div>

							<div class="wow-benefit-item">
								<div class="wow-benefit-icon">🌙</div>
								<div>
									<strong>새벽배송 · 당일배송</strong>
									<p>필요한 상품을 더욱 빠르게 받아보세요.</p>
								</div>
							</div>

							<div class="wow-benefit-item">
								<div class="wow-benefit-icon">↩</div>
								<div>
									<strong>30일 무료반품</strong>
									<p>와우 회원은 더욱 편리하게 무료반품을 이용할 수 있어요.</p>
								</div>
							</div>

							<div class="wow-benefit-item">
								<div class="wow-benefit-icon">₩</div>
								<div>
									<strong>와우 전용 할인</strong>
									<p>회원 전용 상품 할인과 다양한 혜택을 만나보세요.</p>
								</div>
							</div>
						</div>

						<div class="wow-join-price">
							<span>월 이용료</span> <strong>7,890원</strong>
						</div>

						<button type="button" class="wow-join-btn"
							onclick="location.href='${pageContext.request.contextPath}/wow/join'">
							와우 멤버십 가입하기</button>

						<p class="wow-join-notice">언제든지 해지할 수 있습니다.</p>
					</section>
				</c:otherwise>

			</c:choose>
		</main>
	</div>

	<c:if test="${sessionScope.wowMember}">
		<div id="wowCancelModal" class="wow-cancel-overlay">
			<div class="wow-cancel-modal">
				<button type="button" id="wowCancelCloseBtn"
					class="wow-cancel-close">&times;</button>

				<div class="wow-cancel-logo">WOW</div>

				<h3>와우 멤버십을 해지하시겠어요?</h3>

				<p>해지하면 무료배송, 무료반품 등 와우 전용 혜택을 더 이상 이용할 수 없습니다.</p>

				<form action="${pageContext.request.contextPath}/wow/cancel"
					method="post">
					<div class="wow-cancel-buttons">
						<button type="button" id="wowCancelKeepBtn"
							class="wow-cancel-keep">멤버십 유지하기</button>

						<button type="submit" class="wow-cancel-submit">해지하기</button>
					</div>
				</form>
			</div>
		</div>
	</c:if>

	<c:if test="${param.canceled eq 'Y'}">
		<script>
        alert("와우 멤버십 해지 신청이 완료되었습니다.");
    </script>
	</c:if>

	<c:if test="${param.alreadyCanceled eq 'Y'}">
		<script>
        alert("이미 해지 신청된 와우 멤버십입니다.");
    </script>
	</c:if>

	<c:if test="${param.alreadyExpired eq 'Y'}">
		<script>
        alert("이미 종료된 와우 멤버십입니다.");
    </script>
	</c:if>

	<c:if test="${param.notMember eq 'Y'}">
		<script>
        alert("가입된 와우 멤버십이 없습니다.");
    </script>
	</c:if>

	<script>
document.addEventListener("DOMContentLoaded", function() {
    const wowCancelOpenBtn = document.getElementById("wowCancelOpenBtn");
    const wowCancelModal = document.getElementById("wowCancelModal");
    const wowCancelCloseBtn = document.getElementById("wowCancelCloseBtn");
    const wowCancelKeepBtn = document.getElementById("wowCancelKeepBtn");

    function closeWowCancelModal() {
        if (wowCancelModal) {
            wowCancelModal.style.display = "none";
        }
    }

    if (wowCancelOpenBtn) {
        wowCancelOpenBtn.addEventListener("click", function() {
            if (wowCancelModal) {
                wowCancelModal.style.display = "flex";
            }
        });
    }

    if (wowCancelCloseBtn) {
        wowCancelCloseBtn.addEventListener("click", closeWowCancelModal);
    }

    if (wowCancelKeepBtn) {
        wowCancelKeepBtn.addEventListener("click", closeWowCancelModal);
    }

    if (wowCancelModal) {
        wowCancelModal.addEventListener("click", function(event) {
            if (event.target === wowCancelModal) {
                closeWowCancelModal();
            }
        });
    }
});
</script>

</body>
</html>
