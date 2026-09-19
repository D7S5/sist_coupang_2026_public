<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>리뷰 수정</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/review_edit.css">
</head>

<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="review-page">

		<h1 class="review-title">리뷰 수정</h1>

		<form id="reviewForm"
			action="${pageContext.request.contextPath}/review/edit" method="post">

			<!-- 리뷰 번호 -->
			<input type="hidden" name="reviewNo" value="${review.reviewNo}">

			<!-- 상품 별점 -->
			<input type="hidden" name="productRating" id="productRating"
				value="${review.productRating}">

			<!-- 서비스 만족도 -->
			<input type="hidden" name="serviceRating" id="serviceRating"
				value="${review.serviceRating}">


			<!-- 상품 정보 -->
			<section class="product-review">
				<h2>상품 품질에 대해 평가해주세요.</h2>
				<div class="product-box">
					<div class="product-info">
						<strong class="product-name"> <c:out
								value="${review.productName}" />
						</strong>
						<c:if test="${not empty review.optionText}">
							<p class="product-option">
								<c:out value="${review.optionText}" />
							</p>
						</c:if>
					</div>
				</div>


				<!-- 별점 -->
				<div class="review-row">
					<div class="review-row-title">상품은 만족하셨나요?</div>
					<div class="star-rating" id="starRating">
						<button type="button" class="star" data-value="1">★</button>
						<button type="button" class="star" data-value="2">★</button>
						<button type="button" class="star" data-value="3">★</button>
						<button type="button" class="star" data-value="4">★</button>
						<button type="button" class="star" data-value="5">★</button>
					</div>
				</div>

				<!-- 서비스 만족도 -->
				<div class="review-row">
					<div class="review-row-title">배송/서비스는 어떠셨나요?</div>
					<div class="service-rating-buttons">
						<button type="button" class="service-btn" data-value="1">
							👎 불만족</button>
						<button type="button" class="service-btn" data-value="2">
							👍 만족</button>
					</div>
				</div>
				<!-- 한줄 요약 -->
				<div class="review-row">
					<label for="reviewSummary" class="review-row-title"> 한줄 요약
					</label>
					<div>
						<input type="text" id="reviewSummary" name="reviewSummary"
							class="review-summary-input" maxlength="100"
							value="${fn:escapeXml(review.reviewSummary)}"
							placeholder="상품에 대한 한줄평을 입력해주세요.">

						<div class="text-count">
							<span id="summaryCount">0</span>/100
						</div>
					</div>
				</div>

				<!-- 상세 리뷰 -->
				<div class="review-row">
					<label for="reviewContent" class="review-row-title"> 상세 리뷰
					</label>
					<div>
						<textarea id="reviewContent" name="reviewContent"
							class="review-textarea" maxlength="1000"
							placeholder="상품에 대한 솔직한 리뷰를 작성해주세요." required><c:out
								value="${review.reviewContent}" /></textarea>
						<div class="text-count">
							<span id="contentCount">0</span>/1000
						</div>
					</div>
				</div>

			</section>
			<!-- 하단 버튼 -->
			<div class="review-submit-area">
				<button type="button" class="btn-cancel" onclick="history.back();">
					취소</button>
				<button type="submit" class="btn-submit">수정하기</button>
			</div>
		</form>
	</div>

	<jsp:include page="/inc/footer.jsp" />


	<script>
const productRating =
    document.getElementById("productRating");

const serviceRating =
    document.getElementById("serviceRating");

const stars =
    document.querySelectorAll(".star");

const serviceButtons =
    document.querySelectorAll(".service-btn");

const reviewSummary =
    document.getElementById("reviewSummary");

const reviewContent =
    document.getElementById("reviewContent");

const summaryCount =
    document.getElementById("summaryCount");

const contentCount =
    document.getElementById("contentCount");


/* ===============================
   별점
================================ */
function updateStars(rating) {

    stars.forEach(function(star) {

        const value =
            Number(star.dataset.value);

        if (value <= rating) {
            star.classList.add("active");
        } else {
            star.classList.remove("active");
        }
    });
}


stars.forEach(function(star) {

    star.addEventListener(
        "click",
        function() {

            const rating =
                Number(this.dataset.value);

            productRating.value =
                rating;

            updateStars(rating);
        }
    );
});


/* 기존 상품 별점 표시 */
updateStars(
    Number(productRating.value)
);


/* ===============================
   서비스 만족도
================================ */
function updateServiceRating(value) {

    serviceButtons.forEach(function(button) {

        if (button.dataset.value === value) {
            button.classList.add("active");
        } else {
            button.classList.remove("active");
        }
    });
}


serviceButtons.forEach(function(button) {

    button.addEventListener(
        "click",
        function() {

            const value =
                this.dataset.value;

            serviceRating.value =
                value;

            updateServiceRating(value);
        }
    );
});


/* 기존 서비스 만족도 표시 */
if (serviceRating.value) {
    updateServiceRating(
        serviceRating.value
    );
}


/* ===============================
   글자수
================================ */
function updateSummaryCount() {

    summaryCount.textContent =
        reviewSummary.value.length;
}


function updateContentCount() {

    contentCount.textContent =
        reviewContent.value.length;
}


reviewSummary.addEventListener(
    "input",
    updateSummaryCount
);

reviewContent.addEventListener(
    "input",
    updateContentCount
);


/* 처음 로딩할 때 기존 글자수 표시 */
updateSummaryCount();
updateContentCount();


/* ===============================
   제출 검증
================================ */
document.getElementById(
    "reviewForm"
).addEventListener(
    "submit",
    function(event) {

        if (!productRating.value
                || Number(productRating.value) < 1
                || Number(productRating.value) > 5) {

            event.preventDefault();

            alert(
                "상품 별점을 선택해주세요."
            );

            return;
        }


        if (reviewContent.value.trim() === "") {

            event.preventDefault();

            alert(
                "리뷰 내용을 입력해주세요."
            );

            reviewContent.focus();

            return;
        }
    }
);
</script>

</body>
</html>