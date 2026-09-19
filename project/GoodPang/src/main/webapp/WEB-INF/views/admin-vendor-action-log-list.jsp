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
  <title>판매자 액션 로그 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin: 0 0 16px; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; }
    th { background: #fafafa; color: #555; }
    .empty { padding: 40px; text-align: center; color: #999; }
    .target-tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 700; background: #eef1f6; color: #555; }
    .detail { color: #888; }
    .pagination { display: flex; align-items: center; justify-content: center; gap: 4px; padding: 20px 0; }
    .pagination a { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
      border-radius: 6px; font-size: 13px; font-weight: 600; color: #555; text-decoration: none; }
    .pagination a:hover { background: #f4f6fb; }
    .pagination a.active { background: #346aff; color: #fff; font-weight: 700; }

    .search-form {
      display: flex;
      flex-wrap: wrap;
      align-items: flex-end;
      gap: 10px;
      padding: 14px 16px;
      margin-bottom: 16px;
      background: #fafbfc;
      border: 1px solid #eee;
      border-radius: 8px;
    }
    .search-field { display: flex; flex-direction: column; gap: 4px; }
    .search-field label { font-size: 11px; color: #888; }
    .search-field input, .search-field select {
      height: 32px;
      padding: 0 8px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 13px;
    }
    .search-field input[type="date"] { width: 140px; }
    .search-field input[type="text"] { width: 160px; }
    .search-buttons { display: flex; gap: 6px; }
    .search-buttons button, .search-buttons a {
      height: 32px;
      padding: 0 14px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 600;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      cursor: pointer;
    }
    .btn-search { background: #346aff; color: #fff; border: none; }
    .btn-reset { background: #fff; color: #555; border: 1px solid #ddd; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/dashboard">&larr; 대시보드로</a>

  <h1>판매자 액션 로그 (${totalCount}건)</h1>

  <form class="search-form" method="get" action="${pageContext.request.contextPath}/admin/vendor-action-logs">

    <div class="search-field">
      <label for="storeName">스토어명</label>
      <input type="text" id="storeName" name="storeName" value="${searchStoreName}" placeholder="스토어명 검색">
    </div>

    <div class="search-field">
      <label for="actionType">액션</label>
      <select id="actionType" name="actionType">
        <option value="" ${empty searchActionType ? 'selected' : ''}>전체</option>
        <option value="상품 등록" ${searchActionType == '상품 등록' ? 'selected' : ''}>상품 등록</option>
        <option value="상품 노출" ${searchActionType == '상품 노출' ? 'selected' : ''}>상품 노출</option>
        <option value="상품 숨김" ${searchActionType == '상품 숨김' ? 'selected' : ''}>상품 숨김</option>
        <option value="판매 재개" ${searchActionType == '판매 재개' ? 'selected' : ''}>판매 재개</option>
        <option value="판매 중지" ${searchActionType == '판매 중지' ? 'selected' : ''}>판매 중지</option>
        <option value="옵션 수정" ${searchActionType == '옵션 수정' ? 'selected' : ''}>옵션 수정</option>
        <option value="배송 처리" ${searchActionType == '배송 처리' ? 'selected' : ''}>배송 처리</option>
        <option value="배송 완료" ${searchActionType == '배송 완료' ? 'selected' : ''}>배송 완료</option>
        <option value="판매자 탈퇴" ${searchActionType == '판매자 탈퇴' ? 'selected' : ''}>판매자 탈퇴</option>
      </select>
    </div>

    <div class="search-field">
      <label for="targetType">대상 유형</label>
      <select id="targetType" name="targetType">
        <option value="" ${empty searchTargetType ? 'selected' : ''}>전체</option>
        <option value="PRODUCT" ${searchTargetType == 'PRODUCT' ? 'selected' : ''}>PRODUCT</option>
        <option value="PRODUCT_OPTION" ${searchTargetType == 'PRODUCT_OPTION' ? 'selected' : ''}>PRODUCT_OPTION</option>
        <option value="ORDERS" ${searchTargetType == 'ORDERS' ? 'selected' : ''}>ORDERS</option>
        <option value="SELLER" ${searchTargetType == 'SELLER' ? 'selected' : ''}>SELLER</option>
      </select>
    </div>

    <div class="search-field">
      <label for="startDate">시작일</label>
      <input type="date" id="startDate" name="startDate" value="${searchStartDate}">
    </div>

    <div class="search-field">
      <label for="endDate">종료일</label>
      <input type="date" id="endDate" name="endDate" value="${searchEndDate}">
    </div>

    <div class="search-buttons">
      <button class="btn-search" type="submit">검색</button>
      <a class="btn-reset" href="${pageContext.request.contextPath}/admin/vendor-action-logs">초기화</a>
    </div>

  </form>

  <c:choose>

    <c:when test="${empty logList}">
      <div class="empty">기록된 액션 로그가 없습니다.</div>
    </c:when>

    <c:otherwise>

      <table>
        <thead>
          <tr>
            <th>일시</th>
            <th>판매자(스토어)</th>
            <th>액션</th>
            <th>대상</th>
            <th>상세</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="log" items="${logList}">
            <tr>
              <td><fmt:formatDate value="${log.actionDate}" pattern="yyyy-MM-dd HH:mm" /></td>
              <td>${log.storeName} (#${log.sellerNo})</td>
              <td>${log.actionType}</td>
              <td><span class="target-tag">${log.targetType} #${log.targetNo}</span></td>
              <td class="detail">${not empty log.detail ? log.detail : '-'}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

    </c:otherwise>

  </c:choose>

  <c:if test="${totalPages > 1}">
    <nav class="pagination" aria-label="페이지 이동">

      <c:if test="${page > 1}">
        <c:url var="prevUrl" value="/admin/vendor-action-logs">
          <c:param name="page" value="${page - 1}" />
          <c:param name="storeName" value="${searchStoreName}" />
          <c:param name="actionType" value="${searchActionType}" />
          <c:param name="targetType" value="${searchTargetType}" />
          <c:param name="startDate" value="${searchStartDate}" />
          <c:param name="endDate" value="${searchEndDate}" />
        </c:url>
        <a href="${prevUrl}">&larr;</a>
      </c:if>

      <c:forEach var="p" begin="1" end="${totalPages}">
        <c:url var="pageUrl" value="/admin/vendor-action-logs">
          <c:param name="page" value="${p}" />
          <c:param name="storeName" value="${searchStoreName}" />
          <c:param name="actionType" value="${searchActionType}" />
          <c:param name="targetType" value="${searchTargetType}" />
          <c:param name="startDate" value="${searchStartDate}" />
          <c:param name="endDate" value="${searchEndDate}" />
        </c:url>
        <a class="${p == page ? 'active' : ''}" href="${pageUrl}">${p}</a>
      </c:forEach>

      <c:if test="${page < totalPages}">
        <c:url var="nextUrl" value="/admin/vendor-action-logs">
          <c:param name="page" value="${page + 1}" />
          <c:param name="storeName" value="${searchStoreName}" />
          <c:param name="actionType" value="${searchActionType}" />
          <c:param name="targetType" value="${searchTargetType}" />
          <c:param name="startDate" value="${searchStartDate}" />
          <c:param name="endDate" value="${searchEndDate}" />
        </c:url>
        <a href="${nextUrl}">&rarr;</a>
      </c:if>

    </nav>
  </c:if>

</body>

</html>
