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

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_orders.css">
  <title>굿팡 판매자 배송 관리</title>

  <style>
    .delay-badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 600;
      background: #fdeceb;
      color: #f4514a;
    }
    .ontime-text { color: #999; font-size: 12px; }
    tr.delayed-row { background: #fff8f7; }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "delivery"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">배송 관리</h1>
          <p class="page-desc">배송중인 주문을 모니터링하고 지연 건을 확인할 수 있습니다.</p>
        </div>

      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">배송 중</span>
            <span class="stat-icon stat-icon-green"><svg class="icon"><use href="#ic-truck" /></svg></span>
          </div>
          <div class="stat-value">${fn:length(deliveryList)} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">배송 지연</span>
            <span class="stat-icon stat-icon-red"><svg class="icon"><use href="#ic-return" /></svg></span>
          </div>
          <div class="stat-value">${delayedCount} <small>건</small></div>
          <p class="stat-compare" style="color:#999;">배송 시작 후 3일 넘게 미완료된 건</p>
        </article>

      </section>


      <!-- 배송중 목록 -->
      <section class="panel table-panel">

        <div class="result-toolbar">
          <p class="result-count">배송중 목록 <strong>${fn:length(deliveryList)}</strong>건</p>
        </div>

        <div class="table-scroll">
          <table class="order-table">
            <thead>
              <tr>
                <th class="col-order">주문 정보</th>
                <th class="col-info">상품 정보</th>
                <th class="col-info">택배사</th>
                <th class="col-info">송장번호</th>
                <th class="col-date">배송 시작일</th>
                <th class="col-status">경과</th>
                <th class="col-manage">관리</th>
              </tr>
            </thead>
            <tbody>

              <c:choose>

                <c:when test="${empty deliveryList}">
                  <tr>
                    <td colspan="7" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                      배송중인 주문이 없습니다.
                    </td>
                  </tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="delivery" items="${deliveryList}">
                    <tr class="${delivery.delayed ? 'delayed-row' : ''}">
                      <td class="col-order">
                        <p class="order-no">주문번호<br><strong>${delivery.orderNo}</strong></p>
                        <p class="order-buyer">${delivery.buyerName} (${delivery.buyerPhone})</p>
                      </td>
                      <td class="col-info">
                        ${delivery.productName}
                        <c:if test="${delivery.itemCount > 1}"> 외 ${delivery.itemCount - 1}건</c:if>
                      </td>
                      <td class="col-info">${delivery.deliveryServiceCode}</td>
                      <td class="col-info">${delivery.invoiceNo}</td>
                      <td class="col-date">
                        <fmt:formatDate value="${delivery.deliveryStartDate}" pattern="yyyy-MM-dd" /><br>
                        <span class="time"><fmt:formatDate value="${delivery.deliveryStartDate}" pattern="HH:mm" /></span>
                      </td>
                      <td class="col-status">
                        <c:choose>
                          <c:when test="${delivery.delayed}">
                            <span class="delay-badge">지연 ${delivery.elapsedDays}일째</span>
                          </c:when>
                          <c:otherwise>
                            <span class="ontime-text">${delivery.elapsedDays}일째</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="col-manage">
                        <a class="btn btn-outline btn-sm"
                           href="${pageContext.request.contextPath}/vendor/order/detail?orderNo=${delivery.orderNo}">상세보기</a>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>

              </c:choose>

            </tbody>
          </table>
        </div>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>

</body>

</html>
