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
  <title>액션 로그 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin: 0 0 16px; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; }
    th { background: #fafafa; color: #555; }
    .empty { padding: 40px; text-align: center; color: #999; }
    .target-tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: 700; background: #eef1f6; color: #555; }
    .reason { color: #888; }
    .pagination { display: flex; align-items: center; justify-content: center; gap: 4px; padding: 20px 0; }
    .pagination a { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
      border-radius: 6px; font-size: 13px; font-weight: 600; color: #555; text-decoration: none; }
    .pagination a:hover { background: #f4f6fb; }
    .pagination a.active { background: #346aff; color: #fff; font-weight: 700; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/dashboard">&larr; 대시보드로</a>

  <h1>액션 로그 (${totalCount}건)</h1>

  <c:choose>

    <c:when test="${empty logList}">
      <div class="empty">기록된 액션 로그가 없습니다.</div>
    </c:when>

    <c:otherwise>

      <table>
        <thead>
          <tr>
            <th>일시</th>
            <th>관리자</th>
            <th>액션</th>
            <th>대상</th>
            <th>사유</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="log" items="${logList}">
            <tr>
              <td><fmt:formatDate value="${log.actionDate}" pattern="yyyy-MM-dd HH:mm" /></td>
              <td>${log.adminName}</td>
              <td>${log.actionType}</td>
              <td><span class="target-tag">${log.targetType} #${log.targetNo}</span></td>
              <td class="reason">${not empty log.reason ? log.reason : '-'}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

    </c:otherwise>

  </c:choose>

  <c:if test="${totalPages > 1}">
    <nav class="pagination" aria-label="페이지 이동">

      <c:if test="${page > 1}">
        <a href="${pageContext.request.contextPath}/admin/action-logs?page=${page - 1}">&larr;</a>
      </c:if>

      <c:forEach var="p" begin="1" end="${totalPages}">
        <a class="${p == page ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/action-logs?page=${p}">${p}</a>
      </c:forEach>

      <c:if test="${page < totalPages}">
        <a href="${pageContext.request.contextPath}/admin/action-logs?page=${page + 1}">&rarr;</a>
      </c:if>

    </nav>
  </c:if>

</body>

</html>
