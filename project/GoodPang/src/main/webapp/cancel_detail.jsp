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
<title>취소/반품/교환/환불내역 상세 - GoodPang</title>

<!-- 기본 초기화 파일 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">

<!-- jQuery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

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

/* 전체 페이지 레이아웃 */
.page-wrap {
    width: 1025px;
    margin: 30px auto 80px auto;
    display: flex;
    min-height: 750px;
}

/* 왼쪽 마이페이지 사이드 메뉴 */
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

.side-section a:hover, .side-section a.active {
    color: #2878c8;
    font-weight: bold;
}

/* 메인 컨텐츠 */
.content {
    width: 820px;
    padding: 20px 25px 80px;
}

.content h1 {
    margin: 0 0 15px;
    font-size: 22px;
    font-weight: 700;
    border-bottom: 2px solid #333;
    padding-bottom: 10px;
}

.order-meta-info {
    font-size: 13px;
    color: #555;
    margin-bottom: 25px;
}

.order-meta-info span {
    margin-right: 15px;
}

/* 상품 내역 테이블 */
.detail-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 30px;
    border-top: 1px solid #333;
}

.detail-table th {
    background: #fcfcfc;
    border-bottom: 1px solid #e5e5e5;
    padding: 12px 10px;
    font-size: 13px;
    color: #666;
    text-align: center;
}

.detail-table td {
    border-bottom: 1px solid #e5e5e5;
    padding: 15px 10px;
    font-size: 13px;
    vertical-align: middle;
}

.prod-cell {
    display: flex;
    align-items: center;
}

.prod-img {
    width: 60px;
    height: 60px;
    background: #f5f5f5;
    border: 1px solid #eee;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    margin-right: 15px;
}

.prod-text .prod-name {
    font-weight: bold;
    margin-bottom: 5px;
}

.prod-text .prod-opt {
    color: #888;
    font-size: 12px;
}

.status-cell {
    text-align: center;
    line-height: 1.5;
}

.status-cell strong {
    font-size: 14px;
    color: #111;
}

.status-cell p {
    font-size: 11px;
    color: #777;
    margin: 4px 0 0;
}

/* 그리드 정보 테이블 (상세정보, 취소 사유) */
.section-title {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
}

.info-grid-table {
    width: 100%;
    border-collapse: collapse;
    border-top: 1px solid #ccc;
    margin-bottom: 30px;
}

.info-grid-table th {
    width: 22%;
    background: #fafafa;
    border-bottom: 1px solid #eee;
    padding: 12px 15px;
    text-align: left;
    font-size: 13px;
    color: #555;
    font-weight: normal;
}

.info-grid-table td {
    border-bottom: 1px solid #eee;
    padding: 12px 15px;
    font-size: 13px;
    color: #222;
}

/* 환불 안내 박스 */
.refund-wrap {
    display: flex;
    justify-content: space-between;
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc;
    padding: 20px 15px;
    margin-bottom: 25px;
}

.refund-left, .refund-right {
    width: 48%;
}

.refund-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 13px;
    color: #555;
}

.refund-row.total {
    font-size: 15px;
    font-weight: bold;
    color: #e61328;
    border-top: 1px solid #eee;
    padding-top: 10px;
    margin-top: 10px;
}

.notice-text {
    font-size: 11px;
    color: #777;
    margin-top: 15px;
    display: flex;
    align-items: center;
}

/* 목록 버튼 */
.btn-center-area {
    text-align: center;
    margin-top: 30px;
}

.list-btn {
    display: inline-block;
    width: 120px;
    height: 40px;
    line-height: 40px;
    background: #0073e9;
    color: #fff;
    font-size: 14px;
    font-weight: bold;
    border-radius: 2px;
    text-align: center;
}

.list-btn:hover {
    background: #005bb5;
}
</style>
</head>

<body>

<!-- HEADER INCLUDE -->
<header>
    <jsp:include page="/inc/header.jsp" />
</header>
<script src="${pageContext.request.contextPath}/js/header.js"></script>

<div class="page-wrap">

       <!-- 취소/반품 내역 메뉴 파란색 활성화 -->
<jsp:include page="/inc/left_banner.jsp">
    <jsp:param name="activeMenu" value="cancel_history" />
