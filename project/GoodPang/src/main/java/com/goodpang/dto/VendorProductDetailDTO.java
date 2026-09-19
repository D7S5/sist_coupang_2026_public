package com.goodpang.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 상품 상세(vendor-product-detail.jsp) 화면 전체 데이터.
 * PRODUCT 한 건 + 카테고리명 + 옵션 목록 + 상세설명 이미지 목록을 담는다.
 */
@Getter
@Setter
public class VendorProductDetailDTO {

    private int productNo;
    private int sellerNo;

    private String mainCategoryName;
    private String midCategoryName;
    private String subCategoryName;

    private String saleMethod;
    private String brandName;
    private String noBrandYn;
    private String productName;
    private String internalName;
    private int productPrice;

    private String detailType;
    private String productDesc;

    private String shippingZipcode;
    private String shippingAddress;
    private String shippingDetailAddress;
    private String jejuShippingYn;
    private String deliveryServiceCode;
    private String deliveryMethod;
    private String bundleShippingYn;
    private String shippingFeeType;
    private int shippingFee;
    private String leadTimeInputType;
    private Integer leadTimeDays;
    private String sameDayShipYn;
    private String sameDayCutoffTime;

    private String saleStatus;
    private String displayYn;
    private Date createdDate;
    private Date updatedDate;

    private List<VendorProductOptionDetailDTO> options = new ArrayList<>();
    private List<String> detailImageUrls = new ArrayList<>();
}
