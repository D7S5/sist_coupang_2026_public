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
  <title>판매자 목록 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin-bottom: 16px; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; white-space: nowrap; }
    th { background: #fafafa; color: #555; }
    tr.row-link { cursor: pointer; }
    tr.row-link:hover { background: #f5f8ff; }
    .empty { padding: 40px; text-align: center; color: #999; }
    .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; }
    .badge-wait { background: #fff4e5; color: #a15c00; }
    .badge-review { background: #e8f0fe; color: #1a56db; }
    .badge-approved { background: #e6f7ec; color: #0f7b3c; }
    .badge-rejected { background: #fdecea; color: #c0392b; }
    .badge-suspended { background: #f3f0ff; color: #6c3ce9; }
    .badge-withdrawn { background: #f2f2f2; color: #666; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/dashboard">&larr; 대시보드로</a>

  <h1>판매자 목록 (${fn:length(sellerList)}건)</h1>

  <c:choose>

    <c:when test="${empty sellerList}">
      <div class="empty">등록된 판매자가 없습니다.</div>
    </c:when>

    <c:otherwise>

      <table>
        <thead>
          <tr>
            <th>번호</th>
            <th>상호명</th>
            <th>대표자명</th>
            <th>담당자명</th>
            <th>이메일</th>
            <th>휴대폰</th>
            <th>사업자등록번호</th>
            <th>사업자유형</th>
            <th>입점심사 상태</th>
            <th>가입일</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="seller" items="${sellerList}">
            <tr class="row-link"
              onclick="location.href='${pageContext.request.contextPath}/admin/seller-detail?sellerNo=${seller.sellerNo}'">
              <td>${seller.sellerNo}</td>
              <td>${seller.storeName}</td>
              <td>${seller.ceoName}</td>
              <td>${seller.managerName}</td>
              <td>${seller.email}</td>
              <td>${seller.phone}</td>
              <td>${seller.businessNo}</td>
              <td>${seller.businessType}</td>
              <td>
                <c:choose>
                  <c:when test="${seller.approvalStatus == '입점 대기'}">
                    <span class="badge badge-wait">입점 대기</span>
                  </c:when>
                  <c:when test="${seller.approvalStatus == '심사 중'}">
                    <span class="badge badge-review">심사 중</span>
                  </c:when>
                  <c:when test="${seller.approvalStatus == '승인'}">
                    <span class="badge badge-approved">승인</span>
                  </c:when>
                  <c:when test="${seller.approvalStatus == '반려'}">
                    <span class="badge badge-rejected">반려</span>
                  </c:when>
                  <c:when test="${seller.approvalStatus == '정지'}">
                    <span class="badge badge-suspended">정지</span>
                  </c:when>
                  <c:when test="${seller.approvalStatus == '탈퇴'}">
                    <span class="badge badge-withdrawn">탈퇴</span>
                  </c:when>
                  <c:otherwise>
                    ${seller.approvalStatus}
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <fmt:formatDate value="${seller.createdDate}" pattern="yyyy-MM-dd HH:mm" />
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

    </c:otherwise>

  </c:choose>

</body>

</html>
