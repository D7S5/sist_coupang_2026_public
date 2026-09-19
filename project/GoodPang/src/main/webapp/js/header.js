/* =========================================================
   header.js — 헤더에서 일어나는 동작들

   이 파일은 index.html 맨 아래 </body> 바로 앞에서 불러옴.
   그래서 HTML이 전부 만들어진 뒤에 실행돼서
   document.querySelector 가 요소를 제대로 찾을 수 있음.
   ========================================================= */


/* ---------------------------------------------------------
   1. 카테고리 메뉴 — /category/getinfo?ctype=main 결과로 채우기
   쿠팡 원본: #wa-pc-category (평소엔 display:none, CSS :hover로 펼쳐짐)

   서버(CategoryInfo 서블릿)가 ctype=main 일 때 레벨별로 묶어서 줌:
     { "1": [{categoryNo, categoryName, parentCategoryNo, categoryLevel}, ...],
       "2": [...], "3": [...] }

   3단(대분류>중분류>소분류) 구조:
     대분류(li)  → :hover 로 열림 (CSS만으로 처리, 기존 방식 그대로)
     중분류(depth ul li) → 여러 개 중 하나만 소분류를 보여줘야 해서
       CSS :hover 형제선택자만으로는 표현이 번거로워, mouseover 이벤트로 처리
     소분류(depth2) → 중분류 li 하나당 하나씩, .active 클래스가 붙은 것만 보임
   --------------------------------------------------------- */

const categoryMenu = document.querySelector('#wa-pc-category .menu');

if (categoryMenu) {
  fetch('/category/getinfo?ctype=main')
    .then(function (res) { return res.json(); })
    .then(renderCategoryMenu)
    .catch(function (e) {
      console.error('카테고리 메뉴를 불러오지 못했습니다.', e);
    });
}

function renderCategoryMenu(data) {

  const mainList = data['1'] || [];
  const midList = data['2'] || [];
  const subList = data['3'] || [];

  categoryMenu.innerHTML = mainList.map(function (main) {
    const children = midList.filter(function (mid) {
      return mid.parentCategoryNo === main.categoryNo;
    });

    const iconHtml = getCategoryIconHtml(main.categoryName);

    // 하위 항목(중분류)이 있을 때만 ▶ 표시와 depth 패널을 붙임
    if (children.length === 0) {
		 return '<li><a href="/category?categoryNo=' + main.categoryNo + '">' + iconHtml + escapeCategoryHtml(main.categoryName) + '</a></li>';
    }

    // 2026-09-03: 대분류(레벨1) 페이지가 생겨서 실제 이동이 가능해짐 — 예전엔 미완성 페이지로
    // 못 가게 일부러 href="#" 로 막아뒀었음(CLAUDE.md 4장 참고). 패널이 열리는 건 CSS :hover
    // (common.css:1239)라서 href 값과 무관 — 여기 바꿔도 호버 동작엔 영향 없음
    // return '<li><a href="#">' + iconHtml + escapeCategoryHtml(main.categoryName) + '<i class="si"></i></a>'
    return '<li><a href="/category?categoryNo=' + main.categoryNo + '">' + iconHtml + escapeCategoryHtml(main.categoryName) + '<i class="si"></i></a>'
      + renderMidPanel(children, subList, main.imgUrl)
      + '</li>';
  }).join('');
}

/* ---------------------------------------------------------
   대분류 왼쪽 아이콘 — 진짜 스프라이트 이미지가 없어서
   카테고리명 기준으로 간단한 인라인 SVG를 직접 그려 넣음.
   지도에 없는 이름이 오면 기본 아이콘(태그 모양)으로 대체.
   --------------------------------------------------------- */

