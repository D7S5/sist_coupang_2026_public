<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>

    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원가입</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/signup.css">
</head>

<body>
	<div class="page">
		<main class="signup">

			<!-- GoodPang 로고 -->
			<h1 class="logo">
				<a href="${pageContext.request.contextPath}/" title="GoodPang 홈으로">
					<span class="brand-goodpang">GoodPang</span>
				</a>
			</h1>

			<br>

			<h1 class="title">회원정보를 입력해주세요</h1>

			<!-- 서버에서 전달된 오류 메시지 -->
			<c:if test="${not empty error}">
				<p class="message error show">
					<c:out value="${error}" />
				</p>
			</c:if>

			<!--
      SignupServlet:
      @WebServlet("/signup")
      doPost()로 전송
    -->
			<form class="form" id="signupForm"
				action="${pageContext.request.contextPath}/signup" method="post"
				novalidate>

				<!-- 아이디 -->
				<div class="field">
					<input class="input" id="memberId" name="memberId" type="text"
						placeholder="아이디" autocomplete="username" maxlength="50">
				</div>

				<!-- 이메일 -->
				<div class="field">
					<input class="input" id="email" name="email" type="email"
						placeholder="이메일" autocomplete="email" maxlength="100">

					<p id="emailMessage" class="message error">이메일 주소를
						정확하게 입력해주세요.</p>
				</div>

				<!-- 비밀번호 -->
				<div class="field password-field">
					<input class="input" id="password" name="memberPw" type="password"
						placeholder="비밀번호" autocomplete="new-password" maxlength="100">

					<button class="password-button" id="passwordToggle" type="button">
						보기</button>
				</div>

				<!-- 비밀번호 확인 -->
				<div class="field password-field">
					<input class="input" id="passwordConfirm" name="memberPwConfirm"
						type="password" placeholder="비밀번호 확인" autocomplete="new-password"
						maxlength="100">

					<button class="password-button" id="passwordConfirmToggle"
						type="button">보기</button>

					<p id="passwordMessage" class="message error">비밀번호가
						일치하지 않습니다.</p>
				</div>

				<!-- 이름 -->
				<div class="field name-field">
					<input class="input" id="name" name="memberName" type="text"
						placeholder="이름" autocomplete="name" maxlength="30">
				</div>

				<!-- 휴대폰 -->
				<div class="phone-row">
					<select class="country" id="country" aria-label="국가번호">
						<option value="+82">🇰🇷 +82</option>
					</select> <input class="input phone-input" id="phone" name="phone"
						type="tel" placeholder="휴대폰 번호" autocomplete="tel" maxlength="13">
				</div>

				<!-- 인증번호 -->
				<div class="verification-row">
					<input class="input verification-input" id="verification"
						name="verification" type="text" placeholder="인증번호" maxlength="6"
						inputmode="numeric">

					<button class="verify-button" id="verifyButton" type="button">
						인증번호 받기</button>
				</div>

				<p id="verificationMessage" class="message"></p>

				<!-- 약관 -->
				<section class="agreement">

					<!-- 전체 동의 -->
					<div class="agreement-all">
						<label> <input class="check" id="agreeAll" type="checkbox">
							만 14세 이상이며 모두 동의합니다.
						</label>
					</div>

					<div class="agreement-list">

						<!-- 이용약관 -->
						<div class="agreement-item">
							<input class="check required-check" id="agreeTerms"
								type="checkbox"> <label for="agreeTerms">
								<span class="required">[필수]</span> GoodPang 이용약관 동의 <a href="#"
								class="terms-link" onclick="return false;">
									보기 </a>
							</label>
						</div>

						<!-- 전자금융거래 -->
						<div class="agreement-item">
							<input class="check required-check" id="agreeFinance"
								type="checkbox"> <label for="agreeFinance">
								<span class="required">[필수]</span> 전자금융거래 이용약관 동의 <a href="#"
								class="terms-link" onclick="return false;">
									보기 </a>
							</label>
						</div>

						<!-- 개인정보 -->
						<div class="agreement-item">
							<input class="check required-check" id="agreePrivacy"
								type="checkbox"> <label for="agreePrivacy">
								<span class="required">[필수]</span> 개인정보 수집 및 이용 동의 <a href="#"
								class="terms-link" onclick="return false;">
									보기 </a>
							</label>
						</div>

						<!-- 마케팅 -->
						<div class="agreement-item">
							<input class="check" id="agreeMarketing" type="checkbox">

							<label for="agreeMarketing"> <span class="optional">[선택]</span>
								개인정보 수집 및 이용 동의 (마케팅)
							</label>
						</div>

						<!-- 광고성 정보 -->
						<div class="agreement-item">
							<input class="check" id="agreeAdvertisement" type="checkbox">

							<label for="agreeAdvertisement"> <span class="optional">[선택]</span>
								광고성 정보 수신 동의
							</label>
						</div>

					</div>
				</section>

				<!-- 가입 버튼 -->
				<button class="submit" id="submitButton" type="submit">
					동의하고 가입완료</button>

			</form>

			<footer class="footer">
				<div>GoodPang</div>
				<div>© 2026 GoodPang. All rights reserved.</div>
			</footer>

		</main>
	</div>


	<!-- 모달 -->
	<div class="modal-backdrop" id="modalBackdrop">
		<div class="modal">

			<h2 class="modal-title" id="modalTitle">안내</h2>

			<div class="modal-body" id="modalBody"></div>

			<div class="modal-footer">
				<button class="modal-button" id="modalConfirm" type="button">
					확인</button>
			</div>

		</div>
	</div>


	<script>
  /* =========================================================
     요소
  ========================================================= */
  const form =
    document.getElementById("signupForm");

  const memberId =
    document.getElementById("memberId");

  const email =
    document.getElementById("email");

  const password =
    document.getElementById("password");

  const passwordConfirm =
    document.getElementById("passwordConfirm");

  const nameInput =
    document.getElementById("name");

  const phone =
    document.getElementById("phone");

  const verification =
    document.getElementById("verification");

  const verifyButton =
    document.getElementById("verifyButton");

  const verificationMessage =
    document.getElementById("verificationMessage");

  const agreeAll =
    document.getElementById("agreeAll");

  const requiredChecks =
    document.querySelectorAll(".required-check");

  const emailMessage =
    document.getElementById("emailMessage");

  const passwordMessage =
    document.getElementById("passwordMessage");


  /* =========================================================
     모달
  ========================================================= */
  const modalBackdrop =
    document.getElementById("modalBackdrop");

  const modalTitle =
    document.getElementById("modalTitle");

  const modalBody =
    document.getElementById("modalBody");

  const modalConfirm =
    document.getElementById("modalConfirm");


  function showModal(title, message) {

    modalTitle.textContent = title;
    modalBody.textContent = message;

    modalBackdrop.classList.add("show");
  }


  function hideModal() {
    modalBackdrop.classList.remove("show");
  }


  modalConfirm.addEventListener(
    "click",
    hideModal
  );


  modalBackdrop.addEventListener(
    "click",
    function(event) {

      if (event.target === modalBackdrop) {
        hideModal();
      }
    }
  );


  /* =========================================================
     비밀번호 보기
  ========================================================= */
  function setupPasswordToggle(input, button) {

    button.addEventListener(
      "click",
      function() {

        if (input.type === "password") {

          input.type = "text";
          button.textContent = "숨기기";

        } else {

          input.type = "password";
          button.textContent = "보기";
        }
      }
    );
  }


  setupPasswordToggle(
    password,
    document.getElementById("passwordToggle")
  );

  setupPasswordToggle(
    passwordConfirm,
    document.getElementById("passwordConfirmToggle")
  );


  /* =========================================================
     휴대폰 번호 자동 하이픈
  ========================================================= */
  phone.addEventListener(
    "input",
    function() {

      let value =
        this.value
          .replace(/\D/g, "")
          .slice(0, 11);

      if (value.length <= 3) {

        this.value = value;

      } else if (value.length <= 7) {

        this.value =
          value.slice(0, 3)
          + "-"
          + value.slice(3);

      } else {

        this.value =
          value.slice(0, 3)
          + "-"
          + value.slice(3, 7)
          + "-"
          + value.slice(7);
      }
    }
  );


  /* =========================================================
     이메일 검사
  ========================================================= */
  function validateEmail() {

    if (!email.value) {

      email.classList.remove(
        "invalid",
        "valid"
      );

      emailMessage.classList.remove("show");

      return false;
    }

    const emailRegex =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

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


  email.addEventListener(
    "blur",
    validateEmail
  );


  /* =========================================================
     비밀번호 확인
  ========================================================= */
  function validatePasswordConfirm() {

    if (!passwordConfirm.value) {

      passwordConfirm.classList.remove(
        "invalid",
        "valid"
      );

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


  password.addEventListener(
    "input",
    function() {

      if (passwordConfirm.value) {
        validatePasswordConfirm();
      }
    }
  );


  passwordConfirm.addEventListener(
    "input",
    validatePasswordConfirm
  );


  /* =========================================================
     전체 약관 동의
  ========================================================= */
  const allAgreementChecks =
    document.querySelectorAll(
      ".agreement-list .check"
    );


  agreeAll.addEventListener(
    "change",
    function() {

      allAgreementChecks.forEach(
        function(check) {

          check.checked =
            agreeAll.checked;
        }
      );
    }
  );


  allAgreementChecks.forEach(
    function(check) {

      check.addEventListener(
        "change",
        function() {

          const allChecked =
            Array.from(
              allAgreementChecks
            ).every(
              item => item.checked
            );

          agreeAll.checked =
            allChecked;
        }
      );
    }
  );


  /* =========================================================
     인증번호
     - 현재는 실제 SMS를 발송하지 않는 데모
     - 인증번호 받기 클릭 후 6자리 숫자 입력 시 인증 완료
  ========================================================= */
  let verificationRequested = false;
  let verificationCompleted = false;


  verifyButton.addEventListener(
    "click",
    function() {

      const phoneNumber =
        phone.value.replace(/\D/g, "");

      if (phoneNumber.length < 10) {

        phone.classList.add("invalid");

        showModal(
          "휴대폰 번호 확인",
          "휴대폰 번호를 정확하게 입력해주세요."
        );

        phone.focus();

        return;
      }

      phone.classList.remove("invalid");

      verificationRequested = true;
      verificationCompleted = false;

      verification.value = "";
      verification.disabled = false;

      verifyButton.textContent =
        "인증번호 재전송";

      verificationMessage.textContent =
        "인증번호가 발송되었습니다. 데모에서는 임의의 6자리 숫자를 입력해주세요.";

      verificationMessage.className =
        "message success show";

      verification.focus();
    }
  );


  verification.addEventListener(
    "input",
    function() {

      this.value =
        this.value
          .replace(/\D/g, "")
          .slice(0, 6);

      if (!verificationRequested) {
        return;
      }

      if (this.value.length === 6) {

        verificationCompleted = true;

        this.classList.remove("invalid");
        this.classList.add("valid");

        verificationMessage.textContent =
          "인증이 완료되었습니다.";

        verificationMessage.className =
          "message success show";

        verifyButton.textContent =
          "인증완료";

      } else {

        verificationCompleted = false;

        this.classList.remove("valid");

        verificationMessage.textContent =
          "인증번호 6자리를 입력해주세요.";

        verificationMessage.className =
          "message error show";
      }
    }
  );


  /* =========================================================
     필수 입력 검사
  ========================================================= */
  function requiredField(element, message) {

    if (!element.value.trim()) {

      element.classList.add("invalid");

      showModal(
        "입력 내용을 확인해주세요",
        message
      );

      element.focus();

      return false;
    }

    element.classList.remove("invalid");

    return true;
  }


  /* =========================================================
     가입
  ========================================================= */
  form.addEventListener(
    "submit",
    function(event) {

      event.preventDefault();

      /* 아이디 */
      if (
        !requiredField(
          memberId,
          "아이디를 입력해주세요."
        )
      ) {
        return;
      }

      /* 이메일 */
      if (!validateEmail()) {

        showModal(
          "입력 내용을 확인해주세요",
          "이메일 주소를 정확하게 입력해주세요."
        );

        email.focus();

        return;
      }

      /* 비밀번호 */
      if (!password.value) {

        password.classList.add("invalid");

        showModal(
          "입력 내용을 확인해주세요",
          "비밀번호를 입력해주세요."
        );

        password.focus();

        return;
      }

      if (password.value.length < 8) {

        password.classList.add("invalid");

        showModal(
          "입력 내용을 확인해주세요",
          "비밀번호는 8자 이상 입력해주세요."
        );

        password.focus();

        return;
      }

      /* 비밀번호 확인 */
      if (!validatePasswordConfirm()) {

        showModal(
          "입력 내용을 확인해주세요",
          "비밀번호가 일치하지 않습니다."
        );

        passwordConfirm.focus();

        return;
      }

      /* 이름 */
      if (
        !requiredField(
          nameInput,
          "이름을 입력해주세요."
        )
      ) {
        return;
      }

      /* 휴대폰 */
      const phoneNumber =
        phone.value.replace(/\D/g, "");

      if (phoneNumber.length < 10) {

        phone.classList.add("invalid");

        showModal(
          "입력 내용을 확인해주세요",
          "휴대폰 번호를 입력해주세요."
        );

        phone.focus();

        return;
      }

      /* 휴대폰 인증 */
      if (!verificationCompleted) {

        showModal(
          "본인인증",
          "휴대폰 인증을 완료해주세요."
        );

        verification.focus();

        return;
      }

      /* 필수 약관 */
      const allRequired =
        Array.from(
          requiredChecks
        ).every(
          checkbox => checkbox.checked
        );

      if (!allRequired) {

        showModal(
          "약관 동의",
          "필수 약관에 모두 동의해주세요."
        );

        return;
      }

      /*
       * 모든 프론트 검증 통과
       *
       * form.submit()은 위 submit 이벤트를 다시 발생시키지 않고
       * SignupServlet(/signup)의 doPost()로 바로 전송한다.
       */
      form.submit();
    }
  );


  /* =========================================================
     입력 시작 시 에러 표시 제거
  ========================================================= */
  [
    memberId,
    email,
    password,
    passwordConfirm,
    nameInput,
    phone,
    verification
  ].forEach(
    function(element) {

      element.addEventListener(
        "input",
        function() {

          if (this.value) {
            this.classList.remove("invalid");
          }
        }
      );
    }
  );
</script>

</body>

</html>