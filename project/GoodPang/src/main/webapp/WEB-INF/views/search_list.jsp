<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>'${keyword}' 검색결과 | 굿팡</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<%-- 상품 카드(.product-grid/.product-card)는 카테고리 목록과 완전히 같은 모양이라 category.css를 그대로 재사용 --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/category.css">
</head>

<body class="page-category">

	<jsp:include page="/inc/header.jsp" />

	<main class="category-page">
		<div class="category-body" style="justify-content:center;">
			<section class="category-list">

				<h1 class="category-title">'${keyword}' 검색결과</h1>
				<p class="search-result-count">총 <fmt:formatNumber value="${totalCount}" pattern="#,###" />개의 상품이 있습니다.</p>

				<%-- 정렬 — CategoryServlet의 정렬(최신순/낮은가격순/높은가격순/판매량순)과 동일. 쿠팡랭킹순은 검색에서 지원 안 함 --%>
				<c:if test="${not empty keyword}">
					<div class="sort-bar">
						<ul>
							<li class="${sort == 'LATEST' ? 'Sort_selected' : ''}">
								<c:url var="sortLatestUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="LATEST" />
								</c:url>
								<a href="${sortLatestUrl}">최신순</a>
							</li>
							<li class="${sort == 'PRICE_ASC' ? 'Sort_selected' : ''}">
								<c:url var="sortPriceAscUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="PRICE_ASC" />
								</c:url>
								<a href="${sortPriceAscUrl}">낮은가격순</a>
							</li>
							<li class="${sort == 'PRICE_DESC' ? 'Sort_selected' : ''}">
								<c:url var="sortPriceDescUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="PRICE_DESC" />
								</c:url>
								<a href="${sortPriceDescUrl}">높은가격순</a>
							</li>
							<li class="${sort == 'SALE_COUNT' ? 'Sort_selected' : ''}">
								<c:url var="sortSaleCountUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="SALE_COUNT" />
								</c:url>
								<a href="${sortSaleCountUrl}">판매량순</a>
							</li>
						</ul>
					</div>
				</c:if>

				<c:choose>
					<c:when test="${empty products}">
						<p class="empty-message">
							<c:choose>
								<c:when test="${empty keyword}">검색어를 입력해주세요.</c:when>
								<c:otherwise>'${keyword}'에 대한 검색결과가 없습니다.</c:otherwise>
							</c:choose>
						</p>
					</c:when>
					<c:otherwise>

						<c:set var="freeShipMin" value="19800" />
						<ul class="product-grid">
							<c:forEach var="item" items="${products}">
								<li class="product-card">
									<a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">
										<figure>
											<c:if test="${not empty item.thumbnailUrl}">
												<img src="${pageContext.request.contextPath}/${item.thumbnailUrl}" alt="${item.productName}">
											</c:if>
										</figure>
										<p class="free-shipping-badge ${item.salePrice >= freeShipMin ? '' : 'is-empty'}">
											<c:if test="${item.salePrice >= freeShipMin}">무료배송</c:if>
										</p>
										<p class="product-name">${item.productName}</p>
										<p class="product-price">
											<c:if test="${item.discountRate > 0}">
												<span class="discount-rate">${item.discountRate}%</span>
												<span class="normal-price"><fmt:formatNumber value="${item.normalPrice}" pattern="#,###"/>원</span>
											</c:if>
											<span class="price-badge-row ${item.soldOut ? 'is-soldout' : ''}">
												<strong class="sale-price"><fmt:formatNumber value="${item.salePrice}" pattern="#,###"/>원</strong>
												<img class="badge-rocket" src="${pageContext.request.contextPath}/images/icons/logo_rocket_filter_medium.png" alt="로켓배송">
												<img class="badge-tomorrow" src="${pageContext.request.contextPath}/images/icons/badge_199cd481e67.png" alt="내일도착">
											</span>
										</p>
										<c:if test="${item.soldOut}">
											<p class="sold-out-text">일시품절</p>
										</c:if>
										<p class="delivery-date">${deliveryDate} 도착 보장</p>
										<c:if test="${item.reviewCount > 0}">
											<p class="product-rating">
												<span class="star-rating" aria-label="평점 ${item.avgRating}점"><em style="width:${item.avgRating * 20}%"></em></span>
												(${item.reviewCount})
											</p>
										</c:if>
										<p class="cash-reward">
											<img src="${pageContext.request.contextPath}/images/icons/list-cash-icon@2x.png" alt="">
											최대 <fmt:formatNumber value="${item.cashReward}" pattern="#,###"/>원 적립
										</p>
									</a>
								</li>
							</c:forEach>

							<c:set var="remainder" value="${fn:length(products) % 4}" />
							<c:if test="${remainder > 0}">
								<c:forEach begin="1" end="${4 - remainder}">
									<li class="product-card product-card--filler" aria-hidden="true"></li>
								</c:forEach>
							</c:if>
						</ul>

						<c:if test="${totalPages > 1}">
							<nav class="pagination" aria-label="페이지">
								<c:if test="${page > 1}">
									<c:url var="prevPageUrl" value="/search">
										<c:param name="keyword" value="${keyword}" />
										<c:param name="sort" value="${sort}" />
										<c:param name="page" value="${page - 1}" />
									</c:url>
									<a href="${prevPageUrl}">이전</a>
								</c:if>
								<c:forEach var="p" begin="1" end="${totalPages}">
									<c:url var="pageUrl" value="/search">
										<c:param name="keyword" value="${keyword}" />
										<c:param name="sort" value="${sort}" />
										<c:param name="page" value="${p}" />
									</c:url>
									<a href="${pageUrl}" class="${p == page ? 'current' : ''}">${p}</a>
								</c:forEach>
								<c:if test="${page < totalPages}">
									<c:url var="nextPageUrl" value="/search">
										<c:param name="keyword" value="${keyword}" />
										<c:param name="sort" value="${sort}" />
										<c:param name="page" value="${page + 1}" />
									</c:url>
									<a href="${nextPageUrl}">다음</a>
								</c:if>
							</nav>
						</c:if>

					</c:otherwise>
				</c:choose>

			</section>
		</div>
	</main>

	<button type="button" id="goto-top" class="goto-top">
		<span class="blind">맨 위로</span>
	</button>

	<jsp:include page="/inc/footer.jsp" />

	<script src="${pageContext.request.contextPath}/js/header.js"></script>
</body>
</html>
