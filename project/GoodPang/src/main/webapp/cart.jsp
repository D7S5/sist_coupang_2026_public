<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>GoodPang | 장바구니</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/cart.css">
</head>

<body>
	<header class="cart-header">
		<div class="header-inner">
			<h1 class="logo">
				<a href="${pageContext.request.contextPath}/" title="GoodPang 홈으로">
					<span class="brand-goodpang">GoodPang</span>
				</a>
			</h1>
			<div class="cart-step">
				<span class="step">01 옵션선택</span> &gt; <span class="step active">02
					장바구니</span> &gt; <span class="step">03 주문/결제</span> &gt; <span
					class="step">04 주문완료</span>
			</div>
		</div>
	</header>
	<main class="cart-container">

		<h2 class="cart-title">
			&lt; 장바구니(<span id="total-count">${cartCount}</span>)
		</h2>
		<div class="cart-content">
			<section class="cart-left">
				<div class="cart-box">
					<div class="box-header">
						<span class="rocket-badge"> 로켓배송 상품 </span> <span class="sub-text">
							무료배송 - 19,800원 이상 주문 가능 </span>
					</div>
					<c:if test="${empty cartItems}">
						<div class="empty-cart">
							<p>장바구니에 담긴 상품이 없습니다.</p>
						</div>
					</c:if>
					<c:forEach var="item" items="${cartItems}">
						<div class="cart-item" data-option-id="${item.optionId}"
							data-price="${item.unitPrice}">
							<input type="checkbox" class="item-chk" value="${item.optionId}"
								checked>


							<div class="item-img-placeholder">
								<c:choose>
									<c:when test="${not empty item.imageUrl}">
										<a
											href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">
											<img src="${pageContext.request.contextPath}/${item.imageUrl}"
											alt="${item.productName}" class="cart-item-img">
										</a>
									</c:when>

									<c:otherwise>
										<span>이미지 없음</span>
									</c:otherwise>
								</c:choose>
							</div>


							<div class="item-info">
								<p class="item-title">
									<a
										href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">
										${item.productName} </a>
								</p>
								<p class="item-option">
									옵션:
									<c:if test="${not empty item.option1Value}">
										${item.option1Type}: ${item.option1Value}
										</c:if>
									<c:if test="${not empty item.option2Value}">
										/ ${item.option2Type}: ${item.option2Value}
										</c:if>
									<c:if test="${not empty item.option3Value}">
										/ ${item.option3Type}: ${item.option3Value}
										</c:if>
								</p>
								<p class="delivery-date">배송 예정</p>
								<div class="price-wrap">

									<span class="price"> <fmt:formatNumber
											value="${item.unitPrice}" pattern="#,###" />원
									</span>
								</div>
								<form method="post"
									action="${pageContext.request.contextPath}/cart/update"
									class="quantity-control">
									<input type="hidden" name="optionId" value="${item.optionId}">
									<button type="submit" class="btn-qty" name="quantity"
										value="${item.quantity - 1}"
										${item.quantity <= 1 ? 'disabled' : ''}>-</button>
									<input type="text" class="cart-quantity"
										value="${item.quantity}" readonly>
									<button type="submit" class="btn-qty" name="quantity"
										value="${item.quantity + 1}">+</button>
								</form>
							</div>
							<form method="post"
								action="${pageContext.request.contextPath}/cart/delete">

								<input type="hidden" name="optionId" value="${item.optionId}">
								<button type="submit" class="btn-delete">삭제</button>
							</form>
						</div>
					</c:forEach>
				</div>

				<c:if test="${not empty cartItems}">
					<div class="cart-select-actions">
						<label> <input type="checkbox" id="chk-all" checked>
							전체 선택 ( <span class="selected-count"> ${cartCount} </span> / <span
							class="total-count"> ${cartCount} </span> )
						</label>

						<button type="button" class="btn-action" id="btn-delete-selected">
							선택삭제</button>

					</div>
				</c:if>
			</section>

			<aside class="cart-right">
				<div class="order-summary-box">
					<h3>주문 예상 금액</h3>
					<div class="summary-row">
						<span> 총 상품 가격 </span> <span id="price-products"> <fmt:formatNumber
								value="${totalPrice}" pattern="#,###" />원
						</span>
					</div>
					<div class="summary-row">
						<span> 총 배송비 </span> <span id="price-delivery"> +0원 </span>
					</div>
					<hr>
					<div class="summary-row total">
						<span> 총 결제예상금액 </span> <span id="price-total"> <fmt:formatNumber
								value="${totalPrice}" pattern="#,###" />원
						</span>
					</div>
					<button type="button" class="btn-order" id="btn-order"
						${empty cartItems ? 'disabled' : ''}>구매하기</button>
				</div>
			</aside>
		</div>
<!-- ==========================================================================
     [장바구니 하단] 의류(티셔츠) 관련 추천 상품 영역 (실존 외부 이미지 링크 적용)
     ========================================================================== -->
