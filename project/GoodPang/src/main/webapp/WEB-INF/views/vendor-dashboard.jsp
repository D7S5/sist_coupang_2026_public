<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.goodpang.dto.SellerDTO" %>
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

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_dashboard.css">
  <title>굿팡 판매자 대시보드</title>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "dashboard"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

<%
    SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");
    String approvalStatus = (loginSeller != null) ? loginSeller.getApprovalStatus() : null;

    if (approvalStatus != null && !"승인".equals(approvalStatus)) {

        String bannerColor;
        String bannerTitle;
        String bannerDesc;
        String actionUrl = null;
        String actionLabel = null;

        switch (approvalStatus) {
            case "입점 대기":
                bannerColor = "#fff4e5";
                bannerTitle = "입점 절차가 완료되지 않았습니다.";
                bannerDesc = "사업장 정보, 정산계좌, 서류를 제출하셔야 상품 등록 및 판매가 가능합니다.";
                actionUrl = request.getContextPath() + "/vendor/business-info";
                actionLabel = "추가정보 입력하기";
                break;
            case "심사 중":
                bannerColor = "#e8f0fe";
                bannerTitle = "입점 심사가 진행 중입니다.";
                bannerDesc = "심사가 완료되면 등록하신 이메일로 안내드립니다.";
                break;
            case "반려":
                bannerColor = "#fdecea";
                bannerTitle = "입점 신청이 반려되었습니다.";
                bannerDesc = (loginSeller.getRejectReason() != null)
                        ? "사유: " + loginSeller.getRejectReason()
                        : "자세한 사유는 판매자 고객센터로 문의해주세요.";
                actionUrl = request.getContextPath() + "/vendor/business-info";
                actionLabel = "정보 다시 제출하기";
                break;
            default:
                bannerColor = "#f5f5f5";
                bannerTitle = "";
                bannerDesc = "";
        }
%>
      <div style="background:<%= bannerColor %>;border-radius:8px;padding:16px 20px;margin-bottom:20px;display:flex;align-items:center;justify-content:space-between;">
        <div>
          <strong style="display:block;margin-bottom:4px;"><%= bannerTitle %></strong>
          <span><%= bannerDesc %></span>
        </div>
<% if (actionUrl != null) { %>
        <a href="<%= actionUrl %>" style="background:#111;color:#fff;padding:10px 16px;border-radius:6px;text-decoration:none;white-space:nowrap;"><%= actionLabel %></a>
<% } %>
      </div>
<% } %>

      <div class="page-head">

        <div>
          <h1 class="page-title">대시보드</h1>
          <p class="page-desc">${todayLabel}의 판매 현황과 주요 지표를 한눈에 확인하세요.</p>
        </div>

        <div class="date-picker" id="datePicker">

          <button class="date-picker-trigger" id="datePickerTrigger" type="button">
            <svg class="icon"><use href="#ic-calendar" /></svg>
            <span>${selectedDateLabel}</span>
            <svg class="icon chevron"><use href="#ic-chevron-down" /></svg>
          </button>

          <div class="date-picker-panel" id="datePickerPanel">
            <c:forEach var="option" items="${dateOptions}">
              <a href="${pageContext.request.contextPath}/vendor/dashboard?date=${option.date}"
                 class="${option.date == selectedDate ? 'active' : ''}">${option.label}</a>
            </c:forEach>
          </div>

        </div>

      </div>


      <!-- KPI 카드 -->
      <section class="kpi-row">

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">${todayLabel} 주문수</span>
            <span class="kpi-icon kpi-icon-blue"><svg class="icon"><use href="#ic-cart" /></svg></span>
          </div>
          <div class="kpi-value"><fmt:formatNumber value="${dashboardStat.todayOrderCount}" pattern="#,##0" /> <small>건</small></div>
          <div class="kpi-compare">${compareLabel}
            <c:choose>
              <c:when test="${dashboardStat.orderCountUp}">
                <strong class="up">▲ ${dashboardStat.orderCountChangePercent}%</strong>
              </c:when>
              <c:otherwise>
                <strong class="down">▼ ${dashboardStat.orderCountChangePercent}%</strong>
              </c:otherwise>
            </c:choose>
          </div>
          <svg class="kpi-spark" id="orderSpark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="" fill="none" stroke="#4285f4" stroke-width="2" />
          </svg>
        </article>

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">${todayLabel} 매출</span>
            <span class="kpi-icon kpi-icon-green"><svg class="icon"><use href="#ic-receipt" /></svg></span>
          </div>
          <div class="kpi-value">₩ <fmt:formatNumber value="${dashboardStat.todaySales}" pattern="#,##0" /></div>
          <div class="kpi-compare">${compareLabel}
            <c:choose>
              <c:when test="${dashboardStat.salesUp}">
                <strong class="up">▲ ${dashboardStat.salesChangePercent}%</strong>
              </c:when>
              <c:otherwise>
                <strong class="down">▼ ${dashboardStat.salesChangePercent}%</strong>
              </c:otherwise>
            </c:choose>
          </div>
          <svg class="kpi-spark" id="salesSpark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="" fill="none" stroke="#17c964" stroke-width="2" />
          </svg>
        </article>

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">${todayLabel} 방문자수</span>
            <span class="kpi-icon kpi-icon-orange"><svg class="icon"><use href="#ic-users" /></svg></span>
          </div>
          <div class="kpi-value"><fmt:formatNumber value="${dashboardStat.todayVisitorCount}" pattern="#,##0" /> <small>명</small></div>
          <div class="kpi-compare">${compareLabel}
            <c:choose>
              <c:when test="${dashboardStat.visitorCountUp}">
                <strong class="up">▲ ${dashboardStat.visitorCountChangePercent}%</strong>
              </c:when>
              <c:otherwise>
                <strong class="down">▼ ${dashboardStat.visitorCountChangePercent}%</strong>
              </c:otherwise>
            </c:choose>
          </div>
          <svg class="kpi-spark" id="visitorSpark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="" fill="none" stroke="#ff9f1c" stroke-width="2" />
          </svg>
        </article>

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">${todayLabel} 상품 노출수</span>
            <span class="kpi-icon kpi-icon-purple"><svg class="icon"><use href="#ic-eye" /></svg></span>
          </div>
          <div class="kpi-value"><fmt:formatNumber value="${dashboardStat.todayProductViewCount}" pattern="#,##0" /> <small>회</small></div>
          <div class="kpi-compare">${compareLabel}
            <c:choose>
              <c:when test="${dashboardStat.productViewCountUp}">
                <strong class="up">▲ ${dashboardStat.productViewCountChangePercent}%</strong>
              </c:when>
              <c:otherwise>
                <strong class="down">▼ ${dashboardStat.productViewCountChangePercent}%</strong>
              </c:otherwise>
            </c:choose>
          </div>
          <svg class="kpi-spark" id="viewSpark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="" fill="none" stroke="#9b5de5" stroke-width="2" />
          </svg>
        </article>

      </section>


      <!-- 중단 -->
      <section class="mid-row">

        <article class="panel sales-panel">

          <div class="panel-head">
            <h2>매출 현황 <span class="info-dot" title="최근 7일 매출액과 주문수">?</span></h2>
            <div class="tab-group" id="salesTabs">
              <button class="tab-btn active" type="button" data-range="daily">일간</button>
              <button class="tab-btn" type="button" data-range="weekly">주간</button>
              <button class="tab-btn" type="button" data-range="monthly">월간</button>
            </div>
          </div>

          <div class="chart-legend">
            <span class="legend-item"><i class="dot dot-blue"></i>매출액(원)</span>
            <span class="legend-item"><i class="dot dot-green"></i>주문수(건)</span>
          </div>

          <svg id="salesChart" class="sales-chart" viewBox="0 0 680 300"></svg>

        </article>

        <article class="panel">

          <div class="panel-head">
            <h2>주문/배송 현황</h2>
            <a href="${pageContext.request.contextPath}/vendor/order" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <ul class="status-list">
            <li>
              <span class="status-icon status-icon-purple"><svg class="icon"><use href="#ic-receipt" /></svg></span>
              <span class="status-label">결제 완료</span>
              <span class="status-value"><fmt:formatNumber value="${orderStat.waitingCount}" pattern="#,##0" /> <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-teal"><svg class="icon"><use href="#ic-truck" /></svg></span>
              <span class="status-label">배송 중</span>
              <span class="status-value"><fmt:formatNumber value="${orderStat.shippingCount}" pattern="#,##0" /> <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-green"><svg class="icon"><use href="#ic-truck" /></svg></span>
              <span class="status-label">배송 완료</span>
              <span class="status-value"><fmt:formatNumber value="${orderStat.deliveredCount}" pattern="#,##0" /> <small>건</small></span>
            </li>
          </ul>

        </article>

        <article class="panel">

          <div class="panel-head">
            <h2>공지사항</h2>
            <a href="${pageContext.request.contextPath}/vendor/notice" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <ul class="notice-list">

            <c:choose>

              <c:when test="${empty recentNotices}">
                <li><span class="notice-title" style="color:#999;">등록된 공지사항이 없습니다.</span></li>
              </c:when>

              <c:otherwise>
                <c:forEach var="notice" items="${recentNotices}">
                  <li>
                    <a href="${pageContext.request.contextPath}/vendor/notice/detail?noticeNo=${notice.noticeNo}"
                       style="display:contents; color:inherit; text-decoration:none;">
                      <span class="notice-tag ${notice.noticeType == '공지' ? 'tag-notice' : 'tag-info'}">${notice.noticeType}</span>
                      <span class="notice-title">${notice.title}</span>
                      <span class="notice-date"><fmt:formatDate value="${notice.createdDate}" pattern="MM.dd" /></span>
                    </a>
                  </li>
                </c:forEach>
              </c:otherwise>

            </c:choose>

          </ul>

        </article>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>
  <script>

    /* =========================================================
       날짜 선택 드롭다운
       (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리.
        setupDropdown()도 그 파일에 정의돼있는 함수를 그대로 가져다 씀)
    ========================================================= */

    setupDropdown("datePickerTrigger", "datePickerPanel");


    /* =========================================================
       매출 현황 차트 — 막대(매출액) + 선(주문수)
    ========================================================= */

    // 최근 7일/5주/5개월 실데이터 (VendorDashboardServlet -> VendorDashboardDAO.get*SalesStat)
    const dailyStat = ${dailySalesJson};
    const weeklyStat = ${weeklySalesJson};
    const monthlyStat = ${monthlySalesJson};
    const dailyTraffic = ${dailyTrafficJson};


    /* =========================================================
       KPI 카드 스파크라인 — 최근 7일 추이 (일간 매출 현황 데이터 재사용 + 방문자수/노출수)
    ========================================================= */

    function renderSparkline(svgId, values) {
      const svg = document.getElementById(svgId);
      const polyline = svg.querySelector("polyline");

      if (!values || values.length === 0) {
        polyline.setAttribute("points", "");
        return;
      }

      const width = 160;
      const height = 40;
      const padding = 4;

      const max = Math.max.apply(null, values);
      const min = Math.min.apply(null, values);
      const range = max - min;

      const step = values.length > 1 ? width / (values.length - 1) : 0;

      const points = values.map(function (value, index) {
        const x = step * index;
        const y = range === 0
          ? height / 2
          : height - padding - ((value - min) / range) * (height - padding * 2);
        return x + "," + y.toFixed(1);
      });

      polyline.setAttribute("points", points.join(" "));
    }

    renderSparkline("orderSpark", dailyStat.map(function (d) { return d.orderCount; }));
    renderSparkline("salesSpark", dailyStat.map(function (d) { return d.salesAmount; }));
    renderSparkline("visitorSpark", dailyTraffic.map(function (d) { return d.visitorCount; }));
    renderSparkline("viewSpark", dailyTraffic.map(function (d) { return d.viewCount; }));

    function toChartData(stat) {
      return {
        labels: stat.map(function (d) { return d.label; }),
        sales: stat.map(function (d) { return d.salesAmount; }),
        orders: stat.map(function (d) { return d.orderCount; })
      };
    }

    const salesData = {
      daily: toChartData(dailyStat),
      weekly: toChartData(weeklyStat),
      monthly: toChartData(monthlyStat)
    };

    // 실데이터라 규모가 판매자마다 다르므로, 실제 최댓값에 여유를 두고 축 상한을 동적으로 잡는다
    // (구간이 길수록 값이 커지니 step도 일간<주간<월간 순으로 크게)
    function niceMax(values, step) {
      const max = Math.max.apply(null, values.concat([0]));
      return max > 0 ? Math.ceil(max * 1.2 / step) * step : step;
    }

    const salesMax = {
      daily: niceMax(salesData.daily.sales, 100000),
      weekly: niceMax(salesData.weekly.sales, 1000000),
      monthly: niceMax(salesData.monthly.sales, 10000000)
    };
    const orderMax = {
      daily: niceMax(salesData.daily.orders, 10),
      weekly: niceMax(salesData.weekly.orders, 50),
      monthly: niceMax(salesData.monthly.orders, 100)
    };

    function formatAxis(value) {
      if (value >= 100000000) return (value / 100000000) + "억";
      if (value >= 10000) return (value / 10000) + "만";
      return value.toLocaleString();
    }

    function renderSalesChart(range) {
      const data = salesData[range];
      const svg = document.getElementById("salesChart");

      const width = 680;
      const height = 300;
      const padLeft = 60;
      const padRight = 40;
      const padTop = 20;
      const padBottom = 34;

      const plotW = width - padLeft - padRight;
      const plotH = height - padTop - padBottom;

      const sMax = salesMax[range];
      const oMax = orderMax[range];
      const count = data.labels.length;
      const slot = plotW / count;
      const barW = Math.min(40, slot * 0.5);

      let svgParts = [];

      /* 격자 + y축 라벨 (매출액, 5단계) */
      for (let i = 0; i <= 5; i++) {
        const y = padTop + plotH - (plotH * i / 5);
        const value = sMax * i / 5;
        svgParts.push(
          '<line x1="' + padLeft + '" y1="' + y + '" x2="' + (width - padRight) + '" y2="' + y +
          '" stroke="#eee" stroke-width="1" />'
        );
        svgParts.push(
          '<text x="' + (padLeft - 10) + '" y="' + (y + 4) + '" text-anchor="end" class="axis-label">' +
          formatAxis(value) + '</text>'
        );
        svgParts.push(
          '<text x="' + (width - padRight + 10) + '" y="' + (y + 4) + '" text-anchor="start" class="axis-label">' +
          Math.round(oMax * i / 5) + '</text>'
        );
      }

      /* 막대 (매출액) */
      const points = [];
      data.sales.forEach(function (value, index) {
        const x = padLeft + slot * index + (slot - barW) / 2;
        const barH = plotH * (value / sMax);
        const y = padTop + plotH - barH;

        svgParts.push(
          '<rect x="' + x + '" y="' + y + '" width="' + barW + '" height="' + barH +
          '" rx="3" fill="#5b8bff" />'
        );

        svgParts.push(
          '<text x="' + (x + barW / 2) + '" y="' + (height - padBottom + 20) + '" text-anchor="middle" class="axis-label">' +
          data.labels[index] + '</text>'
        );

        const cx = padLeft + slot * index + slot / 2;
        const cy = padTop + plotH - (plotH * (data.orders[index] / oMax));
        points.push(cx + "," + cy);
      });

      /* 선 (주문수) */
      svgParts.push('<polyline points="' + points.join(" ") + '" fill="none" stroke="#17c964" stroke-width="2.5" />');
      points.forEach(function (point) {
        const parts = point.split(",");
        svgParts.push('<circle cx="' + parts[0] + '" cy="' + parts[1] + '" r="3.5" fill="#17c964" />');
      });

      svg.innerHTML = svgParts.join("");
    }

    document.getElementById("salesTabs").addEventListener("click", function (event) {
      const button = event.target.closest(".tab-btn");
      if (!button) return;

      document.querySelectorAll("#salesTabs .tab-btn").forEach(function (btn) {
        btn.classList.remove("active");
      });
      button.classList.add("active");

      renderSalesChart(button.dataset.range);
    });

    renderSalesChart("daily");

  </script>

</body>

</html>
