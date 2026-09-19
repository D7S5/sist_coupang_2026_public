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
  <title>굿팡 판매자 정산상세</title>

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
    .summary-panel {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 16px;
      margin-bottom: 16px;
    }
    .summary-item .label { font-size: 12px; color: #888; margin-bottom: 4px; }
    .summary-item .value { font-size: 16px; font-weight: 700; color: #111; }
    .account-panel {
      display: flex;
      gap: 24px;
      font-size: 13px;
      color: #444;
    }
    .account-panel .label { color: #888; margin-right: 6px; }
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
          <h1 class="page-title">정산상세</h1>
          <p class="page-desc">${summary.periodLabel} 정산기간에 포함된 주문 내역입니다.</p>
        </div>

        <div class="page-actions">
          <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/vendor/settlement">
            <svg class="icon"><use href="#ic-chevron-left" /></svg>
            정산내역 리스트로
          </a>
        </div>

      </div>

      <div class="notice-box">
        ⚠ 수수료율(10%)과 정산주기는 실제 정책이 아직 정해지지 않아 임시로 가정한 값입니다. 반품/취소로 인한 공제도 반영되어 있지 않습니다.
      </div>


      <!-- 정산기간 요약 -->
      <section class="panel">
        <div class="summary-panel">
          <div class="summary-item">
            <p class="label">주문건수</p>
            <p class="value">${summary.orderCount}건</p>
          </div>
          <div class="summary-item">
            <p class="label">매출액</p>
            <p class="value"><fmt:formatNumber value="${summary.salesAmount}" pattern="#,##0" />원</p>
          </div>
          <div class="summary-item">
            <p class="label">수수료(10%)</p>
            <p class="value"><fmt:formatNumber value="${summary.commissionAmount}" pattern="#,##0" />원</p>
          </div>
          <div class="summary-item">
            <p class="label">정산금액</p>
            <p class="value"><fmt:formatNumber value="${summary.settlementAmount}" pattern="#,##0" />원</p>
          </div>
          <div class="summary-item">
            <p class="label">정산예정일 / 상태</p>
            <p class="value">
              ${summary.settlementDate}
              <c:choose>
                <c:when test="${summary.settled}">
                  <span class="status-badge status-done">정산완료</span>
                </c:when>
                <c:otherwise>
                  <span class="status-badge status-waiting">정산예정</span>
                </c:otherwise>
              </c:choose>
            </p>
          </div>
        </div>
      </section>


      <!-- 입금 계좌 -->
      <section class="panel">
        <div class="panel-head">
          <h2>입금 계좌</h2>
        </div>
        <div class="account-panel">
          <span><span class="label">은행</span>${sessionScope.loginSeller.bankName}</span>
          <span><span class="label">계좌번호</span>${sessionScope.loginSeller.accountNo}</span>
          <span><span class="label">예금주</span>${sessionScope.loginSeller.accountHolder}</span>
        </div>
      </section>


      <!-- 주문 내역 -->
      <section class="panel table-panel">

        <div class="result-toolbar">
          <p class="result-count">주문 내역 <strong>${fn:length(detailList)}</strong>건</p>
        </div>

        <div class="table-scroll">
          <table class="order-table">
            <thead>
              <tr>
                <th class="col-order">주문번호</th>
                <th class="col-info">상품 정보</th>
                <th class="col-price">수량</th>
                <th class="col-price">매출액</th>
                <th class="col-price">수수료</th>
                <th class="col-price">정산금액</th>
                <th class="col-date">배송완료일</th>
              </tr>
            </thead>
            <tbody>

              <c:choose>

                <c:when test="${empty detailList}">
                  <tr>
                    <td colspan="7" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                      이 정산기간에 포함된 주문이 없습니다.
                    </td>
                  </tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="detail" items="${detailList}">
                    <tr>
                      <td class="col-order">${detail.orderNo}</td>
                      <td class="col-info">
                        ${detail.productName}
                        <c:if test="${not empty detail.optionLabel}">
                          <p class="product-qty">${detail.optionLabel}</p>
                        </c:if>
                      </td>
                      <td class="col-price">${detail.quantity}개</td>
                      <td class="col-price"><fmt:formatNumber value="${detail.lineAmount}" pattern="#,##0" />원</td>
                      <td class="col-price"><fmt:formatNumber value="${detail.commissionAmount}" pattern="#,##0" />원</td>
                      <td class="col-price"><fmt:formatNumber value="${detail.settlementAmount}" pattern="#,##0" />원</td>
                      <td class="col-date"><fmt:formatDate value="${detail.deliveryEndDate}" pattern="yyyy-MM-dd" /></td>
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
