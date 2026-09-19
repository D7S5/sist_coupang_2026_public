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
  <title>${product.productName} - 상품 상세</title>

  <style>
    .detail-grid {
      display: grid;
      grid-template-columns: 160px 1fr;
      row-gap: 12px;
      column-gap: 16px;
      font-size: 14px;
    }
    .detail-grid dt { color: #888; }
    .detail-grid dd { margin: 0; color: #111; }

    .detail-section { margin-bottom: 16px; }
    .detail-section h2 {
      margin: 0 0 16px;
      font-size: 15px;
      font-weight: 700;
      color: #333;
    }

    .option-table { width: 100%; border-collapse: collapse; font-size: 13px; }
    .option-table th, .option-table td {
      padding: 10px 12px;
      border-bottom: 1px solid #eee;
      text-align: left;
      white-space: nowrap;
    }
    .option-table th { background: #fafafa; color: #555; }
    .option-thumb {
      display: inline-block;
      width: 40px; height: 40px;
      border-radius: 6px;
      background: #eef1f6;
      object-fit: cover;
      vertical-align: middle;
    }

    .image-gallery { display: flex; flex-wrap: wrap; gap: 12px; }
    .image-gallery img {
      width: 140px; height: 140px;
      border-radius: 8px;
      object-fit: cover;
      border: 1px solid #edeef1;
    }

    .desc-empty { color: #999; padding: 20px 0; }
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
          <a class="btn btn-outline btn-sm" style="margin-bottom:12px;"
             href="${pageContext.request.contextPath}/vendor/product">&larr; 목록으로</a>
          <h1 class="page-title">${product.productName}</h1>
          <p class="page-desc">
            상품번호 ${product.productNo}
            &middot; ${product.mainCategoryName} &gt; ${product.midCategoryName} &gt; ${product.subCategoryName}
          </p>
        </div>

        <div class="page-actions">

          <c:if test="${product.displayYn == 'N'}">
            <span class="status-badge status-stopped">숨김됨 (목록 미노출)</span>
          </c:if>

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

          <c:choose>
            <c:when test="${product.displayYn == 'N'}">
              <form method="post" action="${pageContext.request.contextPath}/vendor/product/visibility" style="display:inline;">
                <input type="hidden" name="productNo" value="${product.productNo}">
                <input type="hidden" name="displayYn" value="Y">
                <button class="btn btn-primary btn-sm" type="submit">숨김 해제</button>
              </form>
            </c:when>
            <c:otherwise>
              <form method="post" action="${pageContext.request.contextPath}/vendor/product/visibility" style="display:inline;"
                    onsubmit="return confirm('이 상품을 목록에서 숨기시겠습니까?\n판매자 상품 목록에서만 보이지 않게 되며, 기존 구매자의 주문내역에는 영향이 없습니다.');">
                <input type="hidden" name="productNo" value="${product.productNo}">
                <input type="hidden" name="displayYn" value="N">
                <button class="btn btn-outline btn-sm" type="submit">상품 숨기기</button>
              </form>
            </c:otherwise>
          </c:choose>

        </div>

      </div>


      <section class="panel detail-section">
        <h2>기본 정보</h2>
        <dl class="detail-grid">
          <dt>판매방식</dt>
          <dd>${product.saleMethod}</dd>

          <dt>브랜드</dt>
          <dd>${product.noBrandYn == 'Y' ? '브랜드 없음' : product.brandName}</dd>

          <dt>노출상품명</dt>
          <dd>${product.productName}</dd>

          <dt>등록상품명</dt>
          <dd>${not empty product.internalName ? product.internalName : '-'}</dd>

          <dt>등록일</dt>
          <dd><fmt:formatDate value="${product.createdDate}" pattern="yyyy-MM-dd HH:mm" /></dd>

          <dt>최종 수정일</dt>
          <dd><fmt:formatDate value="${product.updatedDate}" pattern="yyyy-MM-dd HH:mm" /></dd>
        </dl>
      </section>


      <section class="panel detail-section">
        <h2>옵션 목록 (${fn:length(product.options)}개)</h2>

        <p class="sub">기본 상품가격: <strong><fmt:formatNumber value="${product.productPrice}" pattern="#,##0" />원</strong>
          (옵션 판매가는 이 기본가에 더해지는 가감액이며, 최종가 = 기본가 + 옵션 판매가입니다.)</p>

        <c:choose>
          <c:when test="${empty product.options}">
            <p class="desc-empty">등록된 옵션이 없습니다.</p>
          </c:when>
          <c:otherwise>
            <div style="overflow-x:auto;">
              <table class="option-table">
                <thead>
                  <tr>
                    <th>이미지</th>
                    <th>옵션</th>
                    <th>정상가</th>
                    <th>판매가(가감액)</th>
                    <th>최종가</th>
                    <th>재고</th>
                    <th>판매자상품코드</th>
                    <th>모델번호</th>
                    <th>바코드</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="option" items="${product.options}">
                    <tr>
                      <td>
                        <div style="display:flex; flex-wrap:wrap; gap:4px;">
                          <c:if test="${not empty option.mainImageUrl}">
                            <img class="option-thumb" src="${pageContext.request.contextPath}/${option.mainImageUrl}" alt="대표이미지">
                          </c:if>
                          <c:forEach var="extraImageUrl" items="${option.extraImageUrls}">
                            <img class="option-thumb" src="${pageContext.request.contextPath}/${extraImageUrl}" alt="추가이미지">
                          </c:forEach>
                        </div>
                      </td>
                      <td>
                        <c:if test="${not empty option.option1Value}">${option.option1Type}: ${option.option1Value}</c:if>
                        <c:if test="${not empty option.option2Value}"> / ${option.option2Type}: ${option.option2Value}</c:if>
                        <c:if test="${not empty option.option3Value}"> / ${option.option3Type}: ${option.option3Value}</c:if>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty option.normalPrice}"><fmt:formatNumber value="${option.normalPrice}" pattern="#,##0" />원</c:when>
                          <c:otherwise>-</c:otherwise>
                        </c:choose>
                      </td>
                      <td>${option.price > 0 ? '+' : ''}<fmt:formatNumber value="${option.price}" pattern="#,##0" />원</td>
                      <td><fmt:formatNumber value="${product.productPrice + option.price}" pattern="#,##0" />원</td>
                      <td>${option.quantity}</td>
                      <td>${not empty option.sellerProductCode ? option.sellerProductCode : '-'}</td>
                      <td>${not empty option.modelNo ? option.modelNo : '-'}</td>
                      <td>${not empty option.barcode ? option.barcode : '-'}</td>
                      <td>${option.status == 'Y' ? '판매중' : '품절'}</td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </section>


      <section class="panel detail-section">
        <h2>배송 정보</h2>
        <dl class="detail-grid">
          <dt>출고지</dt>
          <dd>(${product.shippingZipcode}) ${product.shippingAddress} ${product.shippingDetailAddress}</dd>

          <dt>제주/도서산간</dt>
          <dd>${product.jejuShippingYn == 'Y' ? '배송 가능' : '배송 불가'}</dd>

          <dt>택배사</dt>
          <dd>${product.deliveryServiceCode}</dd>

          <dt>배송방법</dt>
          <dd>${product.deliveryMethod}</dd>

          <dt>묶음배송</dt>
          <dd>${product.bundleShippingYn == 'Y' ? '가능' : '불가능'}</dd>

          <dt>배송비</dt>
          <dd>
            ${product.shippingFeeType}
            <c:if test="${product.shippingFee > 0}"> (<fmt:formatNumber value="${product.shippingFee}" pattern="#,##0" />원)</c:if>
          </dd>

          <dt>출고 소요일</dt>
          <dd>
            ${product.leadTimeInputType}
            <c:if test="${not empty product.leadTimeDays}"> - ${product.leadTimeDays}일</c:if>
          </dd>

          <dt>당일출고</dt>
          <dd>
            ${product.sameDayShipYn == 'Y' ? '가능' : '불가능'}
            <c:if test="${product.sameDayShipYn == 'Y' && not empty product.sameDayCutoffTime}"> (마감 ${product.sameDayCutoffTime})</c:if>
          </dd>

        </dl>
      </section>


      <section class="panel detail-section">
        <h2>상세설명 (${product.detailType})</h2>

        <c:choose>
          <c:when test="${not empty product.detailImageUrls}">
            <div class="image-gallery">
              <c:forEach var="imageUrl" items="${product.detailImageUrls}">
                <img src="${pageContext.request.contextPath}/${imageUrl}" alt="상세설명 이미지">
              </c:forEach>
            </div>
          </c:when>
          <c:when test="${not empty product.productDesc}">
            <div>${product.productDesc}</div>
          </c:when>
          <c:otherwise>
            <p class="desc-empty">등록된 상세설명이 없습니다.</p>
          </c:otherwise>
        </c:choose>
      </section>

    </main>

  </div>

  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>

</body>

</html>
