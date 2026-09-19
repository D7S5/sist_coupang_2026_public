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
  <title>굿팡 판매자 정산내역</title>

  <style>
    .notice-box {
      background: #fff8e6;
      border: 1px solid #ffe4a3;
      border-radius: 8px;
      padding: 10px 14px;
      font-size: 12px;
      color: #8a6100;
      margin-bottom: 16px;
    }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "settlement"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">정산내역 리스트</h1>
          <p class="page-desc">배송완료된 주문을 1주 단위로 묶어 정산 예정/완료 내역을 확인할 수 있습니다.</p>
        </div>

      </div>

      <div class="notice-box">
        ⚠ 수수료율(10%)과 정산주기(정산기간 종료 후 7일 뒤 지급)는 실제 정책이 아직 정해지지 않아 임시로 가정한 값입니다. 실제 서비스 적용 전 확인이 필요합니다.
      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">정산 회차</span>
            <span class="stat-icon stat-icon-blue"><svg class="icon"><use href="#ic-calculator" /></svg></span>
          </div>
          <div class="stat-value">${fn:length(settlementList)} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">누적 정산금액</span>
            <span class="stat-icon stat-icon-green"><svg class="icon"><use href="#ic-receipt" /></svg></span>
          </div>
          <div class="stat-value">₩ <fmt:formatNumber value="${totalSettlementAmount}" pattern="#,##0" /></div>
        </article>

      </section>


      <!-- 정산내역 목록 -->
      <section class="panel table-panel">

        <div class="result-toolbar">
          <p class="result-count">정산내역 <strong>${fn:length(settlementList)}</strong>건</p>
        </div>

        <div class="table-scroll">
          <table class="order-table">
            <thead>
              <tr>
                <th class="col-date">정산기간</th>
                <th class="col-price">주문건수</th>
                <th class="col-price">매출액</th>
                <th class="col-price">수수료(10%)</th>
                <th class="col-price">정산금액</th>
                <th class="col-date">정산예정일</th>
                <th class="col-status">상태</th>
                <th class="col-manage">관리</th>
              </tr>
            </thead>
            <tbody>

              <c:choose>

                <c:when test="${empty settlementList}">
                  <tr>
                    <td colspan="8" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                      정산 내역이 없습니다.
                    </td>
                  </tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="settlement" items="${settlementList}">
                    <tr>
                      <td class="col-date">${settlement.periodLabel}</td>
                      <td class="col-price">${settlement.orderCount}건</td>
                      <td class="col-price"><fmt:formatNumber value="${settlement.salesAmount}" pattern="#,##0" />원</td>
                      <td class="col-price"><fmt:formatNumber value="${settlement.commissionAmount}" pattern="#,##0" />원</td>
                      <td class="col-price"><strong><fmt:formatNumber value="${settlement.settlementAmount}" pattern="#,##0" />원</strong></td>
                      <td class="col-date">${settlement.settlementDate}</td>
                      <td class="col-status">
                        <c:choose>
                          <c:when test="${settlement.settled}">
                            <span class="status-badge status-done">정산완료</span>
                          </c:when>
                          <c:otherwise>
                            <span class="status-badge status-waiting">정산예정</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="col-manage">
                        <a class="btn btn-outline btn-sm"
                           href="${pageContext.request.contextPath}/vendor/settlement/detail?periodStart=${settlement.periodStart}">상세보기</a>
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
