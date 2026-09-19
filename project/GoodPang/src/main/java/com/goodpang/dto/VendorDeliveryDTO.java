package com.goodpang.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 배송 관리(vendor-delivery.jsp) 테이블 한 행 - 배송중인 주문 하나.
 */
@Getter
@Setter
public class VendorDeliveryDTO {

    // 배송 시작일로부터 이 기간(일)이 지나도 배송완료가 안 되면 "지연"으로 본다
    private static final int DELAY_THRESHOLD_DAYS = 3;

    private int deliveryNo;
    private int orderNo;

    private String deliveryServiceCode;
    private String invoiceNo;
    private Date deliveryStartDate;

    private String buyerName;
    private String buyerPhone;

    private String productName;
    private int itemCount;

    // 배송 시작일로부터 오늘까지 경과일
    public long getElapsedDays() {

        if (deliveryStartDate == null) {
            return 0;
        }

        LocalDate startDate = deliveryStartDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        return ChronoUnit.DAYS.between(startDate, LocalDate.now());
    }

    public boolean isDelayed() {
        return getElapsedDays() >= DELAY_THRESHOLD_DAYS;
    }
}
