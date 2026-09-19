package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * PRODUCT_IMAGE 한 행.
 * OPTION_ID 가 채워져 있으면 "그 옵션 전용 사진"(대표/추가), null 이면 "상품 상세설명 사진".
 * IMAGE_PURPOSE 값: '대표' / '추가' / '상세설명'
 */
@Getter
@Setter
public class ProductImageDTO {

    private int imageNo;
    private int productNo;
    private Integer optionId;      // 상세설명 사진은 null 이라 Integer(객체형)로 둠
    private String imagePurpose;
    private int imageOrder;
    private String imageUrl;
    
}
