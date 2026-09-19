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
  <title>상품 목록 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin-bottom: 16px; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; white-space: nowrap; }
    th { background: #fafafa; color: #555; }
    .empty { padding: 40px; text-align: center; color: #999; }
    .thumb { width: 44px; height: 44px; object-fit: cover; border-radius: 4px; background: #f5f5f5; vertical-align: middle; }
    .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; }
    .badge-wait { background: #fff4e5; color: #a15c00; }
    .badge-active { background: #e6f7ec; color: #0f7b3c; }
    .badge-soldout { background: #eee; color: #666; }
    .badge-stopped { background: #fdecea; color: #c0392b; }
    .action-form { display: inline; margin-right: 4px; }
    .btn { padding: 6px 12px; border-radius: 6px; border: none; font-size: 12px; cursor: pointer; }
    .btn-approve { background: #0f7b3c; color: #fff; }
    .btn-reject { background: #c0392b; color: #fff; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/dashboard">&larr; 대시보드로</a>

  <h1>상품 목록 (${fn:length(productList)}건)</h1>

  <c:choose>

    <c:when test="${empty productList}">
      <div class="empty">등록된 상품이 없습니다.</div>
    </c:when>

    <c:otherwise>

      <table>
        <thead>
          <tr>
            <th>이미지</th>
            <th>상품번호</th>
            <th>상품명</th>
            <th>판매자</th>
            <th>카테고리</th>
            <th>판매가</th>
            <th>판매 상태</th>
            <th>등록일</th>
            <th>승인 처리</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="product" items="${productList}">
            <tr>
              <td>
                <c:if test="${not empty product.thumbnailUrl}">
                  <img class="thumb" src="${pageContext.request.contextPath}/${product.thumbnailUrl}" alt="">
                </c:if>
              </td>
              <td>${product.productNo}</td>
              <td>${product.productName}</td>
              <td>${product.storeName}</td>
              <td>${product.mainCategoryName} &gt; ${product.midCategoryName} &gt; ${product.subCategoryName}</td>
              <td>
                <c:choose>
                  <c:when test="${product.minPrice == product.maxPrice}">
                    <fmt:formatNumber value="${product.minPrice}" pattern="#,##0" />원
                  </c:when>
                  <c:otherwise>
                    <fmt:formatNumber value="${product.minPrice}" pattern="#,##0" />원 ~ <fmt:formatNumber value="${product.maxPrice}" pattern="#,##0" />원
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${product.saleStatus == '승인 대기'}">
                    <span class="badge badge-wait">승인 대기</span>
                  </c:when>
                  <c:when test="${product.saleStatus == '판매 중'}">
                    <span class="badge badge-active">판매 중</span>
                  </c:when>
                  <c:when test="${product.saleStatus == '품절'}">
                    <span class="badge badge-soldout">품절</span>
                  </c:when>
                  <c:when test="${product.saleStatus == '판매 중지'}">
                    <span class="badge badge-stopped">판매 중지</span>
                  </c:when>
                  <c:otherwise>${product.saleStatus}</c:otherwise>
                </c:choose>
              </td>
              <td>
                <fmt:formatDate value="${product.createdDate}" pattern="yyyy-MM-dd HH:mm" />
              </td>
              <td>
                <c:if test="${product.saleStatus == '승인 대기'}">
                  <form class="action-form" method="post" action="${pageContext.request.contextPath}/admin/product-approve"
                        onsubmit="return confirm('이 상품을 승인하시겠습니까?');">
                    <input type="hidden" name="productNo" value="${product.productNo}">
                    <input type="hidden" name="action" value="approve">
                    <button class="btn btn-approve" type="submit">승인</button>
                  </form>
                  <form class="action-form" method="post" action="${pageContext.request.contextPath}/admin/product-approve"
                        onsubmit="return confirm('이 상품을 반려(판매중지)하시겠습니까?');">
                    <input type="hidden" name="productNo" value="${product.productNo}">
                    <input type="hidden" name="action" value="reject">
                    <button class="btn btn-reject" type="submit">반려</button>
                  </form>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

    </c:otherwise>

  </c:choose>

</body>

</html>
