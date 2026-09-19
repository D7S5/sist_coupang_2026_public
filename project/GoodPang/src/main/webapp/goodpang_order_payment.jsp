
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
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
<title>쿠팡 주문/결제</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/goodpang_order_payment.css">
</head>

<body>
	<div class="container">

		<h1 class="logo">
			<a href="${pageContext.request.contextPath}/" title="GoodPang 홈으로">
				<span class="brand-goodpang">GoodPang</span>
			</a>
		</h1>

		<h1 class="page-title">주문/결제</h1>

		<%-- 재고 부족으로 결제가 취소됐을 때만 보이는 안내.
		     OrderPaymentServlet 이 주소에 stockFail / stockLeft 를 붙여서 되돌려보냄.
		     <c:out> 을 쓰는 이유 — 상품명에 <, > 같은 글자가 들어있어도
		     HTML 태그로 해석되지 않게 막아준다(그냥 ${param.stockFail} 로 찍으면 위험). --%>
		<c:if test="${not empty param.stockFail}">
			<div class="stock-alert">
				<strong><c:out value="${param.stockFail}" /></strong> 상품의 재고가
				부족합니다. (남은 수량
				<c:out value="${param.stockLeft}" />
				개) <br> 수량을 줄이거나 장바구니에서 상품을 빼고 다시 시도해주세요.
			</div>
		</c:if>

		<div class="breadcrumb">
			<span>주문결제</span> &gt; 주문완료
		</div>

		<div class="content-wrap">

			<!-- 왼쪽 영역 -->
			<div class="left-section">

				<!-- 배송지 -->
				<div class="section-box">
					<div class="section-header">
						<div>
							<strong>배송지</strong> | <span id="currentReceiverName">${address.receiverName}</span>
						</div>

						<button class="btn-outline" type="button"
							onclick="openAddressModal()">배송지 변경</button>
					</div>

					<div class="section-body">
						<div class="address-detail">
							<span id="currentAddress">${address.address}</span><br> <span
								id="currentDetailAddress">${address.detailAddress}</span><br>
							휴대폰 : <span id="currentTel">${address.tel}</span>
						</div>

						<c:if test="${address.addressDefault}">
							<span class="tag" id="currentDefaultTag">기본배송지</span>
						</c:if>
					</div>
				</div>

				<!-- 배송 요청사항 -->
				<div class="section-box">
					<div class="section-header">
						<strong>배송 요청사항</strong>
						<button class="btn-outline" type="button">변경</button>
					</div>

					<div class="section-body">
						<span id="currentRequestMessage"> <c:choose>
								<c:when test="${not empty address.requestMsg}">
								${address.requestMsg}
							</c:when>
								<c:otherwise>
								배송 요청사항이 없습니다.
							</c:otherwise>
							</c:choose>
						</span>
					</div>
				</div>

				<!-- 결제수단 -->
				<div class="section-box payment-section">

					<div class="section-header">
						<strong>결제수단</strong>
					</div>

					<div class="payment-method">

						<label class="pay-radio-row"> <input type="radio"
							name="paymentMethod" value="COUPAY_MONEY" form="paymentForm">
							<span class="pay-title"> 쿠페이 머니 : <strong> <fmt:formatNumber
										value="${checkout.cashUsed}" pattern="#,###" />원
							</strong>
						</span> <span class="badge-red">최대 캐시적립</span>
						</label>

						<!-- 계좌이체 -->
						<label class="pay-radio-row account-row"> <input
							type="radio" name="paymentMethod" value="BANK_TRANSFER"
							form="paymentForm" checked> <span class="pay-title">계좌이체</span>
						</label>

						<!-- 등록된 계좌 -->
						<div class="bank-setting" id="bankSetting">
							<div class="payment-select-row">

								<select class="bank-select" name="paymentMethodNo" id="bankCode"
									form="paymentForm">

									<c:choose>
										<c:when test="${empty paymentMethods}">
											<option value="">등록된 계좌가 없습니다.</option>
										</c:when>

										<c:otherwise>
											<c:forEach var="pm" items="${paymentMethods}">
												<option value="${pm.paymentMethodNo}"
													${pm.paymentDefault ? 'selected' : ''}>
													${pm.bankName} / **********${pm.accountLast4}</option>
											</c:forEach>
										</c:otherwise>
									</c:choose>

								</select>

								<button type="button" class="payment-add-btn"
									onclick="openPaymentAddModal()">+ 결제수단 등록</button>

								<c:if test="${not empty paymentMethods}">
									<label class="default-payment"> <input type="checkbox"
										name="defaultPayment" id="defaultPayment" value="Y"
										form="paymentForm" checked> <span>기본 결제 수단으로 사용</span>
									</label>
								</c:if>

							</div>
						</div>

						<div class="payment-divider"></div>

						<!-- 다른 결제수단 -->
						<button class="other-payment-title" type="button"
							onclick="toggleOtherPayment()">
							<strong>다른 결제 수단</strong> <span id="paymentArrow">︿</span>
						</button>

						<div class="other-payment-list" id="otherPaymentList">

							<!-- 신용/체크카드 -->
							<label class="pay-radio-row other-radio" for="paymentCard">
								<input type="radio" id="paymentCard" name="paymentMethod"
								value="CARD" form="paymentForm"> <span class="pay-title">
									신용/체크카드 </span>
							</label>

							<!-- 등록된 카드 -->
							<div class="card-setting hidden" id="cardSetting">

								<select class="bank-select" name="cardPaymentMethodNo"
									id="cardCompany" form="paymentForm" disabled>

									<c:choose>
										<c:when test="${empty cardMethods}">
											<option value="">등록된 카드가 없습니다.</option>
										</c:when>
										<c:otherwise>
											<c:forEach var="card" items="${cardMethods}">
												<option value="${card.paymentMethodNo}"
													${card.paymentDefault ? 'selected' : ''}>

													<c:choose>
														<c:when test="${card.cardCompany eq 'BC'}">
															비씨카드
															</c:when>
														<c:when test="${card.cardCompany eq 'SHINHAN'}">
															신한카드
															</c:when>
														<c:when test="${card.cardCompany eq 'KB'}">
															KB국민카드
															</c:when>
														<c:when test="${card.cardCompany eq 'SAMSUNG'}">
															삼성카드
															</c:when>
														<c:when test="${card.cardCompany eq 'HYUNDAI'}">
															현대카드
															</c:when>
														<c:when test="${card.cardCompany eq 'LOTTE'}">
															롯데카드
															</c:when>
														<c:when test="${card.cardCompany eq 'HANA'}">
															하나카드
															</c:when>
														<c:when test="${card.cardCompany eq 'WOORI'}">
															우리카드
															</c:when>
														<c:when test="${card.cardCompany eq 'NH'}">
															NH농협카드
															</c:when>
														<c:otherwise>
															${card.cardCompany}
															</c:otherwise>
													</c:choose> / **** ${card.cardLast4}
												</option>
											</c:forEach>
										</c:otherwise>
									</c:choose>
								</select>
							</div>

							<!-- 휴대폰 -->
							<label class="pay-radio-row other-radio"> <input
								type="radio" name="paymentMethod" value="MOBILE"
								form="paymentForm"> <span class="pay-title">휴대폰</span>
							</label>

							<!-- 무통장입금 -->
							<label class="pay-radio-row other-radio"> <input
								type="radio" name="paymentMethod" value="VIRTUAL_ACCOUNT"
								form="paymentForm"> <span class="pay-title">무통장입금(가상계좌)</span>
							</label>

						</div>
					</div>
				</div>

				<!-- 배송 / 상품 정보 -->
				<div class="delivery-info">
					<h4>배송 ${fn:length(checkoutItems)}건 중
						${fn:length(checkoutItems)}건</h4>
					<c:forEach var="item" items="${checkoutItems}" varStatus="status">
						<div class="product-item">

							<div class="product-img">
								<c:choose>

									<c:when test="${not empty item.productImage}">
										<a
											href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">
											<img
											src="${img:url(item.productImage)}"
											alt="${item.productName}">
										</a>
									</c:when>

									<c:otherwise>
										<span>이미지 없음</span>
									</c:otherwise>

								</c:choose>
							</div>
							<div class="product-info">
								<div class="product-name">${item.productName}</div>
								<div class="product-option">${item.optionName}</div>
								<div class="product-price">
									<fmt:formatNumber value="${item.price}" pattern="#,###" />
									원
								</div>
								<div class="product-qty">수량 ${item.orderQty}개</div>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>

			<!-- 오른쪽 결제 금액 영역 -->
			<div class="right-section">

				<div class="payment-summary">

					<h3>최종 결제 금액</h3>

					<div class="price-row">
						<span>총 상품 가격</span> <span> <fmt:formatNumber
								value="${checkout.productAmount}" pattern="#,###" />원
						</span>
					</div>

					<div class="price-row">
						<span>즉시할인</span> <span class="discount"> -<fmt:formatNumber
								value="${checkout.instantDiscount}" pattern="#,###" />원
						</span>
					</div>

					<div class="price-row">
						<span> 쿠폰할인
							<button class="coupon-btn" type="button">변경</button>
						</span> <span class="discount"> -<fmt:formatNumber
								value="${checkout.couponDiscount}" pattern="#,###" />원
						</span>
					</div>

					<div class="price-row">
						<span>배송비</span> <span> <fmt:formatNumber
								value="${checkout.deliveryFee}" pattern="#,###" />원
						</span>
					</div>

					<div class="price-row">
						<span>쿠팡캐시</span>

						<div class="cash-input">
							<button type="button">전액사용</button>

							<input type="text" name="cashUsed" value="${checkout.cashUsed}"
								readonly> 원
						</div>
					</div>

					<div class="divider"></div>

					<div class="total-row">
						<span class="label">총 결제 금액</span> <span class="amount"> <fmt:formatNumber
								value="${checkout.totalPrice}" pattern="#,###" />원
						</span>
					</div>

					<div class="agree-links">
						개인정보 제3자 제공 동의 <a href="#">보기</a>
					</div>

					<div class="agree-links">
						개인정보 수집 및 이용 안내 <a href="#">보기</a>
					</div>

					<div class="notice">* 개별 판매자가 등록한 마켓플레이스(오픈마켓) 상품에 대한 광고,
						상품주문, 배송 및 환불의 의무와 책임은 각 판매자가 부담하고, 이에 대하여 쿠팡은 통신판매중개자로서 통신판매의
						당사자가 아니므로 일체 책임을 지지 않습니다.</div>

					<div class="final-agree">위 주문 내용을 확인 하였으며, 회원 본인은 개인정보 이용 및
						제공(해외직구의 경우 국외제공) 및 결제에 동의합니다.</div>

					<!-- 결제 FORM -->
					<form id="paymentForm"
						action="${pageContext.request.contextPath}/order/checkout"
						method="post" onsubmit="return validatePayment();">

						<input type="hidden" id="checkoutNo" name="checkoutNo"
							value="${checkoutNo}"> <input type="hidden"
							id="selectedAddressNo" name="addressNo"
							value="${address.addressNo}">

						<!-- PG 더미 결제 결과 -->
						<input type="hidden" id="pgPaymentStatus" name="pgPaymentStatus"
							value=""> <input type="hidden" id="pgTransactionId"
							name="pgTransactionId" value="">

						<button class="btn-pay" type="submit">결제하기</button>
					</form>

				</div>
			</div>

		</div>
	</div>


	<!-- 배송지 선택 모달 -->
	<div id="addressModalOverlay" class="address-modal-overlay">

		<div class="address-modal" onclick="event.stopPropagation();">

			<div class="address-modal-header">
				<h2>배송지 선택</h2>

				<button type="button" class="address-modal-close"
					onclick="closeAddressModal()">&times;</button>
			</div>

			<div class="address-modal-body">

				<c:forEach var="addr" items="${addressList}">

					<div
						class="address-card
							${addr.addressNo == address.addressNo
							? 'selected-address-card'
							: ''}">

						<div class="address-card-name">${addr.receiverName}</div>

						<div class="address-tags">

							<c:if test="${addr.addressDefault}">
								<span class="address-tag default"> 기본배송지 </span>
							</c:if>

							<span class="address-tag fresh"> 로켓프레시 가능 </span> <span
								class="address-tag rocket"> 로켓와우 가능 </span>
						</div>

						<div class="address-card-detail">

							<div class="address-text">
								${addr.address}&nbsp;${addr.detailAddress}</div>

							<div>${addr.tel}</div>

							<c:choose>
								<c:when test="${not empty addr.requestMsg}">
									<div class="address-request">${addr.requestMsg}</div>
								</c:when>

								<c:otherwise>
									<div class="address-request">배송 요청사항 없음</div>
								</c:otherwise>
							</c:choose>

						</div>

						<div class="address-card-buttons">

							<button type="button" class="address-edit-btn"
								data-address-no="${addr.addressNo}"
								data-receiver-name="${fn:escapeXml(addr.receiverName)}"
								data-zipcode="${fn:escapeXml(addr.zipcode)}"
								data-address="${fn:escapeXml(addr.address)}"
								data-detail-address="${fn:escapeXml(addr.detailAddress)}"
								data-tel="${fn:escapeXml(addr.tel)}"
								data-request-msg="${fn:escapeXml(addr.requestMsg)}"
								data-default="${addr.addressDefault}"
								onclick="openEditAddressModal(this)">수정</button>

							<button type="button" class="address-select-btn"
								data-address-no="${addr.addressNo}"
								data-receiver-name="${fn:escapeXml(addr.receiverName)}"
								data-address="${fn:escapeXml(addr.address)}"
								data-detail-address="${fn:escapeXml(addr.detailAddress)}"
								data-tel="${fn:escapeXml(addr.tel)}"
								data-request-msg="${fn:escapeXml(addr.requestMsg)}"
								data-default="${addr.addressDefault}"
								onclick="selectAddress(this)">선택</button>

						</div>
					</div>

				</c:forEach>

				<button type="button" class="address-add-btn"
					onclick="openAddAddressModal()">

					<span class="plus-icon">＋</span> 배송지 추가

				</button>

			</div>
		</div>
	</div>


	<!-- 배송지 추가 모달 -->
	<div id="addAddressModalOverlay" class="address-add-modal-overlay">

		<div class="address-add-modal" onclick="event.stopPropagation();">

			<div class="address-add-header">
				<h2 id="addressModalTitle">배송지 추가</h2>

				<button type="button" class="address-add-close"
					onclick="closeAddAddressModal()">×</button>
			</div>

			<form id="addAddressForm"
				action="${pageContext.request.contextPath}/address/add"
				method="post">

				<input type="hidden" name="addressNo" id="editAddressNo">

				<!-- 결제 페이지에서 배송지 추가했다는 정보 -->
				<input type="hidden" name="checkoutNo" value="${checkoutNo}">

				<input type="hidden" name="from" value="payment">

				<div class="address-add-body">

					<div class="add-address-row">
						<div class="add-address-icon">♙</div>

						<input type="text" name="receiverName" id="newReceiverName"
							placeholder="받는 사람" autocomplete="off">
					</div>

					<div class="add-address-row postcode-row">
						<div class="add-address-icon">◉</div>

						<button type="button" class="postcode-search-btn"
							onclick="findPostcode()">우편번호 찾기</button>

						<input type="hidden" name="zipcode" id="newZipcode">
					</div>

					<div class="add-address-row address-input-row">
						<div class="add-address-icon">⌂</div>

						<input type="text" name="address" id="newAddress" placeholder="주소"
							readonly>
					</div>

					<div class="add-address-row">
						<div class="add-address-icon">⌂</div>

						<input type="text" name="detailAddress" id="newDetailAddress"
							placeholder="상세주소">
					</div>


					<!-- 휴대폰 번호 -->
					<div class="add-address-row phone-row">
						<div class="add-address-icon">▣</div>

						<div class="phone-inputs">
							<input type="text" id="phone1" class="phone-input" maxlength="3"
								inputmode="numeric" value="010"> <span
								class="phone-hyphen">-</span> <input type="text" id="phone2"
								class="phone-input" maxlength="4" inputmode="numeric"> <span
								class="phone-hyphen">-</span> <input type="text" id="phone3"
								class="phone-input" maxlength="4" inputmode="numeric">
						</div>

						<input type="hidden" name="tel" id="newTel">
					</div>

					<!-- 배송 요청사항 -->
					<div class="add-address-row" id="requestMsgDirectArea">

						<div class="add-address-icon">✎</div>

						<input type="text" id="newRequestMsg" name="requestMsg"
							placeholder="배송 요청사항을 입력해주세요." maxlength="100">
					</div>

					<!-- 기본 배송지 -->
					<label class="default-address-check"> <input
						type="checkbox" name="addressDefault" id="newAddressDefault"
						value="Y"> <span class="custom-check"></span> 기본 배송지로 선택
					</label>

					<!-- 저장 -->
					<button type="submit" class="address-save-btn" id="addressSaveBtn">
						저장</button>
				</div>
			</form>

		</div>
	</div>
	<!-- 결제수단 추가 모달 -->
	<div id="paymentAddModalOverlay" class="payment-add-modal-overlay">
		<div class="payment-add-modal" onclick="event.stopPropagation();">
			<div class="payment-add-modal-header">
				<h2>결제수단 추가</h2>
				<button type="button" class="payment-add-modal-close"
					onclick="closePaymentAddModal()">×</button>
			</div>

			<div class="payment-add-modal-body">
				<form id="paymentMethodAddForm"
					action="${pageContext.request.contextPath}/payment-method/add"
					method="post">
					<input type="hidden" name="checkoutNo" value="${checkoutNo}">
					<input type="hidden" name="paymentType" id="newPaymentType"
						value="BANK">


					<!-- 계좌 / 카드 탭 -->
					<div class="payment-add-type">

						<button type="button" id="paymentTypeBankBtn"
							class="payment-type-btn active"
							onclick="selectPaymentAddType('BANK')">계좌</button>

						<button type="button" id="paymentTypeCardBtn"
							class="payment-type-btn" onclick="selectPaymentAddType('CARD')">
							신용/체크카드</button>
					</div>
					<!-- ==================== -->
					<!-- 계좌 -->
					<!-- ==================== -->
					<div id="paymentBankAddArea" class="payment-add-content">

						<div class="payment-add-label">은행</div>

						<select id="newBankCode" name="bankCode"
							class="payment-add-select">

							<option value="">은행을 선택해주세요</option>
							<option value="SHINHAN">신한은행</option>
							<option value="KB">KB국민은행</option>
							<option value="WOORI">우리은행</option>
							<option value="NH">NH농협은행</option>
							<option value="HANA">하나은행</option>
							<option value="KAKAO">카카오뱅크</option>
							<option value="TOSS">토스뱅크</option>

						</select>


						<div class="payment-add-label">계좌번호</div>

						<input type="text" id="newAccountNumber" name="accountNumber"
							class="payment-add-input" placeholder="'-' 없이 계좌번호 입력"
							inputmode="numeric" maxlength="14">


						<div class="payment-add-label">예금주</div>

						<input type="text" id="newAccountHolder" name="accountHolder"
							class="payment-add-input" placeholder="예금주명을 입력해주세요">

					</div>


					<!-- ==================== -->
					<!-- 신용/체크카드 -->
					<!-- ==================== -->
					<div id="paymentCardAddArea" class="payment-add-content hidden">

						<div class="payment-add-label">카드사</div>

						<select id="newCardCompany" name="cardCompany"
							class="payment-add-select">

							<option value="">카드사를 선택해주세요</option>
							<option value="BC">비씨카드</option>
							<option value="SHINHAN">신한카드</option>
							<option value="KB">KB국민카드</option>
							<option value="SAMSUNG">삼성카드</option>
							<option value="HYUNDAI">현대카드</option>
							<option value="LOTTE">롯데카드</option>
							<option value="HANA">하나카드</option>
							<option value="WOORI">우리카드</option>
							<option value="NH">NH농협카드</option>

						</select>


						<div class="payment-add-label">카드번호</div>

						<div class="card-number-area">

							<input type="text" id="cardNumber1" name="cardNumber1"
								class="card-number-input" maxlength="4" inputmode="numeric">

							<span>-</span> <input type="password" id="cardNumber2"
								name="cardNumber2" class="card-number-input" maxlength="4"
								inputmode="numeric"> <span>-</span> <input
								type="password" id="cardNumber3" name="cardNumber3"
								class="card-number-input" maxlength="4" inputmode="numeric">

							<span>-</span> <input type="text" id="cardNumber4"
								name="cardNumber4" class="card-number-input" maxlength="4"
								inputmode="numeric">

						</div>

					</div>


					<label class="payment-add-default"> <input type="checkbox"
						id="newPaymentDefault" name="paymentDefault" value="Y" checked>

						<span>기본 결제수단으로 등록</span>

					</label>


					<button type="button" class="payment-add-save-btn"
						onclick="addPaymentMethod()">등록</button>

				</form>

			</div>
		</div>
	</div>

	<!-- PG 카드 결제 모달 -->
	<div id="pgModalOverlay" class="pg-modal-overlay">
		<div class="pg-modal" onclick="event.stopPropagation();">
			<div class="pg-modal-header">
				<div>
					<strong>GoodPay</strong> <span>안전결제</span>
				</div>
				<button type="button" class="pg-modal-close"
					onclick="closePgModal()">×</button>
			</div>
			<div class="pg-modal-body">

				<!-- 최초 결제 화면 -->
				<div id="pgPaymentScreen">
					<div class="pg-company-title">신용 / 체크카드 결제</div>

					<div class="pg-info-box">
						<div class="pg-info-row">
							<span>가맹점</span> <strong>GoodPang</strong>
						</div>

						<div class="pg-info-row">
							<span>상품명</span> <strong>GoodPang 상품 구매</strong>
						</div>

						<div class="pg-info-row">
							<span>결제금액</span> <strong class="pg-price"> <fmt:formatNumber
									value="${checkout.totalPrice}" pattern="#,###" />원
							</strong>
						</div>
					</div>

					<div class="pg-card-box">
						<div class="pg-card-label">결제 카드</div>
						<div id="pgSelectedCard" class="pg-selected-card"></div>
					</div>

					<div class="pg-agree-box">
						<label> <input type="checkbox" id="pgAgree"> <span>
								결제 내용을 확인하였으며 결제에 동의합니다. </span>
						</label>

					</div>
					<button type="button" class="pg-pay-btn"
						onclick="processDummyPayment()">

						<fmt:formatNumber value="${checkout.totalPrice}" pattern="#,###" />
						원 결제하기
					</button>
				</div>


				<!-- 결제 처리중 -->
				<div id="pgLoadingScreen" class="pg-result-screen hidden">
					<div class="pg-spinner"></div>
					<h3>결제를 진행하고 있습니다.</h3>
					<p>잠시만 기다려주세요.</p>
				</div>

				<!-- 결제 성공 -->
				<div id="pgSuccessScreen" class="pg-result-screen hidden">
					<div class="pg-success-icon">✓</div>
					<h3>결제가 완료되었습니다.</h3>
					<p>결제 승인이 정상적으로 처리되었습니다.</p>
					<div class="pg-success-price">
						<fmt:formatNumber value="${checkout.totalPrice}" pattern="#,###" />
						원
					</div>
				</div>
			</div>
			<div class="pg-modal-footer">GoodPay Dummy Payment Gateway</div>
		</div>
	</div>

	<!-- 계좌이체 더미 결제 모달 -->
	<div id="bankPayModalOverlay" class="bank-pay-modal-overlay">
		<div class="bank-pay-modal" onclick="event.stopPropagation();">
			<div class="bank-pay-header">
				<div>
					<strong>GoodPay</strong> <span>계좌이체</span>
				</div>
				<button type="button" class="bank-pay-close"
					onclick="closeBankPayModal()">×</button>
			</div>

			<div class="bank-pay-body">
				<!-- 결제 확인 -->
				<div id="bankPayConfirmScreen">
					<h2 class="bank-pay-title">계좌이체 결제</h2>

					<div class="bank-pay-info">
						<div class="bank-pay-row">
							<span>가맹점</span> <strong>GoodPang</strong>
						</div>
						<div class="bank-pay-row">
							<span>상품명</span> <strong>GoodPang 상품 구매</strong>
						</div>
						<div class="bank-pay-row">
							<span>결제금액</span> <strong class="bank-pay-price"> <fmt:formatNumber
									value="${checkout.totalPrice}" pattern="#,###" />원
							</strong>
						</div>
					</div>

					<div class="bank-account-area">
						<div class="bank-account-label">출금 계좌</div>
						<div id="bankPaySelectedAccount" class="bank-selected-account"></div>
					</div>

					<div class="bank-pay-notice">선택한 계좌에서 결제금액이 즉시 출금됩니다.</div>

					<label class="bank-pay-agree"> <input type="checkbox"
						id="bankPayAgree"> <span>결제 내용을 확인하였으며 출금에 동의합니다.</span>
					</label>

					<button type="button" class="bank-auth-btn"
						onclick="startBankAuthentication()">계좌 인증하기</button>
				</div>

				<!-- 인증 화면 -->
				<div id="bankAuthScreen" class="bank-pay-screen hidden">
					<div class="bank-auth-icon">🔒</div>
					<h3>계좌 인증</h3>
					<p>인증번호를 입력해주세요.</p>
					<div class="bank-auth-guide">
						<strong>123456</strong>
					</div>
					<input type="text" id="bankAuthNumber" class="bank-auth-input"
						maxlength="6" inputmode="numeric" placeholder="인증번호 6자리">
					<button type="button" class="bank-auth-confirm-btn"
						onclick="confirmBankAuthentication()">인증 확인</button>
				</div>

				<!-- 처리중 -->
				<div id="bankLoadingScreen" class="bank-pay-screen hidden">
					<div class="bank-pay-spinner"></div>
					<h3>계좌이체를 진행하고 있습니다.</h3>
					<p>잠시만 기다려주세요.</p>
				</div>

				<!-- 성공 -->
				<div id="bankSuccessScreen" class="bank-pay-screen hidden">
					<div class="bank-success-icon">✓</div>
					<h3>결제가 완료되었습니다.</h3>
					<p>계좌이체가 정상적으로 처리되었습니다.</p>
					<strong class="bank-success-price"> <fmt:formatNumber
							value="${checkout.totalPrice}" pattern="#,###" />원
					</strong>
				</div>
			</div>

			<div class="bank-pay-footer">GoodPay Dummy Bank Transfer</div>
		</div>
	</div>

	<script
		src="https://t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
	<script>
	
	
	
	let pgPaymentApproved = false;
	
	let bankTransferApproved = false;

	function openBankPayModal() {
	    resetBankPayModal();

	    const bankSelect = document.getElementById("bankCode");

	    if (!bankSelect || !bankSelect.value) {
	        alert("결제할 계좌를 선택해주세요.");
	        return;
	    }

	    const selectedOption = bankSelect.options[bankSelect.selectedIndex];

	    document.getElementById("bankPaySelectedAccount").textContent =
	        selectedOption.textContent.trim();

	    document.getElementById("bankPayModalOverlay").classList.add("show");
	    document.body.classList.add("modal-open");
	}

	function closeBankPayModal() {
	    document.getElementById("bankPayModalOverlay").classList.remove("show");
	    document.body.classList.remove("modal-open");
	}

	function startBankAuthentication() {
	    const agree = document.getElementById("bankPayAgree");

	    if (!agree.checked) {
	        alert("출금 동의에 체크해주세요.");
	        return;
	    }

	    document.getElementById("bankPayConfirmScreen").classList.add("hidden");
	    document.getElementById("bankAuthScreen").classList.remove("hidden");
	    document.getElementById("bankAuthNumber").focus();
	}

	function confirmBankAuthentication() {
	    const authNumber = document.getElementById("bankAuthNumber").value.trim();

	    if (authNumber !== "123456") {
	        alert("인증번호가 올바르지 않습니다.");
	        return;
	    }

	    document.getElementById("bankAuthScreen").classList.add("hidden");
	    document.getElementById("bankLoadingScreen").classList.remove("hidden");

	    setTimeout(function() {
	        bankTransferSuccess();
	    }, 1500);
	}

	function bankTransferSuccess() {
	    document.getElementById("bankLoadingScreen").classList.add("hidden");
	    document.getElementById("bankSuccessScreen").classList.remove("hidden");

	    document.getElementById("pgPaymentStatus").value = "SUCCESS";
	    document.getElementById("pgTransactionId").value = "BANK_" + Date.now();

	    bankTransferApproved = true;

	    setTimeout(function() {
	        document.getElementById("paymentForm").requestSubmit();
	    }, 1000);
	}

	function resetBankPayModal() {
	    const confirmScreen = document.getElementById("bankPayConfirmScreen");
	    const authScreen = document.getElementById("bankAuthScreen");
	    const loadingScreen = document.getElementById("bankLoadingScreen");
	    const successScreen = document.getElementById("bankSuccessScreen");
	    const agree = document.getElementById("bankPayAgree");
	    const authNumber = document.getElementById("bankAuthNumber");

	    if (confirmScreen) confirmScreen.classList.remove("hidden");
	    if (authScreen) authScreen.classList.add("hidden");
	    if (loadingScreen) loadingScreen.classList.add("hidden");
	    if (successScreen) successScreen.classList.add("hidden");
	    if (agree) agree.checked = false;
	    if (authNumber) authNumber.value = "";
	}
	
	document.addEventListener(
		    "DOMContentLoaded",
		    function() {
		        const paymentRadios =
		            document.querySelectorAll(
		                'input[name="paymentMethod"]'
		            );
		        paymentRadios.forEach(function(radio) {
		            radio.addEventListener(
		                "change",
		                function() {
		                    changePaymentMethod(
		                        this.value
		                    );
		                }
		            );
		        });

		        const checkedPayment =
		            document.querySelector(
		                'input[name="paymentMethod"]:checked'
		            );
		        if (checkedPayment) {

		            changePaymentMethod(
		                checkedPayment.value
		            );
		        }
		    }
		);

	const phone1 = document.getElementById("phone1");
	const phone2 = document.getElementById("phone2");
	const phone3 = document.getElementById("phone3");

	function onlyNumber(input) {
	    input.value = input.value.replace(/[^0-9]/g, "");
	}

	function syncPhoneNumber() {

	    const p1 = phone1.value.trim();
	    const p2 = phone2.value.trim();
	    const p3 = phone3.value.trim();

	    const tel = document.getElementById("newTel");

	    if (p1 && p2 && p3) {
	        tel.value = p1 + "-" + p2 + "-" + p3;
	    } else {
	        tel.value = "";
	    }
	}

	phone1.addEventListener("input", function() {

	    onlyNumber(this);

	    if (this.value.length === 3) {
	        phone2.focus();
	    }

	    syncPhoneNumber();
	});

	phone2.addEventListener("input", function() {

	    onlyNumber(this);

	    if (this.value.length === 4) {
	        phone3.focus();
	    }

	    syncPhoneNumber();
	});

	phone3.addEventListener("input", function() {

	    onlyNumber(this);

	    syncPhoneNumber();
	});
	



