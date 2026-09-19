package com.goodpang.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/*
 * 관리자 판매자 액션 로그(admin-vendor-action-log-list.jsp) 검색 조건.
 * 값이 없는(null/빈 문자열) 필드는 조건에서 빠진다 - VendorActionLogDAO 참고.
 */
@Getter
@Setter
public class VendorActionLogSearchDTO {

    private String storeName;   // 스토어명 부분일치 검색
    private String actionType;  // 정확히 일치 (예: "상품 등록")
    private String targetType;  // 정확히 일치 (예: "PRODUCT")
    private LocalDate startDate;
    private LocalDate endDate;
}
