package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 정산상세(vendor-settlement-detail.jsp) 테이블 한 행 - 정산기간에 포함된 주문라인 하나.
 * 수수료율은 VendorSettlementDTO와 동일한 가정치(10%)를 그대로 씀 - 주석 참고.
 */
@Getter
@Setter
public class VendorSettlementDetailDTO {

    private static final double COMMISSION_RATE = 0.10;

    private int orderNo;
    private String productName;
    private String optionLabel;
    private int quantity;
    private int price;             // 옵션 포함 단가
    private Date deliveryEndDate;

    public long getLineAmount() {
        return (long) price * quantity;
    }

    public long getCommissionAmount() {
        return Math.round(getLineAmount() * COMMISSION_RATE);
    }

    public long getSettlementAmount() {
        return getLineAmount() - getCommissionAmount();
    }
}
