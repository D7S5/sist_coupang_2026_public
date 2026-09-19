package com.goodpang.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 주문 상세(vendor-order-detail.jsp) 화면 데이터.
 */
@Getter
@Setter
public class VendorOrderDetailDTO {

    private int orderNo;
    private Date orderDate;
    private String orderStatus;

    private int deliveryFee;
    private int totalPrice;
    private int productAmount;
    private int instantDiscount;
    private int couponDiscount;
    private int cashUsed;

    private String buyerName;
    private String buyerPhone;

    private String receiverName;
    private String receiverTel;
    private String zipcode;
    private String address;
    private String detailAddress;
    private String requestMsg;

    // 배송이 아직 시작 안 됐으면 전부 null
    private String deliveryServiceCode;
    private String invoiceNo;
    private String deliveryStatus;
    private Date deliveryStartDate;
    private Date deliveryEndDate;

    private List<VendorOrderItemDTO> items = new ArrayList<>();
}
