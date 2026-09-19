<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>주문목록/배송조회 - GoodPang</title>

<!-- 파비콘 설정 -->
<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

<!-- 기본 초기화 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<!-- 공통 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<!-- 주문상세/리스트 전용 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/order_list.css">

<!-- jQuery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<!-- JSP → JS 로 contextPath 전달 -->
<script>
	var contextPath = "${pageContext.request.contextPath}";
</script>
</head>

<body>
	<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>

	<div class="category-layer" id="wa-pc-category"></div>

	<!-- =========================
         마이페이지 3단 레이아웃 (좌: 메뉴 / 중: 본문 / 우: 배너)
    ========================== -->
	<div class="mypage-container">

		<!-- 주문목록 메뉴 파란색 활성화 -->
		<jsp:include page="/inc/left_banner.jsp">
		    <jsp:param name="activeMenu" value="order_list" />
		</jsp:include>

		<!-- =========================
             [2열] 중앙 메인 본문
        ========================== -->
		<main class="mypage-main">

			<!-- 쿠페이/쿠팡캐시 배너 -->
			<div class="mypage-cash-bar">
				<div class="cash-col">
					쿠페이 머니 <span class="cash-val">0원</span>
				</div>
				<div class="cash-divider"></div>
				<div class="cash-col">
					쿠팡캐시 <span class="cash-val">0원</span>
				</div>
			</div>

			<h2 class="content-heading">주문목록</h2>

			<!-- 검색 및 연도/기간 선택 필터 -->
			<div class="order-filter-container">
				<div class="search-input-wrap">
					<input type="text" placeholder="주문한 상품을 검색할 수 있어요!">
					<button type="button" class="btn-search-icon">🔍</button>
				</div>
				<!-- 연도 필터 버튼 -->
				<div class="year-filter-list">
					<button type="button"
						class="btn-year btn-period ${empty yearFilter or yearFilter eq 'recent' ? 'active' : ''}"
						data-year="recent">최근 6개월</button>
					<button type="button"
						class="btn-year btn-period ${yearFilter eq '2026' ? 'active' : ''}"
						data-year="2026">2026</button>
					<button type="button"
						class="btn-year btn-period ${yearFilter eq '2025' ? 'active' : ''}"
						data-year="2025">2025</button>
					<button type="button"
						class="btn-year btn-period ${yearFilter eq '2024' ? 'active' : ''}"
						data-year="2024">2024</button>
				</div>
			</div>

			<!-- 동적으로 출력될 주문 카드 컨테이너 -->
			<div id="order-card-list">
				<c:choose>
					<c:when test="${not empty orderList}">

						<%-- 이전 주문번호를 저장할 변수 초기화 --%>
						<c:set var="prevOrderNo" value="" />
						<c:set var="prevItem" value="" />

						<c:forEach var="item" items="${orderList}" varStatus="status">

							<%-- 1. 새로운 주문번호가 시작될 때 이전 주문 카드 박스 닫기 및 버튼 출력 --%>
							<c:if test="${not empty prevOrderNo and prevOrderNo ne item.orderNo}">
										</div>
										<!-- // .product-list-wrap 닫기 -->

										<!-- 하단 액션 버튼 그룹 (주문 상태별 정확한 분기) -->
										<c:choose>
											<%-- [1] 주문 취소 관련 상태 -> [취소 내역 조회] 단일 넓은 버튼만 출력 --%>
											<c:when test="${prevItem.orderStatus eq '취소처리' or prevItem.orderStatus eq '주문취소' or prevItem.orderStatus eq '취소완료'}">
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/cancel_history'">
														취소 내역 조회
													</button>
												</div>
											</c:when>

											<%-- [2] 결제 완료 상태 -> [배송 조회] | [주문 취소] 2개 버튼만 출력 --%>
											<c:when test="${prevItem.orderStatus eq '결제완료' or prevItem.orderStatus eq '결제 완료'}">
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action primary"
														onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'">
														배송 조회
													</button>
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'">
														주문 취소
													</button>
												</div>
											</c:when>

											<%-- [3] 배송중 상태 -> [배송 조회] | [교환, 반품 신청] 2개 버튼만 출력 --%>
											<c:when test="${prevItem.orderStatus eq '배송중' or prevItem.orderStatus eq '배송 중' or prevItem.orderStatus eq '배송시작'}">
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action primary"
														onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'">
														배송 조회
													</button>
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'">
														교환, 반품 신청
													</button>
												</div>
											</c:when>

											<%-- [4] 배송완료 등 기타 상태 -> [배송 조회] | [교환, 반품 신청] | [리뷰 작성/보기] 3개 버튼 모두 출력 --%>
											<c:otherwise>
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action primary"
														onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'">
														배송 조회
													</button>

													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'">
														교환, 반품 신청
													</button>

													<c:set var="isReviewWritten" value="false" />
													<c:forEach var="review" items="${reviewList}">
														<c:if test="${review.orderDetailNo eq prevItem.orderDetailNo and review.reviewWritten}">
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
																onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${prevItem.orderDetailNo}&productNo=${prevItem.productNo}'">
																리뷰 작성하기
															</button>
														</c:otherwise>
													</c:choose>
												</div>
											</c:otherwise>
										</c:choose>
										<!-- // .delivery-buttons 닫기 -->

									</div>
									<!-- // .delivery-main 닫기 -->
								</section>
								<!-- // .delivery-box 닫기 -->
							</c:if>

							<%-- 2. 새로운 주문번호일 때만 신규 카드 상자(box)와 헤더 출력 --%>
							<c:if test="${prevOrderNo ne item.orderNo}">
								<section class="delivery-box"
									style="border: 1px solid #e0e0e0; border-radius: 8px; padding: 16px; margin-bottom: 20px; background: #fff;">

									<!-- 상단 헤더: 주문 날짜 및 상세보기 -->
									<div class="card-header"
										style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">
										<div class="order-info"
											style="font-size: 14px; font-weight: bold; color: #333;">
											<span><fmt:formatDate value="${item.orderDate}" pattern="yyyy.MM.dd" /> 주문</span> 
											<span style="color: #ccc; margin: 0 8px;">|</span> 
											<span>주문번호 <strong class="order-number">${item.orderNo}</strong></span>
										</div>
										<a href="${pageContext.request.contextPath}/order/order_detail?orderNo=${item.orderNo}"
											class="link-detail"
											style="color: #0073e9; text-decoration: none; font-size: 12px; font-weight: bold;">
											주문 상세보기 &gt;
										</a>
									</div>

									<!-- 카드 본문 -->
									<div class="delivery-main">
										<div class="delivery-status"
											style="font-size: 16px; font-weight: bold; color: #00891a; margin-bottom: 12px;">
											${item.orderStatus}
										</div>

										<!-- 동일 주문 상품 목록 감싸기 -->
										<div class="product-list-wrap">
							</c:if>

							<!-- 동일한 주문번호면 이 product-row 영역만 누적 반복 출력됨 -->
							<div class="product-row"
								style="display: flex; align-items: center; justify-content: space-between; padding: 10px 0; border-bottom: 1px dashed #f0f0f0;">

								<!-- 상품 이미지 영역 -->
								<div class="product-image" style="width: 64px; height: 64px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 6px; overflow: hidden; margin-right: 15px; flex-shrink: 0;">
								    <c:choose>
								        <c:when test="${not empty item.imageUrl}">
								            <img src="${pageContext.request.contextPath}/${item.imageUrl}" alt="${item.productName}" style="width: 100%; height: 100%; object-fit: cover;" />
								        </c:when>
								    </c:choose>
								</div>

								<!-- 의류 상품 정보 명세 -->
								<div class="product-info" style="flex: 1;">
									<div class="product-name" style="font-size: 14px; font-weight: bold; margin-bottom: 4px;">
										<a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}" style="color: #333; text-decoration: none;"> 
											<span class="rocket" style="color: #0073e9;">🚀 로켓배송</span> ${item.productName}
										</a>
									</div>
									<div class="product-price" style="font-size: 13px; color: #333; margin-bottom: 4px;">
										<fmt:formatNumber value="${item.itemPrice * item.quantity}" pattern="#,###" />원
										<span>·</span> ${item.quantity}개
									</div>
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

							</div>
							<!-- // .product-row 닫기 -->

							<%-- 현재 주문번호 및 아이템 정보 업데이트 --%>
							<c:set var="prevOrderNo" value="${item.orderNo}" />
							<c:set var="prevItem" value="${item}" />

							<%-- 3. 전체 목록의 맨 마지막 상품일 때 마지막 카드 상자 닫기 및 버튼 출력 --%>
							<c:if test="${status.last}">
										</div>
										<!-- // .product-list-wrap 닫기 -->

										<!-- 하단 액션 버튼 그룹 (마지막 주문건) -->
										<c:choose>
											<%-- [1] 주문 취소 관련 상태 -> [취소 내역 조회] 단일 넓은 버튼만 출력 --%>
											<c:when test="${prevItem.orderStatus eq '취소처리' or prevItem.orderStatus eq '주문취소' or prevItem.orderStatus eq '취소완료'}">
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/cancel_history'">
														취소 내역 조회
													</button>
												</div>
											</c:when>

											<%-- [2] 결제 완료 상태 -> [배송 조회] | [주문 취소] 2개 버튼만 출력 --%>
											<c:when test="${prevItem.orderStatus eq '결제완료' or prevItem.orderStatus eq '결제 완료'}">
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action primary"
														onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'">
														배송 조회
													</button>
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'">
														주문 취소
													</button>
												</div>
											</c:when>

											<%-- [3] 배송중 상태 -> [배송 조회] | [교환, 반품 신청] 2개 버튼만 출력 --%>
											<c:when test="${prevItem.orderStatus eq '배송중' or prevItem.orderStatus eq '배송 중' or prevItem.orderStatus eq '배송시작'}">
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action primary"
														onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'">
														배송 조회
													</button>
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'">
														교환, 반품 신청
													</button>
												</div>
											</c:when>

											<%-- [4] 배송완료 등 기타 상태 -> [배송 조회] | [교환, 반품 신청] | [리뷰 작성/보기] 3개 버튼 모두 출력 --%>
											<c:otherwise>
												<div class="delivery-buttons">
													<button type="button" class="delivery-btn btn-action primary"
														onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'">
														배송 조회
													</button>

													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'">
														교환, 반품 신청
													</button>

													<c:set var="isReviewWritten" value="false" />
													<c:forEach var="review" items="${reviewList}">
														<c:if test="${review.orderDetailNo eq prevItem.orderDetailNo and review.reviewWritten}">
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
																onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${prevItem.orderDetailNo}&productNo=${prevItem.productNo}'">
																리뷰 작성하기
															</button>
														</c:otherwise>
													</c:choose>
												</div>
											</c:otherwise>
										</c:choose>
										<!-- // .delivery-buttons 닫기 -->

									</div>
									<!-- // .delivery-main 닫기 -->
								</section>
								<!-- // .delivery-box 닫기 -->
							</c:if>

						</c:forEach>
					</c:when>

					<c:otherwise>
						<div class="empty-order-container" style="text-align: center; padding: 60px 0;">
							<div class="empty-icon" style="font-size: 40px; margin-bottom: 10px;">!</div>
							<h3>최근 주문 내역이 없습니다.</h3>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

			<!-- 페이지 이동 버튼 -->
			<div class="pager-box">
				<button type="button" id="btn-page-prev" class="btn-pager">&lt; 이전</button>
				<button type="button" id="btn-page-next" class="btn-pager">다음 &gt;</button>
			</div>

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
	<!-- //.mypage-container -->

	<jsp:include page="/inc/footer.jsp" />

	<!-- 연도 선택 및 페이징 이벤트 스크립트 -->
	<script>
		$(document).ready(function() {
			var currentYear = "${empty yearFilter ? 'recent' : yearFilter}";
			var currentPage = parseInt("${empty curPage ? 1 : curPage}");
			var totalPages = parseInt("${empty totalPages ? 1 : totalPages}");

			$('.btn-period').on('click', function() {
				var selectedYear = $(this).data('year');
				location.href = contextPath + "/order/order_list?year=" + selectedYear + "&page=1";
			});

			$('#btn-page-prev').on('click', function() {
				if (currentPage > 1) {
					location.href = contextPath + "/order/order_list?year=" + currentYear + "&page=" + (currentPage - 1);
				} else {
					alert("첫 번째 페이지입니다.");
				}
			});

			$('#btn-page-next').on('click', function() {
				if (currentPage < totalPages) {
					location.href = contextPath + "/order/order_list?year=" + currentYear + "&page=" + (currentPage + 1);
				} else {
					alert("마지막 페이지입니다.");
				}
			});
		});
	</script>
</body>
</html>