package com.goodpang.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 상품 옵션 관리(vendor-product-options.jsp) 테이블에서
 * 같은 상품(productNo)의 옵션들을 한 그룹으로 묶어 rowspan 렌더링에 쓴다.
 */
@Getter
@Setter
public class VendorProductOptionGroupDTO {

    private int productNo;
    private String productName;
    private String thumbnailUrl;
    private List<VendorProductOptionDTO> options;
}
