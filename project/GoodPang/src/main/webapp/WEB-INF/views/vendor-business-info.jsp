<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.goodpang.dto.SellerDTO" %>

    <%
    
    System.out.println("call vendor-business-info.jsp");
    %>
    
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_dashboard.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor-signup.css">
  <title>굿팡 판매자 정보관리</title>

  <style>
    /* vendor-signup.css는 원래 독립된 회원가입 화면용이라, 대시보드 레이아웃(topbar/sidebar) 안에
       들어올 때는 전체화면 카드 전제(.page/.vendor-signup)를 걷어내고 폭만 맞춰서 씀 */
    .business-info-card { max-width: 640px; margin-bottom: 16px; }
    .business-info-card .field-group:first-child { margin-top: 0; }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "businessInfo"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>

<%
    SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");
%>

    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">판매자 정보관리</h1>
          <p class="page-desc">입점 심사를 위해 사업장 정보, 정산계좌, 서류를 등록해주세요.</p>
        </div>

      </div>

      <% if (request.getAttribute("error") != null) { %>
        <p class="message error show"><%= request.getAttribute("error") %></p>
      <% } %>

      <section class="panel business-info-card">

        <form class="form" id="businessInfoForm" novalidate method="post" enctype="multipart/form-data"
          action="${pageContext.request.contextPath}/vendor/business-info">

          <!-- 사업장 주소 -->

          <section class="field-group">

            <h2 class="group-title">사업장 정보</h2>

            <div class="field">
              <label class="label" for="zipcode">우편번호</label>
              <div class="inline-row">
                <input class="input" id="zipcode" name="zipcode" type="text" placeholder="우편번호"
                  value="<%= loginSeller.getZipcode() != null ? loginSeller.getZipcode() : "" %>">
                <button class="check-button" id="zipcodeButton" type="button">우편번호 찾기</button>
              </div>
            </div>

            <div class="field">
              <label class="label" for="businessAddress">사업장 주소</label>
              <input class="input" id="businessAddress" name="businessAddress" type="text" placeholder="기본주소"
                value="<%= loginSeller.getBusinessAddress() != null ? loginSeller.getBusinessAddress() : "" %>">
            </div>

            <div class="field">
              <label class="label" for="businessDetailAddress">상세주소</label>
              <input class="input" id="businessDetailAddress" name="businessDetailAddress" type="text" placeholder="상세주소"
                value="<%= loginSeller.getBusinessDetailAddress() != null ? loginSeller.getBusinessDetailAddress() : "" %>">
            </div>

            <div class="field">
              <label class="label" for="mailOrderNo">통신판매업신고번호</label>
              <input class="input" id="mailOrderNo" name="mailOrderNo" type="text" placeholder="예) 2024-서울강남-00000"
                value="<%= loginSeller.getMailOrderNo() != null ? loginSeller.getMailOrderNo() : "" %>">
            </div>

            <div class="field">
              <label class="label" for="categoryNo">대표 판매 카테고리</label>
              <select class="input" id="categoryNo" name="categoryNo">
                <option value="">선택해주세요</option>
              </select>
            </div>

          </section>

          <!-- 정산계좌 -->

          <section class="field-group">

            <h2 class="group-title">정산계좌</h2>

            <div class="field">
              <label class="label" for="bankName">은행명</label>
              <input class="input" id="bankName" name="bankName" type="text" placeholder="은행명"
                value="<%= loginSeller.getBankName() != null ? loginSeller.getBankName() : "" %>">
            </div>

            <div class="field">
              <label class="label" for="accountNo">계좌번호</label>
              <input class="input" id="accountNo" name="accountNo" type="text" placeholder="-없이 입력"
                value="<%= loginSeller.getAccountNo() != null ? loginSeller.getAccountNo() : "" %>">
            </div>

            <div class="field">
              <label class="label" for="accountHolder">예금주명</label>
              <input class="input" id="accountHolder" name="accountHolder" type="text" placeholder="예금주명"
                value="<%= loginSeller.getAccountHolder() != null ? loginSeller.getAccountHolder() : "" %>">
            </div>

          </section>

          <!-- 서류첨부 -->

          <section class="field-group">

            <h2 class="group-title">서류첨부</h2>

            <p class="agreement-all-desc">
              이미지 파일(jpg, jpeg, png)만 첨부 가능하며, 파일당 최대 5MB까지 첨부할 수 있습니다.
            </p>

            <div class="field">
              <label class="label" for="businessCert">사업자등록증</label>
              <input class="input" id="businessCert" name="businessCert" type="file" accept=".jpg,.jpeg,.png">
<% if (loginSeller.getBusinessCertUrl() != null) { %>
              <p class="message">기존 첨부파일이 있습니다. 새로 첨부하지 않으면 기존 파일이 유지됩니다.</p>
<% } %>
            </div>

            <div class="field">
              <label class="label" for="mailOrderCert">통신판매신고증</label>
              <input class="input" id="mailOrderCert" name="mailOrderCert" type="file" accept=".jpg,.jpeg,.png">
<% if (loginSeller.getMailOrderCertUrl() != null) { %>
              <p class="message">기존 첨부파일이 있습니다. 새로 첨부하지 않으면 기존 파일이 유지됩니다.</p>
<% } %>
            </div>

          </section>

          <button class="submit" id="submitButton" type="submit">
            제출하기
          </button>

        </form>

      </section>

      <!-- 회원탈퇴 -->
      <section class="panel business-info-card">

        <section class="field-group">

          <h2 class="group-title">회원탈퇴</h2>

          <p class="agreement-all-desc">
            탈퇴하면 로그인이 즉시 차단되고, 등록하신 모든 상품이 고객 화면에서 사라집니다. 이 작업은 되돌릴 수 없습니다.
          </p>

          <form method="post" id="withdrawForm"
            action="${pageContext.request.contextPath}/vendor/withdraw"
            onsubmit="return confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.');">

            <div class="field">
              <label class="label" for="withdrawPassword">비밀번호 확인</label>
              <input class="input" id="withdrawPassword" name="password" type="password" placeholder="비밀번호를 입력해주세요">
            </div>

            <button class="submit" type="submit" style="background:#c0392b;">
              탈퇴하기
            </button>

          </form>

        </section>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>
  <script src="//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
  <script>

    document.getElementById("zipcodeButton").addEventListener("click", function () {

      new kakao.Postcode({

        oncomplete: function (data) {

          const addr = (data.userSelectedType === "R") ? data.roadAddress : data.jibunAddress;

          document.getElementById("zipcode").value = data.zonecode;
          document.getElementById("businessAddress").value = addr;
          document.getElementById("businessDetailAddress").focus();
        }

      }).open();
    });

    (function loadCategories() {

      fetch("${pageContext.request.contextPath}/category/getinfo?ctype=main")
        .then(function (res) { return res.json(); })
        .then(function (grouped) {

          const select = document.getElementById("categoryNo");
          const mainCategories = grouped["1"] || [];
          const selectedCategoryNo = "<%= loginSeller.getCategoryNo() != null ? loginSeller.getCategoryNo() : "" %>";

          mainCategories.forEach(function (category) {

            const option = document.createElement("option");
            option.value = category.categoryNo;
            option.textContent = category.categoryName;

            if (String(category.categoryNo) === selectedCategoryNo) {
              option.selected = true;
            }

            select.appendChild(option);
          });
        })
        .catch(function (err) {
          console.error("카테고리 목록을 불러오지 못했습니다.", err);
        });

    })();

  </script>

</body>

</html>
