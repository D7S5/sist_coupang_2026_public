<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>리뷰 수정</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/review_write.css">
</head>

<body>

	<jsp:include page="/inc/header.jsp" />
	<div class="review-page">
		<h1 class="review-title">리뷰 수정</h1>
		<form action="${pageContext.request.contextPath}/review/edit"
			method="post" id="reviewForm">
			<input type="hidden" name="reviewNo" value="${review.reviewNo}">
			<input type="hidden" name="productRating" id="productRating"
				value="${review.productRating}"> <input type="hidden"
				name="serviceRating" id="serviceRating"
				value="${review.serviceRating}">


			<!-- 상품 -->
			<div class="product-box">
				<div class="product-info">
					<strong> <c:out value="${review.productName}" />
					</strong>
				</div>
			</div>


			<!-- 상품 별점 -->
			<div class="review-row">
				<div class="review-label">상품은 만족하셨나요?</div>
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
				<div class="review-label">배송/서비스는 만족하셨나요?</div>
				<div class="service-buttons">
					<button type="button" class="service-btn" data-value="1">
						👎 불만족</button>
					<button type="button" class="service-btn" data-value="2">
						👍 만족</button>
				</div>
			</div>
			<!-- 한줄 요약 -->
			<div class="review-row">
				<label for="reviewSummary"> 한줄 요약 </label> <input type="text"
					id="reviewSummary" name="reviewSummary" maxlength="100"
					value="<c:out value='${review.reviewSummary}' />">
			</div>
			<!-- 리뷰 내용 -->
			<div class="review-row">
				<label for="reviewContent"> 상세 리뷰 </label>
				<textarea id="reviewContent" name="reviewContent"
					class="review-textarea" maxlength="1000" required><c:out
						value="${review.reviewContent}" /></textarea>
			</div>
			<div class="review-actions">
				<button type="button" onclick="history.back();">취소</button>
				<button type="submit">수정하기</button>
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


function paintStars(value) {

    stars.forEach(star => {

        const starValue =
            Number(star.dataset.value);

        if (starValue <= value) {
            star.classList.add("active");
        } else {
            star.classList.remove("active");
        }
    });
}


stars.forEach(star => {

    star.addEventListener(
        "click",
        function () {

            const value =
                Number(this.dataset.value);

            productRating.value =
                value;

            paintStars(value);
        }
    );
});


serviceButtons.forEach(button => {

    button.addEventListener(
        "click",
        function () {

            serviceButtons.forEach(
                btn => btn.classList.remove("active")
            );

            this.classList.add("active");

            serviceRating.value =
                this.dataset.value;
        }
    );
});


/* 기존 별점 표시 */
paintStars(
    Number(productRating.value)
);


/* 기존 서비스 평가 표시 */
serviceButtons.forEach(button => {

    if (button.dataset.value
            === serviceRating.value) {

        button.classList.add("active");
    }
});


document.getElementById(
    "reviewForm"
).addEventListener(
    "submit",
    function (e) {

        if (!productRating.value) {

            e.preventDefault();

            alert(
                "상품 별점을 선택해주세요."
            );
        }
    }
);
</script>

</body>
</html>