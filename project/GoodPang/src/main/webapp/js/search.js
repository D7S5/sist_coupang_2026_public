/* =========================================================
   search.js — 검색결과 페이지(index_search.html) 전용 동작

   ★ 상품 카드는 더 이상 HTML에 직접 적혀있지 않음.
   data/search-products.json 을 fetch로 읽어와서
   search-product-card 마크업을 JS로 직접 찍어냄 (main.js의 TODO와 같은 방식).

   흐름:
     1. JSON 로드 → products 배열에 저장 (원본 순서 그대로 = "랭킹순")
     2. 정렬/필터 상태가 바뀔 때마다 products를 가공해서 render() 재호출
     3. render() 는 매번 HTML을 새로 만들어 .search-product-list 에 통째로 그림

   ⚠ fetch는 file:// 로 더블클릭해서 열면 브라우저가 막아버림(CORS).
   Tomcat 같은 서버로 띄워서 http:// 로 열어야 정상 동작함
   (header.js에도 같은 이유로 fetch를 안 쓴 코드가 남아있음)
   ========================================================= */

(function () {

  const list = document.querySelector('.search-product-list');
  if (!list) return;

  const resultCount = document.querySelector('.search-result-count');
  const emptyMsg = document.querySelector('.search-empty');

  let products = [];              // 서버(JSON)에서 받아온 원본 데이터. 정렬해도 이 배열은 안 건드림
  let currentSort = 'rank';       // 랭킹순 / price-asc / price-desc / review-desc
  const activeFilters = new Set();   // 켜진 빠른 필터 이름 ('rocket', 'coupon' ...)


  /* ---------------------------------------------------------
     상품 1개 → <li class="search-product-card"> HTML 문자열
     --------------------------------------------------------- */

  function starIcons(rating) {
    const filled = Math.min(5, Math.max(0, Math.round(rating)));
    return '★'.repeat(filled) + '☆'.repeat(5 - filled);
  }

  function won(n) {
    return n.toLocaleString('ko-KR') + '원';
  }

  function cardHTML(p) {
    const badge = p.ad
      ? '<span class="badge-ad">AD</span>'
      : (p.rocket ? '<span class="badge-rocket">로켓배송</span>' : '');

    const priceRow = p.discountRate
      ? '<span class="discount">' + p.discountRate + '%</span><span class="price">' + won(p.price) + '</span>'
      : '<span class="price">' + won(p.price) + '</span>';

    const originalPriceHtml = p.originalPrice
      ? '<p class="price-original">' + won(p.originalPrice) + '</p>'
      : '';

    const tags = [
      p.freeShipping ? '<span class="tag tag--free">무료배송</span>' : '',
      p.coupon ? '<span class="tag tag--coupon">쿠폰할인</span>' : ''
    ].filter(Boolean).join(' ');

    return ''
      + '<li class="search-product-card" style="--hue:' + p.hue + '"'
      + ' data-price="' + p.price + '" data-review="' + p.reviewCount + '"'
      + ' data-rocket="' + p.rocket + '" data-coupon="' + p.coupon + '">'
      + '<a href="#">'
      + '<div class="search-thumb-wrap">'
      + '<img class="thumb-img" src="' + p.image + '" alt="' + p.name + '" loading="lazy">'
      + badge
      + '</div>'
      + '<p class="brand">' + p.brand + '</p>'
      + '<p class="name">' + p.name + '</p>'
      + '<div class="rating"><span class="stars">' + starIcons(p.rating) + '</span>'
      + '<span class="count">(' + p.reviewCount.toLocaleString('ko-KR') + ')</span></div>'
      + '<div class="price-row">' + priceRow + '</div>'
      + originalPriceHtml
      + '<div class="tag-row">' + tags + '</div>'
      + '</a>'
      + '</li>';
  }


  /* ---------------------------------------------------------
     정렬 + 필터를 적용한 배열을 만들고 화면에 그림
     --------------------------------------------------------- */

  function sortProducts(source) {
    const arr = source.slice();   // 원본 products 배열은 그대로 두고 복사본만 정렬

    if (currentSort === 'price-asc') arr.sort((a, b) => a.price - b.price);
    else if (currentSort === 'price-desc') arr.sort((a, b) => b.price - a.price);
    else if (currentSort === 'review-desc') arr.sort((a, b) => b.reviewCount - a.reviewCount);
    // 'rank' 는 JSON에 담긴 원본 순서를 그대로 씀 (별도 정렬 없음)

    return arr;
  }

  function filterProducts(source) {
    return source.filter(function (p) {
      const passRocket = !activeFilters.has('rocket') || p.rocket;
      const passCoupon = !activeFilters.has('coupon') || p.coupon;
      return passRocket && passCoupon;
    });
  }

  function render() {
    const visible = filterProducts(sortProducts(products));

    list.innerHTML = visible.map(cardHTML).join('');

    if (resultCount) {
      resultCount.textContent = '상품 ' + visible.length.toLocaleString('ko-KR') + '개';
    }
    if (emptyMsg) {
      emptyMsg.classList.toggle('show', visible.length === 0);
    }
  }


  /* ---------------------------------------------------------
     정렬 탭
     --------------------------------------------------------- */

  document.querySelectorAll('.sort-tab').forEach(function (tab) {
    tab.addEventListener('click', function () {
      document.querySelectorAll('.sort-tab').forEach(t => t.classList.remove('is-active'));
      tab.classList.add('is-active');
      currentSort = tab.dataset.sort;
      render();
    });
  });


  /* ---------------------------------------------------------
     빠른 필터 (여러 개 동시에 켤 수 있음 — AND 조건)
     --------------------------------------------------------- */

  document.querySelectorAll('.quick-filter[data-filter]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const key = btn.dataset.filter;

      if (activeFilters.has(key)) {
        activeFilters.delete(key);
        btn.classList.remove('is-active');
      } else {
        activeFilters.add(key);
        btn.classList.add('is-active');
      }

      render();
    });
  });


  /* ---------------------------------------------------------
     체크박스 필터 — 카테고리/브랜드 등은 아직 실제 데이터가 없어서
     선택 상태만 시각적으로 반영. 나중에 필드가 생기면
     filterProducts() 안에 조건을 추가하면 됨
     --------------------------------------------------------- */

  document.querySelectorAll('.filter-check input').forEach(function (checkbox) {
    checkbox.addEventListener('change', function () {
      checkbox.closest('.filter-check').classList.toggle('is-checked', checkbox.checked);
    });
  });


  /* ---------------------------------------------------------
     필터 초기화
     --------------------------------------------------------- */

  const resetBtn = document.querySelector('.filter-reset');
  if (resetBtn) {
    resetBtn.addEventListener('click', function () {
      document.querySelectorAll('.filter-check input').forEach(function (checkbox) {
        checkbox.checked = false;
        checkbox.closest('.filter-check').classList.remove('is-checked');
      });

      document.querySelectorAll('.quick-filter[data-filter]').forEach(function (btn) {
        btn.classList.remove('is-active');
      });

      activeFilters.clear();
      render();
    });
  }


  /* ---------------------------------------------------------
     JSON 데이터 로드 — 실제 서비스라면 이 fetch 주소가
     검색 API(예: /api/search?q=티셔츠) 로 바뀌는 자리
     나중에 restful api 만들면 이부분을 교체할것 
     
     public폴더 안의 data/search-products.json  이걸 
     경로문제는 수업때 배운함수로 바꿀것
     --------------------------------------------------------- */

  fetch('data/search-products.json')
    .then(function (res) {
      if (!res.ok) throw new Error('상품 데이터를 불러오지 못했습니다 (' + res.status + ')');
      return res.json();
    })
    .then(function (data) {
      products = data;
      render();
    })
    .catch(function (err) {
      console.error(err);
      list.innerHTML = '';
      if (emptyMsg) {
        emptyMsg.textContent = '상품 데이터를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.';
        emptyMsg.classList.add('show');
      }
      if (resultCount) {
        resultCount.textContent = '상품 0개';
      }
    });

}());
