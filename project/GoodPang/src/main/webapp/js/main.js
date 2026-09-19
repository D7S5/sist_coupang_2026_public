/* =========================================================
   main.js — 메인페이지 본문 동작

   ★ 제일 중요한 개념:
   쿠팡은 React로 상품 카드를 찍어내는데,
   우리는 "데이터 배열 + 반복문 + innerHTML" 로 똑같이 할 수 있음.
   ========================================================= */


/* ---------------------------------------------------------
   1. 히어로 배너 자동 슬라이드  ✅ 완성 (2026-08-05)

   ★ 핵심: "움직이는" 게 아니라 6장을 같은 자리에 겹쳐두고
     그중 1장만 보이게 하는 것. JS는 클래스만 갈아끼움.

   ⚠ 동시에 3가지를 맞춰야 함. 하나라도 빠지면 화면이 어긋남:
       ① .main-today__bg          → .is-on          (배경 그림)
       ② .today-image             → .is-on          (클릭용 투명 링크)
       ③ .todayshot-right-thumbnail → .today-selected (썸네일 파란 테두리)

   ※ fetch 를 안 쓰므로 index.html 을 더블클릭해도 잘 돌아감
   --------------------------------------------------------- */

const heroBgs = document.querySelectorAll('.main-today__bg');
const heroLinks = document.querySelectorAll('.today-image');
const heroThumbs = document.querySelectorAll('.todayshot-right-thumbnail');
const heroSection = document.querySelector('.main-today');

if (heroBgs.length > 0) {

  let heroIndex = 0;        // 지금 몇 번째를 보여주는 중인지 (0부터 시작)
  let heroTimer = null;     // 자동 넘김 타이머를 담아둘 곳
  const HERO_DELAY = 4000;  // 4초마다 넘김

  /* i 번째 배너를 보여줌 */
  function heroShow(i) {
    /* % 는 나머지 연산. 6장일 때 6 % 6 = 0 이라 마지막 다음은 처음으로 돌아감.
       앞에 heroBgs.length 를 더해두면 -1 이 들어와도 5로 바뀜(거꾸로 갈 때 대비) */
    heroIndex = (i + heroBgs.length) % heroBgs.length;

    /* toggle(클래스, 조건) = 조건이 true면 붙이고 false면 뗌.
       add/remove 를 따로 쓰면 "이전 걸 떼는" 코드를 빠뜨리기 쉬운데
       이렇게 하면 전부 훑으면서 한 번에 정리돼서 안 어긋남 */
    heroBgs.forEach(function (el, n) { el.classList.toggle('is-on', n === heroIndex); });
    heroLinks.forEach(function (el, n) { el.classList.toggle('is-on', n === heroIndex); });
    heroThumbs.forEach(function (el, n) { el.classList.toggle('today-selected', n === heroIndex); });
  }

  function heroStart() {
    heroStop();   // 타이머가 겹쳐서 두 개가 도는 걸 막음
    heroTimer = setInterval(function () { heroShow(heroIndex + 1); }, HERO_DELAY);
  }

  function heroStop() {
    clearInterval(heroTimer);
  }

  /* 썸네일에 마우스만 올려도 바뀜 (원본과 같은 방식). 클릭도 됨 */
  heroThumbs.forEach(function (thumb, n) {
    thumb.addEventListener('mouseenter', function () { heroShow(n); });

    const link = thumb.querySelector('a');
    if (link) {
      link.addEventListener('click', function (e) {
        // href="#" 라서 그냥 두면 페이지 맨 위로 튐. 그걸 막음
        e.preventDefault();
        heroShow(n);
      });
    }
  });

  /* 배너 위에 마우스를 올려둔 동안은 자동 넘김을 멈춤.
     보는 중에 휙 바뀌면 불편하니까 */
  if (heroSection) {
    heroSection.addEventListener('mouseenter', heroStop);
    heroSection.addEventListener('mouseleave', heroStart);
  }

  heroShow(0);   // 시작 상태를 HTML이 아니라 JS가 정하도록 한 번 맞춰줌
  heroStart();
}


