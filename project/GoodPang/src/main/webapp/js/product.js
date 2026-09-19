/* ============================================================
   product.js — 상품 상세 페이지 전용 JS

   지금 들어있는 것 2개뿐:
     1. 수량 + / - 버튼
     2. 썸네일을 누르면 큰 이미지가 바뀌는 것

     header.js 는 헤더/맨위로 버튼용이라 이 페이지에도 그대로 필요함.
   ============================================================ */


/* ── 1. 수량 + / - (+ 가격 연동) ──────────────────
   최소 1개, 최대는 "지금 고른 옵션의 남은 재고"만큼.
   1일 때는 - 버튼을, 재고만큼 채웠으면 + 버튼을 disabled 로 막음 (CSS 가 회색으로 흐리게 보여줌)

   ★ 2026-08-28 — 원래 MAX 가 20 으로 박혀 있었는데(CLAUDE.md 에 "DB 연동할 때 고치기"로 적혀있던 것),
     이제 PRODUCT_OPTION.QUANTITY 가 옵션 JSON 에 같이 실려오므로 옵션마다 다른 재고를 쓸 수 있음.
     옵션을 바꾸면 setupOptionSelect() 가 setStock() 을 불러서 최대값을 갈아끼움.

   ★ 2026-08-21 추가 — 수량을 바꾸면 위 가격(.total-price)도 같이 바뀌게 함.
     원래 이름이 "total-price" 인데 실제로는 "단가"만 찍혀있어서 이름값을 못 했음.
     원본 쿠팡도 수량을 바꾸면 이 숫자가 "단가 × 수량" 으로 바뀜.

     단가는 .total-price 의 data-unit-price 에서 읽음 (예: "19900").
     화면 글자(예: "19,900원")를 다시 파싱하면 두 번째 클릭부터
     "39,800원" 을 단가로 잘못 읽는 사고가 나서, 원래 값을 data 속성에 따로 저장해둠.

     ★ 2026-08-30 확정 — PRODUCT_OPTION.PRICE/NORMAL_PRICE 는 PRODUCT_PRICE(기본가)에 더해지는 "추가금".
       옵션을 바꾸면 setupOptionSelect() 가 아래 setPrice() 를 불러서 판매가/정상가/할인율을 다시 그림.
       판매가 = basePrice + 옵션의 PRICE, 정상가 = basePrice + 옵션의 NORMAL_PRICE(없으면 할인 표시 안 함). */
function setupQuantity() {
  const box = document.querySelector('.product-quantity');
  if (!box) return;                     // 이 페이지에 수량박스가 없으면 아무것도 안 함

  const input = box.querySelector('.qty-input');
  const minus = box.querySelector('.qty-minus');
  const plus  = box.querySelector('.qty-plus');
  const priceEl = document.querySelector('.total-price');
  const discountEl = document.querySelector('.discount');
  const discountLabelEl = document.querySelector('.discount-label');   // "할인" 글자 — 2026-08-31 추가
  const originBox = document.querySelector('.price-origin');
  const originPriceEl = originBox ? originBox.querySelector('.origin-price') : null;
  // 재고보다 많이 입력했을 때 뜨는 말풍선 — 2026-09-03 추가 (원본 쿠팡 실측 결과 반영)
  const maxTip = document.getElementById('qtyMaxTip');
  let tipTimer = null;
  const basePrice = priceEl ? Number(priceEl.dataset.basePrice) || 0 : 0;
  let unitPrice = priceEl ? Number(priceEl.dataset.unitPrice) || 0 : 0;
  // 2026-09-01 추가 — 정상가(취소선)의 "1개당" 값. 할인 중이 아니면 null(취소선 자체를 안 보여줌).
  // render() 가 이 값 × 수량으로 취소선도 같이 다시 그림 — 예전엔 이 변수가 없어서 수량을 바꿔도
  // 취소선 금액이 1개 값 그대로 안 바뀌던 버그가 있었음 (사용자가 "수량 버튼 누르면 정상가는 금액이
  // 안 바뀐다"고 지적함)
  let normalUnitPrice = null;

  const MIN = 1;
  /* 옵션이 없는 상품(재고 정보가 안 내려오는 경우)을 위한 기본값.
     옵션이 있으면 페이지가 열릴 때 setStock() 이 진짜 재고로 덮어씀 */
  let max = 20;

  /* 화면에 숫자를 다시 그리고, 버튼을 켤지 끌지 정하는 함수.
     "값을 바꾸는 곳"과 "화면을 고치는 곳"을 한 군데로 모아두면
     나중에 고칠 때 여기만 보면 됨 */
  function render(n) {
    input.value = n;
    minus.disabled = (n <= MIN);
    plus.disabled  = (n >= max);

    /* 품절이면 가격을 안 건드림 — CSS 가 이미 회색으로 처리하고 있고,
       0원으로 다시 그리면 "품절인데 원가는 남아있는" 원본 화면과 달라짐 */
    if (priceEl && !document.body.classList.contains('is-soldout')) {
      priceEl.textContent = (unitPrice * n).toLocaleString('ko-KR') + '원';
    }
    // 취소선(정상가)도 판매가와 똑같이 수량만큼 곱해서 다시 그림 (할인 중일 때만 — normalUnitPrice
    // 가 null 이면 애초에 취소선 자체가 안 보이는 상태라 건드릴 필요 없음)
    if (originPriceEl && normalUnitPrice != null) {
      originPriceEl.textContent = (normalUnitPrice * n).toLocaleString('ko-KR') + '원';
    }
  }

  minus.addEventListener('click', function () {
    const n = Number(input.value) - 1;
    if (n >= MIN) render(n);
  });

  plus.addEventListener('click', function () {
    const n = Number(input.value) + 1;
    if (n <= max) render(n);
  });

  render(Number(input.value) || MIN);    // 페이지가 열릴 때 버튼 상태를 한 번 맞춰둠

  /* ★ 2026-09-02 추가 — 사용자가 숫자를 직접 타이핑해서 수량을 정할 수 있게 함.
     (그동안 readonly 라 +/- 버튼으로만 바꿀 수 있었음)

     타이핑 도중(input 이벤트)엔 숫자가 아닌 글자만 지우고, 범위 보정(1~재고)은 하지 않음 —
     "12"를 치려는데 "1"을 치는 순간 재고 부족으로 잘려버리면 뒷자리를 못 침.
     범위 보정과 가격 재계산은 입력을 다 마친 시점(포커스 아웃 or Enter)에만 한다. */
  input.addEventListener('input', function () {
    const digitsOnly = input.value.replace(/[^0-9]/g, '');
    input.value = digitsOnly;
  });

  // 재고보다 많이 입력했을 때 말풍선을 잠깐 보여줌 (2초 후 자동으로 사라짐 — 원본과 같은 방식)
  function showMaxTip() {
    if (!maxTip) return;
    maxTip.textContent = '선택 가능한 수량은 ' + max + '개 입니다.';
    maxTip.classList.add('show');
    clearTimeout(tipTimer);
    tipTimer = setTimeout(function () {
      maxTip.classList.remove('show');
    }, 2000);
  }

  function commit() {
    const n = Number(input.value) || MIN;      // 빈 값/0 은 MIN 으로
    if (n > max) showMaxTip();                 // 재고보다 많이 입력했을 때만 알림
    render(Math.min(Math.max(n, MIN), max));   // 1 ~ max(재고) 범위로 보정
  }

  input.addEventListener('blur', commit);
  input.addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
      e.preventDefault();   // 폼 안에 있어서 Enter 로 submit 되는 것 방지
      commit();
      input.blur();
    }
  });

  /* 옵션이 바뀔 때마다 setupOptionSelect() 가 이 함수를 불러줌.
     지금 담아둔 수량이 새 재고보다 많으면 재고만큼으로 줄임
     (예: 10개 담아뒀는데 재고 3개짜리 옵션으로 바꾸면 3으로) */
  function setStock(stock) {
    max = Math.max(stock, MIN);          // 재고 0 이어도 max 는 1 — 품절 처리는 updateFromSelects 가 따로 함
    const now = Number(input.value) || MIN;
    render(Math.min(now, max));
  }

  /* 옵션이 바뀔 때마다 setupOptionSelect() 가 그 옵션의 price/normalPrice 를 넘겨서 불러줌.
     판매가 = basePrice + optionPrice, 정상가 = basePrice + optionNormalPrice —
     ProductServlet 의 displayPrice/displayNormalPrice 계산과 똑같은 공식. */
  function setPrice(optionPrice, optionNormalPrice) {
    unitPrice = basePrice + (optionPrice || 0);

    const normalPrice = (optionNormalPrice != null) ? basePrice + optionNormalPrice : null;
    const hasDiscount = normalPrice != null && normalPrice > unitPrice;

    if (discountEl) {
      discountEl.style.display = hasDiscount ? '' : 'none';
      if (hasDiscount) {
        discountEl.textContent = Math.round((1 - unitPrice / normalPrice) * 100) + '%';
      }
    }
    if (discountLabelEl) discountLabelEl.style.display = hasDiscount ? '' : 'none';
    if (originBox) originBox.style.display = hasDiscount ? '' : 'none';
    // originPriceEl 의 글자는 여기서 직접 안 쓰고 normalUnitPrice 만 갱신함 —
    // 실제 텍스트(× 수량)는 바로 아래 render() 가 그림 (수량 곱하는 로직을 두 군데 두면
    // 하나만 고치고 하나는 깜빡하는 사고가 나서 한 곳으로 모음)
    normalUnitPrice = hasDiscount ? normalPrice : null;

    render(Number(input.value) || MIN);   // 단가가 바뀌었으니 화면 가격도 다시 그림
  }

  return { setStock: setStock, setPrice: setPrice };
}


