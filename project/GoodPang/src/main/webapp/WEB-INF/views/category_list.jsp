<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${categoryName} | 굿팡</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/category.css">
</head>

<body class="page-category">

	<%-- 상단 띠배너 — index.html 21~27번 줄과 완전히 동일한 마크업 재사용(2026-09-01).
	     header.jsp 안에는 이 배너가 없고, index.html 이 header include 앞에 직접 넣어둔 걸 그대로 가져옴.
	     CSS(.coupang-top-banner, .top-banner-placeholder)와 배너 이미지(top-1.jpg/top-2.jpg)는
	     common.css/main.css 에 이미 있는 걸 그대로 씀 — 새로 만든 것 없음 --%>
	<div class="coupang-top-banner">
		<div class="banner-middle">
			<a href="#"> <span class="top-banner-placeholder"
				style="background: #b5f3fe; color: #0b3d5c"> 오늘 밤 12시까지 주문해도
					로켓배송은 내일 도착! <i class="arrow"></i>
			</span></a> <a href="#"> <span class="top-banner-placeholder"
				style="background: #80daff; color: #0b2d45"> 김원훈의 모발관리템 <i
					class="arrow"></i></span></a>
		</div>
	</div>

	<jsp:include page="/inc/header.jsp" />

	<%-- 정렬/페이지/평점/가격 링크에 항상 같이 실어야 하는 값들. 새 링크 만들 때마다 이어붙임.
	     색상은 다중선택(2026-09-01)이라 선택된 개수만큼 color= 를 반복해서 실음.
	     <c:param> 으로 URL 인코딩까지 여기서 미리 해둠(한글이라 직접 이어붙이면 깨짐) --%>
	<%-- ★ listSize 는 여기 baseUrl 에 안 넣음 — 넣으면 60/120 토글 링크가 자기 값을 뒤에 또 붙일 때
	     같은 파라미터가 두 번(listSize=기존값&...&listSize=새값) 들어가서 서블릿이 앞의(기존) 값을
	     먼저 읽어버려 토글이 안 먹힘. 대신 아래 각 링크마다 "&listSize=${listSize}" 를 직접 붙임
	     (60/120 토글 두 줄만 리터럴 60/120 을 씀) --%>
	<c:url var="baseUrl" value="/category">
		<c:param name="categoryNo" value="${categoryNo}" />
		<c:forEach var="c" items="${selectedColors}">
			<c:param name="color" value="${c}" />
		</c:forEach>
	</c:url>

	<%-- 필터가 하나라도 선택돼 있는지 — "전체해제" 버튼과 "선택한 필터" 줄을 보여줄지 결정하는 데만 씀 --%>
	<c:set var="hasActiveFilter"
		value="${not empty selectedColors || rating > 0 || minPrice > 0 || not empty maxPrice}" />

	<%-- ==================================================
	     브레드크럼 — 원본 재실측(2026-09-01): 화면 끝까지 이어지는 회색 띠(#F9FAFB,
	     border-bottom #DFE3E8) 안에 있음. .category-page(max-width:1300px) 밖에 따로 둬야
	     배경만 풀폭으로 퍼지고 글자는 그 안에서 1300px 폭으로 맞춰짐 --%>
	<div class="breadcrumb-bar">
		<nav class="breadcrumb" aria-label="브레드크럼">
			<a href="${pageContext.request.contextPath}/">굿팡 홈</a>
			<c:forEach var="crumb" items="${breadcrumb}">
				<span class="sep">&gt;</span>
				<%-- 2026-09-02: 소분류(현재 페이지)도 클릭 가능하게 링크로 바꿈(사용자 요청) —
				     "current" 클래스는 그대로 둬서 색상 등 스타일 구분은 유지.
				     2026-09-03: current 판정을 "레벨3이면" → "지금 보고 있는 번호와 같으면" 으로 바꿈.
				     중분류 페이지는 마지막 칸이 레벨2라 예전 조건으론 아무 칸도 굵게 안 됐음 --%>
				<a href="${pageContext.request.contextPath}/category?categoryNo=${crumb.categoryNo}"
					class="${crumb.categoryNo == categoryNo ? 'current' : ''}">${crumb.categoryName}</a>
			</c:forEach>
		</nav>
	</div>

	<main class="category-page">

		<div class="category-body">

			<%-- ==================================================
			     왼쪽 필터 사이드바
			     ================================================== --%>
			<aside class="category-filter">

				<div class="filter-title-row">
					<h2 class="filter-title">필터</h2>
					<%-- 실제 쿠팡도 필터 하나라도 고르면 "전체해제" 버튼이 뜸(2026-09-01 Playwright 재확인).
					     색상/평점/가격처럼 서버가 아는 필터만 초기화 대상 — categoryNo 만 남기고 나머지 다 뗀 URL로 이동.
					     ★ 2026-09-01: 브랜드/핏 같은 장식용 체크박스만 골랐을 때도 이 버튼이 떠야 해서(버그 리포트 1번),
					     서버가 모르는 상태라 JS가 관리할 수 있게 항상 DOM에 두고 hidden 속성만 토글하는 식으로 바꿈 --%>
					<%-- listSize 는 필터가 아니라 "몇 개씩 볼지" 화면 설정이라 전체해제해도 유지(2026-09-03) --%>
					<a href="${pageContext.request.contextPath}/category?categoryNo=${categoryNo}&listSize=${listSize}"
						id="filterClearAll" class="filter-clear-all" ${hasActiveFilter ? '' : 'hidden'}>전체해제</a>
				</div>

				<%-- 원본은 "필터" 제목 바로 아래, 소제목 없이 로켓/무료배송 체크박스가 옴.
				     실제 로켓 배지 이미지는 승인 안 된 도메인이라 못 받아와서 글자만(2026-08-31).
				     ★ 2026-09-01: 이 줄은 DB 연동이 없는 장식용 필터임 — 그래도 "체크박스가 있으면 눌렀을 때
				     체크 표시는 나야 한다"는 요청으로 name 없는 진짜 <input type="checkbox"> 를 넣음. name 이
				     없어서 폼에 실리지도 않고 서버에도 안 감. data-deco-id/data-label 은 category.js 가
				     "선택한 필터" 줄에 칩을 추가하려고 씀(버그 리포트 1번 — 체크만 되고 전체해제/선택한 필터에
				     안 잡히던 문제 수정) --%>
				<%-- 2026-09-05: 원본 재실측 결과 이 줄도 "제목(h5)만 없는 그룹"이라 나머지 필터 그룹과
				     똑같은 상자(padding 10px 10px 8px + 아래 1px 회색선)를 씀 — 그래서 section.filter-group
				     으로 감싸서 그 규칙을 그대로 쓰게 함(전엔 .filter-top-row 에 따로 여백을 주고 있었음) --%>
				<%-- ★ 2026-09-05 재실측으로 구조 교체(사용자 지적: "R.LUX부터 로켓직구만 보기까지 색상이 다르고
				     배치도 조금 다름"). 원본은 6개를 평평하게 늘어놓은 게 아니라 아래처럼 2단으로 되어 있음:

				       □ [로켓 배지]                  ← 로켓 전체(3종 한번에)
				         ┌ 연회색(#FAFAFA) 띠 ─────┐
				         □ R.LUX 만 보기   ← 흐림(못 고름)
				         □ 로켓 만 보기
				         □ 로켓직구 만 보기
				         └──────────────────┘
				       □ [탑브랜드 배지]
				       □ 무료배송

				     그래서 서버가 주던 평평한 문자열 배열(topFilterItems)은 더 못 쓰고 여기서 직접 씀
				     (어차피 DB 연동이 없는 장식용 줄이라 서버가 알 필요가 없는 값임 — CategoryServlet 에서도 지움).
				     배지 이미지는 로켓만 우리 폴더에 있고(logo_rocket_filter_medium.png) R.LUX·직구·C.에비뉴는
				     아직 없어서 그 셋은 글자로 둠. data-deco-id/data-label 은 예전처럼 category.js 가
				     "선택한 필터" 칩을 만드는 데 씀 --%>
				<section class="filter-group">
					<ul class="filter-top-row">
						<li>
							<label><input type="checkbox" data-deco-id="top-rocket-all" data-label="로켓"><i class="filter-function-bar-asset"></i><span class="service-badge"><img src="${pageContext.request.contextPath}/images/icons/logo_rocket_filter_medium.png" alt="로켓"></span></label>
							<%-- ★ 2026-09-05 3차 수정. 원본은 이 줄들이 전부 "브랜드 로고 이미지 + '만 보기' 글자" 인데
							     우리는 그 로고 이미지가 없어서 글자로 대신 씀. 그래서 최소한 색이라도 맞추려고
							     원본 로고 이미지를 화면에서 캡처해 픽셀 색을 직접 뽑아왔음(눈대중 아님):
							       로켓배송 #459DBF / 로켓직구 #9C27B0 / C.에비뉴 #41297F / R.LUX 검정(굵게)
							     그래서 브랜드 이름 부분만 <span class="brand-*"> 로 감싸고 "만 보기"는 검정 그대로 둠.
							     ★ 버그 수정: R.LUX 체크박스가 안 눌리던 문제 — 원본이 pointer-events:none 인 걸 그대로
							     따라해서 disabled 를 걸어놨었음. 이 프로젝트 규칙("체크박스가 있으면 눌렀을 때 체크
							     표시는 난다", 2026-09-01)에 맞춰 disabled 를 떼고 다른 항목과 똑같이 눌리게 함
							     (흐린 색(opacity 0.4)은 원본 모습이라 그대로 둠) --%>
							<%-- 2026-09-03: R.LUX/로켓배송/로켓직구 3개는 색만 입힌 글자였던 걸 원본 로고 이미지로 교체
							     (coupang.com 에서 직접 받은 실제 로고 파일 — images/category/badge_*.png).
							     brand-rlux/brand-rocket/brand-jikgu 색상 규칙은 category.css 에서 주석 처리해 둠 --%>
							<ul class="filter-service-sub">
								<%-- <li><label class="is-dimmed"><input type="checkbox" data-deco-id="top-rlux" data-label="R.LUX 만 보기"><i class="filter-function-bar-asset"></i><span class="brand-rlux">R.LUX</span> 만 보기</label></li> --%>
								<li><label class="is-dimmed"><input type="checkbox" data-deco-id="top-rlux" data-label="R.LUX 만 보기"><i class="filter-function-bar-asset"></i><span class="service-badge sub is-rlux"><img src="${pageContext.request.contextPath}/images/category/badge_rlux.png" alt="R.LUX"></span>만 보기</label></li>
								<%-- <li><label><input type="checkbox" data-deco-id="top-rocket" data-label="로켓배송 만 보기"><i class="filter-function-bar-asset"></i><span class="brand-rocket">로켓배송</span> 만 보기</label></li> --%>
								<li><label><input type="checkbox" data-deco-id="top-rocket" data-label="로켓배송 만 보기"><i class="filter-function-bar-asset"></i><span class="service-badge sub"><img src="${pageContext.request.contextPath}/images/category/badge_rocket.png" alt="로켓배송"></span>만 보기</label></li>
								<%-- <li><label><input type="checkbox" data-deco-id="top-jikgu" data-label="로켓직구 만 보기"><i class="filter-function-bar-asset"></i><span class="brand-jikgu">로켓직구</span> 만 보기</label></li> --%>
								<li><label><input type="checkbox" data-deco-id="top-jikgu" data-label="로켓직구 만 보기"><i class="filter-function-bar-asset"></i><span class="service-badge sub"><img src="${pageContext.request.contextPath}/images/category/badge_jikgu.png" alt="로켓직구"></span>만 보기</label></li>
							</ul>
						</li>
						<%-- <li><label><input type="checkbox" data-deco-id="top-topbrand" data-label="C.에비뉴"><i class="filter-function-bar-asset"></i><span class="brand-cavenue">C.에비뉴</span></label></li> --%>
						<li><label><input type="checkbox" data-deco-id="top-topbrand" data-label="C.에비뉴"><i class="filter-function-bar-asset"></i><span class="service-badge sub is-cavenue"><img src="${pageContext.request.contextPath}/images/category/badge_cavenue.svg" alt="C.에비뉴"></span></label></li>
						<li><label><input type="checkbox" data-deco-id="top-free" data-label="무료배송"><i class="filter-function-bar-asset"></i><span>무료배송</span></label></li>
					</ul>
				</section>

				<%-- 카테고리 필터 — 실제로 동작함(클릭하면 그 카테고리로 이동) --%>
				<section class="filter-group">
					<h3>카테고리</h3>
					<ul>
						<%-- 2026-09-03: 중분류면 자식(티셔츠·바지…), 소분류면 예전처럼 형제 —
						     어느 쪽을 담을지는 CategoryServlet 이 정해서 sidebarCategories 로 내려줌.
						     맨 아래 "함께 본 카테고리"는 계속 siblingCategories 를 씀(원본도 거긴 형제) --%>
						<c:forEach var="sib" items="${sidebarCategories}">
							<li>
								<a href="${pageContext.request.contextPath}/category?categoryNo=${sib.categoryNo}"
									class="${sib.categoryNo == categoryNo ? 'selected' : ''}">
									${sib.categoryName}
								</a>
							</li>
						</c:forEach>
					</ul>
				</section>

				<%-- 여기부터 실제 DB에 속성 컬럼이 없어서 화면만 있고 동작은 안 하는 필터들
				     (2026-08-30 확정 — 원본과 구성은 맞추되, 데이터가 없는 걸 있는 척 만들지 않음).
				     원본 실측(2026-08-31, STRUCTURE.md) 순서 그대로: 카테고리→브랜드→상품상태→색상→핏→...→별점→가격 --%>
				<c:forEach var="entry" items="${beforeColorGroups}" varStatus="gst">
					<section class="filter-group is-inert">
						<h3>${entry.key}</h3>
						<ul>
							<c:forEach var="item" items="${entry.value}" varStatus="ist">
								<li><label><input type="checkbox" data-deco-id="before-${gst.index}-${ist.index}" data-label="${item}"><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
							</c:forEach>
						</ul>
					</section>
				</c:forEach>

				<%-- 색상 — 하드코딩 아님, PRODUCT_OPTION 에 실제 등록된 값만 나옴(2026-08-30 확정).
				     2026-09-01: 다중선택으로 바꿈(실제 쿠팡 Playwright 재확인 — 여러 색 동시 선택 가능,
				     선택한 색상끼리는 OR). 체크박스 하나 바꿀 때마다 폼을 통째로 자동 제출해서 서버에 다시 물어봄
				     (색상 조합 자체가 DB 조회 결과라 클라이언트에서만 처리할 수 없음 — 다른 실동작 필터와 같은 원칙) --%>
				<c:if test="${not empty colorOptions}">
					<section class="filter-group">
						<h3>색상</h3>
						<form id="colorFilterForm" method="get" action="${pageContext.request.contextPath}/category">
							<input type="hidden" name="categoryNo" value="${categoryNo}">
							<input type="hidden" name="sort" value="${sort}">
							<input type="hidden" name="minPrice" value="${minPrice}">
							<input type="hidden" name="maxPrice" value="${maxPrice}">
							<input type="hidden" name="rating" value="${rating}">
							<input type="hidden" name="listSize" value="${listSize}">
							<ul class="filter-chip-list">
								<c:forEach var="color" items="${colorOptions}">
									<li>
										<label>
											<input type="checkbox" name="color" value="${color}"
												onchange="document.getElementById('colorFilterForm').submit()"
												${selectedColorMap[color] ? 'checked' : ''}>
											<i class="filter-function-bar-asset"></i><span>${color}</span>
										</label>
									</li>
								</c:forEach>
							</ul>
						</form>
					</section>
				</c:if>

				<c:forEach var="entry" items="${afterColorGroups}" varStatus="gst">
					<section class="filter-group is-inert">
						<h3>${entry.key}</h3>
						<ul>
							<c:forEach var="item" items="${entry.value}" varStatus="ist">
								<li><label><input type="checkbox" data-deco-id="after-${gst.index}-${ist.index}" data-label="${item}"><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
							</c:forEach>
						</ul>
					</section>
				</c:forEach>

				<%-- 평점 — 실제로 동작함. 원본은 맨 끝에서 두 번째.
				     ★ 2026-09-05: 원본 재실측 결과 "4점 이상"~"1점 이상" 앞에는 항상 별 이미지가 붙어 있음
				     (원본은 69×12 스프라이트를 점수별 background-position 으로 바꿔 씀 —
				      ref/category/필터사이드바_원본_20260905.png 맨 아래쪽 참고).
				     우리는 그 CDN 이미지를 못 받으므로 상품카드에서 이미 쓰는 우리 별 이미지를 재사용하고,
				     채워진 별 개수는 em 의 폭(별 1개 = 20%)으로 표현함 — 카드 별점과 똑같은 방식.
				     "별점 전체"에는 원본도 별이 없음 --%>
				<section class="filter-group">
					<h3>별점</h3>
					<%-- JSP EL 은 리스트 리터럴이 없어서 5개를 그냥 하나씩 적음(0=전체) --%>
					<ul>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=0" class="${rating == 0 ? 'selected' : ''}">별점 전체</a></li>
						<c:forEach var="r" begin="1" end="4" step="1">
							<c:set var="star" value="${5 - r}" />
							<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${star}"
									class="${rating == star ? 'selected' : ''}"><span class="star-rating" aria-hidden="true"><em style="width:${star * 20}%"></em></span>${star}점 이상</a></li>
						</c:forEach>
					</ul>
				</section>

				<%-- 가격대 — 실제로 동작함. 쿠팡 원본 구간(9천 단위)을 그대로 씀. 원본은 맨 끝 --%>
				<section class="filter-group">
					<h3>가격</h3>
					<ul>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&rating=${rating}&minPrice=0&maxPrice="
								class="${empty maxPrice ? 'selected' : ''}">전체</a></li>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&rating=${rating}&minPrice=0&maxPrice=9000"
								class="${maxPrice == 9000 ? 'selected' : ''}">9,000원 이하</a></li>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&rating=${rating}&minPrice=9000&maxPrice=18000"
								class="${minPrice == 9000 && maxPrice == 18000 ? 'selected' : ''}">9,000원~18,000원</a></li>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&rating=${rating}&minPrice=18000&maxPrice=27000"
								class="${minPrice == 18000 && maxPrice == 27000 ? 'selected' : ''}">18,000원~27,000원</a></li>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&rating=${rating}&minPrice=27000&maxPrice=36000"
								class="${minPrice == 27000 && maxPrice == 36000 ? 'selected' : ''}">27,000원~36,000원</a></li>
						<li><a href="${baseUrl}&listSize=${listSize}&sort=${sort}&rating=${rating}&minPrice=36000&maxPrice="
								class="${minPrice == 36000 && empty maxPrice ? 'selected' : ''}">36,000원 이상</a></li>
					</ul>
					<%-- 2026-09-01: 여기 색상 hidden input 이 빠져있어서, 가격을 직접 입력해서 검색하면
					     선택해둔 색상이 통째로 날아가는 버그가 있었음 — 다른 필터 링크들처럼 선택된 색상을
					     그대로 실어서 보냄 --%>
					<form class="filter-price-direct" method="get" action="${pageContext.request.contextPath}/category">
						<input type="hidden" name="categoryNo" value="${categoryNo}">
						<input type="hidden" name="sort" value="${sort}">
						<input type="hidden" name="rating" value="${rating}">
						<input type="hidden" name="listSize" value="${listSize}">
						<c:forEach var="c" items="${selectedColors}">
							<input type="hidden" name="color" value="${c}">
						</c:forEach>
						<%-- 2026-09-05: placeholder("최소"/"최대") 글자가 잘린다는 지적 — 원본을 다시 보니
						     입력칸에 placeholder 가 아예 없고(빈 칸) title 속성만 있음. 칸 폭이 44px 이라
						     원본도 글자를 넣을 자리가 없는 것. 원본대로 placeholder 를 빼고, 대신 화면낭독기용
						     title/aria-label 을 남겨서 무슨 칸인지는 알 수 있게 함 --%>
						<%-- 2026-09-05: type="number" 는 브라우저가 칸 오른쪽에 위아래 화살표(스피너)를 그려주는데,
						     44px 짜리 좁은 칸이라 그 버튼이 자리를 다 잡아먹음 → 빼달라는 요청.
						     원본도 type="text" + maxlength="10" 이라 그대로 따라감.
						     inputmode="numeric" 은 휴대폰에서 숫자 자판이 뜨게 하는 것(모양은 안 바뀜).
						     숫자가 아닌 값이 들어와도 CategoryServlet 의 parseIntOrDefault 가 기본값으로 넘김 --%>
						<input type="text" name="minPrice" maxlength="10" inputmode="numeric" title="최소 가격" aria-label="최소 가격">
						<span>~</span>
						<input type="text" name="maxPrice" maxlength="10" inputmode="numeric" title="최대 가격" aria-label="최대 가격">
						<button type="submit">검색</button>
					</form>
				</section>

			</aside>

			<%-- ==================================================
			     오른쪽 상품 목록
			     ================================================== --%>
			<section class="category-list">

				<%-- 카테고리 제목 — 2026-09-03 추가. 원본 재확인(Playwright, browser_evaluate): 24px/700/#000000,
				     정렬줄과 12px 떨어져 있음. 그동안 아예 빠져있었음(브레드크럼의 소분류 이름과 헷갈려서 안 넣었던 듯) --%>
				<%-- 2026-09-03: breadcrumb[2](소분류 칸)를 집어 쓰던 걸 categoryName 으로 바꿈 —
				     중분류 페이지는 브레드크럼이 2칸뿐이라 [2]번이 없어서 제목이 빈칸이 됐음 --%>
				<h1 class="category-title">${categoryName}</h1>

				<%-- ==================================================
				     중분류(레벨2) 페이지에만 나오는 영역 — 2026-09-03 추가.
				     원본 쿠팡(www.coupang.com/np/categories/502993, Playwright 실측) 확인 결과
				     제목 아래에 ① 하위 카테고리 원형 타일 ② 링크 배너 ③ 프로모션 배너 3장 순서로 옴.

				     ★ 원본은 이 타일 그리드가 DOM 이 아니라 "배너 이미지 1장(1080x623) + 퍼센트 좌표로 얹은
				       투명 <a> 11개" 였음(옛날 이미지맵 방식). 우리는 카테고리가 DB 에 있으니 그대로 흉내내지
				       않고 타일을 하나씩 그림 — 카테고리가 늘면 화면도 같이 늘어나야 하니까.
				       대신 원 크기·간격·글자는 원본 이미지를 픽셀로 재서 맞춤(category.css 참고).

				     소분류 페이지에서는 isMidCategory 가 false 라 이 블록이 통째로 안 나옴 --%>
				<c:if test="${isMidCategory}">
					<div class="mid-extra">

						<c:if test="${not empty categoryTiles}">
							<ul class="category-tiles">
								<c:forEach var="tile" items="${categoryTiles}">
									<li>
										<a href="${pageContext.request.contextPath}/category?categoryNo=${tile.categoryNo}">
											<span class="tile-thumb">
												<img src="${pageContext.request.contextPath}/images/category/tile_${tile.categoryNo}.png" alt="">
											</span>
											<span class="tile-name">${tile.categoryName}</span>
										</a>
									</li>
								</c:forEach>
							</ul>
						</c:if>

						<%-- 배너 4장 — 원본에서 받아온 이미지 그대로. 아직 연결할 기획전 페이지가 없어서 링크는 비워둠
						     (href="#" 이라 눌러도 아무 데도 안 감). 나중에 기획전이 생기면 여기만 바꾸면 됨.
						     2026-09-06: 이 4장은 패션 페이지에서 캡처한 그림이라 패션(대분류 1) 밑의 중분류에서만 나옴.
						     뷰티 밑 중분류(스킨케어 등)에선 hasMidBanners 가 false 라 이 블록이 통째로 빠짐 --%>
						<c:if test="${hasMidBanners}">
						<div class="mid-banners">
							<a href="#" class="banner-quicklinks">
								<img src="${pageContext.request.contextPath}/images/category/banner_quicklinks.png" alt="쿠팡 추천 모음">
							</a>
							<a href="#">
								<img src="${pageContext.request.contextPath}/images/category/banner_promo1.png" alt="기획전 배너">
							</a>
							<a href="#">
								<img src="${pageContext.request.contextPath}/images/category/banner_promo2.png" alt="기획전 배너">
							</a>
							<a href="#">
								<img src="${pageContext.request.contextPath}/images/category/banner_promo3.png" alt="기획전 배너">
							</a>
						</div>
						</c:if>

					</div>
				</c:if>

				<%-- ==================================================
				     대분류(레벨1) 페이지에만 나오는 영역 — 2026-09-03 추가.
				     원본(coupang.com/np/categories/564653, Playwright 실측) 확인 결과 제목 바로 아래에
				     ① 히어로 배너 캐러셀 ② 브랜드관 배너 캐러셀이 옴. 근데 원본에서 실제 DOM을 뜯어보니
				     둘 다 접속할 때마다 바뀌는 광고 마켓플레이스 캐러셀(광고주별 링크가 계속 바뀜)이라
				     전부 못 가져오고, 대표 이미지 몇 장만 정지 배너로 캡처해서 씀
				     (원본 이미지 그대로 받음 — static.coupangcdn.com/image/bannerunit/...).
				     ★ 2026-09-06: 이 배너들은 전부 "패션의류/잡화" 페이지에서 캡처한 그림이라(l1_tiles.png 는
				       칸 이름까지 그림에 박혀 있음) 뷰티·식품 같은 다른 대분류에 쓰면 엉뚱한 화면이 됨.
				       그래서 조건을 isTopCategory(레벨1이기만 하면) → hasTopBanners(배너 자료가 있는 대분류)로 바꿈.
				       자료가 없는 대분류는 제목 + 정렬바 + 상품 목록만 나온다.
				     중분류/소분류와 배너 자료가 없는 대분류에서는 이 블록이 통째로 안 나옴 --%>
				<c:if test="${hasTopBanners}">
					<div class="top-extra">

						<%-- ① 히어로 영역 — 원본 실측: 1030px 칸 안에 두 덩어리가 나란히 있음.
						       왼쪽 768×324 : 큰 배너 10장이 4초마다 은은하게 바뀌는 캐러셀(원본은 Swiper 의 fade 모드)
						       오른쪽 261×324 : 작은 배너(261×59) 10장이 세로로 쌓여 있고 위아래로 넘어감.
						                        맨 아래 260×29 짜리 옅은 바에 ∧ ∨ 화살표가 가운데 있음
						     넘기는 동작은 category.js 가 함(라이브러리 안 씀 — main.js 의 배너들과 같은 방식).
						     히어로 아래 여백 30px 도 실측값 --%>
						<div class="top-hero">

							<div class="top-hero-main">
								<c:forEach var="n" begin="1" end="10">
									<a href="#" class="hero-slide"><img src="${pageContext.request.contextPath}/images/category/l1_hero_${n}.png" alt="기획전 배너"></a>
								</c:forEach>
							</div>

							<%-- 작은 배너 이미지는 522×58 짜리 "2컷 스프라이트" 임 — 왼쪽 절반이 평소 모습,
							     오른쪽 절반이 강조된 모습(파란 배경). 그래서 칸을 261px 로 잘라놓고
							     마우스를 올리면 이미지를 왼쪽으로 261px 밀어서 오른쪽 컷이 보이게 함(CSS) --%>
							<div class="top-hero-side">
								<div class="hero-side-track">
									<c:forEach var="n" begin="1" end="10">
										<a href="#" class="hero-side-item"><img src="${pageContext.request.contextPath}/images/category/l1_side_${n}.png" alt="기획전 배너"></a>
									</c:forEach>
								</div>
								<div class="hero-side-nav">
									<button type="button" class="hero-side-prev" aria-label="이전 배너"></button>
									<button type="button" class="hero-side-next" aria-label="다음 배너"></button>
								</div>
							</div>

						</div>

						<%-- ② 그 아래는 1080px 폭 배너가 세로로 "간격 없이" 쭉 붙어 있음(실측: 446→375→237×9→124→412×4).
						     원본 순서·크기 그대로 옮김.
						     ★ 첫 장(l1_tiles)은 원본처럼 "이미지 1장 + 투명 <a> 10개" 로 만든 이미지맵.
						       칸 위치는 이미지에서 원을 직접 검출해 잰 값(원 지름 110px, 가로 간격 132px,
						       시작 x=221, 윗줄 y=59 / 아랫줄 y=237 — 중분류 타일과 같은 규격)을 % 로 바꿔 CSS 에 넣음.
						       어느 칸을 어느 카테고리로 보낼지는 CategoryServlet 이 이름으로 찾아서 tileSlots 로 내려줌 —
						       우리 DB 에 없는 칸(C.에비뉴/C.스트리트 등)은 href 없는 빈 칸이라 눌러도 아무 일 안 남 --%>
						<div class="top-banners">

							<div class="top-tiles">
								<img src="${pageContext.request.contextPath}/images/category/l1_tiles.png" alt="카테고리 바로가기">
								<div class="tile-hits">
									<c:forEach var="slot" items="${tileSlots}">
										<c:choose>
											<c:when test="${slot.categoryNo > 0}">
												<a class="tile-hit" href="${pageContext.request.contextPath}/category?categoryNo=${slot.categoryNo}" title="${slot.categoryName}"></a>
											</c:when>
											<c:otherwise>
												<a class="tile-hit is-empty"></a>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</div>
							</div>

							<img src="${pageContext.request.contextPath}/images/category/l1_promo_cards.png" alt="기획전 모음">
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_1.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_2.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_3.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_4.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_5.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_6.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_7.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_8.png" alt="기획전 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_band_9.png" alt="기획전 배너"></a>
							<img src="${pageContext.request.contextPath}/images/category/l1_brand_title.png" alt="브랜드관">
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_brand_1.png" alt="브랜드관 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_brand_2.png" alt="브랜드관 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_brand_3.png" alt="브랜드관 배너"></a>
							<a href="#"><img src="${pageContext.request.contextPath}/images/category/l1_brand_4.png" alt="브랜드관 배너"></a>
						</div>

					</div>
				</c:if>

				<%-- 이것도 마찬가지로 EL 리스트 리터럴 대신 4개를 그냥 하나씩 적음 --%>
				<div class="sort-bar">
					<ul>
			     	    <li class="${sort == 'RANKING' ? 'Sort_selected' : ''}">
            			  <a href="${baseUrl}&listSize=${listSize}&sort=RANKING&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">쿠팡랭킹순</a>
       				    </li>
						<li class="${sort == 'LATEST' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&listSize=${listSize}&sort=LATEST&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">최신순</a>
						</li>
						<li class="${sort == 'PRICE_ASC' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&listSize=${listSize}&sort=PRICE_ASC&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">낮은가격순</a>
						</li>
						<li class="${sort == 'PRICE_DESC' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&listSize=${listSize}&sort=PRICE_DESC&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">높은가격순</a>
						</li>
						<li class="${sort == 'SALE_COUNT' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&listSize=${listSize}&sort=SALE_COUNT&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">판매량순</a>
						</li>
					</ul>
					<%-- 보기 개수 60/120 — 2026-09-03 2차 수정: 처음엔 정렬(ul.Sort_sort)처럼 두 개를 나란히
					     늘어놓았는데, 실제 원본은 그게 아니라 "마우스 올리면 그 밑에 펼쳐지는" 호버 드롭다운이라는
					     지적을 받아 다시 실측(browser_evaluate + hover)해서 구조를 고침:
					     - 평소엔 현재 선택된 값 하나만 보임(li.selected 만 display, 나머지는 display:none)
					     - .list-size-dropdown 에 마우스를 올리면 두 줄 다 세로로 펼쳐짐(순수 CSS :hover, JS 없음)
					     - 클릭하면 그동안처럼 페이지 전체가 다시 요청됨(원본도 마찬가지 — STRUCTURE.md 10장) --%>
					<div class="list-size-dropdown">
						<ul>
							<li class="${listSize == 60 ? 'selected' : ''}">
								<a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&listSize=60">60개씩 보기</a>
							</li>
							<li class="${listSize == 120 ? 'selected' : ''}">
								<a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&listSize=120">120개씩 보기</a>
							</li>
						</ul>
					</div>
				</div>

				<%-- 선택한 필터 — 실제 쿠팡 재확인(2026-09-01 Playwright): 정렬줄 바로 아래, 상품목록 위에 옴.
				     칩 하나 지우면 그 값만 뺀 URL로 재요청, 나머지 선택은 그대로 유지.
				     ★ 장식용 체크박스(브랜드/핏 등)를 골랐을 때도 여기 칩이 떠야 해서(버그 리포트 1번),
				     서버가 모르는 상태라 항상 DOM에 두고 hidden 만 토글 — data-has-server-filter 는
				     "서버 필터가 있어서 원래도 보여야 하는지"를 category.js 에 알려주는 용도 --%>
				<div id="filterSelectedBar" class="filter-selected-bar" data-has-server-filter="${hasActiveFilter}" ${hasActiveFilter ? '' : 'hidden'}>
					<span class="filter-selected-label">선택한 필터:</span>
					<ul id="filterSelectedChips" class="filter-selected-chips">
						<c:forEach var="color" items="${selectedColors}">
							<c:url var="removeColorUrl" value="/category">
								<c:param name="categoryNo" value="${categoryNo}" />
								<c:param name="listSize" value="${listSize}" />
								<c:param name="sort" value="${sort}" />
								<c:param name="minPrice" value="${minPrice}" />
								<c:param name="maxPrice" value="${maxPrice}" />
								<c:param name="rating" value="${rating}" />
								<c:forEach var="other" items="${selectedColors}">
									<c:if test="${other ne color}">
										<c:param name="color" value="${other}" />
									</c:if>
								</c:forEach>
							</c:url>
							<li><a href="${removeColorUrl}">${color}<span class="remove">삭제</span></a></li>
						</c:forEach>

						<c:if test="${rating > 0}">
							<c:url var="removeRatingUrl" value="/category">
								<c:param name="categoryNo" value="${categoryNo}" />
								<c:param name="listSize" value="${listSize}" />
								<c:param name="sort" value="${sort}" />
								<c:param name="minPrice" value="${minPrice}" />
								<c:param name="maxPrice" value="${maxPrice}" />
								<c:param name="rating" value="0" />
								<c:forEach var="c" items="${selectedColors}"><c:param name="color" value="${c}" /></c:forEach>
							</c:url>
							<li><a href="${removeRatingUrl}">${rating}점 이상<span class="remove">삭제</span></a></li>
						</c:if>

						<c:if test="${minPrice > 0 || not empty maxPrice}">
							<c:url var="removePriceUrl" value="/category">
								<c:param name="categoryNo" value="${categoryNo}" />
								<c:param name="listSize" value="${listSize}" />
								<c:param name="sort" value="${sort}" />
								<c:param name="rating" value="${rating}" />
								<c:param name="minPrice" value="0" />
								<c:forEach var="c" items="${selectedColors}"><c:param name="color" value="${c}" /></c:forEach>
							</c:url>
							<li><a href="${removePriceUrl}">가격<span class="remove">삭제</span></a></li>
						</c:if>
					</ul>
				</div>

				<c:choose>
					<c:when test="${empty products}">
						<p class="empty-message">조건에 맞는 상품이 없습니다.</p>
					</c:when>
					<c:otherwise>
						<%-- 무료배송 기준 금액 — 실제 쿠팡 정책이 "로켓배송 상품 19,800원 이상 구매 시 무료배송"이고,
						     우리 상세페이지(product.jsp 300줄)와 장바구니(cart.jsp 41줄)도 이미 같은 숫자를 쓰고 있어서 맞춤.
						     숫자를 카드 안에 직접 적지 않고 여기 변수로 한 번만 둔 이유: 나중에 정책이 바뀌거나
						     PRODUCT.SHIPPING_FEE_TYPE 컬럼을 실제로 연동할 때 고칠 곳이 한 군데로 모이기 때문 --%>
						<c:set var="freeShipMin" value="19800" />
						<ul class="product-grid">
							<c:forEach var="item" items="${products}">
								<li class="product-card">
									<%-- 카드 링크 — 2026-09-06 optionId 추가.
									     원본 쿠팡도 목록 카드가 상품이 아니라 "그 색상 옵션 하나"를 가리킴(실측: 같은 상품인데
									     블랙/화이트에서 링크의 itemId 가 다름). 그래서 카드가 고른 대표 옵션을 그대로 넘겨서
									     상세페이지가 같은 옵션을 선택된 상태로 열게 함 — 상세 쪽은 이미 optionId 를 읽고 있어서
									     (ProductHandler) 자바는 안 고쳐도 됨.
									     optionId 가 0 이면(옵션이 없는 상품) 안 붙임 = 예전과 똑같이 동작 --%>
									<%-- 옛 버전: <a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}"> --%>
									<%-- 옛 버전2: <a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}<c:if test="${item.optionId > 0}">&amp;optionId=${item.optionId}</c:if>"> --%>
									<%-- 2026-09-06: 상품번호를 쿼리스트링이 아니라 경로로 넘김 → /product/132?optionId=538
									     (쿠팡도 /vp/products/6056289614?itemId=... 처럼 상품번호만 경로에 둠)
									     web.xml 에 "/product/*" 매핑을 추가하고 ProductHandler 가 getPathInfo() 로 읽게 해둠.
									     optionId 가 첫 파라미터가 되므로 "&amp;" 가 아니라 "?" 로 시작함.
									     ※ 옛 주소(?productNo=)도 그대로 살아있어서 팀원 화면 15곳은 안 고쳐도 됨 --%>
									<a href="${pageContext.request.contextPath}/product/${item.productNo}<c:if test="${item.optionId > 0}">?optionId=${item.optionId}</c:if>">
										<figure>
											<c:if test="${not empty item.thumbnailUrl}">
												<img src="${img:url(item.thumbnailUrl)}" alt="${item.productName}">
											</c:if>
										</figure>
										<%-- 무료배송 뱃지 — 2026-09-02 추가, 2026-09-05 조건 추가(사용자 지시).
										     전에는 모든 카드에 무조건 "무료배송"이 찍혔는데, 실제 정책은 19,800원 이상일 때만 무료라
										     판매가가 그 아래인 상품에는 글자를 안 띄우도록 <c:if> 로 감쌌음.
										     ★ <p> 자체는 조건과 상관없이 항상 남겨둠 — 이 줄이 통째로 사라지면 그 카드만 아래 내용이
										       위로 딸려 올라가서 같은 줄 카드끼리 상품명·가격 위치가 어긋남(=1번 항목에서 고쳤던
										       "카드마다 크기가 제각각" 문제가 되살아남). 그래서 글자만 빼고 자리는 CSS 의
										       min-height 로 잡아둠.
										     ★ 2026-09-05 2차(사용자 요청 "무료배송 아닌 상품은 상품명을 사진과 조금 더 가깝게"):
										       글자가 없을 때는 is-empty 클래스를 붙여서 그 빈 자리를 절반으로 줄임.
										       클래스를 JSP 에서 붙이는 이유 — CSS 의 :empty 선택자는 "안에 아무것도 없을 때"만
										       걸리는데, 이 <p> 안에는 줄바꿈·들여쓰기 공백이 남아 있어서 :empty 가 안 먹음 --%>
										<p class="free-shipping-badge ${item.salePrice >= freeShipMin ? '' : 'is-empty'}">
											<c:if test="${item.salePrice >= freeShipMin}">무료배송</c:if>
										</p>
										<p class="product-name">${item.productName}</p>
										<%-- ★ 2026-09-05 재실측으로 배지 위치 정정(사용자 지적: "로켓·내일 아이콘은 금액 옆에 나와야 함").
										     원본 카드의 가격 줄은 이렇게 생겼음:
										       <div style="display:flex; align-items:center; row-gap:2px; flex-wrap:wrap">
										         <strong>9,900원</strong>(margin-right 5px)
										         <img 로켓배지 79x16, margin-right 2px>
										         <img 내일도착배지 29x16>
										       </div>
										     즉 판매가와 **같은 줄**에 배지가 붙고, 자리가 모자라면 그 줄 안에서 아래로 접힘(flex-wrap).
										     전에는 배지를 별도의 줄(.delivery-badges)로 아래에 뒀는데 그게 원본과 달랐음 --%>
										<p class="product-price">
											<c:if test="${item.discountRate > 0}">
												<span class="discount-rate">${item.discountRate}%</span>
												<span class="normal-price"><fmt:formatNumber value="${item.normalPrice}" pattern="#,###"/>원</span>
											</c:if>
											<span class="price-badge-row ${item.soldOut ? 'is-soldout' : ''}">
												<strong class="sale-price"><fmt:formatNumber value="${item.salePrice}" pattern="#,###"/>원</strong>
												<%-- 실제 배송정보 컬럼(DELIVERY_METHOD 등)을 아직 안 써서 모든 카드에 똑같이 표시.
												     내일도착 배지(badge_199cd481e67.png)는 우리 파일이 58×32 라 높이 16px 로 맞추면
												     폭이 29px — 원본 실측(29×16)과 정확히 같아짐 --%>
												<img class="badge-rocket" src="${pageContext.request.contextPath}/images/icons/logo_rocket_filter_medium.png" alt="로켓배송">
												<img class="badge-tomorrow" src="${pageContext.request.contextPath}/images/icons/badge_199cd481e67.png" alt="내일도착">
											</span>
										</p>
										<%-- 품절 표시 — 2026-09-03 원본 실측(Playwright). 모든 옵션 재고 0(PRODUCT.SALE_STATUS='품절')일 때만.
										     원본은 이미지·상품명은 그대로 두고 가격·배지만 흐리게(위 is-soldout, category.css 참고) +
										     가격 줄 바로 밑에 이 문구만 추가하는 방식이었음(오버레이 아님) --%>
										<c:if test="${item.soldOut}">
											<p class="sold-out-text">일시품절</p>
										</c:if>
										<%-- 배송문구 — 2026-09-05 재실측으로 두 가지 정정(사용자 지적):
										     ① 색상: 로켓(내일 도착) 상품은 초록색 #008C00 (원본 인라인 스타일에 그대로 박혀 있음).
										        로켓이 아닌 상품만 검정계열 #212B36 이고 문구도 "9/5(토) 도착 예정" 형태였음
										     ② 문구: "도착 예정"이 아니라 **"도착 보장"** — 우리는 모든 카드에 로켓 배지를 달고 있으므로
										        로켓 쪽 문구인 "내일(요일) 도착 보장" 으로 통일함(날짜 M/d 는 원본에도 안 붙음) --%>
										<p class="delivery-date">${deliveryDate} 도착 보장</p>
										<%-- 2026-09-02 2차: 별점/적립 순서가 뒤바뀌어 있었음 — 원본은 배송문구 다음이 별점,
										     그 다음이 적립(STRUCTURE.md 7장). 무료배송 뱃지 넣으면서 대조 없이 기존 순서에
										     끼워넣었던 게 원인 --%>
										<c:if test="${item.reviewCount > 0}">
											<p class="product-rating">
												<span class="star-rating" aria-label="평점 ${item.avgRating}점"><em style="width:${item.avgRating * 20}%"></em></span>
												(${item.reviewCount})
											</p>
										</c:if>
										<%-- 적립 — 실제 적립 정책 테이블이 없어서 판매가 1%로 임의 계산(2026-09-01 사용자 요청으로 재추가).
										     ★ 2026-09-05: 원본 카드와 모양을 맞춤. ref/category/live_snapshot_recheck.txt 606~607줄에
										     찍힌 실제 원본 카드 구조가 "동전 아이콘(img) + '최대 795원 적립' 글자" 라서 그대로 따라감
										     (그동안은 아이콘 없이 "적립 795원" 이라 어순도 원본과 달랐음).
										     아이콘은 2026-09-05 사용자가 원본과 같은 파일(list-cash-icon@2x.png, 28×28)을
										     images/icons 에 직접 넣어줘서 그걸 씀 — 동그란 테두리는 CSS 가 아니라 이 이미지 자체임 --%>
										<p class="cash-reward">
											<img src="${pageContext.request.contextPath}/images/icons/list-cash-icon@2x.png" alt="">
											최대 <fmt:formatNumber value="${item.cashReward}" pattern="#,###"/>원 적립
										</p>
									</a>
								</li>
							</c:forEach>

							<%-- 채움 카드 — 2026-09-03 추가. 카드마다 border-bottom 이 있어서(위 .product-card 참고)
							     4개씩 꽉 찬 줄은 자연스럽게 이어진 선처럼 보이는데, 마지막 줄이 4개를 못 채우면
							     그 줄만큼만 선이 짧게 그려짐. 실제 원본도 구조상 똑같이 카드 하나하나에 선을 긋는
							     방식이라 이 자체는 원본과 다른 게 아니지만(원본은 상품이 수백 개라 거의 안 보이는
							     상황), 우리는 테스트 상품이 적어서 항상 짧은 줄로 끝나 어색해 보임 — 내용 없는
							     채움 카드로 남은 자리를 메워서 선을 끝까지 이어지게 함(4의 배수로 맞춤) --%>
							<c:set var="remainder" value="${fn:length(products) % 4}" />
							<c:if test="${remainder > 0}">
								<c:forEach begin="1" end="${4 - remainder}">
									<li class="product-card product-card--filler" aria-hidden="true"></li>
								</c:forEach>
							</c:if>
						</ul>

						<%-- 페이지네이션 --%>
						<nav class="pagination" aria-label="페이지">
							<c:if test="${page > 1}">
								<a href="${baseUrl}&listSize=${listSize}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&page=${page - 1}">이전</a>
							</c:if>
							<c:forEach var="p" begin="1" end="${totalPages}">
								<a href="${baseUrl}&listSize=${listSize}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&page=${p}"
									class="${p == page ? 'current' : ''}">${p}</a>
							</c:forEach>
							<c:if test="${page < totalPages}">
								<a href="${baseUrl}&listSize=${listSize}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&page=${page + 1}">다음</a>
							</c:if>
						</nav>
					</c:otherwise>
				</c:choose>

			<%-- 함께 본 카테고리 -- 2026-09-02 추가. 원본 재확인(Playwright, coupang.com):
			     페이지네이션 바로 아래, 왼쪽 카테고리 필터와 같은 형제 목록에서 현재 카테고리만 뺀 11개가
			     다시 한 번 나옴. findSiblingCategories() 를 그대로 재사용(새 쿼리 필요 없음) --%>
			<div class="also-viewed">
				<h2>함께 본 카테고리</h2>
				<ul class="also-viewed-list">
					<c:forEach var="sib" items="${siblingCategories}">
						<c:if test="${sib.categoryNo != categoryNo}">
							<li><a href="${pageContext.request.contextPath}/category?categoryNo=${sib.categoryNo}">${sib.categoryName}</a></li>
						</c:if>
					</c:forEach>
				</ul>
			</div>

			</section>
		</div>
	</main>

	<%-- 맨 위로 버튼 — 2026-09-05 추가. 메인(index.html 2278줄)/상세(product.jsp 1309줄)에 이미 있는 것을
	     마크업 그대로 가져다 씀(새로 만든 것 없음). 스타일은 common.css `.goto-top`(1517줄~),
	     스크롤 감지·클릭 동작은 header.js(264줄~)가 #goto-top 을 찾아서 붙여줌 —
	     이 페이지는 아래에서 header.js 를 이미 읽고 common.css 도 이미 링크돼 있어서 이 버튼만 넣으면 됨 --%>
	<button type="button" id="goto-top" class="goto-top">
		<span class="blind">맨 위로</span>
	</button>

	<jsp:include page="/inc/footer.jsp" />

	<script src="${pageContext.request.contextPath}/js/header.js"></script>
	<script src="${pageContext.request.contextPath}/js/category.js"></script>
</body>
</html>
