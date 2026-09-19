<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<title>와우 멤버십 가입</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/wow_join.css">
</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="wow-join-page">
		<div class="wow-join-container">

			<div class="wow-join-header">
				<div class="wow-join-logo">WOW!</div>
				<h1>와우 멤버십</h1>
				<p>매일 누리는 특별한 혜택을 만나보세요.</p>
			</div>

			<section class="wow-benefit-section">
				<h2>와우 회원이라면 누릴 수 있어요</h2>

				<div class="wow-benefit-list">
					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🚚</div>
						<div class="wow-benefit-text">
							<strong>로켓배송 무료배송</strong>
							<p>금액 조건 없이 로켓배송 상품을 무료배송으로 받아보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🌙</div>
						<div class="wow-benefit-text">
							<strong>새벽배송 · 당일배송</strong>
							<p>필요한 상품을 주문하고 더 빠르게 받아보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">↩</div>
						<div class="wow-benefit-text">
							<strong>30일 무료반품</strong>
							<p>상품이 마음에 들지 않아도 편리하게 무료반품할 수 있어요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">₩</div>
						<div class="wow-benefit-text">
							<strong>와우 회원 전용 할인</strong>
							<p>다양한 상품의 와우 회원 전용 할인가를 만나보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🥬</div>
						<div class="wow-benefit-text">
							<strong>로켓프레시 혜택</strong>
							<p>신선식품도 빠르고 편리하게 받아보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🍽</div>
						<div class="wow-benefit-text">
							<strong>쿠팡이츠 혜택</strong>
							<p>와우 회원을 위한 다양한 쿠팡이츠 혜택을 이용할 수 있어요.</p>
						</div>
					</div>
				</div>
			</section>

			<form action="${pageContext.request.contextPath}/wow/join"
				method="post" id="wowJoinForm">

				<section class="wow-payment-section">
					<h2>결제수단</h2>

					<c:choose>
						<%-- 등록된 결제수단이 있는 경우 --%>
						<c:when test="${not empty paymentMethods}">
							<div class="wow-payment-method-list">
								<c:forEach var="payment" items="${paymentMethods}"
									varStatus="status">

									<label class="wow-payment-method"> <input type="radio"
										name="paymentMethodNo" value="${payment.paymentMethodNo}"
										${status.first ? 'checked' : ''}>

										<div class="wow-payment-info">
											<c:choose>
												<c:when test="${payment.paymentType eq 'BANK'}">
													<strong>${payment.bankName}</strong>
													<span>계좌 ****${payment.accountLast4}</span>

													<c:if test="${not empty payment.accountHolder}">
														<span>${payment.accountHolder}</span>
													</c:if>
												</c:when>

												<c:when test="${payment.paymentType eq 'CARD'}">
													<strong>${payment.cardCompany}</strong>
													<span>카드 ****${payment.cardLast4}</span>
												</c:when>

												<c:otherwise>
													<strong>${payment.paymentType}</strong>
												</c:otherwise>
											</c:choose>

											<c:if test="${payment.paymentDefault}">
												<span class="wow-default-badge">기본 결제수단</span>
											</c:if>
										</div>
									</label>

								</c:forEach>
							</div>

							<button type="button"
								class="wow-payment-add-btn paymentAddOpenBtn">+ 다른 결제수단
								등록하기</button>
						</c:when>

						<%-- 등록된 결제수단이 없는 경우 --%>
						<c:otherwise>
							<div class="wow-no-payment">
								<p>등록된 결제수단이 없습니다.</p>

								<button type="button"
									class="wow-payment-add-btn paymentAddOpenBtn">결제수단
									등록하기</button>
							</div>
						</c:otherwise>
					</c:choose>
				</section>

				<section class="wow-price-section">
					<h2>멤버십 이용료</h2>

					<div class="wow-price-box">
						<div class="wow-price-row">
							<span>와우 멤버십</span> <strong>월 7,890원</strong>
						</div>
						<p>매월 선택한 결제수단으로 자동 결제됩니다.</p>
					</div>
				</section>

				<section class="wow-agree-section">
					<label class="wow-agree"> <input type="checkbox"
						id="wowAgree"> <span>와우 멤버십 이용 조건 및 월 7,890원 정기결제에
							동의합니다.</span>
					</label>

					<p class="wow-agree-notice">가입 후 언제든지 와우 멤버십 관리 페이지에서 해지할 수
						있습니다.</p>
				</section>

				<button type="submit" id="wowSubmitBtn" class="wow-submit-btn"
					disabled>와우 멤버십 가입하기</button>
			</form>

			<button type="button" class="wow-back-btn" onclick="history.back()">
				다음에 할게요</button>

		</div>
	</div>


	<%-- 결제수단 등록 모달 --%>
	<div id="paymentAddModal" class="payment-add-overlay">
		<div class="payment-add-modal">
			<button type="button" id="paymentAddCloseBtn"
				class="payment-add-close">&times;</button>

			<div class="payment-add-header">
				<h2>결제수단 등록</h2>
				<p>와우 멤버십 결제에 사용할 결제수단을 등록해주세요.</p>
			</div>

			<form action="${pageContext.request.contextPath}/payment-method/add"
				method="post" id="paymentAddForm">

				<input type="hidden" name="redirect" value="/wow/join">

				<div class="payment-type-list">
					<label class="payment-type-item"> <input type="radio"
						name="paymentType" value="BANK" checked> <span>계좌</span>
					</label> <label class="payment-type-item"> <input type="radio"
						name="paymentType" value="CARD"> <span>카드</span>
					</label>
				</div>

				<%-- 계좌 --%>
				<div id="bankSection" class="payment-input-section">
					<div class="payment-field">
						<label for="bankCode">은행</label> <select id="bankCode"
							name="bankCode">
							<option value="">은행을 선택해주세요.</option>
							<option value="SHINHAN">신한은행</option>
							<option value="KB">KB국민은행</option>
							<option value="WOORI">우리은행</option>
							<option value="NH">NH농협은행</option>
							<option value="HANA">하나은행</option>
							<option value="KAKAO">카카오뱅크</option>
							<option value="TOSS">토스뱅크</option>
						</select>
					</div>

					<div class="payment-field">
						<label for="accountNumber">계좌번호</label> <input type="text"
							id="accountNumber" name="accountNumber"
							placeholder="- 없이 숫자 10~14자리" inputmode="numeric" maxlength="14"
							autocomplete="off">
					</div>

					<div class="payment-field">
						<label for="accountHolder">예금주</label> <input type="text"
							id="accountHolder" name="accountHolder" placeholder="예금주명 입력"
							maxlength="10">
					</div>
				</div>

				<%-- 카드 --%>
				<div id="cardSection" class="payment-input-section"
					style="display: none;">
					<div class="payment-field">
						<label for="cardCompany">카드사</label> <select id="cardCompany"
							name="cardCompany">
							<option value="">카드사를 선택해주세요.</option>
							<option value="SHINHAN">신한카드</option>
							<option value="KB">KB국민카드</option>
							<option value="SAMSUNG">삼성카드</option>
							<option value="HYUNDAI">현대카드</option>
							<option value="LOTTE">롯데카드</option>
							<option value="HANA">하나카드</option>
							<option value="WOORI">우리카드</option>
						</select>
					</div>

					<div class="payment-field">
						<label>카드번호</label>

						<div class="card-number-group">
							<input type="text" id="cardNumber1" name="cardNumber1"
								class="card-number-part" inputmode="numeric" maxlength="4"
								autocomplete="off"> <span class="card-number-dash">-</span>

							<input type="text" id="cardNumber2" name="cardNumber2"
								class="card-number-part" inputmode="numeric" maxlength="4"
								autocomplete="off"> <span class="card-number-dash">-</span>

							<input type="password" id="cardNumber3" name="cardNumber3"
								class="card-number-part" inputmode="numeric" maxlength="4"
								autocomplete="off"> <span class="card-number-dash">-</span>

							<input type="password" id="cardNumber4" name="cardNumber4"
								class="card-number-part" inputmode="numeric" maxlength="4"
								autocomplete="off">
						</div>

						<p class="payment-field-help">카드번호 16자리를 입력해주세요.</p>
					</div>
				</div>

				<label class="payment-default-label"> <input type="checkbox"
					name="paymentDefault" value="Y"> <span>기본 결제수단으로 설정</span>
				</label>

				<div class="payment-add-buttons">
					<button type="button" id="paymentAddCancelBtn"
						class="payment-cancel-btn">취소</button>
					<button type="submit" class="payment-submit-btn">등록하기</button>
				</div>
			</form>
		</div>
	</div>


	<script>
		document
				.addEventListener(
						"DOMContentLoaded",
						function() {

							/* 와우 가입 */
							const agree = document.getElementById("wowAgree");
							const submitBtn = document
									.getElementById("wowSubmitBtn");
							const wowJoinForm = document
									.getElementById("wowJoinForm");
							const paymentMethods = document
									.querySelectorAll('input[name="paymentMethodNo"]');

							function checkWowForm() {
								const selectedPayment = document
										.querySelector('input[name="paymentMethodNo"]:checked');
								submitBtn.disabled = !(agree.checked && selectedPayment);
							}

							agree.addEventListener("change", checkWowForm);

							paymentMethods.forEach(function(payment) {
								payment
										.addEventListener("change",
												checkWowForm);
							});

							wowJoinForm
									.addEventListener(
											"submit",
											function(event) {
												const selectedPayment = document
														.querySelector('input[name="paymentMethodNo"]:checked');

												if (!selectedPayment) {
													event.preventDefault();
													alert("결제수단을 선택해주세요.");
													return;
												}

												if (!agree.checked) {
													event.preventDefault();
													alert("와우 멤버십 이용 조건에 동의해주세요.");
													return;
												}
											});

							/* 결제수단 등록 모달 */
							const openBtns = document
									.querySelectorAll(".paymentAddOpenBtn");
							const modal = document
									.getElementById("paymentAddModal");
							const closeBtn = document
									.getElementById("paymentAddCloseBtn");
							const cancelBtn = document
									.getElementById("paymentAddCancelBtn");
							const paymentAddForm = document
									.getElementById("paymentAddForm");
							const paymentTypes = document
									.querySelectorAll('#paymentAddForm input[name="paymentType"]');
							const bankSection = document
									.getElementById("bankSection");
							const cardSection = document
									.getElementById("cardSection");
							const cardNumberParts = document
									.querySelectorAll(".card-number-part");

							cardNumberParts
									.forEach(function(input, index) {
										input
												.addEventListener(
														"input",
														function() {
															this.value = this.value
																	.replace(
																			/[^0-9]/g,
																			"")
																	.slice(0, 4);

															if (this.value.length === 4
																	&& index < cardNumberParts.length - 1) {
																cardNumberParts[index + 1]
																		.focus();
															}
														});

										input
												.addEventListener(
														"keydown",
														function(event) {
															if (event.key === "Backspace"
																	&& this.value.length === 0
																	&& index > 0) {
																cardNumberParts[index - 1]
																		.focus();
															}
														});
									});

							function openPaymentModal() {
								modal.style.display = "flex";
								document.body.style.overflow = "hidden";
							}

							function closePaymentModal() {
								modal.style.display = "none";
								document.body.style.overflow = "";
							}

							function changePaymentType() {
								const selected = document
										.querySelector('#paymentAddForm input[name="paymentType"]:checked');

								if (!selected) {
									return;
								}

								if (selected.value === "BANK") {
									bankSection.style.display = "block";
									cardSection.style.display = "none";
								} else {
									bankSection.style.display = "none";
									cardSection.style.display = "block";
								}
							}

							openBtns
									.forEach(function(btn) {
										btn.addEventListener("click",
												openPaymentModal);
									});

							closeBtn.addEventListener("click",
									closePaymentModal);
							cancelBtn.addEventListener("click",
									closePaymentModal);

							modal.addEventListener("click", function(event) {
								if (event.target === modal) {
									closePaymentModal();
								}
							});

							paymentTypes.forEach(function(type) {
								type.addEventListener("change",
										changePaymentType);
							});

							paymentAddForm
									.addEventListener(
											"submit",
											function(event) {
												const paymentType = document
														.querySelector('#paymentAddForm input[name="paymentType"]:checked').value;

												if (paymentType === "BANK") {
													const bankCode = document
															.getElementById("bankCode").value;
													const accountNumber = document
															.getElementById("accountNumber").value
															.trim();
													const accountHolder = document
															.getElementById("accountHolder").value
															.trim();

													if (!bankCode) {
														event.preventDefault();
														alert("은행을 선택해주세요.");
														return;
													}

													if (!accountNumber) {
														event.preventDefault();
														alert("계좌번호를 입력해주세요.");
														return;
													}

													if (!accountHolder) {
														event.preventDefault();
														alert("예금주를 입력해주세요.");
														return;
													}
												} else {
													const cardCompany = document
															.getElementById("cardCompany").value;
													const cardNumber = document
															.getElementById("cardNumber").value
															.trim();

													if (!cardCompany) {
														event.preventDefault();
														alert("카드사를 선택해주세요.");
														return;
													}

													if (!cardNumber) {
														event.preventDefault();
														alert("카드번호를 입력해주세요.");
														return;
													}
												}
											});

							changePaymentType();
							checkWowForm();
						});
	</script>

</body>
</html>