/* ---------------------------------------------------------
   2. 상품 카드 찍어내기  ★ 핵심
   data/products.json 의 데이터를 읽어서 HTML로 만들어 넣음
   --------------------------------------------------------- */
// TODO
//
// fetch('data/products.json')
//   .then(res => res.json())
//   .then(products => {
//     const list = document.querySelector('.prod-list');
//
//     // 상품 하나당 <li> 하나씩 만들기
//     const html = products.map(function (item) {
//       return `
//         <li>
//           <a href="#" class="product-unit">
//             <img src="${item.image}" alt="${item.name}">
//             <span class="name">${item.name}</span>
//             <div class="price">
//               <strong>${item.price.toLocaleString()}원</strong>
//             </div>
//           </a>
//         </li>
//       `;
//     }).join('');
//
//     list.innerHTML = html;
//   });
//
// 💡 item.price.toLocaleString() → 8200 을 "8,200" 으로 바꿔줌 (쉼표 자동)
// 💡 `${...}` 는 백틱(``) 안에서만 되는 문법. 따옴표 아님! 키보드 1 왼쪽 키


/* ---------------------------------------------------------
   3. HOT! TREND 상품목록 캐러셀 (2026-08-06)

   쿠팡 원본과 같은 방식: 상품을 밀어서 스크롤하는 게 아니라
   **한 페이지(6개)만 켜고 나머지는 꺼둠.**
     CSS  .prod-list li { display: none }
     JS   현재 페이지 6개에만 .is-on 을 붙임

   HTML 구조 (유닛마다 하나씩):
     .best-product-list
       ├─ a.move.preview   ← 이전
       ├─ a.move.next      ← 다음
       ├─ ul.prod-list     ← 상품 li 들
       └─ ol.dot-navi      ← 페이지 동그라미 (JS 가 개수에 맞춰 만듦)

   ★ 상품이 몇 개든 알아서 동작함. 나중에 JSP 로 상품을 늘려도 코드 수정 불필요
   --------------------------------------------------------- */

/** 상품목록 하나를 캐러셀로 만들어줌 */
function setupProductCarousel(box) {
  const PER_PAGE = 6;                                   // 한 페이지에 6개 (3열 x 2줄)
  const items = Array.from(box.querySelectorAll('.prod-list > li'));
  if (items.length === 0) return;

  const pageCount = Math.ceil(items.length / PER_PAGE);
  const prevBtn = box.querySelector('.move.preview');
  const nextBtn = box.querySelector('.move.next');
  const dotNavi = box.querySelector('.dot-navi');

  let page = 0;

  /* 페이지가 1개뿐이면 화살표·동그라미가 필요 없음 */
  if (pageCount <= 1) {
    if (prevBtn) prevBtn.style.display = 'none';
    if (nextBtn) nextBtn.style.display = 'none';
    if (dotNavi) dotNavi.style.display = 'none';
    items.forEach(li => li.classList.add('is-on'));
    return;
  }

  /* 동그라미를 페이지 수만큼 만듦 (HTML 에 직접 안 적어도 되게) */
  if (dotNavi) {
    dotNavi.innerHTML = '';
    for (let i = 0; i < pageCount; i++) {
      const li = document.createElement('li');
      const a = document.createElement('a');
      a.href = '#';
      a.title = (i + 1) + '번째 페이지';
      a.textContent = String(i + 1);
      li.appendChild(a);

      // 클릭하면 그 페이지로. (i 를 그대로 쓰려고 let 으로 선언한 것)
      li.addEventListener('click', function (e) {
        e.preventDefault();
        show(i);
      });
      dotNavi.appendChild(li);
    }
  }

  /** n 번째 페이지를 화면에 표시 */
  function show(n) {
    page = Math.max(0, Math.min(n, pageCount - 1));   // 범위를 벗어나지 않게

    items.forEach(function (li, idx) {
      // idx 가 이번 페이지 범위 안이면 켜고, 아니면 끔
      const inPage = Math.floor(idx / PER_PAGE) === page;
      li.classList.toggle('is-on', inPage);
    });

    if (dotNavi) {
      Array.from(dotNavi.children).forEach(function (li, idx) {
        li.classList.toggle('selected', idx === page);
      });
    }

    // 맨 앞/맨 뒤에서는 해당 화살표를 감춤
    if (prevBtn) prevBtn.classList.toggle('is-off', page === 0);
    if (nextBtn) nextBtn.classList.toggle('is-off', page === pageCount - 1);
  }

  if (prevBtn) prevBtn.addEventListener('click', function (e) { e.preventDefault(); show(page - 1); });
  if (nextBtn) nextBtn.addEventListener('click', function (e) { e.preventDefault(); show(page + 1); });

  show(0);   // 처음엔 1페이지
}

