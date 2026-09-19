package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutItemDTO {

    private int checkoutItemNo;
    private int checkoutNo;
    private int productNo;

    private Integer optionId;

    private int orderQty;
    private int price;

    // JSP 출력용
    private String productName;
    private String productImage;

    private String optionName;
}