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

<title>취소/반품/교환/환불 내역</title>

<!-- 기본 초기화 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<!-- 공통 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">

<!-- jQuery -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<style>

* {
    box-sizing: border-box;
}

body {
    margin: 0;
    font-family: Arial, "Malgun Gothic", sans-serif;
    color: #111;
    background: #fff;
}

a {
    text-decoration: none;
    color: inherit;
}


/* =========================
   HEADER 자리
   ========================= */
.header{


}

/* =========================
   전체 영역
   ========================= */

.page-wrap {
    width: 1025px;
    margin: 30px auto 80px auto;
    display: flex;
    min-height: 750px;
}


/* =========================
   왼쪽 MY쿠팡 메뉴
   ========================= */

.side-menu {
    width: 135px;
    flex-shrink: 0;
    border: 1px solid #ddd;
    margin-top: 20px;
    margin-bottom: 30px;
}

.side-title {
    height: 54px;
    background: #2878c8;
    color: #fff;

    display: flex;
    align-items: center;
    padding-left: 20px;

    font-size: 18px;
    font-weight: bold;
}

.side-section {
    padding: 20px 15px;
    border-bottom: 1px solid #ddd;
}

.side-section h3 {
    margin: 0 0 12px;
    font-size: 14px;
}

.side-section a {
    display: block;
    margin-bottom: 8px;
    font-size: 13px;
    line-height: 1.3;
}

.side-section a:hover {
    color: #2878c8;
}

.side-section a.active {
    color: #2878c8;
    font-weight: bold;
}


/* =========================================
   오른쪽 광고
========================================= */
/* =========================
   오른쪽 광고 (화면에 고정, 스크롤 따라옴)
========================= */
.right-banner {
    position: fixed;
    top: 130px;
    left: 50%;
    margin-left: 610px;   /* mypage-container(1180px) 중앙 기준 오른쪽 여백 */
    margin-left: 530px;
    width: 92px;
    z-index: 100;
}
.banner {
    position: relative;

    width: 92px;
    height: 134px;

    margin-bottom: 9px;

    border-radius: 3px;

    overflow: hidden;

    padding: 14px 8px;

    color: #fff;
}

.banner strong {
    position: relative;
    z-index: 2;

    display: block;

    font-size: 14px;
    line-height: 1.25;
}

.banner span {
    position: absolute;

    left: 8px;
    top: 49px;

    z-index: 3;

    width: 16px;
    height: 16px;

    display: flex;
    align-items: center;
    justify-content: center;

    border-radius: 50%;

    background: rgba(255,255,255,.9);

    color: #276fe5;

    font-size: 14px;
    font-weight: bold;
}

.banner-product,
.banner-character,
.banner-bell,
.banner-shop {
    position: absolute;

    right: 5px;
    bottom: 2px;

    font-size: 52px;
}

.banner-1 {
    background: #fff;
    border: 1px solid #ddd;
    color: #2875db;
}

.banner-1 strong {
    font-size: 17px;
}

.banner-2 {
    background: #b9e4f5;
    color: #222;
}

.banner-3 {
    background: #f5a7c5;
    color: #222;
}

.banner-3 em {
    color: #ff0066;
    font-style: normal;
}

.banner-4 {
    background: #3339ef;
}

.banner-5 {
    background: #ef4a3b;
}

.banner-bell {
    font-size: 52px;
}

.banner-shop {
    font-size: 48px;
}

.badge {
    position: absolute;

    right: 7px;
    top: 54px;

    width: 18px;
    height: 18px;

    display: flex;
    align-items: center;
    justify-content: center;

    background: #e60012;

    border-radius: 50%;

    font-size: 10px;
}


/* =========================
   오른쪽 본문
   ========================= */

.content {
    width: 920px;
    padding: 50px 25px 80px;
}

.content h1 {
    margin: 0 0 25px;
    font-size: 25px;
    font-weight: 700;
}


/* =========================
   탭
   ========================= */

.tab-menu {
    width: 100%;
    display: flex;
    border-bottom: 1px solid #aaa;
    margin-bottom: 25px;
}

.tab-menu a {
    width: 50%;
    height: 48px;

    display: flex;
    align-items: center;
    justify-content: center;

    border: 1px solid #ccc;
    border-bottom: none;

    font-size: 15px;
}

.tab-menu a + a {
    border-left: none;
}

