<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>판매자 상세 - ${seller.storeName}</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 22px; margin: 0 0 4px; }
    .sub { color: #888; margin-bottom: 20px; }

    .section { border: 1px solid #eee; border-radius: 8px; padding: 20px; margin-bottom: 16px; }
    .section h2 { font-size: 15px; margin: 0 0 14px; color: #333; }

    .grid { display: grid; grid-template-columns: 160px 1fr; row-gap: 10px; column-gap: 12px; font-size: 14px; }
    .grid dt { color: #888; }
    .grid dd { margin: 0; }

    .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; }
    .badge-wait { background: #fff4e5; color: #a15c00; }
    .badge-review { background: #e8f0fe; color: #1a56db; }
    .badge-approved { background: #e6f7ec; color: #0f7b3c; }
    .badge-rejected { background: #fdecea; color: #c0392b; }
    .badge-suspended { background: #f3f0ff; color: #6c3ce9; }
    .badge-withdrawn { background: #f2f2f2; color: #666; }

    .doc-link { display: inline-block; margin-right: 12px; padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; color: #111; }
    .doc-link.disabled { color: #bbb; border-color: #eee; pointer-events: none; }

    .doc-preview { display: inline-block; margin-right: 16px; text-align: center; }
    .doc-preview img { display: block; width: 140px; height: 140px; object-fit: cover; border: 1px solid #ddd; border-radius: 6px; }
    .doc-preview span { display: block; margin-top: 6px; font-size: 13px; color: #333; }
    .doc-preview.disabled span { color: #bbb; }
    .doc-preview-empty { display: inline-block; width: 140px; height: 140px; border: 1px dashed #eee; border-radius: 6px; color: #bbb; font-size: 13px; text-align: center; line-height: 140px; }

    .action-row { display: flex; gap: 10px; align-items: flex-start; }
    .action-row form { display: flex; gap: 8px; align-items: center; }
    .btn { padding: 10px 18px; border-radius: 6px; border: none; font-size: 14px; cursor: pointer; }
    .btn-approve { background: #0f7b3c; color: #fff; }
    .btn-reject { background: #c0392b; color: #fff; }
    .reject-reason-input { padding: 9px 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; width: 260px; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/sellers">&larr; 목록으로</a>

  <h1>${seller.storeName}</h1>
  <p class="sub">판매자번호 ${seller.sellerNo}</p>

  <div class="section">
    <h2>입점심사 상태</h2>
    <dl class="grid">
      <dt>상태</dt>
      <dd>
        <c:choose>
          <c:when test="${seller.approvalStatus == '입점 대기'}">
            <span class="badge badge-wait">입점 대기</span>
          </c:when>
          <c:when test="${seller.approvalStatus == '심사 중'}">
            <span class="badge badge-review">심사 중</span>
          </c:when>
          <c:when test="${seller.approvalStatus == '승인'}">
            <span class="badge badge-approved">승인</span>
          </c:when>
          <c:when test="${seller.approvalStatus == '반려'}">
            <span class="badge badge-rejected">반려</span>
          </c:when>
          <c:when test="${seller.approvalStatus == '정지'}">
            <span class="badge badge-suspended">정지</span>
          </c:when>
          <c:when test="${seller.approvalStatus == '탈퇴'}">
            <span class="badge badge-withdrawn">탈퇴</span>
          </c:when>
          <c:otherwise>${seller.approvalStatus}</c:otherwise>
        </c:choose>
      </dd>

      <c:if test="${not empty seller.rejectReason}">
        <dt>${seller.approvalStatus == '정지' ? '정지 사유' : '반려 사유'}</dt>
        <dd>${seller.rejectReason}</dd>
      </c:if>

      <dt>가입일</dt>
      <dd><fmt:formatDate value="${seller.createdDate}" pattern="yyyy-MM-dd HH:mm" /></dd>

      <dt>최종 수정일</dt>
      <dd><fmt:formatDate value="${seller.updatedDate}" pattern="yyyy-MM-dd HH:mm" /></dd>
    </dl>

    <hr style="border:none;border-top:1px solid #eee;margin:16px 0;">

    <div class="action-row">

      <form method="post" action="${pageContext.request.contextPath}/admin/seller-approve"
        onsubmit="return confirm('이 판매자를 승인하시겠습니까?');">
        <input type="hidden" name="sellerNo" value="${seller.sellerNo}">
        <input type="hidden" name="action" value="approve">
        <button class="btn btn-approve" type="submit">승인</button>
      </form>

      <form method="post" action="${pageContext.request.contextPath}/admin/seller-approve"
        onsubmit="return confirm('이 판매자를 반려하시겠습니까?');">
        <input type="hidden" name="sellerNo" value="${seller.sellerNo}">
        <input type="hidden" name="action" value="reject">
        <input class="reject-reason-input" type="text" name="rejectReason" placeholder="반려 사유를 입력해주세요">
        <button class="btn btn-reject" type="submit">반려</button>
      </form>

      <c:choose>

        <c:when test="${seller.approvalStatus == '정지'}">
          <form method="post" action="${pageContext.request.contextPath}/admin/seller-approve"
            onsubmit="return confirm('이 판매자의 정지를 해제하고 승인 상태로 되돌리시겠습니까?');">
            <input type="hidden" name="sellerNo" value="${seller.sellerNo}">
            <input type="hidden" name="action" value="reactivate">
            <button class="btn btn-approve" type="submit">정지 해제</button>
          </form>
        </c:when>

        <c:otherwise>
          <form method="post" action="${pageContext.request.contextPath}/admin/seller-approve"
            onsubmit="return confirm('이 판매자 계정을 정지시키겠습니까? 정지되면 로그인이 차단됩니다.');">
            <input type="hidden" name="sellerNo" value="${seller.sellerNo}">
            <input type="hidden" name="action" value="suspend">
            <input class="reject-reason-input" type="text" name="suspendReason" placeholder="정지 사유를 입력해주세요">
            <button class="btn btn-reject" type="submit">계정 정지</button>
          </form>
        </c:otherwise>

      </c:choose>

    </div>
  </div>

  <div class="section">
    <h2>계정 / 담당자 정보</h2>
    <dl class="grid">
      <dt>이메일(아이디)</dt>
      <dd>${seller.email}</dd>
      <dt>담당자명</dt>
      <dd>${seller.managerName}</dd>
      <dt>휴대폰번호</dt>
      <dd>${seller.phone}</dd>
    </dl>
  </div>

  <div class="section">
    <h2>사업자 정보</h2>
    <dl class="grid">
      <dt>사업자유형</dt>
      <dd>${seller.businessType}</dd>
      <dt>사업자등록번호</dt>
      <dd>${seller.businessNo}</dd>
      <dt>대표자명</dt>
      <dd>${seller.ceoName}</dd>
      <dt>상호명</dt>
      <dd>${seller.storeName}</dd>
      <dt>대표 판매 카테고리</dt>
      <dd>${not empty seller.categoryNo ? seller.categoryNo : "미입력"}</dd>
      <dt>통신판매업신고번호</dt>
      <dd>${not empty seller.mailOrderNo ? seller.mailOrderNo : "미입력"}</dd>
      <dt>사업장 주소</dt>
      <dd>
        <c:choose>
          <c:when test="${not empty seller.businessAddress}">
            (${seller.zipcode}) ${seller.businessAddress} ${seller.businessDetailAddress}
          </c:when>
          <c:otherwise>미입력</c:otherwise>
        </c:choose>
      </dd>
    </dl>
  </div>

  <div class="section">
    <h2>정산계좌</h2>
    <dl class="grid">
      <dt>은행명</dt>
      <dd>${not empty seller.bankName ? seller.bankName : "미입력"}</dd>
      <dt>계좌번호</dt>
      <dd>${not empty seller.accountNo ? seller.accountNo : "미입력"}</dd>
      <dt>예금주명</dt>
      <dd>${not empty seller.accountHolder ? seller.accountHolder : "미입력"}</dd>
    </dl>
  </div>

  <div class="section">
    <h2>첨부서류</h2>

    <c:choose>
      <c:when test="${not empty seller.businessCertUrl}">
        <a class="doc-preview" target="_blank"
          href="${pageContext.request.contextPath}/${seller.businessCertUrl}">
          <img src="${pageContext.request.contextPath}/${seller.businessCertUrl}" alt="사업자등록증">
          <span>사업자등록증 보기</span>
        </a>
      </c:when>
      <c:otherwise>
        <div class="doc-preview disabled">
          <div class="doc-preview-empty">미첨부</div>
          <span>사업자등록증</span>
        </div>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${not empty seller.mailOrderCertUrl}">
        <a class="doc-preview" target="_blank"
          href="${pageContext.request.contextPath}/${seller.mailOrderCertUrl}">
          <img src="${pageContext.request.contextPath}/${seller.mailOrderCertUrl}" alt="통신판매신고증">
          <span>통신판매신고증 보기</span>
        </a>
      </c:when>
      <c:otherwise>
        <div class="doc-preview disabled">
          <div class="doc-preview-empty">미첨부</div>
          <span>통신판매신고증</span>
        </div>
      </c:otherwise>
    </c:choose>

  </div>

</body>

</html>
