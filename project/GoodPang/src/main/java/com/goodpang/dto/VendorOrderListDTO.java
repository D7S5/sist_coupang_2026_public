package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 주문 목록(vendor_orders.jsp) 테이블 한 행.
 * ORDER_DETAIL 한 건(판매자의 상품이 포함된 주문 라인) + 주문/구매자 정보를 담는다.
 */
@Getter
@Setter
public class VendorOrderListDTO {

    private int orderNo;
    private int orderDetailNo;

    private String productName;
    private String thumbnailUrl;
    private String optionLabel;
    private int orderQty;
    private int price;

    private String orderStatus;
    private Date orderDate;

    private String buyerName;
    private String buyerPhone;
}