.tab-menu a.active {
    color: #2878c8;
    border: 1px solid #2878c8;
    border-bottom: 1px solid #fff;
    font-weight: bold;
}


/* =========================
   안내 문구
   ========================= */

.info-area {
    margin-bottom: 20px;
    font-size: 12px;
    color: #666;
    line-height: 1.8;
}

.info-area span {
    color: #2878c8;
}


/* =========================
   주문 하나
   ========================= */

.order-box {
    border: 1px solid #d5d5d5;
    margin-bottom: 15px;
}


/* 주문 상단 */

.order-header {
    height: 42px;
    padding: 0 15px;

    display: flex;
    align-items: center;

    background: #fafafa;
    border-bottom: 1px solid #ddd;

    font-size: 13px;
}

.order-header span {
    margin-right: 12px;
}

.order-header .bar {
    color: #ccc;
    margin-right: 12px;
}


/* 주문 상품 영역 */

.order-body {
    min-height: 105px;
    display: flex;
}


/* 상품 정보 */

.product-info {
    width: 55%;
    padding: 20px;
    display: flex;
    align-items: center;
}



.product-text {
    flex: 1;
}

.product-name {
    margin: 0 0 8px;
    font-size: 14px;
    font-weight: bold;
}

.product-option {
    margin: 0;
    color: #888;
    font-size: 12px;
    line-height: 1.5;
}


/* 수량 / 금액 */

.product-price {
    width: 15%;
    border-left: 1px solid #ddd;
    border-right: 1px solid #ddd;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    font-size: 13px;
}

.product-price strong {
    margin-top: 8px;
    font-size: 14px;
}


/* 취소 상태 */

.cancel-status {
    width: 30%;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    text-align: center;
}

.cancel-status strong {
    color: #222;
    font-size: 14px;
    margin-bottom: 7px;
}

.cancel-status p {
    margin: 0 0 10px;
    color: #777;
    font-size: 11px;
    line-height: 1.5;
}


/* 상세 버튼 */

.detail-btn {
    padding: 6px 12px;

    border: 1px solid #2878c8;
    background: #fff;
    color: #2878c8;

    font-size: 11px;
    cursor: pointer;
}

.detail-btn:hover {
    background: #2878c8;
    color: #fff;
}


/* =========================
   페이징
   ========================= */

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;

    margin-top: 35px;
}

.pagination a {
    width: 32px;
    height: 32px;

    display: flex;
    align-items: center;
    justify-content: center;

    border: 1px solid #ddd;

    margin: 0 2px;

    font-size: 12px;
}

.pagination a.active {
    background: #2878c8;
    border-color: #2878c8;
    color: #fff;
}


/* =========================================
   오른쪽 미니 메뉴
========================================= */

.side-mini-menu {
    border: 1px solid #ddd;
}

.side-mini-menu div {
    height: 30px;

    padding: 0 8px;

    display: flex;
    align-items: center;
    justify-content: space-between;

    background: #34405c;

    color: #fff;

    font-size: 10px;
}

.side-mini-menu div + div {
    border-top: 1px solid #566078;
}

.side-mini-menu strong {
    color: #39a5ff;
    font-size: 11px;
}


/* 최근 상품 */

.recent-product {
    height: 78px;

    margin-top: 0;

    border: 1px solid #ddd;

    display: flex;
    align-items: center;
    justify-content: center;

    font-size: 42px;
}

/* 데이터가 없을 때 표시할 CSS */
.empty-box {
    border: 1px solid #d5d5d5;
    padding: 60px 0;
    text-align: center;
    color: #777;
    font-size: 14px;
}

/* =========================
   FOOTER 자리
   ========================= */

footer {
    /* 기존 footer include 넣을 자리 */
}

</style>

</head>

<body>


<!-- =========================
     HEADER
     ========================= -->

<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>

<!-- =========================
     본문
     ========================= -->

<div class="page-wrap">


   <!-- 취소/반품 내역 메뉴 파란색 활성화 -->
<jsp:include page="/inc/left_banner.jsp">
    <jsp:param name="activeMenu" value="cancel_history" />
