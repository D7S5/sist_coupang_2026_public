package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 관리자 상품 승인 목록(admin-product-list.jsp) 테이블 한 행.
 */
@Getter
@Setter
public class AdminProductApprovalDTO {

    private int productNo;
    private String productName;

    private int sellerNo;
    private String storeName;

    private String saleStatus;

    private String mainCategoryName;
    private String midCategoryName;
    private String subCategoryName;

    private Integer minPrice;
    private Integer maxPrice;

    private String thumbnailUrl;

    private Date createdDate;
}
