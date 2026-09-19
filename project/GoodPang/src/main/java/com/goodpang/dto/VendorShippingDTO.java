package com.goodpang.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 출고/운송장 관리(vendor-shipping.jsp) 테이블 한 행 - 출고 대기(결제완료) 주문 하나.
 */
@Getter
@Setter
public class VendorShippingDTO {

    // 결제완료 후 이 기간(일)이 지나도 출고(배송중 전환) 안 되면 "출고 지연"으로 본다
    private static final int DELAY_THRESHOLD_DAYS = 2;

    private int orderNo;
    private Date orderDate; // 결제완료(주문) 일시

    private String buyerName;
    private String buyerPhone;

    private String productName;
    private int itemCount;
    private long totalAmount;

    // 결제완료일로부터 오늘까지 경과일
    public long getElapsedDays() {

        if (orderDate == null) {
            return 0;
        }

        LocalDate startDate = orderDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        return ChronoUnit.DAYS.between(startDate, LocalDate.now());
    }

    public boolean isDelayed() {
        return getElapsedDays() >= DELAY_THRESHOLD_DAYS;
    }
}
