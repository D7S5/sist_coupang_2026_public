package com.goodpang.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 상품 등록 화면(vendor-product-write.jsp) 제출 내용을 PRODUCT 테이블 한 행 + 딸린 옵션 목록으로 담는 DTO.
 * VendorProductWriteServlet.doPost()가 요청 파라미터로 채우고, ProductWriteDAO.insertProduct()가 그대로 INSERT에 사용한다.
 */
@Getter
@Setter
public class ProductWriteDTO {

    private int sellerNo;
    private int subCategoryNo;

    private String saleMethod;
    private String brandName;
    private String noBrandYn;
    private String productName;
    private String internalName;
    private int productPrice;
    private String optionYn = "Y";

    private String detailType;

    private String shippingZipcode;
    private String shippingAddress;
    private String shippingDetailAddress;
    private String jejuShippingYn;
    private String deliveryServiceCode;
    private String deliveryMethod;
    private String bundleShippingYn;
    private String shippingFeeType;
    private String leadTimeInputType;
    private Integer leadTimeDays;
    private String sameDayShipYn;
    private String sameDayCutoffTime;

    private String saleStatus;

    private List<ProductOptionWriteDTO> options = new ArrayList<>();
    private List<String> detailImageUrls = new ArrayList<>();
}
