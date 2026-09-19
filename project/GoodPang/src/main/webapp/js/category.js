// 카테고리 목록 페이지(category_list.jsp) 전용 JS.
//
// 정렬 / 페이지네이션 / 카테고리·평점·가격·색상 필터는 전부 페이지 전체를 다시 요청하는 방식임
// (원본 coupang.com 도 실제로 이렇게 동작함 — SPA 부분갱신 아님, ref/category/STRUCTURE.md 10장 참고).
// 색상은 체크박스를 바꾸면 그 폼(#colorFilterForm)이 자동 제출되는 것까지 JSP 인라인 onchange 로 처리함.
//
// 2026-09-01: 브랜드/핏/소재 등 DB 연동이 없는 장식용 체크박스(name 없는 <input type="checkbox">)는
// 서버가 전혀 모르는 상태라, "선택한 필터"에 안 잡히고 "전체해제" 버튼도 안 뜨던 버그가 있었음
// (버그 리포트 1번). 여기서 그 상태를 직접 관리함 — 체크될 때마다 "선택한 필터" 줄에 칩을 추가하고,
// 전체해제 버튼도 보이게 함. 새로고침하면 그대로 리셋되는 건 의도한 동작(DB에 없는 필터라 정직하게 둠).
document.addEventListener('DOMContentLoaded', function () {
	var clearAllBtn = document.getElementById('filterClearAll');
	var selectedBar = document.getElementById('filterSelectedBar');
	var chipList = document.getElementById('filterSelectedChips');

	if (!selectedBar || !chipList) return;

	// beforeColorGroups/afterColorGroups(is-inert 섹션) + 맨 위 로켓/무료배송 줄, 전부 장식용
	var decoBoxes = document.querySelectorAll(
		'.category-filter .is-inert input[type="checkbox"], .filter-top-row input[type="checkbox"]'
	);

	decoBoxes.forEach(function (box) {
		box.addEventListener('change', function () {
			syncDecoChip(box);
			syncClearAllVisibility();
		});
	});

	function syncDecoChip(box) {
		var existing = chipList.querySelector('[data-deco-for="' + box.dataset.decoId + '"]');

		if (box.checked && !existing) {
			var li = document.createElement('li');
			li.setAttribute('data-deco-for', box.dataset.decoId);

			var a = document.createElement('a');
			a.href = '#';
			a.textContent = box.dataset.label;

			var remove = document.createElement('span');
			remove.className = 'remove';
			remove.textContent = '삭제';
			a.appendChild(remove);

			a.addEventListener('click', function (e) {
				e.preventDefault();
				box.checked = false;
				syncDecoChip(box);
				syncClearAllVisibility();
			});

			li.appendChild(a);
			chipList.appendChild(li);
		} else if (!box.checked && existing) {
			existing.remove();
		}
	}

	function syncClearAllVisibility() {
		var anyDecoChecked = Array.prototype.some.call(decoBoxes, function (b) { return b.checked; });
		var hasServerFilter = selectedBar.dataset.hasServerFilter === 'true';
		var show = anyDecoChecked || hasServerFilter;

		selectedBar.hidden = !show;
		if (clearAllBtn) clearAllBtn.hidden = !show;
	}
});

