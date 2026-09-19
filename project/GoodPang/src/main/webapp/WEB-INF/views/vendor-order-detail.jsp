<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>주문 상세 - 주문번호 ${order.orderNo}</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 22px; margin: 0 0 4px; }
    .sub { color: #888; margin-bottom: 20px; }

    .section { border: 1px solid #eee; border-radius: 8px; padding: 20px; margin-bottom: 16px; }
    .section h2 { font-size: 15px; margin: 0 0 14px; color: #333; }

    .grid { display: grid; grid-template-columns: 140px 1fr; row-gap: 10px; column-gap: 12px; font-size: 14px; }
    .grid dt { color: #888; }
    .grid dd { margin: 0; }

    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; }
    th { background: #fafafa; color: #555; }

    .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; }
    .badge-paid { background: #e8f0fe; color: #1a56db; }
    .badge-shipping { background: #fff4e5; color: #a15c00; }
    .badge-done { background: #e6f7ec; color: #0f7b3c; }
    .badge-etc { background: #eee; color: #666; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/vendor/order">&larr; 목록으로</a>

  <h1>주문번호 ${order.orderNo}</h1>
  <p class="sub">
    <c:choose>
      <c:when test="${order.orderStatus == '결제완료'}"><span class="badge badge-paid">결제완료</span></c:when>
      <c:when test="${order.orderStatus == '배송중'}"><span class="badge badge-shipping">배송중</span></c:when>
      <c:when test="${order.orderStatus == '주문완료'}"><span class="badge badge-done">주문완료</span></c:when>
      <c:otherwise><span class="badge badge-etc">${order.orderStatus}</span></c:otherwise>
    </c:choose>
    <fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss" /> 주문
  </p>

  <div class="section">
    <h2>주문상품</h2>
    <table>
      <thead>
        <tr>
          <th>상품명</th>
          <th>옵션</th>
          <th>수량</th>
          <th>단가</th>
          <th>금액</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="item" items="${order.items}">
          <tr>
            <td>${item.productName}</td>
            <td>${not empty item.optionLabel ? item.optionLabel : '-'}</td>
            <td>${item.orderQty}</td>
            <td><fmt:formatNumber value="${item.price}" pattern="#,##0" />원</td>
            <td><fmt:formatNumber value="${item.price * item.orderQty}" pattern="#,##0" />원</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>

  <div class="section">
    <h2>결제 정보</h2>
    <dl class="grid">
      <dt>상품금액</dt>
      <dd><fmt:formatNumber value="${order.productAmount}" pattern="#,##0" />원</dd>
      <dt>배송비</dt>
      <dd><fmt:formatNumber value="${order.deliveryFee}" pattern="#,##0" />원</dd>
      <dt>즉시할인</dt>
      <dd>-<fmt:formatNumber value="${order.instantDiscount}" pattern="#,##0" />원</dd>
      <dt>쿠폰할인</dt>
      <dd>-<fmt:formatNumber value="${order.couponDiscount}" pattern="#,##0" />원</dd>
      <dt>적립금 사용</dt>
      <dd>-<fmt:formatNumber value="${order.cashUsed}" pattern="#,##0" />원</dd>
      <dt>최종 결제금액</dt>
      <dd><strong><fmt:formatNumber value="${order.totalPrice}" pattern="#,##0" />원</strong></dd>
    </dl>
  </div>

  <div class="section">
    <h2>구매자 / 배송지</h2>
    <dl class="grid">
      <dt>주문자</dt>
      <dd>${order.buyerName} (${order.buyerPhone})</dd>
      <dt>수령인</dt>
      <dd>${order.receiverName} (${order.receiverTel})</dd>
      <dt>배송지 주소</dt>
      <dd>(${order.zipcode}) ${order.address} ${order.detailAddress}</dd>
      <dt>배송 요청사항</dt>
      <dd>${not empty order.requestMsg ? order.requestMsg : '-'}</dd>
    </dl>
  </div>

  <div class="section">
    <h2>배송 현황</h2>
    <c:choose>
      <c:when test="${not empty order.invoiceNo}">
        <dl class="grid">
          <dt>택배사</dt>
          <dd>${order.deliveryServiceCode}</dd>
          <dt>송장번호</dt>
          <dd>${order.invoiceNo}</dd>
          <dt>배송상태</dt>
          <dd>${order.deliveryStatus}</dd>
          <dt>배송 시작일시</dt>
          <dd><fmt:formatDate value="${order.deliveryStartDate}" pattern="yyyy-MM-dd HH:mm" /></dd>
          <dt>배송 완료일시</dt>
          <dd>
            <c:choose>
              <c:when test="${not empty order.deliveryEndDate}"><fmt:formatDate value="${order.deliveryEndDate}" pattern="yyyy-MM-dd HH:mm" /></c:when>
              <c:otherwise>-</c:otherwise>
            </c:choose>
          </dd>
        </dl>
      </c:when>
      <c:otherwise>
        <p>아직 배송이 시작되지 않았습니다.</p>
      </c:otherwise>
    </c:choose>
  </div>

</body>

</html>
