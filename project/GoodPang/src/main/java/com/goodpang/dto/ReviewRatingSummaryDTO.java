package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRatingSummaryDTO {

    private int reviewCount;
    private double avgRating;

    private int bestPercent;
    private int goodPercent;
    private int normalPercent;
    private int poorPercent;
    private int badPercent;
}