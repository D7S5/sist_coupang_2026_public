package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewItemDTO {

    private int orderDetailNo;
    private int productNo;

    private String productName;
    private String productImage;
    private String optionName;
    
    
}