</jsp:include>


    <!-- 중앙 본문 -->
    <main class="content">
        <h1>취소/반품/교환/환불내역 상세</h1>

        <div class="order-meta-info">
            <span>주문일 : <fmt:formatDate value="${cancelInfo2.orderDate}" pattern="yyyy/MM/dd" /></span>
            <span>|</span>
            <span>주문번호 : ${cancelInfo2.orderNo}</span>
        </div>

        <!-- 상품 요약 테이블 -->
        <table class="detail-table">
            <thead>
                <tr>
                    <th style="width: 55%;">상품</th>
                    <th style="width: 20%;">금액</th>
                    <th style="width: 25%;">진행 상태</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${cancelDetailList}">
                    <tr>
                   <td>
    <div class="prod-cell">
        <!-- 1. 상품 이미지 (order_detail.jsp 방식 적용) -->
        <div class="prod-img" style="overflow: hidden; width: 60px; height: 60px; flex-shrink: 0;">
           
                <img src="${pageContext.request.contextPath}/${item.imageUrl}"  />
            
        </div>

        <!-- 2. 상품 텍스트 및 옵션 (order_detail.jsp 방식 그대로 적용) -->
        <div class="prod-text" style="margin-left: 12px;">
            <div class="prod-name" style="font-weight: bold; font-size: 14px; margin-bottom: 4px;">
                ${item.productName}
            </div>

            <!-- 옵션 정보 동적 출력 (기존 c:if 구문 적용) -->
            <c:if test="${not empty item.option1Value or not empty item.option2Value}">
                <div class="product-option" style="font-size: 12px; color: #888;">
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
</td>
                        <td style="text-align: center;">
                            <strong>${item.quantity}개</strong><br>
                            <fmt:formatNumber value="${item.itemPrice * item.quantity}" pattern="#,###" />원
                        </td>
                        <td class="status-cell">
                            <strong>${item.orderStatus}</strong>
                            <p>
                                <c:if test="${not empty item.expectedCancelDate}">
                                    <fmt:formatDate value="${item.expectedCancelDate}" pattern="M/d(E)" /> 이내
                                </c:if><br>
                                환불 완료 예정
                            </p>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- 상세정보 -->
        <div class="section-title">상세정보</div>
        <table class="info-grid-table">
            <tr>
                <th>취소접수일자</th>
                <td><fmt:formatDate value="${cancelInfo2.requestDate}" pattern="yyyy/MM/dd" /></td>
            </tr>
            <tr>
                <th>취소접수번호</th>
                <td>${cancelInfo2.returnNo}</td>
            </tr>
            
        </table>

        <!-- 취소 사유 -->
        <div class="section-title">취소 사유</div>
        <table class="info-grid-table">
            <tr>
                <th>취소 사유</th>
                <td>${not empty cancelInfo2.returnReason ? cancelInfo2.returnReason : '품절로 인해 자동취소 되었습니다.'}</td>
            </tr>
        </table>
<!-- =========================================================
     1. JSTL을 활용한 주문건 전체 상품금액 및 최종 환불금액 누적합
     ========================================================= -->
<!-- A. 전체 상품 금액 누적용 변수 초기화 -->
<c:set var="sumItemTotal" value="0" />

<!-- B. 취소 상세 상품 목록을 돌면서 각 상품의 (단가 * 수량)을 누적합 -->
<c:forEach var="item" items="${cancelDetailList}">
    <c:set var="sumItemTotal" value="${sumItemTotal + (item.itemPrice * item.quantity)}" />
</c:forEach>

<!-- 배송비 (null이 들어오면 기본값 0 적용) -->
<c:set var="actualDeliveryFee" value="${not empty cancelInfo2.deliveryFee ? cancelInfo2.deliveryFee : 0}" />

<!-- D. 최종 환불 완료 금액 (누적 총 상품금액 + DB 전달 배송비) -->
<c:set var="finalRefundTotal" value="${sumItemTotal + actualDeliveryFee}" />


<!-- =========================================================
     2. 환불 안내 UI 영역 출력
     ========================================================= -->
<div class="section-title">환불안내</div>
<div class="refund-wrap">
    <div class="refund-left">
        <!-- 상품금액 -->
        <div class="refund-row">
            <span>상품금액</span>
            <strong><fmt:formatNumber value="${sumItemTotal}" pattern="#,###" />원</strong>
        </div>
        
        <!-- 배송비 (DB에서 전달된 deliveryFee 그대로 출력) -->
        <div class="refund-row">
            <span>배송비</span>
            <strong>
                <c:choose>
                    <c:when test="${actualDeliveryFee == 0}">
                        0원
                    </c:when>
                    <c:otherwise>
                        +<fmt:formatNumber value="${actualDeliveryFee}" pattern="#,###" />원
                    </c:otherwise>
                </c:choose>
            </strong>
        </div>
        
        <!-- 반품비 -->
        <div class="refund-row">
            <span>반품비</span>
            <strong>0원</strong>
        </div>
    </div>
    
    <div class="refund-right">
        <!-- 환불 수단 -->
        <div class="refund-row">
            <span>환불 수단</span>
            <strong>
                <c:choose>
                    <c:when test="${not empty cancelInfo.cardCompanyName}">
                        ${cancelInfo.cardCompanyName} / 일시불
                    </c:when>
                    <c:when test="${not empty cancelInfo.bankName}">
                        ${cancelInfo.bankName} / 계좌이체
                    </c:when>
                    <c:otherwise>
                        ${cancelInfo.paymentMethod}
                    </c:otherwise>
                </c:choose>
            </strong>
        </div>
        
        <!-- 환불 완료 총액 -->
        <div class="refund-row total">
            <span>환불 완료</span>
            <strong><fmt:formatNumber value="${finalRefundTotal}" pattern="#,###" />원</strong>
        </div>
    </div>
</div>

        <div class="notice-text">
            ❶ 카드사로 결제 취소 요청이 전달된 후 환불까지 평일 기준 3~7일이 소요됩니다.
        </div>

        <!-- 목록 버튼 -->
        <div class="btn-center-area">
            <a href="${pageContext.request.contextPath}/order/cancel_history" class="list-btn">목록</a>
        </div>

    </main>

    <!-- 우측 배너 INCLUDE -->
    <jsp:include page="/inc/right_banner.jsp" />

</div>

<!-- FOOTER INCLUDE -->
<footer>
    <jsp:include page="/inc/footer.jsp" />
</footer>

</body>
</html>