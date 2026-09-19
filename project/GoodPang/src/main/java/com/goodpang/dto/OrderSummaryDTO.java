package com.goodpang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private int totalProductPrice;
    private int deliveryFee;
    private int finalPrice;
    
    private int instantDiscount;
    private int couponDiscount;
    private int cashUsed; 
    private int remainCash;
}
