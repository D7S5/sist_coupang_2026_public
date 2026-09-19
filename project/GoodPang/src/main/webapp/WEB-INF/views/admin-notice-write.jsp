<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>공지사항 등록 - 관리자</title>
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin: 0 0 16px; }
    .error { color: #f4514a; margin-bottom: 12px; font-size: 13px; }
    .field { margin-bottom: 14px; }
    .field label { display: block; margin-bottom: 6px; font-size: 13px; color: #555; }
    .field input[type="text"], .field textarea, .field select {
      width: 100%; max-width: 640px; padding: 10px 12px;
      border: 1px solid #ddd; border-radius: 6px; font-size: 14px; box-sizing: border-box;
    }
    .field textarea { min-height: 240px; resize: vertical; font-family: inherit; }
    .btn { padding: 8px 16px; border-radius: 6px; border: none; font-size: 13px; cursor: pointer; }
    .btn-primary { background: #346aff; color: #fff; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/notices">&larr; 목록으로</a>

  <h1>공지사항 등록</h1>

  <% if (request.getAttribute("error") != null) { %>
    <p class="error"><%= request.getAttribute("error") %></p>
  <% } %>

  <form method="post" action="${pageContext.request.contextPath}/admin/notice/write">

    <%
      String selectedType = request.getParameter("noticeType");
      if (selectedType == null) selectedType = "안내";
    %>

    <div class="field">
      <label>구분</label>
      <select name="noticeType">
        <option value="공지" <%= "공지".equals(selectedType) ? "selected" : "" %>>공지</option>
        <option value="안내" <%= "안내".equals(selectedType) ? "selected" : "" %>>안내</option>
      </select>
    </div>

    <div class="field">
      <label>제목</label>
      <input type="text" name="title" maxlength="200" required
             value="<%= request.getParameter("title") != null ? request.getParameter("title") : "" %>">
    </div>

    <div class="field">
      <label>내용</label>
      <textarea name="content" required><%= request.getParameter("content") != null ? request.getParameter("content") : "" %></textarea>
    </div>

    <button class="btn btn-primary" type="submit">등록</button>

  </form>

</body>

</html>