</jsp:include>

    <!-- =========================
         본문
         ========================= -->

    <main class="content">

        <h1>취소/반품/교환/환불 내역</h1>


        <!-- 탭 -->

        <div class="tab-menu">

           <a href="${pageContext.request.contextPath}/order/order_list" >주문목록/배송조회</a>

            <a href="${pageContext.request.contextPath}/order/cancel_history"
               class="active">
                취소/반품/교환
            </a>

        </div>


        <!-- 안내 -->

        <div class="info-area">

            <div>
                - 취소/반품/교환 신청한 내역을 확인할 수 있습니다.
            </div>

            <div>
                - 상품에 대한 자세한 문의는
                <span>고객센터</span>를 이용해주세요.
            </div>

        </div>


        <c:choose>
            <c:when test="${not empty cancelList}">
                <c:forEach var="checkList" items="${cancelList}">
                    <div class="order-box">

                        <div class="order-header">

                            <span>취소접수일 : <fmt:formatDate value="${checkList.requestDate}" pattern="yyyy/MM/dd" /></span>

                            <span class="bar">|</span>

                            <span>주문일 : <fmt:formatDate value="${checkList.orderDate}" pattern="yyyy/MM/dd" /></span>

                            <span class="bar">|</span>

                            <span>주문번호 : ${checkList.orderNo}</span>

                        </div>


                        <div class="order-body">


                            <div class="product-info">

                                <div class="product-text">

                                    <p class="product-name">
                                        ${checkList.productName}
                                    </p>

                                    <!-- 옵션 1, 2 동적 출력 구문 (checkList 기준) -->
                                    <c:if test="${not empty checkList.option1Value or not empty checkList.option2Value}">
                                        <p class="product-option">
                                            <span>옵션: </span>
                                            <c:if test="${not empty checkList.option1Value}">
                                                <c:if test="${not empty checkList.option1Type}">${checkList.option1Type}: </c:if>${checkList.option1Value}
                                            </c:if>
                                            <c:if test="${not empty checkList.option1Value and not empty checkList.option2Value}"> / </c:if>
                                            <c:if test="${not empty checkList.option2Value}">
                                                <c:if test="${not empty checkList.option2Type}">${checkList.option2Type}: </c:if>${checkList.option2Value}
                                            </c:if>
                                        </p>
                                    </c:if>

                                </div>

                            </div>


                            <div class="product-price">

                                <span>${checkList.quantity}개</span>

                                <strong><fmt:formatNumber value="${checkList.totalPrice*checkList.quantity}" pattern="#,###" />원</strong>

                            </div>


                            <div class="cancel-status">

                                <strong>${checkList.orderStatus}</strong>

                                <p>
                                    <c:if test="${not empty checkList.expectedCancelDate}">
                                        <fmt:formatDate value="${checkList.expectedCancelDate}" pattern="M/dd(E)" /> 취소 완료 예정
                                    </c:if>
                                </p>

                                <!-- 이동 이벤트 핸들러 추가 -->
                            <button type="button" class="detail-btn" 
							    onclick="location.href='${pageContext.request.contextPath}/order/cancel_detail?orderNo=${checkList.orderNo}';">
							    취소상세
							</button>

                            </div>

                        </div>

                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-box">
                    <p>취소/반품/교환 내역이 존재하지 않습니다.</p>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- =========================
             페이지 (동적 페이징 수정 반영)
             ========================= -->

        <div class="pagination">
            <%-- 이전 페이지 버튼 --%>
            <c:choose>
                <c:when test="${curPage > 1}">
                    <a href="${pageContext.request.contextPath}/order/cancel_history?page=${curPage - 1}">&lt;</a>
                </c:when>
                <c:otherwise>
                    <a href="javascript:void(0);" onclick="alert('첫 번째 페이지입니다.');">&lt;</a>
                </c:otherwise>
            </c:choose>

            <%-- 페이지 번호 동적 출력 --%>
            <c:forEach var="p" begin="1" end="${totalPages}">
                <a href="${pageContext.request.contextPath}/order/cancel_history?page=${p}" 
                   class="${p eq curPage ? 'active' : ''}">${p}</a>
            </c:forEach>

            <%-- 다음 페이지 버튼 --%>
            <c:choose>
                <c:when test="${curPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/order/cancel_history?page=${curPage + 1}">&gt;</a>
                </c:when>
                <c:otherwise>
                    <a href="javascript:void(0);" onclick="alert('마지막 페이지입니다.');">&gt;</a>
                </c:otherwise>
            </c:choose>
        </div>

    </main>
    
	<jsp:include page="/inc/right_banner.jsp" />	

</div>



<!-- =========================
     FOOTER
     ========================= -->
<footer>
<jsp:include page="/inc/footer.jsp" />
</footer>

</body>
</html>