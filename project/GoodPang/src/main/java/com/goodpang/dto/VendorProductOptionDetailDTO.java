package com.goodpang.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 상품 상세(vendor-product-detail.jsp) 옵션 목록 한 행.
 * PRODUCT_OPTION 한 건 + 그 옵션에 딸린 PRODUCT_IMAGE(대표 1장 + 추가 목록)를 담는다.
 */
@Getter
@Setter
public class VendorProductOptionDetailDTO {

    private int optionId;

    private String option1Type;
    private String option1Value;
    private String option2Type;
    private String option2Value;
    private String option3Type;
    private String option3Value;

    private Integer normalPrice;
    private int price;
    private String autoPriceAdjustYn;
    private int quantity;

    private String sellerProductCode;
    private String modelNo;
    private String barcode;
    private String status;

    private String mainImageUrl;
    private List<String> extraImageUrls = new ArrayList<>();
}
