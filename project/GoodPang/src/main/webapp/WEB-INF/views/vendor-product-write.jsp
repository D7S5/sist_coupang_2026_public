<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!DOCTYPE html>

  <html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_product_write.css">
    <title>굿팡 판매자 상품 등록</title>

  </head>

  <body>

    <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

    <% String sellerGrade = null; %>
    <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

    <% String menu = "productWrite"; %>
    <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


      <!-- 메인 -->
      <main class="main">

        <form id="productForm" novalidate>

          <div class="write-head">
            <h1 class="page-title">상품 등록 <span class="required-dot">•</span><span class="required-label">필수항목</span>
            </h1>
            <div class="write-head-actions">
              <button class="btn btn-outline" type="button">동영상 가이드</button>
              <button class="btn btn-primary" type="button" id="topSubmitButton">상품등록</button>
            </div>
          </div>


          <!-- 노출상품명 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>노출상품명 <span class="required-dot">•</span></h2>
            </div>
            <div class="block-body">

              <div class="field-row">
                <label class="field-label">브랜드 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <input class="input" id="brandInput" type="text" placeholder="상품에 표시된 브랜드 이름 (예: 나이키, 휠라)">
                  <label class="checkbox-item sub-checkbox">
                    <input type="checkbox" id="noBrandCheck">
                    브랜드 없음(또는 자체제작)
                  </label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">노출상품명 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <div class="input-counter">
                    <input class="input" id="displayNameInput" type="text" maxlength="100"
                      placeholder="상품 모델(해당 시) + 상품 유형 + 핵심 특징">
                    <span class="counter"><span id="displayNameCount">0</span> / 100</span>
                  </div>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">등록상품명(판매자관리용) <span class="required-dot">•</span></label>
                <div class="field-control">
                  <input class="input" id="internalNameInput" type="text" placeholder="판매자 내부 관리용 상품명 (고객에게 노출되지 않음)">
                </div>
              </div>

            </div>
          </section>


          <!-- 카테고리 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>카테고리 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">

              <div class="info-box">
                쿠팡에서 판매중인 상품 정보를 불러와 손쉽게 상품을 등록할 수 있습니다.<br>
                카탈로그 매칭은 내 상품의 검색 노출수와, 랭킹을 높이는데 보다 더 유리합니다. 상품을 매칭하고 아이템 위너로 만들어 보세요.
              </div>

              <div class="autocomplete">
                <div class="input-icon-wrap">
                  <svg class="icon">
                    <use href="#ic-search" />
                  </svg>
                  <input class="input" id="categorySearchInput" type="text" placeholder="예) 귤, 백팩, 공기청정기">
                </div>
                <ul class="autocomplete-list" id="categorySearchList" hidden></ul>
              </div>

              <p class="selected-category" id="selectedCategory"></p>

            </div>
          </section>


          <!-- 옵션 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>옵션 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">

              <div class="field-row">
                <label class="field-label">기본 상품가격(원) <span class="required-dot">•</span></label>
                <div class="field-control">
                  <input class="input" id="basePriceInput" type="number" min="0" placeholder="예: 5000">
                  <p class="field-hint">옵션별 판매가는 이 기본 상품가격에 더해지는 가감액입니다. (예: 기본가 5,000원 + 옵션가 -1,000원 = 최종가 4,000원)</p>
                </div>
              </div>

              <div class="tab-group" id="optionModeTabs">
                <button class="tab-btn active" type="button" data-mode="on">설정함</button>
                <button class="tab-btn" type="button" data-mode="off">설정 안 함</button>
              </div>

              <div id="optionModeOn">

                <div id="optionGroupEditor">
                  <div id="optionGroupList"></div>

                  <button class="btn btn-outline btn-sm" type="button" id="addOptionGroupButton">+ 옵션 그룹 추가 (최대 2개)</button>

                  <a href="#" class="text-link">옵션 구성을 제안하고 싶어요</a>
                </div>

                <p class="field-hint" id="noOptionHint" hidden>옵션 없이 이 상품가격·재고·이미지 그대로 단일 상품으로 등록됩니다.</p>

                <p class="option-count">옵션 목록 (총 <span id="optionCount">0</span>개) <span class="required-dot">•</span>
                </p>

                <div class="table-toolbar">
                  <button class="btn btn-outline btn-sm" type="button" id="optionDeleteButton">삭제</button>
                  <button class="btn btn-outline btn-sm" type="button" id="bulkPriceButton">판매가 일괄입력</button>
                  <button class="btn btn-outline btn-sm" type="button" id="bulkStockButton">재고수량 일괄입력</button>
                </div>

                <div class="table-scroll">
                  <table class="data-table" id="optionTable">
                    <thead>
                      <tr>
                        <th class="col-check"><input type="checkbox" id="optionCheckAll"></th>
                        <th>옵션명</th>
                        <th>정상가(원)</th>
                        <th>판매가(원) <span class="required-dot">•</span> <span class="help-q" title="기본 상품가격에 더해지는 가감액입니다. 0원이면 기본가와 동일, 음수도 입력 가능합니다.">?</span></th>
                        <th>판매자 자동가격조정 <span class="help-q" title="경쟁 판매자들과 비교해서 내 상품 가격을 자동으로 낮춰(또는 조정해) '아이템위너'(대표 판매자로 노출되는 자리)를 계속 유지하게 해주는 기능">?</span></th>
                        <th>재고수량 <span class="required-dot">•</span></th>
                        <th>판매자상품코드</th>
                        <th>모델번호</th>
                        <th>상품바코드</th>
                      </tr>
                    </thead>
                    <tbody id="optionTableBody">
                      <tr id="optionEmptyRow">
                        <td colspan="9" class="empty-row">데이터가 존재하지 않습니다.</td>
                      </tr>
                    </tbody>
                  </table>
                </div>

              </div>

            </div>
          </section>


          <!-- 상품이미지 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>상품이미지 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="imageBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="imageBlockBody">

              <div class="tab-group" id="imageModeTabs">
                <button class="tab-btn" type="button" data-mode="basic">기본 등록</button>
                <button class="tab-btn active" type="button" data-mode="option">옵션별 등록</button>
              </div>

              <p class="hint">이미지 권장 크기 : 1,000px x 1,000px (최소 500px 이상) / 10MB 이하의 JPG, PNG 파일</p>

              <div class="table-toolbar">
                <button class="btn btn-outline btn-sm" type="button" id="bulkMainImageButton">대표이미지 일괄등록</button>
                <button class="btn btn-outline btn-sm" type="button" id="bulkExtraImageButton">추가이미지 일괄등록</button>
                <button class="btn btn-outline btn-sm" type="button">이미지 URL주소로 일괄등록</button>
                <button class="btn btn-outline btn-sm" type="button">추가이미지 일괄삭제</button>
              </div>

              <div class="table-scroll">
                <table class="data-table" id="imageTable">
                  <thead>
                    <tr>
                      <th class="col-check"><input type="checkbox" id="imageCheckAll"></th>
                      <th>옵션명</th>
                      <th>대표이미지 <span class="required-dot">•</span></th>
                      <th>추가이미지 (최대 9장)</th>
                      <th>이미지 URL주소로 등록</th>
                    </tr>
                  </thead>
                  <tbody id="imageTableBody"></tbody>
                </table>
              </div>

              <input type="file" id="bulkMainImageFileInput" accept="image/jpeg,image/png" hidden>
              <input type="file" id="bulkExtraImageFileInput" accept="image/jpeg,image/png" multiple hidden>

              <input type="file" id="imageMainFileInput" accept="image/jpeg,image/png" hidden>
              <input type="file" id="imageExtraFileInput" accept="image/jpeg,image/png" multiple hidden>

            </div>
          </section>


          <!-- 상세설명 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>상세설명 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="descBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="descBlockBody">

              <div class="tab-group" id="descModeTabs">
                <button class="tab-btn active" type="button" data-mode="basic">기본 등록</button>
                <button class="tab-btn" type="button" data-mode="option">옵션별 등록</button>
              </div>

              <div class="tab-group" id="descTypeTabs">
                <button class="tab-btn active" type="button" data-type="image">이미지 업로드</button>
                <button class="tab-btn" type="button" data-type="editor">에디터 작성</button>
                <button class="tab-btn" type="button" data-type="html">HTML 작성</button>
              </div>

              <div class="desc-empty" id="descEmpty">
                <svg class="icon empty-icon">
                  <use href="#ic-image" />
                </svg>
                <p>등록된 이미지가 없습니다</p>
                <button class="btn btn-primary" type="button" id="descRegisterButton">이미지 등록</button>
              </div>

              <div class="desc-preview" id="descPreview" hidden>
                <div class="desc-preview-list" id="descPreviewList"></div>
                <button class="btn btn-outline btn-sm" type="button" id="descManageButton">이미지 관리 (<span id="descImageCount">0</span>장)</button>
              </div>

              <p class="hint">이미지 권장 크기 : 780px x 5,000px / 10MB 이하의 JPG, PNG 파일</p>

              <input type="file" id="descImageFileInput" accept="image/jpeg,image/png" multiple hidden>

            </div>
          </section>



          <!-- 구비서류 -->
          <section class="panel form-block collapsed" id="docsBlock">
            <div class="block-head">
              <h2>구비서류 <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="docsBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="docsBlockBody" hidden>
              <p class="hint">선택하신 카테고리에 별도로 필요한 구비서류가 없습니다.</p>
            </div>
          </section>


          <!-- 배송 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>배송 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="shippingBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="shippingBlockBody">

              <div class="field-row">
                <label class="field-label">출고지 우편번호 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <input class="input" id="shippingZipcodeInput" type="text" placeholder="우편번호"
                         value="${sessionScope.loginSeller.zipcode}">
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">출고지 주소 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <input class="input" id="shippingAddressInput" type="text" placeholder="기본주소"
                         value="${sessionScope.loginSeller.businessAddress}">
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">출고지 상세주소</label>
                <div class="field-control">
                  <input class="input" id="shippingDetailAddressInput" type="text" placeholder="상세주소"
                         value="${sessionScope.loginSeller.businessDetailAddress}">
                  <p class="side-note">기본값은 사업자 정보에 등록된 사업장 주소이며, 이 상품만 다른 출고지를 쓰려면 직접 수정해주세요.</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">제주/도서산간 배송여부 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="jejuShipping" value="Y" checked>가능</label>
                  <label class="radio-item"><input type="radio" name="jejuShipping" value="N">불가능</label>
                  <p class="side-note">주소록/배송관리 메뉴에서 택배사와 도서산간 추가배송비를 설정할 수 있습니다.</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">택배사 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <select class="input select" id="courierSelect">
                    <option value="">선택하세요</option>
                    <option>롯데택배 (쿠팡 제휴 택배사)</option>
                    <option>한진택배 (쿠팡 제휴 택배사)</option>
                    <option selected>로젠택배</option>
                    <option>우체국</option>
                    <option>CJ대한통운</option>
                    <option>경동택배</option>
                    <option>일양택배</option>
                    <option>천일택배</option>
                    <option>대신택배</option>
                  </select>
                  <p class="side-note">제주/도서산간지역 배송 시 출고지에 등록된 택배사만 선택할 수 있습니다.</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">배송방법 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <select class="input select" id="deliveryMethodSelect">
                    <option value="">선택하세요</option>
                    <option selected>일반배송</option>
                    <option>신선냉동</option>
                    <option>주문제작</option>
                    <option>구매대행</option>
                    <option>설치배송 또는 판매자 직접전달</option>
                  </select>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">묶음배송 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="bundleShipping" value="Y" checked>가능</label>
                  <label class="radio-item"><input type="radio" name="bundleShipping" value="N">불가능</label>
                  <p class="side-note">출고 정보가 같은 상품만 묶음배송할 수 있습니다. (착불배송 선택 불가)</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">배송비 종류 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <select class="input select" id="shippingFeeTypeSelect">
                    <option selected>무료배송</option>
                    <option>유료배송</option>
                    <option>조건부무료배송</option>
                    <option>9,800원 이상 무료배송</option>
                    <option>19,800원 이상 무료배송</option>
                    <option>30,000원 이상 무료배송</option>
                    <option>착불배송</option>
                  </select>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">출고 소요일 <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="leadTime" value="기본 입력" checked>기본 입력</label>
                  <label class="radio-item"><input type="radio" name="leadTime" value="구매 옵션별로 입력">구매 옵션별로 입력</label>
                </div>
              </div>

              <div class="field-row" id="dayLeadTimeRow">
                <label class="field-label"></label>
                <div class="field-control lead-time-control">
                  <input class="input lead-time-input" id="leadTimeDaysInput" type="number" value="1" min="0">
                  <span>일</span>
                  <label class="checkbox-item">
                    <input type="checkbox" id="sameDayShipCheck" checked>
                    당일출고
                  </label>
                </div>
              </div>

              <div class="field-row" id="sameDayCutoffRow">
                <label class="field-label">당일출고 마감</label>
                <div class="field-control">
                  <select class="input select cutoff-select" id="cutoffTimeSelect">
                    <option>10:00</option>
                    <option>11:00</option>
                    <option selected>12:00</option>
                    <option>13:00</option>
                    <option>14:00</option>
                    <option>15:00</option>
                    <option>16:00</option>
                    <option>17:00</option>
                    <option>18:00</option>
                  </select>
                  <p class="side-note">당일출고가 지켜지지 않는 경우, 판매자 점수 하락 등 페널티가 발생할 수 있습니다.</p>
                </div>
              </div>

            </div>
          </section>


          <!-- 하단 버튼 -->
          <div class="write-foot">
            <button class="btn btn-primary btn-lg" type="submit">판매요청</button>
          </div>

        </form>

      </main>

    </div>


    <!-- 상세설명 이미지 등록 모달 -->
    <div class="modal-backdrop" id="descModalBackdrop">
      <div class="modal modal-lg">

        <div class="modal-head">
          <h2>이미지 등록</h2>
          <button class="icon-button" type="button" id="descModalClose">
            <svg class="icon">
              <use href="#ic-close" />
            </svg>
          </button>
        </div>

        <div class="modal-body">

          <div class="tab-group" id="descModalTabs">
            <button class="tab-btn active" type="button" data-type="image">이미지 추가</button>
            <button class="tab-btn" type="button" data-type="html">텍스트(HTML) 추가</button>
          </div>

          <p class="hint">컨텐츠의 순서를 바꾸시려면 왼쪽의 ⁝⁝ 를 원하는 위치로 끌어다 놓으세요.(드래그 앤 드롭)</p>

          <div class="modal-toolbar">
            <button class="btn btn-outline btn-sm" type="button">전체 다운로드</button>
            <button class="btn btn-outline btn-sm" type="button" id="descClearAllButton">전체 삭제</button>
            <button class="btn btn-outline btn-sm" type="button">실제크기</button>
            <button class="btn btn-outline btn-sm active" type="button">축소크기</button>
          </div>

          <div class="desc-image-list" id="descImageList"></div>

          <button class="add-image-slot" type="button" id="descAddImageButton">
            <svg class="icon">
              <use href="#ic-plus" />
            </svg>
            이미지 추가
          </button>

        </div>

        <div class="modal-foot">
          <button class="btn btn-outline" type="button" id="descModalCancel">취소</button>
          <button class="btn btn-primary" type="button" id="descModalSave">저장</button>
        </div>

      </div>
    </div>


    <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>
    <script>

      /* =========================================================
         섹션 접기/펼치기 (우측 상단 ^ 버튼)
         (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리)
      ========================================================= */

      document.querySelectorAll(".collapse-toggle").forEach(function (button) {
        button.addEventListener("click", function () {
          const body = document.getElementById(button.dataset.target);
          const block = button.closest(".form-block");
          const willHide = !body.hidden;

          body.hidden = willHide;
          block.classList.toggle("collapsed", willHide);
        });
      });

      document.querySelectorAll(".collapse-line").forEach(function (button) {
        button.addEventListener("click", function () {
          const body = document.getElementById(button.dataset.target);
          body.hidden = !body.hidden;
          button.classList.toggle("open", !body.hidden);
        });
      });


      /* =========================================================
         탭 공통 (data-tab / data-mode / data-type 버튼 그룹)
      ========================================================= */

      function setupTabGroup(groupId, onChange) {
        const group = document.getElementById(groupId);
        if (!group) return;

        group.addEventListener("click", function (event) {
          const button = event.target.closest(".tab-btn");
          if (!button) return;

          group.querySelectorAll(".tab-btn").forEach((btn) => btn.classList.remove("active"));
          button.classList.add("active");

          if (onChange) onChange(button);
        });
      }

      setupTabGroup("optionModeTabs", function (button) {
        optionMode = button.dataset.mode;
        document.getElementById("optionGroupEditor").hidden = optionMode !== "on";
        document.getElementById("noOptionHint").hidden = optionMode === "on";
        renderOptionTable();
      });

      setupTabGroup("imageModeTabs");
      setupTabGroup("descModeTabs");
      setupTabGroup("descTypeTabs");
      setupTabGroup("descModalTabs");


      /* =========================================================
         노출상품명 글자수
      ========================================================= */

      const displayNameInput = document.getElementById("displayNameInput");
      const displayNameCount = document.getElementById("displayNameCount");

      displayNameInput.addEventListener("input", function () {
        displayNameCount.textContent = displayNameInput.value.length;
      });


      /* =========================================================
         브랜드 없음 체크 시 브랜드 입력 비활성화
      ========================================================= */

      const brandInput = document.getElementById("brandInput");
      const noBrandCheck = document.getElementById("noBrandCheck");
      const basePriceInput = document.getElementById("basePriceInput");

      noBrandCheck.addEventListener("change", function () {
        brandInput.disabled = noBrandCheck.checked;
        if (noBrandCheck.checked) brandInput.value = "";
      });


      /* =========================================================
         카테고리 — 검색어로 최종(리프) 카테고리 후보 조회 → 선택
      ========================================================= */

      const categorySearchInput = document.getElementById("categorySearchInput");
      const categorySearchList = document.getElementById("categorySearchList");
      const selectedCategory = document.getElementById("selectedCategory");

      let selectedCategoryNo = null;
      let categorySearchTimer = null;

      function searchCategory() {
        const keyword = categorySearchInput.value.trim();

        if (!keyword) {
          categorySearchList.hidden = true;
          categorySearchList.innerHTML = "";
          return;
        }

        fetch("${pageContext.request.contextPath}/category/getinfo?keyword=" + encodeURIComponent(keyword))
          .then(function (res) { return res.json(); })
          .then(function (categories) {

            if (!Array.isArray(categories) || categories.length === 0) {
              categorySearchList.innerHTML = "<li class='empty'>검색 결과가 없습니다.</li>";
              categorySearchList.hidden = false;
              return;
            }

            categorySearchList.innerHTML = categories
              .map(function (category) {
                var displayText = category.categoryPath || category.categoryName;
                return "<li data-category-no='" + category.categoryNo + "' data-category-name='" + category.categoryName + "'>" + displayText + "</li>";
              })
              .join("");
            categorySearchList.hidden = false;
          })
          .catch(function (err) {
            console.error("카테고리 검색에 실패했습니다.", err);
            categorySearchList.innerHTML = "<li class='empty'>검색 중 오류가 발생했습니다.</li>";
            categorySearchList.hidden = false;
          });
      }

      categorySearchInput.addEventListener("input", function () {
        clearTimeout(categorySearchTimer);
        categorySearchTimer = setTimeout(searchCategory, 300);
      });

      categorySearchList.addEventListener("click", function (event) {
        const item = event.target.closest("li[data-category-no]");
        if (!item) return;

        selectedCategoryNo = item.dataset.categoryNo;
        selectedCategory.textContent = "선택된 카테고리 : " + item.textContent;
        categorySearchList.hidden = true;
        categorySearchInput.value = item.dataset.categoryName;
      });


      /* =========================================================
         옵션 — 판매자가 옵션명을 직접 정하는 옵션 그룹 (최대 2개) → 조합으로 옵션 목록 테이블 생성
      ========================================================= */

      const MAX_OPTION_GROUPS = 2;

      let optionMode = "on"; // "on" = 옵션 설정함, "off" = 설정 안 함(단일 상품)

      const optionGroups = [
        { name: "사이즈", values: [] },
        { name: "색상", values: [] }
      ];

      function renderChips(listElement, values, onRemove) {
        listElement.innerHTML = values
          .map(
            (value, index) =>
              '<span class="chip" data-index="' + index + '">' + value +
              '<button type="button" class="chip-remove">&times;</button></span>'
          )
          .join("");

        listElement.querySelectorAll(".chip-remove").forEach(function (button) {
          button.addEventListener("click", function () {
            const index = Number(button.parentElement.dataset.index);
            onRemove(index);
          });
        });
      }

      function renderOptionGroups() {
        const container = document.getElementById("optionGroupList");

        container.innerHTML = optionGroups
          .map(function (group, index) {
            const removeButton = optionGroups.length > 1
              ? '<button class="btn btn-outline btn-sm option-group-remove" type="button" data-group="' + index + '">그룹 삭제</button>'
              : "";

            return (
              '<div class="field-row option-group">' +
              '<label class="field-label option-group-label">' +
              '<input class="input input-sm option-group-name" type="text" placeholder="옵션명 (예: 사이즈)" ' +
              'value="' + group.name.replace(/"/g, "&quot;") + '" data-group="' + index + '">' +
              ' <span class="required-dot">•</span>' +
              "</label>" +
              '<div class="field-control">' +
              '<div class="option-add-row">' +
              '<input class="input option-value-input" type="text" placeholder="옵션값 입력" data-group="' + index + '">' +
              '<button class="btn btn-primary option-value-add" type="button" data-group="' + index + '">추가</button>' +
              removeButton +
              '<div class="chip-list option-value-chips" data-group="' + index + '"></div>' +
              "</div>" +
              '<p class="hint">예: S, Medium, Free, 대 / 화이트, 블랙, 레드 등</p>' +
              "</div>" +
              "</div>"
            );
          })
          .join("");

        optionGroups.forEach(function (group, index) {
          renderChips(
            container.querySelector('.option-value-chips[data-group="' + index + '"]'),
            group.values,
            function (valueIndex) {
              group.values.splice(valueIndex, 1);
              renderOptionGroups();
              renderOptionTable();
            }
          );
        });

        document.getElementById("addOptionGroupButton").disabled = optionGroups.length >= MAX_OPTION_GROUPS;
      }

      function cartesianCombine(groups) {
        return groups.reduce(
          function (acc, group) {
            const next = [];
            acc.forEach(function (partial) {
              group.values.forEach(function (value) {
                next.push(partial.concat([{ type: group.name, value: value }]));
              });
            });
            return next;
          },
          [[]]
        );
      }

      let currentCombinations = [];

      function renderOptionTable() {
        const tbody = document.getElementById("optionTableBody");
        const emptyRow = document.getElementById("optionEmptyRow");

        let combinations;

        if (optionMode === "off") {
          // 옵션 설정 안 함 — PRODUCT_OPTION에는 옵션값 없이 단일 행 하나만 만들어서
          // 기존 옵션별 가격/재고/이미지 등록 UI·서버 로직을 그대로 재사용한다.
          combinations = [{ label: "단일 상품 (옵션 없음)", option1: null, option2: null, option3: null }];
        } else {
          const activeGroups = optionGroups.filter((group) => group.values.length > 0);

          combinations = activeGroups.length === 0
            ? []
            : cartesianCombine(activeGroups).map(function (parts) {
                return {
                  label: parts.map((part) => part.value).join(", "),
                  option1: parts[0] || null,
                  option2: parts[1] || null,
                  option3: parts[2] || null
                };
              });
        }

        currentCombinations = combinations;

        document.getElementById("optionCount").textContent = combinations.length;

        if (combinations.length === 0) {
          tbody.innerHTML = "";
          tbody.appendChild(emptyRow);
          renderImageTable(combinations);
          return;
        }

        tbody.innerHTML = combinations
          .map(
            (combo, index) =>
              '<tr data-row="' + index + '">' +
              '<td class="col-check"><input type="checkbox"></td>' +
              "<td>" + combo.label + "</td>" +
              '<td><input class="input input-sm" type="number" value="0"></td>' +
              '<td><input class="input input-sm" type="number" value="0"></td>' +
              '<td class="center"><label class="switch"><input type="checkbox"><span class="slider"></span></label></td>' +
              '<td><input class="input input-sm" type="number" value="0"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              "</tr>"
          )
          .join("");

        renderImageTable(combinations);
      }

      /* =========================================================
         상품이미지 — 옵션별 대표이미지 1장 / 추가이미지 최대 9장 업로드·미리보기
      ========================================================= */

      const optionImages = [];
      let activeImageRow = null;

      const imageMainFileInput = document.getElementById("imageMainFileInput");
      const imageExtraFileInput = document.getElementById("imageExtraFileInput");

      const MAX_EXTRA_IMAGES = 9;
      const MAX_IMAGE_SIZE = 10 * 1024 * 1024;

      function isValidImageFile(file) {
        if (file.type !== "image/jpeg" && file.type !== "image/png") {
          alert(file.name + " : JPG, PNG 파일만 등록할 수 있습니다.");
          return false;
        }
        if (file.size > MAX_IMAGE_SIZE) {
          alert(file.name + " : 10MB 이하 파일만 등록할 수 있습니다.");
          return false;
        }
        return true;
      }

      function renderImageSlot(row) {
        const state = optionImages[row];
        if (!state) return;

        const mainSlot = document.querySelector('.main-slot[data-row="' + row + '"]');
        if (mainSlot) {
          mainSlot.classList.toggle("filled", !!state.main);
          mainSlot.innerHTML = state.main
            ? '<img src="' + state.main.url + '" alt="">' +
              '<button type="button" class="slot-remove" data-row="' + row + '" data-slot="main">&times;</button>'
            : '<svg class="icon"><use href="#ic-plus"/></svg>';
        }

        const group = document.querySelector('.image-slot-group[data-row="' + row + '"]');
        if (group) {
          const thumbs = state.extra
            .map(function (item, index) {
              return '<span class="image-slot filled extra-slot">' +
                '<img src="' + item.url + '" alt="">' +
                '<button type="button" class="slot-remove" data-row="' + row + '" data-slot="extra" data-index="' + index + '">&times;</button>' +
                '</span>';
            })
            .join("");

          const addButton = state.extra.length < MAX_EXTRA_IMAGES
            ? '<button type="button" class="image-slot add-extra-slot" data-row="' + row + '"><svg class="icon"><use href="#ic-plus"/></svg></button>'
            : "";

          group.innerHTML = thumbs + addButton;
        }
      }

      function renderImageTable(combinations) {
        const tbody = document.getElementById("imageTableBody");

        optionImages.length = 0;
        combinations.forEach(function () {
          optionImages.push({ main: null, extra: [] });
        });

        tbody.innerHTML = combinations
          .map(
            (combo, index) =>
              '<tr data-row="' + index + '">' +
              '<td class="col-check"><input type="checkbox" class="image-row-check" data-row="' + index + '"></td>' +
              "<td>" + combo.label + "</td>" +
              '<td><span class="image-slot main-slot" data-row="' + index + '"><svg class="icon"><use href="#ic-plus"/></svg></span></td>' +
              '<td><div class="image-slot-group" data-row="' + index + '">' +
              '<button type="button" class="image-slot add-extra-slot" data-row="' + index + '"><svg class="icon"><use href="#ic-plus"/></svg></button>' +
              "</div></td>" +
              '<td><button type="button" class="btn btn-outline btn-xs">이미지 URL 입력</button></td>' +
              "</tr>"
          )
          .join("");
      }

      document.getElementById("imageTableBody").addEventListener("click", function (event) {

        const removeButton = event.target.closest(".slot-remove");
        if (removeButton) {
          event.stopPropagation();

          const row = Number(removeButton.dataset.row);
          const slot = removeButton.dataset.slot;

          if (slot === "main") {
            URL.revokeObjectURL(optionImages[row].main.url);
            optionImages[row].main = null;
          } else {
            const index = Number(removeButton.dataset.index);
            URL.revokeObjectURL(optionImages[row].extra[index].url);
            optionImages[row].extra.splice(index, 1);
          }

          renderImageSlot(row);
          return;
        }

        const mainSlot = event.target.closest(".main-slot");
        if (mainSlot) {
          activeImageRow = Number(mainSlot.dataset.row);
          imageMainFileInput.click();
          return;
        }

        const addExtraSlot = event.target.closest(".add-extra-slot");
        if (addExtraSlot) {
          activeImageRow = Number(addExtraSlot.dataset.row);
          imageExtraFileInput.click();
        }
      });

      imageMainFileInput.addEventListener("change", function () {
        const file = imageMainFileInput.files[0];
        imageMainFileInput.value = "";

        if (!file || activeImageRow === null || !isValidImageFile(file)) return;

        const state = optionImages[activeImageRow];
        if (state.main) URL.revokeObjectURL(state.main.url);
        state.main = { file: file, url: URL.createObjectURL(file) };

        renderImageSlot(activeImageRow);
      });

      imageExtraFileInput.addEventListener("change", function () {
        const files = Array.from(imageExtraFileInput.files);
        imageExtraFileInput.value = "";

        if (activeImageRow === null) return;

        const state = optionImages[activeImageRow];
        const remaining = MAX_EXTRA_IMAGES - state.extra.length;

        files.slice(0, remaining).forEach(function (file) {
          if (!isValidImageFile(file)) return;
          state.extra.push({ file: file, url: URL.createObjectURL(file) });
        });

        if (files.length > remaining) {
          alert("추가이미지는 최대 " + MAX_EXTRA_IMAGES + "장까지 등록할 수 있습니다.");
        }

        renderImageSlot(activeImageRow);
      });


      /* =========================================================
         상품이미지 — 체크한 옵션 행에 대표/추가이미지 일괄 적용
      ========================================================= */

      function getCheckedImageRows() {
        return Array.from(document.querySelectorAll(".image-row-check:checked")).map(
          (checkbox) => Number(checkbox.dataset.row)
        );
      }

      document.getElementById("imageCheckAll").addEventListener("change", function () {
        document.querySelectorAll(".image-row-check").forEach(
          (checkbox) => (checkbox.checked = this.checked)
        );
      });

      const bulkMainImageFileInput = document.getElementById("bulkMainImageFileInput");
      const bulkExtraImageFileInput = document.getElementById("bulkExtraImageFileInput");

      document.getElementById("bulkMainImageButton").addEventListener("click", function () {
        if (getCheckedImageRows().length === 0) {
          alert("대표이미지를 적용할 옵션을 먼저 체크해주세요.");
          return;
        }
        bulkMainImageFileInput.click();
      });

      document.getElementById("bulkExtraImageButton").addEventListener("click", function () {
        if (getCheckedImageRows().length === 0) {
          alert("추가이미지를 적용할 옵션을 먼저 체크해주세요.");
          return;
        }
        bulkExtraImageFileInput.click();
      });

      bulkMainImageFileInput.addEventListener("change", function () {
        const file = bulkMainImageFileInput.files[0];
        bulkMainImageFileInput.value = "";

        if (!file || !isValidImageFile(file)) return;

        getCheckedImageRows().forEach(function (row) {
          const state = optionImages[row];
          if (!state) return;

          if (state.main) URL.revokeObjectURL(state.main.url);
          state.main = { file: file, url: URL.createObjectURL(file) };
          renderImageSlot(row);
        });
      });

      bulkExtraImageFileInput.addEventListener("change", function () {
        const files = Array.from(bulkExtraImageFileInput.files).filter(isValidImageFile);
        bulkExtraImageFileInput.value = "";

        if (files.length === 0) return;

        let anyRowFull = false;

        getCheckedImageRows().forEach(function (row) {
          const state = optionImages[row];
          if (!state) return;

          const remaining = MAX_EXTRA_IMAGES - state.extra.length;
          if (files.length > remaining) anyRowFull = true;

          files.slice(0, remaining).forEach(function (file) {
            state.extra.push({ file: file, url: URL.createObjectURL(file) });
          });
          renderImageSlot(row);
        });

        if (anyRowFull) {
          alert("추가이미지는 옵션당 최대 " + MAX_EXTRA_IMAGES + "장까지 등록할 수 있습니다. 일부 옵션은 다 채워지지 않았을 수 있습니다.");
        }
      });

      function addOptionValue(groupIndex) {
        const input = document.querySelector('.option-value-input[data-group="' + groupIndex + '"]');
        const value = input.value.trim();
        const group = optionGroups[groupIndex];

        if (!value || group.values.includes(value)) return;

        group.values.push(value);
        renderOptionGroups();
        renderOptionTable();
      }

      document.getElementById("optionGroupList").addEventListener("input", function (event) {
        if (!event.target.classList.contains("option-group-name")) return;

        const index = Number(event.target.dataset.group);
        optionGroups[index].name = event.target.value;
        renderOptionTable();
      });

      document.getElementById("optionGroupList").addEventListener("keydown", function (event) {
        if (event.key !== "Enter" || !event.target.classList.contains("option-value-input")) return;
        event.preventDefault();
        addOptionValue(Number(event.target.dataset.group));
      });

      document.getElementById("optionGroupList").addEventListener("click", function (event) {

        const addButton = event.target.closest(".option-value-add");
        if (addButton) {
          addOptionValue(Number(addButton.dataset.group));
          return;
        }

        const removeButton = event.target.closest(".option-group-remove");
        if (removeButton) {
          optionGroups.splice(Number(removeButton.dataset.group), 1);
          renderOptionGroups();
          renderOptionTable();
        }
      });

      document.getElementById("addOptionGroupButton").addEventListener("click", function () {
        if (optionGroups.length >= MAX_OPTION_GROUPS) return;
        optionGroups.push({ name: "", values: [] });
        renderOptionGroups();
      });

      renderOptionGroups();
      renderOptionTable();

      document.getElementById("optionDeleteButton").addEventListener("click", function () {
        document.querySelectorAll("#optionTableBody tr").forEach(function (row) {
          const checkbox = row.querySelector(".col-check input");
          if (checkbox && checkbox.checked) row.remove();
        });
      });

      document.getElementById("bulkPriceButton").addEventListener("click", function () {
        const value = prompt("일괄 적용할 판매가를 입력하세요.", "10000");
        if (value === null) return;
        document.querySelectorAll("#optionTableBody td:nth-child(4) input").forEach(function (input) {
          input.value = value;
        });
      });

      document.getElementById("bulkStockButton").addEventListener("click", function () {
        const value = prompt("일괄 적용할 재고수량을 입력하세요.", "100");
        if (value === null) return;
        document.querySelectorAll("#optionTableBody td:nth-child(6) input").forEach(function (input) {
          input.value = value;
        });
      });

      document.getElementById("optionCheckAll").addEventListener("change", function () {
        document.querySelectorAll("#optionTableBody .col-check input").forEach(
          (checkbox) => (checkbox.checked = this.checked)
        );
      });


      /* =========================================================
         배송 — 출고 소요일 / 당일출고
      ========================================================= */

      document.getElementById("sameDayShipCheck").addEventListener("change", function () {
        document.getElementById("sameDayCutoffRow").hidden = !this.checked;
      });


      /* =========================================================
         상세설명 이미지 등록 모달
      ========================================================= */

      const descImages = [];
      const descModalBackdrop = document.getElementById("descModalBackdrop");
      const descImageFileInput = document.getElementById("descImageFileInput");

      let descEditIndex = null;

      function renderDescImageList() {
        const list = document.getElementById("descImageList");

        if (descImages.length === 0) {
          list.innerHTML = "";
          return;
        }

        list.innerHTML = descImages
          .map(
            (item, index) =>
              '<div class="desc-image-row" data-index="' + index + '">' +
              '<span class="drag-handle">⁝⁝</span>' +
              '<div class="desc-image-thumb"><img src="' + item.url + '" alt=""></div>' +
              '<div class="desc-image-actions">' +
              '<button type="button" class="btn btn-outline btn-xs desc-edit">수정</button>' +
              '<button type="button" class="btn btn-outline btn-xs desc-remove">삭제</button>' +
              "</div></div>"
          )
          .join("");

        list.querySelectorAll(".desc-edit").forEach(function (button) {
          button.addEventListener("click", function () {
            descEditIndex = Number(button.closest(".desc-image-row").dataset.index);
            descImageFileInput.multiple = false;
            descImageFileInput.click();
          });
        });

        list.querySelectorAll(".desc-remove").forEach(function (button) {
          button.addEventListener("click", function () {
            const index = Number(button.closest(".desc-image-row").dataset.index);
            URL.revokeObjectURL(descImages[index].url);
            descImages.splice(index, 1);
            renderDescImageList();
          });
        });
      }

      function renderDescPreview() {
        const hasImages = descImages.length > 0;

        document.getElementById("descEmpty").hidden = hasImages;
        document.getElementById("descPreview").hidden = !hasImages;
        document.getElementById("descImageCount").textContent = descImages.length;

        document.getElementById("descPreviewList").innerHTML = descImages
          .map((item) => '<div class="desc-image-thumb"><img src="' + item.url + '" alt=""></div>')
          .join("");
      }

      document.getElementById("descRegisterButton").addEventListener("click", function () {
        descModalBackdrop.classList.add("show");
      });

      document.getElementById("descManageButton").addEventListener("click", function () {
        descModalBackdrop.classList.add("show");
      });

      document.getElementById("descAddImageButton").addEventListener("click", function () {
        descEditIndex = null;
        descImageFileInput.multiple = true;
        descImageFileInput.click();
      });

      descImageFileInput.addEventListener("change", function () {
        const files = Array.from(descImageFileInput.files).filter(isValidImageFile);
        descImageFileInput.value = "";

        if (files.length === 0) return;

        if (descEditIndex !== null) {
          URL.revokeObjectURL(descImages[descEditIndex].url);
          descImages[descEditIndex] = { file: files[0], url: URL.createObjectURL(files[0]) };
        } else {
          files.forEach(function (file) {
            descImages.push({ file: file, url: URL.createObjectURL(file) });
          });
        }

        renderDescImageList();
      });

      document.getElementById("descClearAllButton").addEventListener("click", function () {
        descImages.forEach((item) => URL.revokeObjectURL(item.url));
        descImages.length = 0;
        renderDescImageList();
      });

      function closeDescModal() {
        descModalBackdrop.classList.remove("show");
      }

      document.getElementById("descModalClose").addEventListener("click", closeDescModal);
      document.getElementById("descModalCancel").addEventListener("click", closeDescModal);

      document.getElementById("descModalSave").addEventListener("click", function () {
        renderDescPreview();
        closeDescModal();
      });

      descModalBackdrop.addEventListener("click", function (event) {
        if (event.target === descModalBackdrop) closeDescModal();
      });


      /* =========================================================
         폼 제출 — 옵션별 대표/추가이미지까지 포함해서 서버에 실제로 저장
      ========================================================= */

      function collectOptionRows() {
        return Array.from(document.querySelectorAll("#optionTableBody tr[data-row]")).map(function (row) {
          const rowIndex = Number(row.dataset.row);
          const combo = currentCombinations[rowIndex] || { option1: null, option2: null, option3: null };
          const cells = row.querySelectorAll("td");

          return {
            option1: combo.option1,
            option2: combo.option2,
            option3: combo.option3,
            normalPrice: cells[2].querySelector("input").value,
            salePrice: cells[3].querySelector("input").value,
            autoPriceAdjustYn: cells[4].querySelector("input").checked ? "Y" : "N",
            quantity: cells[5].querySelector("input").value,
            sellerProductCode: cells[6].querySelector("input").value,
            modelNo: cells[7].querySelector("input").value,
            barcode: cells[8].querySelector("input").value,
            images: optionImages[rowIndex] || { main: null, extra: [] }
          };
        });
      }

      function validateProductForm(rows) {
        if (!displayNameInput.value.trim()) {
          alert("노출상품명을 입력해주세요.");
          return false;
        }

        if (!document.getElementById("internalNameInput").value.trim()) {
          alert("등록상품명(판매자관리용)을 입력해주세요.");
          return false;
        }

        if (!noBrandCheck.checked && !brandInput.value.trim()) {
          alert("브랜드를 입력하거나 '브랜드 없음'을 선택해주세요.");
          return false;
        }

        if (!selectedCategoryNo) {
          alert("카테고리를 검색해서 선택해주세요.");
          return false;
        }

        if (!basePriceInput.value.trim() || Number(basePriceInput.value) <= 0) {
          alert("기본 상품가격을 입력해주세요.");
          return false;
        }

        if (!document.getElementById("shippingZipcodeInput").value.trim()
            || !document.getElementById("shippingAddressInput").value.trim()) {
          alert("출고지 우편번호와 주소를 입력해주세요.");
          return false;
        }

        if (optionMode === "on" && optionGroups.some((group) => group.values.length > 0 && !group.name.trim())) {
          alert("옵션명을 입력해주세요.");
          return false;
        }

        if (rows.length === 0) {
          alert("옵션을 최소 1개 이상 추가해주세요.");
          return false;
        }

        const basePrice = Number(basePriceInput.value);

        for (const row of rows) {
          if (row.salePrice === "" || isNaN(Number(row.salePrice))) {
            alert("모든 옵션의 판매가(기본가에 대한 가감액)를 입력해주세요.");
            return false;
          }
          if (basePrice + Number(row.salePrice) <= 0) {
            alert("기본가와 옵션 판매가를 더한 최종가가 0원 이하인 옵션이 있습니다. 옵션 판매가를 확인해주세요.");
            return false;
          }
          if (row.quantity === "" || Number(row.quantity) < 0) {
            alert("모든 옵션의 재고수량을 입력해주세요.");
            return false;
          }
          if (!row.images.main) {
            alert("모든 옵션에 대표이미지를 등록해주세요.");
            return false;
          }
        }

        return true;
      }

      function buildProductFormData(rows) {
        const formData = new FormData();

        formData.append("brandName", noBrandCheck.checked ? "" : brandInput.value.trim());
        formData.append("noBrandYn", noBrandCheck.checked ? "Y" : "N");
        formData.append("displayName", displayNameInput.value.trim());
        formData.append("internalName", document.getElementById("internalNameInput").value.trim());
        formData.append("categoryNo", selectedCategoryNo);
        formData.append("productPrice", basePriceInput.value.trim());


        const detailTypeMap = { image: "이미지 업로드", editor: "에디터 작성", html: "HTML 작성" };
        const activeDescType = document.querySelector("#descTypeTabs .tab-btn.active").dataset.type;
        formData.append("detailType", detailTypeMap[activeDescType] || "이미지 업로드");

        if (activeDescType === "image") {
          formData.append("descImageCount", descImages.length);
          descImages.forEach(function (item, index) {
            formData.append("descImage_" + index, item.file);
          });
        }

        formData.append("shippingZipcode", document.getElementById("shippingZipcodeInput").value.trim());
        formData.append("shippingAddress", document.getElementById("shippingAddressInput").value.trim());
        formData.append("shippingDetailAddress", document.getElementById("shippingDetailAddressInput").value.trim());
        formData.append("jejuShippingYn", document.querySelector('input[name="jejuShipping"]:checked').value);
        formData.append("courier", document.getElementById("courierSelect").value);
        formData.append("deliveryMethod", document.getElementById("deliveryMethodSelect").value);
        formData.append("bundleShippingYn", document.querySelector('input[name="bundleShipping"]:checked').value);
        formData.append("shippingFeeType", document.getElementById("shippingFeeTypeSelect").value);
        formData.append("leadTimeInputType", document.querySelector('input[name="leadTime"]:checked').value);
        formData.append("leadTimeDays", document.getElementById("leadTimeDaysInput").value);

        const sameDayShipYn = document.getElementById("sameDayShipCheck").checked ? "Y" : "N";
        formData.append("sameDayShipYn", sameDayShipYn);
        formData.append("cutoffTime", sameDayShipYn === "Y" ? document.getElementById("cutoffTimeSelect").value : "");

        formData.append("optionYn", optionMode === "off" ? "N" : "Y");
        formData.append("optionCount", rows.length);

        rows.forEach(function (row, i) {
          formData.append("option_" + i + "_option1Type", row.option1 ? row.option1.type : "");
          formData.append("option_" + i + "_option1Value", row.option1 ? row.option1.value : "");
          formData.append("option_" + i + "_option2Type", row.option2 ? row.option2.type : "");
          formData.append("option_" + i + "_option2Value", row.option2 ? row.option2.value : "");
          formData.append("option_" + i + "_option3Type", row.option3 ? row.option3.type : "");
          formData.append("option_" + i + "_option3Value", row.option3 ? row.option3.value : "");
          formData.append("option_" + i + "_normalPrice", row.normalPrice || "0");
          formData.append("option_" + i + "_salePrice", row.salePrice);
          formData.append("option_" + i + "_autoPriceAdjustYn", row.autoPriceAdjustYn);
          formData.append("option_" + i + "_quantity", row.quantity);
          formData.append("option_" + i + "_sellerProductCode", row.sellerProductCode || "");
          formData.append("option_" + i + "_modelNo", row.modelNo || "");
          formData.append("option_" + i + "_barcode", row.barcode || "");
          formData.append("option_" + i + "_mainImage", row.images.main.file);

          const extraImages = row.images.extra || [];
          formData.append("option_" + i + "_extraImageCount", extraImages.length);
          extraImages.forEach(function (item, j) {
            formData.append("option_" + i + "_extraImage_" + j, item.file);
          });
        });

        return formData;
      }

      function submitProductForm(event) {
        if (event) event.preventDefault();

        const rows = collectOptionRows();
        if (!validateProductForm(rows)) return;

        const formData = buildProductFormData(rows);
        const submitButtons = document.querySelectorAll('#topSubmitButton, .write-foot button[type="submit"]');
        submitButtons.forEach(function (btn) { btn.disabled = true; });

        fetch("${pageContext.request.contextPath}/vendor/product/write", {
          method: "POST",
          body: formData
        })
          .then(function (res) { return res.json(); })
          .then(function (result) {
            if (result.success) {
              alert("상품이 등록되었습니다. (상품번호 " + result.productNo + ")");
              window.location.href = "${pageContext.request.contextPath}/vendor/product";
            } else {
              alert(result.message || "상품 등록에 실패했습니다.");
            }
          })
          .catch(function (err) {
            console.error("상품 등록 요청 실패", err);
            alert("상품 등록 중 오류가 발생했습니다.");
          })
          .finally(function () {
            submitButtons.forEach(function (btn) { btn.disabled = false; });
          });
      }

      document.getElementById("productForm").addEventListener("submit", submitProductForm);
      document.getElementById("topSubmitButton").addEventListener("click", submitProductForm);

    </script>

  </body>

  </html>