<section class="personalized-gw" style="margin-top: 40px; border-top: 2px solid #333; padding-top: 20px;">
    
    <div class="ad-carousel">
        <div class="ad-carousel-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
            <h2 style="font-size: 18px; font-weight: bold; color: #111;">이런 의류 상품은 어떠신가요?</h2>
            <span class="ad-label" style="font-size: 11px; color: #888; border: 1px solid #ccc; padding: 2px 6px; border-radius: 2px;">추천</span>
        </div>

        <div class="ad-carousel-body">
            <ul class="ad-list" style="display: flex; gap: 16px; list-style: none; padding: 0; margin: 0; overflow-x: auto;">
                
                <!-- 상품 1: 오버핏 반팔 티셔츠 -->
                <li class="ad-card" style="width: 180px; flex-shrink: 0; border: 1px solid #eee; border-radius: 6px; padding: 10px; background: #fff;">
                    <a href="${pageContext.request.contextPath}/product/detail?productNo=38" style="text-decoration: none; color: inherit; display: block;">
                        <div class="ad-thumb" style="width: 100%; height: 180px; overflow: hidden; border-radius: 4px; background: #f9f9f9; margin-bottom: 8px;">
                            <img src="https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=300&auto=format&fit=crop" 
                                 alt="데일리 오버핏 피그먼트 반팔 티셔츠" 
                                 style="width: 100%; height: 100%; object-fit: cover;" />
                        </div>
                        <span class="ad-name" style="font-size: 13px; color: #333; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 36px; line-height: 1.4; margin-bottom: 6px;">
                            데일리 오버핏 피그먼트 반팔 티셔츠 (5color)
                        </span>
                        <span class="ad-tag ad-tag--free" style="font-size: 10px; color: #0073e9; background: #f0f7ff; padding: 2px 4px; border-radius: 2px; font-weight: bold;">무료배송</span>
                        <div class="ad-price" style="margin-top: 6px; font-size: 15px; font-weight: bold; color: #111;">
                            <strong>14,900원</strong>
                        </div>
                        <div class="ad-rating" style="font-size: 11px; color: #888; margin-top: 4px;">
                            <span class="stars" style="color: #ffa500;">★★★★★</span>
                            <span class="count">(1,240)</span>
                        </div>
                    </a>
                </li>

                <!-- 상품 2: 순면 무지 티셔츠 -->
                <li class="ad-card" style="width: 180px; flex-shrink: 0; border: 1px solid #eee; border-radius: 6px; padding: 10px; background: #fff;">
                    <a href="${pageContext.request.contextPath}/product/detail?productNo=38" style="text-decoration: none; color: inherit; display: block;">
                        <div class="ad-thumb" style="width: 100%; height: 180px; overflow: hidden; border-radius: 4px; background: #f9f9f9; margin-bottom: 8px;">
                            <img src="https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=300&auto=format&fit=crop" 
                                 alt="100% 프리미엄 순면 라운드 반팔 티셔츠" 
                                 style="width: 100%; height: 100%; object-fit: cover;" />
                        </div>
                        <span class="ad-name" style="font-size: 13px; color: #333; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 36px; line-height: 1.4; margin-bottom: 6px;">
                            [1+1] 100% 프리미엄 순면 라운드 반팔 티셔츠 3팩
                        </span>
                        <span class="ad-tag ad-tag--free" style="font-size: 10px; color: #00891a; background: #f0fdf4; padding: 2px 4px; border-radius: 2px; font-weight: bold;">🚀 로켓배송</span>
                        <div class="ad-price" style="margin-top: 6px; font-size: 15px; font-weight: bold; color: #e61328;">
                            <span style="font-size: 12px; color: #999; text-decoration: line-through; font-weight: normal; margin-right: 4px;">25,000원</span>
                            <strong>18,900원</strong>
                        </div>
                        <div class="ad-rating" style="font-size: 11px; color: #888; margin-top: 4px;">
                            <span class="stars" style="color: #ffa500;">★★★★☆</span>
                            <span class="count">(852)</span>
                        </div>
                    </a>
                </li>

                <!-- 상품 3: 프린팅 그래픽 티셔츠 -->
                <li class="ad-card" style="width: 180px; flex-shrink: 0; border: 1px solid #eee; border-radius: 6px; padding: 10px; background: #fff;">
                    <a href="${pageContext.request.contextPath}/product/detail?productNo=38" style="text-decoration: none; color: inherit; display: block;">
                        <div class="ad-thumb" style="width: 100%; height: 180px; overflow: hidden; border-radius: 4px; background: #f9f9f9; margin-bottom: 8px;">
                            <img src="https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=300&auto=format&fit=crop" 
                                 alt="빈티지 그래픽 프린팅 드롭숄더 티셔츠" 
                                 style="width: 100%; height: 100%; object-fit: cover;" />
                        </div>
                        <span class="ad-name" style="font-size: 13px; color: #333; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 36px; line-height: 1.4; margin-bottom: 6px;">
                            빈티지 스트릿 그래픽 프린팅 드롭숄더 반팔티
                        </span>
                        <span class="ad-tag ad-tag--free" style="font-size: 10px; color: #0073e9; background: #f0f7ff; padding: 2px 4px; border-radius: 2px; font-weight: bold;">무료배송</span>
                        <div class="ad-price" style="margin-top: 6px; font-size: 15px; font-weight: bold; color: #111;">
                            <strong>19,800원</strong>
                        </div>
                        <div class="ad-rating" style="font-size: 11px; color: #888; margin-top: 4px;">
                            <span class="stars" style="color: #ffa500;">★★★★★</span>
                            <span class="count">(2,104)</span>
                        </div>
                    </a>
                </li>

                <!-- 상품 4: 베이직 컬렉션 티셔츠 -->
                <li class="ad-card" style="width: 180px; flex-shrink: 0; border: 1px solid #eee; border-radius: 6px; padding: 10px; background: #fff;">
                    <a href="${pageContext.request.contextPath}/product/detail?productNo=38" style="text-decoration: none; color: inherit; display: block;">
                        <div class="ad-thumb" style="width: 100%; height: 180px; overflow: hidden; border-radius: 4px; background: #f9f9f9; margin-bottom: 8px;">
                            <img src="https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=300&auto=format&fit=crop" 
                                 alt="베이직 코튼 머슬핏 반팔티" 
                                 style="width: 100%; height: 100%; object-fit: cover;" />
                        </div>
                        <span class="ad-name" style="font-size: 13px; color: #333; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 36px; line-height: 1.4; margin-bottom: 6px;">
                            [쿨링] 초경량 드라이핏 스포츠 기능성 머슬핏 반팔티
                        </span>
                        <span class="ad-tag ad-tag--free" style="font-size: 10px; color: #00891a; background: #f0fdf4; padding: 2px 4px; border-radius: 2px; font-weight: bold;">🚀 로켓배송</span>
                        <div class="ad-price" style="margin-top: 6px; font-size: 15px; font-weight: bold; color: #111;">
                            <strong>12,500원</strong>
                        </div>
                        <div class="ad-rating" style="font-size: 11px; color: #888; margin-top: 4px;">
                            <span class="stars" style="color: #ffa500;">★★★★☆</span>
                            <span class="count">(430)</span>
                        </div>
                    </a>
                </li>

            </ul>
        </div>
    </div>