function toggleOtherPayment() {

	const paymentList =
		document.getElementById("otherPaymentList");

	const paymentArrow =
		document.getElementById("paymentArrow");

	paymentList.classList.toggle("hidden");

	if (paymentList.classList.contains("hidden")) {
		paymentArrow.innerHTML = "﹀";
	} else {
		paymentArrow.innerHTML = "︿";
	}
}


function changePaymentMethod(paymentMethod) {

    console.log("changePaymentMethod:", paymentMethod);

    const bankSetting =
        document.getElementById("bankSetting");

    const bankCode =
        document.getElementById("bankCode");

    const defaultPayment =
        document.getElementById("defaultPayment");

    const cardSetting =
        document.getElementById("cardSetting");

    const cardCompany =
        document.getElementById("cardCompany");


    // 계좌이체
    if (paymentMethod === "BANK_TRANSFER") {

        if (bankSetting) {
            bankSetting.classList.remove("hidden");
        }

        if (bankCode) {
            bankCode.disabled = false;
        }

        if (defaultPayment) {
            defaultPayment.disabled = false;
        }

    } else {

        if (bankSetting) {
            bankSetting.classList.add("hidden");
        }

        if (bankCode) {
            bankCode.disabled = true;
        }

        if (defaultPayment) {
            defaultPayment.disabled = true;
        }
    }


    // 신용 / 체크카드
    if (paymentMethod === "CARD") {

        console.log("카드 활성화");

        if (cardSetting) {
            cardSetting.classList.remove("hidden");
        }

        if (cardCompany) {
            cardCompany.disabled = false;
        }

    } else {

        if (cardSetting) {
            cardSetting.classList.add("hidden");
        }

        if (cardCompany) {
            cardCompany.disabled = true;
        }
    }
}

