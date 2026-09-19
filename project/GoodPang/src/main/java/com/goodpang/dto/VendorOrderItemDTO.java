package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 주문 상세(vendor-order-detail.jsp)의 주문상품 한 줄.
 */
@Getter
@Setter
public class VendorOrderItemDTO {

    private int orderDetailNo;
    private String productName;
    private String optionLabel;
    private int orderQty;
    private int price;
}