/* 페이지 안의 모든 상품목록에 적용 */
document.querySelectorAll('.best-product-list').forEach(setupProductCarousel);


/* ---------------------------------------------------------
   4. HOT! TREND 세로 광고배너 캐러셀 (2026-08-06)

   상품목록과 다른 점: **몇 초마다 저절로 넘어감.**
   원본은 Swiper 라이브러리로 가로 슬라이드하지만,
   우리는 배너를 겹쳐두고 .is-on 한 장만 켜는 방식 (히어로 배너와 같음).

   마우스를 올리면 자동넘김이 멈춤 — 읽는 중에 바뀌면 불편하니까.
   (히어로 배너에도 같은 처리를 해뒀음)
   --------------------------------------------------------- */
function setupPromotionCarousel(box) {
  const SLIDE_MS = 4000;                                 // 4초마다 다음 장
  const slides = Array.from(box.querySelectorAll('.promotion-link'));
  const dotNavi = box.querySelector('.dot-navi');
  const prevBtn = box.querySelector('.move.preview');
  const nextBtn = box.querySelector('.move.next');

  if (slides.length === 0) return;

  /* 한 장뿐이면 넘길 게 없음 */
  if (slides.length === 1) {
    slides[0].classList.add('is-on');
    if (prevBtn) prevBtn.style.display = 'none';
    if (nextBtn) nextBtn.style.display = 'none';
    if (dotNavi) dotNavi.style.display = 'none';
    return;
  }

  let idx = 0;
  let timer = null;

  if (dotNavi) {
    dotNavi.innerHTML = '';
    slides.forEach(function (_, i) {
      const li = document.createElement('li');
      const a = document.createElement('a');
      a.href = '#';
      a.title = (i + 1) + '번째 배너';
      a.textContent = String(i + 1);
      li.appendChild(a);
      li.addEventListener('click', function (e) { e.preventDefault(); go(i); });
      dotNavi.appendChild(li);
    });
  }

  function go(n) {
    // (n + 길이) % 길이 → 끝에서 다음을 누르면 처음으로 돌아감 (상품목록과 다른 점)
    idx = (n + slides.length) % slides.length;
    slides.forEach(function (s, i) { s.classList.toggle('is-on', i === idx); });
    if (dotNavi) {
      Array.from(dotNavi.children).forEach(function (li, i) {
        li.classList.toggle('selected', i === idx);
      });
    }
  }

  function start() { stop(); timer = setInterval(function () { go(idx + 1); }, SLIDE_MS); }
  function stop()  { if (timer) { clearInterval(timer); timer = null; } }

  if (prevBtn) prevBtn.addEventListener('click', function (e) { e.preventDefault(); go(idx - 1); start(); });
  if (nextBtn) nextBtn.addEventListener('click', function (e) { e.preventDefault(); go(idx + 1); start(); });

  // 마우스를 올리면 멈추고, 벗어나면 다시 돌아감
  box.addEventListener('mouseenter', stop);
  box.addEventListener('mouseleave', start);

  go(0);
  start();
}

document.querySelectorAll('.promotion').forEach(setupPromotionCarousel);


/* ---------------------------------------------------------
   5. 개인화 광고 캐러셀 4개 (#personalizedGW) + 띠배너 (2026-08-07)

   상품목록 캐러셀(setupProductCarousel)과 원리는 같은데
   한 페이지에 5개(6개 아님)이고, 동그라미 대신 "1/3" 같은 숫자 표시를 쓰는
   캐러셀도 있어서 옵션을 받는 버전으로 새로 만듦. (기존 함수를 고치면
   이미 검증된 HOT!TREND 상품목록이 흔들릴 수 있어서 손 안 댐) --------------------------------------------------------- */