const CATEGORY_ICON_PATHS = {
  '패션의류/잡화': '<circle cx="12" cy="4.5" r="1.3"/><path d="M12 5.8 V7.2"/><path d="M12 7.2 L3 13 Q2 13.6 3 14 H21 Q22 13.6 21 13 Z"/>',
  '뷰티': '<path d="M9 3 H15 L14 9 H10 Z"/><path d="M10 9 H14 V19 A2 2 0 0 1 12 21 A2 2 0 0 1 10 19 Z"/>',
  '출산/유아동': '<path d="M9 3 H15 V6 H9 Z"/><path d="M9.5 6 H14.5 L15 9 V19 A2 2 0 0 1 13 21 H11 A2 2 0 0 1 9 19 V9 Z"/><path d="M9 13 H15"/>',
  '식품': '<path d="M6 3 V11 M4 3 V8 A2 2 0 0 0 6 10 A2 2 0 0 0 8 8 V3"/><path d="M17 3 C15 5 15 9 17 11 V21"/>',
  '주방용품': '<path d="M4 9 H20 V15 A4 4 0 0 1 16 19 H8 A4 4 0 0 1 4 15 Z"/><path d="M2 9 H6 M18 9 H22"/><path d="M10 5 H14 V9 H10 Z"/>',
  '생활용품': '<path d="M10 3 H14 V5 H10 Z"/><path d="M9 5 H15 L16 8 V20 A2 2 0 0 1 14 22 H10 A2 2 0 0 1 8 20 V8 Z"/>',
  '홈인테리어': '<path d="M12 2 L18 9 H6 Z"/><path d="M12 9 V17"/><path d="M8 21 H16"/><path d="M10 17 H14 L15 21 H9 Z"/>',
  '가전디지털': '<rect x="3" y="4" width="18" height="12" rx="1"/><path d="M8 20 H16 M12 16 V20"/>',
  '스포츠/레저': '<rect x="2" y="9" width="3" height="6"/><rect x="19" y="9" width="3" height="6"/><path d="M5 11 H8 M16 11 H19 M5 13 H8 M16 13 H19"/><rect x="8" y="10" width="8" height="4"/>',
  '자동차용품': '<path d="M3 15 L5 9 H19 L21 15"/><path d="M3 15 H21 V18 H3 Z"/><circle cx="7" cy="18" r="1.6"/><circle cx="17" cy="18" r="1.6"/>',
  '도서/음반/DVD': '<path d="M4 4 H12 V20 H4 Z"/><path d="M12 4 H20 V20 H12 Z"/><path d="M12 4 V20"/>',
  '완구/취미': '<path d="M9 4 H12 A1.5 1.5 0 0 1 12 7 H15 V10 A1.5 1.5 0 0 0 15 13 V16 H12 A1.5 1.5 0 0 1 12 19 H9 V16 A1.5 1.5 0 0 0 9 13 H6 V10 A1.5 1.5 0 0 1 9 10 Z"/>',
  '문구/오피스': '<path d="M4 20 L5 16 L16 5 L19 8 L8 19 Z"/><path d="M14 7 L17 10"/>',
  '반려동물용품': '<circle cx="7" cy="9" r="1.6"/><circle cx="11" cy="6.5" r="1.6"/><circle cx="15" cy="6.5" r="1.6"/><circle cx="18" cy="9.5" r="1.6"/><path d="M12 12 C8 12 6 15 7 18 C7.5 20 9.5 20.5 12 19.5 C14.5 20.5 16.5 20 17 18 C18 15 16 12 12 12 Z"/>',
  '헬스/건강식품': '<rect x="4" y="9" width="16" height="6" rx="3"/><path d="M12 9 V15"/>'
};

/* 지도에 없는 카테고리(새로 추가됐는데 아이콘이 아직 없는 경우)용 기본 태그 아이콘 */
const CATEGORY_ICON_DEFAULT = '<path d="M12 3 L20 3 L20 11 L13 20 L4 11 L4 4 Z"/><circle cx="8" cy="8" r="1.4"/>';

function getCategoryIconHtml(categoryName) {
  const iconPath = CATEGORY_ICON_PATHS[categoryName] || CATEGORY_ICON_DEFAULT;
  return '<span class="cat-icon"><svg viewBox="0 0 24 24">' + iconPath + '</svg></span>';
}

