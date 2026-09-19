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

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_products.css">
  <title>굿팡 판매자 상품 옵션 관리</title>

  <style>
    .option-table .col-product { width: 30%; }
    .option-table .col-option { width: 14%; }
    .option-table input[type="number"] {
      width: 100px;
      height: 32px;
      padding: 0 8px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 13px;
    }
    .option-table select.status-select {
      height: 32px;
      padding: 0 6px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 13px;
    }
    /* 상품 그룹이 한눈에 구분되도록, 한 상품에 속한 옵션 행들끼리 배경색을 번갈아 표시 */
    .option-table tbody tr.option-group-alt > td {
      background: #f5f6fa;
    }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "productOptions"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">상품 옵션 관리</h1>
          <p class="page-desc">전체 상품의 옵션별 재고와 가격을 한 화면에서 확인하고 수정할 수 있습니다.</p>
        </div>

      </div>


      <!-- 상품 필터 -->
      <form class="filter-row" method="get" action="${pageContext.request.contextPath}/vendor/product/options" style="margin-bottom: 12px;">
        <div class="filter-field">
          <label>상품 선택</label>
          <select class="input select" name="productNo" onchange="this.form.submit()">
            <option value="">전체 상품 (${fn:length(productFilterOptions)}개)</option>
            <c:forEach var="p" items="${productFilterOptions}">
              <option value="${p.productNo}" ${selectedProductNo == p.productNo ? 'selected' : ''}>${p.productName}</option>
            </c:forEach>
          </select>
        </div>
      </form>


      <!-- 결과 툴바 -->
      <div class="result-toolbar">
        <p class="result-count">옵션 목록 <strong>${fn:length(optionList)}</strong>개</p>
      </div>


      <!-- 옵션 목록 테이블 -->
      <section class="panel table-panel">

        <table class="product-table option-table">
          <thead>
            <tr>
              <th class="col-product">상품 정보</th>
              <th class="col-option">옵션</th>
              <th class="col-price">판매가</th>
              <th class="col-price">정상가</th>
              <th class="col-stock">재고수량</th>
              <th class="col-status">상태</th>
              <th class="col-manage">관리</th>
            </tr>
          </thead>
          <tbody>

            <c:choose>

              <c:when test="${empty optionList}">
                <tr>
                  <td colspan="7" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                    등록된 상품 옵션이 없습니다.
                  </td>
                </tr>
              </c:when>

              <c:otherwise>
                <c:forEach var="group" items="${groupedOptions}" varStatus="groupStatus">
                  <c:forEach var="option" items="${group.options}" varStatus="optionStatus">
                    <%-- input/select/button 이 이 옵션 전용 <form id="optionForm{optionId}">에 속하도록
                         form="..." 속성으로 연결 (그 form 태그 자체는 테이블 밖에 따로 둠 —
                         <form>은 <tr> 안에서 여러 <td>에 걸쳐 감쌀 수 없어서 이 방식으로 우회) --%>
                    <tr class="${groupStatus.index % 2 == 1 ? 'option-group-alt' : ''}">
                      <c:if test="${optionStatus.first}">
                        <td class="col-product" rowspan="${fn:length(group.options)}">
                          <div class="product-cell">
                            <span class="thumb" style="display:block; width:44px; height:44px; overflow:hidden;">
                              <c:if test="${not empty group.thumbnailUrl}">
                                <img src="${pageContext.request.contextPath}/${group.thumbnailUrl}" alt="${group.productName}"
                                     style="width:44px; height:44px; object-fit:cover; border-radius:6px;">
                              </c:if>
                            </span>
                            <div class="product-text">
                              <p class="product-name">
                                <a href="${pageContext.request.contextPath}/vendor/product/detail?productNo=${group.productNo}"
                                   style="color:inherit; text-decoration:none;">${group.productName}</a>
                              </p>
                              <p class="product-sku">상품번호 ${group.productNo}</p>
                            </div>
                          </div>
                        </td>
                      </c:if>
                      <td class="col-option">${option.optionLabel}</td>
                      <td class="col-price">
                        <input type="number" name="price" min="0" step="1" value="${option.price}" form="optionForm${option.optionId}">
                      </td>
                      <td class="col-price">
                        <input type="number" name="normalPrice" min="0" step="1" value="${option.normalPrice}" form="optionForm${option.optionId}">
                      </td>
                      <td class="col-stock">
                        <input type="number" name="quantity" min="0" step="1" value="${option.quantity}" form="optionForm${option.optionId}">
                      </td>
                      <td class="col-status">
                        <select class="status-select" name="status" form="optionForm${option.optionId}">
                          <option value="Y" ${option.status == 'Y' ? 'selected' : ''}>정상</option>
                          <option value="N" ${option.status == 'N' ? 'selected' : ''}>품절</option>
                        </select>
                      </td>
                      <td class="col-manage">
                        <button class="btn btn-primary btn-sm" type="submit" form="optionForm${option.optionId}">저장</button>
                      </td>
                    </tr>
                  </c:forEach>
                </c:forEach>
              </c:otherwise>

            </c:choose>

          </tbody>
        </table>

        <c:forEach var="option" items="${optionList}">
          <form id="optionForm${option.optionId}" method="post"
                action="${pageContext.request.contextPath}/vendor/product/option/update">
            <input type="hidden" name="optionId" value="${option.optionId}">
          </form>
        </c:forEach>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>

</body>

</html>
