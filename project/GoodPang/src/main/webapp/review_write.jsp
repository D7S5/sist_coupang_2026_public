<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<title>리뷰 작성</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/review_write.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
</head>
<body>
		<jsp:include page="/inc/header.jsp" />
		
		<div class="mypang-layout">
		
		<jsp:include page="/inc/left_banner.jsp" />
		
			<main class="review-page">

				<h1 class="review-title">리뷰관리</h1>

				<!-- 회원 정보 -->
				<div class="review-member-box">

					<div class="member-profile">
						<div class="member-avatar">👤</div>

						<div class="member-name">
							${sessionScope.loginMember.memberName}</div>
					</div>

					<div class="member-divider"></div>

					<div class="member-stat">
						<span class="member-stat-title"> 작성한 리뷰 </span>

						<div class="member-stat-value">
							<strong>${reviewCount}</strong> <span>개</span>
						</div>
					</div>

				</div>


				<form id="reviewForm"
					action="${pageContext.request.contextPath}/review/write"
					method="post" enctype="multipart/form-data">

					<input type="hidden" name="orderDetailNo"
						value="${param.orderDetailNo}"> <input type="hidden"
						name="productNo" value="${param.productNo}"> <input
						type="hidden" name="serviceRating" id="serviceRating"> <input
						type="hidden" name="productRating" id="productRating">


					<!-- 서비스 리뷰 -->
					<section class="service-review">
						<div class="section-title-wrap">
							<span class="section-icon">●</span>
							<h2 class="section-title">쿠팡(주) 서비스 리뷰</h2>
						</div>
						<p class="section-desc">로켓배송의 배송, 포장, 친절, 응대 등 전체적인 서비스는
							어떠셨나요?</p>
						<div class="service-score-row">
							<div class="row-label">만족도</div>
							<div class="service-buttons">
								<button type="button" class="service-btn" data-score="1"
									aria-label="불만족">👎</button>

								<button type="button" class="service-btn" data-score="2"
									aria-label="만족">👍</button>
							</div>
						</div>
					</section>

					<!-- 상품 리뷰 -->
					<section class="product-review">

						<div class="section-title-wrap">
							<span class="product-heading-icon"> ☺ </span>

							<h2 class="section-title">상품 품질 리뷰</h2>
						</div>

						<p class="section-desc">이 상품의 품질에 대해서 얼마나 만족하시나요?</p>


						<!-- 상품 -->
						<div class="product-box">

							<c:choose>

								<c:when test="${not empty reviewItem.productImage}">
									<img class="product-image"
										src="${pageContext.request.contextPath}${reviewItem.productImage}"
										alt="${reviewItem.productName}">
								</c:when>

								<c:otherwise>
									<div class="product-image product-image-empty">상품 이미지</div>
								</c:otherwise>

							</c:choose>


							<div class="product-info">

								<div class="product-name">${reviewItem.productName}</div>

								<c:if test="${not empty reviewItem.optionName}">
									<div class="product-option">${reviewItem.optionName}</div>
								</c:if>


								<div class="star-rating">

									<button type="button" class="star" data-rating="1">★</button>

									<button type="button" class="star" data-rating="2">★</button>

									<button type="button" class="star" data-rating="3">★</button>

									<button type="button" class="star" data-rating="4">★</button>

									<button type="button" class="star" data-rating="5">★</button>

									<span class="star-guide" id="starGuide"> (필수) </span>

								</div>

							</div>

						</div>


						<!-- 상세 리뷰 -->
						<div class="review-row">

							<div class="review-label">
								상세<br>리뷰
							</div>

							<div class="review-content">

								<div class="textarea-wrap">

									<textarea name="reviewContent" id="reviewContent"
										class="review-textarea" maxlength="1000"
										placeholder="다른 고객님에게 도움이 되도록 상품에 대한 솔직한 평가를 남겨주세요."></textarea>

									<span class="text-count" id="reviewCount"> 0 / 1000 </span>

								</div>

							</div>

						</div>


						<!-- 사진 -->
						<div class="review-row">

							<div class="review-label">
								사진<br>첨부
							</div>

							<div class="review-content">

								<div class="photo-area">

									<label for="photoInput" class="photo-btn"> 사진 첨부하기 </label> <strong
										id="photoCount"> 0 / 10 </strong> <span class="photo-guide">
										사진은 최대 10장까지 첨부할 수 있습니다. </span>

								</div>

								<input type="file" id="photoInput" name="reviewImages"
									accept="image/*" multiple>

								<div class="preview-container" id="previewContainer"></div>

							</div>

						</div>


						<!-- 한줄 요약 -->
						<div class="review-row">

							<div class="review-label">
								한줄<br>요약
							</div>

							<div class="review-content">

								<div class="summary-wrap">

									<input type="text" name="reviewSummary" id="reviewSummary"
										class="summary-input" maxlength="30"
										placeholder="한 줄 요약을 입력해 주세요"> <span
										class="summary-count" id="summaryCount"> 0 / 30 </span>

								</div>

							</div>

						</div>


						<div class="button-area">

							<button type="button" class="cancel-btn"
								onclick="history.back();">취소하기</button>

							<button type="submit" class="submit-btn">등록하기</button>

						</div>

					</section>

				</form>

			</main>
			
			<aside class="review-right-banner">
			<jsp:include page="/inc/right_banner.jsp" />
		</aside>

		</div>

		<jsp:include page="/inc/footer.jsp" />

		<script>
