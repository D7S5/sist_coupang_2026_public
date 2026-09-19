package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 상품 옵션 관리(vendor-product-options.jsp) 테이블 한 행.
 * 판매자의 전체 상품을 가로질러, 옵션(PRODUCT_OPTION) 단위로 한 줄씩 보여준다.
 */
@Getter
@Setter
public class VendorProductOptionDTO {

    private int optionId;
    private int productNo;
    private String productName;
    private String thumbnailUrl;

    private String option1Value;
    private String option2Value;
    private String option3Value;

    private int price;
    private Integer normalPrice;
    private int quantity;
    private String status; // Y=정상(판매가능) / N=품절

    // 상품 정보 아래 옵션값 표시용. 있는 값만 " / "로 이어붙임 (product.jsp의 ProductOptionDTO.getLabel()과 동일한 규칙)
    public String getOptionLabel() {
        StringBuilder sb = new StringBuilder();
        if (option1Value != null) sb.append(option1Value);
        if (option2Value != null) sb.append(sb.length() > 0 ? " / " : "").append(option2Value);
        if (option3Value != null) sb.append(sb.length() > 0 ? " / " : "").append(option3Value);
        return sb.toString();
    }
}
