<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">

<header id="header">

	<!-- ============================= -->

	<!-- 상단 유틸바 -->

	<!-- ============================= -->

	<div class="top-bar">

		<div class="top-bar-inner">

			<!-- 왼쪽 -->

			<ul class="top-bar-left">

				<li class="new-header"><a href="#" class="bookmark"> 즐겨찾기 </a>

				</li>

				<li class="vendor-join"><a href="#"> 입점신청 <i class="ic"></i>

				</a>

					<ul class="vendor-join-menu">

						<li><a href="#">오픈마켓</a></li>

						<li><a href="#">여행·티켓</a></li>

						<li><a href="#">로켓배송</a></li>

						<li><a href="#">제휴마케팅</a></li>

						<li><a href="#">로켓그로스</a></li>

					</ul></li>

			</ul>

			<!-- 오른쪽 -->

			<ul class="top-bar-right">

				<li><a href="${pageContext.request.contextPath}/vendor/login">

						판매자 로그인 </a></li>

				<li class="cs-center"><a href="#"> 고객센터 </a>

					<ul class="cs-menu">

						<li><a href="#">자주묻는 질문</a></li>

						<li><a href="#">1:1 채팅문의</a></li>

						<li><a href="#">고객의 소리</a></li>

						<li><a href="#">취소 / 반품 안내</a></li>

					</ul></li>
				<c:choose>
					<c:when test="${not empty sessionScope.loginMember}">
						<li><span>${sessionScope.loginMember.memberName}님</span></li>

						<li><a href="${pageContext.request.contextPath}/logout">
								로그아웃 </a></li>
					</c:when>

					<c:otherwise>
						<li><a href="${pageContext.request.contextPath}/signup">
								회원가입 </a></li>
						<li><a href="${pageContext.request.contextPath}/login">
								로그인 </a></li>
					</c:otherwise>

				</c:choose>

			</ul>

		</div>

	</div>



	<!-- ============================= -->

	<!-- 헤더 본체 -->

	<!-- ============================= -->

	<div class="header-body">

		<div class="header-inner">



			<!-- ============================= -->

			<!-- 카테고리 -->

			<!-- ============================= -->

			<div class="category-wrap">

				<button type="button" class="category-btn">

					<span class="category-bars"> <i></i> <i></i> <i></i>

					</span> <span class="category-text"> 카테고리 </span>

				</button>



				<!-- 카테고리 펼침 -->

				<div class="category-layer" id="wa-pc-category">

					<ul class="menu">

						<!-- 뷰티 -->

						<li><a href="#"> 뷰티 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">스킨케어</a></li>

									<li><a href="#">메이크업</a></li>

									<li><a href="#">클렌징</a></li>

									<li><a href="#">헤어케어</a></li>

									<li><a href="#">바디케어</a></li>

									<li><a href="#">향수</a></li>

								</ul>

							</div></li>



						<!-- 여성패션 -->

						<li><a href="#"> 여성패션 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">원피스</a></li>

									<li><a href="#">블라우스/셔츠</a></li>

									<li><a href="#">티셔츠</a></li>

									<li><a href="#">팬츠</a></li>

									<li><a href="#">스커트</a></li>

									<li><a href="#">아우터</a></li>

								</ul>

							</div></li>



						<!-- 남성패션 -->

						<li><a href="#"> 남성패션 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">티셔츠</a></li>

									<li><a href="#">셔츠</a></li>

									<li><a href="#">팬츠</a></li>

									<li><a href="#">아우터</a></li>

									<li><a href="#">정장</a></li>

									<li><a href="#">언더웨어</a></li>

								</ul>

							</div></li>



						<!-- 식품 -->

						<li><a href="#"> 식품 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">신선식품</a></li>

									<li><a href="#">정육/계란</a></li>

									<li><a href="#">쌀/잡곡</a></li>

									<li><a href="#">간편식</a></li>

									<li><a href="#">과자/간식</a></li>

									<li><a href="#">음료/생수</a></li>

								</ul>

							</div></li>



						<!-- 가구 -->

						<li><a href="#"> 가구/홈인테리어 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">침대/매트리스</a></li>

									<li><a href="#">소파</a></li>

									<li><a href="#">책상/의자</a></li>

									<li><a href="#">수납/정리</a></li>

									<li><a href="#">조명</a></li>

									<li><a href="#">커튼/블라인드</a></li>

								</ul>

							</div></li>



						<!-- 가전 -->

						<li><a href="#"> 가전/디지털 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">노트북</a></li>

									<li><a href="#">모니터</a></li>

									<li><a href="#">TV</a></li>

									<li><a href="#">냉장고</a></li>

									<li><a href="#">세탁기</a></li>

									<li><a href="#">이어폰/헤드폰</a></li>

								</ul>

							</div></li>



						<!-- 문구 -->

						<li><a href="#"> 문구/오피스 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">노트/필기구</a></li>

									<li><a href="#">사무용지</a></li>

									<li><a href="#">파일/바인더</a></li>

									<li><a href="#">프린터용품</a></li>

									<li><a href="#">사무가구</a></li>

									<li><a href="#">포장용품</a></li>

								</ul>

							</div></li>



						<!-- 생활용품 -->

						<li><a href="#"> 생활용품 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">화장지/물티슈</a></li>

									<li><a href="#">세제</a></li>

									<li><a href="#">청소용품</a></li>

									<li><a href="#">욕실용품</a></li>

									<li><a href="#">수납/정리</a></li>

									<li><a href="#">벌레퇴치</a></li>

								</ul>

							</div></li>



						<!-- 스포츠 -->

						<li><a href="#"> 스포츠/레저 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">운동화</a></li>

									<li><a href="#">등산/캠핑</a></li>

									<li><a href="#">헬스/요가</a></li>

									<li><a href="#">자전거</a></li>

									<li><a href="#">골프</a></li>

									<li><a href="#">수영</a></li>

								</ul>

							</div></li>



						<!-- 건강 -->

						<li><a href="#"> 헬스/건강식품 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">비타민</a></li>

									<li><a href="#">홍삼</a></li>

									<li><a href="#">유산균</a></li>

									<li><a href="#">오메가3</a></li>

									<li><a href="#">단백질보충제</a></li>

									<li><a href="#">다이어트</a></li>

								</ul>

							</div></li>



						<!-- 출산 -->

						<li><a href="#"> 출산/유아동 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">기저귀</a></li>

									<li><a href="#">분유/이유식</a></li>

									<li><a href="#">물티슈</a></li>

									<li><a href="#">유모차</a></li>

									<li><a href="#">카시트</a></li>

									<li><a href="#">젖병/수유</a></li>

								</ul>

							</div></li>



						<!-- 유아동패션 -->

						<li><a href="#"> 유아동패션 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">아기옷</a></li>

									<li><a href="#">아동복</a></li>

									<li><a href="#">아동신발</a></li>

									<li><a href="#">내복/잠옷</a></li>

									<li><a href="#">외출복</a></li>

									<li><a href="#">모자/양말</a></li>

								</ul>

							</div></li>



						<!-- 주방 -->

						<li><a href="#"> 주방용품 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">냄비/프라이팬</a></li>

									<li><a href="#">식기/그릇</a></li>

									<li><a href="#">컵/텀블러</a></li>

									<li><a href="#">조리도구</a></li>

									<li><a href="#">밀폐용기</a></li>

									<li><a href="#">주방수납</a></li>

								</ul>

							</div></li>



						<!-- 반려동물 -->

						<li><a href="#"> 반려동물용품 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">강아지사료</a></li>

									<li><a href="#">고양이사료</a></li>

									<li><a href="#">간식</a></li>

									<li><a href="#">배변용품</a></li>

									<li><a href="#">장난감</a></li>

									<li><a href="#">미용/목욕</a></li>

								</ul>

							</div></li>



						<!-- 완구 -->

						<li><a href="#"> 완구/취미 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">블록/조립</a></li>

									<li><a href="#">인형</a></li>

									<li><a href="#">보드게임</a></li>

									<li><a href="#">프라모델</a></li>

									<li><a href="#">악기</a></li>

									<li><a href="#">그림/공예</a></li>

								</ul>

							</div></li>



						<!-- 자동차 -->

						<li><a href="#"> 자동차용품 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">타이어</a></li>

									<li><a href="#">엔진오일</a></li>

									<li><a href="#">블랙박스</a></li>

									<li><a href="#">세차용품</a></li>

									<li><a href="#">방향제</a></li>

									<li><a href="#">실내용품</a></li>

								</ul>

							</div></li>



						<!-- 도서 -->

						<li><a href="#"> 도서/CD/DVD <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">소설/에세이</a></li>

									<li><a href="#">자기계발</a></li>

									<li><a href="#">어린이</a></li>

									<li><a href="#">만화</a></li>

									<li><a href="#">외국어</a></li>

									<li><a href="#">음반/DVD</a></li>

								</ul>

							</div></li>



						<!-- 여행 -->

						<li><a href="#"> 여행 <i class="si"></i>

						</a>

							<div class="depth">

								<ul>

									<li><a href="#">국내숙소</a></li>

									<li><a href="#">해외숙소</a></li>

									<li><a href="#">항공권</a></li>

									<li><a href="#">렌터카</a></li>

									<li><a href="#">투어/티켓</a></li>

									<li><a href="#">캠핑/글램핑</a></li>

								</ul>

							</div></li>

					</ul>

				</div>

			</div>



			<!-- ============================= -->

			<!-- 오른쪽 헤더 -->

			<!-- ============================= -->

			<div class="header-col">



				<!-- ============================= -->

				<!-- 로고 + 검색 + 마이쿠팡 -->

				<!-- ============================= -->

				<div class="header-main">



					<!-- GoodPang -->

					<h1 class="logo">

						<a href="${pageContext.request.contextPath}/"
							class="brand-goodpang"> GoodPang </a>

					</h1>



					<!-- 검색 -->

					<form class="search-form"
						action="${pageContext.request.contextPath}/search" method="get">

						<div class="select--category">

							<a href="#" class="select--category__current"> 전체 </a> <span
								class="select--category--button"></span>

						</div>



						<div class="header-search">

							<label for="search-keyword" class="blind"> 상품 검색 </label> <input
								type="text" id="search-keyword" name="keyword"
								class="search-keyword" placeholder="찾고 싶은 상품을 검색해보세요!"
								maxlength="150" autocomplete="off">



							<div class="headerPopupWords popularity-words">

								<div class="autocomplete_wrap"></div>

								<div class="history-btns">

									<span class="delete-all-kwdhistory del-button"> 전체삭제 </span> <span
										class="history-on-off on"> 최근검색어끄기 </span>

								</div>

							</div>

						</div>



						<!-- 마이크 -->

						<a href="#" class="speech-mic"> <span class="blind">

								음성검색 </span>

						</a>



						<!-- 검색 버튼 -->

						<button type="submit" class="search-btn" title="검색">

							<img
								src="${pageContext.request.contextPath}/images/icons/search.png"
								width="20" height="21" alt="검색">

						</button>

					</form>



					<!-- ============================= -->

					<!-- 마이쿠팡 / 장바구니 -->

					<!-- ============================= -->

					<ul class="icon-menus">



						<!-- 마이쿠팡 -->

						<li class="my-coupang"><a
							href="${pageContext.request.contextPath}/order/order_list"> <img
								src="${pageContext.request.contextPath}/images/icons/person.png"
								width="44" height="44" alt="마이쿠팡"> <span
								class="icon-label"> 마이쿠팡 </span>

						</a>



							<p class="my-coupang-menu">

								<span class="wrapper"> <i class="arrow"></i> <a
									href="${pageContext.request.contextPath}/order/order_list">

										주문목록 </a> <a href="#"> 취소/반품 </a> <a href="#"> 찜 리스트 </a>

								</span>

							</p></li>



						<!-- 장바구니 -->
						<li class="cart"><a
							href="${pageContext.request.contextPath}/cart"> <img
								src="${pageContext.request.contextPath}/images/icons/cart.png"
								width="44" height="44" alt="장바구니"> <em id="cartCount"
								class="cart-count"> ${empty sessionScope.cartCount ? 0 : sessionScope.cartCount}
							</em> <span class="icon-label">장바구니</span>
						</a>

							<div class="cart-preview">
								<!-- <span class="wrapper" id="cartPreviewWrapper"> <i
									class="arrow"></i> -->
								<span class="wrapper" id="cartPreviewWrapper"> <i
									class="cart-preview-arrow"></i>
								<c:choose>
										<c:when test="${not empty sessionScope.cartPreviewItems}">
											<ul class="cart-preview-list">
												<c:forEach var="item"
													items="${sessionScope.cartPreviewItems}">
													<li class="cart-preview-item"><a
														href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">
															<div class="cart-preview-image">
																<c:choose>
																	<c:when test="${not empty item.imageUrl}">
																		<img
																			src="${pageContext.request.contextPath}/${item.imageUrl}"
																			alt="${item.productName}">
																	</c:when>
																	<c:otherwise>
																		<span>이미지</span>
																	</c:otherwise>
																</c:choose>
															</div>

															<div class="cart-preview-info">
																<p class="cart-preview-name">${item.productName}</p>

																<%-- <p class="cart-preview-price">
																	<fmt:formatNumber value="${item.unitPrice}"
																		pattern="#,###" />
																	원
																</p> --%>

																<p class="cart-preview-quantity">수량
																	${item.quantity}개</p>
															</div>
													</a></li>
												</c:forEach>
											</ul>
										</c:when>

										<c:otherwise>
											<div class="cart-preview-empty">장바구니에 담은 상품이 없습니다.</div>
										</c:otherwise>
									</c:choose> <a href="${pageContext.request.contextPath}/cart"
									class="cart-btn"> <span> 장바구니 전체보기 <!-- <i
											class="blue-arrow"></i> -->
									</span>
								</a>
								</span>
							</div></li>

					</ul>

				</div>



				<!-- ============================= -->

				<!-- 태블릿 검색 -->

				<!-- ============================= -->

				<div class="search-row">

					<form class="search-form"
						action="${pageContext.request.contextPath}/search" method="get">



						<div class="select--category">

							<a href="#" class="select--category__current"> 전체 </a> <span
								class="select--category--button"></span>

						</div>



						<div class="header-search">

							<label for="search-keyword-tablet" class="blind"> 상품 검색 </label>

							<input type="text" id="search-keyword-tablet" name="keyword"
								class="search-keyword" placeholder="찾고 싶은 상품을 검색해보세요!"
								maxlength="150" autocomplete="off">



							<div class="headerPopupWords popularity-words">

								<div class="autocomplete_wrap"></div>

								<div class="history-btns">

									<span class="delete-all-kwdhistory del-button"> 전체삭제 </span> <span
										class="history-on-off on"> 최근검색어끄기 </span>

								</div>

							</div>

						</div>



						<a href="#" class="speech-mic"> <span class="blind">

								음성검색 </span>

						</a>



						<button type="submit" class="search-btn" title="검색">

							<img
								src="${pageContext.request.contextPath}/images/icons/search.png"
								width="20" height="21" alt="검색">

						</button>

					</form>

				</div>



				<!-- ============================= -->

				<!-- GNB -->

				<!-- ============================= -->

				<nav class="gnb">

					<h2 class="blind">주요 서비스 바로가기</h2>



					<div class="gnb-menu-container">



						<button type="button" class="gnb-menu-btn gnb-menu-btn-left"
							disabled>

							<span class="blind"> 이전 메뉴 보기 </span>

						</button>



						<div class="gnb-menu-viewport">



							<ul class="gnb-menu-scroll">



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/coupang-play.png"
										alt=""> <span> 굿팡플레이 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/rocket-delivery.png"
										alt=""> <span> 로켓배송 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/rocket-fresh.png"
										alt=""> <span> 로켓프레시 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/fbi_icon_3x.png"
										alt=""> <span> 다시 구매 </span> <i class="in"> N <span
											class="blind"> 신규 </span>

									</i>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/biz.png"
										alt=""> <span> 굿팡비즈 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/oversea-delivery.png"
										alt=""> <span> 로켓직구 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/gold-box.png"
										alt=""> <span> 골드박스 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/new-item-of-month.png"
										alt=""> <span> 이달의 신상 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/sell-on-coupang.png"
										alt=""> <span> 입점신청 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/omp.png"
										alt=""> <span> 판매자특가 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/wow.png"
										alt=""> <span> 와우회원 할인 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/benefit.png"
										alt=""> <span> 이벤트/쿠폰 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/returned-market.png"
										alt=""> <span> 반품마켓 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/sustainable-market.png"
										alt=""> <span> 착한상점 </span> <i class="in"> N <span
											class="blind"> 신규 </span>

									</i>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/event.png"
										alt=""> <span> 기획전 </span>

								</a></li>



								<li class="gnb-menu-item"><a href="#"> <img
										src="${pageContext.request.contextPath}/images/gnb/travel.png"
										alt=""> <span> 굿팡트래블 </span> <i class="in"> N <span
											class="blind"> 신규 </span>

									</i>

								</a></li>



							</ul>

						</div>



						<button type="button"
							class="gnb-menu-btn

                                       gnb-menu-btn-right

                                       gnb-menu-btn-active">

							<span class="blind"> 다음 메뉴 보기 </span>

						</button>



					</div>

				</nav>

			</div>

		</div>

	</div>

