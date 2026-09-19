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
<title>결제수단 관리</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/payment_method_list.css">
</head>

<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="payment-page">
		<div class="payment-container">

			<div class="payment-header">
				<div>
					<h1>결제수단 관리</h1>
					<p>등록된 계좌와 카드를 관리할 수 있습니다.</p>
				</div>

				<button type="button" class="payment-add-btn"
					onclick="location.href='${pageContext.request.contextPath}/payment-method/add'">
					+ 결제수단 등록</button>
			</div>

			<c:choose>

				<c:when test="${not empty paymentMethods}">

					<div class="payment-list">

						<c:forEach var="payment" items="${paymentMethods}">

							<div class="payment-card">

								<div class="payment-card-top">
									<div class="payment-type-icon">
										<c:choose>
											<c:when test="${payment.paymentType eq 'BANK'}">
                                            🏦
                                        </c:when>

											<c:when test="${payment.paymentType eq 'CARD'}">
                                            💳
                                        </c:when>

											<c:otherwise>
                                            💰
                                        </c:otherwise>
										</c:choose>
									</div>

									<div class="payment-info">

										<c:choose>

											<c:when test="${payment.paymentType eq 'BANK'}">
												<div class="payment-name">
													${payment.bankName}

													<c:if test="${payment.paymentDefault}">
														<span class="default-badge"> 기본 결제수단 </span>
													</c:if>
												</div>

												<div class="payment-number">계좌
													****${payment.accountLast4}</div>

												<c:if test="${not empty payment.accountHolder}">
													<div class="payment-sub">예금주 ${payment.accountHolder}
													</div>
												</c:if>
											</c:when>

											<c:when test="${payment.paymentType eq 'CARD'}">
												<div class="payment-name">
													${payment.cardCompany}

													<c:if test="${payment.paymentDefault}">
														<span class="default-badge"> 기본 결제수단 </span>
													</c:if>
												</div>

												<div class="payment-number">카드
													****${payment.cardLast4}</div>
											</c:when>

										</c:choose>

									</div>
								</div>

								<div class="payment-actions">

									<c:if test="${not payment.paymentDefault}">
										<form
											action="${pageContext.request.contextPath}/payment-method/default"
											method="post">

											<input type="hidden" name="paymentMethodNo"
												value="${payment.paymentMethodNo}">

											<button type="submit" class="payment-default-btn">
												기본 결제수단 설정</button>
										</form>
									</c:if>

									<form
										action="${pageContext.request.contextPath}/payment-method/delete"
										method="post" onsubmit="return confirm('이 결제수단을 삭제하시겠습니까?');">

										<input type="hidden" name="paymentMethodNo"
											value="${payment.paymentMethodNo}">

										<button type="submit" class="payment-delete-btn">삭제</button>
									</form>

								</div>

							</div>

						</c:forEach>

					</div>

				</c:when>

				<c:otherwise>

					<div class="payment-empty">
						<div class="payment-empty-icon">💳</div>

						<h2>등록된 결제수단이 없습니다.</h2>

						<p>결제수단을 등록하면 주문과 와우 멤버십 결제에 편리하게 사용할 수 있습니다.</p>

						<button type="button" class="payment-empty-add-btn"
							onclick="location.href='${pageContext.request.contextPath}/payment-method/add'">
							결제수단 등록하기</button>
					</div>

				</c:otherwise>

			</c:choose>

			<button type="button" class="payment-back-btn"
				onclick="history.back()">이전으로</button>

		</div>
	</div>

</body>
</html>