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
<title>리뷰관리</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/review_list.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/review_available.css">
</head>

<body>
	<jsp:include page="/inc/header.jsp" />

	<div class="mypang-layout">

		<!-- 리뷰관리 메뉴 파란색 활성화 -->
		<jsp:include page="/inc/left_banner.jsp">
			<jsp:param name="activeMenu" value="review" />
		</jsp:include>
		<!-- 중앙 리뷰관리 -->
		<main class="review-page">
			<h1 class="review-title">리뷰관리</h1>

			<div class="review-member-box">
				<div class="member-profile">
					<div class="member-avatar">👤</div>
					<div class="member-name">${sessionScope.loginMember.memberName}</div>
				</div>

				<div class="member-divider"></div>

				<div class="member-stat">
					<span class="member-stat-title">작성 가능한 리뷰</span>
					<div class="member-stat-value">
						<strong>${availableCount}</strong> <span>개</span>
					</div>
				</div>

				<div class="member-divider"></div>

				<div class="member-stat">
					<span class="member-stat-title">작성한 리뷰</span>
					<div class="member-stat-value">
						<strong>${writtenCount}</strong> <span>개</span>
					</div>
				</div>
			</div>

			<div class="review-tabs">
				<a
					href="${pageContext.request.contextPath}/review/list?tab=available"
					class="${activeTab eq 'available' ? 'active' : ''}"> 작성 가능한 리뷰
					<span>${availableCount}</span>
				</a> <a
					href="${pageContext.request.contextPath}/review/list?tab=written"
					class="${activeTab eq 'written' ? 'active' : ''}"> 작성한 리뷰 <span>${writtenCount}</span>
				</a>
			</div>

			<c:if test="${activeTab eq 'available'}">
				<div class="available-review-list">
					<c:choose>
						<c:when test="${empty availableReviewList}">
							<div class="available-empty">
								<div class="available-empty-icon">✎</div>
								<strong>작성 가능한 리뷰가 없습니다.</strong>
								<p>상품 구매 후 리뷰를 작성할 수 있습니다.</p>
								<a href="${pageContext.request.contextPath}/order/order_list"
									class="available-order-btn">주문내역 확인하기</a>
							</div>
						</c:when>

						<c:otherwise>
							<c:forEach var="review" items="${availableReviewList}">
								<article class="available-review-item">


									<div class="available-product-image">
										<c:choose>

											<c:when test="${not empty review.productImage}">
												<a
													href="${pageContext.request.contextPath}/product?productNo=${review.productNo}">
													<img
													src="${pageContext.request.contextPath}/${review.productImage}"
													alt="${review.productName}"
													onerror="this.style.display='none';">
												</a>
											</c:when>

											<c:otherwise>
												<img
													src="${pageContext.request.contextPath}/images/product/default-product.png"
													alt="${review.productName}">
											</c:otherwise>

										</c:choose>
									</div>



									<div class="available-product-info">
										<a
											href="${pageContext.request.contextPath}/product?productNo=${review.productNo}"
											class="available-product-name">${review.productName}</a>

										<c:if test="${not empty review.optionName}">
											<div class="available-product-option">${review.optionName}</div>
										</c:if>

										<div class="available-product-status">배송 완료</div>
										<p class="available-product-guide">구매하신 상품은 어떠셨나요?</p>
									</div>

									<div class="available-review-action">
										<a
											href="${pageContext.request.contextPath}/review/write?orderDetailNo=${review.orderDetailNo}&productNo=${review.productNo}"
											class="available-write-btn">리뷰 작성하기</a>
									</div>
								</article>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>

			<c:if test="${activeTab eq 'written'}">
				<div class="written-review-list">
					<c:choose>
						<c:when test="${empty writtenReviewList}">
							<div class="review-empty">
								<div class="empty-icon">✎</div>
								<p>작성한 리뷰가 없습니다.</p>
								<a href="${pageContext.request.contextPath}/order/order_list"
									class="order-btn">주문내역 확인하기</a>
							</div>
						</c:when>

						<c:otherwise>
							<c:forEach var="review" items="${writtenReviewList}">
								<article class="written-review-item">
									<div class="written-product">
										<a
											href="${pageContext.request.contextPath}/product?productNo=${review.productNo}"
											class="written-product-name">${review.productName}</a>

										<c:if test="${not empty review.optionText}">
											<div class="written-product-option">${review.optionText}</div>
										</c:if>
									</div>

									<div class="written-review-body">
										<div class="written-review-top">
											<div class="written-review-stars">${review.ratingStars}</div>
											<div class="written-review-date">
												<fmt:formatDate value="${review.reviewDate}"
													pattern="yyyy.MM.dd" />
											</div>
										</div>

										<c:if test="${not empty review.reviewSummary}">
											<div class="written-review-summary">
												<p>
													<c:out value="${review.reviewSummary}" />
												</p>
											</div>
										</c:if>

										<div class="written-review-content">
											<p>
												<c:out value="${review.reviewContent}" />
											</p>
										</div>

										<c:if test="${not empty review.imageUrls}">
											<div class="written-review-photos">
												<c:forEach var="imageUrl" items="${review.imageUrls}">
													<div class="written-review-photo">
														<img src="${pageContext.request.contextPath}${imageUrl}"
															alt="리뷰 사진" onclick="openReviewImage(this.src)">
													</div>
												</c:forEach>
											</div>
										</c:if>

										<c:if test="${not empty review.serviceRating}">
											<div class="written-service-rating">
												서비스 만족도 :
												<c:choose>
													<c:when test="${review.serviceRating == 2}">👍 만족</c:when>
													<c:otherwise>👎 불만족</c:otherwise>
												</c:choose>
											</div>
										</c:if>

										<div class="written-review-actions">
											<a
												href="${pageContext.request.contextPath}/review/edit?reviewNo=${review.reviewNo}"
												class="written-edit-btn">수정</a>

											<form
												action="${pageContext.request.contextPath}/review/delete"
												method="post">
												<input type="hidden" name="reviewNo"
													value="${review.reviewNo}">
												<button type="submit" class="written-delete-btn"
													onclick="return confirm('리뷰를 삭제하시겠습니까?');">삭제</button>
											</form>
										</div>
									</div>
								</article>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>

			<c:if test="${totalPages > 1}">
				<div class="review-pagination">
					<c:if test="${startPage > 1}">
						<a
							href="${pageContext.request.contextPath}/review/list?tab=${activeTab}&page=${startPage - 1}"
							class="page-arrow">‹</a>
					</c:if>

					<c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
						<c:choose>
							<c:when test="${pageNo eq currentPage}">
								<span class="page-number active">${pageNo}</span>
							</c:when>
							<c:otherwise>
								<a
									href="${pageContext.request.contextPath}/review/list?tab=${activeTab}&page=${pageNo}"
									class="page-number">${pageNo}</a>
							</c:otherwise>
						</c:choose>
					</c:forEach>

					<c:if test="${endPage < totalPages}">
						<a
							href="${pageContext.request.contextPath}/review/list?tab=${activeTab}&page=${endPage + 1}"
							class="page-arrow">›</a>
					</c:if>
				</div>
			</c:if>
		</main>

		<aside class="review-right-banner">
			<jsp:include page="/inc/right_banner.jsp" />
		</aside>
	</div>

	<jsp:include page="/inc/footer.jsp" />
	<div id="reviewImageModal" class="review-image-modal"
		onclick="closeReviewImage()">
		<div class="review-image-modal-content"
			onclick="event.stopPropagation();">
			<button type="button" class="review-image-close"
				onclick="closeReviewImage()">×</button>
			<img id="reviewImageLarge" src="" alt="확대된 리뷰 이미지">
		</div>
	</div>

	<script>
		function openReviewImage(src) {
			const modal = document.getElementById("reviewImageModal");
			const image = document.getElementById("reviewImageLarge");

			image.src = src;
			modal.classList.add("show");
			document.body.style.overflow = "hidden";
		}

		function closeReviewImage() {
			const modal = document.getElementById("reviewImageModal");
			const image = document.getElementById("reviewImageLarge");

			modal.classList.remove("show");
			image.src = "";
			document.body.style.overflow = "";
		}

		document.addEventListener("keydown", function(event) {
			if (event.key === "Escape") {
				closeReviewImage();
			}
		});
	</script>
</body>
</html>