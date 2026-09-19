<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>GoodPang | 배송지 관리</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/goodpang_addressbook.css">

</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<main class="addressbook-page">

			<jsp:include page="/inc/left_banner.jsp">
	    <jsp:param name="activeMenu" value="address" />
	      </jsp:include>
		<section class="content-area">

			<div class="benefit-bar">
				<div class="benefit-item">
					<strong>굿페이 머니</strong> <span>0 <small>원</small></span>
				</div>

				<div class="benefit-item">
					<strong>굿팡캐시</strong> <span>0 <small>원</small></span>
				</div>
			</div>

			<div class="address-panel">

				<div class="panel-header">
					<h2>주소록·배송지 관리</h2>

					<button type="button" class="guide-btn">가려진 정보 보기</button>
				</div>

				<c:choose>

					<c:when test="${not empty addressList}">

						<c:forEach var="address" items="${addressList}">

							<article class="address-card">

								<div class="name-line">

									<strong>${address.receiverName}</strong>

									<c:if test="${address.addressDefault eq 'Y'}">
										<span class="badge default"> 기본배송지 </span>
									</c:if>

									<span class="badge green"> 로켓프레시 가능 </span> <span
										class="badge blue"> 로켓와우 가능 </span>

								</div>

								<p class="road">
									${address.address}

									<c:if test="${not empty address.detailAddress}">
                                    ${address.detailAddress}
                                </c:if>
								</p>

								<p>${address.tel}</p>

								<p>
									<c:choose>
										<c:when test="${not empty address.requestMsg}">
                                        ${address.requestMsg}
                                    </c:when>
										<c:otherwise>
                                        문 앞
                                    </c:otherwise>
									</c:choose>
								</p>

								<form method="get"
									action="${pageContext.request.contextPath}/address/edit">

									<input type="hidden" name="addressNo"
										value="${address.addressNo}">

									<button class="edit-btn" type="submit">수정</button>
								</form>

							</article>

						</c:forEach>

					</c:when>

					<c:otherwise>
						<div class="empty-address">등록된 배송지가 없습니다.</div>
					</c:otherwise>

				</c:choose>

			</div>

			<a href="${pageContext.request.contextPath}/address/add"
				class="add-address"> <span class="plus">＋</span> 배송지 추가
			</a>

		</section>

		<aside class="address-right-area">
			<jsp:include page="/inc/right_banner.jsp" />
		</aside>
	</main>

	<jsp:include page="/inc/footer.jsp" />

</body>
</html>