</section>
	</main>

	<script>
	const contextPath = "${pageContext.request.contextPath}";
	const isWowMember = ${isWowMember eq true};

	const checkAll =
	    document.getElementById("chk-all");

	const itemCheckboxes =
	    document.querySelectorAll(".item-chk");

	const selectedCount =
	    document.querySelector(".selected-count");

	const totalCount =
	    document.querySelector(".total-count");

	const deleteSelectedBtn =
	    document.getElementById("btn-delete-selected");


	function updateSelectedCount() {

	    const checkedItems =
	        document.querySelectorAll(
	            ".item-chk:checked"
	        );

	    if (selectedCount) {
	        selectedCount.textContent =
	            checkedItems.length;
	    }

	    if (totalCount) {
	        totalCount.textContent =
	            itemCheckboxes.length;
	    }

	    if (checkAll) {

	        checkAll.checked =
	            itemCheckboxes.length > 0
	            && checkedItems.length ===
	               itemCheckboxes.length;
	    }
	}

	if (checkAll) {

	    checkAll.addEventListener(
	        "change",
	        function() {

	            itemCheckboxes.forEach(
	                function(item) {

	                    item.checked =
	                        checkAll.checked;
	                }
	            );

	            updateSelectedCount();
	        }
	    );
	}

	itemCheckboxes.forEach(
	    function(item) {

	        item.addEventListener(
	            "change",
	            function() {

	                updateSelectedCount();
	            }
	        );
	    }
	);

	if (deleteSelectedBtn) {

	    deleteSelectedBtn.addEventListener(
	        "click",
	        function() {

	            const checkedItems =
	                document.querySelectorAll(
	                    ".item-chk:checked"
	                );

	            if (checkedItems.length === 0) {

	                alert(
	                    "삭제할 상품을 선택해주세요."
	                );

	                return;
	            }

	            if (!confirm(
	                "선택한 상품을 삭제하시겠습니까?"
	            )) {
	                return;
	            }

	            const form =
	                document.createElement(
	                    "form"
	                );

	            form.method = "post";

	            form.action =
	                contextPath
	                + "/cart/delete-selected";

	            checkedItems.forEach(
	                function(item) {

	                    const input =
	                        document.createElement(
	                            "input"
	                        );

	                    input.type = "hidden";
	                    input.name = "optionId";
	                    input.value = item.value;

	                    form.appendChild(input);
	                }
	            );

	            document.body.appendChild(form);

	            form.submit();
	        }
	    );
	}

	updateSelectedCount();
</script>


	<script src="${pageContext.request.contextPath}/js/cart.js"></script>

</body>

</html>