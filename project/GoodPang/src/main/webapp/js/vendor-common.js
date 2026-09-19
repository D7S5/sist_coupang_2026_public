/* =========================================================
   vendor-common.js — 판매자센터 공통 동작

   vendor_dashboard.jsp / vendor_orders.jsp / vendor_products.jsp
   vendor_product_write.jsp 4개 파일에 똑같이 복사돼있던 스크립트를
   여기 하나로 모음.

   ⚠ 로드 순서 주의: 각 페이지 자신의 <script> 보다 "먼저" 이 파일을
   불러와야 함. 페이지별 스크립트가 여기서 정의한 setupDropdown() 같은
   함수를 그대로 가져다 쓰기 때문 (예: vendor_dashboard.jsp의 날짜선택 드롭다운)
   ========================================================= */


/* ---------------------------------------------------------
   1. 사이드바 서브메뉴 아코디언
   --------------------------------------------------------- */

document.querySelectorAll(".side-group-toggle").forEach(function (button) {
  button.addEventListener("click", function () {
    const submenu = button.nextElementSibling;
    const isOpen = button.classList.contains("open");

    document.querySelectorAll(".side-group-toggle.open").forEach(function (other) {
      if (other !== button) {
        other.classList.remove("open");
        other.nextElementSibling.classList.remove("open");
      }
    });

    button.classList.toggle("open", !isOpen);
    submenu.classList.toggle("open", !isOpen);
  });
});


/* ---------------------------------------------------------
   2. 사이드바 접기/펼치기 (상단바 햄버거 버튼 + 사이드바 하단 버튼)

   collapseBtn은 페이지에 없을 수도 있어서(→ 이제는 sidebar.jspf 덕분에
   모든 페이지에 다 있지만) 안전하게 존재 여부를 확인하고 붙임
   --------------------------------------------------------- */

const sidebarEl = document.getElementById("sidebar");

const sidebarToggleBtn = document.getElementById("sidebarToggle");
if (sidebarToggleBtn && sidebarEl) {
  sidebarToggleBtn.addEventListener("click", function () {
    sidebarEl.classList.toggle("collapsed");
  });
}

const collapseBtn = document.getElementById("collapseBtn");
if (collapseBtn && sidebarEl) {
  collapseBtn.addEventListener("click", function () {
    sidebarEl.classList.toggle("collapsed");
  });
}


/* ---------------------------------------------------------
   3. 드롭다운 공통 (사용자 메뉴 / 날짜선택 등)

   setupDropdown()은 페이지별 스크립트에서도 그대로 재사용함
   (예: vendor_dashboard.jsp → setupDropdown("datePickerTrigger", "datePickerPanel"))

   열려있는 드롭다운 목록을 배열에 등록해두고, 화면 아무데나 클릭하면
   전부 닫음 — 페이지마다 "바깥 클릭하면 닫기" 코드를 따로 안 써도 됨
   --------------------------------------------------------- */

const openDropdownPanels = [];

function setupDropdown(triggerId, panelId) {
  const trigger = document.getElementById(triggerId);
  const panel = document.getElementById(panelId);
  if (!trigger || !panel) return;

  openDropdownPanels.push(panel);

  trigger.addEventListener("click", function (event) {
    event.stopPropagation();
    panel.classList.toggle("open");
  });
}

setupDropdown("userMenuTrigger", "userMenuPanel");

document.addEventListener("click", function () {
  openDropdownPanels.forEach(function (panel) {
    panel.classList.remove("open");
  });
});
