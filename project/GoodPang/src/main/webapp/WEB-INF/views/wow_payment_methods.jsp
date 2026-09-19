<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<c:choose>
    <c:when test="${empty paymentMethods}">
        <div class="wow-payment-empty">등록된 결제수단이 없습니다.</div>
    </c:when>

    <c:otherwise>
        <c:forEach var="payment" items="${paymentMethods}" varStatus="status">
            <label class="wow-payment-method">
                <input type="radio"
                       name="paymentMethodNo"
                       value="${payment.paymentMethodNo}"
                       ${status.first ? 'checked' : ''}
                       required>

                <div class="wow-payment-method-info">
                    <c:choose>
                        <c:when test="${payment.paymentType eq 'BANK'}">
                            <strong><c:out value="${payment.bankName}" /></strong>
                            <span>계좌 **** <c:out value="${payment.accountLast4}" /></span>
                            <span>예금주 <c:out value="${payment.accountHolder}" /></span>
                        </c:when>

                        <c:when test="${payment.paymentType eq 'CARD'}">
                            <strong>
                                <c:choose>
                                    <c:when test="${payment.cardCompany eq 'SHINHAN'}">신한카드</c:when>
                                    <c:when test="${payment.cardCompany eq 'KB'}">KB국민카드</c:when>
                                    <c:when test="${payment.cardCompany eq 'SAMSUNG'}">삼성카드</c:when>
                                    <c:when test="${payment.cardCompany eq 'HYUNDAI'}">현대카드</c:when>
                                    <c:when test="${payment.cardCompany eq 'LOTTE'}">롯데카드</c:when>
                                    <c:when test="${payment.cardCompany eq 'HANA'}">하나카드</c:when>
                                    <c:otherwise><c:out value="${payment.cardCompany}" /></c:otherwise>
                                </c:choose>
                            </strong>

                            <span>카드 **** <c:out value="${payment.cardLast4}" /></span>
                        </c:when>
                    </c:choose>

                    <c:if test="${payment.paymentDefault}">
                        <span class="wow-default-badge">기본 결제수단</span>
                    </c:if>
                </div>
            </label>
        </c:forEach>
    </c:otherwise>
</c:choose>