/** 중분류 목록(depth)과 그 오른쪽에 겹쳐지는 소분류 패널들(depth2), 그리고 대분류 프로모 이미지를 함께 만듦 */
function renderMidPanel(midItems, subList, mainImgUrl) {
  let firstWithSub = null;   // 패널이 열리자마자 기본으로 보여줄 소분류 (첫 번째 것)

  const midHtml = midItems.map(function (mid) {
    const subItems = subList.filter(function (sub) {
      return sub.parentCategoryNo === mid.categoryNo;
    });

    if (subItems.length > 0 && firstWithSub === null) {
      firstWithSub = mid.categoryNo;
    }

    if (subItems.length === 0) {
		 return '<li><a href="/category?categoryNo=' + mid.categoryNo + '">' + escapeCategoryHtml(mid.categoryName) + '</a></li>';
    }

    const isActive = mid.categoryNo === firstWithSub;
    return '<li data-mid="' + mid.categoryNo + '"' + (isActive ? ' class="is-active"' : '') + '>'
      + '<a href="/category?categoryNo=' + mid.categoryNo + '">' + escapeCategoryHtml(mid.categoryName) + '<i class="si"></i></a>'
      + '</li>';
  }).join('');

  const depth2Html = midItems.map(function (mid) {
    const subItems = subList.filter(function (sub) {
      return sub.parentCategoryNo === mid.categoryNo;
    });
    if (subItems.length === 0) return '';

    const isActive = mid.categoryNo === firstWithSub;
    const subHtml = subItems.map(function (sub) {
		 return '<li><a href="/category?categoryNo=' + sub.categoryNo + '">' + escapeCategoryHtml(sub.categoryName) + '</a></li>';
    }).join('');

    return '<div class="depth2' + (isActive ? ' active' : '') + '" data-mid="' + mid.categoryNo + '">'
      + '<ul>' + subHtml + '</ul>'
      + '</div>';
  }).join('');

  const promoHtml = mainImgUrl
    ? '<div class="depth-promo"><img src="' + escapeCategoryHtml(mainImgUrl) + '" alt=""></div>'
    : '';

  return '<div class="depth"><ul>' + midHtml + '</ul>' + promoHtml + depth2Html + '</div>';
}

/* 중분류 위에 마우스를 올리면 그 항목의 소분류(depth2)만 보이게 전환
   (이벤트 위임: menu 전체가 fetch 때마다 통째로 다시 그려지므로,
    각 li에 직접 리스너를 다는 대신 categoryMenu 하나에만 걸어둠) */
if (categoryMenu) {
  categoryMenu.addEventListener('mouseover', function (event) {
    const midItem = event.target.closest('li[data-mid]');
    if (!midItem) return;

    const depthPanel = midItem.closest('.depth');
    if (!depthPanel) return;

    depthPanel.querySelectorAll('li[data-mid]').forEach(function (li) {
      li.classList.toggle('is-active', li === midItem);
    });

    depthPanel.querySelectorAll('.depth2').forEach(function (panel) {
      panel.classList.toggle('active', panel.dataset.mid === midItem.dataset.mid);
    });
  });
}

/* 카테고리명이 그대로 HTML로 들어가지 않게 막음 (검색 자동완성 쪽과 같은 목적) */
function escapeCategoryHtml(s) {
  return String(s).replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}


/* ---------------------------------------------------------
   2. GNB 좌우 화살표 스크롤  ✅ 완성 (2026-08-05)

   메뉴가 16개인데 창에는 9개쯤만 보임. 화살표로 밀어서 나머지를 봄.

   움직이는 원리 (CSS와 나눠 가짐):
     .gnb-menu-container  overflow:hidden  ← 창 밖으로 나간 건 잘라냄
     .gnb-menu-scroll     left 값을 바꿈   ← JS가 하는 일은 이것 하나뿐
                          transition 도 CSS에 이미 있어서 저절로 부드럽게 밀림

   화살표 모양도 CSS가 담당함. JS는 .gnb-menu-btn-active 클래스만 붙였다 뗌
     붙어있음 = 진회색 (더 갈 수 있음)  /  없음 = 연회색 (끝)
   --------------------------------------------------------- */

const gnbContainer = document.querySelector('.gnb-menu-container');
const gnbScroll = document.querySelector('.gnb-menu-scroll');
const gnbLeftBtn = document.querySelector('.gnb-menu-btn-left');
const gnbRightBtn = document.querySelector('.gnb-menu-btn-right');