function validatePayment() {

    const addressNo =
        document.getElementById(
            "selectedAddressNo"
        ).value;

    const paymentMethod =
        document.querySelector(
            'input[name="paymentMethod"]:checked'
        );

    if (!addressNo ||
        addressNo.trim() === "") {
        alert("배송지를 선택해주세요.");
        return false;
    }

    if (!paymentMethod) {
        alert("결제수단을 선택해주세요.");
        return false;
    }

    if (paymentMethod.value === "BANK_TRANSFER") {
        const bankCode =
            document.getElementById(
                "bankCode"
            );

        if (!bankCode ||
            !bankCode.value) {
            alert("계좌를 선택해주세요.");
            return false;
        }
    }
    
    if (paymentMethod.value === "BANK_TRANSFER") {
        const bankCode = document.getElementById("bankCode");

        if (!bankCode || !bankCode.value) {
            alert("계좌를 선택해주세요.");
            return false;
        }

        if (!bankTransferApproved) {
            openBankPayModal();
            return false;
        }
    }

    if (paymentMethod.value === "CARD") {

        const cardCompany =
            document.getElementById(
                "cardCompany"
            );

        if (!cardCompany ||
            !cardCompany.value) {

            alert("결제할 카드를 선택해주세요.");

            return false;
        }

        if (!pgPaymentApproved) {

            openPgModal();

            return false;
        }

    }
    const payButton =
        document.querySelector(
            ".btn-pay"
        );
    if (payButton) {
        payButton.disabled = true;
        payButton.textContent =
            "주문 처리중...";
    }
    return true;
}