document.addEventListener("DOMContentLoaded", function () {

    // 서비스 만족도
    const serviceButtons = document.querySelectorAll(".service-btn");
    const serviceRating = document.getElementById("serviceRating");

    serviceButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            serviceButtons.forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");
            serviceRating.value = this.dataset.score;
        });
    });

    // 상품 별점
    const stars = document.querySelectorAll(".star");
    const productRating = document.getElementById("productRating");
    const starGuide = document.getElementById("starGuide");

    const ratingText = {
        1: "별로예요",
        2: "그저 그래요",
        3: "보통이에요",
        4: "좋아요",
        5: "최고예요"
    };

    stars.forEach(function (star) {
        star.addEventListener("click", function () {
            const rating = Number(this.dataset.rating);

            productRating.value = rating;

            stars.forEach(function (item) {
                item.classList.toggle(
                    "active",
                    Number(item.dataset.rating) <= rating
                );
            });

            starGuide.textContent = ratingText[rating];
        });
    });

    // 상세 리뷰 글자 수
    const reviewContent = document.getElementById("reviewContent");
    const reviewCount = document.getElementById("reviewCount");

    reviewContent.addEventListener("input", function () {
        reviewCount.textContent = this.value.length + " / 1000";
    });

    // 한줄 요약 글자 수
    const reviewSummary = document.getElementById("reviewSummary");
    const summaryCount = document.getElementById("summaryCount");

    reviewSummary.addEventListener("input", function () {
        summaryCount.textContent = this.value.length + " / 30";
    });

    // 사진 미리보기
    const photoInput = document.getElementById("photoInput");
    const photoCount = document.getElementById("photoCount");
    const previewContainer = document.getElementById("previewContainer");

    photoInput.addEventListener("change", function () {
        previewContainer.innerHTML = "";

        const files = Array.from(this.files);

        if (files.length > 10) {
            alert("사진은 최대 10장까지 첨부할 수 있습니다.");
            this.value = "";
            photoCount.textContent = "0 / 10";
            return;
        }

        photoCount.textContent = files.length + " / 10";

        files.forEach(function (file) {
            if (!file.type.startsWith("image/")) return;

            const reader = new FileReader();

            reader.onload = function (event) {
                const div = document.createElement("div");
                const img = document.createElement("img");

                div.className = "preview-item";
                img.src = event.target.result;

                div.appendChild(img);
                previewContainer.appendChild(div);
            };

            reader.readAsDataURL(file);
        });
    });

    // 등록 검증
    document.getElementById("reviewForm")
        .addEventListener("submit", function (event) {

            if (productRating.value === "") {
                alert("상품 별점을 선택해주세요.");
                event.preventDefault();
                return;
            }

            if (reviewContent.value.trim() === "") {
                alert("상세 리뷰를 작성해주세요.");
                reviewContent.focus();
                event.preventDefault();
            }
        });
});
</script>
</body>
</html>