/* ── 2. 썸네일 → 큰 이미지 ───────────────────────
   썸네일을 누르면 ① 파란 테두리가 그쪽으로 옮겨가고 ② 큰 이미지가 그 사진으로 바뀜.

   2026-08-28: li 하나하나에 클릭을 붙이는 대신 목록(ul) 하나에만 붙이는 방식(이벤트 위임)으로 바꿈 —
   옵션 드롭박스(사이즈/색상 등)를 바꾸면 setupOptionSelect() 가 이 목록의 li 들을 통째로 새로 그리는데,
   li 마다 붙여둔 클릭은 새로 그려지면 사라지지만 ul 에 붙여둔 클릭은 안 사라지기 때문. */
function setupThumbs() {
  const list = document.querySelector('.product-image__thumbs');
  if (!list) return;

  list.addEventListener('click', function (e) {
    const li = e.target.closest('li');
    if (!li || !list.contains(li)) return;

    e.preventDefault();                 // <a href="#"> 때문에 맨 위로 튀는 것 방지

    /* 파란 테두리(.is-on)를 전부 떼고, 누른 것에만 다시 붙임.
       "하나만 켜기"는 전부 끄고 → 하나 켜기 순서가 제일 간단함 */
    list.querySelectorAll('li').forEach(function (other) {
      other.classList.remove('is-on');
    });
    li.classList.add('is-on');

    /* 누른 썸네일의 사진을 큰 이미지 자리에 그대로 넣음.
       썸네일과 큰 이미지가 같은 파일이라 새로 받아올 게 없어 즉시 바뀜 */
    const main = document.querySelector('.product-image__main img');
    const picked = li.querySelector('img');
    if (main && picked) main.src = picked.src;
  });
}


/* ── 2.5 대표 이미지 확대(마우스 오버 돋보기) ─────────────
   (2, 3 사이에 끼워넣은 기능이라 번호를 2.5 로 붙임 — 아래 5.5/6.3/6.5 와 같은 방식,
   전체를 다시 번호 매기면 다른 곳 참조 주석까지 다 밀려서 여기서도 그 관례를 따름)
   2026-09-01 추가. 로그인해서 실제 쿠팡을 Playwright 로 대표 이미지에 마우스를 직접 올려서
   실측함(뷰포트 1280 / 1536 / 1920 세 군데). 확인한 것:
     · 뷰포트 1280px 미만이면 이 기능 자체가 원본 DOM 에 없음(숨기는 게 아니라 아예 안 만듦) —
       본문이 좁아지면 오른쪽에 확대사진 놓을 자리가 없어져서인 듯. 우리도 같은 폭 기준으로 막음
     · 렌즈(마우스 따라다니는 반투명 박스) 222×218px / 확대판(오른쪽에 뜨는 확대 사진) 476×466px —
       뷰포트가 바뀌어서 대표 이미지 자체 크기(439px↔556px)가 달라져도 이 둘은 그대로임(고정 픽셀,
       이미지 폭에 비례하는 게 아니었음)
     · 확대판은 이미지 바로 오른쪽에 간격 없이(0px) 붙고, 위쪽 끝이 이미지와 나란함
     · 배경 확대율은 마우스 위치와 상관없이 항상 250% 고정
     · 확대판에 쓰는 사진은 화면에 보이는 썸네일용(492x492 등 축소본)이 아니라 별도의
       고해상도 원본 — 우리는 그런 별도 파일이 없어서 지금 큰 이미지에 쓰는 파일을 그대로 씀
       (원본 사진 자체가 작으면 확대했을 때 흐려질 수 있음 — 나중에 사진 커지면 자동으로 좋아짐)

   ★ 렌즈는 이미지 박스 테두리를 못 벗어나게 clamp — 마우스가 이미지 밖으로 나가도 렌즈는
     가장자리에 붙어서 멈춤 (원본도 이렇게 동작하는 걸 실측 확인) */
