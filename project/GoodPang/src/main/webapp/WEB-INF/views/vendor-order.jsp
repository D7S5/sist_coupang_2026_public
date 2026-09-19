<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
  <title>굿팡 판매자 주문/배송 관리</title>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = "파워셀러"; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "orders"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>

    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">주문/배송 관리</h1>
          <p class="page-desc">주문 처리 및 배송 현황을 확인하고 관리할 수 있습니다.</p>
        </div>

      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">결제 완료</span>
            <span class="stat-icon stat-icon-teal"><svg class="icon"><use href="#ic-receipt" /></svg></span>
          </div>
          <div class="stat-value">${orderStat.waitingCount} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">배송 중</span>
            <span class="stat-icon stat-icon-green"><svg class="icon"><use href="#ic-truck" /></svg></span>
          </div>
          <div class="stat-value">${orderStat.shippingCount} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">배송 완료 (오늘)</span>
            <span class="stat-icon stat-icon-blue"><svg class="icon"><use href="#ic-check-circle" /></svg></span>
          </div>
          <div class="stat-value">${orderStat.deliveredTodayCount} <small>건</small></div>
        </article>

      </section>


      <!-- 검색 필터 -->
      <section class="panel filter-panel">

        <form class="filter-row filter-row-main" id="searchForm" method="get"
              action="${pageContext.request.contextPath}/vendor/order">

          <div class="filter-field date-field">

            <label>주문일</label>

            <div class="date-controls">

              <div class="quick-range">
                <button class="quick-btn" type="button" data-range="0">오늘</button>
                <button class="quick-btn" type="button" data-range="6">7일</button>
                <button class="quick-btn" type="button" data-range="30">1개월</button>
                <button class="quick-btn" type="button" data-range="90">3개월</button>
              </div>

              <div class="date-range">
                <div class="date-input">
                  <input class="input" type="text" name="startDate" id="startDate"
                         value="${searchStartDate}" placeholder="YYYY-MM-DD">
                  <svg class="icon"><use href="#ic-calendar" /></svg>
                </div>
                <span class="date-tilde">~</span>
                <div class="date-input">
                  <input class="input" type="text" name="endDate" id="endDate"
                         value="${searchEndDate}" placeholder="YYYY-MM-DD">
                  <svg class="icon"><use href="#ic-calendar" /></svg>
                </div>
              </div>

            </div>

          </div>

          <div class="filter-field select-field">
            <label>주문상태</label>
            <select class="input select" name="orderStatus">
              <option value="" ${empty searchOrderStatus ? 'selected' : ''}>전체</option>
              <option value="결제완료" ${searchOrderStatus == '결제완료' ? 'selected' : ''}>결제 완료</option>
              <option value="배송중" ${searchOrderStatus == '배송중' ? 'selected' : ''}>배송 중</option>
              <option value="배송완료" ${searchOrderStatus == '배송완료' ? 'selected' : ''}>배송 완료</option>
              <option value="주문취소" ${searchOrderStatus == '주문취소' ? 'selected' : ''}>주문 취소</option>
            </select>
          </div>

          <div class="filter-field select-field">
            <label>배송상태</label>
            <select class="input select" name="deliveryStatus">
              <option value="" ${empty searchDeliveryStatus ? 'selected' : ''}>전체</option>
              <option value="출고대기" ${searchDeliveryStatus == '출고대기' ? 'selected' : ''}>출고 대기</option>
              <option value="배송중" ${searchDeliveryStatus == '배송중' ? 'selected' : ''}>배송 중</option>
              <option value="배송완료" ${searchDeliveryStatus == '배송완료' ? 'selected' : ''}>배송 완료</option>
            </select>
          </div>

          <div class="filter-field select-field">
            <label>결제상태</label>
            <select class="input select" name="paymentStatus">
              <option value="" ${empty searchPaymentStatus ? 'selected' : ''}>전체</option>
              <option value="결제완료" ${searchPaymentStatus == '결제완료' ? 'selected' : ''}>결제 완료</option>
              <option value="결제취소" ${searchPaymentStatus == '결제취소' ? 'selected' : ''}>결제 취소</option>
            </select>
          </div>

          <button class="btn btn-outline btn-detail" type="submit">
            <svg class="icon"><use href="#ic-filter" /></svg>
            상세검색
          </button>

          <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/vendor/order">초기화</a>

        </form>

      </section>


      <!-- 본문: 주문 목록 + 사이드 -->
      <div class="content-grid">

        <!-- 주문 목록 -->
        <section class="panel table-panel">

          <div class="result-toolbar">
            <p class="result-count">주문 목록 <strong>${totalCount}</strong>건</p>

            <select class="input select select-sm">
              <option>주문일 최신순</option>
              <option>주문일 오래된순</option>
              <option>주문금액 높은순</option>
              <option>주문금액 낮은순</option>
            </select>
          </div>

          <div class="table-scroll">
            <table class="order-table">
              <thead>
                <tr>
                  <th class="col-check"><input type="checkbox" id="checkAll"></th>
                  <th class="col-order">주문 정보</th>
                  <th class="col-info">상품 정보</th>
                  <th class="col-price">주문금액</th>
                  <th class="col-status">주문상태</th>
                  <th class="col-date">주문일</th>
                  <th class="col-manage">관리</th>
                </tr>
              </thead>
              <tbody>

                <c:choose>

                  <c:when test="${empty orderList}">
                    <tr>
                      <td colspan="7" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                        접수된 주문이 없습니다.
                      </td>
                    </tr>
                  </c:when>

                  <c:otherwise>
                    <c:forEach var="order" items="${orderList}">
                      <tr>
                        <td class="col-check"><input type="checkbox"></td>
                        <td class="col-order">
                          <p class="order-no">주문번호<br><strong>${order.orderNo}</strong></p>
                          <p class="order-buyer">${order.buyerName} (${order.buyerPhone})</p>
                        </td>
                        <td class="col-info">
                          <div class="product-cell">
                            <span class="thumb" style="display:block; width:44px; height:44px; overflow:hidden;">
                              <c:if test="${not empty order.thumbnailUrl}">
                                <img src="${pageContext.request.contextPath}/${order.thumbnailUrl}" alt="${order.productName}"
                                     style="width:44px; height:44px; object-fit:cover; border-radius:6px;">
                              </c:if>
                            </span>
                            <div class="product-text">
                              <p class="product-name">${order.productName}</p>
                              <c:if test="${not empty order.optionLabel}">
                                <p class="product-qty">${order.optionLabel}</p>
                              </c:if>
                              <p class="product-qty">수량 ${order.orderQty}개</p>
                            </div>
                          </div>
                        </td>
                        <td class="col-price">
                          <p class="price"><fmt:formatNumber value="${order.price * order.orderQty}" pattern="#,##0" />원</p>
                        </td>
                        <td class="col-status"><span class="status-badge">${order.orderStatus}</span></td>
                        <td class="col-date">
                          <fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd" /><br>
                          <span class="time"><fmt:formatDate value="${order.orderDate}" pattern="HH:mm:ss" /></span>
                        </td>
                        <td class="col-manage">
                          <a class="btn btn-outline btn-sm"
                             href="${pageContext.request.contextPath}/vendor/order/detail?orderNo=${order.orderNo}">상세보기</a>
                          <c:if test="${order.orderStatus == '결제완료'}">
                            <form method="post" action="${pageContext.request.contextPath}/vendor/order/ship"
                                  style="display:inline-flex; gap:4px; align-items:center; margin-top:4px;"
                                  onsubmit="if (!this.invoiceNo.value.trim()) { alert('송장번호를 입력해주세요.'); return false; } return confirm('입력한 송장번호로 배송중 처리하시겠습니까?');">
                              <input type="hidden" name="orderNo" value="${order.orderNo}">
                              <input class="input input-sm" type="text" name="invoiceNo" placeholder="송장번호" style="width:110px;">
                              <button class="btn btn-primary btn-sm" type="submit">배송중으로 변경</button>
                            </form>
                          </c:if>
                        </td>
                      </tr>
                    </c:forEach>
                  </c:otherwise>

                </c:choose>

              </tbody>
            </table>
          </div>

          <!-- 페이지네이션 -->
          <c:if test="${totalPages > 1}">
            <nav class="pagination" aria-label="페이지 이동">

              <c:if test="${page > 1}">
                <c:url var="prevPageUrl" value="/vendor/order">
                  <c:param name="startDate" value="${searchStartDate}" />
                  <c:param name="endDate" value="${searchEndDate}" />
                  <c:param name="orderStatus" value="${searchOrderStatus}" />
                  <c:param name="deliveryStatus" value="${searchDeliveryStatus}" />
                  <c:param name="paymentStatus" value="${searchPaymentStatus}" />
                  <c:param name="page" value="${page - 1}" />
                </c:url>
                <a class="page-arrow" href="${prevPageUrl}" aria-label="이전 페이지">
                  <svg class="icon"><use href="#ic-chevron-left" /></svg>
                </a>
              </c:if>

              <c:forEach var="p" begin="1" end="${totalPages}">
                <c:url var="pageUrl" value="/vendor/order">
                  <c:param name="startDate" value="${searchStartDate}" />
                  <c:param name="endDate" value="${searchEndDate}" />
                  <c:param name="orderStatus" value="${searchOrderStatus}" />
                  <c:param name="deliveryStatus" value="${searchDeliveryStatus}" />
                  <c:param name="paymentStatus" value="${searchPaymentStatus}" />
                  <c:param name="page" value="${p}" />
                </c:url>
                <a class="page-num ${p == page ? 'active' : ''}" href="${pageUrl}">${p}</a>
              </c:forEach>

              <c:if test="${page < totalPages}">
                <c:url var="nextPageUrl" value="/vendor/order">
                  <c:param name="startDate" value="${searchStartDate}" />
                  <c:param name="endDate" value="${searchEndDate}" />
                  <c:param name="orderStatus" value="${searchOrderStatus}" />
                  <c:param name="deliveryStatus" value="${searchDeliveryStatus}" />
                  <c:param name="paymentStatus" value="${searchPaymentStatus}" />
                  <c:param name="page" value="${page + 1}" />
                </c:url>
                <a class="page-arrow" href="${nextPageUrl}" aria-label="다음 페이지">
                  <svg class="icon rotate-180"><use href="#ic-chevron-left" /></svg>
                </a>
              </c:if>

            </nav>
          </c:if>

        </section>


        <!-- 사이드 -->
        <aside class="side-col">

          <section class="panel">

            <div class="panel-head">
              <h2>배송 현황 <span class="head-sub">(현재 기준)</span></h2>
              <a href="#" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
            </div>

            <div class="donut-wrap">

              <c:choose>
                <c:when test="${orderStat.totalCount > 0}">
                  <div class="donut" id="statusDonut"
                       style="background: conic-gradient(
                         #4285f4 0% ${orderStat.deliveredPercent}%,
                         #17c964 ${orderStat.deliveredPercent}% ${orderStat.deliveredPercent + orderStat.shippingPercent}%,
                         #ff9f1c ${orderStat.deliveredPercent + orderStat.shippingPercent}% 100%
                       );"></div>
                </c:when>
                <c:otherwise>
                  <div class="donut" id="statusDonut" style="background:#eee;"></div>
                </c:otherwise>
              </c:choose>

              <ul class="donut-legend">
                <li><i class="dot dot-blue"></i>배송 완료 <b>${orderStat.deliveredCount}건 (${orderStat.deliveredPercent}%)</b></li>
                <li><i class="dot dot-green"></i>배송 중 <b>${orderStat.shippingCount}건 (${orderStat.shippingPercent}%)</b></li>
                <li><i class="dot dot-orange"></i>출고 대기 <b>${orderStat.waitingCount}건 (${orderStat.waitingPercent}%)</b></li>
              </ul>

            </div>

          </section>

          <section class="panel">

            <div class="panel-head">
              <h2>공지사항</h2>
              <a href="#" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
            </div>

            <ul class="notice-list">
              <li>
                <span class="notice-tag tag-notice">공지</span>
                <span class="notice-title">시스템 점검 안내 (5/25 새벽)</span>
                <span class="notice-date">05.17</span>
              </li>
              <li>
                <span class="notice-tag tag-notice">공지</span>
                <span class="notice-title">배송 지역 보상 정책 변경 안내</span>
                <span class="notice-date">05.14</span>
              </li>
              <li>
                <span class="notice-tag tag-info">안내</span>
                <span class="notice-title">판매자 이용약관 개정 안내</span>
                <span class="notice-date">05.10</span>
              </li>
              <li>
                <span class="notice-tag tag-info">안내</span>
                <span class="notice-title">여름맞이 프로모션 참여 안내</span>
                <span class="notice-date">05.08</span>
              </li>
              <li>
                <span class="notice-tag tag-info">안내</span>
                <span class="notice-title">정산 주기 변경 안내 (6월~)</span>
                <span class="notice-date">05.07</span>
              </li>
            </ul>

          </section>

          <section class="panel">

            <div class="panel-head">
              <h2>자주 찾는 메뉴</h2>
            </div>

            <div class="quick-menu">
              <a href="${pageContext.request.contextPath}/vendor/product/write" class="quick-menu-item">
                <span class="quick-icon"><svg class="icon"><use href="#ic-doc-plus" /></svg></span>
                상품 등록
              </a>
              <a href="#" class="quick-menu-item">
                <span class="quick-icon"><svg class="icon"><use href="#ic-list" /></svg></span>
                반품 신청 목록
              </a>
              <a href="#" class="quick-menu-item">
                <span class="quick-icon"><svg class="icon"><use href="#ic-calculator" /></svg></span>
                정산 내역
              </a>
              <a href="#" class="quick-menu-item">
                <span class="quick-icon"><svg class="icon"><use href="#ic-megaphone" /></svg></span>
                프로모션 관리
              </a>
            </div>

          </section>

        </aside>

      </div>

    </main>

  </div>


  <c:if test="${param.shipError == 'duplicateInvoice'}">
    <script>alert("송장번호가 이미 사용 중입니다. 다른 송장번호를 입력해주세요.");</script>
  </c:if>

  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>
  <script>

    /* =========================================================
       주문일 빠른선택 버튼 - 오늘 기준으로 시작/종료일을 채우고 바로 검색
       (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리)
    ========================================================= */

    function toDateInputValue(date) {
      var year = date.getFullYear();
      var month = String(date.getMonth() + 1).padStart(2, "0");
      var day = String(date.getDate()).padStart(2, "0");
      return year + "-" + month + "-" + day;
    }

    document.querySelectorAll(".quick-btn").forEach(function (button) {
      button.addEventListener("click", function () {
        var daysBack = Number(button.dataset.range);

        var end = new Date();
        var start = new Date();
        start.setDate(start.getDate() - daysBack);

        document.getElementById("startDate").value = toDateInputValue(start);
        document.getElementById("endDate").value = toDateInputValue(end);

        document.getElementById("searchForm").submit();
      });
    });


    /* =========================================================
       전체 선택 체크박스
    ========================================================= */

    document.getElementById("checkAll").addEventListener("change", function () {
      document.querySelectorAll(".order-table tbody .col-check input").forEach(
        (checkbox) => (checkbox.checked = this.checked)
      );
    });


  </script>

</body>

</html>
