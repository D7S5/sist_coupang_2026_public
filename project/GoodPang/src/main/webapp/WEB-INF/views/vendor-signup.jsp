<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor-signup.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <title>굿팡 비즈니스 입점 신청</title>

</head>

<body>

  <div class="page">

    <main class="vendor-signup">

      <!-- 굿팡 로고 -->
      <div class="logo-area">
      	<a href="${pageContext.request.contextPath}/index.jsp" class="brand-goodpang">GoodPang marketplace</a>
      </div>


      <!-- 제목 -->
      <h1 class="title">
        굿팡 비즈니스, 지금 시작하세요
      </h1>

      <p class="subtitle">
        사업자 정보를 입력하고 굿팡 파트너로 입점 신청을 진행해주세요.
      </p>


      <form class="form" id="vendorForm" novalidate method="post"
        action="${pageContext.request.contextPath}/vendor/signup">

        <!-- 사업자 유형 -->

        <section class="field-group">

          <h2 class="group-title">
            사업자 유형
          </h2>

          <div class="biz-type-row">

            <label class="biz-type-option">

              <input type="radio" name="bizType" id="bizTypePersonal" value="individual" checked>

              <span class="biz-type-card">
                <strong>개인사업자</strong>
                <small>개인 명의로 사업자등록을 하신 경우</small>
              </span>

            </label>

            <label class="biz-type-option">

              <input type="radio" name="bizType" id="bizTypeCorp" value="corporate">

              <span class="biz-type-card">
                <strong>법인사업자</strong>
                <small>법인 명의로 사업자등록을 하신 경우</small>
              </span>

            </label>

          </div>

        </section>


        <!-- 사업자 정보 -->

        <section class="field-group">

          <h2 class="group-title">
            사업자 정보
          </h2>

          <div class="field">

            <label class="label" for="bizNumber">
              사업자등록번호
            </label>

            <div class="inline-row">

              <input class="input" id="bizNumber" name="bizNumber" type="text" inputmode="numeric"
                placeholder="000-00-00000" maxlength="12">

              <button class="check-button" id="bizNumberCheck" type="button">
                확인
              </button>

            </div>

            <p id="bizNumberMessage" class="message"></p>

          </div>

          <div class="field">

            <label class="label" for="companyName">
              상호명 (회사명)
            </label>

            <input class="input" id="companyName" name="companyName" type="text" placeholder="상호명을 입력해주세요">

          </div>

          <div class="field">

            <label class="label" for="ceoName">
              대표자명
            </label>

            <input class="input" id="ceoName" name="ceoName" type="text" placeholder="대표자 성함을 입력해주세요"
              autocomplete="name">

          </div>

        </section>


        <!-- 담당자 정보 -->

        <section class="field-group">

          <h2 class="group-title">
            담당자 정보
          </h2>

          <div class="field">

            <label class="label" for="managerName">
              담당자명
            </label>

            <input class="input" id="managerName" name="managerName" type="text" placeholder="담당자 이름을 입력해주세요">

          </div>

          <div class="field">

            <label class="label" for="email">
              아이디(이메일)
            </label>

            <input class="input" id="email" name="email" type="email" placeholder="예) partner@company.com"
              autocomplete="email">

            <p id="emailMessage" class="message error">
              이메일 주소를 정확하게 입력해주세요.
            </p>

          </div>

          <div class="field password-field">

            <label class="label" for="password">
              비밀번호
            </label>

            <input class="input" id="password" name="password" type="password" placeholder="영문, 숫자 포함 8자 이상"
              autocomplete="new-password">

            <button class="password-button" id="passwordToggle" type="button">
              보기
            </button>

          </div>

          <div class="field password-field">

            <label class="label" for="passwordConfirm">
              비밀번호 확인
            </label>

            <input class="input" id="passwordConfirm" name="passwordConfirm" type="password"
              placeholder="비밀번호를 다시 입력해주세요" autocomplete="new-password">

            <button class="password-button" id="passwordConfirmToggle" type="button">
              보기
            </button>

            <p id="passwordMessage" class="message error">
              비밀번호가 일치하지 않습니다.
            </p>

          </div>

          <div class="field">

            <label class="label" for="phone">
              휴대폰 번호
            </label>

            <div class="phone-row">

              <select class="country" id="country" aria-label="국가번호">
                <option value="+82">🇰🇷 +82</option>
              </select>

              <input class="input phone-input" id="phone" name="phone" type="tel" placeholder="휴대폰 번호"
                autocomplete="tel" maxlength="13">

            </div>

            <div class="verification-row">

              <input class="input verification-input" id="verification" name="verification" type="text"
                placeholder="인증번호" maxlength="6" inputmode="numeric">

              <button class="verify-button" id="verifyButton" type="button">
                인증번호 받기
              </button>

            </div>

            <p id="verificationMessage" class="message"></p>

          </div>

        </section>


        <!-- 약관 -->

        <section class="agreement">

          <!-- 전체 동의 -->

          <div class="agreement-all">

            <label>

              <input class="check" id="agreeAll" type="checkbox">

              모두 동의합니다.

            </label>

          </div>

          <p class="agreement-all-desc">
            모두 동의에는 필수 및 선택 목적(광고성 정보 수신 포함)에 대한 동의가 포함되어 있으며, 선택 목적에 동의를 거부하시는 경우에도 서비스 이용이 가능합니다.
          </p>


          <div class="agreement-list">

            <!-- 만 19세 이상 -->

            <div class="agreement-item">

              <input class="check required-check" id="agreeAge" type="checkbox">

              <label for="agreeAge">

                <span class="required">
                  [필수]
                </span>

                만 19세 이상입니다

              </label>

            </div>


            <!-- 굿팡 서비스 이용약관 - 사업자용 -->

            <div class="agreement-item">

              <input class="check required-check" id="agreeTerms" type="checkbox">

              <label for="agreeTerms">

                <span class="required">
                  [필수]
                </span>

                굿팡 서비스 이용약관 - 사업자용

                <a href="./sc-ui/account/privacy/termsService.html" class="terms-link" target="_blank" rel="noopener"
                  onclick="return openTermsWindow(this.href);">
                  보기
                </a>

              </label>

            </div>


            <!-- 전자금융거래 -->

            <div class="agreement-item">

              <input class="check required-check" id="agreeFinance" type="checkbox">

              <label for="agreeFinance">

                <span class="required">
                  [필수]
                </span>

                굿팡페이(주) 전자금융거래 이용약관

                <a href="./sc-ui/account/privacy/termsEft.html" class="terms-link" target="_blank" rel="noopener"
                  onclick="return openTermsWindow(this.href);">
                  보기
                </a>

              </label>

            </div>


            <!-- 마케팅 목적 개인정보 -->

            <div class="agreement-item">

              <input class="check" id="agreeMarketingPi" type="checkbox">

              <label for="agreeMarketingPi">

                <span class="optional">
                  [선택]
                </span>

                마케팅 목적의 개인정보 수집 및 이용 동의

                <a href="./sc-ui/account/privacy/agreePiForAd.html" class="terms-link" target="_blank" rel="noopener"
                  onclick="return openTermsWindow(this.href);">
                  보기
                </a>

              </label>

            </div>


            <!-- 프로모션(광고) 수신 -->

            <div class="agreement-item">

              <input class="check" id="agreeAd" type="checkbox">

              <label for="agreeAd">

                <span class="optional">
                  [선택]
                </span>

                특별 프로모션 혜택(광고) 수신 동의

                <a href="./sc-ui/account/privacy/agreeForAd.html" class="terms-link" target="_blank" rel="noopener"
                  onclick="return openTermsWindow(this.href);">
                  보기
                </a>

              </label>

            </div>

          </div>

          <a href="./sc-ui/account/privacy/agreePi.html" class="privacy-guide-link" target="_blank" rel="noopener"
            onclick="return openTermsWindow(this.href);">
            개인정보 수집 및 이용 안내
          </a>

        </section>


        <!-- 가입 버튼 -->

        <button class="submit" id="submitButton" type="submit">
          입점 신청하기
        </button>

      </form>


      <p class="login-link">
        이미 굿팡 비즈니스 계정이 있으신가요?
        <a href="${pageContext.request.contextPath}/vendor/login">로그인</a>
      </p>


      <!-- 하단 -->

      <footer class="footer">

        <div>
          굿팡(주)
        </div>

        <div>
          © 2026 Coupang Clone. All rights reserved.
        </div>

      </footer>

    </main>

  </div>


  <!-- 모달 -->

  <div class="modal-backdrop" id="modalBackdrop">

    <div class="modal">

      <h2 class="modal-title" id="modalTitle">
        안내
      </h2>

      <div class="modal-body" id="modalBody"></div>

      <div class="modal-footer">

        <button class="modal-button" id="modalConfirm" type="button">
          확인
        </button>

      </div>

    </div>

  </div>


  <script>

    /* =========================================================
       약관 보기 — 팝업 새창
    ========================================================= */

    function openTermsWindow(url) {
      const width = 720;
      const height = 800;
      const left = (window.screen.width - width) / 2;
      const top = (window.screen.height - height) / 2;

      window.open(
        url,
        "termsWindow",
        "width=" + width + ",height=" + height +
        ",left=" + left + ",top=" + top +
        ",scrollbars=yes,resizable=yes"
      );

      return false;
    }


    /* =========================================================
       요소
    ========================================================= */

    const form = document.getElementById("vendorForm");

    const bizNumber = document.getElementById("bizNumber");
    const bizNumberCheck = document.getElementById("bizNumberCheck");
    const bizNumberMessage = document.getElementById("bizNumberMessage");

    const companyName = document.getElementById("companyName");
    const ceoName = document.getElementById("ceoName");
    const managerName = document.getElementById("managerName");

    const email = document.getElementById("email");
    const emailMessage = document.getElementById("emailMessage");

    const password = document.getElementById("password");
    const passwordConfirm = document.getElementById("passwordConfirm");
    const passwordMessage = document.getElementById("passwordMessage");

    const phone = document.getElementById("phone");

    const verification = document.getElementById("verification");
    const verifyButton = document.getElementById("verifyButton");
    const verificationMessage = document.getElementById("verificationMessage");

    const agreeAll = document.getElementById("agreeAll");
    const requiredChecks = document.querySelectorAll(".required-check");


    /* =========================================================
       모달
    ========================================================= */

    const modalBackdrop = document.getElementById("modalBackdrop");
    const modalTitle = document.getElementById("modalTitle");
    const modalBody = document.getElementById("modalBody");
    const modalConfirm = document.getElementById("modalConfirm");


    function showModal(title, message) {
      modalTitle.textContent = title;
      modalBody.textContent = message;
      modalBackdrop.classList.add("show");
    }


    function hideModal() {
      modalBackdrop.classList.remove("show");
    }


    modalConfirm.addEventListener("click", hideModal);

    modalBackdrop.addEventListener("click", function (event) {
      if (event.target === modalBackdrop) {
        hideModal();
      }
    });


    /* =========================================================
       사업자등록번호 — 자동 하이픈(000-00-00000) + 확인
    ========================================================= */

    let bizNumberChecked = false;

    bizNumber.addEventListener("input", function () {
      const value = this.value.replace(/\D/g, "").slice(0, 10);

      if (value.length <= 3) {
        this.value = value;
      } else if (value.length <= 5) {
        this.value = value.slice(0, 3) + "-" + value.slice(3);
      } else {
        this.value =
          value.slice(0, 3) + "-" + value.slice(3, 5) + "-" + value.slice(5);
      }

      bizNumberChecked = false;
      bizNumberCheck.classList.remove("checked");
      bizNumber.classList.remove("invalid", "valid");
      bizNumberMessage.className = "message";
    });


    bizNumberCheck.addEventListener("click", function () {
      const digits = bizNumber.value.replace(/\D/g, "");

      if (digits.length !== 10) {
        bizNumber.classList.add("invalid");
        bizNumberMessage.textContent = "사업자등록번호 10자리를 정확히 입력해주세요.";
        bizNumberMessage.className = "message error show";
        bizNumberChecked = false;
        return;
      }

      bizNumber.classList.remove("invalid");
      bizNumber.classList.add("valid");
      bizNumberCheck.classList.add("checked");
      bizNumberMessage.textContent = "확인되었습니다.";
      bizNumberMessage.className = "message success show";
      bizNumberChecked = true;
    });


    /* =========================================================
       비밀번호 보기
    ========================================================= */

    function setupPasswordToggle(input, button) {
      button.addEventListener("click", function () {
        if (input.type === "password") {
          input.type = "text";
          button.textContent = "숨기기";
        } else {
          input.type = "password";
          button.textContent = "보기";
        }
      });
    }

    setupPasswordToggle(password, document.getElementById("passwordToggle"));
    setupPasswordToggle(
      passwordConfirm,
      document.getElementById("passwordConfirmToggle")
    );


    /* =========================================================
       휴대폰 번호 자동 하이픈
    ========================================================= */

    phone.addEventListener("input", function () {
      const value = this.value.replace(/\D/g, "").slice(0, 11);

      if (value.length <= 3) {
        this.value = value;
      } else if (value.length <= 7) {
        this.value = value.slice(0, 3) + "-" + value.slice(3);
      } else {
        this.value =
          value.slice(0, 3) + "-" + value.slice(3, 7) + "-" + value.slice(7);
      }
    });


    /* =========================================================
       이메일 검사
    ========================================================= */

    function validateEmail() {
      if (!email.value) {
        email.classList.remove("invalid", "valid");
        emailMessage.classList.remove("show");
        return false;
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

      if (!emailRegex.test(email.value)) {
        email.classList.add("invalid");
        email.classList.remove("valid");
        emailMessage.classList.add("show");
        return false;
      }

      email.classList.remove("invalid");
      email.classList.add("valid");
      emailMessage.classList.remove("show");
      return true;
    }

    email.addEventListener("blur", validateEmail);


    /* =========================================================
       비밀번호 확인
    ========================================================= */

    function validatePasswordConfirm() {
      if (!passwordConfirm.value) {
        passwordConfirm.classList.remove("invalid", "valid");
        passwordMessage.classList.remove("show");
        return false;
      }

      if (password.value !== passwordConfirm.value) {
        passwordConfirm.classList.add("invalid");
        passwordConfirm.classList.remove("valid");
        passwordMessage.classList.add("show");
        return false;
      }

      passwordConfirm.classList.remove("invalid");
      passwordConfirm.classList.add("valid");
      passwordMessage.classList.remove("show");
      return true;
    }

    password.addEventListener("input", function () {
      if (passwordConfirm.value) {
        validatePasswordConfirm();
      }
    });

    passwordConfirm.addEventListener("input", validatePasswordConfirm);


    /* =========================================================
       전체 약관 동의
    ========================================================= */

    agreeAll.addEventListener("change", function () {
      requiredChecks.forEach(function (check) {
        check.checked = agreeAll.checked;
      });
    });

    requiredChecks.forEach(function (check) {
      check.addEventListener("change", function () {
        const allChecked = Array.from(requiredChecks).every(
          (item) => item.checked
        );
        agreeAll.checked = allChecked;
      });
    });


    /* =========================================================
       인증번호 — 데모용. 실제 SMS 발송은 하지 않음
    ========================================================= */

    let verificationRequested = false;
    let verificationCompleted = false;

    verifyButton.addEventListener("click", function () {
      const phoneDigits = phone.value.replace(/\D/g, "");

      if (phoneDigits.length < 10) {
        phone.classList.add("invalid");
        showModal("휴대폰 번호 확인", "휴대폰 번호를 정확하게 입력해주세요.");
        phone.focus();
        return;
      }

      phone.classList.remove("invalid");

      verificationRequested = true;
      verificationCompleted = false;

      verifyButton.textContent = "인증번호 재전송";
      verifyButton.classList.add("sent");

      verificationMessage.textContent =
        "인증번호가 발송되었습니다. 데모에서는 임의의 6자리 숫자를 입력해주세요.";
      verificationMessage.className = "message show";

      verification.focus();
    });

    verification.addEventListener("input", function () {
      this.value = this.value.replace(/\D/g, "").slice(0, 6);

      if (!verificationRequested) {
        return;
      }

      if (this.value.length === 6) {
        verificationCompleted = true;

        this.classList.remove("invalid");
        this.classList.add("valid");

        verificationMessage.textContent = "인증이 완료되었습니다.";
        verificationMessage.className = "message success show";

        verifyButton.textContent = "인증완료";
        verifyButton.disabled = true;
      } else {
        verificationCompleted = false;

        this.classList.remove("valid");

        verificationMessage.textContent = "인증번호 6자리를 입력해주세요.";
        verificationMessage.className = "message error show";
      }
    });


    /* =========================================================
       필수 입력 검사
    ========================================================= */

    function requiredField(element, message) {
      if (!element.value.trim()) {
        element.classList.add("invalid");
        showModal("입력 내용을 확인해주세요", message);
        element.focus();
        return false;
      }

      element.classList.remove("invalid");
      return true;
    }


    /* =========================================================
       가입 신청
    ========================================================= */

    form.addEventListener("submit", function (event) {
      event.preventDefault();

      /* 사업자등록번호 */

      if (!bizNumberChecked) {
        showModal("입력 내용을 확인해주세요", "사업자등록번호 확인을 완료해주세요.");
        bizNumber.focus();
        return;
      }

      /* 상호명 / 대표자명 / 담당자명 */

      if (!requiredField(companyName, "상호명을 입력해주세요.")) return;
      if (!requiredField(ceoName, "대표자명을 입력해주세요.")) return;
      if (!requiredField(managerName, "담당자명을 입력해주세요.")) return;

      /* 이메일 */

      if (!validateEmail()) {
        showModal("입력 내용을 확인해주세요", "이메일 주소를 정확하게 입력해주세요.");
        email.focus();
        return;
      }

      /* 비밀번호 */

      if (!password.value) {
        password.classList.add("invalid");
        showModal("입력 내용을 확인해주세요", "비밀번호를 입력해주세요.");
        password.focus();
        return;
      }

      if (password.value.length < 8) {
        password.classList.add("invalid");
        showModal("입력 내용을 확인해주세요", "비밀번호는 8자 이상 입력해주세요.");
        password.focus();
        return;
      }

      if (!validatePasswordConfirm()) {
        showModal("입력 내용을 확인해주세요", "비밀번호가 일치하지 않습니다.");
        passwordConfirm.focus();
        return;
      }

      /* 휴대폰 */

      const phoneDigits = phone.value.replace(/\D/g, "");

      if (phoneDigits.length < 10) {
        phone.classList.add("invalid");
        showModal("입력 내용을 확인해주세요", "휴대폰 번호를 입력해주세요.");
        phone.focus();
        return;
      }

      if (!verificationRequested) {
        showModal("본인인증", "휴대폰 인증번호를 받아주세요.");
        return;
      }

      if (!verificationCompleted) {
        showModal("본인인증", "휴대폰 인증을 완료해주세요.");
        verification.focus();
        return;
      }

      /* 필수 약관 */

      const allRequired = Array.from(requiredChecks).every(
        (checkbox) => checkbox.checked
      );

      if (!allRequired) {
        showModal("약관 동의", "필수 약관에 모두 동의해주세요.");
        return;
      }

      /* 완료 - 서버로 실제 제출 (doPost에서 파라미터 확인용) */

      form.submit();
    });


    /* =========================================================
       입력 시작 시 에러 제거
    ========================================================= */

    [
      companyName,
      ceoName,
      managerName,
      email,
      password,
      passwordConfirm,
      phone,
      verification
    ].forEach(function (element) {
      element.addEventListener("input", function () {
        if (this.value) {
          this.classList.remove("invalid");
        }
      });
    });

  </script>

</body>

</html>