if (gnbContainer && gnbScroll && gnbLeftBtn && gnbRightBtn) {

  let gnbOffset = 0;   // 지금 왼쪽으로 몇 px 밀려 있는지 (0 = 맨 처음)
  let gnbMax = 0;      // 최대로 밀 수 있는 거리
  let gnbStep = 0;     // 한 번 누를 때 밀 거리

  /* 창 너비와 메뉴 전체 길이를 재서 gnbMax / gnbStep 을 다시 계산.
     창 크기가 바뀌면 이 값들도 달라지므로 함수로 빼둠 */
  function gnbMeasure() {
    // 보이는 창의 안쪽 너비 = 패딩 박스 - 좌우 패딩(화살표 자리 32px씩)
    const cs = getComputedStyle(gnbContainer);
    const visible = gnbContainer.clientWidth
      - parseFloat(cs.paddingLeft) - parseFloat(cs.paddingRight);

    /* 메뉴 전체 길이.
       .gnb-menu-scroll 은 width가 9999px로 박혀 있어서 그 값은 쓸 수 없음.
       대신 "마지막 메뉴의 왼쪽 끝 + 그 메뉴의 너비" 로 실제 길이를 구함.
       (offsetLeft 는 position:relative 인 .gnb-menu-scroll 기준이라 안정적) */
    const items = gnbScroll.children;
    const last = items[items.length - 1];
    const total = last ? last.offsetLeft + last.offsetWidth : 0;

    // 넘치는 만큼만 밀 수 있음. 안 넘치면 0 (화살표 둘 다 비활성)
    gnbMax = Math.max(total - visible, 0);
    gnbStep = visible;   // 한 번에 창 하나만큼(= 한 페이지) 이동

    if (gnbOffset > gnbMax) gnbOffset = gnbMax;   // 창이 커져서 덜 밀어도 될 때
    gnbApply();
  }

  /* 계산된 위치를 실제 화면에 반영 */
  function gnbApply() {
    gnbScroll.style.left = -gnbOffset + 'px';

    const canLeft = gnbOffset > 0;
    const canRight = gnbOffset < gnbMax;

    // 화살표 색 (CSS가 이 클래스를 보고 진회색/연회색을 정함)
    gnbLeftBtn.classList.toggle('gnb-menu-btn-active', canLeft);
    gnbRightBtn.classList.toggle('gnb-menu-btn-active', canRight);

    // 못 가는 방향은 아예 눌리지 않게 막음 (색만 바꾸면 눌리긴 해서 헷갈림)
    gnbLeftBtn.disabled = !canLeft;
    gnbRightBtn.disabled = !canRight;
  }

  gnbRightBtn.addEventListener('click', function () {
    gnbOffset = Math.min(gnbOffset + gnbStep, gnbMax);   // 끝을 넘지 않게
    gnbApply();
  });

  gnbLeftBtn.addEventListener('click', function () {
    gnbOffset = Math.max(gnbOffset - gnbStep, 0);        // 0보다 작아지지 않게
    gnbApply();
  });

  // 창 크기가 바뀌면 다시 계산
  window.addEventListener('resize', gnbMeasure);

  gnbMeasure();
  // 아이콘 이미지가 늦게 떠서 길이가 달라지는 경우를 대비해 한 번 더
  window.addEventListener('load', gnbMeasure);
}


/* ---------------------------------------------------------
   3. 맨 위로 버튼  ✅ 완성 (2026-08-05)

   스크롤을 300px 넘게 내리면 나타나고, 누르면 맨 위로 올라감.

   ※ "JS는 나중에 몰아서" 하기로 했지만 이건 예외로 지금 만들었음.
     버튼이 기본적으로 숨겨져 있어서(opacity:0) JS가 없으면
     영영 안 보이는 = 아무 쓸모 없는 버튼이 되기 때문.

   ※ 이 코드는 fetch() 를 안 써서 index.html 을 더블클릭해도 잘 돌아감.
     (더블클릭이 막히는 건 나중에 상품목록에서 fetch 를 쓸 때부터)
   --------------------------------------------------------- */

const gotoTop = document.querySelector('#goto-top');

// 요소가 없을 때 아래 코드가 에러를 내면서 멈추면
// 이 파일의 다른 기능까지 같이 죽음. 그래서 있는지 먼저 확인함
if (gotoTop) {

  // scroll 이벤트는 스크롤하는 내내 아주 자주 불림.
  // 여기서는 클래스만 붙였다 떼는 가벼운 일이라 그냥 둬도 괜찮음
  window.addEventListener('scroll', function () {
    // window.scrollY = 지금 위에서 몇 px 내려왔는지
    if (window.scrollY > 300) {
      gotoTop.classList.add('is-on');
    } else {
      gotoTop.classList.remove('is-on');
    }
  });

  gotoTop.addEventListener('click', function () {
    // behavior:'smooth' = 뚝 끊기지 않고 스르륵 올라감
    window.scrollTo({ top: 0, behavior: 'smooth' });

    /* ★ 안전장치 (2026-08-08 추가)
       일부 환경에서는 smooth 스크롤이 **아예 동작하지 않음** (실제로 이 프로젝트를
       검증하던 브라우저가 그랬음 — behavior:'auto' 는 되는데 'smooth' 만 무시됨).
       그러면 버튼을 눌러도 화면이 그대로라 "고장난 버튼"처럼 보임.

       그래서 0.6초 뒤에도 여전히 위로 안 갔으면 즉시 이동으로 대신함.
       smooth 가 잘 되는 환경에서는 그 사이에 이미 0 에 도착해 있으므로 아무 일도 안 일어남 */
    setTimeout(function () {
      if (window.scrollY > 0) {
        window.scrollTo(0, 0);
      }
    }, 600);
  });
}


