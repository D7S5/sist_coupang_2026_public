package com.goodpang.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 정산관리(vendor-settlement.jsp) 테이블 한 행 - 정산 회차(1주 단위) 하나.
 *
 * ⚠ 근사치입니다. 실제 수수료율/정산주기를 관리하는 테이블이 DB에 없어서(SELLER/PRODUCT/CATEGORY
 * 어디에도 수수료 관련 컬럼이 없음), 수수료율은 임의로 고정한 값(COMMISSION_RATE)이고 정산주기도
 * "배송완료일 기준 1주 단위 + N일 뒤 지급"으로 단순 가정한 것입니다. 실제 서비스라면 카테고리별/
 * 판매자 등급별로 다른 수수료율과, 정산 배치를 관리하는 별도 테이블이 필요합니다.
 */
@Getter
@Setter
public class VendorSettlementDTO {

    private static final double COMMISSION_RATE = 0.10;   // 가정치: 매출의 10%
    private static final int PAYOUT_DELAY_DAYS = 7;        // 가정치: 정산기간 종료 후 7일 뒤 지급

    private LocalDate periodStart; // 정산기간 시작일(월요일)
    private int orderCount;
    private long salesAmount;      // 이 판매자 몫 매출액 합계(배송완료 기준)

    public LocalDate getPeriodEnd() {
        return periodStart.plusDays(6);
    }

    public String getPeriodLabel() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return periodStart.format(fmt) + " ~ " + getPeriodEnd().format(fmt);
    }

    public long getCommissionAmount() {
        return Math.round(salesAmount * COMMISSION_RATE);
    }

    public long getSettlementAmount() {
        return salesAmount - getCommissionAmount();
    }

    public LocalDate getSettlementDate() {
        return getPeriodEnd().plusDays(PAYOUT_DELAY_DAYS);
    }

    public boolean isSettled() {
        return !getSettlementDate().isAfter(LocalDate.now());
    }
}