function setupImageZoom() {
  const box = document.querySelector('.product-image__main');
  if (!box) return;

  const LENS_W = 222, LENS_H = 218;
  const ZOOM = 2.5;   // = 250% (실측값, 렌즈/확대판 크기 비율로 계산한 값이 아니라 원본에 박혀있던 고정 배율)

  let lens = null, overlayImg = null;

  function build() {
    lens = document.createElement('div');
    lens.className = 'magnifier-lens';

    const overlay = document.createElement('div');
    overlay.className = 'magnify-overlay';
    overlayImg = document.createElement('div');
    overlayImg.className = 'magnify-overlay__img';
    overlay.appendChild(overlayImg);

    box.appendChild(lens);
    box.appendChild(overlay);
  }

  function remove() {
    box.querySelectorAll('.magnifier-lens, .magnify-overlay').forEach(function (el) { el.remove(); });
    lens = null;
    overlayImg = null;
  }

  function move(e) {
    if (!lens) return;
    const rect = box.getBoundingClientRect();
    let x = e.clientX - rect.left;
    let y = e.clientY - rect.top;

    // 렌즈 중심이 이미지 박스를 못 벗어나게 막음
    x = Math.max(LENS_W / 2, Math.min(rect.width  - LENS_W / 2, x));
    y = Math.max(LENS_H / 2, Math.min(rect.height - LENS_H / 2, y));

    const lensLeft = x - LENS_W / 2;
    const lensTop  = y - LENS_H / 2;
    lens.style.left = lensLeft + 'px';
    lens.style.top  = lensTop + 'px';

    // 렌즈가 가리키는 지점을 확대판에서 보여줌 — 배경이 250%로 커져있으니
    // 좌표에도 같은 배율을 곱해야 렌즈 위치랑 확대판 안 위치가 맞물림
    if (overlayImg) {
      overlayImg.style.backgroundPosition = (-lensLeft * ZOOM) + 'px ' + (-lensTop * ZOOM) + 'px';
    }
  }

  box.addEventListener('mouseenter', function (e) {
    if (window.innerWidth < 1280) return;   // 원본도 이 폭 밑에서는 기능 자체가 없음
    const img = box.querySelector('img');
    if (!img || !img.src) return;
    build();
    overlayImg.style.backgroundImage = 'url(' + img.src + ')';
    move(e);
  });
  box.addEventListener('mousemove', move);
  box.addEventListener('mouseleave', remove);
}


/* ── 3. 광고 캐러셀 "다음" 버튼 ──────────────────
   "함께 비교하면 좋을 상품"은 카드가 가로로 쭉 이어져 있고, 보이는 상자(.sdp-ads__body)가
   넘치는 부분을 잘라내고 있음. 버튼을 누르면 안쪽 목록(ul)을 왼쪽으로 밀어서 다음 카드를 보여줌.

   ★ scrollLeft 가 아니라 transform:translateX 를 쓴 이유
     스크롤막대를 만들지 않고 움직이려는 것. CSS 에 transition 을 걸어놔서 부드럽게 밀림. */
function setupAdSlider() {
  const body = document.querySelector('.sdp-ads__body');
  if (!body) return;

  const list = body.querySelector('.sdp-ads__list');
  const next = body.querySelector('.sdp-ads__next');
  const item = list.querySelector('.ad-item');
  if (!list || !next || !item) return;

  /* 카드 하나가 차지하는 가로 길이 = 카드 폭 + 오른쪽 여백.
     CSS 에 140/16 이라고 적혀 있지만 여기서 다시 계산함 —
     나중에 CSS 값을 바꿔도 JS 를 안 고쳐도 되게 하려고 */
  const style = getComputedStyle(item);
  const step = item.getBoundingClientRect().width + parseFloat(style.marginRight);

  let moved = 0;                       // 지금까지 밀어낸 거리(px)

  function render() {
    list.style.transform = 'translateX(' + (-moved) + 'px)';
    /* 더 밀 데가 없으면 버튼을 끔 */
    const max = list.scrollWidth - body.clientWidth;
    next.disabled = moved >= max - 1;
  }

  next.addEventListener('click', function () {
    const max = list.scrollWidth - body.clientWidth;
    /* 한 번에 "보이는 칸 수"만큼 밀되, 끝을 넘지 않게 */
    const page = Math.floor(body.clientWidth / step) * step;
    moved = Math.min(moved + page, max);
    render();
  });

  render();                            // 처음에 버튼 상태를 맞춰둠
}


/* ── 4. 옵션 고르기 ─────────────────────────────
   2026-08-28(3차): "1번 축은 드롭박스, 2번째 이후 축은 사진 있으면 칩(원본이 그랬음), 없으면 드롭박스".
   상품 29번(사이즈×색상)으로 확인해보니 ① 하나로 합친 드롭박스는 색상이 텍스트 속에 묻히고,
   ② 두 축을 그냥 다 드롭박스로 하면 원본에 있던 "색상 사진 칩" UI가 사라짐 — 그래서 이렇게 정리함.

   #productOptionsData 에 "조합 하나(OPTION_ID) = 객체 하나" 형태의 JSON 이 들어있음.
   ProductServlet 이 ProductOptionDTO 리스트를 Gson 으로 그대로 바꾼 것이라 이름이 DB 컬럼과 같음:
   {optionId:69, option1Type:'사이즈', option1Value:'M', option2Type:'색상', option2Value:'Black',
    images:[{imageUrl:'upload/5/....jpg', imagePurpose:'대표', ...}, ...]}

   ★ 칩 사진은 "그 값 하나"의 사진이 아니라 "조합 하나(OPTION_ID)"에 딸려있음 — 예를 들어
     색상 칩 하나에 보여줄 사진은 (지금 고른 사이즈 + 이 색상) 조합의 사진을 씀. 사이즈를 바꾸면
     색상 칩 사진도 그 사이즈 기준으로 다시 그림(refreshChipThumbnails)

   ★ 2026-08-31 — 이 JSON(combos)은 이제 "축·값 구조"와 칩 사진(고르기 전 미리보기)에만 씀.
     실제로 화면에 반영되는 가격/재고/큰 이미지는 옵션을 고를 때마다 fetchOptionDetail() 이
     /option 으로 ajax 요청해서 서버 값을 다시 받아옴 (로그인 회원등급별 할인 대비 — 상세 이유는
     fetchOptionDetail() 위 주석 참고) */
