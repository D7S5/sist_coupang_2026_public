<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>주문 취소 완료 - GoodPang</title>

<!-- 공통 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">

<!-- 쿠팡 파란색 기반 전용 스타일 (인라인 정의) -->
<style>
body {
    background-color: #f4f5f7;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    color: #333333;
}

/* 메인 컨테이너 카드 */
.confirm-wrap {
    max-width: 580px;
    margin: 60px auto 80px auto;
    padding: 40px 32px;
    background-color: #ffffff;
    border: 1px solid #e2e5e8;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    text-align: center;
}

/* 체크 아이콘 (쿠팡 블루) */
.check-icon-circle {
    width: 60px;
    height: 60px;
    background-color: #0073e9;
    color: #ffffff;
    font-size: 32px;
    font-weight: bold;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px auto;
}

/* 헤더 타이틀 & 설명 */
.confirm-title {
    font-size: 22px;
    font-weight: 700;
    color: #111111;
    margin-bottom: 10px;
}

.confirm-desc {
    font-size: 14px;
    color: #666666;
    line-height: 1.5;
    margin-bottom: 30px;
}

/* 환불 안내 박스 */
.info-box {
    background-color: #fafbfc;
    border: 1px solid #e9ecef;
    border-radius: 6px;
    padding: 18px 20px;
    text-align: left;
    margin-bottom: 32px;
}

.info-box ul {
    margin: 0;
    padding-left: 18px;
}

.info-box li {
    font-size: 13px;
    color: #555555;
    line-height: 1.6;
}

/* 하단 버튼 그룹 */
.button-group {
    display: flex;
    gap: 12px;
}

.btn-history, .btn-home {
    flex: 1;
    height: 48px;
    font-size: 15px;
    font-weight: 700;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s ease;
}

/* 취소/반품 내역 보기 버튼 (쿠팡 블루 메인 버튼) */
.btn-history {
    background-color: #0073e9;
    color: #ffffff;
    border: 1px solid #0073e9;
}

.btn-history:hover {
    background-color: #005bb5;
}

/* 쇼핑 계속하기 버튼 (서브 서브 버튼) */
.btn-home {
    background-color: #ffffff;
    color: #555555;
    border: 1px solid #cccccc;
}

.btn-home:hover {
    background-color: #f8f9fa;
    color: #111111;
}
</style>

</head>
<body>

<jsp:include page="/inc/header.jsp" />
<script src="${pageContext.request.contextPath}/js/header.js"></script>

<div class="confirm-wrap">

    <!-- 파란색 체크 아이콘 -->
    <div class="check-icon-circle">✓</div>

    <!-- 메인 메세지 -->
    <h1 class="confirm-title">주문 취소가 완료되었습니다</h1>
    <p class="confirm-desc">
        신청하신 주문 취소 건이 정상적으로 처리되었습니다.<br>
        자세한 처리 상태는 취소/반품 내역에서 확인하실 수 있습니다.
    </p>

    <!-- 환불 관련 안내 -->
    <div class="info-box">
        <ul>
            <li><strong>환불 안내:</strong> 결제하신 수단(신용카드/계좌이체 등)에 따라 영업일 기준 1~3일 이내 승인 취소 및 환불 처리됩니다.</li>
            <li>취소 완료된 주문은 다시 복구할 수 없으며 필요 시 재주문해 주셔야 합니다.</li>
        </ul>
    </div>

    <!-- 하단 버튼 영역 -->
    <div class="button-group">
        <!-- 클릭 시 history 페이지로 바로 이동 -->
        <button type="button" class="btn-history" onclick="location.href='${pageContext.request.contextPath}/order/cancel_history';">
            취소/반품 내역 보기
        </button>
        <button type="button" class="btn-home" onclick="location.href='${pageContext.request.contextPath}/';">
            쇼핑 계속하기
        </button>
    </div>

</div>

<jsp:include page="/inc/footer.jsp" />

</body>
</html>