/* 배송지 모달 */
function openAddressModal() {

	const modal =
		document.getElementById("addressModalOverlay");

	modal.classList.add("show");
	document.body.classList.add("modal-open");
}


function closeAddressModal() {

	const modal =
		document.getElementById("addressModalOverlay");

	modal.classList.remove("show");
	document.body.classList.remove("modal-open");
}


/* 배송지 선택 */
function selectAddress(button) {

	const addressNo = button.dataset.addressNo;
	const receiverName = button.dataset.receiverName;
	const address = button.dataset.address;
	const detailAddress = button.dataset.detailAddress;
	const tel = button.dataset.tel;
	const requestMsg = button.dataset.requestMsg;
	const isDefault = button.dataset.default === "true";

	document.getElementById(
		"currentReceiverName"
	).textContent = receiverName;

	document.getElementById(
		"currentAddress"
	).textContent = address;

	document.getElementById(
		"currentDetailAddress"
	).textContent = detailAddress;

	document.getElementById(
		"currentTel"
	).textContent = tel;

	document.getElementById(
		"selectedAddressNo"
	).value = addressNo;


	const requestArea =
		document.getElementById(
			"currentRequestMessage"
		);

	if (requestArea) {

		if (
			requestMsg !== null &&
			requestMsg !== undefined &&
			requestMsg.trim() !== ""
		) {

			requestArea.textContent = requestMsg;
			requestArea.style.color = "#333";

		} else {

			requestArea.textContent =
				"배송 요청사항이 없습니다.";

			requestArea.style.color = "#aaa";
		}
	}


	const defaultTag =
		document.getElementById(
			"currentDefaultTag"
		);

	if (defaultTag) {

		if (isDefault) {
			defaultTag.style.display = "inline-block";
		} else {
			defaultTag.style.display = "none";
		}
	}


	document
		.querySelectorAll(".address-card")
		.forEach(function(card) {
			card.classList.remove(
				"selected-address-card"
			);
		});


	button
		.closest(".address-card")
		.classList
		.add("selected-address-card");

	closeAddressModal();
}