/* ---------------------------------------------------------
   4. 검색 자동완성 / 최근검색어 (2026-08-08)

   원본 구조를 그대로 씀 (2c80969368e44df3.css 977~1100줄):
     .header-search
       ├─ input.search-keyword
       └─ .headerPopupWords.popularity-words   ← 이 상자가 아래로 펼쳐짐
            ├─ .autocomplete_wrap              ← 목록이 들어갈 자리
            └─ .history-btns                   ← 전체삭제 / 최근검색어끄기

   상태 2가지 (원본과 동일):
     ① 기본        — 최근 검색어 목록. 아래 회색 버튼줄 보임
     ② .auto-search — 글자를 치면 추천어 목록. 버튼줄 숨김

   ★ 검색폼이 PC용 / 태블릿용 두 벌이라 forEach 로 둘 다 처리함.
     최근검색어는 localStorage 에 저장 — 새로고침해도 남아있음
   --------------------------------------------------------- */

/* 추천어 재료 (원본은 서버에서 받아옴. 나중에 JSP/DB 로 바꿀 자리) */
const SUGGEST_WORDS = [
  '노트북', '노트북 거치대', '노트북 파우치', '무선마우스', '무선이어폰', '모니터', '모니터암',
  '生수', '생수 2L', '커피', '커피머신', '캡슐커피', '키보드', '기계식 키보드',
  '선풍기', '서큘레이터', '제습기', '가습기', '공기청정기',
  '운동화', '슬리퍼', '반팔티', '반바지', '원피스', '가방', '백팩',
  '마스크팩', '선크림', '클렌징폼', '샴푸', '바디워시',
  '강아지 사료', '고양이 모래', '물티슈', '휴지', '세제', '섬유유연제'
];

const HISTORY_KEY = 'coupang-clone-search-history';
const HISTORY_OFF_KEY = 'coupang-clone-search-history-off';

/* localStorage 는 문자열만 저장할 수 있어서 JSON 으로 바꿔 넣고 뺌.
   (사생활 보호 모드 등에서 막힐 수 있어 try 로 감쌈) */
function loadHistory() {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY)) || [];
  } catch (e) {
    return [];
  }
}

function saveHistory(list) {
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(list));
  } catch (e) { /* 저장이 막혀도 화면 동작은 계속되게 무시 */ }
}

function isHistoryOff() {
  try {
    return localStorage.getItem(HISTORY_OFF_KEY) === '1';
  } catch (e) {
    return false;
  }
}

function setHistoryOff(off) {
  try {
    localStorage.setItem(HISTORY_OFF_KEY, off ? '1' : '0');
  } catch (e) { /* 무시 */ }
}