function setupOptionSelect(setStock, setPrice) {
  const dataEl = document.getElementById('productOptionsData');
  const box = document.getElementById('fashionOption');
  if (!dataEl || !box) return;

  const combos = JSON.parse(dataEl.textContent);
  if (!combos.length) return;

  // optionsJson을 만들 때(ProductServlet) 이미지 경로를 img:url과 같은 규칙으로 미리
  // 절대/상대 경로로 바꿔서 내려주므로, 여기서는 그대로 꺼내 쓰기만 하면 됨
  const contextPath = dataEl.dataset.contextPath || '';
  combos.forEach(function (c) {
    c.imageUrls = (c.images || []).map(function (img) {
      return img.imageUrl;
    });
  });

  // 이 상품이 실제로 쓰는 축 목록. option1 은 항상 있고, option2/option3 는 있을 때만
  const axes = [];
  [1, 2, 3].forEach(function (n) {
    if (combos[0]['option' + n + 'Type']) {
      axes.push({ key: 'option' + n + 'Value', type: combos[0]['option' + n + 'Type'] });
    }
  });

  // 이 상품에 사진이 하나라도 딸린 조합이 있는지 — 2번째 이후 축을 칩으로 할지 드롭박스로 할지 판단 기준
  const anyImages = combos.some(function (c) { return c.imageUrls.length; });

  /* 2026-09-01 추가 — 새로고침해도 옵션 선택 유지.
     주소(?optionId=)에 담겨온 조합을 찾아서, 아래에서 드롭박스/칩을 그릴 때 "처음부터 이 값"으로
     선택해둠. ProductServlet 도 같은 optionId 를 보고 mainOption(대표사진·가격)을 이미 그 옵션
     기준으로 렌더해놨으니, 여기서 화면(드롭박스·칩)만 맞춰주면 서버가 그린 것과 어긋나지 않음.
     주소에 optionId 가 없거나 이 상품 조합에 없는 값이면 그냥 null → 아래에서 예전처럼 첫 값 씀 */
  const urlOptionId = new URLSearchParams(location.search).get('optionId');
  const initialCombo = urlOptionId
    ? combos.find(function (c) { return String(c.optionId) === urlOptionId; })
    : null;

  const optionIdInput = document.getElementById('selectedOptionId');
  const colorInput    = document.getElementById('selectedColor');
  const thumbBox      = document.querySelector('.product-image__thumbs');
  const mainImg       = document.querySelector('.product-image__main img');

  const controls = [];   // { axisKey, getValue(), root, chipList(옵션) }

  // 지금 각 축에서 고른 값과 정확히 일치하는 조합(OPTION_ID)을 찾음
  function findPickedCombo() {
    return combos.find(function (c) {
      return controls.every(function (ctl) { return c[ctl.axisKey] === ctl.getValue(); });
    });
  }

  // 칩 축의 사진들을 "지금 다른 축에서 고른 값"기준으로 다시 그림 (사이즈 바꾸면 색상칩 사진도 갱신)
  function refreshChipThumbnails(chipControl) {
    chipControl.root.querySelectorAll('li').forEach(function (li) {
      const testCombo = combos.find(function (c) {
        if (c[chipControl.axisKey] !== li.dataset.value) return false;
        return controls.every(function (other) {
          return other === chipControl || c[other.axisKey] === other.getValue();
        });
      });
      const img = li.querySelector('img');
      if (img && testCombo && testCombo.imageUrls.length) img.src = testCombo.imageUrls[0];
    });
  }

  let optionFetchSeq = 0;   // 응답이 늦게 와서 이전 클릭 결과가 나중에 덮어쓰는 것 방지용 번호표

  function updateFromSelects() {
    controls.forEach(function (ctl) { if (ctl.chipList) refreshChipThumbnails(ctl); });

    const picked = findPickedCombo();
    if (!picked) return;   // 이론상 항상 찾아져야 함(모든 조합이 다 있다고 가정) — 방어코드

    if (optionIdInput) optionIdInput.value = picked.optionId;

    // 2026-09-03 추가 — 맨 아래 "굿팡상품번호: 95 - 395" 줄이 옵션을 바꿔도 안 바뀌던 버그.
    // 서버(/option)에 다시 물어볼 필요 없이 지금 고른 조합(picked)이 이미 optionId 를 갖고 있으므로 바로 찍음.
    const goodpangOptionNoEl = document.getElementById('goodpangOptionNo');
    if (goodpangOptionNoEl) goodpangOptionNoEl.textContent = picked.optionId;

    if (colorInput) {
      // 원래 "색상"만 담던 자리인데, 지금은 고른 값들을 다 이어붙여서 담음 (예: "M / Black")
      colorInput.value = controls.map(function (ctl) { return ctl.getValue(); }).join(' / ');
    }

    /* 2026-09-01 추가 — 지금 고른 옵션을 주소창에 실어둠(?optionId=).
       실제 쿠팡도 옵션을 바꾸면 페이지 이동 없이 주소(itemId/vendorItemId)만 바뀌는 걸
       Playwright 로 확인함. pushState 가 아니라 replaceState 를 쓰는 이유: 옵션 하나 누를 때마다
       "뒤로가기" 기록이 쌓이면 안 되고(다른 사이즈 5번 눌렀는데 뒤로가기 5번 눌러야 이 상품
       페이지를 벗어나는 건 이상함), 주소는 "지금 상태"만 보여주면 되기 때문.
       이 함수는 페이지 처음 열릴 때도 한 번 불려서(맨 아래 updateFromSelects() 참고), 그때도
       주소에 optionId 가 없었다면 여기서 채워짐 — 원본도 처음부터 itemId 가 주소에 있는 것과 같음 */
    if (window.history && window.history.replaceState) {
      const params = new URLSearchParams(location.search);
      params.set('optionId', picked.optionId);
      history.replaceState(null, '', location.pathname + '?' + params.toString());
    }

    fetchOptionDetail(picked.optionId);
  }

  /* ★ 2026-08-31 추가 — 가격/재고/사진은 이제 combos(페이지 처음 받아둔 JSON)를 그대로 안 쓰고
     서버(/option)에 다시 물어봄. combos 는 이제 "이 상품이 어떤 축(사이즈/색상)에 어떤 값을
     가지는지" 구조를 그리는 데만 쓰고, 실제 값(누구한테 얼마로 보여줄지)은 서버가 매번 계산해서
     내려줌 — 나중에 로그인 회원등급별 할인이 생겨도 이 함수는 안 고쳐도 됨(서버만 고치면 됨). */
  function fetchOptionDetail(optionId) {
    const seq = ++optionFetchSeq;

    fetch(contextPath + '/option?optionId=' + optionId)
      .then(function (res) {
        if (!res.ok) throw new Error('옵션 정보를 불러오지 못했습니다: ' + res.status);
        return res.json();
      })
      .then(function (data) {
        if (seq !== optionFetchSeq) return;   // 그 사이에 다른 옵션을 또 눌렀으면 이 응답은 버림

        /* 재고/품절 — 옵션마다 재고가 다르므로 고를 때마다 다시 판단.
           STATUS 가 'N'(판매중지)이거나 재고가 0이면 품절로 봄 */
        // 옛 버전(2026-09-06 이전):
        //   const soldout = (data.quantity <= 0) || (data.status === 'N');
        //   document.body.classList.toggle('is-soldout', soldout);
        /* ★ 2026-09-06 — 상품 전체가 '판매 중지'면 어떤 옵션을 골라도 계속 품절 상태여야 함.
           이 줄이 옵션을 바꿀 때마다 is-soldout 을 다시 계산해서 덮어쓰기 때문에, 여기서도
           상품 단위 상태(body 의 data-sale-status, product.jsp 에서 심음)를 같이 봐야 함.
           안 그러면 판매중지 상품에서 옵션을 한 번만 바꿔도 구매 버튼이 되살아남 */
        const saleStopped = document.body.dataset.saleStatus === '판매 중지';
        const soldout = saleStopped || (data.quantity <= 0) || (data.status === 'N');
        document.body.classList.toggle('is-soldout', soldout);
        if (setStock) setStock(data.quantity);
        if (setPrice) setPrice(data.price, data.normalPrice);

        // /option 서블릿도 이미지 경로를 이미 img:url과 같은 규칙으로 변환해서 내려줌
        const imageUrls = data.imageUrls || [];
        if (!imageUrls.length || !thumbBox || !mainImg) return;

        thumbBox.innerHTML = imageUrls.map(function (url, i) {
          return '<li class="' + (i === 0 ? 'is-on' : '') + '">'
               +   '<a href="#"><img src="' + url + '" alt=""></a>'
               + '</li>';
        }).join('');

        mainImg.src = imageUrls[0];
      })
      .catch(function (err) {
        console.error(err);
      });
  }

  axes.forEach(function (axis, axisIndex) {
    // 이 축에서 실제로 쓰인 값만 중복 없이 뽑음 (등장한 순서 그대로 유지)
    const values = [];
    combos.forEach(function (c) {
      if (c[axis.key] && values.indexOf(c[axis.key]) === -1) values.push(c[axis.key]);
    });

    const section = document.createElement('section');
    section.className = 'option-row';

    const useChips = axisIndex > 0 && anyImages;   // 1번 축은 무조건 드롭박스

    // 이 축에서 "처음부터 선택돼 있어야 할 값" — URL 에 실려온 조합이 있으면 그 값, 없으면 예전처럼 첫 값
    const initialValue = (initialCombo && initialCombo[axis.key]) || values[0];

    if (useChips) {
      const label = document.createElement('div');
      label.innerHTML = axis.type + ': <span class="option-value">' + initialValue + '</span>';
      label.className = 'option-label';
      section.appendChild(label);

      const ul = document.createElement('ul');
      ul.className = 'option-chips';

      values.forEach(function (v) {
        const li = document.createElement('li');
        li.dataset.value = v;
        if (v === initialValue) li.classList.add('is-on');
        li.innerHTML = '<a href="#"><img src="" alt="' + v + '"></a>';
        li.addEventListener('click', function (e) {
          e.preventDefault();
          ul.querySelectorAll('li').forEach(function (o) { o.classList.remove('is-on'); });
          li.classList.add('is-on');
          const valueLabel = label.querySelector('.option-value');
          if (valueLabel) valueLabel.textContent = v;
          updateFromSelects();
        });
        ul.appendChild(li);
      });

      section.appendChild(ul);
      box.appendChild(section);

      controls.push({
        axisKey: axis.key,
        root: ul,
        chipList: true,
        getValue: function () {
          const on = ul.querySelector('li.is-on');
          return on ? on.dataset.value : values[0];
        }
      });

    } else {
      const label = document.createElement('div');
      label.className = 'option-label';
      label.textContent = axis.type;
      section.appendChild(label);

      const wrap = document.createElement('div');
      wrap.className = 'option-select';

      const select = document.createElement('select');
      values.forEach(function (v) {
        const opt = document.createElement('option');
        opt.value = v;
        opt.textContent = v;
        if (v === initialValue) opt.selected = true;
        select.appendChild(opt);
      });
      select.addEventListener('change', updateFromSelects);

      wrap.appendChild(select);
      section.appendChild(wrap);
      box.appendChild(section);

      controls.push({
        axisKey: axis.key,
        root: select,
        chipList: false,
        getValue: function () { return select.value; }
      });
    }
  });

  updateFromSelects();   // 페이지 처음 열렸을 때도 hidden input·칩 사진을 첫 조합 값으로 맞춰둠
}


