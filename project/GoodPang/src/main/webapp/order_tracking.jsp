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

<title>배송 조회 - GoodPang</title>

<!-- 공통 CSS 및 마이페이지 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/order_list.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/order_tracking.css">

<!-- jQuery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

</head>
<body>

	<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>

	<!-- 마이페이지 3단 표준 레이아웃 -->
	<div class="mypage-container">

		<!-- [1열] 좌측 MY쿠팡 메뉴 -->
		<aside class="mycoupang-side">
			<div class="side-title">MY쿠팡</div>
			<div class="side-section">
				<h3>MY 쇼핑</h3>
				<a href="${pageContext.request.contextPath}/order/order_list" class="active">주문목록/배송조회</a> 
				<a href="${pageContext.request.contextPath}/order/cancel_history">취소/반품/교환/환불 내역</a> 
				<a href="#">와우 멤버십</a>
				<a href="#">구독 서비스 <span class="new">N</span></a> 
				<a href="#">로켓프레시 프레시백 <span class="new">N</span></a> 
				<a href="#">영수증 조회/출력</a>
			</div>
			<div class="side-section">
				<h3>MY 혜택</h3>
				<a href="#">쿠폰 · 이용권</a> 
				<a href="#">쿠팡캐시/기프트카드</a>
			</div>
			<div class="side-section">
				<h3>MY 활동</h3>
				<a href="#">문의하기</a> 
				<a href="#">문의내역 확인</a> 
				<a href="#">리뷰관리</a> 
				<a href="#">찜 리스트</a>
			</div>
			<div class="side-section">
				<h3>MY 정보</h3>
				<a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a> 
				<a href="#">결제수단·쿠페이 관리</a> 
				<a href="#">배송지 관리</a> 
				<a href="#">패스키 관리</a> 
				<a href="#">회원 탈퇴</a>
			</div>
			<div class="side-help">
				<a href="#"> <span class="help-icon">📝</span> <span>쿠팡문의</span> </a> 
				<a href="#"> <span class="help-icon">📢</span> <span>고객의 소리<br><small>제안·칭찬·불편신고</small></span> </a> 
				<a href="#"> <span class="help-icon">📦</span> <span>취소/반품 안내</span> </a>
			</div>
		</aside>

		<!-- [2열] 중앙 메인 본문 -->
		<main class="mypage-main">

			<h2 class="content-heading">배송 조회</h2>

			<!-- 상단 배송 상태 회색 안내 박스 -->
			<div class="tracking-status-banner">
				<h1>
					<c:if test="${not empty trackingInfo.logList}">
						<fmt:formatDate value="${trackingInfo.logList[0].logTime}" pattern="M/d(E)" /> 
					</c:if>
					<c:choose>
						<c:when test="${trackingInfo.orderStatus eq '배송완료'}">도착 완료</c:when>
						<c:otherwise>${trackingInfo.orderStatus}</c:otherwise>
					</c:choose>
				</h1>
				<p>
					<c:choose>
						<c:when test="${trackingInfo.orderStatus eq '배송완료'}">
							고객님이 주문하신 상품이 배송완료 되었습니다.
						</c:when>
						<c:when test="${trackingInfo.orderStatus eq '배송중' or trackingInfo.orderStatus eq '배송시작'}">
							고객님이 주문하신 상품이 빠르게 배송 중입니다.
						</c:when>
						<c:otherwise>
							고객님의 상품을 신속하게 준비하고 있습니다.
						</c:otherwise>
					</c:choose>
				</p>
			</div>

			<!-- 배송 정보 카드 (좌: 송장정보 / 우: 수령지) -->
			<div class="tracking-info-card">
    
				<div class="info-left">
					<div class="rocket-icon">🚀</div>
					<div class="rocket-details">
						<strong>로켓배송</strong>
						<span class="invoice-num">송장번호: ${trackingInfo.invoiceNo}</span>
						<p class="delivery-notice">※ 배송업무 중 연락을 받을 수 없습니다.</p>
					</div>
				</div>

				<div class="info-right">
					<table class="receiver-table">
						<tr>
							<th>받는사람</th>
							<td>${trackingInfo.memberName}</td>
						</tr>
						<tr>
							<th>받는주소</th>
							<td>${trackingInfo.address} ${trackingInfo.detailAddress}</td>
						</tr>
						<tr>
							<th>배송요청사항</th>
							<td>${trackingInfo.requestMsg}</td>
						</tr>
						<tr>
							<th>상품수령방법</th>
							<td class="receive-badge">${trackingInfo.receiveLocation}</td>
						</tr>
					</table>
				</div>

			</div>

			<!-- 시간대별 배송 추적 이력 테이블 -->
			<div class="tracking-log-box">
				<table class="tracking-table">
					<thead>
						<tr>
							<th style="width: 35%;">시간</th>
							<th style="width: 35%;">현재위치</th>
							<th style="width: 30%;">배송상태</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty trackingInfo.logList}">
								<c:forEach var="log" items="${trackingInfo.logList}">
									<tr>
										<td><fmt:formatDate value="${log.logTime}" pattern="yyyy년 M월 d일 HH:mm" /></td>
										<td>${log.currentLocation}</td>
										<td class="status-cell">${log.deliveryStatus}</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan="3" style="text-align: center; padding: 30px; color: #888;">
										배송 추적 상세 이력 정보가 준비 중입니다.
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
			
			<!-- 하단 FAQ 슬라이드 아코디언 영역 -->
			<div class="faq-section">
				<h3>배송에 대해 궁금한 점이 있으십니까?</h3>

				<!-- 아코디언 1 -->
				<div class="faq-item">
					<div class="faq-q">
						<span>Q. [상품누락] 상품을 구매했는데 일부만 배송되었어요.</span>
						<span class="faq-arrow">▼</span>
					</div>
					<div class="faq-a">
						<p>상품이 누락되었다면 교환을 통해 상품을 다시 받으시거나, 반품 후 환불받으실 수 있습니다.</p>
						<p style="margin-top: 6px; color: #0073e9; font-weight: bold;">
							<a href="${pageContext.request.contextPath}/order/cancel_history" style="color: #0073e9; text-decoration: none;">&gt; 교환/반품 신청 바로가기</a>
						</p>
					</div>
				</div>

				<!-- 아코디언 2 -->
				<div class="faq-item">
					<div class="faq-q">
						<span>Q. [환불] 반품 신청을 했는데, 언제 환불되나요?</span>
						<span class="faq-arrow">▼</span>
					</div>
					<div class="faq-a">
						<p>반품 상품이 물류센터에 입고되어 검수 완료된 후 1~3일(영업일 기준) 이내에 결제하신 수단으로 환불 처리됩니다.</p>
					</div>
				</div>

				<!-- 아코디언 3 -->
				<div class="faq-item">
					<div class="faq-q">
						<span>Q. [배송완료미수령] 상품을 받지 못했는데 배송완료로 확인됩니다.</span>
						<span class="faq-arrow">▼</span>
					</div>
					<div class="faq-a">
						<p>문 앞, 경비실, 택배함 등 수령 장소를 다시 한번 확인해 주세요. 미수령 시 고객센터(1577-7011)로 문의해 주시기 바랍니다.</p>
					</div>
				</div>

				<!-- 아코디언 4 -->
				<div class="faq-item">
					<div class="faq-q">
						<span>Q. [교환/반품] 상품을 교환/반품하고 싶어요.</span>
						<span class="faq-arrow">▼</span>
					</div>
					<div class="faq-a">
						<p>[마이쿠팡 &gt; 취소/반품/교환 내역] 메뉴에서 간편하게 신청하실 수 있습니다.</p>
					</div>
				</div>

			</div>

		</main>
		<jsp:include page="/inc/right_banner.jsp" />

	
	</div>
	

	<jsp:include page="/inc/footer.jsp" />

	<!-- 슬라이드 아코디언 & 마우스 커서 호버 JS Script -->
	<script>
		$(document).ready(function() {
			// 1. 아코디언 클릭 시 Slide Toggle 애니메이션
			$('.faq-q').on('click', function() {
				var $item = $(this).closest('.faq-item');
				var $answer = $item.find('.faq-a');
				
				// 클릭한 항목 제외 다른 항목 접기 (선택사항)
				$('.faq-item').not($item).removeClass('active').find('.faq-a').slideUp(200);
				
				// 현재 클릭한 답변 토글
				$item.toggleClass('active');
				$answer.stop().slideToggle(200);
			});
		});
	</script>

</body>

</html>