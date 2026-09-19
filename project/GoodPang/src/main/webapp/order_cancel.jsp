<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>주문 취소 - GoodPang</title>

<!-- 공통 CSS 및 전용 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/order_cancel.css">

</head>
<body>

<jsp:include page="/inc/header.jsp" />
<script src="${pageContext.request.contextPath}/js/header.js"></script>
<div class="cancel-wrap">

    <!-- 상단 헤더 -->
    <div class="cancel-header">
        <button type="button" class="back-btn" onclick="history.back();" title="뒤로가기">←</button>
        <h1>주문 취소</h1>
    </div>

    <!-- 주문 상품 정보 -->
    <section class="cancel-section">
        <h2>주문 상품</h2>
        <div class="product-box">
            <div class="product-img">
                <div class="clothes-icon">👕</div>
            </div>
            <div class="product-info">
                <p class="product-name">
                    <span class="rocket-badge">🚀 로켓배송</span> ${cancelInfo.productName}
                </p>
	 <c:if test="${not empty cancelInfo.option1Value or not empty cancelInfo.option2Value}">
		    <div class="product-option">
		        <span>옵션: </span>
		        <c:if test="${not empty cancelInfo.option1Value}">
		            <c:if test="${not empty cancelInfo.option1Type}">${cancelInfo.option1Type}: </c:if>${cancelInfo.option1Value}
		        </c:if>
		        <c:if test="${not empty cancelInfo.option1Value and not empty cancelInfo.option2Value}"> / </c:if>
		        <c:if test="${not empty cancelInfo.option2Value}">
		            <c:if test="${not empty cancelInfo.option2Type}">${cancelInfo.option2Type}: </c:if>${cancelInfo.option2Value}
		        </c:if>
		    </div>
		</c:if>
                <p class="product-count">수량 : ${cancelInfo.quantity}개</p>
                <strong class="product-price">
                    <fmt:formatNumber value="${cancelInfo.totalPrice-cancelInfo.deliveryFee}" pattern="#,###" />원
                </strong>
            </div>
        </div>
    </section>

    <!-- 취소 사유 선택 -->
    <section class="cancel-section">
        <h2>취소 사유</h2>
        <select name="cancelReason" id="cancelReason" class="cancel-select">
            <option value="">취소 사유를 선택해주세요</option>
            <option value="change">단순 변심</option>
            <option value="wrong">상품을 잘못 주문함</option>
            <option value="delivery">배송이 너무 늦음</option>
            <option value="price">가격이 마음에 들지 않음</option>
            <option value="etc">기타</option>
        </select>
    </section>

    <!-- 환불 정보 -->
    <section class="cancel-section">
        <h2>환불 정보</h2>
        <div class="refund-box">
            <div class="refund-row">
                <span>상품 금액</span>
                <strong><fmt:formatNumber value="${cancelInfo.totalPrice-cancelInfo.deliveryFee}" pattern="#,###" />원</strong>
            </div>
            <div class="refund-row">
                <span>배송비</span>
                <strong><fmt:formatNumber value="${cancelInfo.deliveryFee}" pattern="#,###" />원</strong>
            </div>
            <div class="refund-line"></div>
            <div class="refund-row total">
                <span>환불 예정 금액</span>
                <strong class="total-price"><fmt:formatNumber value="${cancelInfo.totalPrice}" pattern="#,###" />원</strong>
            </div>
        </div>
    </section>

    <!-- 주문 취소 안내 -->
    <section class="notice">
        <h3>주문 취소 안내</h3>
        <ul>
            <li>주문 취소 후에는 상품을 다시 주문해야 합니다.</li>
            <li>결제 수단에 따라 환불까지 1~3일(영업일 기준)이 소요될 수 있습니다.</li>
            <li>배송이 시작된 상품은 주문 취소가 제한되며 반품 절차로 진행됩니다.</li>
        </ul>
    </section>

    <!-- 버튼 영역 -->
    <div class="button-area">
        <button type="button" class="cancel-submit" onclick="cancelOrder(${cancelInfo.orderNo});">
            주문 취소하기
        </button>
    </div>

</div>

<jsp:include page="/inc/footer.jsp" />

<script>
function cancelOrder(orderNo) {
    const reason = document.getElementById("cancelReason").value;

    if (!reason) {
        alert("취소 사유를 선택해주세요.");
        document.getElementById("cancelReason").focus();
        return;
    }

    if (!confirm("주문을 취소하시겠습니까?")) {
        return;
    }

    // 서버로 주문 취소 처리 요청 전달
    location.href = "${pageContext.request.contextPath}/order/cancel_action?orderNo=" + orderNo + "&reason=" + encodeURIComponent(reason);
}
</script>

</body>
</html>