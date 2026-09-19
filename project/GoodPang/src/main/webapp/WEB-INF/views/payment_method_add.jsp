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
<title>결제수단 등록</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/payment_method_add.css">
</head>

<body>

<jsp:include page="/inc/header.jsp" />

<div class="payment-add-page">
    <div class="payment-add-container">

        <div class="payment-add-header">
            <h1>결제수단 등록</h1>
            <p>결제에 사용할 계좌 또는 카드를 등록해주세요.</p>
        </div>

        <form action="${pageContext.request.contextPath}/payment-method/add"
              method="post" id="paymentAddForm">

            <div class="payment-section">
                <h2>결제수단 선택</h2>

                <div class="payment-type-list">
                    <label class="payment-type-item">
                        <input type="radio" name="paymentType" value="BANK" checked>
                        <span>계좌</span>
                    </label>

                    <label class="payment-type-item">
                        <input type="radio" name="paymentType" value="CARD">
                        <span>카드</span>
                    </label>
                </div>
            </div>

            <%-- 계좌 등록 --%>
            <div id="bankSection" class="payment-section">
                <h2>계좌 정보</h2>

                <div class="payment-field">
                    <label for="bankCode">은행</label>
                    <select id="bankCode" name="bankCode">
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
                    <label for="accountNumber">계좌번호</label>
                    <input type="text" id="accountNumber" name="accountNumber"
                           placeholder="- 없이 숫자 10~14자리"
                           inputmode="numeric" maxlength="14" autocomplete="off">
                </div>

                <div class="payment-field">
                    <label for="accountHolder">예금주</label>
                    <input type="text" id="accountHolder" name="accountHolder"
                           placeholder="예금주명 입력" maxlength="10">
                </div>
            </div>

            <%-- 카드 등록 --%>
            <div id="cardSection" class="payment-section" style="display:none;">
                <h2>카드 정보</h2>

                <div class="payment-field">
                    <label for="cardCompany">카드사</label>
                    <select id="cardCompany" name="cardCompany">
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
                               class="card-number-part" inputmode="numeric"
                               maxlength="4" autocomplete="off">

                        <span>-</span>

                        <input type="text" id="cardNumber2" name="cardNumber2"
                               class="card-number-part" inputmode="numeric"
                               maxlength="4" autocomplete="off">

                        <span>-</span>

                        <input type="password" id="cardNumber3" name="cardNumber3"
                               class="card-number-part" inputmode="numeric"
                               maxlength="4" autocomplete="off">

                        <span>-</span>

                        <input type="password" id="cardNumber4" name="cardNumber4"
                               class="card-number-part" inputmode="numeric"
                               maxlength="4" autocomplete="off">
                    </div>

                    <p class="payment-help">카드번호 16자리를 입력해주세요.</p>
                </div>
            </div>

            <label class="payment-default-label">
                <input type="checkbox" name="paymentDefault" value="Y">
                <span>기본 결제수단으로 설정</span>
            </label>

            <div class="payment-add-actions">
                <button type="button" class="payment-cancel-btn"
                        onclick="history.back()">
                    취소
                </button>

                <button type="submit" class="payment-submit-btn">
                    등록하기
                </button>
            </div>
        </form>

    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const paymentTypes = document.querySelectorAll('input[name="paymentType"]');
    const bankSection = document.getElementById("bankSection");
    const cardSection = document.getElementById("cardSection");
    const accountNumber = document.getElementById("accountNumber");
    const cardNumberParts = document.querySelectorAll(".card-number-part");
    const form = document.getElementById("paymentAddForm");

    function changePaymentType() {
        const selected = document.querySelector('input[name="paymentType"]:checked');

        if (selected.value === "BANK") {
            bankSection.style.display = "block";
            cardSection.style.display = "none";
        } else {
            bankSection.style.display = "none";
            cardSection.style.display = "block";
        }
    }

    accountNumber.addEventListener("input", function() {
        this.value = this.value.replace(/[^0-9]/g, "").slice(0, 14);
    });

    cardNumberParts.forEach(function(input, index) {
        input.addEventListener("input", function() {
            this.value = this.value.replace(/[^0-9]/g, "").slice(0, 4);

            if (this.value.length === 4 && index < cardNumberParts.length - 1) {
                cardNumberParts[index + 1].focus();
            }
        });

        input.addEventListener("keydown", function(event) {
            if (event.key === "Backspace"
                    && this.value.length === 0
                    && index > 0) {
                cardNumberParts[index - 1].focus();
            }
        });
    });

    paymentTypes.forEach(function(type) {
        type.addEventListener("change", changePaymentType);
    });

    form.addEventListener("submit", function(event) {
        const paymentType =
            document.querySelector('input[name="paymentType"]:checked').value;

        if (paymentType === "BANK") {
            const bankCode = document.getElementById("bankCode").value;
            const account = accountNumber.value.trim();
            const accountHolder =
                document.getElementById("accountHolder").value.trim();

            if (!bankCode) {
                event.preventDefault();
                alert("은행을 선택해주세요.");
                return;
            }

            if (!/^\d{10,14}$/.test(account)) {
                event.preventDefault();
                alert("계좌번호는 숫자 10~14자리로 입력해주세요.");
                accountNumber.focus();
                return;
            }

            if (!accountHolder) {
                event.preventDefault();
                alert("예금주를 입력해주세요.");
                return;
            }

        } else {
            const cardCompany =
                document.getElementById("cardCompany").value;

            if (!cardCompany) {
                event.preventDefault();
                alert("카드사를 선택해주세요.");
                return;
            }

            for (const input of cardNumberParts) {
                if (!/^\d{4}$/.test(input.value)) {
                    event.preventDefault();
                    alert("카드번호는 각 칸에 숫자 4자리씩 입력해주세요.");
                    input.focus();
                    return;
                }
            }
        }
    });

    changePaymentType();
});
</script>

</body>
</html>