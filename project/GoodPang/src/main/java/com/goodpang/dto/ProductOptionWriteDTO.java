package com.goodpang.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 상품 등록 화면(vendor-product-write.jsp)의 옵션 목록 테이블 한 행.
 * PRODUCT_OPTION 한 행 + 그 옵션에 딸린 PRODUCT_IMAGE(대표 1장 + 추가 최대 9장)에 대응.
 */
@Getter
@Setter
public class ProductOptionWriteDTO {

    private String option1Type;
    private String option1Value;
    private String option2Type;
    private String option2Value;
    private String option3Type;
    private String option3Value;

    private Integer normalPrice;
    private int salePrice;
    private String autoPriceAdjustYn;
    private int quantity;

    private String sellerProductCode;
    private String modelNo;
    private String barcode;

    private String mainImageUrl;
    private List<String> extraImageUrls = new ArrayList<>();
}