/* 배송지 추가 */
function openAddAddressModal() {

    const selectModal =
        document.getElementById(
            "addressModalOverlay"
        );

    selectModal.classList.remove("show");

    const form =
        document.getElementById(
            "addAddressForm"
        );

    // 추가 URL로 복구
    form.action =
        "${pageContext.request.contextPath}/address/add";

    // 제목
    document.getElementById(
        "addressModalTitle"
    ).textContent = "배송지 추가";

    // addressNo 제거
    document.getElementById(
        "editAddressNo"
    ).value = "";

    // 입력값 초기화
    document.getElementById(
        "newReceiverName"
    ).value = "";

    document.getElementById(
        "newZipcode"
    ).value = "";

    document.getElementById(
        "newAddress"
    ).value = "";

    document.getElementById(
        "newDetailAddress"
    ).value = "";

    document.getElementById("phone1").value = "";
    document.getElementById("phone2").value = "";
    document.getElementById("phone3").value = "";
    document.getElementById("newTel").value = "";
    
    const requestMsg =
        document.getElementById(
            "newRequestMsg"
        );

    if (requestMsg) {
        requestMsg.value = "";
    }

    const defaultCheckbox =
        form.querySelector(
            'input[name="addressDefault"]'
        );

    if (defaultCheckbox) {
        defaultCheckbox.checked = false;
    }

    document.getElementById(
        "addressSaveBtn"
    ).textContent = "저장";

    const addModal =
        document.getElementById(
            "addAddressModalOverlay"
        );

    addModal.classList.add("show");

    document.body.classList.add(
        "modal-open"
    );
}