/* ── 5. 필수 표기 정보 더보기 ─────────────────────
   표에 .is-open 을 붙였다 뗐다 하면 숨겨둔 줄(.is-folded)이 나타나고 사라짐.
   aria-expanded 는 화면낭독기에게 "지금 펼쳐져 있다/접혀 있다"를 알려주는 표시 */
function setupItemBriefMore() {
  const btn   = document.querySelector('.item-brief__more');
  const table = document.querySelector('.item-brief__table');
  if (!btn || !table) return;

  btn.addEventListener('click', function () {
    const open = table.classList.toggle('is-open');
    btn.classList.toggle('is-open', open);
    btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    btn.firstChild.nodeValue = open ? '필수 표기 정보 접기' : '필수 표기 정보 더보기';
  });
}


/* ── 5.5 리뷰 설문 "자세히 보기" ────────────────
   2026-08-24 추가. 원본을 Playwright 로 직접 열어서 확인한 동작:
     모달이 아니라 **왼쪽 요약 칸 안에서 아래로 펼쳐지고**, 버튼 글자가 "접기" 로 바뀜.
   위 setupItemBriefMore 와 완전히 같은 방식이라 구조를 일부러 똑같이 맞춰둠
   (나중에 하나를 고치면 다른 것도 같이 보게 하려고). */
function setupSurveyMore() {
  const btn    = document.querySelector('.survey-more');
  const detail = document.querySelector('.survey-detail');
  if (!btn || !detail) return;

  btn.addEventListener('click', function () {
    const open = detail.classList.toggle('is-open');
    btn.classList.toggle('is-open', open);
    btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    btn.textContent = open ? '접기' : '자세히 보기';
  });
}


/* ── 6. 탭 — 지금 보고 있는 구간을 자동으로 표시 ──────
   원본은 스크롤을 내리면 탭이 화면 위에 붙어 있고(CSS sticky),
   지금 보는 구간에 맞춰 활성 탭이 저절로 바뀜. 그걸 흉내낸 것.

   ★ 원리: 각 탭이 가리키는 섹션(#detail, #reviews …)의 위치를 재서,
     화면 위쪽 기준선을 이미 지나간 섹션 중 **제일 마지막 것**을 지금 구간으로 봄. */
function setupTabSpy() {
  const tabs = [].slice.call(document.querySelectorAll('.detail-tabs a'));
  if (!tabs.length) return;

  /* 각 탭과 그 탭이 가리키는 섹션을 짝지어 둠 */
  const pairs = tabs.map(function (a) {
    return { tab: a, target: document.querySelector(a.getAttribute('href')) };
  }).filter(function (p) { return p.target; });

  /* 탭을 누르면 그 섹션으로 부드럽게 이동.
     탭이 화면 위에 붙어 있으므로 탭 높이(49)만큼 위로 더 올려야 제목이 안 가림 */
  pairs.forEach(function (p) {
    p.tab.addEventListener('click', function (e) {
      e.preventDefault();
      const y = window.scrollY + p.target.getBoundingClientRect().top - 49;
      window.scrollTo({ top: y, behavior: 'smooth' });
    });
  });

  function update() {
    const line = 60;              // 화면 위에서 60px 지점을 기준선으로 봄
    let current = pairs[0];
    pairs.forEach(function (p) {
      if (p.target.getBoundingClientRect().top <= line) current = p;
    });
    pairs.forEach(function (p) {
      p.tab.classList.toggle('is-on', p === current);
    });
  }

  /* 스크롤할 때마다 계산하면 너무 자주 도니까
     "다음 화면 그리기 직전에 한 번만" 하도록 묶음 (requestAnimationFrame) */
  let waiting = false;
  window.addEventListener('scroll', function () {
    if (waiting) return;
    waiting = true;
    requestAnimationFrame(function () { update(); waiting = false; });
  });

  update();
}


/* ── 6.3 리뷰 정렬 / 검색 / 별점 필터 ─────────────
   2026-08-21 추가. 원본을 실제로 열어서 확인함(vp_04_review.jpeg):
     정렬은 "베스트순·최신순" 둘뿐 — 별점순은 없음.
     옆의 "모든 별점" select 는 정렬이 아니라 "필터"임 (고른 별점만 남기고 나머진 숨김).

   ▶JSP 로 가면: 정렬·필터·검색 전부 서버에 물어보는 걸로 바뀜
     (예: ?sort=best&rating=5&q=포장 → SQL 의 ORDER BY / WHERE).
     지금은 서버가 없어서 이미 화면에 있는 카드 3개를 JS 로 줄 세우고 숨기는 걸로 흉내냄. */
