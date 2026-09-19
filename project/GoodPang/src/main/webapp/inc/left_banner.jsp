<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!-- ==========================================================================
     [공통 모듈] 좌측 MY쿠팡 메뉴 전용 인라인 CSS (디자인 깨짐 방지)
     ========================================================================== -->
<style type="text/css">
/* =========================================
   왼쪽 MY쿠팡 전체 컨테이너
========================================= */
.mycoupang-side {
    width: 135px;
    flex-shrink: 0;
    border: 1px solid #ddd;
    background: #fff;
    box-sizing: border-box;
    height: fit-content; 
}

.mycoupang-side .side-title {
    height: 54px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #2673b9;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
}

.mycoupang-side .side-section {
    padding: 20px 15px 17px;
    border-bottom: 1px solid #ddd;
}

.mycoupang-side .side-section h3 {
    margin: 0 0 12px;
    font-size: 13px;
    font-weight: bold;
    color: #111;
}

.mycoupang-side .side-section a {
    display: block;
    margin-bottom: 7px;
    color: #222;
    text-decoration: none;
    line-height: 1.25;
    font-size: 11px;
}

.mycoupang-side .side-section a:hover {
    text-decoration: underline;
}

/* 현재 페이지 활성화 (파란색 강조) */
.mycoupang-side .side-section a.active {
    color: #1675d1;
    font-weight: bold;
}

/* N 아이콘 배지 */
.mycoupang-side .new {
    display: inline-flex;
    width: 12px;
    height: 12px;
    align-items: center;
    justify-content: center;
    background: #e5003d;
    color: #fff;
    font-size: 8px;
    font-weight: bold;
    margin-left: 2px;
}

/* 하단 고객센터 메뉴 */
.mycoupang-side .side-help {
    margin-top: 0;              /* 위쪽 여백 완전히 제거 */
    border-top: none;           /* 이중 테두리 발생 방지 */
    padding: 12px 15px 5px 15px;/* 위/아래 패딩 밀착 조정 */
}

.mycoupang-side .side-help a {
    display: flex;
    align-items: flex-start;
    gap: 6px;
    margin-bottom: 10px;
    font-size: 11px;
    color: #2673b9;
    font-weight: bold;
    text-decoration: none;
}

.mycoupang-side .side-help a small {
    font-size: 9px;
    font-weight: normal;
    color: #666;
}
</style>

<!-- ==========================================================================
     [공통 모듈] 좌측 MY쿠팡 메뉴 HTML/JSTL 구조
     ========================================================================== -->
<aside class="mycoupang-side">

	<div class="side-title">MY굿팡</div>

	<div class="side-section">
		<h3>MY 쇼핑</h3>
		<a href="${pageContext.request.contextPath}/order/order_list"
		   class="${param.activeMenu eq 'order_list' ? 'active' : ''}">주문목록/배송조회</a>
		   
		<a href="${pageContext.request.contextPath}/order/cancel_history"
		   class="${param.activeMenu eq 'cancel_history' ? 'active' : ''}">취소/반품/교환/환불 내역</a>
		   
		<a href="${pageContext.request.contextPath}/wow/membership"
		   class="${param.activeMenu eq 'wow' ? 'active' : ''}">와우 멤버십</a>
		   
		<a href="#" class="${param.activeMenu eq 'subscription' ? 'active' : ''}">구독 서비스 <span class="new">N</span></a>
		<a href="#" class="${param.activeMenu eq 'freshbag' ? 'active' : ''}">로켓프레시 프레시백 <span class="new">N</span></a>
		<a href="#" class="${param.activeMenu eq 'receipt' ? 'active' : ''}">영수증 조회/출력</a>
	</div>

	<div class="side-section">
		<h3>MY 혜택</h3>
		<a href="#" class="${param.activeMenu eq 'coupon' ? 'active' : ''}">쿠폰 · 이용권</a>
		<a href="#" class="${param.activeMenu eq 'cash' ? 'active' : ''}">쿠팡캐시/기프트카드</a>
	</div>

	<div class="side-section">
		<h3>MY 활동</h3>
		<a href="#" class="${param.activeMenu eq 'inquiry' ? 'active' : ''}">문의하기</a>
		<a href="#" class="${param.activeMenu eq 'inquiry_list' ? 'active' : ''}">문의내역 확인</a>
		<a href="${pageContext.request.contextPath}/review/list" 
		   class="${param.activeMenu eq 'review' ? 'active' : ''}">리뷰관리</a>
		<a href="#" class="${param.activeMenu eq 'wish' ? 'active' : ''}">찜 리스트</a>
	</div>

	<div class="side-section">
		<h3>MY 정보</h3>
		<a href="${pageContext.request.contextPath}/member/modify" 
		   class="${param.activeMenu eq 'member_modify' ? 'active' : ''}">개인정보확인/수정</a>
		<a href="#" class="${param.activeMenu eq 'pay' ? 'active' : ''}">결제수단·쿠페이 관리</a>
		<a href="${pageContext.request.contextPath}/address/list" 
		   class="${param.activeMenu eq 'address' ? 'active' : ''}">배송지 관리</a>
		<a href="#" class="${param.activeMenu eq 'passkey' ? 'active' : ''}">패스키 관리</a>
		<a href="${pageContext.request.contextPath}/member/withdraw" class="${param.activeMenu eq 'leave' ? 'active' : ''}">회원 탈퇴</a>
	</div>

	<!-- 고객센터 메뉴 -->
	<div class="side-help">
		<a href="#"> <span class="help-icon">📝</span> <span>쿠팡문의</span> </a>
		<a href="#"> <span class="help-icon">📢</span> <span>고객의 소리<br> <small>제안·칭찬·불편신고</small></span> </a>
		<a href="#"> <span class="help-icon">📦</span> <span>취소/반품 안내</span> </a>
	</div>

</aside>