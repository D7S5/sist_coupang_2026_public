<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- =========================================================
     icon-sprite.jspf — 판매자센터 공통 아이콘 스프라이트

     vendor_dashboard.jsp / vendor_orders.jsp / vendor_product_write.jsp
     vendor_products.jsp 4개 파일에 각자 따로 있던 <symbol> 목록을
     전부 모아서 중복 제거한 것.

     ⚠ 이 파일은 <%@ page %> 지시어를 넣지 않음.
     이 파일은 <%@ include %>(정적 include)로만 쓰이는데, 정적 include는
     번역(translation) 단계에서 이 파일 내용을 포함하는 JSP 소스에
     그대로 이어붙이는 방식이라 인코딩은 "포함하는 쪽" JSP의
     <%@ page pageEncoding="UTF-8" %> 하나로 전체가 결정됨.
     그래서 이 파일에 따로 page 지시어를 넣어도 아무 효과가 없고,
     오히려 저장할 때 편집기가 이 파일을 UTF-8이 아닌 다른 인코딩(EUC-KR 등)으로
     잘못 열었다가 다시 저장하면 한글이 깨져버리는 사고만 나기 쉬움
     (실제로 이번에 한 번 그렇게 깨졌었음 — 파일 자체 바이트가
      "UTF-8로 잘못 읽은 뒤 다시 UTF-8로 저장"된 이중 손상 상태였음).

     심볼은 실제로 <use href="#ic-xxx">로 참조될 때만 화면에 그려지고
     이 태그 자체는 style="display:none"이라 안 보이므로,
     이 페이지에서 안 쓰는 아이콘이 섞여 있어도 아무 문제 없음
     (그래서 페이지별로 골라 담을 필요 없이 통째로 공유해도 됨)
