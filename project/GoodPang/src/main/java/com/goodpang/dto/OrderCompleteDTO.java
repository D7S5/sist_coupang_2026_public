package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class OrderCompleteDTO {

    // 배송 정보
    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address;
    private String requestMsg;

    // 도착 예정일
    private String arrivalDate;

    // 판매자
    private String sellerName;

    // 금액
    private Integer orderAmount;
    private Integer discountAmount;
    private Integer shippingFee;
    private Integer paymentAmount;
}