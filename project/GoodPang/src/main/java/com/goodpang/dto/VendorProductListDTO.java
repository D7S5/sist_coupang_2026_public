package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 상품 목록(vendor_products.jsp) 테이블 한 행.
 * PRODUCT 한 건 + PRODUCT_OPTION 가격/재고 요약 + 대표이미지 1장을 담는다.
 */
@Getter
@Setter
public class VendorProductListDTO {

    private int productNo;
    private String productName;
    private String saleMethod;
    private String saleStatus;

    private String mainCategoryName;
    private String midCategoryName;
    private String subCategoryName;

    private Integer minPrice;
    private Integer maxPrice;
    private Integer totalQuantity;
    private int optionCount;

    private String thumbnailUrl;

    private Date createdDate;
    private Date updatedDate;
}