function setupReviewTools() {
  const tools = document.querySelector('.review-tools');
  if (!tools) return;

  const list      = document.querySelector('.review-list');
  const sortLinks = tools.querySelectorAll('.review-sort a');
  const searchBox = tools.querySelector('.review-search');
  const ratingSel = tools.querySelector('.review-filter');
  const emptyMsg  = document.querySelector('.review-empty');
  const pager     = document.querySelector('.review-pagination');
  const pageNums  = pager ? pager.querySelector('.page-numbers') : null;
  const prevBtn   = pager ? pager.querySelector('.page-prev') : null;
  const nextBtn   = pager ? pager.querySelector('.page-next') : null;

  function items() {
    return Array.from(list.querySelectorAll('.review-item'));
  }

  /* 별점 — 2026-08-27: 원본과 별 아이콘을 맞추면서 별을 글자(★★★★☆)가 아니라
     스프라이트 이미지(.star-rating em 의 width%)로 그리게 바꿈 — 그러면 화면엔 셀 수 있는
     글자가 없어져서, article 태그에 심어둔 data-rating(product.jsp 에서 ${r.rating} 그대로 찍음)
     을 읽음. "두 군데 값이 어긋날 수 있다"던 예전 우려는 이제 해당 없음 — 화면(width%)과
     data-rating 이 둘 다 서버에서 같은 ${r.rating} 값 하나로 같이 찍히기 때문 */
  function ratingOf(item) {
    return Number(item.dataset.rating) || 0;
  }


  /* 정렬 — 카드 순서를 바꿔서 다시 꽂아넣음.
      "베스트순" 기준은 원본이 비공개라 지금은 data-helpful(더미 "도움돼요" 수)로 대
  신함

       ★ 2026-08-27 버그 수정: insertBefore 의 기준 노드로 emptyMsg 를 썼는데, 리뷰가
  있으면
         product.jsp 가 .review-empty 자체를 안 찍어서(<c:otherwise>) emptyMsg 가 null
   임.
         insertBefore(el, null) 은 "맨 끝에 붙이기"랑 같아서, 카드들이 .review-list 의
   진짜 마지막
         자식인 <nav class="review-pagination"> 뒤로 밀려나가 정렬할 때마다 페이지네이
  션이
         리뷰 위로 올라가 보였음. emptyMsg 가 없으면 pager 앞에 꽂도록 기준을 바꿔서
  고침 */ 
  function sortBy(key) {
    const sorted = items().sort(function (a, b) {
      if (key === 'latest') {
        return b.querySelector('.date').textContent.localeCompare(a.querySelector('.date').textContent);
      }
      return (Number(b.dataset.helpful) || 0) - (Number(a.dataset.helpful) || 0);
    });
	const anchor = emptyMsg || pager || null;
	sorted.forEach(function (el) { list.insertBefore(el, anchor); });
	  }

  /* ★ 정렬·검색·별점필터·페이지 이동이 전부 한 함수(render)를 거치게 만든 이유:
       따로 만들면 "2페이지를 보다가 검색어를 치면 2페이지가 그대로 남아 아무것도 안 보이는"
       식으로 서로 어긋남. 조건이 하나라도 바뀌면 무조건 처음부터 다시 계산하는 게 안전함.
     순서: ① 필터로 남길 카드를 고른다 → ② 그중 지금 페이지 몫만 보여준다 */
  const PAGE_SIZE = 3;    // 한 페이지에 리뷰 3개 (더미가 5개라 2페이지가 됨)
  let page = 1;

    /* 2026-08-27 수정: REVIEW_SUMMARY(한줄요약) 연동하면서 .review-headline 이 리뷰
  카드에
       다시 생김(product.jsp) — 있는 리뷰만 나오므로 null-safe(?.)하게 검색 대상에 같
  이 넣음 */
  function passesFilter(item) {
    const q = searchBox.value.trim().toLowerCase();
    const rating = ratingSel.value;   // '' = 모든 별점
	const headlineEl = item.querySelector('.review-headline');
	const textEl = item.querySelector('.review-text');
	const text = ((headlineEl ? headlineEl.textContent : '') + ' ' + (textEl ? textEl.textContent : '')).toLowerCase();
	    return (!q || text.includes(q)) && (!rating || ratingOf(item) === Number(rating));
  }

  function render() {
    const kept = items().filter(passesFilter);
    const totalPages = Math.max(1, Math.ceil(kept.length / PAGE_SIZE));
    if (page > totalPages) page = totalPages;      // 필터로 개수가 줄면 페이지도 당겨줌

    const start = (page - 1) * PAGE_SIZE;
    items().forEach(function (item) {
      const idx = kept.indexOf(item);
      const onThisPage = idx >= start && idx < start + PAGE_SIZE;
      item.classList.toggle('is-hidden', !onThisPage);
    });

	/* product.jsp 는 리뷰가 있으면 <c:otherwise> 쪽(.review-empty)을 아예 안 찍음 —
	       그래서 리뷰가 있을 땐 emptyMsg 가 null 일 수 있음. null-safe 하게 처리 */
	if (emptyMsg) emptyMsg.style.display = kept.length === 0 ? 'block' : 'none';
    renderPager(totalPages);
  }

  /* 페이지 번호 버튼을 매번 새로 그림.
     미리 HTML 에 박아두지 않는 이유: 필터를 걸면 총 페이지 수가 달라지기 때문 */
  function renderPager(totalPages) {
    if (!pager) return;
    pager.classList.toggle('is-hidden', totalPages <= 1);

    pageNums.innerHTML = '';
    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.textContent = i;
      if (i === page) btn.classList.add('is-on');
      btn.addEventListener('click', function () { page = i; render(); scrollToTop(); });
      pageNums.appendChild(btn);
    }

    prevBtn.disabled = (page === 1);
    nextBtn.disabled = (page === totalPages);
  }

  /* 페이지를 넘기면 리뷰 목록 맨 위로 올려줌 —
     안 그러면 3페이지째 아래쪽에 있다가 넘겼을 때 화면 중간에 뚝 떨어짐 */
  function scrollToTop() {
    const section = document.querySelector('.product-review');
    if (section) window.scrollTo({ top: section.offsetTop - 60, behavior: 'smooth' });
  }

  sortLinks.forEach(function (a) {
    a.addEventListener('click', function (e) {
      e.preventDefault();
      sortLinks.forEach(function (other) { other.classList.remove('is-on'); });
      a.classList.add('is-on');
      sortBy(a.dataset.sort);
      page = 1;          // 정렬이 바뀌면 1페이지부터 다시
      render();
    });
  });

  /* 검색·필터를 건드리면 보고 있던 페이지 번호는 의미가 없어지므로 1페이지로 */
  searchBox.addEventListener('input',  function () { page = 1; render(); });
  ratingSel.addEventListener('change', function () { page = 1; render(); });

  if (prevBtn) prevBtn.addEventListener('click', function () {
    if (page > 1) { page--; render(); scrollToTop(); }
  });
  if (nextBtn) nextBtn.addEventListener('click', function () {
    page++; render(); scrollToTop();
  });

  sortBy('best');   // 페이지가 열릴 때 기본 탭(베스트순) 기준으로 한 번 정렬해둠
  render();
}


/* ── 6.5 배송 방법 선택 ─────────────────────────
   2026-08-21 추가. 원본 상세페이지를 Playwright 로 직접 열어서 확인함:
     ① 로켓배송 상품 19,800원 이상 무료배송   ← 기본 선택
     ② 무료배송 + 무료반품 | 로켓와우 신청시
   ②를 고르면 [장바구니 담기][바로구매] 두 칸이 [로켓와우로 무료배송 >] 한 칸으로 바뀜.
   (처음엔 그냥 클릭해봐도 안 바뀌길래 "버그인가" 했는데, div 전체가 아니라
    18px짜리 동그라미(span.radio)를 정확히 눌러야 반응하는 걸 원본에서도 확인함 —
    그래서 우리도 li 전체에 클릭 이벤트를 걺. 이러면 그런 정밀 클릭이 필요 없어져서 더 편함)

   ▶JSP: 지금은 "① 기본 선택"을 화면에서만 정하는데, 나중엔 로그인 여부·와우 가입 여부에
     따라 서버가 처음부터 다른 쪽을 선택해서 내려줄 자리 (${member.isWow ? 'is-on' : ''}) */