/** 검색폼 하나에 자동완성을 붙임 */
function setupSearchAutocomplete(box) {
  const input = box.querySelector('.search-keyword');
  const popup = box.querySelector('.popularity-words');
  if (!input || !popup) return;

  const wrap = popup.querySelector('.autocomplete_wrap');
  const delAll = popup.querySelector('.delete-all-kwdhistory');
  const onOff = popup.querySelector('.history-on-off');

  /* --- 최근 검색어 목록 그리기 --- */
  function renderHistory() {
    popup.classList.remove('auto-search');   // 버튼줄이 보이는 기본 상태

    if (isHistoryOff()) {
      wrap.innerHTML = '<p class="history-off-msg" style="display:block">최근 검색어 저장 기능이 꺼져 있습니다.</p>';
      onOff.textContent = '최근검색어켜기';
      return;
    }
    onOff.textContent = '최근검색어끄기';

    const list = loadHistory();
    if (list.length === 0) {
      wrap.innerHTML = '<h3>최근 검색어</h3>'
        + '<p class="history-off-msg" style="display:block">최근 검색어가 없습니다.</p>';
      return;
    }

    /* ol > li > (a.kwd + span.delete-kwdhistory) 구조는 원본 그대로 */
    let html = '<h3>최근 검색어</h3><ol>';
    list.forEach(function (word, i) {
      html += '<li>'
        + '<a href="#" class="kwd">' + escapeHtml(word) + '</a>'
        + '<span class="delete-kwdhistory" data-index="' + i + '" title="삭제">✕</span>'
        + '</li>';
    });
    html += '</ol>';
    wrap.innerHTML = html;
  }

  /* --- 추천어 목록 그리기 (글자를 쳤을 때) --- */
  function renderSuggest(keyword) {
    popup.classList.add('auto-search');   // 버튼줄 숨김 + 여백 조정

    const kw = keyword.trim().toLowerCase();
    const hits = SUGGEST_WORDS.filter(function (w) {
      return w.toLowerCase().indexOf(kw) !== -1;
    }).slice(0, 10);

    if (hits.length === 0) {
      wrap.innerHTML = '<p class="history-off-msg" style="display:block">추천 검색어가 없습니다.</p>';
      return;
    }

    /* 입력한 글자와 겹치는 부분만 <strong> 로 감싸 파랗게 (원본 방식) */
    wrap.innerHTML = hits.map(function (w) {
      const at = w.toLowerCase().indexOf(kw);
      const marked = escapeHtml(w.slice(0, at))
        + '<strong>' + escapeHtml(w.slice(at, at + kw.length)) + '</strong>'
        + escapeHtml(w.slice(at + kw.length));
      return '<a href="#">' + marked + '</a>';
    }).join('');
  }

  /* 사용자가 친 글자가 그대로 HTML 로 들어가지 않게 막음.
     (예: <b> 를 치면 진짜 태그가 되어버리는 걸 방지 — XSS 라고 부름) */
  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  function open() {
    if (input.value.trim() === '') renderHistory();
    else renderSuggest(input.value);
    popup.classList.add('is-open');
  }

  function close() {
    popup.classList.remove('is-open');
  }

  /* --- 이벤트 --- */
  input.addEventListener('focus', open);

  input.addEventListener('input', function () {
    open();   // 글자가 있으면 추천어, 지우면 다시 최근검색어
  });

  /* 검색을 실행하면 최근검색어에 추가 */
  const form = box.closest('form');
  if (form) {
    form.addEventListener('submit', function (e) {
      const word = input.value.trim();

      if (!word) {
        e.preventDefault();   // 빈 검색어로는 이동하지 않음
        return;
      }

      if (!isHistoryOff()) {
        let list = loadHistory();
        list = list.filter(function (w) { return w !== word; });   // 중복 제거
        list.unshift(word);                                        // 맨 앞에 넣기
        saveHistory(list.slice(0, 10));                            // 최대 10개
      }
      close();
      // preventDefault 하지 않음 — /search?keyword=... 로 실제 이동
    });
  }

  /* 목록 안 클릭 처리 — 항목이 JS 로 계속 새로 그려지므로
     각 항목에 직접 거는 대신 부모(wrap)에 한 번만 검 (이벤트 위임) */
  wrap.addEventListener('click', function (e) {
    const del = e.target.closest('.delete-kwdhistory');
    if (del) {
      e.preventDefault();
      const list = loadHistory();
      list.splice(Number(del.dataset.index), 1);
      saveHistory(list);
      renderHistory();
      return;
    }

    const link = e.target.closest('a');
    if (link) {
      e.preventDefault();
      input.value = link.textContent;
      close();
    }
  });

  if (delAll) {
    delAll.addEventListener('click', function () {
      saveHistory([]);
      renderHistory();
    });
  }

  if (onOff) {
    onOff.addEventListener('click', function () {
      setHistoryOff(!isHistoryOff());
      renderHistory();
    });
  }

  /* 바깥을 클릭하면 닫기.
     ⚠ mousedown 을 쓰는 이유: click 은 목록 항목을 누를 때
       "닫기" 가 먼저 실행돼서 항목 클릭이 씹힘 */
  document.addEventListener('mousedown', function (e) {
    if (!box.contains(e.target)) close();
  });

  input.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') close();
  });
}

document.querySelectorAll('.header-search').forEach(setupSearchAutocomplete);
