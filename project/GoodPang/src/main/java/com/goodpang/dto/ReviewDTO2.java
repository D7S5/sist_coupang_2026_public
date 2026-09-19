package com.goodpang.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO2 {
    
	private int reviewNo;
    private int productRating;
    private Integer serviceRating;
    private String reviewContent;
    private String reviewSummary;
    private Date reviewDate;
    private int memberNo;
    private int orderDetailNo;
    private int rating;
    
    private String maskedName;    // "고*미"
    private String optionText;    // "사이즈 M" 같은 표시용 문자열 (없으면 빈 문자열)
    private String productName;   // PRODUCT.PRODUCT_NAME — 2026-08-27 추가
    
    private String ratingStars;
    
    private int productNo;
    
    private List<String> imageUrls = new ArrayList<>();
}