function closeAddAddressModal() {

	const addModal =
		document.getElementById(
			"addAddressModalOverlay"
		);

	addModal.classList.remove("show");


	const selectModal =
		document.getElementById(
			"addressModalOverlay"
		);

	selectModal.classList.add("show");
}


function findPostcode() {

    new kakao.Postcode({

        oncomplete: function(data) {

            let address = "";

            // 도로명 주소 선택
            if (data.userSelectedType === "R") {

                address = data.roadAddress;

            // 지번 주소 선택
            } else {

                address = data.jibunAddress;
            }

            // 우편번호
            document.getElementById(
                "newZipcode"
            ).value = data.zonecode;

            // 주소
            document.getElementById(
                "newAddress"
            ).value = address;

            // 상세주소로 포커스
            document.getElementById(
                "newDetailAddress"
            ).focus();
        }

    }).open();
}


/* 모달 배경 클릭 */
document
	.getElementById("addressModalOverlay")
	.addEventListener("click", function(event) {

		if (event.target === this) {
			closeAddressModal();
		}

	});


document
	.getElementById("addAddressModalOverlay")
	.addEventListener("click", function(event) {

		if (event.target === this) {
			closeAddAddressModal();
		}

	});


/* ESC 닫기 */
document.addEventListener(
	"keydown",
	function(event) {

		if (event.key === "Escape") {

			const addModal =
				document.getElementById(
					"addAddressModalOverlay"
				);

			if (addModal.classList.contains("show")) {
				closeAddAddressModal();
			} else {
				closeAddressModal();
			}
		}

	}
);


let paymentAddType = "BANK";


function openPaymentAddModal() {
	const modal = document.getElementById("paymentAddModalOverlay");

	modal.classList.add("show");
	document.body.classList.add("modal-open");

	selectPaymentAddType("BANK");
}

function closePaymentAddModal() {
	const modal = document.getElementById("paymentAddModalOverlay");

	modal.classList.remove("show");
	document.body.classList.remove("modal-open");

	resetPaymentAddForm();
}


