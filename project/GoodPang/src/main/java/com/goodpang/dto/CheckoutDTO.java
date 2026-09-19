package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutDTO {

    private int checkoutNo;
    private int memberNo;

    private int productAmount;
    private int instantDiscount;
    private int couponDiscount;
    private int cashUsed;
    private int deliveryFee;
    private int totalPrice;
}