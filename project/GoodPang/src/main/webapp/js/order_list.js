// 년도/기간별 의류 상품 샘플 데이터
/*
const orderData = {
    recent: [
        {
            orderNo: 20,
            date: "2026. 8. 12",
            delivery: "8/13(목) 도착",
            badge: "🚀 로켓 내일",
            name: "베이직 100% 순면 오버핏 라운드반팔티 2팩 남여공용",
            price: "18,900원 • 1개",
            img: "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=150"
        },
        {
            orderNo: 30,
            date: "2026. 8. 3",
            delivery: "8/4(화) 도착",
            badge: "🚀 판매자로켓 내일",
            name: "데일리 와이드 일자 데님 팬츠 청바지 중청",
            price: "25,500원 • 1개",
            img: "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=150"
        },
        {
            orderNo: 50,
            date: "2026. 7. 25",
            delivery: "7/26(일) 도착",
            badge: "🚀 로켓 내일",
            name: "프리미엄 페이크 라이더 레더 자켓 블루종",
            price: "59,000원 • 1개",
            img: "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=150"
        }
    ],
    "2026": [
        {
            orderNo: 60,
            date: "2026. 4. 10",
            delivery: "4/11(토) 도착",
            badge: "🚀 로켓 내일",
            name: "헤비웨이트 기모 맨투맨 후드 집업 티셔츠 [그레이]",
            price: "32,800원 • 2개",
            img: "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=150"
        },
        {
            orderNo: "20260218-0001",
            date: "2026. 2. 18",
            delivery: "2/19(목) 도착",
            badge: "🚀 판매자로켓 내일",
            name: "클래식 더블 버튼 오버핏 롱 트렌치코트 (베이지)",
            price: "89,000원 • 1개",
            img: "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=150"
        }
    ],
    "2025": [
        {
            orderNo: "20251105-0001",
            date: "2025. 11. 05",
            delivery: "11/6(목) 도착",
            badge: "🚀 로켓 내일",
            name: "스판 밴딩 핀턱 와이드 슬랙스 팬츠 [블랙]",
            price: "22,900원 • 1개",
            img: "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=150"
        },
        {
            orderNo: "20250814-0001",
            date: "2025. 08. 14",
            delivery: "8/15(금) 도착",
            badge: "🚀 로켓 내일",
            name: "린넨 카라 숏스리브 캐주얼 반팔 셔츠 (화이트)",
            price: "27,400원 • 1개",
            img: "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=150"
        }
    ],
    "2024": [
        {
            orderNo: "20240920-0001",
            date: "2024. 09. 20",
            delivery: "9/21(토) 도착",
            badge: "🚀 로켓 내일",
            name: "울 믹스 하프 집업 니트 스웨터 (네이비)",
            price: "41,200원 • 1개",
            img: "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=150"
        }
    ]
};
*/
document.addEventListener("DOMContentLoaded", function () {
    const listContainer = document.getElementById("order-card-list");
    const periodBtns = document.querySelectorAll(".btn-period");
    const btnPrev = document.getElementById("btn-page-prev");
    const btnNext = document.getElementById("btn-page-next");

    const PAGE_SIZE = 3;      // 한 페이지에 보여줄 카드 개수
    let currentYearKey = "recent";
    let currentPage = 0;

    // 1. 주문 목록 화면에 렌더링하는 함수 (페이징 포함)
    function renderOrders(yearKey, page) {
        currentYearKey = yearKey;

      //  const items = orderData[yearKey] || orderData["recent"];
/*
        if (items.length === 0) {
            listContainer.innerHTML = `<div style="text-align:center; padding:50px; color:#888;">해당 기간의 주문 내역이 없습니다.</div>`;
            updatePagerState(0, 0);
            return;
        }

        const totalPages = Math.ceil(items.length / PAGE_SIZE);
        const safePage = Math.min(Math.max(page, 0), totalPages - 1);
        currentPage = safePage;

        const start = safePage * PAGE_SIZE;
        const pageItems = items.slice(start, start + PAGE_SIZE);

        let html = "";
        pageItems.forEach(item => {
            html += `
            <div class="order-card">
                <div class="card-header">
                    <span class="order-date">${item.date} 주문</span>
                    <a href="${contextPath}/order/order_detail?orderNo=${item.orderNo || ''}" class="link-detail">주문 상세보기 &gt;</a>
                </div>
                <div class="card-body">
                    <div class="delivery-status">
                        <span class="status-title">배송완료</span>
                        <span class="status-date">• ${item.delivery}</span>
                    </div>
                    <div class="item-content">
                        <div class="item-img">
                            <img src="${item.img}" alt="상품이미지">
                        </div>
                        <div class="item-info">
                            <p class="badge-rocket">${item.badge}</p>
                            <p class="product-name">${item.name}</p>
                            <p class="product-price">${item.price}</p>
                            <button type="button" class="btn-cart-add" onclick="location.href='cart.jsp'">장바구니 담기</button>
                        </div>
                        <div class="item-actions">
                            <button type="button" class="btn-action primary">배송 조회</button>
                            <button type="button" class="btn-action">교환, 반품 신청</button>
                            <button type="button" class="btn-action">리뷰 작성하기</button>
                        </div>
                    </div>
                </div>
            </div>`;
        });
		*/
        listContainer.innerHTML = html;
        updatePagerState(safePage, totalPages);
    }

    // 2. 이전/다음 버튼 활성/비활성 처리
    function updatePagerState(page, totalPages) {
        if (!btnPrev || !btnNext) return;
        btnPrev.disabled = page <= 0;
        btnNext.disabled = totalPages === 0 || page >= totalPages - 1;
    }

    // 초기 실행 (최근 6개월, 1페이지)
    renderOrders("recent", 0);

    // 3. 기간(연도) 선택 클릭 이벤트
    periodBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            periodBtns.forEach(b => b.classList.remove("active"));
            this.classList.add("active");

            const selectedYear = this.getAttribute("data-year");
            renderOrders(selectedYear, 0);
        });
    });

    // 4. 이전/다음 페이지 이동 이벤트
    if (btnPrev) {
        btnPrev.addEventListener("click", function () {
            if (this.disabled) return;
            renderOrders(currentYearKey, currentPage - 1);
        });
    }
    if (btnNext) {
        btnNext.addEventListener("click", function () {
            if (this.disabled) return;
            renderOrders(currentYearKey, currentPage + 1);
        });
    }
});