/** 화면 폭에 따라 "한 줄에 몇 개 보일지" 를 정함 (원본 값 그대로)
    원본 2c654b8dcffb86bf.css 의 `.carousel-item` width:
      ≥1500 → 20%(5개) / 769~1499 → 25%(4개) / 601~768 → 33.3%(3개) / ≤600 → 40%(2.5개)
    ⚠ 마지막 구간은 2.5개라 딱 안 떨어짐 → 2개로 끊고 나머지는 다음 페이지로 넘김 */
function adPerPage() {
  const w = window.innerWidth;
  if (w >= 1500) return 5;
  if (w >= 769) return 4;
  if (w >= 601) return 3;
  return 2;
}

function setupAdCarousel(box) {
  const items = Array.from(box.querySelectorAll('.ad-list > li'));
  if (items.length === 0) return;

  const prevBtn = box.querySelector('.move.preview');
  const nextBtn = box.querySelector('.move.next');
  const pageEl = box.querySelector('.ad-page');   // "1/3" 표시 (없는 캐러셀도 있음)

  let perPage = adPerPage();
  let pageCount = Math.ceil(items.length / perPage);
  let page = 0;

  function show(n) {
    page = Math.max(0, Math.min(n, pageCount - 1));

    items.forEach(function (li, idx) {
      li.classList.toggle('is-on', Math.floor(idx / perPage) === page);
    });

    if (pageEl) pageEl.innerHTML = (page + 1) + '<span>/' + pageCount + '</span>';

    // 페이지가 1개뿐이면 화살표·숫자표시가 필요 없음
    const single = pageCount <= 1;
    if (prevBtn) {
      prevBtn.style.display = single ? 'none' : '';
      prevBtn.classList.toggle('is-off', page === 0);
    }
    if (nextBtn) {
      nextBtn.style.display = single ? 'none' : '';
      nextBtn.classList.toggle('is-off', page === pageCount - 1);
    }
    if (pageEl) pageEl.style.display = single ? 'none' : '';
  }

  /* 화면 폭이 바뀌면 한 줄 개수도 바뀌므로 다시 계산.
     지금 보고 있던 첫 상품이 계속 보이게 페이지를 맞춰줌 (안 그러면 엉뚱한 데로 튐) */
  function relayout() {
    const next = adPerPage();
    if (next === perPage) return;
    const firstVisible = page * perPage;      // 바뀌기 전 첫 상품 번호
    perPage = next;
    pageCount = Math.ceil(items.length / perPage);
    show(Math.floor(firstVisible / perPage));
  }

  if (prevBtn) prevBtn.addEventListener('click', function (e) { e.preventDefault(); show(page - 1); });
  if (nextBtn) nextBtn.addEventListener('click', function (e) { e.preventDefault(); show(page + 1); });

  window.addEventListener('resize', relayout);

  show(0);
}

document.querySelectorAll('.ad-carousel').forEach(setupAdCarousel);


/* --- 띠배너 (.gw-line-banners) — 세로배너 캐러셀(setupPromotionCarousel)과 같은
   "여러 장 겹쳐두고 .is-on 한 장만" 방식. 4초 자동회전 + hover 정지도 그대로 --------------------------------------------------------- */
function setupLineBanners(box) {
  const SLIDE_MS = 4000;
  const slides = Array.from(box.querySelectorAll('.line-banner'));
  const prevBtn = box.querySelector('.move.preview');
  const nextBtn = box.querySelector('.move.next');
  if (slides.length === 0) return;

  if (slides.length === 1) {
    slides[0].classList.add('is-on');
    if (prevBtn) prevBtn.style.display = 'none';
    if (nextBtn) nextBtn.style.display = 'none';
    return;
  }

  let idx = 0;
  let timer = null;

  function go(n) {
    idx = (n + slides.length) % slides.length;
    slides.forEach(function (s, i) { s.classList.toggle('is-on', i === idx); });
  }
  function start() { stop(); timer = setInterval(function () { go(idx + 1); }, SLIDE_MS); }
  function stop() { if (timer) { clearInterval(timer); timer = null; } }

  if (prevBtn) prevBtn.addEventListener('click', function (e) { e.preventDefault(); go(idx - 1); start(); });
  if (nextBtn) nextBtn.addEventListener('click', function (e) { e.preventDefault(); go(idx + 1); start(); });

  box.addEventListener('mouseenter', stop);
  box.addEventListener('mouseleave', start);

  go(0);
  start();
}

