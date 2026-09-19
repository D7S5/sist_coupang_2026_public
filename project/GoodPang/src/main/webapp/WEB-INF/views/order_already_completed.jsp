<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>이미 완료된 주문입니다</title>
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/order_already_completed.css">

</head>

<body>

<div class="complete-container">

    <div class="complete-icon">✓</div>

    <div class="complete-title">
        이미 완료된 주문입니다.
    </div>

    <div class="complete-message">
        이미 결제가 완료되었거나 처리된 주문입니다.<br>
        주문 내역에서 주문 상태를 확인해주세요.
    </div>

    <div class="button-area">

        <button
            class="btn btn-home"
            onclick="location.href='${pageContext.request.contextPath}/'">
            홈으로
        </button>

        <button
            class="btn btn-orders"
            onclick="location.href='${pageContext.request.contextPath}/order/order_list'">
            주문내역 보기
        </button>

    </div>

</div>

</body>
</html>