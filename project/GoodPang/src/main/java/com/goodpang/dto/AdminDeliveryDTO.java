package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 관리자 배송중 상품 목록(admin-delivery-list.jsp) 테이블 한 행.
 */
@Getter
@Setter
public class AdminDeliveryDTO {

    private int deliveryNo;
    private int orderNo;

    private String deliveryServiceCode;
    private String invoiceNo;
    private String deliveryStatus;
    private Date deliveryStartDate;

    private String buyerName;
    private String buyerPhone;

    private String zipcode;
    private String address;
    private String detailAddress;

    private String productName;
    private String productImageUrl;
    private String storeName;
    private int itemCount;
}
