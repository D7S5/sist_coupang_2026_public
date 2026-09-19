<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>

<meta charset="UTF-8">

<title>주문상세 - GoodPang</title>

<!-- 파비콘 설정 -->
<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

<!-- 기본 초기화 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">

<!-- 공통 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">

<!-- 주문상세 전용 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/order_detail.css">

<!-- jQuery -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<!-- 주문상세 전용 JS -->
<script src="${pageContext.request.contextPath}/js/order_detail.js"></script>

</head>

<body>
	<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>
	
	<div class="order-detail-wrap">

		<!-- =========================
         [1열] 왼쪽 MY쿠팡 메뉴
    ========================== -->
		<jsp:include page="/inc/left_banner.jsp">
		    <jsp:param name="activeMenu" value="order_list" />
		</jsp:include>

		<!-- =========================
         [2열] 가운데 본문
    ========================== -->
		<main class="order-content">

			<!-- 주문 상세 제목 -->
			<section class="order-header">

				<h1>주문상세</h1>

				<div class="order-info">
					<strong><fmt:formatDate value="${orderInfo.orderDate}" pattern="yyyy. M. d" /></strong> 주문 
					<span class="dot">·</span> 주문번호 
					<span class="order-number">${orderInfo.orderNo}</span>
				</div>

			</section>

			<!-- =========================
			     배송 상품 리스트 (JSTL 반복문)
			========================== -->
			<c:forEach var="item" items="${detailList}">
				<section class="delivery-box" style="border: 1px solid #e0e0e0; border-radius: 8px; padding: 16px; margin-bottom: 20px; background: #fff;">

					<div class="delivery-main">

						<div class="delivery-title" style="font-size: 16px; font-weight: bold; color: #00891a; margin-bottom: 12px;">
							<strong>${item.orderStatus}</strong>
						</div>

						<div class="product-row" style="display: flex; align-items: center; justify-content: space-between; padding: 10px 0;">

							<!-- 상품 이미지 영역 -->
							<div class="product-image" style="width: 72px; height: 72px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 6px; overflow: hidden; margin-right: 15px; flex-shrink: 0;">
							    <c:choose>
							        <c:when test="${not empty item.imageUrl}">
							            <img src="${pageContext.request.contextPath}/${item.imageUrl}" alt="${item.productName}" style="width: 100%; height: 100%; object-fit: cover;" />
							        </c:when>
							    </c:choose>
							</div>

							<!-- 상품 정보 -->
							<div class="product-info" style="flex: 1;">

								<div class="product-name" style="font-size: 14px; font-weight: bold; margin-bottom: 4px;">
									<span class="rocket" style="color: #0073e9;">🚀 로켓배송</span> 
									<a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}" style="color: #333; text-decoration: none;">
										${item.productName}
									</a>
								</div>

								<div class="product-price" style="font-size: 13px; color: #333; margin-bottom: 4px;">
									<fmt:formatNumber value="${item.itemPrice * item.quantity}" pattern="#,###" />원 
									<span>·</span> ${item.quantity}개
								</div>

								<!-- 옵션 정보 동적 출력 -->
								<c:if test="${not empty item.option1Value or not empty item.option2Value}">
									<div class="product-option" style="font-size: 12px; color: #666; margin-top: 4px;">
										<span>옵션: </span>
										<c:if test="${not empty item.option1Value}">
											<c:if test="${not empty item.option1Type}">${item.option1Type}: </c:if>${item.option1Value}
										</c:if>
										<c:if test="${not empty item.option1Value and not empty item.option2Value}"> / </c:if>
										<c:if test="${not empty item.option2Value}">
											<c:if test="${not empty item.option2Type}">${item.option2Type}: </c:if>${item.option2Value}
										</c:if>
									</div>
								</c:if>

							</div>

							<button type="button" class="cart-btn"
							    onclick="addCart('${item.optionId}', '${item.quantity}')"
							    style="padding: 8px 12px; border: 1px solid #ccc; background: #fff; border-radius: 4px; font-size: 12px; cursor: pointer;">
							    장바구니 담기
							</button>

						</div>

					</div>

					<!-- =========================================================
					     하단 액션 버튼 그룹 (주문 상태별 정확한 분기 - order_list와 동일)
					     ========================================================= -->
					<c:choose>
						<%-- [1] 주문 취소 관련 상태 -> [취소 내역 조회] 단일 넓은 버튼만 출력 --%>
						<c:when test="${item.orderStatus eq '취소처리' or item.orderStatus eq '주문취소' or item.orderStatus eq '취소완료'}">
							<div class="delivery-buttons">
								<button type="button" class="delivery-btn btn-action"
									onclick="location.href='${pageContext.request.contextPath}/order/cancel_history'">
									취소 내역 조회
								</button>
							</div>
						</c:when>

						<%-- [2] 결제 완료 상태 -> [배송 조회] | [주문 취소] 2개 버튼만 출력 --%>
						<c:when test="${item.orderStatus eq '결제완료' or item.orderStatus eq '결제 완료'}">
							<div class="delivery-buttons">
								<button type="button" class="delivery-btn btn-action primary"
									onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${orderInfo.orderNo}'">
									배송 조회
								</button>
								<button type="button" class="delivery-btn btn-action"
									onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${orderInfo.orderNo}'">
									주문 취소
								</button>
							</div>
						</c:when>

						<%-- [3] 배송중 상태 -> [배송 조회] | [교환, 반품 신청] 2개 버튼만 출력 --%>
						<c:when test="${item.orderStatus eq '배송중' or item.orderStatus eq '배송 중' or item.orderStatus eq '배송시작'}">
							<div class="delivery-buttons">
								<button type="button" class="delivery-btn btn-action primary"
									onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${orderInfo.orderNo}'">
									배송 조회
								</button>
								<button type="button" class="delivery-btn btn-action"
									onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${orderInfo.orderNo}'">
									교환, 반품 신청
								</button>
							</div>
						</c:when>

						<%-- [4] 배송완료 등 기타 상태 -> [배송 조회] | [교환, 반품 신청] | [리뷰 작성/보기] 3개 버튼 모두 출력 --%>
						<c:otherwise>
							<div class="delivery-buttons">
								<!-- [1] 배송조회 버튼 -->
								<button type="button" class="delivery-btn btn-action primary"
									onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${orderInfo.orderNo}'">
									배송 조회
								</button>

								<!-- [2] 교환, 반품 신청 버튼 -->
								<button type="button" class="delivery-btn btn-action"
									onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${orderInfo.orderNo}'">
									교환, 반품 신청
								</button>

								<!-- [3] 리뷰 작성 / 작성한 리뷰 보기 버튼 (배송완료건만 노출) -->
								<c:set var="isReviewWritten" value="false" />
								<c:forEach var="review" items="${reviewList}">
									<c:if test="${review.orderDetailNo eq item.orderDetailNo and review.reviewWritten}">
										<c:set var="isReviewWritten" value="true" />
									</c:if>
								</c:forEach>

								<c:choose>
									<c:when test="${isReviewWritten}">
										<button type="button" class="delivery-btn btn-action"
											onclick="location.href='${pageContext.request.contextPath}/review/available'">
											작성한 리뷰 보기
										</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="delivery-btn btn-action"
											onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${item.orderDetailNo}&productNo=${item.productNo}'">
											리뷰 작성하기
										</button>
									</c:otherwise>
								</c:choose>
							</div>
						</c:otherwise>
					</c:choose>
					<!-- // .delivery-buttons 닫기 -->

				</section>
			</c:forEach>

			<!-- =========================
			     받는사람 정보
			========================== -->
			<section class="detail-section">

				<h2>받는사람 정보</h2>

				<div class="section-line"></div>

				<div class="info-table">

					<div class="info-row">
						<div class="info-title">받는사람</div>
						<div class="info-value">${orderInfo.memberName}</div>
					</div>

					<div class="info-row">
						<div class="info-title">연락처</div>
						<div class="info-value">${orderInfo.phone}</div>
					</div>

					<div class="info-row">
						<div class="info-title">받는주소</div>
						<div class="info-value">${orderInfo.address} ${orderInfo.detailAddress}</div>
					</div>

					<div class="info-row">
						<div class="info-title">배송요청사항</div>
						<div class="info-value">${not empty orderInfo.requestMsg ? orderInfo.requestMsg : '요청사항 없음'}</div>
					</div>

				</div>

			</section>

			<!-- =========================
			     결제 정보
			========================== -->
			<section class="detail-section payment-section">
				<h2>결제 정보</h2>
				<div class="section-line"></div>

				<div class="payment-box">

					<div class="payment-method">
						<c:choose>
							<%-- 1. 카드 결제 시: 카드사명 / 일시불 --%>
							<c:when test="${not empty orderInfo.cardCompanyName}">
								${orderInfo.cardCompanyName} / 일시불
							</c:when>
							
							<%-- 2. 계좌이체 결제 시: 은행명 / 계좌이체 --%>
							<c:when test="${not empty orderInfo.bankName}">
								${orderInfo.bankName} / 계좌이체
							</c:when>
							
							<%-- 3. 그 외 결제 방식 --%>
							<c:otherwise>
								${orderInfo.paymentMethod}
							</c:otherwise>
						</c:choose>
					</div>

					<div class="payment-price">
						<div class="price-row">
							<span>총 상품가격</span> 
							<strong> 
								<fmt:formatNumber value="${orderInfo.totalPrice - orderInfo.deliveryFee}" pattern="#,###" /> 원
							</strong>
						</div>
						<div class="price-row">
							<span>배송비</span> 
							<strong> 
								<fmt:formatNumber value="${orderInfo.deliveryFee}" pattern="#,###" /> 원
							</strong>
						</div>
					</div>

				</div>

				<div class="payment-total">
					<div>
						<c:choose>
							<c:when test="${not empty orderInfo.cardCompanyName}">
								${orderInfo.cardCompanyName} / 일시불
							</c:when>
							<c:when test="${not empty orderInfo.bankName}">
								${orderInfo.bankName} / 계좌이체
							</c:when>
							<c:otherwise>
								${orderInfo.paymentMethod}
							</c:otherwise>
						</c:choose>
					</div>

					<div>
						<span>총 결제금액</span> 
						<strong> 
							<fmt:formatNumber value="${orderInfo.totalPrice}" pattern="#,###" /> 원
						</strong>
					</div>

				</div>

			</section>

			<!-- =========================
			     결제영수증 정보
			========================== -->
			<section class="detail-section receipt-section">

				<h2>결제영수증 정보</h2>

				<div class="section-line"></div>

				<div class="receipt-row">
					<span> 해당 주문건에 대해 구매 카드영수증 확인이 가능합니다. </span>
					<button type="button" class="receipt-btn" id="cardReceiptBtn">카드영수증</button>
				</div>

				<div class="receipt-row">
					<span> 해당 주문건에 대해 거래명세서 확인이 가능합니다. </span>
					<button type="button" class="receipt-btn" id="statementBtn">거래명세서</button>
				</div>

			</section>

			<!-- 배송상품 주문상태 안내 -->
			<div class="delivery-step-box">
				<div class="step-head">
					<span>배송상품 주문상태 안내</span> 
					<a href="#" class="link-more">자세한 내용 더보기 &gt;</a>
				</div>
				<div class="step-flow">
					<div class="step-item">
						<div class="icon-circle">💳</div>
						<strong>결제완료</strong>
						<p>주문·결제 확인이 완료되었습니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">📦</div>
						<strong>상품준비중</strong>
						<p>판매자가 발송할 상품을 준비중입니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">🚚</div>
						<strong>배송시작</strong>
						<p>상품준비가 완료되어 곧 배송될 예정입니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">🚛</div>
						<strong>배송중</strong>
						<p>상품이 고객님께 배송중입니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">🎁</div>
						<strong>배송완료</strong>
						<p>상품이 주문자에게 전달완료되었습니다.</p>
					</div>
				</div>
			</div>

			<!-- 취소/반품/교환 안내 -->
			<div class="notice-info-box">
				<p class="notice-title">⚠ 취소/반품/교환 신청전 확인해주세요!</p>

				<div class="notice-sec">
					<h4>취소</h4>
					<ul>
						<li>여행/레저/숙박 상품은 취소 시 수수료가 발생할 수 있으며,</li>
						<li>취소수수료를 확인하여 2일 이내(주말,공휴일 제외 처리결과)를 문자로 안내드립니다.(당일 접수 기준, 마감시간 오후 4시)</li>
						<li>문화 상품은 사용 전날 24시까지 취소 신청 시 취소수수료가 발생되지 않습니다.</li>
					</ul>
				</div>
			</div>

		</main>

		<!-- 우측 날개 배너 모듈 include -->
		<jsp:include page="/inc/right_banner.jsp" />
	</div>
	<!-- //.order-detail-wrap -->

	<jsp:include page="/inc/footer.jsp" />

</body>
</html>