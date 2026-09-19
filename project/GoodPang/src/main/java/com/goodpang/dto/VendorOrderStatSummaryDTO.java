package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 주문/배송 관리(vendor-order.jsp) 상단 통계 카드 + 우측 배송현황 도넛 집계 결과.
 * 실제 존재하는 주문상태(결제완료=출고대기 / 배송중 / 배송완료) 기준으로만 집계한다.
 * 신규주문·결제대기(체크아웃 단계의 미결제)·상품준비중·취소반품교환은 대응하는 상태값
 * 자체가 시스템에 없어서 집계하지 않는다.
 */
@Getter
@Setter
public class VendorOrderStatSummaryDTO {

    private int waitingCount;       // 결제완료 (출고 대기)
    private int shippingCount;      // 배송중
    private int deliveredCount;     // 배송완료 (전체)
    private int deliveredTodayCount; // 배송완료 중 오늘 완료된 건수

    public int getTotalCount() {
        return waitingCount + shippingCount + deliveredCount;
    }

    public int getWaitingPercent() {
        return percentOf(waitingCount);
    }

    public int getShippingPercent() {
        return percentOf(shippingCount);
    }

    public int getDeliveredPercent() {
        return percentOf(deliveredCount);
    }

    private int percentOf(int count) {
        int total = getTotalCount();
        return (total == 0) ? 0 : (int) Math.round(count * 100.0 / total);
    }
}