// 더보기/접기 — 2026-09-03 추가. 위 블록(선택한 필터 칩)과는 별개 기능이라 DOMContentLoaded 리스너를
// 따로 둠 — selectedBar/chipList 가 없어서 위 블록이 일찍 return 해도 이건 항상 실행되게.
//
// coupang.com 을 Playwright 로 직접 열어서 확인한 실제 동작(원본은 URL 안 바뀌는 순수 클라이언트 토글):
// 체크박스 필터 그룹(색상/소재/네크라인/패턴·프린트/제조년도 등)은 처음엔 5개까지만 보이고,
// "+ 더보기" 를 누르면 나머지가 펼쳐지며 버튼 글자가 "- 접기"로 바뀜.
// 카테고리/평점/가격처럼 <a> 링크만 있는 목록은 원본도 접지 않으므로, "체크박스가 들어있는 ul"만 골라서 처리함
// (li 개수가 아니라 checkbox 유무로 판별 — 카테고리 ul은 12개나 되지만 원본이 안 접기 때문).
//
// ★ 2026-09-05 버그 수정 — "색상에서 더보기를 눌러 펼친 뒤 새로고침하면 다시 접힘".
// 원인: 펼침 상태를 이 파일 안 변수(folded)에만 들고 있었는데, 색상 필터를 하나 고르면
// 페이지가 통째로 다시 요청되면서(=우리 필터는 전부 서버 재요청 방식) 그 변수가 사라짐.
// 그래서 "색을 고르려고 더보기를 눌렀는데, 고르는 순간 다시 접혀버리는" 상황이 매번 생겼음.
//
// 고친 방법: 펼친 그룹의 제목을 sessionStorage 에 적어둠.
//  - sessionStorage 는 "그 탭이 열려있는 동안"만 남는 저장소라, 새로고침·필터 이동에는 살아남고
//    탭을 닫으면 깨끗이 지워짐 — 화면 설정 하나 기억하는 용도로 딱 맞음(로그인처럼 중요한 값 아님)
//  - 그룹을 구분하는 열쇠는 그룹 제목(h3) 글자. 카테고리가 바뀌면 필터 구성도 달라질 수 있어서
//    카테고리 번호(URL 의 categoryNo)까지 같이 열쇠에 넣음
//  - 브라우저 설정에 따라 sessionStorage 를 아예 못 쓰는 경우가 있어서 try/catch 로 감쌈
//    (못 쓰면 상태 유지만 안 될 뿐, 더보기 기능 자체는 그대로 동작)
document.addEventListener('DOMContentLoaded', function () {
	var FOLD_LIMIT = 5;
	var lists = document.querySelectorAll('.category-filter .filter-group ul');

	var categoryNo = new URLSearchParams(location.search).get('categoryNo') || '';
	var STORAGE_KEY = 'category-filter-unfolded:' + categoryNo;

	function loadUnfolded() {
		try {
			return JSON.parse(sessionStorage.getItem(STORAGE_KEY)) || [];
		} catch (e) {
			return [];
		}
	}

	function saveUnfolded(list) {
		try {
			sessionStorage.setItem(STORAGE_KEY, JSON.stringify(list));
		} catch (e) {
			/* 저장이 막힌 브라우저 — 상태 유지만 포기하고 그냥 넘어감 */
		}
	}

	var unfolded = loadUnfolded();

	lists.forEach(function (ul) {
		if (!ul.querySelector('input[type="checkbox"]')) return;

		var items = Array.prototype.slice.call(ul.children);
		if (items.length <= FOLD_LIMIT) return;

		// 그룹 제목 — <section class="filter-group"> 안의 h3. 제목 없는 그룹(맨 위 배송 줄)은 li 개수가
		// 적어서 여기까지 안 오지만, 혹시 몰라 제목이 없으면 순서 번호로 대신함
		var section = ul.closest('.filter-group');
		var heading = section && section.querySelector('h3');
		var groupKey = heading ? heading.textContent.trim() : ('group-' + Array.prototype.indexOf.call(lists, ul));

		var hiddenItems = items.slice(FOLD_LIMIT);

		// 새로고침 전에 펼쳐뒀던 그룹이면 처음부터 펼친 상태로 시작
		var folded = unfolded.indexOf(groupKey) === -1;
		hiddenItems.forEach(function (li) { li.hidden = folded; });

		var toggleLi = document.createElement('li');
		toggleLi.className = 'filter-fold-toggle';

		var toggleBtn = document.createElement('a');
		toggleBtn.href = '#';
		toggleBtn.textContent = folded ? '+ 더보기' : '- 접기';
		toggleLi.appendChild(toggleBtn);
		ul.appendChild(toggleLi);

		toggleBtn.addEventListener('click', function (e) {
			e.preventDefault();
			folded = !folded;
			hiddenItems.forEach(function (li) { li.hidden = folded; });
			toggleBtn.textContent = folded ? '+ 더보기' : '- 접기';

			// 펼쳤으면 목록에 추가, 접었으면 제거해서 다시 저장
			var idx = unfolded.indexOf(groupKey);
			if (!folded && idx === -1) unfolded.push(groupKey);
			if (folded && idx !== -1) unfolded.splice(idx, 1);
			saveUnfolded(unfolded);
		});
	});
});

