package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewAvailableDTO {

	private int orderDetailNo;
    private int productNo;
    private String productName;
    private boolean reviewWritten;
    
    private String productImage;
    private Date orderDate;
    private Date deliveryDate;
    
    private String optionText;

    public boolean isReviewWritten() {
        return reviewWritten;
    }

    public void setReviewWritten(boolean reviewWritten) {
        this.reviewWritten = reviewWritten;
    }
}