function selectPaymentAddType(type) {

	paymentAddType = type;

	const bankBtn =
		document.getElementById("paymentTypeBankBtn");

	const cardBtn =
		document.getElementById("paymentTypeCardBtn");

	const bankArea =
		document.getElementById("paymentBankAddArea");

	const cardArea =
		document.getElementById("paymentCardAddArea");

	const paymentType =
		document.getElementById("newPaymentType");


	if (type === "BANK") {

		bankBtn.classList.add("active");
		cardBtn.classList.remove("active");

		bankArea.classList.remove("hidden");
		cardArea.classList.add("hidden");

		paymentType.value = "BANK";

	} else {

		bankBtn.classList.remove("active");
		cardBtn.classList.add("active");

		bankArea.classList.add("hidden");
		cardArea.classList.remove("hidden");

		paymentType.value = "CARD";
	}
}

function openEditAddressModal(button) {

    // 배송지 선택 모달 닫기
    document
        .getElementById("addressModalOverlay")
        .classList.remove("show");

    const editModal =
        document.getElementById(
            "addAddressModalOverlay"
        );

    // 제목 변경
    document.getElementById(
        "addressModalTitle"
    ).textContent = "배송지 수정";

    // form action 변경
    const form =
        document.getElementById(
            "addAddressForm"
        );

    form.action =
        "${pageContext.request.contextPath}/address/edit";

    // 기존 데이터 넣기
    document.getElementById(
        "editAddressNo"
    ).value = button.dataset.addressNo;

    document.getElementById(
        "newReceiverName"
    ).value = button.dataset.receiverName || "";

    document.getElementById(
        "newZipcode"
    ).value = button.dataset.zipcode || "";

    document.getElementById(
        "newAddress"
    ).value = button.dataset.address || "";

    document.getElementById(
        "newDetailAddress"
    ).value = button.dataset.detailAddress || "";

    const tel = button.dataset.tel || "";
    const telNumber = tel.replace(/[^0-9]/g, "");

    if (telNumber.length === 11) {

        document.getElementById("phone1").value =
            telNumber.substring(0, 3);

        document.getElementById("phone2").value =
            telNumber.substring(3, 7);

        document.getElementById("phone3").value =
            telNumber.substring(7, 11);

    } else {

        document.getElementById("phone1").value = "010";
        document.getElementById("phone2").value = "";
        document.getElementById("phone3").value = "";
    }

    syncPhoneNumber();

    const requestMsg =
        document.getElementById("newRequestMsg");

    if (requestMsg) {
        requestMsg.value =
            button.dataset.requestMsg || "";
    }

    // 기본 배송지
    const defaultCheckbox =
        form.querySelector(
            'input[name="addressDefault"]'
        );

    if (defaultCheckbox) {
        defaultCheckbox.checked =
            button.dataset.default === "true";
    }

    // 저장 → 수정
    document.getElementById(
        "addressSaveBtn"
    ).textContent = "수정";

    editModal.classList.add("show");

    document.body.classList.add(
        "modal-open"
    );
}



function addPaymentMethod() {

	if (paymentAddType === "BANK") {

		const bankCode =
			document.getElementById("newBankCode").value;
		const accountNumber =
			document.getElementById("newAccountNumber")
				.value.trim();
		const accountHolder =
			document.getElementById("newAccountHolder")
				.value.trim();
		if (!bankCode) {
			alert("은행을 선택해주세요.");
			return;
		}
		if (!accountNumber) {
			alert("계좌번호를 입력해주세요.");
			return;
		}
		if (!/^[0-9]+$/.test(accountNumber)) {
			alert("계좌번호는 숫자만 입력해주세요.");
			return;
		}
		if (!accountHolder) {
			alert("예금주를 입력해주세요.");
			return;
		}
	} else {
		const cardCompany =
			document.getElementById("newCardCompany").value;
		const card1 =
			document.getElementById("cardNumber1")
				.value.trim();
		const card2 =
			document.getElementById("cardNumber2")
				.value.trim();
		const card3 =
			document.getElementById("cardNumber3")
				.value.trim();
		const card4 =
			document.getElementById("cardNumber4")
				.value.trim();
		if (!cardCompany) {
			alert("카드사를 선택해주세요.");
			return;
		}
		const cardNumber =
			card1 + card2 + card3 + card4;
		if (!/^[0-9]{16}$/.test(cardNumber)) {
			alert("카드번호 16자리를 입력해주세요.");
			return;
		}
	}
	if (!confirm("결제수단을 등록하시겠습니까?")) {
		return;
	}
	document
		.getElementById("paymentMethodAddForm")
		.submit();
}

function addBankPaymentMethod() {
	const bankSelect = document.getElementById("newBankCode");
	const bankCode = bankSelect.value;
	const accountNumber =
		document.getElementById("newAccountNumber").value.trim();
	const accountHolder =
		document.getElementById("newAccountHolder").value.trim();

	if (!bankCode) {
		alert("은행을 선택해주세요.");
		return;
	}

	if (!accountNumber) {
		alert("계좌번호를 입력해주세요.");
		return;
	}

	if (!/^[0-9]+$/.test(accountNumber)) {
		alert("계좌번호는 숫자만 입력해주세요.");
		return;
	}

	if (!accountHolder) {
		alert("예금주를 입력해주세요.");
		return;
	}

	const bankName =
		bankSelect.options[bankSelect.selectedIndex].text;

	const last4 = accountNumber.slice(-4);

	const currentBankSelect =
		document.getElementById("bankCode");

	const option = document.createElement("option");

	option.value = bankCode;
	option.textContent =
		bankName + " / **********" + last4;

	option.selected = true;

	currentBankSelect.appendChild(option);

	const bankRadio =
		document.querySelector(
			'input[name="paymentMethod"][value="BANK_TRANSFER"]'
		);

	if (bankRadio) {
		bankRadio.checked = true;
		changePaymentMethod("BANK");
	}

	const newDefault =
		document.getElementById("newPaymentDefault").checked;

	const defaultPayment =
		document.getElementById("defaultPayment");

	if (defaultPayment) {
		defaultPayment.checked = newDefault;
	}

	closePaymentAddModal();
}

function addCardPaymentMethod() {
	const cardSelect =
		document.getElementById("newCardCompany");

	const cardCompany = cardSelect.value;

	const card1 =
		document.getElementById("cardNumber1").value.trim();

	const card2 =
		document.getElementById("cardNumber2").value.trim();

	const card3 =
		document.getElementById("cardNumber3").value.trim();

	const card4 =
		document.getElementById("cardNumber4").value.trim();

	if (!cardCompany) {
		alert("카드사를 선택해주세요.");
		return;
	}

	const cardNumber =
		card1 + card2 + card3 + card4;

	if (!/^[0-9]{16}$/.test(cardNumber)) {
		alert("카드번호 16자리를 정확하게 입력해주세요.");
		return;
	}

	const cardName =
		cardSelect.options[cardSelect.selectedIndex].text;

	const currentCardSelect =
		document.getElementById("cardCompany");

	let exists = false;

	for (let i = 0;
		 i < currentCardSelect.options.length;
		 i++) {

		if (currentCardSelect.options[i].value === cardCompany) {
			currentCardSelect.selectedIndex = i;
			exists = true;
			break;
		}
	}

	if (!exists) {
		const option =
			document.createElement("option");

		option.value = cardCompany;
		option.textContent = cardName;
		option.selected = true;

		currentCardSelect.appendChild(option);
	}

	const cardRadio =
		document.querySelector(
			'input[name="paymentMethod"][value="CARD"]'
		);

	if (cardRadio) {
		cardRadio.checked = true;
		changePaymentMethod("CARD");
	}

	closePaymentAddModal();
}