function setupDeliveryOption() {
  const list = document.querySelector('.radio-group');
  if (!list) return;

  const items = list.querySelectorAll('.radio-item');
  const WOW_INDEX = 1;   // 두 번째 항목 = 로켓와우

  items.forEach(function (li, i) {
    li.addEventListener('click', function () {
      items.forEach(function (other) { other.classList.remove('is-on'); });
      li.classList.add('is-on');

      /* body 에 클래스를 붙이면 css/product.css 가 버튼을 알아서 바꿔치기함
         (품절 처리와 똑같은 방식 — CLAUDE.md 참고) */
      document.body.classList.toggle('is-wow-delivery', i === WOW_INDEX);
    });
  });
}

function refreshCartPreview() {

    const preview =
        document.getElementById(
            "cartPreviewWrapper"
        );

    if (!preview) return;

    const productOptionsData =
        document.getElementById(
            "productOptionsData"
        );

    const contextPath =
        productOptionsData
            ? productOptionsData.dataset.contextPath
            : "";

    fetch(contextPath + "/cart/preview", {
        method: "GET",
        headers: {
            "X-Requested-With":
                "XMLHttpRequest"
        }
    })
    .then(function(response) {

        if (!response.ok) {
            throw new Error(
                "장바구니 미리보기를 불러오지 못했습니다."
            );
        }

        return response.text();
    })
    .then(function(html) {

        preview.innerHTML = html;
    })
    .catch(function(error) {

        console.error(error);
    });
}


function setupCartAdd() {

     const cartAddBtn =
         document.getElementById("cartAddBtn");

     const cartAddedPopup =
         document.getElementById("cartAddedPopup");

     const cartPopupClose =
         document.getElementById("cartPopupClose");

     if (!cartAddBtn) return;

     cartAddBtn.addEventListener("click", function(e) {

         e.preventDefault();

         const form =
             cartAddBtn.closest("form");

         if (!form) return;

         const formData =
             new FormData(form);

         const params =
             new URLSearchParams();

         formData.forEach(function(value, key) {
             params.append(key, value);
         });

         cartAddBtn.disabled = true;

         fetch(form.action, {
             method: "POST",
             body: params,
             headers: {
                 "X-Requested-With": "XMLHttpRequest"
             }
         })
         .then(function(response) {

             if (!response.ok) {
                 throw new Error(
                     "장바구니 담기에 실패했습니다."
                 );
             }

             return response.json();
         })
         .then(function(data) {

             if (!data.success) return;

             // 팝업 표시
             if (cartAddedPopup) {
                 cartAddedPopup.classList.add("show");
             }

             // 헤더 장바구니 개수만 변경
             const cartCount =
                 document.getElementById("cartCount");

             if (cartCount && data.cartCount != null) {
                 cartCount.textContent = data.cartCount;
             }
			 
			 refreshCartPreview();
         })
         .catch(function(error) {

             console.error(error);

             alert(
                 "장바구니 담기 중 오류가 발생했습니다."
             );
         })
         .finally(function() {

             cartAddBtn.disabled = false;
         });
     });

     if (cartPopupClose) {

         cartPopupClose.addEventListener(
             "click",
             function() {

                 if (cartAddedPopup) {
                     cartAddedPopup.classList.remove("show");
                 }
             }
         );
     }
 }


/* ── 7. 품절 상태 ───────────────────────────────
   <body class="page-product is-soldout"> 가 붙으면 화면이 품절 모습으로 바뀜.
   (실제로 바꾸는 건 css/product.css 9장 — 클래스만 붙이고 뗌)

   ★ 2026-08-28: 예전에 확인용으로 주소 뒤에 ?soldout=1 을 붙이면 켜지던 setupSoldout() 함수를 지웠음.
     그 함수가 초기화 목록에서 setupOptionSelect() 보다 뒤에 실행되면서
     classList.toggle('is-soldout', false) 로 방금 붙인 클래스를 도로 떼버려서,
     "첫 화면에서는 품절이 안 뜨고 옵션을 눌러야 뜨는" 사고가 났음.
     이제 품절 판정은 setupOptionSelect() 안의 updateFromSelects() 한 군데에서만 함
     (그 옵션의 QUANTITY 가 0 이거나 STATUS 가 'N' 이면 품절). */


/* setupQuantity() 가 "재고/가격을 바꾸는 함수" 두 개를 돌려주고, 그걸 setupOptionSelect() 에 넘겨줌 —
   옵션을 고를 때마다 그 옵션의 재고·가격으로 화면이 바뀌게 하려는 것 */
const quantityControls = setupQuantity() || {};
setupThumbs();
setupImageZoom();
setupAdSlider();
setupOptionSelect(quantityControls.setStock, quantityControls.setPrice);
setupReviewTools();
setupDeliveryOption();
setupCartAdd();
/* ── 9. 리뷰 사진 갤러리 (2단 모달) ────────────────
   2026-08-24 추가. 원본을 Playwright 로 직접 열어서 확인한 흐름 그대로:
     ① 리뷰 사진 썸네일 클릭       → "갤러리" 모달 (사진 전체를 격자로)
     ② 갤러리 안 썸네일 또 클릭    → "사진 뷰어" (큰 사진 + 좌우 화살표 + 그 리뷰 글)
   뷰어의 화살표는 **갤러리 사진 전체**를 넘김 (그 리뷰 것만이 아님 — 원본이 그렇게 동작함).
   사진이 바뀌면 위 작성자 정보와 아래 리뷰 글도 그 사진의 주인 리뷰로 같이 바뀜.

   ★★ 사진 목록의 출처는 "리뷰 카드 안의 .review-photos" 하나뿐임 (2026-08-24 수정).
     전에는 위쪽 갤러리 줄에도 사진을 손으로 적고 data-review 로 주인을 표시했는데,
     같은 정보(어느 사진이 누구 것인지)가 두 군데에 있어서 실제로 어긋났음 —
     갤러리는 review-4 를 최*영 것이라 했지만 최*영 카드에는 사진이 없어서
     "전체보기로 가보면 사진이 없는" 상태가 됐음(사용자가 발견).
     지금은 카드에서 긁어모으므로 어긋날 수가 없고, 위쪽 갤러리 줄도 JS 가 그려줌.
     (수량↔가격, 리뷰 별점 때도 같은 원칙을 썼음: 값은 한 곳에만 둔다)

   ★ 리뷰 id 로 주인을 기억하는 이유: 정렬(베스트순/최신순)을 누르면 카드의 DOM 순서가
     바뀌므로 "몇 번째 리뷰" 로 기억하면 어긋남.

   ▶JSP: 카드 안 사진만 <c:forEach> 로 찍어내면 이 JS 는 그대로 동작함. */
