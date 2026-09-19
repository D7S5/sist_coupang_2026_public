package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 취소/반품/교환 관리(vendor-return.jsp) 테이블 한 행.
 * PRODUCT_RETURN 한 건(판매자의 상품이 포함된 취소/반품/교환 신청) 정보를 담는다.
 */
@Getter
@Setter
public class VendorReturnDTO {

    private long returnNo;
    private long orderDetailNo;
    private int orderNo;

    private String productName;
    private String thumbnailUrl;
    private String optionLabel;
    private int returnQty;

    private String returnType;    // 취소/반품/교환/기타 - RETURN_STATUS 값을 기준으로 분류한 결과
    private String returnStatus;  // PRODUCT_RETURN.RETURN_STATUS 원본 값
    private String returnReason;
    private int refundAmount;
    private Date requestDate;

    private String buyerName;
    private String buyerPhone;
}