function resetPaymentAddForm() {
	document.getElementById("newBankCode").value = "";
	document.getElementById("newAccountNumber").value = "";
	document.getElementById("newAccountHolder").value = "";

	document.getElementById("newCardCompany").value = "";
	document.getElementById("cardNumber1").value = "";
	document.getElementById("cardNumber2").value = "";
	document.getElementById("cardNumber3").value = "";
	document.getElementById("cardNumber4").value = "";

	document.getElementById("newPaymentDefault").checked = true;
}

document
	.getElementById("paymentAddModalOverlay")
	.addEventListener("click", function(event) {
		if (event.target === this) {
			closePaymentAddModal();
		}
		
	});
	
const cardNumberInputs = [
    document.getElementById("cardNumber1"),
    document.getElementById("cardNumber2"),
    document.getElementById("cardNumber3"),
    document.getElementById("cardNumber4")
];

cardNumberInputs.forEach(function(input, index) {

    input.addEventListener("input", function() {

        // 숫자만 입력
        this.value =
            this.value.replace(/[^0-9]/g, "");

        // 최대 4자리
        if (this.value.length > 4) {
            this.value = this.value.substring(0, 4);
        }

        // 4자리 입력 완료 → 다음 칸
        if (
            this.value.length === 4 &&
            index < cardNumberInputs.length - 1
        ) {
            cardNumberInputs[index + 1].focus();
        }
    });

    input.addEventListener("keydown", function(event) {

        // 현재 칸이 비어있는데 Backspace
        if (
            event.key === "Backspace" &&
            this.value.length === 0 &&
            index > 0
        ) {
            cardNumberInputs[index - 1].focus();
        }
    });

});	

function openPgModal() {

    resetPgModal();

    const modal =
        document.getElementById(
            "pgModalOverlay"
        );

    const cardSelect =
        document.getElementById(
            "cardCompany"
        );

    const selectedOption =
        cardSelect.options[
            cardSelect.selectedIndex
        ];

    document.getElementById(
        "pgSelectedCard"
    ).textContent =
        selectedOption.textContent.trim();

    modal.classList.add("show");

    document.body.classList.add(
        "modal-open"
    );
}


function closePgModal() {

    const modal =
        document.getElementById(
            "pgModalOverlay"
        );

    modal.classList.remove("show");

    document.body.classList.remove(
        "modal-open"
    );
}

function processDummyPayment() {

    const agree =
        document.getElementById(
            "pgAgree"
        );

    if (!agree.checked) {

        alert(
            "결제 동의에 체크해주세요."
        );

        return;
    }

    document.getElementById(
        "pgPaymentScreen"
    ).classList.add("hidden");

    document.getElementById(
        "pgLoadingScreen"
    ).classList.remove("hidden");


    setTimeout(function() {

        dummyPaymentSuccess();

    }, 1500);
}

function dummyPaymentSuccess() {

    /*
     * 로딩 화면 숨기기
     */
    document.getElementById(
        "pgLoadingScreen"
    ).classList.add("hidden");


    /*
     * 성공 화면 표시
     */
    document.getElementById(
        "pgSuccessScreen"
    ).classList.remove("hidden");


    /*
     * 더미 거래번호
     */
    const transactionId =
        "GOODPAY_" + Date.now();


    document.getElementById(
        "pgPaymentStatus"
    ).value = "SUCCESS";


    document.getElementById(
        "pgTransactionId"
    ).value = transactionId;


    /*
     * PG 승인 완료 표시
     *
     * 이 값이 true이기 때문에
     * validatePayment()이 다시 호출되어도
     * PG 모달을 다시 띄우지 않는다.
     */
    pgPaymentApproved = true;


    const checkoutInput =
        document.getElementById(
            "checkoutNo"
        );

    if (checkoutInput) {

        sessionStorage.setItem(
            "paidCheckout_" +
            checkoutInput.value,
            "Y"
        );
    }


    /*
     * 결제 완료 화면을 1초 보여준 후
     * 실제 /order/checkout 요청
     */
    setTimeout(function() {

        const paymentForm =
            document.getElementById(
                "paymentForm"
            );

        console.log(
            "주문 submit 실행"
        );

        paymentForm.requestSubmit();

    }, 1000);
}

document
.getElementById("pgModalOverlay")
.addEventListener(
    "click",
    function(event) {

        if (event.target === this) {

            closePgModal();
        }
    }
);

window.addEventListener(
	    "pageshow",
	    function(event) {

	        const checkoutInput =
	            document.getElementById(
	                "checkoutNo"
	            );

	        if (!checkoutInput) {
	            return;
	        }

	        const checkoutNo =
	            checkoutInput.value;

	        const paid =
	            sessionStorage.getItem(
	                "paidCheckout_" + checkoutNo
	            );

	        /*
	         * 이미 결제한 checkout 페이지를
	         * 뒤로가기로 다시 들어온 경우
	         */
	        if (paid === "Y") {

	            window.location.replace(
	                "${pageContext.request.contextPath}"
	                + "/order/already-completed"
	            );

	            return;
	        }


	        /*
	         * BFCache로 복원되었지만
	         * 결제되지 않은 페이지라면
	         * PG 모달 상태 초기화
	         */
	        resetPgModal();
	    }
	);
	
function resetPgModal() {

    const modal =
        document.getElementById(
            "pgModalOverlay"
        );

    const paymentScreen =
        document.getElementById(
            "pgPaymentScreen"
        );

    const loadingScreen =
        document.getElementById(
            "pgLoadingScreen"
        );

    const successScreen =
        document.getElementById(
            "pgSuccessScreen"
        );

    const agree =
        document.getElementById(
            "pgAgree"
        );

    const payButton =
        document.querySelector(
            ".btn-pay"
        );


    if (modal) {
        modal.classList.remove("show");
    }

    if (paymentScreen) {
        paymentScreen.classList.remove(
            "hidden"
        );
    }

    if (loadingScreen) {
        loadingScreen.classList.add(
            "hidden"
        );
    }

    if (successScreen) {
        successScreen.classList.add(
            "hidden"
        );
    }

    if (agree) {
        agree.checked = false;
    }

    if (payButton) {
        payButton.disabled = false;
        payButton.textContent =
            "결제하기";
    }

    document.body.classList.remove(
        "modal-open"
    );
}
</script>

</body>
</html>