function setupReviewGallery() {
  const source = document.querySelector('.review-gallery');
  const modal  = document.querySelector('.review-gallery-modal');
  const viewer = document.querySelector('.photo-viewer');
  if (!source || !modal || !viewer) return;

  /* 리뷰 카드를 전부 훑어서 사진을 모음. [{ src, reviewId }, ...]
     ⚠ 페이지네이션으로 숨겨진(.is-hidden) 카드도 DOM 에는 있으므로 같이 걷힘 —
       원본도 갤러리에는 2페이지 사진까지 다 나오므로 이게 맞음 */
  const photos = [];
  document.querySelectorAll('.review-item').forEach(function (card) {
    const id = card.dataset.reviewId || '';
    card.querySelectorAll('.review-photos img').forEach(function (img) {
      photos.push({ src: img.src, reviewId: id });
    });
  });

  /* 위쪽 갤러리 줄을 모은 사진으로 그림 (HTML 에는 빈 <ul> 만 있음).
     클릭은 아래에서 ul 에 이벤트 위임으로 한 번만 걸어둠 */
  photos.forEach(function (p) {
    const li = document.createElement('li');
    const img = document.createElement('img');
    img.src = p.src;
    img.alt = '';
    li.appendChild(img);
    source.appendChild(li);
  });

  const grid      = modal.querySelector('.gallery-grid');
  const countEl   = modal.querySelector('.gallery-count strong');
  const stage     = viewer.querySelector('.viewer-img');
  const thumbList = viewer.querySelector('.viewer-thumbs');
  const prevBtn   = viewer.querySelector('.viewer-prev');
  const nextBtn   = viewer.querySelector('.viewer-next');
  let current = 0;

  /* 열려 있는 모달이 하나라도 있으면 뒤 페이지 스크롤을 막음 */
  function lockScroll() {
    const open = !modal.hidden || !viewer.hidden;
    document.body.classList.toggle('is-modal-open', open);
  }

  /* ── ① 갤러리 모달 ── */
  function openGallery() {
    countEl.textContent = photos.length;
    grid.innerHTML = '';
    photos.forEach(function (p, i) {
      const li = document.createElement('li');
      const img = document.createElement('img');
      img.src = p.src;
      img.alt = '';
      li.appendChild(img);
      /* 원본에 있는 오른쪽 아래 말풍선 배지 (CSS 로 그림) */
      const badge = document.createElement('span');
      badge.className = 'photo-badge';
      li.appendChild(badge);
      li.addEventListener('click', function () { openViewer(i); });
      grid.appendChild(li);
    });
    modal.hidden = false;
    lockScroll();
  }
  
 
  /* ── ② 사진 뷰어 ── */
  function openViewer(index) {
    current = index;
    thumbList.innerHTML = '';
    photos.forEach(function (p, i) {
      const li = document.createElement('li');
      const img = document.createElement('img');
      img.src = p.src;
      img.alt = '';
      li.appendChild(img);
      li.addEventListener('click', function () { showPhoto(i); });
      thumbList.appendChild(li);
    });
    viewer.hidden = false;
    lockScroll();
    showPhoto(index);
  }

  /* 사진 한 장을 화면에 올림 + 그 사진 주인 리뷰의 정보로 위아래를 채움 */
  function showPhoto(index) {
    current = (index + photos.length) % photos.length;   // 끝에서 넘기면 처음으로 돌아감
    const p = photos[current];
    stage.src = p.src;

    [].slice.call(thumbList.children).forEach(function (li, i) {
      li.classList.toggle('is-on', i === current);
    });

    /* 사진이 1장뿐이면 화살표를 감춤 */
    prevBtn.hidden = nextBtn.hidden = (photos.length <= 1);

    /* 이 사진이 달린 리뷰 카드를 id 로 찾아서 글자를 그대로 가져옴.
       리뷰 내용을 뷰어에 또 적어두지 않는 이유: 같은 글이 두 군데 있으면
       한쪽만 고쳤을 때 어긋남 (수량→가격 만들 때 겪은 것과 같은 종류의 실수) */
	   const card = document.querySelector('.review-item[data-review-id="' + p.reviewId + '"]');
	       const pick = function (sel) {
	         const el = card && card.querySelector(sel);
	         return el ? el.textContent.trim() : '';
	       };
	  viewer.querySelector('.viewer-writer .name').textContent  = pick('.name');
 	  viewer.querySelector('.viewer-writer .date').textContent  = pick('.date');

      /* 별점은 글자가 아니라 스프라이트(.star-rating > em 의 width%)라 textContent 로는
          못 옮김 — 원본 카드의 em width 를 그대로 복사함 (2026-08-27) */
      const srcStar = card && card.querySelector('.star-rating em');
      const dstStar = viewer.querySelector('.viewer-writer .star-rating em');
       if (dstStar) dstStar.style.width = srcStar ? srcStar.style.width : '0%';
      viewer.querySelector('.viewer-option').textContent        = pick('.review-option');
      viewer.querySelector('.viewer-text').textContent          = pick('.review-text');
      viewer.dataset.reviewId = p.reviewId;
	     }

  function closeViewer() { viewer.hidden = true; lockScroll(); }
  function closeAll()    { viewer.hidden = true; modal.hidden = true; lockScroll(); }

  /* 리뷰 사진 썸네일 → 갤러리 열기.
     각 li 가 아니라 부모(ul)에 이벤트를 거는 이유(이벤트 위임): 나중에 JSP 가
     사진을 몇 장 찍어낼지 모르므로, 목록이 바뀌어도 코드를 안 고치게 하려고 */
  source.addEventListener('click', function (e) {
    const li = e.target.closest('li');
    if (li) { e.preventDefault(); openGallery(); }
  });

  modal.querySelector('.gallery-close').addEventListener('click', closeAll);
  viewer.querySelector('.viewer-close').addEventListener('click', closeViewer);
  prevBtn.addEventListener('click', function () { showPhoto(current - 1); });
  nextBtn.addEventListener('click', function () { showPhoto(current + 1); });

  /* "전체보기" → 모달을 다 닫고 그 리뷰 카드로 이동 */
  viewer.querySelector('.viewer-all').addEventListener('click', function () {
    const id = viewer.dataset.reviewId;
    closeAll();
    const card = document.querySelector('.review-item[data-review-id="' + id + '"]');
    if (card) window.scrollTo({ top: card.offsetTop - 120, behavior: 'smooth' });
  });

  /* 어두운 배경(덮개)을 직접 눌렀을 때만 닫음.
     e.target === 덮개 를 확인하는 이유: 안쪽 패널을 눌렀을 때도 클릭이 부모로
     올라와서(버블링) 같이 닫혀버리기 때문 */
  modal.addEventListener('click', function (e) { if (e.target === modal) closeAll(); });
  viewer.addEventListener('click', function (e) { if (e.target === viewer) closeViewer(); });

  /* 키보드 — ESC 로 닫기, 좌우 화살표로 사진 넘기기 (뷰어가 열려 있을 때만) */
  document.addEventListener('keydown', function (e) {
    if (!viewer.hidden) {
      if (e.key === 'Escape')     closeViewer();
      if (e.key === 'ArrowLeft')  showPhoto(current - 1);
      if (e.key === 'ArrowRight') showPhoto(current + 1);
    } else if (!modal.hidden && e.key === 'Escape') {
      closeAll();
    }
  });
}



setupItemBriefMore();
setupSurveyMore();
setupTabSpy();
setupReviewGallery();


