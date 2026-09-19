<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<div class="wow-payment-popup">
    <button type="button" class="wow-payment-close" id="wowPaymentCloseBtn">&times;</button>

    <div class="wow-payment-popup-logo">WOW!</div>
    <h2>와우 멤버십 가입</h2>
    <p class="wow-payment-desc">결제수단을 선택해주세요.</p>

    <form action="${pageContext.request.contextPath}/wow/join"
          method="post"
          id="wowModalPaymentForm">

        <c:choose>
            <c:when test="${not empty paymentMethods}">
                <div class="wow-popup-payment-list">
                    <c:forEach var="payment" items="${paymentMethods}" varStatus="status">
                        <label class="wow-popup-payment-item">
                            <input type="radio"
                                   name="paymentMethodNo"
                                   value="${payment.paymentMethodNo}"
                                   <c:if test="${status.first}">checked</c:if>>

                            <div class="wow-popup-payment-info">
                                <c:choose>
                                    <c:when test="${payment.paymentType eq 'BANK'}">
                                        <strong>${payment.bankName}</strong>
                                        <span>계좌 ****${payment.accountLast4}</span>
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
                                    <span class="wow-popup-default">기본 결제수단</span>
                                </c:if>
                            </div>
                        </label>
                    </c:forEach>
                </div>

                <div class="wow-popup-price">
                    <span>월 이용료</span>
                    <strong>7,890원</strong>
                </div>

                <label class="wow-popup-agree">
                    <input type="checkbox" id="wowModalAgree">
                    <span>월 7,890원 정기결제에 동의합니다.</span>
                </label>

                <button type="submit"
                        class="wow-popup-submit"
                        id="wowModalSubmit"
                        disabled>
                    결제하고 와우 가입하기
                </button>
            </c:when>

            <c:otherwise>
                <div class="wow-popup-no-payment">
                    <p>등록된 결제수단이 없습니다.</p>

                    <button type="button"
                            onclick="location.href='${pageContext.request.contextPath}/payment-method/list'">
                        결제수단 등록하기
                    </button>
                </div>
            </c:otherwise>
        </c:choose>

    </form>
</div>