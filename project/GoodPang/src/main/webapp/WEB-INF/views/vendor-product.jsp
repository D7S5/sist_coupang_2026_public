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

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_products.css">
  <title>굿팡 판매자 상품 관리</title>

  <style>
    .view-tabs { display: flex; gap: 8px; margin-bottom: 16px; }
    .view-tab {
      padding: 8px 16px;
      border-radius: 8px;
      font-size: 13px;
      font-weight: 600;
      color: #666;
      background: #fff;
      border: 1px solid #ddd;
    }
    .view-tab.active {
      background: #346aff;
      border-color: #346aff;
      color: #fff;
    }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "products"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">상품 관리</h1>
          <p class="page-desc">판매 중인 상품을 등록, 수정하고 관리할 수 있습니다.</p>
        </div>

        <div class="page-actions">
          <button class="btn btn-outline" type="button">엑셀 다운로드</button>
          <button class="btn btn-outline" type="button">엑셀 업로드</button>
          <a class="btn btn-primary" href="${pageContext.request.contextPath}/vendor/product/write">상품 등록</a>
        </div>

      </div>


      <!-- 노출중/숨김 탭 -->
      <div class="view-tabs">
        <a class="view-tab ${not hiddenView ? 'active' : ''}"
           href="${pageContext.request.contextPath}/vendor/product">노출 중</a>
        <a class="view-tab ${hiddenView ? 'active' : ''}"
           href="${pageContext.request.contextPath}/vendor/product?view=hidden">숨김</a>
      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">전체 상품</span>
            <span class="stat-icon stat-icon-blue"><svg class="icon"><use href="#ic-box" /></svg></span>
          </div>
          <div class="stat-value">${totalCount} <small>개</small></div>
          <a href="#" class="stat-link">전체보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">판매 중</span>
            <span class="stat-icon stat-icon-green"><svg class="icon"><use href="#ic-bag" /></svg></span>
          </div>
          <div class="stat-value">${saleCount} <small>개</small></div>
          <span class="stat-rate rate-green">
            <fmt:formatNumber value="${totalCount > 0 ? (saleCount * 100.0 / totalCount) : 0}" pattern="0.0" />%
          </span>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">품절</span>
            <span class="stat-icon stat-icon-orange"><svg class="icon"><use href="#ic-ban" /></svg></span>
          </div>
          <div class="stat-value">${soldOutCount} <small>개</small></div>
          <span class="stat-rate rate-orange">
            <fmt:formatNumber value="${totalCount > 0 ? (soldOutCount * 100.0 / totalCount) : 0}" pattern="0.0" />%
          </span>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">판매 중지</span>
            <span class="stat-icon stat-icon-red"><svg class="icon"><use href="#ic-pause-circle" /></svg></span>
          </div>
          <div class="stat-value">${stoppedCount} <small>개</small></div>
          <span class="stat-rate rate-red">
            <fmt:formatNumber value="${totalCount > 0 ? (stoppedCount * 100.0 / totalCount) : 0}" pattern="0.0" />%
          </span>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">승인 대기</span>
            <span class="stat-icon stat-icon-blue"><svg class="icon"><use href="#ic-clock" /></svg></span>
          </div>
          <div class="stat-value">${pendingCount} <small>개</small></div>
          <span class="stat-rate rate-blue">
            <fmt:formatNumber value="${totalCount > 0 ? (pendingCount * 100.0 / totalCount) : 0}" pattern="0.0" />%
          </span>
        </article>

      </section>


      <!-- 검색 필터 -->
      <section class="panel filter-panel">

        <div class="filter-row">

          <div class="filter-field">
            <label>상품명</label>
            <input class="input" type="text" placeholder="상품명을 입력해주세요.">
          </div>

          <div class="filter-field">
            <label>카테고리</label>
            <select class="input select">
              <option>대분류 선택</option>
              <option>가전디지털</option>
              <option>주방용품</option>
              <option>식품/음료</option>
              <option>가구/인테리어</option>
            </select>
          </div>

          <div class="filter-field">
            <label>판매 상태</label>
            <select class="input select">
              <option>전체</option>
              <option>판매 중</option>
              <option>품절</option>
              <option>판매 중지</option>
              <option>승인 대기</option>
            </select>
          </div>

          <div class="filter-field">
            <label>상품 유형</label>
            <select class="input select">
              <option>전체</option>
              <option>일반상품</option>
              <option>로켓배송</option>
              <option>정기배송</option>
            </select>
          </div>

          <div class="filter-field">
            <label>브랜드</label>
            <select class="input select">
              <option>전체</option>
              <option>코멧</option>
              <option>로켓프레시</option>
              <option>탐사</option>
              <option>홈플래닛</option>
            </select>
          </div>

        </div>

        <div class="filter-row filter-row-date">

          <div class="filter-field date-field">

            <label>등록일</label>

            <div class="date-controls">

              <div class="quick-range">
                <button class="quick-btn" type="button">오늘</button>
                <button class="quick-btn" type="button">7일</button>
                <button class="quick-btn" type="button">1개월</button>
                <button class="quick-btn" type="button">3개월</button>
              </div>

              <div class="date-range">
                <div class="date-input">
                  <input class="input" type="text" value="2025-05-12">
                  <svg class="icon"><use href="#ic-calendar" /></svg>
                </div>
                <span class="date-tilde">~</span>
                <div class="date-input">
                  <input class="input" type="text" value="2025-05-19">
                  <svg class="icon"><use href="#ic-calendar" /></svg>
                </div>
              </div>

            </div>

          </div>

          <div class="filter-buttons">
            <button class="btn btn-outline" type="button" id="resetButton">초기화</button>
            <button class="btn btn-primary" type="button">검색</button>
          </div>

        </div>

      </section>


      <!-- 결과 툴바 -->
      <div class="result-toolbar">
        <p class="result-count">검색 결과 <strong>${totalCount}</strong>개</p>

        <div class="result-controls">
          <select class="input select select-sm">
            <option>등록일 최신순</option>
            <option>등록일 오래된순</option>
            <option>판매가 높은순</option>
            <option>판매가 낮은순</option>
          </select>
          <select class="input select select-sm">
            <option>20개씩 보기</option>
            <option>50개씩 보기</option>
            <option>100개씩 보기</option>
          </select>
        </div>
      </div>


      <!-- 상품 목록 테이블 -->
      <section class="panel table-panel">

        <table class="product-table">
          <thead>
            <tr>
              <th class="col-check"><input type="checkbox" id="checkAll"></th>
              <th class="col-info">상품 정보</th>
              <th class="col-price">판매가</th>
              <th class="col-stock">재고 수량</th>
              <th class="col-status">판매 상태</th>
              <th class="col-date">등록일</th>
              <th class="col-date">수정일</th>
              <th class="col-manage">관리</th>
            </tr>
          </thead>
          <tbody>

            <c:choose>

              <c:when test="${empty productList}">
                <tr>
                  <td colspan="8" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                    <c:choose>
                      <c:when test="${hiddenView}">숨긴 상품이 없습니다.</c:when>
                      <c:otherwise>등록된 상품이 없습니다.</c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:when>

              <c:otherwise>
                <c:forEach var="product" items="${productList}">
                  <tr>
                    <td class="col-check"><input type="checkbox"></td>
                    <td class="col-info">
                      <div class="product-cell">
                        <c:choose>
                          <c:when test="${not empty product.thumbnailUrl}">
                            <span class="thumb">
                              <img src="${pageContext.request.contextPath}/${product.thumbnailUrl}"
                                   alt="${product.productName}"
                                   style="width:100%; height:100%; object-fit:cover; border-radius:6px;">
                            </span>
                          </c:when>
                          <c:otherwise>
                            <span class="thumb"></span>
                          </c:otherwise>
                        </c:choose>
                        <div class="product-text">
                          <p class="product-name">
                            <a href="${pageContext.request.contextPath}/vendor/product/detail?productNo=${product.productNo}"
                               style="color:inherit; text-decoration:none;">${product.productName}</a>
                          </p>
                          <p class="product-sku">상품번호 ${product.productNo}</p>
                          <p class="product-cat">${product.mainCategoryName} &gt; ${product.midCategoryName} &gt; ${product.subCategoryName}</p>
                        </div>
                      </div>
                    </td>
                    <td class="col-price">
                      <c:choose>
                        <c:when test="${product.minPrice eq product.maxPrice}">
                          ₩<fmt:formatNumber value="${product.minPrice}" pattern="#,##0" />
                        </c:when>
                        <c:otherwise>
                          ₩<fmt:formatNumber value="${product.minPrice}" pattern="#,##0" /> ~
                          ₩<fmt:formatNumber value="${product.maxPrice}" pattern="#,##0" />
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td class="col-stock">${product.totalQuantity}</td>
                    <td class="col-status">
                      <c:choose>
                        <c:when test="${product.saleStatus == '판매 중'}">
                          <span class="status-badge status-active">판매 중</span>
                        </c:when>
                        <c:when test="${product.saleStatus == '품절'}">
                          <span class="status-badge status-soldout">품절</span>
                        </c:when>
                        <c:when test="${product.saleStatus == '판매 중지'}">
                          <span class="status-badge status-stopped">판매 중지</span>
                        </c:when>
                        <c:otherwise>
                          <span class="status-badge status-wait">${product.saleStatus}</span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td class="col-date">
                      <fmt:formatDate value="${product.createdDate}" pattern="yyyy-MM-dd" /><br>
                      <span class="time"><fmt:formatDate value="${product.createdDate}" pattern="HH:mm" /></span>
                    </td>
                    <td class="col-date">
                      <fmt:formatDate value="${product.updatedDate}" pattern="yyyy-MM-dd" /><br>
                      <span class="time"><fmt:formatDate value="${product.updatedDate}" pattern="HH:mm" /></span>
                    </td>
                    <td class="col-manage">
                      <a class="btn btn-outline btn-sm"
                         href="${pageContext.request.contextPath}/vendor/product/detail?productNo=${product.productNo}">상세보기</a>
                      <c:choose>
                        <c:when test="${hiddenView}">
                          <form method="post" action="${pageContext.request.contextPath}/vendor/product/visibility" style="display:inline;">
                            <input type="hidden" name="productNo" value="${product.productNo}">
                            <input type="hidden" name="displayYn" value="Y">
                            <button class="btn btn-primary btn-sm" type="submit">숨김 해제</button>
                          </form>
                        </c:when>
                        <c:otherwise>
                          <button class="btn btn-outline btn-sm" type="button" disabled title="준비 중인 기능입니다">수정</button>
                          <c:if test="${product.saleStatus == '판매 중'}">
                            <form method="post" action="${pageContext.request.contextPath}/vendor/product/status"
                                  style="display:inline;"
                                  onsubmit="return confirm('이 상품의 판매를 중지하시겠습니까?');">
                              <input type="hidden" name="productNo" value="${product.productNo}">
                              <input type="hidden" name="saleStatus" value="판매 중지">
                              <button class="btn btn-outline btn-sm" type="submit">판매중지</button>
                            </form>
                          </c:if>
                          <c:if test="${product.saleStatus == '판매 중지'}">
                            <form method="post" action="${pageContext.request.contextPath}/vendor/product/status" style="display:inline;">
                              <input type="hidden" name="productNo" value="${product.productNo}">
                              <input type="hidden" name="saleStatus" value="판매 중">
                              <button class="btn btn-primary btn-sm" type="submit">판매재개</button>
                            </form>
                          </c:if>
                          <form method="post" action="${pageContext.request.contextPath}/vendor/product/visibility"
                                style="display:inline;"
                                onsubmit="return confirm('이 상품을 목록에서 숨기시겠습니까?\n판매자 상품 목록에서만 보이지 않게 되며, 기존 구매자의 주문내역에는 영향이 없습니다.');">
                            <input type="hidden" name="productNo" value="${product.productNo}">
                            <input type="hidden" name="displayYn" value="N">
                            <button class="btn btn-outline btn-sm" type="submit">숨김</button>
                          </form>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>

            </c:choose>

          </tbody>
        </table>

        <!-- 페이지네이션 -->
        <c:if test="${totalPages > 1}">
          <nav class="pagination" aria-label="페이지 이동">

            <c:if test="${page > 1}">
              <c:url var="prevPageUrl" value="/vendor/product">
                <c:if test="${hiddenView}"><c:param name="view" value="hidden" /></c:if>
                <c:param name="page" value="${page - 1}" />
              </c:url>
              <a class="page-arrow" href="${prevPageUrl}" aria-label="이전 페이지">
                <svg class="icon"><use href="#ic-chevron-left" /></svg>
              </a>
            </c:if>

            <c:forEach var="p" begin="1" end="${totalPages}">
              <c:url var="pageUrl" value="/vendor/product">
                <c:if test="${hiddenView}"><c:param name="view" value="hidden" /></c:if>
                <c:param name="page" value="${p}" />
              </c:url>
              <a class="page-num ${p == page ? 'active' : ''}" href="${pageUrl}">${p}</a>
            </c:forEach>

            <c:if test="${page < totalPages}">
              <c:url var="nextPageUrl" value="/vendor/product">
                <c:if test="${hiddenView}"><c:param name="view" value="hidden" /></c:if>
                <c:param name="page" value="${page + 1}" />
              </c:url>
              <a class="page-arrow" href="${nextPageUrl}" aria-label="다음 페이지">
                <svg class="icon rotate-180"><use href="#ic-chevron-left" /></svg>
              </a>
            </c:if>

          </nav>
        </c:if>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>
  <script>

    /* =========================================================
       등록일 빠른선택 버튼
       (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리)
    ========================================================= */

    document.querySelectorAll(".quick-btn").forEach(function (button) {
      button.addEventListener("click", function () {
        document.querySelectorAll(".quick-btn").forEach(function (btn) {
          btn.classList.remove("active");
        });
        button.classList.add("active");
      });
    });


    /* =========================================================
       전체 선택 체크박스
    ========================================================= */

    document.getElementById("checkAll").addEventListener("change", function () {
      document.querySelectorAll(".product-table tbody .col-check input").forEach(
        (checkbox) => (checkbox.checked = this.checked)
      );
    });


    /* =========================================================
       초기화
    ========================================================= */

    document.getElementById("resetButton").addEventListener("click", function () {
      document.querySelectorAll(".filter-panel .input").forEach(function (field) {
        if (field.tagName === "SELECT") {
          field.selectedIndex = 0;
        } else if (field.type === "text" && !field.closest(".date-input")) {
          field.value = "";
        }
      });
      document.querySelectorAll(".quick-btn").forEach((btn) => btn.classList.remove("active"));
    });

  </script>

</body>

</html>
