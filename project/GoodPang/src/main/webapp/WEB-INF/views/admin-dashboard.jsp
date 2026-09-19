<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>관리자 대시보드</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; background: #f7f8fa; }
    h1 { font-size: 20px; margin-bottom: 24px; }

    .menu-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 16px;
      max-width: 900px;
    }

    .menu-card {
      display: block;
      padding: 20px;
      background: #fff;
      border: 1px solid #eee;
      border-radius: 10px;
      text-decoration: none;
      color: inherit;
    }

    .menu-card:hover { border-color: #346aff; box-shadow: 0 2px 10px rgba(52,106,255,.12); }

    .menu-card h2 { margin: 0 0 6px; font-size: 15px; color: #111; }
    .menu-card p { margin: 0; font-size: 12px; color: #888; }

    .top-bar { display: flex; align-items: center; justify-content: space-between; max-width: 900px; margin-bottom: 24px; }
    .top-bar h1 { margin: 0; }
    .top-bar .admin-name { font-size: 13px; color: #555; }
    .top-bar .logout-link { margin-left: 12px; color: #888; text-decoration: none; }
    .top-bar .logout-link:hover { text-decoration: underline; }
  </style>

</head>

<body>

  <div class="top-bar">
    <h1>관리자 대시보드</h1>
    <div>
      <span class="admin-name">${sessionScope.adminName}님</span>
      <a class="logout-link" href="${pageContext.request.contextPath}/admin/logout">로그아웃</a>
    </div>
  </div>

  <div class="menu-grid">

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/products">
      <h2>상품 승인 관리</h2>
      <p>등록된 상품을 확인하고 승인/판매중지 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/sellers">
      <h2>판매자 입점 심사</h2>
      <p>입점 신청한 판매자 목록을 확인하고 승인/반려 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/deliveries">
      <h2>배송 관리</h2>
      <p>배송중인 주문 목록을 확인하고 배송완료 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/notices">
      <h2>공지사항 관리</h2>
      <p>판매자에게 노출되는 공지사항을 등록/수정/삭제합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/action-logs">
      <h2>액션 로그</h2>
      <p>관리자가 수행한 승인/반려/정지/삭제 등의 처리 이력을 확인합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/vendor-action-logs">
      <h2>판매자 액션 로그</h2>
      <p>판매자가 상품 등록/노출전환/판매중지/옵션수정/배송처리 등에서 수행한 작업 이력을 확인합니다.</p>
    </a>

  </div>

</body>

</html>