// ============================================================
// 대분류(레벨1) 페이지 히어로 배너 — 2026-09-03 추가.
//
// 원본(coupang.com/np/categories/564653)은 Swiper 라이브러리를 쓰지만 우리는 라이브러리를 새로
// 넣지 않고, 메인페이지 배너들(main.js)과 똑같은 방식(setInterval + 클래스 토글)으로 만듦.
//   왼쪽 큰 배너 : 10장이 겹쳐 있고 4초마다 투명도로 바뀜(fade)
//   오른쪽 작은 띠: 10장이 세로로 쌓여 있고, 4초마다 한 칸씩 위로 밀림 + 화살표로도 넘길 수 있음
//
// 소분류·중분류 페이지엔 이 요소들이 아예 없으므로 아무것도 안 하고 그냥 끝남.
// ============================================================
document.addEventListener('DOMContentLoaded', function () {
	var SLIDE_MS = 4000;   // main.js 의 다른 배너들과 같은 간격(4초)으로 통일
	var ITEM_H = 59;       // 오른쪽 띠 한 칸 높이(원본 실측)
	var VISIBLE = 5;       // 324px 칸에 5칸 반이 보임 — 셀 때는 5칸으로

	var heroBox = document.querySelector('.top-hero');
	if (!heroBox) return;   // 소분류·중분류 페이지엔 이 영역이 없음

	var slides = heroBox.querySelectorAll('.hero-slide');
	var items = heroBox.querySelectorAll('.hero-side-item');
	var track = heroBox.querySelector('.hero-side-track');
	if (slides.length === 0) return;

	var idx = 0;        // 지금 보이는 큰 배너 번호
	var offset = 0;     // 띠를 몇 칸 밀어놨는지
	var timer = null;

	/*
	 * ★ 오른쪽 띠는 큰 배너의 "목차" 다 (원본 확인: 큰배너 활성 index 와 띠 활성 index 가 같았음).
	 *   그래서 배너가 넘어갈 때마다 같은 번호의 띠 칸을 파랗게(is-active) 바꾸고,
	 *   그 칸이 화면 밖으로 나가 있으면 보이도록 띠를 굴린다.
	 */
	function show(n) {
		idx = (n + slides.length) % slides.length;

		slides.forEach(function (s, i) { s.classList.toggle('is-active', i === idx); });
		items.forEach(function (it, i) { it.classList.toggle('is-active', i === idx); });

		// 지금 칸이 보이는 범위(offset ~ offset+4) 밖이면 그 칸이 보이게 띠를 옮김
		if (idx < offset) offset = idx;
		else if (idx > offset + VISIBLE - 1) offset = idx - VISIBLE + 1;

		var maxOffset = Math.max(0, items.length - VISIBLE);
		if (offset > maxOffset) offset = maxOffset;
		if (offset < 0) offset = 0;

		if (track) track.style.transform = 'translateY(' + (-offset * ITEM_H) + 'px)';
	}

	function start() {
		stop();
		timer = setInterval(function () { show(idx + 1); }, SLIDE_MS);
	}

	function stop() {
		clearInterval(timer);
	}

	// 띠 아래 ∧ ∨ 화살표 — 이전/다음 배너로 넘김
	var prevBtn = heroBox.querySelector('.hero-side-prev');
	var nextBtn = heroBox.querySelector('.hero-side-next');
	if (prevBtn) prevBtn.addEventListener('click', function () { show(idx - 1); start(); });
	if (nextBtn) nextBtn.addEventListener('click', function () { show(idx + 1); start(); });

	// 띠 칸에 마우스만 올려도 그 배너로 바뀜 — 메인페이지 히어로 썸네일(main.js 59줄)과 같은 방식.
	// (원본은 이 칸들이 기획전 페이지로 가는 진짜 링크인데 우리는 그 페이지가 없어서 href="#" 임)
	items.forEach(function (it, i) {
		it.addEventListener('mouseenter', function () { show(i); });
	});

	// 배너 위에 마우스를 올려둔 동안은 자동 넘김을 멈춤(보는 중에 바뀌면 불편함 — main.js 와 같은 규칙)
	heroBox.addEventListener('mouseenter', stop);
	heroBox.addEventListener('mouseleave', start);

	show(0);
	start();
});