document.querySelectorAll('.gw-line-banners').forEach(setupLineBanners);


/* ---------------------------------------------------------
   세로 아이콘 레일 (.category-menu)

     1) 아이콘을 클릭하면 그 카테고리로 부드럽게 스크롤
     2) 스크롤할 때마다 "지금 화면에 보이는 카테고리"에 색을 넣어줌
        (아이콘에 .on, 레일 위쪽 선 색도 그 카테고리 색으로)

   위치를 따라다니게 하는 건 JS 가 아니라 CSS 의 position:sticky 가 함.
   (원본은 JS 로 top 을 계산하지만 sticky 로 하면 같은 결과가 나옴)
   --------------------------------------------------------- */
function setupCategoryRail() {
  var rail = document.querySelector('.category-menu');
  if (!rail) return;

  var anchors = Array.prototype.slice.call(rail.querySelectorAll('.category-anchor'));
  if (!anchors.length) return;

  // 아이콘 ↔ 카테고리 유닛 짝짓기. href="#cat-beauty" 로 찾아감
  var items = [];
  anchors.forEach(function (a) {
    var unit = document.querySelector(a.getAttribute('href'));
    if (unit) items.push({ anchor: a, unit: unit });
  });
  if (!items.length) return;

  // 지금 몇 번째가 켜져 있는지 기억해두고, 바뀔 때만 DOM 을 건드림
  // (스크롤은 1초에도 수십 번 불리기 때문에 매번 고치면 느려짐)
  var current = -1;

  function setActive(i) {
    if (i === current) return;
    current = i;

    items.forEach(function (it, n) {
      it.anchor.classList.toggle('on', n === i);
    });

    // 레일 위쪽 선 색 = 지금 카테고리 색.
    // 카테고리 이름이 곧 클래스명이라(.beauty 등) 그걸 그대로 갈아끼움
    items.forEach(function (it) {
      rail.classList.remove(it.unit.id.replace('cat-', ''));
    });
    rail.classList.add(items[i].unit.id.replace('cat-', ''));
  }

  function update() {
    // "화면 위쪽에서 조금 내려온 지점"을 기준선으로 잡고,
    // 그 선을 지난 유닛 중 가장 마지막 것을 현재 카테고리로 봄
    var line = window.innerHeight * 0.3;
    var found = 0;

    for (var i = 0; i < items.length; i++) {
      var top = items[i].unit.getBoundingClientRect().top;
      if (top <= line) found = i;
      else break;
    }
    setActive(found);
  }

  // 클릭하면 그 카테고리로 이동.
  // ⚠ 이 브라우저에서 behavior:'smooth' 가 무시되는 경우가 있어서
  //   맨위로 버튼(header.js)과 같은 방식으로 안전장치를 둠
  items.forEach(function (it, i) {
    it.anchor.addEventListener('click', function (e) {
      e.preventDefault();

      // 헤더에 가리지 않도록 위쪽에 20px 여유를 둠
      var y = it.unit.getBoundingClientRect().top + window.scrollY - 20;

      window.scrollTo({ top: y, behavior: 'smooth' });
      setTimeout(function () {
        if (Math.abs(window.scrollY - y) > 5) window.scrollTo(0, y);
      }, 600);

      setActive(i);
    });
  });

  // 스크롤할 때마다 다시 계산.
  // 재는 게 유닛 5개뿐이라 가벼워서 따로 속도 제한(throttle)은 안 걸었음.
  // (requestAnimationFrame 으로 묶는 방법도 있는데, 그러면 화면이 안 그려지는
  //  상황에서 아예 안 돌아서 검증이 어려움 — 실제로 iframe 테스트에서 걸림)
  window.addEventListener('scroll', update);
  window.addEventListener('resize', update);

  update();
}

setupCategoryRail();