</header>

<script>
(function() {
    const cartContextPath = '${pageContext.request.contextPath}';
    const isLoggedIn = ${not empty sessionScope.loginMember ? 'true' : 'false'};

    async function refreshCart() {
        /* if (!isLoggedIn) return; */

        try {
            const response = await fetch(cartContextPath + '/cart/status', {
                method: 'GET',
                cache: 'no-store',
                credentials: 'same-origin'
            });

            if (!response.ok) return;

            const data = await response.json();
            const cartCount = document.getElementById('cartCount');

            if (cartCount) {
                cartCount.textContent = data.count ?? 0;
            }

            const wrapper = document.getElementById('cartPreviewWrapper');

            if (!wrapper) return;

            let html = '<i class="arrow"></i>';

            if (!data.items || data.items.length === 0) {
                html += '<div class="cart-preview-empty">'
                      + '장바구니에 담은 상품이 없습니다.'
                      + '</div>';
            } else {
                html += '<ul class="cart-preview-list">';

                data.items.forEach(function(item) {
                    let imageHtml = '<span>이미지</span>';

                    if (item.imageUrl) {
                        imageHtml =
                            '<img src="' + getCartImageUrl(item.imageUrl) + '"'
                            + ' alt="' + escapeHtml(item.productName) + '">';
                    }
                    
                    let optionText = '';

                    if (item.option1Value) {
                        optionText += (item.option1Type ? item.option1Type + ': ' : '')
                                   + item.option1Value;
                    }

                    if (item.option2Value) {
                        if (optionText) optionText += ' / ';

                        optionText += (item.option2Type ? item.option2Type + ': ' : '')
                                   + item.option2Value;
                    }

                    if (item.option3Value) {
                        if (optionText) optionText += ' / ';

                        optionText += (item.option3Type ? item.option3Type + ': ' : '')
                                   + item.option3Value;
                    }

                    let optionHtml = '';

                    if (optionText) {
                        optionHtml =
                            '<p class="cart-preview-option">'
                            + escapeHtml(optionText)
                            + '</p>';
                    }

                    html +=
                        '<li class="cart-preview-item">'
                        + '<a href="' + cartContextPath + '/product?productNo=' + item.productNo + '">'
                        + '<div class="cart-preview-image">' + imageHtml + '</div>'
                        + '<div class="cart-preview-info">'
                        + '<p class="cart-preview-name">' + escapeHtml(item.productName) + '</p>'
                        + optionHtml
                        + '<p class="cart-preview-quantity">수량 ' + item.quantity + '개</p>'
                        + '</div>'
                        + '</a>'
                        + '</li>';
                });

                html += '</ul>';
            }

            html += '<a href="' + cartContextPath + '/cart" class="cart-btn">'
                  + '<span>장바구니 전체보기</span>'
                  + '</a>';

            wrapper.innerHTML = html;

        } catch (error) {
            console.error('장바구니 갱신 실패', error);
        }
    }

    function getCartImageUrl(imageUrl) {
        if (!imageUrl) return '';

        imageUrl = String(imageUrl).replace(/^\/+/, '');

        return window.location.origin + cartContextPath + '/' + imageUrl;
    }

    function escapeHtml(value) {
        if (!value) return '';

        return String(value)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }

        document.addEventListener('DOMContentLoaded', refreshCart);
        window.addEventListener('focus', refreshCart);
    
})();
</script>