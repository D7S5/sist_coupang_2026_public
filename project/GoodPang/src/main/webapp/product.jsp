<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
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
<title>${p.productName}-${p.subCategoryName}|굿팡</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<!-- 1. 브라우저 기본 스타일 지우기 -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<!-- 2. 헤더/푸터 (모든 페이지 공통) -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/product.css">
<%-- <link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/wow_join_modal.css">
 --%>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/product_wow_modal.css">
<!-- 4. 상세페이지 전용 -->
</head>

<%-- 2026-09-06 — 판매중지 상품 처리.
     ① is-soldout : 품절과 같은 CSS 를 그대로 재사용(구매 버튼이 품절 버튼으로 바뀜, product.css 9장).
        옵션마다 다른 품절과 달리 판매중지는 상품 전체라, 처음부터 여기서 붙여둠.
     ② data-sale-status : product.js 가 옵션을 바꿀 때마다 is-soldout 을 다시 계산해서 덮어쓰기 때문에
        (product.js:460) 자바스크립트도 이 값을 보고 판매중지면 계속 품절 상태를 유지하게 함.
     옛 버전: <body class="page-product"> --%>
<body class="page-product ${p.saleStatus eq '판매 중지' ? 'is-soldout' : ''}"
      data-sale-status="${p.saleStatus}">
	<!-- ==================================================
       HEADER — 페이지 맨 위. 로고 / 검색 / 메뉴
       ================================================== -->
	<!-- ★ 이 헤더는 inc/header.jsp 에서 그대로 옮겨온 것 (2026-08-24).
	     손으로 고치지 말 것 — 고쳐야 하면 inc/header.jsp 를 고치고 여기로 다시 옮길 것.
	     JSP 판(product.jsp)에서는 이 자리가 &lt;jsp:include page="inc/header.jsp"/&gt; 한 줄임 -->
	<jsp:include page="inc/header.jsp" />

	<!-- ==================================================
       MAIN — 상품 상세 페이지 본문
       쿠팡 원본: div.prod-atf (상품이미지 650 + 구매박스 650)

       ★ 이 페이지는 "틀 하나 + 상품 데이터 N개" 구조를 염두에 두고 만듦.
         지금은 값을 직접 써넣었지만, 나중에 JSP 로 바꿀 때
         값이 들어갈 자리만 {p.name} 같은 걸로 갈아끼우면 됨.
         그래서 값이 들어가는 자리마다 ▶JSP 주석을 달아둠.

       ★ 클래스 이름은 전부 .product-page 안에서만 쓰도록 CSS 를 잡았음.
         index.html 과 이름이 겹쳐도 안 부딪히게 하려는 것 (CLAUDE.md 7장 규칙)
       ================================================== -->
	<main class="product-page">
		<div class="product-page__inner">

			<!-- ── 빵부스러기(breadcrumb) — 지금 내가 어느 카테고리에 있는지 ──
           원본은 "쿠팡 홈 > 쿠팡수입 > 패션의류/잡화 > ..." 형태.
            <nav> 으로 감싸는 이유: 이건 글이 아니라 "길 안내"라서

           ▶JSP: ProductServlet 이 request 에 담아준 "p"(ProductDTO) 의
             mainCategoryName / midCategoryName / subCategoryName 을 그대로 씀
             (ProductDAO 가 PRODUCT→SUB_CATEGORY→MID_CATEGORY→MAIN_CATEGORY 조인해서 채워둔 값).
             JS 는 필요 없음 — 요청마다 서버(JSP)가 이미 값을 들고 있어서 EL 로 바로 찍으면 됨.
             (카테고리 "메뉴판" 을 그리는 category/getinfo + header.js 와는 다른 데이터임 — 그건
             사이트 전체 카테고리 트리고, 여긴 "이 상품 하나"가 속한 카테고리 경로임)
           2026-08-31: /category 카테고리 목록 페이지가 생겨서 href="#" 를 실제 링크로 채움
             (CategoryServlet, categoryNo 는 ProductDAO 가 새로 내려주는 mid/mainCategoryNo) -->
			<nav class="breadcrumb">
				<ol>
					<li><a href="index.jsp">굿팡 홈</a></li>
					<!--
					<li><a href="#">남성패션</a></li>
					<li><a href="#">의류</a></li>
					<li><a href="#">티셔츠</a></li>
					 -->
					<li><a
						href="${pageContext.request.contextPath}/category?categoryNo=${p.mainCategoryNo}">${p.mainCategoryName}</a></li>
					<li><a
						href="${pageContext.request.contextPath}/category?categoryNo=${p.midCategoryNo}">${p.midCategoryName}</a></li>
					<li><a
						href="${pageContext.request.contextPath}/category?categoryNo=${p.subCategoryNo}">${p.subCategoryName}</a></li>

				</ol>
			</nav>

			<!-- ===== 상품 위쪽 영역 (ATF) =====
           ATF = Above The Fold. 스크롤 안 내려도 바로 보이는 부분이라는 뜻.
           왼쪽 사진 / 오른쪽 구매박스 를 반반으로 나눔 -->
			<div class="prod-atf">

				<!-- ── 왼쪽: 상품 이미지 ────────────────────────── -->
				<!-- 2026-09-01: 여기 있던 <c:set var="mainOption" value="${options[0]}"/> 를 지움 —
				     그대로 두면 ProductServlet 이 옵션 선택 유지를 위해 골라둔 mainOption(request 속성)을
				     이 c:set 이 페이지 스코프로 다시 덮어써서 "항상 첫 옵션"으로 돌아가버림.
				     이제 mainOption 은 ProductServlet 이 request 에 담아준 것을 그대로 씀 -->
				<div class="product-image">

					<!-- 썸네일 세로 목록 (원본 실측 70 x 70, 아래 여백 4px)
             썸네일과 큰 이미지가 **같은 파일**을 씀 — 누르면 JS 가 큰 이미지의 src 를 갈아끼움.
                 alt="" 인 이유: 옆의 큰 이미지가 같은 사진이라 화면낭독기가 두 번 읽으면 방해됨

             2026-08-28: PRODUCT_IMAGE(맨 처음 옵션의 대표/추가 사진) 연동함.
             옵션 드롭박스(#optionSelect)를 바꾸면 js/product.js 가 그 옵션의 사진으로 이 목록을 바꿔치기함.
             ★ 임시 ★ 이 옵션에 등록된 사진이 없으면(대부분 아직 없음) 옛날 더미 photo-1.jpg 로 대체 -->
					<ul class="product-image__thumbs">
						<c:choose>
							<c:when test="${not empty mainOption.images}">
								<c:forEach items="${mainOption.images}" var="img"
									varStatus="loop">
									<li class="${loop.first ? 'is-on' : ''}"><a href="#"><img
											src="${img:url(img.imageUrl)}"
											alt=""></a></li>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<li class="is-on"><a href="#"><img
										src="${pageContext.request.contextPath}/images/product-detail/photo-1.jpg"
										alt=""></a></li>
							</c:otherwise>
						</c:choose>
					</ul>


					<!-- 큰 이미지 (정사각형) — 썸네일 첫 번째와 같은 사진으로 시작 -->
					<div class="product-image__main">
						<c:choose>
							<c:when test="${not empty mainOption.images}">
								<img
									src="${img:url(mainOption.images[0].imageUrl)}"
									alt="${p.productName}">
							</c:when>
							<c:otherwise>
								<img
									src="${pageContext.request.contextPath}/images/product-detail/photo-1.jpg"
									alt="${p.productName}">
							</c:otherwise>
						</c:choose>
					</div>
				</div>

				<!-- ── 오른쪽: 구매박스 ────────────────────────── -->
				<div class="prod-atf-contents">

					<!-- ① 머리부분 — 재고경고 / 브랜드 / 상품명 / 별점 -->
					<div class="product-buy-header">

						<!-- 브랜드명 — 파란 글씨(#346aff). ▶JSP:  -->
						<!-- <a href="#" class="brand-info">베이직스</a> -->
						<a href="#" class="brand-info">${p.storeName}</a>

						<div class="product-title-row">
							<!-- 상품명. 페이지에서 제일 중요한 제목이라 h1.
                   ※ index.html 은 h1 이 로고였는데, 이 페이지의 주인공은 상품이라
                     여기서는 상품명이 h1 이 되는 게 맞음 -->
							<!-- <h1 class="product-title">무형광 남성 반팔 라운드 티셔츠 3종 세트, 화이트/그레이/블랙</h1> -->
							<h1 class="product-title">${p.productName}</h1>

							<!-- 찜 / 공유 버튼 — 2026-08-31: 로그인해서 실제 쿠팡을 Playwright 로 열어
                   다시 확인해보니 우리가 그렸던 "테두리 원 + 선(stroke) 아이콘"은 원본과
                   전혀 다른 방식이었음. 원본은 테두리/배경이 아예 없는 38x38 버튼에
                   채워진(fill) 도형 아이콘(20x20, #454F5B) — path 를 그대로 옮김 -->
							<div class="wish-and-share">
								<button type="button" class="btn-wish">
									<svg viewBox="0 0 24 24" aria-hidden="true">
										<path fill-rule="evenodd" clip-rule="evenodd"
											d="M12.174 4.43124C13.4923 2.68731 15.0031 1.70166 16.9982 1.52692L17.2732 1.50812C18.8998 1.42763 20.5664 1.94772 21.7255 2.97238C23.1953 4.27167 24 6.252 24 8.31785C24 12.688 20.8931 16.8316 12.5507 22.3348C12.2166 22.5552 11.7834 22.5552 11.4493 22.3348C3.10692 16.8316 0 12.688 0 8.31785C0 6.252 0.80471 4.27167 2.27453 2.97238C3.43363 1.94772 5.10025 1.42763 6.72677 1.50812C8.8591 1.61362 10.4478 2.60804 11.826 4.43124L12 4.66712L12.174 4.43124ZM20.4008 4.47083C19.6531 3.80978 18.4997 3.44987 17.3721 3.50568C15.5061 3.598 14.1913 4.71228 12.8675 7.02107C12.4833 7.69117 11.5167 7.69117 11.1325 7.02107C9.80866 4.71228 8.49392 3.598 6.62792 3.50568C5.50027 3.44987 4.34694 3.80978 3.59916 4.47083C2.58082 5.37102 2 6.80038 2 8.31785C2 11.755 4.52788 15.2449 11.7579 20.1359L11.999 20.2981L12.6249 19.8752C19.4355 15.2067 21.9098 11.8187 21.9976 8.50204L22 8.31785C22 6.80038 21.4192 5.37102 20.4008 4.47083Z" />
									</svg>
									<span class="blind">찜하기</span>
								</button>
								<button type="button" class="btn-share">
									<svg viewBox="0 0 24 24" aria-hidden="true">
										<path fill-rule="evenodd" clip-rule="evenodd"
											d="M22 4C22 5.933 20.433 7.5 18.5 7.5C17.604 7.5 16.7866 7.16332 16.1675 6.60956L8.88141 11.0933C8.95876 11.3825 9 11.6864 9 12C9 12.3136 8.95876 12.6175 8.88142 12.9067L16.1675 17.3904C16.7866 16.8367 17.604 16.5 18.5 16.5C20.433 16.5 22 18.067 22 20C22 21.933 20.433 23.5 18.5 23.5C16.567 23.5 15 21.933 15 20C15 19.6864 15.0412 19.3825 15.1186 19.0933L7.83249 14.6096C7.21338 15.1633 6.39601 15.5 5.5 15.5C3.567 15.5 2 13.933 2 12C2 10.067 3.567 8.50002 5.5 8.50002C6.396 8.50002 7.21337 8.83671 7.83248 9.39047L15.1186 4.90671C15.0412 4.61753 15 4.31358 15 4C15 2.067 16.567 0.5 18.5 0.5C20.433 0.5 22 2.067 22 4Z" />
									</svg>
									<span class="blind">공유하기</span>
								</button>
							</div>
						</div>

						<!-- 별점 + 리뷰수. 리뷰 영역으로 이동하는 링크(#reviews)
                           2026-08-27: 유니코드 별(★★★★☆) → 리뷰 카드와 같은 스프라이트(.star-rating)로 통일.
                 여기 별은 "이 리뷰 하나"가 아니라 "이 상품 전체 평균 평점"이라 avgRating(서버가 reviews 리스트로 계산해서 내려줌, ProductServlet)을 씀
                 2026-09-02: "(1)" → "1개 상품평" 문구로 바꿈 — 사용자가 로그인 안 한 상태로 직접
                 캡처한 최신 화면(ref/product/스크린샷 2026-09-02 083346.png) 기준 -->
						<div class="review-atf">
							<span class="star-rating" aria-label="평점 ${avgRating}점"><em
								style="width:${avgRating * 20}%"></em></span> <a href="#reviews"
								class="count">${reviewCount}개 상품평</a>
						</div>
					</div>

					<!-- ② 가격
					     2026-09-02: 사용자가 직접 캡처해서 준 최신 화면(위 스크린샷) 기준으로 다시 짬 —
					     원가(취소선)가 따로 줄을 차지하는 게 아니라 할인율/판매가/원가/안내아이콘/배송배지가
					     전부 한 줄에 나란히 있었음. price-origin 을 별도 줄에서 price-now 안쪽으로 옮김
					     (JS 는 그대로 .price-origin 을 querySelector 하므로 product.js 는 안 건드려도 됨).
					     "할인" 라벨(discount-label, 2026-08-31에 넣었던 것)은 이 캡처엔 안 보여서 뺌 —
					     로그인 상태에서 다시 보이면 되살릴 것. "67%" 가 빨간 리본 모양 배지였고 그 아래
					     "9,180원 할인 · 5시간 남음"같은 카운트다운 줄도 있었는데, 이건 이 상품이 마침
					     타임특가 중이라 그런 걸로 보여서(일반 할인 표시가 아니라 특가 이벤트 전용 UI로
					     추정) 지금은 안 옮김 — 우리 DB에 그런 이벤트 데이터 자체가 없음 -->
					<div class="price-container">
						<div class="price-now">
							<!-- 2026-08-30 확정 — PRODUCT_OPTION.PRICE/NORMAL_PRICE 는 PRODUCT_PRICE(기본가)에 더해지는
							     "추가금". 판매가/정상가 둘 다 ProductServlet 이 기본가+추가금으로 계산해둔 값을 씀.
							     정상가가 없으면(NORMAL_PRICE 미입력) 할인 표시 자체를 숨김. 옵션 바꾸면 product.js 의
							     setPrice() 가 이 태그들 글자·style 을 그대로 갈아끼움 -->
							<span class="discount"
								${empty displayNormalPrice ? ' style="display:none"' : ''}>${discountRate}%</span>
							<strong class="total-price" data-unit-price="${displayPrice}"
								data-base-price="${p.productPrice}">${displayPrice}원</strong>
							<!-- 원가 취소선 (#768695 + line-through). 정상 추가금(NORMAL_PRICE) 입력 안 한 옵션이면 안 보임 -->
							<div class="price-origin"
								${empty displayNormalPrice ? ' style="display:none"' : ''}>
								<span class="origin-price">${displayNormalPrice}원</span>
								<!-- 원본에 있는 ⓘ — 눌러도 아무 일 없는 안내 아이콘. 이미지 없이 글자로 그림 -->
								<button type="button" class="price-info">
									<span class="blind">가격 안내</span>
								</button>
							</div>
							<!-- 로켓배송/내일도착 배지 — 2026-09-02: CSS로 그리던 텍스트 배지 대신
							     실제 이미지로 교체(사용자가 images/icons 에 받아다 줌) -->
							<span class="badge-rocket"><img
									src="${pageContext.request.contextPath}/images/icons/logo_rocket_filter_medium.png"
									alt="로켓배송"></span> <span class="badge-tomorrow"><img
									src="${pageContext.request.contextPath}/images/icons/badge_199cd481e67.png"
									alt="내일도착"></span>
						</div>

						<!-- 품절일 때만 보이는 줄. 평소엔 CSS 가 감춤 (body 에 .is-soldout 이 붙어야 나옴)
						     원본 실측: 14px / 700 / rgb(170,181,192)
						     2026-09-06 — 같은 자리에 판매중지도 표시. 재고가 없어서 못 사는 것과
						     판매자가 내려서 못 사는 것은 이유가 다르니 글자를 갈라줌
						     옛 버전: <p class="soldout-text">품절</p> -->
						<p class="soldout-text">
							<c:choose>
								<c:when test="${p.saleStatus eq '판매 중지'}">판매 중지된 상품입니다</c:when>
								<c:otherwise>품절</c:otherwise>
							</c:choose>
						</p>

						<!-- 와우회원 전용 배지 — 2026-08-31 추가.
						     sessionScope.wowMember 는 로그인할 때(LoginServlet) WOW_MEMBERSHIP 테이블 기준으로
						     세팅되고, 가입/해지 직후(WowJoinServlet/WowCancelServlet)에도 갱신됨.
						     로그인 자체를 안 했으면 세션에 이 값이 아예 없어서 EL 이 false 로 취급 — 로그인 여부를
						     따로 검사할 필요 없음. 로그인해서 실제 쿠팡 와우회원 화면을 Playwright 로 열어
						     확인한 구조/실측값 그대로 옮김. 로고는 실제 쿠팡 파일(wow@2x.png, 40x16 실측)을
						     사용자가 받아다 줌 -->
						<c:if test="${sessionScope.wowMember}">
							<div class="wow-benefit-badge">
								<div class="wow-benefit-badge-top">
									<img class="wow-benefit-badge-logo"
										src="${pageContext.request.contextPath}/images/wow@2x.png"
										alt="와우"> <span class="wow-benefit-badge-title">고객님은
										<strong>와우회원</strong>으로
									</span>
								</div>
								<!-- 체크 아이콘 — 원본은 static.coupangcdn.com 이미지(승인 안 된 도메인)라
								     못 받아옴. 공유 아이콘(위 svg)과 같은 방식으로 인라인 SVG 로 정확한
								     크기(12x12)만 맞춰 그림. 나중에 실제 아이콘 파일 받으면 img 로 교체 가능 -->
								<ul class="wow-benefit-badge-items">
									<li><svg class="wow-benefit-check" viewBox="0 0 12 12"
											aria-hidden="true">
											<polyline points="2.5,6.2 5,9 9.5,3.3" />
										</svg>무조건 무료배송</li>
									<li><svg class="wow-benefit-check" viewBox="0 0 12 12"
											aria-hidden="true">
											<polyline points="2.5,6.2 5,9 9.5,3.3" />
										</svg>무료반품</li>
								</ul>
							</div>
						</c:if>
					</div>

					<!-- ③ 배송 정보
					     2026-08-31: 여기 있던 <hr class="price-bottom-divider"> 를 지움 —
					     로그인해서 실제 쿠팡을 Playwright 로 열어 재보니 가격 블록과 배송 블록
					     사이에 선(border/hr) 자체가 없었음(둘 다 border:0, margin-top:16px 로만
					     간격을 줌). 지어낸 선이었던 것 -->
					<div class="delivery-container">
						<!-- 2026-09-01: 와우회원이 아닐 때 여기 붙어 있던 "(로켓배송 상품 19,800원 이상
						     구매 시)" 괄호를 지움 — 사용자가 "라디오 위에 이 한 줄이 중복"이라고 지적함.
						     바로 아래 radio-group 첫 번째 항목이 "로켓배송 상품 19,800원 이상 무료배송"으로
						     똑같은 조건을 이미 말하고 있어서, 여기서도 또 말하면 한 화면에 같은 문장이 두 번 나옴.
						     와우회원 문구("무료배송"만, 괄호 없음)와도 이제 모양이 같아져서 c:choose 자체가
						     필요 없어짐 -->
						<p class="shipping-fee">
							<em class="txt-bold">무료배송</em>
						</p>
						<p class="delivery-date">

							<em class="txt-green">${deliveryDate}</em> <em
								class="txt-green-normal">도착 보장</em> <span class="txt-sub">(11시간
								20분 내 주문 시 / 서울·경기 기준)</span>
						</p>

						<!-- 배송 방법 선택 (2026-08-21 추가, 2026-08-31 와우회원 분기 추가)
						     원본에 실제로 있는 라디오 버튼 2개. input[type=radio] 가 아니라
						     span 을 CSS 로 동그랗게 그린 커스텀 라디오 (원본 클래스명 그대로 씀).
						     ★ 둘째 항목(로켓와우)을 고르면 아래 [장바구니 담기][바로구매] 두 칸이
						     [로켓와우로 무료배송 >] 한 칸으로 바뀜 — 원본을 Playwright 로 직접 클릭해서 확인함.
						     ▶ 와우회원은 이미 "무조건 무료배송"이라 이 선택 자체가 원본에 없음 —
						       로그인해서 실제 와우회원 화면을 Playwright 로 열어 직접 확인함(라디오 0개, 위
						       wow-benefit-badge 만 있음) -->
						<c:if test="${not sessionScope.wowMember}">
							<ul class="radio-group">
								<li class="radio-item is-on"><span class="radio"></span> <span
									class="radio-text">로켓배송 상품 19,800원 이상 무료배송</span></li>
								<li class="radio-item"><span class="radio"></span> <span
									class="radio-text">무료배송 + 무료반품 <span class="separator">|</span>
										로켓와우 신청시
								</span>
									<button type="button" class="radio-info">
										<span class="blind">안내</span>
									</button></li>
							</ul>
						</c:if>
					</div>

					<!-- 판매자 정보 — 사이즈 위. 2026-09-01: 머지하면서 통째로 빠져 있던 걸 되살림.
					     로그인해서 실제 쿠팡을 Playwright 로 다시 확인(클래스명도 원본 그대로 seller-info) —
					     "판매자 상품 보러가기"는 원본에선 판매자 스토어(shop.coupang.com) 링크인데,
					     우리는 그런 페이지가 없어서 href="#" 로 자리만 잡음 -->
					<div class="seller-info">
						<div class="seller-info-row">
							판매자: <a href="#" class="seller-info-link">${p.storeName} <span
								class="seller-info-visit">판매자 상품 보러가기</span>
							</a>
						</div>
					</div>

					<!-- ④ 옵션 — "1번 축은 드롭박스, 그 다음 축은 사진 있으면 칩" (원본과 같은 모양)
             PRODUCT_OPTION 은 "조합 하나(사이즈+색상 등) = 행 하나"로 저장돼 있어서, 축마다 중복 없는
             값 목록을 뽑는 건 JSTL 보다 JS 가 훨씬 간단함 — 그래서 여기선 데이터만 넘기고
             실제 화면은 js/product.js 의 setupOptionSelect() 가 그림.
             options[0].option1Type 이 없으면(=옵션 자체가 없는 상품) 섹션 전체를 안 그림 -->
					<c:if
						test="${not empty options && not empty options[0].option1Type}">
						<div class="fashion-option" id="fashionOption"></div>

						<!-- 화면엔 안 보임 — js 가 읽어가는 원본 데이터 자리.
                 JSON 은 ProductServlet 에서 Gson 으로 만들어 optionsJson 에 담아 보내줌
                 (JSP 에서 손으로 조립하면 값에 따옴표가 들어갈 때 깨져서).
                 type="application/json" 이라 브라우저가 스크립트로 실행하지 않고 텍스트로만 취급함.
                 data-context-path: 사진 주소는 optionsJson 에 이미 img:url 규칙으로 변환되어 들어있어서
                 안 씀 - 옵션 바꿀 때 /option 을 다시 호출하는 fetch URL 조립에만 씀(product.js 참고) -->
						<script id="productOptionsData" type="application/json"
							data-context-path="${pageContext.request.contextPath}">${optionsJson}</script>
					</c:if>

					<!-- ⑤ 적립 혜택
					     2026-09-02: "혜택보기" 링크와 "PC에서도 간편한 결제" 칩 줄을 다시 넣음 —
					     로그인 여부와 상관없이 넣기로 확정(2026-09-01엔 "로그인해서 보니 없더라"고
					     지웠던 건데, 사용자가 로그아웃 상태 최신 캡처에서 이 줄이 있는 걸 다시 확인함.
					     로그인/로그아웃에 따라 갈리는지는 불확실하지만 이번엔 그냥 고정으로 넣기로 함).
					     띄어쓰기도 정리 — em 안쪽 공백 하나 빼서 "원 " + " <u>" 두 번 겹치던 것 하나로 줄임 -->
					<div class="conditional-benefits">
						<div class="benefit-row">
							<span class="benefit-label">적립</span> <span class="benefit-text">
								<em>최대 ${rewardCash}원</em> <u>굿팡캐시 적립</u> · 굿페이 머니 결제시
							</span> <a href="#" class="benefit-more">혜택보기</a>
						</div>
						<!-- 2026-09-02: 결제수단 칩에 아이콘 추가 — 사용자가 images/icons 에 받아다 준
						     coupaymoney_color@4x.png(굿페이머니 M) / card_color@4x.png(카드) /
						     bankbook_color@4x.png(계좌이체) -->
						<div class="benefit-row pay-methods">
							<strong>PC에서도 간편한 결제</strong> <span class="pay-chip"><img
									src="${pageContext.request.contextPath}/images/icons/coupaymoney_color@4x.png"
									alt="">굿페이머니</span> <span class="pay-chip"><img
									src="${pageContext.request.contextPath}/images/icons/card_color@4x.png"
									alt="">카드</span> <span class="pay-chip"><img
									src="${pageContext.request.contextPath}/images/icons/bankbook_color@4x.png"
									alt="">계좌이체</span>
						</div>
					</div>

					<!-- ⑥ 수량 + 구매 버튼 (원본 실측: 전부 높이 42px) -->
					<form class="prod-buy-quantity-and-footer" method="post"
						action="${pageContext.request.contextPath}/cart/add">

						<!-- 실제 CART에 저장할 OPTION_ID
                 2026-08-28: 하드코딩(25) 대신 첫 번째 옵션 값으로 시작, #optionSelect 를 바꾸면
                 js/product.js 가 이 값을 그 옵션의 OPTION_ID 로 갈아끼움.
                 2026-09-01: "첫 번째 옵션"이 아니라 mainOption(=URL 의 optionId 로 새로고침해도
                 유지되는 선택 옵션, ProductServlet 이 골라서 request 에 담아줌)으로 바꿈 -->
						<input type="hidden" name="optionId" id="selectedOptionId"
							value="${not empty mainOption ? mainOption.optionId : ''}">

						<!-- 화면 표시용 선택 옵션 -->
						<!-- 상품번호 -->
						<input type="hidden" name="productNo" value="${p.productNo}">
						<!-- 원래 "색상"만 담던 자리인데, 옵션이 색상이 아닐 수도 있어서
                 지금은 선택한 옵션 조합 전체 라벨(예: "블랙 / S")을 담음 -->
						<input type="hidden" name="color" id="selectedColor"
							value="${not empty mainOption ? mainOption.label : ''}">

						<!-- 수량 -->
						<div class="product-quantity">

							<input type="text" class="qty-input" name="quantity" value="1"
								inputmode="numeric">
							<div class="qty-spin">
								<button type="button" class="qty-plus">
									<span class="blind"> 수량 더하기 </span>
								</button>
								<button type="button" class="qty-minus">
									<span class="blind"> 수량 빼기 </span>
								</button>
							</div>
						</div>
						<!-- 재고 초과 입력 시 말풍선 — 2026-09-03 추가 -->
						<div class="qty-max-tip" id="qtyMaxTip"></div>

						<!-- 장바구니 -->
						<button type="button" class="prod-cart-btn" id="cartAddBtn">
							장바구니 담기</button>
						<div id="cartAddedPopup" class="cart-added-popup">

							<button type="button" class="cart-popup-close"
								id="cartPopupClose">×</button>

							<p>상품이 장바구니에 담겼습니다.</p>

							<a href="${pageContext.request.contextPath}/cart"
								class="cart-popup-link"> 장바구니 바로가기 &gt; </a>
						</div>

						<!-- 바로구매 -->
						<button type="submit" class="prod-buy-btn"
							formaction="${pageContext.request.contextPath}/order/buy"
							formmethod="post">
							바로구매 <i class="arrow-right"></i>

						</button>

						<!-- 로켓와우 -->
						<c:choose>
							<c:when test="${isWowMember}">
								<button type="submit" class="prod-wow-btn"
									formaction="${pageContext.request.contextPath}/order/buy"
									formmethod="post">
									로켓와우로 무료배송 <i class="arrow-right"></i>
								</button>

							</c:when>
							<c:otherwise>
								<button type="button" class="prod-wow-btn" id="wowJoinOpenBtn">
									로켓와우로 무료배송 <i class="arrow-right"></i>
								</button>
							</c:otherwise>
						</c:choose>
						<!-- 품절 (2026-09-06 — 판매중지도 이 버튼을 같이 씀. 글자만 갈라줌)
						     옛 버전: <button type="button" class="prod-soldout-btn" disabled>품절</button> -->
						<button type="button" class="prod-soldout-btn" disabled>
							<c:choose>
								<c:when test="${p.saleStatus eq '판매 중지'}">판매 중지</c:when>
								<c:otherwise>품절</c:otherwise>
							</c:choose>
						</button>

					</form>

					<!-- ⑦ 맨 아래 작은 글씨 색상계열, 굿팡상품번호 텍스트만 삭제, 통째로 삭제하면 배치 달라짐
					     ★ 2026-09-03: 굿팡상품번호 줄 복구 — 하드코딩 아니고 DB 값(p.productNo, mainOption.optionId).
					       색상계열은 ProductDTO 에 컬럼이 없어서 아직 못 살림, 자리만 비워둠 -->
					<div class="product-description">
						<ul>
							<li></li>
							<!-- 2026-09-03: optionId 에 id 부여 — 옵션 바꿀 때 product.js 가 이 글자만 갱신함
							     (옵션 변경은 페이지 새로고침 없이 ajax 로만 처리돼서, id 없이는 이 값이 처음 그대로 남아있었음) -->
							<li>굿팡상품번호: ${p.productNo}<c:if test="${not empty mainOption}"> - <span id="goodpangOptionNo">${mainOption.optionId}</span></c:if></li>
						</ul>
					</div>

				</div>
				<!-- //.prod-atf-contents -->
			</div>
			<!-- //.prod-atf -->

			<!-- ===== 광고 캐러셀 ① 함께 비교하면 좋을 상품 =====
			     원본: div.sdp-ads > .recommendation (높이 446px)
			     실측: 위 여백 35 / 아래 40 / 제목 22px·700 / 카드 140px + 오른쪽 여백 16 / 사진 140x140
			
			     ★ 메인페이지의 .ad-carousel 과 "비슷하지만 다름" — 카드가 훨씬 좁고(140 vs 188)
			       한 번에 5개씩 넘기는 게 아니라 가로로 쭉 이어지다 잘림. 그래서 새로 만듦.
			       (css/main.css 를 불러오면 재사용할 수 있지만, 거기엔 메인 본문 전용 58KB 가
			        같이 딸려와서 상세페이지에는 안 불러오기로 함 — CLAUDE.md 참고) -->
			<section class="sdp-ads">
				<div class="sdp-ads__head">
					<h2>함께 비교하면 좋을 상품</h2>
					<span class="ad-label">광고</span>
				</div>

				<!-- .is-cut = 넘치는 부분을 잘라내는 상자. 안쪽 ul 이 옆으로 밀리며 움직임 -->
				<div class="sdp-ads__body">
					<ul class="sdp-ads__list">
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-1.jpg"
									alt=""></span> <span class="ad-item__name">베스티하루 로카티 ROKA
									반팔 티셔츠</span> <span class="ad-item__price"> <span class="was">할인
										<del>23,900</del>
								</span> <span class="now"><em class="rate">58%</em> <strong>9,900</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(63)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-2.jpg"
									alt=""></span> <span class="ad-item__name">3장 세트 ROKA 기능성
									냉감 쿨링 남자 여자 반팔티 로카티</span> <span class="ad-item__price"> <span
									class="now"><strong>18,900</strong></span>
							</span> <span class="ad-item__tag">무료배송</span> <span
								class="ad-item__ship">모레(금) 도착 예정</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(30)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-3.jpg"
									alt=""></span> <span class="ad-item__name">워크존 로카티 코리아아미
									쿨링 반팔 티셔츠 남녀공용</span> <span class="ad-item__price"> <span
									class="was">할인 <del>18,300</del></span> <span class="now"><em
										class="rate">60%</em> <strong>7,290</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(398)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-4.jpg"
									alt=""></span> <span class="ad-item__name">밀리랩 로카티 ROKA
									반팔 티셔츠 2P</span> <span class="ad-item__price"> <span
									class="was">할인 <del>25,800</del></span> <span class="now"><em
										class="rate">36%</em> <strong>16,500</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(6,543)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-5.jpg"
									alt=""></span> <span class="ad-item__name">5장 모던프로 스포츠
									기능성 드라이 라운드 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>120,000</del></span> <span class="now"><em
										class="rate">87%</em> <strong>14,800</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(357)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-6.jpg"
									alt=""></span> <span class="ad-item__name">NEOX 네옥스 쿨론
									자카드 검정 라운드넥 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>34,900</del></span> <span class="now"><em
										class="rate">77%</em> <strong>7,910</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(76)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-7.jpg"
									alt=""></span> <span class="ad-item__name">쿨링 ROKA 로카티 반팔
									단체 티셔츠 반티 체육대회 유니폼</span> <span class="ad-item__price"> <span
									class="was">할인 <del>20,000</del></span> <span class="now"><em
										class="rate">56%</em> <strong>8,800</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(6,384)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-8.jpg"
									alt=""></span> <span class="ad-item__name">아소트 냉감 통기성 메쉬
									로카티 코리아 아미 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>21,900</del></span> <span class="now"><em
										class="rate">54%</em> <strong>9,920</strong></span>
							</span> <span class="ad-item__tag">무료배송</span> <span
								class="ad-item__ship">모레(금) 도착 예정</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(682)</span>
						</a></li>


						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-1.jpg"
									alt=""></span> <span class="ad-item__name">베스티하루 로카티 ROKA
									반팔 티셔츠</span> <span class="ad-item__price"> <span class="was">할인
										<del>23,900</del>
								</span> <span class="now"><em class="rate">58%</em> <strong>9,900</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(63)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-2.jpg"
									alt=""></span> <span class="ad-item__name">3장 세트 ROKA 기능성
									냉감 쿨링 남자 여자 반팔티 로카티</span> <span class="ad-item__price"> <span
									class="now"><strong>18,900</strong></span>
							</span> <span class="ad-item__tag">무료배송</span> <span
								class="ad-item__ship">${deliveryDate} 도착 예정</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(30)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-3.jpg"
									alt=""></span> <span class="ad-item__name">워크존 로카티 코리아아미
									쿨링 반팔 티셔츠 남녀공용</span> <span class="ad-item__price"> <span
									class="was">할인 <del>18,300</del></span> <span class="now"><em
										class="rate">60%</em> <strong>7,290</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(398)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-4.jpg"
									alt=""></span> <span class="ad-item__name">밀리랩 로카티 ROKA
									반팔 티셔츠 2P</span> <span class="ad-item__price"> <span
									class="was">할인 <del>25,800</del></span> <span class="now"><em
										class="rate">36%</em> <strong>16,500</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(6,543)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-5.jpg"
									alt=""></span> <span class="ad-item__name">5장 모던프로 스포츠
									기능성 드라이 라운드 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>120,000</del></span> <span class="now"><em
										class="rate">87%</em> <strong>14,800</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(357)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-6.jpg"
									alt=""></span> <span class="ad-item__name">NEOX 네옥스 쿨론
									자카드 검정 라운드넥 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>34,900</del></span> <span class="now"><em
										class="rate">77%</em> <strong>7,910</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(76)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-7.jpg"
									alt=""></span> <span class="ad-item__name">쿨링 ROKA 로카티 반팔
									단체 티셔츠 반티 체육대회 유니폼</span> <span class="ad-item__price"> <span
									class="was">할인 <del>20,000</del></span> <span class="now"><em
										class="rate">56%</em> <strong>8,800</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(6,384)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-8.jpg"
									alt=""></span> <span class="ad-item__name">아소트 냉감 통기성 메쉬
									로카티 코리아 아미 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>21,900</del></span> <span class="now"><em
										class="rate">54%</em> <strong>9,920</strong></span>
							</span> <span class="ad-item__tag">무료배송</span> <span
								class="ad-item__ship">${deliveryDate} 도착 예정</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(682)</span>
						</a></li>
					</ul>
					<button type="button" class="sdp-ads__next">
						<span class="blind">다음 상품</span>
					</button>
				</div>
			</section>

			<!-- ===== 광고 캐러셀 ② 오늘의 판매자 특가 =====
			     원본: div.personalizedGW (높이 496px) — 메인페이지에도 같은 이름의 섹션이 있음
			     실측: 카드 225px / 사진 160x160 / 제목 가운데 + 오른쪽에 1/5 -->
			<section class="gw-deal">
				<div class="gw-deal__head">
					<h2>
						오늘의 <em>판매자 특가</em>
					</h2>
					<span class="gw-deal__page">1/5</span>
				</div>

				<ul class="gw-deal__list">
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-1.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">REPUBLIC OF KOREA 쿨링 반팔 티셔츠 RT002 -
								헬스 운동 기능성</span> <span class="gw-card__was">할인 <em>43%</em> <del>15,000</del></span>
							<strong class="gw-card__price">8,550원</strong> <span
							class="gw-card__ship">내일(목) 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(60)</span>
					</a></li>


					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-1.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">REPUBLIC OF KOREA 쿨링 반팔 티셔츠 RT002 -
								헬스 운동 기능성</span> <span class="gw-card__was">할인 <em>43%</em> <del>15,000</del></span>
							<strong class="gw-card__price">8,550원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(60)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-2.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">NEOX 네옥스 밀리터리 디지털 기능성 V넥 쿨론 반팔 티셔츠</span> <span
							class="gw-card__was">할인 <em>68%</em> <del>24,500</del></span> <strong
							class="gw-card__price">7,670원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(44)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-3.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">인디오 남성용 빅사이즈 밀리터리 카모플라쥬 쿨링 반팔티셔츠</span> <span
							class="gw-card__was">할인 <em>10%</em> <del>10,800</del></span> <strong
							class="gw-card__price">9,720원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(26)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-4.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">NEOX 네옥스 쿨론 밀리터리 디지털 라운드넥 로카 런닝</span> <span
							class="gw-card__was">할인 <em>80%</em> <del>34,900</del></span> <strong
							class="gw-card__price">6,950원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(14)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-5.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">FLY 남녀공용 기능성 드라이 스포츠 반팔 티셔츠 헬스 러닝</span> <span
							class="gw-card__was">할인 <em>11%</em> <del>6,490</del></span> <strong
							class="gw-card__price">5,770원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(216)</span>
					</a></li>
				</ul>
			</section>

			<!-- ===== 탭줄 (상품상세 / 상품평 / 상품문의 / 배송·교환·반품) =====
           원본 실측: 4칸 균등, 칸 높이 49px, 위 여백 50px,
                     글자 16px/700/#555, 테두리 1px #ccc

           ★ 원본은 스크롤 위치를 따라 활성 탭이 자동으로 바뀌는 방식(scroll-spy)임.
             (페이지 맨 위에서 재보면 활성 탭이 아예 없어서 그걸로 확인함)
             지금은 JS 없이 "상품상세"에 .is-on 을 고정으로 붙여둠.
             나머지 3개는 아직 그 섹션을 안 만들어서 링크가 갈 곳이 없음 —
             다음 단계에서 리뷰·문의·배송 섹션을 만들면 그때 살아남 -->
			<!-- ★ 스크롤을 내려도 화면 위에 붙어 있음(sticky) + 지금 보고 있는 구간에 따라
			     활성 탭이 저절로 바뀜(scroll-spy). 원본이 그렇게 동작함 (2026-08-21 반영) -->
			<nav class="detail-tabs">

				<a href="#detail" class="is-on">상품상세</a>
				<!-- <a href="#reviews">상품평 (2,033)</a> -->
				<a href="#reviews">상품평 (${reviewCount})</a> <a href="#qna">상품문의</a>
				<a href="#delivery">배송/교환/반품 안내</a>
			</nav>

			<!-- ===== 필수 표기 정보 =====
           원본: div#itemBrief — 법으로 표기가 의무인 항목들.
           원본도 값이 전부 "상품 상세페이지 참조"로만 돼 있음 (판매자가 안 채운 것) -->
			<div class="item-brief" id="detail">
				<p class="item-brief__title">필수 표기 정보</p>

				<!-- 한 줄에 [라벨][값][라벨][값] 4칸씩 들어감.
             ▶JSP: &lt;c:forEach&gt; 로 감쌀 자리 — 항목 수가 카테고리마다 다름
                   (의류는 소재·치수, 식품은 원산지·유통기한 …) -->
				<table class="item-brief__table">
					<tbody>
						<tr>
							<th>제품 소재</th>
							<td>상품 상세페이지 참조</td>
							<th>색상</th>
							<td>상품 상세페이지 참조</td>
						</tr>
						<tr>
							<th>치수</th>
							<td>상품 상세페이지 참조</td>
							<th>제조자(수입자)</th>
							<td>상품 상세페이지 참조</td>
						</tr>
						<tr>
							<th>제조국</th>
							<td>상품 상세페이지 참조</td>
							<th>세탁방법 및 취급시 주의사항</th>
							<td>상품 상세페이지 참조</td>
						</tr>

						<!-- 여기부터는 "더보기" 를 눌러야 보이는 줄들.
                 처음엔 CSS 로 감춰두고(.is-folded), JS 가 클래스를 떼면 나타남 -->
						<tr class="is-folded">
							<th>제조연월</th>
							<td>상품 상세페이지 참조</td>
							<th>품질보증기준</th>
							<td>관련 법 및 소비자 분쟁 해결 기준에 따름</td>
						</tr>
						<tr class="is-folded">
							<th>A/S 책임자와 전화번호</th>
							<td>판매자에게 문의</td>
							<th>취급시 주의사항</th>
							<td>상품 상세페이지 참조</td>
						</tr>
					</tbody>
				</table>

				<!-- 눌러도 아직 아무 일 없음. 나중에 접힌 줄을 펴는 자리 -->
				<!-- 눌러서 접힌 줄을 펴고 접음. 글자도 같이 바뀜 (js/product.js) -->
				<button type="button" class="item-brief__more" aria-expanded="false">필수
					표기 정보 더보기</button>
			</div>

			<!-- ===== 사기 거래 경고 =====
           원본 실측: 배경 #fce9e6 / 글자 #e12705 / 14px / padding 18px 16px / 모서리 4px
           ! 아이콘은 이미지가 아니라 CSS 로 그림(빨간 동그라미 + 느낌표) -->
			<p class="fraud-notice">판매자가 현금거래를 요구하면 거부하시고 즉시 사기 거래 신고센터
				(1670-9832)에 신고하시기 바랍니다.</p>

			<!-- ===== 상세설명 =====
           원본: div.product-detail-content — 이 페이지에서 제일 긴 영역(10,140px)인데
                 구조는 제일 단순함. 판매자가 올린 **긴 이미지 여러 장**이 전부.
                 폭 780px 고정 + 가운데 정렬(margin:0 auto).

           2026-08-28: PRODUCT_IMAGE(OPTION_ID 가 null, IMAGE_PURPOSE='상세설명') 연동함.
           ★ 임시 ★ 아직 등록된 사진이 없는 상품(대부분)은 회색 상자 대신 옛날 더미 3장으로 대체 —
             익스포트.sql 스냅샷 기준으로는 27번 상품에만 실제 상세설명 사진이 있음
             (라이브 DB엔 더 있을 수 있음, CLAUDE.md 참고). -->
			<div class="product-detail-content">
				<c:choose>
					<c:when test="${not empty detailImages}">
						<c:forEach items="${detailImages}" var="img">
							<div class="detail-image">
								<img src="${img:url(img.imageUrl)}"
									alt="">
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="detail-image">
							<img
								src="${pageContext.request.contextPath}/images/product-detail/detail-1.jpg"
								alt="">
						</div>
						<div class="detail-image">
							<img
								src="${pageContext.request.contextPath}/images/product-detail/detail-2.jpg"
								alt="">
						</div>
						<div class="detail-image">
							<img
								src="${pageContext.request.contextPath}/images/product-detail/detail-3.jpg"
								alt="">
						</div>
					</c:otherwise>
				</c:choose>
			</div>



			<!-- ===== 상품 리뷰 =====
           원본: div#sdpReview.product-review (높이 3,320px)
           실측: 위에 border-top 1px #333 / 위 여백 30px
                 좌우 2단 — 왼쪽 요약 360 / 오른쪽 목록 821 / 사이 24 (원본 폭 1205 기준)
                 우리는 본문이 1020 이라 왼쪽 300 고정 + 오른쪽은 남는 만큼 -->
			<section class="product-review" id="reviews">
				<h2 class="review-title">상품 리뷰</h2>

				<div class="review-body">

					<!-- ── 왼쪽: 요약 ───────────────────────── -->
					<div class="review-summary">

						<!-- 별점 + 리뷰 개수
                 ★ 옆의 큰 숫자는 "평균 평점"이 아니라 **리뷰 개수**임.
                   원본이 `★★★★☆ 17` 인데 그 상품 평점은 4.2 이고 리뷰가 17개였음
                   (JSON-LD 의 ratingValue 4.2 / ratingCount 17 로 확인).
                   별 그림이 평점을, 숫자가 개수를 나타내는 구조 -->
						<div class="review-score">
							<span class="star-rating" aria-label="평점 ${avgRating}점"><em
								style="width:${avgRating * 20}%"></em></span> <strong>${reviewCount}</strong>
						</div>

						<!-- 별점 분포 막대
                 ★ 막대 길이를 style="width:78%" 처럼 인라인으로 준 이유:
                   이 값만 데이터에 따라 매번 달라지기 때문. CSS 파일에는 못 적음.
                   ▶JSP 로 가면 style="width:{r.percent}%" 가 될 자리 -->
						<!-- <ul class="score-graph">
							<li><span class="label">최고</span> <span class="bar"><i
									style="width: 78%"></i></span> <span class="pct">78%</span></li>
							<li><span class="label">좋음</span> <span class="bar"><i
									style="width: 5%"></i></span> <span class="pct">5%</span></li>
							<li><span class="label">보통</span> <span class="bar"><i
									style="width: 0%"></i></span> <span class="pct">0%</span></li>
							<li><span class="label">별로</span> <span class="bar"><i
									style="width: 0%"></i></span> <span class="pct">0%</span></li>
							<li><span class="label">나쁨</span> <span class="bar"><i
									style="width: 17%"></i></span> <span class="pct">17%</span></li>
						</ul> -->

						<ul class="score-graph">
							<li><span class="label">최고</span> <span class="bar">
									<i style="width:${reviewStats.bestPercent}%"></i>
							</span> <span class="pct">${reviewStats.bestPercent}%</span></li>

							<li><span class="label">좋음</span> <span class="bar">
									<i style="width:${reviewStats.goodPercent}%"></i>
							</span> <span class="pct">${reviewStats.goodPercent}%</span></li>

							<li><span class="label">보통</span> <span class="bar">
									<i style="width:${reviewStats.normalPercent}%"></i>
							</span> <span class="pct">${reviewStats.normalPercent}%</span></li>

							<li><span class="label">별로</span> <span class="bar">
									<i style="width:${reviewStats.poorPercent}%"></i>
							</span> <span class="pct">${reviewStats.poorPercent}%</span></li>

							<li><span class="label">나쁨</span> <span class="bar">
									<i style="width:${reviewStats.badPercent}%"></i>
							</span> <span class="pct">${reviewStats.badPercent}%</span></li>
						</ul>

						<!-- 구매자 설문 요약 -->
						<dl class="review-survey">
							<div class="survey-row">
								<dt>사이즈</dt>
								<dd>정사이즈예요</dd>
								<span class="pct">80%</span>
							</div>
							<div class="survey-row">
								<dt>색상</dt>
								<dd>화면과 비슷해요</dd>
								<span class="pct">40%</span>
							</div>
						</dl>

						<!-- ★ "자세히 보기" 를 누르면 열리는 항목별 전체 분포 (2026-08-24 추가)
						     원본을 Playwright 로 직접 열어서 확인함 — 모달이 아니라 **그 자리에서 아래로 펼쳐짐**.
						     원본 실측: 항목명 110px 고정 / 막대 8px·radius 4px / 퍼센트 38px 우측정렬 /
						               사이 gap 12px, 그룹 안 gap 8px, 그룹 사이 gap 24px, 글자 14px/17px
						     ★ 1등 항목만 막대가 시안색(#34CAE2), 나머지는 회색 — 원본이 그렇게 구분함
						       (그래서 클래스 .is-top 을 1등에만 붙임)
						     평소엔 CSS 가 감춰두고, .is-open 이 붙으면 나타남 (js/product.js setupSurveyMore)
						     ▶JSP: &lt;c:forEach items="{survey}"&gt; 로 감쌀 자리 — 설문 항목 수가 카테고리마다 다름
						       (의류는 사이즈·색상, 식품은 맛·양 …). 퍼센트는 style="width:{o.percent}%" -->
						<div class="survey-detail">
							<div class="survey-group">
								<p class="survey-group__title">사이즈</p>
								<div class="survey-opt is-top">
									<span class="name">정사이즈예요</span> <span class="bar"><i
										style="width: 80%"></i></span> <span class="pct">80%</span>
								</div>
								<div class="survey-opt">
									<span class="name">생각보다 커요</span> <span class="bar"><i
										style="width: 20%"></i></span> <span class="pct">20%</span>
								</div>
							</div>
							<div class="survey-group">
								<p class="survey-group__title">색상</p>
								<div class="survey-opt is-top">
									<span class="name">화면과 비슷해요</span> <span class="bar"><i
										style="width: 40%"></i></span> <span class="pct">40%</span>
								</div>
								<div class="survey-opt">
									<span class="name">화면과 같아요</span> <span class="bar"><i
										style="width: 40%"></i></span> <span class="pct">40%</span>
								</div>
								<div class="survey-opt">
									<span class="name">화면과 달라요</span> <span class="bar"><i
										style="width: 20%"></i></span> <span class="pct">20%</span>
								</div>
							</div>
						</div>

						<!-- 글자가 "자세히 보기" ↔ "접기" 로 바뀜 (원본과 동일). 화살표는 CSS ::after 로 그림 -->
						<button type="button" class="survey-more" aria-expanded="false">자세히
							보기</button>

						<!-- 안내 문구 — 리뷰가 상품(productId)에 붙지 판매자에 붙는 게 아니라는 뜻.
                 나중에 DB 설계할 때 리뷰 테이블이 어디에 연결되는지와 같은 얘기 -->
						<p class="review-notice">동일한 상품에 대해 작성된 상품 리뷰로, 판매자는 다를 수
							있습니다.</p>
						<a href="#" class="review-policy">상품리뷰 운영원칙</a>
					</div>

					<!-- ── 오른쪽: 목록 ───────────────────────── -->
					<div class="review-list">

						<!-- 리뷰 사진 모아보기 — 누르면 갤러리 모달이 열림 (js/product.js setupReviewGallery)

						     ★ 안이 비어 있는 게 맞음. JS 가 **아래 리뷰 카드들의 .review-photos 를 훑어서**
						       사진을 모아 여기에 채움.
						     ★ 왜 이렇게 바꿨나 (2026-08-24):
						       전에는 여기에 사진 목록을 손으로 적고 data-review 로 주인을 표시했음.
						       그런데 "어느 사진이 누구 리뷰인지" 가 갤러리와 리뷰 카드 **두 군데**에 적히는 구조라
						       실제로 어긋났음 — 갤러리는 review-4 를 최*영 것이라 했는데
						       최*영 카드에는 사진이 없어서, 전체보기로 가면 사진이 없었음.
						       이제 사진의 출처가 **리뷰 카드 하나뿐**이라 어긋날 수가 없음.
						     ▶JSP: 카드 안 사진만 &lt;c:forEach&gt; 로 찍어내면 이 줄은 그대로 두면 됨 -->
						<ul class="review-gallery"></ul>

						<!-- 정렬 / 검색 / 별점 필터 (2026-08-21 실제로 동작하게 만듦 — js/product.js setupReviewTools)
						     ★ 원본에 "별점순"은 없음. 베스트순·최신순 두 개뿐이고, 별점은 정렬이 아니라
						       옆의 select 로 "필터"만 함 (vp_04_review.jpeg 로 확인). 지금 구조 그대로 맞음.
						     각 .review-item 의 data-helpful 은 "도움이 돼요" 누적 수 — 화면엔 숫자가 안 보이지만
						     베스트순 정렬 기준으로 씀. 지금은 지어낸 더미 값.
						     ▶JSP: 정렬·필터·검색 전부 서버 쿼리(ORDER BY / WHERE)로 넘어갈 자리.
						       지금 JS 는 그 전까지 화면에서만 흉내내는 것 -->
						<div class="review-tools">
							<div class="review-sort">
								<a href="#" class="is-on" data-sort="best">베스트순</a> <a href="#"
									data-sort="latest">최신순</a>
							</div>
							<input type="text" class="review-search" placeholder="검색어를 입력하세요">
							<select class="review-filter">
								<option value="">모든 별점</option>
								<option value="5">5점</option>
								<option value="4">4점</option>
								<option value="3">3점</option>
								<option value="2">2점</option>
								<option value="1">1점</option>
							</select>
						</div>
						<!-- 리뷰 카드
                 ▶JSP: &lt;c:forEach items="{reviews}" var="r"&gt; 로 감쌀 자리.
                   그래서 3개를 **완전히 같은 구조**로 만들어둠 (안에 든 글자만 다름)
                   
                 ★ 원본은 리뷰 카드가 10개라 이 영역이 3,320px 인데 우리는 3개라 1,447px (44%).
                   **모자란 게 아니라 개수만 다른 것.** 카드 하나의 구조·크기는 원본과 같음.
                   DB 를 붙이면 &lt;c:forEach&gt; 가 리뷰 수만큼 찍어내므로 저절로 채워짐 —
                   지금 개수를 늘리려고 카드를 복붙할 필요 없음 (2026-08-21 확인) -->
						<c:choose>
							<c:when test="${not empty reviews}">
								<c:forEach items="${reviews}" var="r">
									<article class="review-item" data-rating="${r.rating}"
										data-review-id="${r.reviewNo}">
										<div class="review-head">
											<span class="review-avatar"></span>
											<div class="review-writer">
												<strong class="name">${r.maskedName}</strong> <span
													class="seller"> ${r.storeName} </span>
												<div class="meta">
													<span class="star-rating" aria-label="별점 ${r.rating}점">
														<em style="width:${r.rating * 20}%"></em>
													</span> <span class="date"> ${r.reviewDate} </span>
												</div>
											</div>
										</div>
										<c:if test="${not empty r.productName}">
											<p class="review-option">
												${r.productName}
												<c:if test="${not empty r.optionText}">
							· ${r.optionText}
						</c:if>
											</p>
										</c:if>
										<!-- 리뷰 사진 -->
										<c:if test="${not empty r.imageUrls}">
											<div class="review-photos">

												<c:forEach items="${r.imageUrls}" var="imageUrl">
													<div class="review-photo-item">

														<%-- <img src="${pageContext.request.contextPath}${imageUrl}"
															alt="리뷰 이미지" class="review-photo-img"> --%>
														<img src="${img:url(imageUrl)}"
															alt="리뷰 이미지" class="review-photo-img"
															onclick="openReviewImage(this.src)">
													</div>
												</c:forEach>
											</div>
										</c:if>

										<!-- 한줄 요약 -->
										<c:if test="${not empty r.reviewSummary}">
											<p class="review-headline">${r.reviewSummary}</p>
										</c:if>

										<!-- 상세 리뷰 -->
										<p class="review-text">${r.reviewContent}</p>

										<div class="review-foot">
											<button type="button" class="btn-helpful">도움이 돼요</button>

											<a href="#" class="btn-report"> 신고하기 </a>
										</div>

									</article>

								</c:forEach>

							</c:when>

							<c:otherwise>
								<p class="review-empty">조건에 맞는 후기가 없어요</p>
							</c:otherwise>

						</c:choose>



						<!-- 페이지네이션 — 원본 실측(ref/product/vp_05_review2.jpeg): "‹ (1) 2 ›" 모양,
						     지금 고른 페이지만 파란 원 테두리.
						     번호 버튼은 JS(setupReviewTools)가 리뷰 개수를 보고 그때그때 새로 그림 —
						     정렬·검색·별점 필터를 걸면 보이는 개수가 바뀌니까 페이지 수도 같이 바뀌어야 함.
						     ▶JSP: 총 페이지 수 = 서버가 리뷰 개수(2,033) ÷ 페이지당 개수로 계산해서 내려줄 자리.
						       지금은 더미 리뷰 5개를 3개씩 나눠 2페이지로 흉내냄 (js/product.js PAGE_SIZE). -->
						<c:if test="${fn:length(reviews) > 3}">
							<nav class="review-pagination">
								<button type="button" class="page-prev" aria-label="이전 페이지"
									disabled>‹</button>
								<span class="page-numbers"></span>
								<button type="button" class="page-next" aria-label="다음 페이지">
									›</button>
							</nav>
						</c:if>
						<!-- //.review-list -->
					</div>
				</div>
			</section>

			<!-- ===== 상품문의 =====
           원본: div#btf-qna (높이 500px)
           실측: 사방 테두리 1px #e5e7eb / 위아래 여백 26 / 안쪽 여백 30px 16px -->
			<section class="prod-qna" id="qna">
				<div class="qna-head">
					<h2 class="qna-title">상품문의</h2>
					<button type="button" class="btn-ask">문의하기</button>
				</div>

				<!-- 안내 5줄 — 실측 13px / #555. 일부만 굵게 (원본 그대로) -->
				<ul class="qna-guide">
					<li>구매한 상품의 <strong>취소/반품은 마이굿팡 구매내역에서 신청</strong> 가능합니다.
					</li>
					<li>상품문의 및 후기게시판을 통해 취소나 환불, 반품 등은 처리되지 않습니다.</li>
					<li><strong>가격, 판매자, 교환/환불 및 배송 등 해당 상품 자체와 관련 없는 문의는
							고객센터 내 1:1 문의하기</strong>를 이용해주세요.</li>
					<li><strong>"해당 상품 자체"와 관계없는 글, 양도, 광고성, 욕설, 비방, 도배
							등의 글은 예고 없이 이동, 노출제한, 삭제 등의 조치가 취해질 수 있습니다.</strong></li>
					<li>공개 게시판이므로 전화번호, 메일 주소 등 고객님의 소중한 개인정보는 절대 남기지 말아주세요.</li>
				</ul>

				<!-- 검색창 — 원본은 테두리가 유난히 진하고 두꺼움(#212b36 2px).
             돋보기는 이미지가 아니라 CSS 로 그림 (동그라미 + 45도 손잡이) -->
				<div class="qna-search">
					<input type="text" placeholder="궁금한 내용을 단어나 키워드로 검색하세요.">
					<button type="button" class="btn-search-qna">
						<span class="blind">검색</span>
					</button>
				</div>

				<p class="qna-empty">아직 문의가 없습니다.</p>

				<div class="qna-report">
					<span>판매 부적격 상품 또는 허위과장광고 및 지식재산권을 침해하는 상품의 경우 신고하여 주시기
						바랍니다.</span>
					<button type="button" class="btn-report-qna">신고하기</button>
				</div>
			</section>

			<!-- ===== 배송/교환/반품 안내 + 판매자 정보 =====
           원본: div#sdpEtc (높이 1,377px)

           ★ 여기 글은 거의 전부 **모든 상품이 똑같이 쓰는 고정 문구**임.
             상품마다 달라지는 건 맨 아래 "판매자 정보" 표뿐.
             ▶JSP 로 가면 이 절 전체를 &lt;jsp:include page="/WEB-INF/views/etc-policy.jsp"/&gt;
               로 빼고, 판매자 정보만 {seller.xxx} 로 채우면 됨 -->
			<section class="prod-etc" id="delivery">

				<!-- ── 배송정보 ───────────────────────── -->
				<h3 class="etc-title">배송정보</h3>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>배송방법</th>
							<td>순차배송</td>
							<!-- rowspan=2 : 세로로 두 칸을 하나로 합침.
                   왼쪽 두 줄(배송방법/묶음배송)에 배송비 한 칸이 걸쳐 있는 모양 -->
							<th rowspan="2">배송비</th>
							<td rowspan="2">무료배송<br> - 로켓배송 상품 중 19,800원 이상 구매 시
								무료배송<br> - 도서산간 지역 추가비용 없음
							</td>
						</tr>
						<tr>
							<th>묶음배송 여부</th>
							<td>가능</td>
						</tr>
						<tr>
							<th>배송기간</th>
							<!-- colspan=3 : 가로로 세 칸을 하나로 합침 -->
							<td colspan="3">• 굿팡친구 배송 지역 : 주문 및 결제 완료 후, 1-2일 이내 도착<br>
								• 굿팡친구 미배송 지역 : 주문 및 결제 완료 후, 2-3일 이내 도착<br> - 도서 산간 지역 등은
								하루가 더 소요될 수 있습니다.<br> • 천재지변, 물량 수급 변동 등 예외적인 사유 발생 시, 다소
								지연될 수 있는 점 양해 부탁드립니다.
							</td>
						</tr>
					</tbody>
				</table>

				<!-- ── 교환/반품 안내 ──────────────────── -->
				<h3 class="etc-title">교환/반품 안내</h3>
				<p class="etc-note">
					• 교환/반품에 관한 일반적인 사항은 판매자가 제시사항보다 관계법령이 우선합니다.<br> 다만, 판매자의
					제시사항이 관계법령보다 소비자에게 유리한 경우에는 판매자 제시사항이 적용됩니다.
				</p>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>교환/반품 비용</th>
							<td>• 와우멤버십 회원: 무료로 반품/교환 가능<br> • 와우멤버십 회원 아닌 경우:<br>
								1) [총 주문금액] - [반품 상품금액] = 19,800원 미만인 경우 반품비 5,000원<br> 2)
								[총 주문금액] - [반품 상품금액] = 19,800원 이상인 경우 반품비 2,500원
							</td>
						</tr>
						<tr>
							<th>교환/반품 신청 기준일</th>
							<td>• 단순변심에 의한 로켓배송 상품의 교환/반품은 제품 수령 후 30일 이내까지, 교환/반품 제한사항에
								해당하지 않는 경우에만 가능 (교환/반품 비용 고객부담)<br> • 상품의 내용이 표시·광고의 내용과 다른
								경우에는 상품을 수령한 날부터 3개월 이내, 그 사실을 안 날 또는 알 수 있었던 날부터 30일 이내에 청약철회
								가능
							</td>
						</tr>
					</tbody>
				</table>

				<!-- ── 교환/반품 제한사항 ──────────────── -->
				<h3 class="etc-title">교환/반품 제한사항</h3>
				<ul class="etc-list">
					<li>주문/제작 상품의 경우, 상품의 제작이 이미 진행된 경우</li>
					<li>상품 포장을 개봉하여 사용 또는 설치 완료되어 상품의 가치가 훼손된 경우 (단, 내용 확인을 위한 포장
						개봉의 경우는 예외)</li>
					<li>고객의 사용, 시간경과, 일부 소비에 의하여 상품의 가치가 현저히 감소한 경우</li>
					<li>세트상품 일부 사용, 구성품을 분실하였거나 취급 부주의로 인한 파손/고장/오염으로 재판매 불가한 경우</li>
					<li>모니터 해상도의 차이로 인해 색상이나 이미지가 실제와 달라, 고객이 단순 변심으로 교환/반품을 무료로
						요청하는 경우</li>
					<li>제조사의 사정 (신모델 출시 등) 및 부품 가격 변동 등에 의해 무료 교환/반품으로 요청하는 경우</li>
				</ul>
				<p class="etc-note">※ 각 상품별로 아래와 같은 사유로 취소/반품이 제한될 수 있습니다.</p>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>의류/잡화/수입명품</th>
							<td>• 상품의 택(TAG) 및 라벨의 멸실 또는 훼손, 상품의 사용 또는 훼손, 구성품 누락으로 상품의
								가치가 현저히 감소된 경우</td>
						</tr>
						<tr>
							<th>계절상품/식품/화장품</th>
							<td>• 신선/냉장/냉동 상품의 단순변심의 경우<br> • 뷰티 상품 이용 시 트러블(알러지,
								붉은 반점, 가려움, 따가움)이 발생하는 경우, 진료 확인서 및 소견서 등을 증빙하면 환불이 가능 (제반비용
								고객부담)
							</td>
						</tr>
						<tr>
							<th>전자/가전/설치상품</th>
							<td>• 전자제품 특성상 정품 스티커가 제거되었거나, 설치 또는 사용 이후에 단순변심인 경우<br>
								• 설치 또는 사용하여 재판매가 어려운 경우, 액정이 있는 상품의 전원을 켠 경우<br> • 홀로그램 등을
								분리, 분실, 훼손하여 상품의 가치가 현저히 감소하여 재판매가 불가할 경우 (노트북, 데스크탑 PC 등)
							</td>
						</tr>
						<tr>
							<th>자동차용품</th>
							<td>• 상품을 개봉하여 장착한 이후 단순변심인 경우</td>
						</tr>
						<tr>
							<th>CD/DVD/GAME/BOOK</th>
							<td>• 복제가 가능한 상품의 포장 등을 훼손한 경우</td>
						</tr>
					</tbody>
				</table>

				<!-- ── 판매자 정보 ────────────────────
             ★ 이 표만 상품(정확히는 판매자)마다 달라짐. 나머지는 전부 고정 문구.
               ▶JSP: {seller.name} {seller.tel} … 로 바뀔 자리 -->
				<h3 class="etc-title">판매자 정보</h3>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>소비자상담 및 교환반품 문의</th>
							<td colspan="3">굿팡고객센터 1577-0000<br> <span class="sub">배송
									및 환불/교환에 대한 문의는 마이굿팡-고객센터 내 상담하기를 이용해주세요.</span>
							</td>
						</tr>
						<tr>
							<th>상호/대표자</th>
							<td>${p.storeName}/${p.ceoName}</td>
							<th>사업장 소재지</th>
							<td>${p.businessAddress}${p.businessDetailAddress}</td>
						</tr>
						<tr>
							<th>e-mail</th>
							<td>${p.email}</td>
							<th>연락처</th>
							<td>${p.phone}</td>
						</tr>
						<tr>
							<th>통신판매업 신고번호</th>
							<td>${p.mailOrderNo}</td>
							<th>사업자번호</th>
							<td>${p.businessNo}</td>
						</tr>
						<tr>
							<th>구매안전 서비스</th>
							<td colspan="3">02-006-00042 <a href="#" class="link">서비스
									가입사실 확인 &gt;</a><br> <span class="sub">본 판매자는 고객님의
									안전거래를 위해 관련 법률에 의거하여 굿팡페이의 구매안전서비스를 적용하고 있습니다.</span>
							</td>
						</tr>

					</tbody>
				</table>

				<p class="etc-footnote">미성년자가 체결한 계약은 법정대리인이 동의하지 않는 경우 본인 또는
					법정대리인이 취소할 수 있습니다. 굿팡은 통신판매중개자로서 통신판매의 당사자가 아니며, 광고, 고시정보, 상품 주문,
					배송 및 환불의 의무와 책임은 각 판매자에게 있습니다.</p>
			</section>

		</div>
	</main>


	<!-- ★ 어사이드(날개배너) 없음 — 2026-08-20 판단
	     원본 쿠팡 상세페이지에도 **안 나옴**. DOM 에는 <article id="wa-sidebar"> 가 있지만
	     class 가 `fw-absolute fw-hidden` 뿐이라 숨겨져 있음 (Tailwind 의 fw-hidden = display:none).
	     메인페이지 원본은 같은 자리에 `s600:!fw-flex s1024:fw-block` 이 더 붙어서 되살아남.
	     → 쿠팡은 두 페이지에 같은 껍데기를 쓰되 상세페이지에서는 꺼버리는 것.
	     스크린샷(ref/product/vp_01_atf.jpeg) 오른쪽이 완전히 빈 것으로도 확인함.
	     ※ 이걸 빼면서 "날개배너가 푸터 아래로 삐져나오던 문제"도 같이 사라짐 -->


	<!-- 맨 위로 버튼 (스크롤 내리면 나타남)
       ↑ 화살표는 이미지가 아니라 CSS 로 그림 (테두리 2개만 남기고 45도 회전 —
         장바구니 ›, 오늘의 발견 > 와 같은 기법). 글자는 화면낭독기용으로만 남겨둠 -->
	<button type="button" id="goto-top" class="goto-top">
		<span class="blind">맨 위로</span>
	</button>


	<!-- ==================================================
       FOOTER — 페이지 맨 아래. 회사정보 / 약관 / 고객센터
       쿠팡 원본: footer#wa-footer (높이 428px)
       ================================================== -->
	<!-- ★ 이 푸터는 inc/footer.jsp 에서 그대로 옮겨온 것. 위 헤더와 같은 규칙 -->
	<jsp:include page="inc/footer.jsp" />


	<!-- ==================================================
	     리뷰 사진 갤러리 — 2단 구조 (2026-08-24)
	     원본을 Playwright 로 직접 열어서 확인한 흐름:
	       ① 리뷰 사진 썸네일 클릭 → "갤러리" 모달 (사진 전체를 격자로)
	       ② 그 안의 썸네일을 또 클릭 → "사진 뷰어" (큰 사진 + 좌우 화살표 + 리뷰 글)
	     둘 다 여기 미리 만들어두고 평소엔 CSS 가 감춰둠. 안의 내용은 JS 가 채움
	     (js/product.js setupReviewGallery).

	     ★ 왜 body 끝에 두는가: 모달은 화면 전체를 덮어야 하는데, 리뷰 섹션 안에 두면
	       부모의 overflow·position·z-index 에 갇혀서 잘리거나 뒤에 깔릴 수 있음.
	       화면 전체를 덮는 것은 문서 맨 끝에 두는 게 정석
	     ================================================== -->

	<!-- ── ① 갤러리 모달 ── 원본 실측: 흰 패널 982px / 덮개 rgba(17,17,17,0.87) / z-index 9999 -->
	<div class="review-gallery-modal" hidden>
		<div class="gallery-panel" role="dialog" aria-modal="true"
			aria-label="리뷰 사진 갤러리">
			<div class="gallery-head">
				<strong class="gallery-title">갤러리</strong> <span class="gallery-sub">섬네일을
					클릭하면 더 많은 상품평 정보를 볼 수 있어요</span>
				<button type="button" class="gallery-close">
					<span class="blind">갤러리 닫기</span>
				</button>
			</div>
			<div class="gallery-body">
				<!-- 사진 개수 — JS 가 숫자를 채움 -->
				<p class="gallery-count">
					이미지 <strong>0</strong>
				</p>
				<!-- 격자 — JS 가 li 를 만들어 넣음 (원본 실측 140x140, radius 4px) -->
				<ul class="gallery-grid"></ul>
			</div>
		</div>
	</div>

	<!-- ── ② 사진 뷰어 ── 원본 실측: 어두운 패널 rgb(34,34,34) / 982px -->
	<div class="photo-viewer" hidden>
		<div class="viewer-panel" role="dialog" aria-modal="true"
			aria-label="리뷰 사진 크게 보기">
			<!-- 위: 누가 쓴 리뷰인지 -->
			<div class="viewer-head">
				<span class="viewer-avatar"></span>
				<div class="viewer-writer">
					<strong class="name"></strong>
					<div class="meta">
						<span class="stars"></span> <span class="date"></span>
					</div>
				</div>
				<button type="button" class="viewer-close">
					<span class="blind">닫기</span>
				</button>
			</div>

			<!-- 가운데: 큰 사진 + 좌우 화살표 -->
			<div class="viewer-stage">
				<button type="button" class="viewer-prev">
					<span class="blind">이전 사진</span>
				</button>
				<img class="viewer-img" src="" alt="">
				<button type="button" class="viewer-next">
					<span class="blind">다음 사진</span>
				</button>
			</div>

			<!-- 사진 아래: 작은 썸네일 줄 (지금 보는 것에 파란 테두리). JS 가 채움 -->
			<ul class="viewer-thumbs"></ul>

			<!-- 맨 아래: 옵션 + 리뷰 글 + 전체보기 -->
			<div class="viewer-foot">
				<div class="viewer-text-wrap">
					<p class="viewer-option"></p>
					<p class="viewer-text"></p>
				</div>
				<button type="button" class="viewer-all">전체보기</button>
			</div>
		</div>
	</div>

	<!-- 상품상세 전용 와우 가입 모달 -->
	<div id="productWowModal" class="product-wow-overlay">
		<div class="product-wow-modal">

			<button type="button" id="productWowCloseBtn"
				class="product-wow-close">&times;</button>

			<!-- STEP 1 -->
			<div id="productWowStep1">

				<div class="product-wow-header">
					<div class="product-wow-logo">WOW</div>

					<h2>
						와우 멤버십으로<br> 이 상품을 무료배송 받으세요
					</h2>

					<p>지금 가입하면 바로 와우회원 혜택이 적용됩니다.</p>
				</div>

				<div class="product-wow-benefits">

					<div class="product-wow-benefit">
						<span class="product-wow-benefit-icon">🚀</span>

						<div>
							<strong>로켓배송 무료배송</strong>
							<p>금액에 상관없이 무료배송 혜택</p>
						</div>
					</div>

					<div class="product-wow-benefit">
						<span class="product-wow-benefit-icon">📦</span>

						<div>
							<strong>무료반품</strong>
							<p>와우회원 전용 무료반품 혜택</p>
						</div>
					</div>

					<div class="product-wow-benefit">
						<span class="product-wow-benefit-icon">💰</span>

						<div>
							<strong>와우 전용 혜택</strong>
							<p>다양한 할인과 적립 혜택</p>
						</div>
					</div>

				</div>

				<div class="product-wow-price">
					<span>월 이용료</span> <strong> 7,890 <em>원</em>
					</strong>
				</div>

				<div class="product-wow-actions">

					<button type="button" id="productWowCancelBtn"
						class="product-wow-btn-secondary">다음에 할게요</button>

					<button type="button" id="productWowNextBtn"
						class="product-wow-btn-primary">가입하고 무료배송 받기</button>

				</div>

			</div>

			<!-- STEP 2 -->
			<div id="productWowStep2" class="product-wow-step2"
				style="display: none;">
				<div class="product-wow-header product-wow-payment-header">
					<div class="product-wow-logo">WOW</div>
					<h2>결제수단을 선택해주세요</h2>
					<p>매월 등록된 결제수단으로 7,890원이 자동 결제됩니다.</p>
				</div>
				<div class="product-wow-payment-price">
					<span>오늘 결제금액</span> <strong>7,890원</strong>
				</div>
				<form action="${pageContext.request.contextPath}/wow/join"
					method="post" id="productWowPaymentForm">

					<input type="hidden" name="joinMode" value="modal"><input
						type="hidden" name="afterWowJoin" value="buy"> <input
						type="hidden" name="productNo" value="${p.productNo}"> <input
						type="hidden" name="optionId" id="productWowOptionId"> <input
						type="hidden" name="quantity" id="productWowQuantity"> <input
						type="hidden" name="color" id="productWowColor">

					<!-- AJAX로 결제수단 들어오는 곳 -->
					<div id="productWowPaymentMethodList"
						class="product-wow-payment-list">결제수단을 불러오는 중입니다...</div>
					<button type="button" id="productWowPaymentAddBtn"
						class="product-wow-payment-add-btn">
						<span>새 결제수단</span> <strong>+</strong>
					</button>
					<label class="product-wow-agree"> <input type="checkbox"
						id="productWowAgree" required> <span> 와우 멤버십 월
							7,890원 정기결제에 동의합니다. </span>
					</label>
					<div class="product-wow-actions">
						<button type="button" id="productWowBackBtn"
							class="product-wow-btn-secondary">이전</button>
						<button type="submit" class="product-wow-btn-primary">
							7,890원 결제하고 무료배송</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- 상품상세 와우 결제수단 추가 모달 -->
	<div id="productWowPaymentAddModal" class="product-wow-add-overlay">

		<div class="product-wow-add-modal">

			<button type="button" id="productWowPaymentAddCloseBtn"
				class="product-wow-add-close">&times;</button>

			<h2 class="product-wow-add-title">새 결제수단 등록</h2>

			<p class="product-wow-add-description">와우 멤버십 결제에 사용할 결제수단을
				등록해주세요.</p>

			<div class="product-wow-payment-tabs">

				<button type="button" class="product-wow-payment-tab active"
					data-type="BANK">계좌</button>

				<button type="button" class="product-wow-payment-tab"
					data-type="CARD">카드</button>

			</div>

			<form id="productWowPaymentAddForm">

				<input type="hidden" id="productWowPaymentType" name="paymentType"
					value="BANK">

				<!-- 계좌 -->
				<div id="productWowBankArea">

					<div class="product-wow-form-row">

						<label for="productWowBankCode"> 은행 </label> <select
							id="productWowBankCode" name="bankCode">

							<option value="">은행 선택</option>
							<option value="SHINHAN">신한은행</option>
							<option value="KB">KB국민은행</option>
							<option value="WOORI">우리은행</option>
							<option value="NH">NH농협은행</option>
							<option value="HANA">하나은행</option>
							<option value="KAKAO">카카오뱅크</option>
							<option value="TOSS">토스뱅크</option>

						</select>

					</div>

					<div class="product-wow-form-row">

						<label for="productWowAccountNumber"> 계좌번호 </label> <input
							type="text" id="productWowAccountNumber" maxlength="16"
							inputmode="numeric" placeholder="'-' 없이 입력">

					</div>

					<div class="product-wow-form-row">

						<label for="productWowAccountHolder"> 예금주 </label> <input
							type="text" id="productWowAccountHolder" maxlength="30"
							placeholder="예금주 이름">

					</div>

				</div>

				<!-- 카드 -->
				<div id="productWowCardArea" style="display: none;">

					<div class="product-wow-form-row">

						<label for="productWowCardCompany"> 카드사 </label> <select
							id="productWowCardCompany">

							<option value="">카드사 선택</option>
							<option value="SHINHAN">신한카드</option>
							<option value="KB">KB국민카드</option>
							<option value="SAMSUNG">삼성카드</option>
							<option value="HYUNDAI">현대카드</option>
							<option value="LOTTE">롯데카드</option>
							<option value="HANA">하나카드</option>

						</select>

					</div>

					<div class="product-wow-form-row">

						<label>카드번호</label>

						<div class="product-wow-card-number">

							<input type="text" id="productWowCard1" maxlength="4"
								inputmode="numeric"> <span>-</span> <input type="text"
								id="productWowCard2" maxlength="4" inputmode="numeric">

							<span>-</span> <input type="text" id="productWowCard3"
								maxlength="4" inputmode="numeric"> <span>-</span> <input
								type="text" id="productWowCard4" maxlength="4"
								inputmode="numeric">

						</div>

					</div>

				</div>

				<label class="product-wow-default-payment"> <input
					type="checkbox" name="paymentDefault" value="Y"> 기본 결제수단으로
					설정

				</label>

				<div class="product-wow-add-actions">

					<button type="button" id="productWowPaymentAddCancelBtn"
						class="product-wow-add-cancel">취소</button>

					<button type="submit" class="product-wow-add-submit">등록하기
					</button>

				</div>

			</form>

		</div>

	</div>

	<script>
    const contextPath = "${pageContext.request.contextPath}";
</script>

	<!-- JS는 </body> 바로 앞에! HTML을 다 읽은 뒤에 실행되게 하려고 -->
	<script src="${pageContext.request.contextPath}/js/header.js"></script>
	<script src="${pageContext.request.contextPath}/js/product.js"></script>

	<script>
	document.addEventListener("DOMContentLoaded", function () {

    const openBtn = document.getElementById("wowJoinOpenBtn");

    const modal =
        document.getElementById("productWowModal");

    const closeBtn =
        document.getElementById("productWowCloseBtn");

    const cancelBtn =
        document.getElementById("productWowCancelBtn");

    const nextBtn =
        document.getElementById("productWowNextBtn");

    const backBtn =
        document.getElementById("productWowBackBtn");

    const step1 =
        document.getElementById("productWowStep1");

    const step2 =
        document.getElementById("productWowStep2");

    const paymentList =
        document.getElementById("productWowPaymentMethodList");

    const paymentForm =
        document.getElementById("productWowPaymentForm");
    
    const paymentAddBtn =
        document.getElementById("productWowPaymentAddBtn");

    const paymentAddModal =
        document.getElementById("productWowPaymentAddModal");

    const paymentAddCloseBtn =
        document.getElementById("productWowPaymentAddCloseBtn");

    const paymentAddCancelBtn =
        document.getElementById("productWowPaymentAddCancelBtn");

    const paymentAddForm =
        document.getElementById("productWowPaymentAddForm");

    const paymentType =
        document.getElementById("productWowPaymentType");

    const paymentTabs =
        document.querySelectorAll(".product-wow-payment-tab");

    const bankArea =
        document.getElementById("productWowBankArea");

    const cardArea =
        document.getElementById("productWowCardArea");
    
    paymentAddForm.addEventListener(
    	    "submit",
    	    async function (event) {

    	        event.preventDefault();

    	        const type = paymentType.value;

    	        const params =
    	            new URLSearchParams();

    	        params.set(
    	            "paymentType",
    	            type
    	        );

    	        const defaultPayment =
    	            paymentAddForm.querySelector(
    	                '[name="paymentDefault"]'
    	            );

    	        params.set(
    	            "paymentDefault",
    	            defaultPayment &&
    	            defaultPayment.checked
    	                ? "Y"
    	                : "N"
    	        );

    	        if (type === "BANK") {

    	            const bankCode =
    	                document.getElementById(
    	                    "productWowBankCode"
    	                );

    	            const accountNumber =
    	                document.getElementById(
    	                    "productWowAccountNumber"
    	                );

    	            const accountHolder =
    	                document.getElementById(
    	                    "productWowAccountHolder"
    	                );

    	            if (!bankCode.value) {
    	                alert("은행을 선택해주세요.");
    	                return;
    	            }

    	            if (!accountNumber.value.trim()) {
    	                alert("계좌번호를 입력해주세요.");
    	                return;
    	            }

    	            if (!accountHolder.value.trim()) {
    	                alert("예금주를 입력해주세요.");
    	                return;
    	            }

    	            params.set(
    	                "bankCode",
    	                bankCode.value
    	            );

    	            params.set(
    	                "accountNumber",
    	                accountNumber.value.trim()
    	            );

    	            params.set(
    	                "accountHolder",
    	                accountHolder.value.trim()
    	            );
    	        }

    	        if (type === "CARD") {

    	            const cardCompany =
    	                document.getElementById(
    	                    "productWowCardCompany"
    	                );

    	            const card1 =
    	                document.getElementById(
    	                    "productWowCard1"
    	                ).value.replace(/\D/g, "");

    	            const card2 =
    	                document.getElementById(
    	                    "productWowCard2"
    	                ).value.replace(/\D/g, "");

    	            const card3 =
    	                document.getElementById(
    	                    "productWowCard3"
    	                ).value.replace(/\D/g, "");

    	            const card4 =
    	                document.getElementById(
    	                    "productWowCard4"
    	                ).value.replace(/\D/g, "");

    	            const cardNumber =
    	                card1 + card2 + card3 + card4;

    	            if (!cardCompany.value) {
    	                alert("카드사를 선택해주세요.");
    	                return;
    	            }

    	            if (cardNumber.length !== 16) {
    	                alert("카드번호 16자리를 입력해주세요.");
    	                return;
    	            }

    	            params.set(
    	                "cardCompany",
    	                cardCompany.value
    	            );

    	            params.set(
    	                "cardNumber",
    	                cardNumber
    	            );
    	        }

    	        try {

    	            const response =
    	                await fetch(
    	                    contextPath
    	                    + "/wow/payment-method",
    	                    {
    	                        method: "POST",
    	                        headers: {
    	                            "Content-Type":
    	                                "application/x-www-form-urlencoded; charset=UTF-8"
    	                        },
    	                        body: params.toString()
    	                    }
    	                );

    	            if (response.status === 401) {
    	                window.location.href =
    	                    contextPath + "/login";
    	                return;
    	            }

    	            if (!response.ok) {

    	                const message =
    	                    await response.text();

    	                throw new Error(
    	                    message ||
    	                    "결제수단 등록에 실패했습니다."
    	                );
    	            }

    	            // 결제수단 목록 다시 조회
    	            await loadWowPaymentMethods();

    	            // 추가 모달 닫기
    	            closePaymentAddModal();

    	            // 입력폼 초기화
    	            paymentAddForm.reset();

    	            paymentType.value = "BANK";

    	            paymentTabs.forEach(function (tab) {
    	                tab.classList.toggle(
    	                    "active",
    	                    tab.dataset.type === "BANK"
    	                );
    	            });

    	            bankArea.style.display = "block";
    	            cardArea.style.display = "none";

    	        } catch (error) {

    	            console.error(error);
    	            alert(error.message);
    	        }
    	    }
    	);
    
    paymentTabs.forEach(function (tab) {

        tab.addEventListener("click", function () {

            paymentTabs.forEach(function (item) {
                item.classList.remove("active");
            });

            tab.classList.add("active");

            const type = tab.dataset.type;

            paymentType.value = type;

            if (type === "BANK") {
                bankArea.style.display = "block";
                cardArea.style.display = "none";
            } else {
                bankArea.style.display = "none";
                cardArea.style.display = "block";
            }
        });
    });
    
    paymentAddBtn.addEventListener("click", function () {
        paymentAddModal.style.display = "flex";
    });

    function closePaymentAddModal() {
        paymentAddModal.style.display = "none";
    }

    paymentAddCloseBtn.addEventListener(
        "click",
        closePaymentAddModal
    );

    paymentAddCancelBtn.addEventListener(
        "click",
        closePaymentAddModal
    );

    paymentAddModal.addEventListener("click", function (event) {
        if (event.target === paymentAddModal) {
            closePaymentAddModal();
        }
    });

    if (!openBtn || !modal) {
        return;
    }

    openBtn.addEventListener("click", function () {
        modal.style.display = "flex";
        step1.style.display = "block";
        step2.style.display = "none";
    });

/*     nextBtn.addEventListener("click", async function () {

        try {
            await loadWowPaymentMethods();

            step1.style.display = "none";
            step2.style.display = "block";

        } catch (error) {
            console.error(error);

            paymentList.innerHTML =
                "결제수단을 불러오지 못했습니다.";
        }
    }); */
    
    nextBtn.addEventListener("click", async function () {

        try {
            const loaded = await loadWowPaymentMethods();

            if (!loaded) {
                return;
            }

            step1.style.display = "none";
            step2.style.display = "block";

        } catch (error) {
            console.error(error);
            paymentList.innerHTML =
                "결제수단을 불러오지 못했습니다.";
        }
    });

    function closeModal() {
        modal.style.display = "none";
        step1.style.display = "block";
        step2.style.display = "none";
    }

    closeBtn.addEventListener("click", closeModal);
    cancelBtn.addEventListener("click", closeModal);

    modal.addEventListener("click", function (event) {

        if (event.target === modal) {
            closeModal();
        }
    });

/*     async function loadWowPaymentMethods() {

        paymentList.innerHTML =
            "결제수단을 불러오는 중입니다...";
        const response = await fetch(
            contextPath + "/wow/payment-method",
            {
                method: "GET"
            }
        );

        if (response.status === 401) {
            window.location.href =
                contextPath + "/login";

            return;
        }

        if (!response.ok) {
            throw new Error(
                "결제수단을 불러오지 못했습니다."
            );
        }
        const html =
            await response.text();

        paymentList.innerHTML = html;
    }
     */
     async function loadWowPaymentMethods() {

    	    paymentList.innerHTML =
    	        "결제수단을 불러오는 중입니다...";
    	    const response = await fetch(
    	        contextPath + "/wow/payment-method",
    	        {
    	            method: "GET"
    	        }
    	    );

    	    if (response.status === 401) {
    	        window.location.replace(
    	            contextPath + "/login"
    	        );
    	        return false;
    	    }

    	    if (!response.ok) {
    	        throw new Error(
    	            "결제수단을 불러오지 못했습니다."
    	        );
    	    }

    	    const html =
    	        await response.text();
    	    paymentList.innerHTML = html;
    	    return true;
    	}
     

    paymentForm.addEventListener("submit", function (event) {

        const selectedPayment =
            paymentForm.querySelector(
                'input[name="paymentMethodNo"]:checked'
            );

        if (!selectedPayment) {
            event.preventDefault();

            alert("결제수단을 선택해주세요.");

            return;
        }

        const optionId =
            document.getElementById("selectedOptionId");

        const quantity =
            document.querySelector(".qty-input");

        const color =
            document.getElementById("selectedColor");

        document.getElementById(
            "productWowOptionId"
        ).value =
            optionId ? optionId.value : "";

        document.getElementById(
            "productWowQuantity"
        ).value =
            quantity ? quantity.value : "1";

        document.getElementById(
            "productWowColor"
        ).value =
            color ? color.value : "";
    });
});
	
	
</script>
	<div id="reviewImageModal" class="review-image-modal"
		onclick="closeReviewImage()">

		<div class="review-image-modal-content"
			onclick="event.stopPropagation();">

			<button type="button" class="review-image-close"
				onclick="closeReviewImage()">×</button>

			<img id="reviewImageLarge" src="" alt="확대된 리뷰 이미지">
		</div>
	</div>
	<script>
function openReviewImage(src) {
	const modal = document.getElementById("reviewImageModal");
	const image = document.getElementById("reviewImageLarge");

	image.src = src;
	modal.classList.add("show");

	document.body.style.overflow = "hidden";
}

function closeReviewImage() {
	const modal = document.getElementById("reviewImageModal");
	const image = document.getElementById("reviewImageLarge");

	modal.classList.remove("show");
	image.src = "";

	document.body.style.overflow = "";
}

document.addEventListener("keydown", function(event) {
	if (event.key === "Escape") {
		closeReviewImage();
	}
});
</script>
</body>
</html>

