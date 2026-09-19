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
  <title>굿팡 판매자 출고/운송장 관리</title>

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

  <% String menu = "shipping"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">출고/운송장 관리</h1>
          <p class="page-desc">아직 송장을 등록하지 않은 출고 대기 주문을 확인하고, 송장번호를 입력해 출고 처리할 수 있습니다.</p>
        </div>

      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">출고 대기</span>
            <span class="stat-icon stat-icon-orange"><svg class="icon"><use href="#ic-clock" /></svg></span>
          </div>
          <div class="stat-value">${fn:length(shippingList)} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">출고 지연</span>
            <span class="stat-icon stat-icon-red"><svg class="icon"><use href="#ic-return" /></svg></span>
          </div>
          <div class="stat-value">${delayedCount} <small>건</small></div>
          <p class="stat-compare" style="color:#999;">결제완료 후 2일 넘게 미출고된 건</p>
        </article>

      </section>


      <!-- 출고 대기 목록 -->
      <section class="panel table-panel">

        <div class="result-toolbar">
          <p class="result-count">출고 대기 목록 <strong>${fn:length(shippingList)}</strong>건</p>
        </div>

        <div class="table-scroll">
          <table class="order-table">
            <thead>
              <tr>
                <th class="col-order">주문 정보</th>
                <th class="col-info">상품 정보</th>
                <th class="col-price">주문금액</th>
                <th class="col-date">결제일</th>
                <th class="col-status">경과</th>
                <th class="col-manage">관리</th>
              </tr>
            </thead>
            <tbody>

              <c:choose>

                <c:when test="${empty shippingList}">
                  <tr>
                    <td colspan="6" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                      출고 대기중인 주문이 없습니다.
                    </td>
                  </tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="shipping" items="${shippingList}">
                    <tr class="${shipping.delayed ? 'delayed-row' : ''}">
                      <td class="col-order">
                        <p class="order-no">주문번호<br><strong>${shipping.orderNo}</strong></p>
                        <p class="order-buyer">${shipping.buyerName} (${shipping.buyerPhone})</p>
                      </td>
                      <td class="col-info">
                        ${shipping.productName}
                        <c:if test="${shipping.itemCount > 1}"> 외 ${shipping.itemCount - 1}건</c:if>
                      </td>
                      <td class="col-price">
                        <p class="price"><fmt:formatNumber value="${shipping.totalAmount}" pattern="#,##0" />원</p>
                      </td>
                      <td class="col-date">
                        <fmt:formatDate value="${shipping.orderDate}" pattern="yyyy-MM-dd" /><br>
                        <span class="time"><fmt:formatDate value="${shipping.orderDate}" pattern="HH:mm" /></span>
                      </td>
                      <td class="col-status">
                        <c:choose>
                          <c:when test="${shipping.delayed}">
                            <span class="delay-badge">지연 ${shipping.elapsedDays}일째</span>
                          </c:when>
                          <c:otherwise>
                            <span class="ontime-text">${shipping.elapsedDays}일째</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="col-manage">
                        <form method="post" action="${pageContext.request.contextPath}/vendor/order/ship"
                              style="display:inline-flex; gap:4px; align-items:center;"
                              onsubmit="if (!this.invoiceNo.value.trim()) { alert('송장번호를 입력해주세요.'); return false; } return confirm('입력한 송장번호로 출고 처리하시겠습니까?');">
                          <input type="hidden" name="orderNo" value="${shipping.orderNo}">
                          <input type="hidden" name="redirectTo" value="/vendor/shipping">
                          <input class="input input-sm" type="text" name="invoiceNo" placeholder="송장번호" style="width:110px;">
                          <button class="btn btn-primary btn-sm" type="submit">출고 처리</button>
                        </form>
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


  <c:if test="${param.shipError == 'duplicateInvoice'}">
    <script>alert("송장번호가 이미 사용 중입니다. 다른 송장번호를 입력해주세요.");</script>
  </c:if>

  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>

</body>

</html>
