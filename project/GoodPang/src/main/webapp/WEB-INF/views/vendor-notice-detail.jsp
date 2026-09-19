<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
  <title>공지사항 - ${notice.title}</title>

  <style>
    .notice-detail .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    .notice-detail h1 { font-size: 22px; margin: 0 0 8px; }
    .notice-detail .meta { color: #888; font-size: 13px; margin-bottom: 20px; }
    .notice-detail .content { border-top: 1px solid #eee; padding-top: 20px; font-size: 14px; line-height: 1.7; white-space: pre-wrap; }
    .notice-tag { display: inline-block; padding: 2px 6px; border-radius: 4px; font-size: 11px; font-weight: 700; margin-right: 6px; vertical-align: middle; }
    .tag-notice { background: #fdeceb; color: #f4514a; }
    .tag-info { background: #eef1f6; color: #777; }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "notice"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <section class="panel notice-detail" style="padding: 24px;">

        <a class="back-link" href="${pageContext.request.contextPath}/vendor/notice">&larr; 목록으로</a>

        <h1><span class="notice-tag ${notice.noticeType == '공지' ? 'tag-notice' : 'tag-info'}">${notice.noticeType}</span>${notice.title}</h1>
        <p class="meta">
          ${notice.adminName} · <fmt:formatDate value="${notice.createdDate}" pattern="yyyy-MM-dd HH:mm" />
        </p>

        <div class="content">${notice.content}</div>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>

</body>

</html>