========================================================= --%>
<svg class="icon-sprite" xmlns="http://www.w3.org/2000/svg" style="display:none">
  <defs>

    <symbol id="ic-home" viewBox="0 0 24 24">
      <path d="M4 11 L12 4 L20 11 M6 10 V20 H18 V10" />
    </symbol>

    <symbol id="ic-box" viewBox="0 0 24 24">
      <path d="M12 3 L20 7 V17 L12 21 L4 17 V7 Z" />
      <path d="M4 7 L12 11 L20 7" />
      <path d="M12 11 V21" />
    </symbol>

    <symbol id="ic-truck" viewBox="0 0 24 24">
      <rect x="2" y="8" width="11" height="8" />
      <path d="M13 11 H18 L21 14 V16 H13 Z" />
      <circle cx="6" cy="18" r="1.6" />
      <circle cx="17" cy="18" r="1.6" />
    </symbol>

    <symbol id="ic-calculator" viewBox="0 0 24 24">
      <rect x="5" y="3" width="14" height="18" rx="1" />
      <rect x="7" y="5" width="10" height="4" />
      <circle cx="8.5" cy="12" r="0.9" fill="currentColor" stroke="none" />
      <circle cx="12" cy="12" r="0.9" fill="currentColor" stroke="none" />
      <circle cx="15.5" cy="12" r="0.9" fill="currentColor" stroke="none" />
      <circle cx="8.5" cy="16" r="0.9" fill="currentColor" stroke="none" />
      <circle cx="12" cy="16" r="0.9" fill="currentColor" stroke="none" />
      <circle cx="15.5" cy="16" r="0.9" fill="currentColor" stroke="none" />
    </symbol>

    <symbol id="ic-users" viewBox="0 0 24 24">
      <circle cx="9" cy="8" r="3" />
      <path d="M3 20 C3 16 6 14 9 14 C12 14 15 16 15 20" />
      <circle cx="17" cy="9" r="2.3" />
      <path d="M15.2 20 C15.2 17.2 16.8 15.3 19 15.3 C20.6 15.3 21.5 16.4 21.5 17.6" />
    </symbol>

    <symbol id="ic-megaphone" viewBox="0 0 24 24">
      <path d="M3 10 V14 H6 L14 18 V6 L6 10 Z" />
      <path d="M14 9 A3 3 0 0 1 14 15" />
      <path d="M6 14 L7 19" />
    </symbol>

    <symbol id="ic-store" viewBox="0 0 24 24">
      <path d="M4 9 V20 H20 V9" />
      <path d="M2 9 L4 4 H20 L22 9 Z" />
      <rect x="9" y="13" width="6" height="7" />
    </symbol>

    <symbol id="ic-chart" viewBox="0 0 24 24">
      <path d="M5 20 V11 M11 20 V4 M17 20 V13 M3 20 H21" />
    </symbol>

    <symbol id="ic-chat" viewBox="0 0 24 24">
      <path d="M4 5 H20 V16 H9 L5 20 V16 H4 Z" />
    </symbol>

    <symbol id="ic-bell" viewBox="0 0 24 24">
      <path d="M12 3 C9 3 7 5.5 7 8.5 V12 L5 16 H19 L17 12 V8.5 C17 5.5 15 3 12 3 Z" />
      <path d="M10 19 a2 2 0 0 0 4 0" />
    </symbol>

    <symbol id="ic-book" viewBox="0 0 24 24">
      <path d="M4 4 H12 V20 H4 Z" />
      <path d="M12 4 H20 V20 H12 Z" />
      <path d="M12 4 V20" />
    </symbol>

    <symbol id="ic-gear" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="3" />
      <path
        d="M12 2 V5 M12 19 V22 M2 12 H5 M19 12 H22 M4.9 4.9 L7 7 M17 17 L19.1 19.1 M19.1 4.9 L17 7 M7 17 L4.9 19.1" />
    </symbol>

    <symbol id="ic-menu" viewBox="0 0 24 24">
      <path d="M3 6 H21 M3 12 H21 M3 18 H21" />
    </symbol>

    <symbol id="ic-help" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="9" />
      <path d="M9.5 9.3 a2.5 2.5 0 1 1 3.5 2.3 c-.8 .4 -1 1 -1 1.9" />
      <circle cx="12" cy="17" r="0.7" fill="currentColor" stroke="none" />
    </symbol>

    <symbol id="ic-user" viewBox="0 0 24 24">
      <circle cx="12" cy="8" r="4" />
      <path d="M4 20 C4 15.5 7.5 13 12 13 C16.5 13 20 15.5 20 20" />
    </symbol>

    <symbol id="ic-chevron-down" viewBox="0 0 24 24">
      <path d="M6 9 L12 15 L18 9" />
    </symbol>

    <symbol id="ic-chevron-left" viewBox="0 0 24 24">
      <path d="M15 5 L8 12 L15 19" />
    </symbol>

    <symbol id="ic-search" viewBox="0 0 24 24">
      <circle cx="11" cy="11" r="7" />
      <path d="M20 20 L16 16" />
    </symbol>

    <symbol id="ic-plus" viewBox="0 0 24 24">
      <path d="M12 5 V19 M5 12 H19" />
    </symbol>

    <symbol id="ic-close" viewBox="0 0 24 24">
      <path d="M6 6 L18 18 M18 6 L6 18" />
    </symbol>

    <symbol id="ic-image" viewBox="0 0 24 24">
      <rect x="3" y="4" width="18" height="16" rx="2" />
      <circle cx="9" cy="10" r="1.8" />
      <path d="M4 17 L9.5 12 L13 15 L16 12 L20 16" />
    </symbol>

    <symbol id="ic-cart" viewBox="0 0 24 24">
      <circle cx="9" cy="20" r="1.4" />
      <circle cx="17" cy="20" r="1.4" />
      <path d="M3 4 H6 L8.5 15 H18 L20 7 H7" />
    </symbol>

    <symbol id="ic-receipt" viewBox="0 0 24 24">
      <path d="M6 3 H16 L19 6 V21 H6 Z" />
      <path d="M16 3 V6 H19" />
      <path d="M9 11 H15 M9 14 H15 M9 17 H13" />
    </symbol>

    <symbol id="ic-eye" viewBox="0 0 24 24">
      <path d="M2 12 C5 6 19 6 22 12 C19 18 5 18 2 12 Z" />
      <circle cx="12" cy="12" r="3" />
    </symbol>

    <symbol id="ic-clock" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="8" />
      <path d="M12 7 V12 L16 14" />
    </symbol>

    <symbol id="ic-check-circle" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="9" />
      <path d="M8 12 L11 15 L16 9" />
    </symbol>

    <symbol id="ic-clipboard-check" viewBox="0 0 24 24">
      <rect x="6" y="4" width="12" height="17" rx="1" />
      <path d="M9 3 H15 V6 H9 Z" />
      <path d="M9 13 L11 15 L15 10" />
    </symbol>

    <symbol id="ic-return" viewBox="0 0 24 24">
      <path d="M4 9 A8 8 0 1 1 4 15" />
      <path d="M4 4 V9 H9" />
    </symbol>

    <symbol id="ic-calendar" viewBox="0 0 24 24">
      <rect x="4" y="5" width="16" height="15" rx="1" />
      <path d="M4 9 H20" />
      <path d="M8 3 V6 M16 3 V6" />
    </symbol>

    <symbol id="ic-smile" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="9" />
      <circle cx="9" cy="10" r="1" fill="currentColor" stroke="none" />
      <circle cx="15" cy="10" r="1" fill="currentColor" stroke="none" />
      <path d="M8 14 C9.5 16.5 14.5 16.5 16 14" />
    </symbol>

    <symbol id="ic-bag" viewBox="0 0 24 24">
      <path d="M6 8 H18 L19 21 H5 Z" />
      <path d="M9 8 V6 a3 3 0 0 1 6 0 V8" />
    </symbol>

    <symbol id="ic-card" viewBox="0 0 24 24">
      <rect x="3" y="5" width="18" height="14" rx="2" />
      <path d="M3 10 H21" />
      <path d="M6 15 H10" />
    </symbol>

    <symbol id="ic-filter" viewBox="0 0 24 24">
      <path d="M4 5 H20 L14 13 V19 L10 21 V13 Z" />
    </symbol>

    <symbol id="ic-doc-plus" viewBox="0 0 24 24">
      <path d="M6 3 H15 L19 7 V21 H6 Z" />
      <path d="M15 3 V7 H19" />
      <path d="M12 12 V18 M9 15 H15" />
    </symbol>

    <symbol id="ic-list" viewBox="0 0 24 24">
      <rect x="5" y="3" width="14" height="18" rx="1" />
      <path d="M9 8 H15 M9 12 H15 M9 16 H13" />
    </symbol>

    <symbol id="ic-ban" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="9" />
      <path d="M5.5 5.5 L18.5 18.5" />
    </symbol>

    <symbol id="ic-pause-circle" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="9" />
      <path d="M10 8.5 V15.5 M14 8.5 V15.5" />
    </symbol>

  </defs>
</svg>
