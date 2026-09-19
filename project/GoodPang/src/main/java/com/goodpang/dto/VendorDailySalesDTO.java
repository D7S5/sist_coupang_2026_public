package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 대시보드(vendor-dashboard.jsp) "매출 현황" 차트 한 구간(일/주/월)분.
 * VendorDashboardDAO.getDailySalesStat / getWeeklySalesStat / getMonthlySalesStat 공통 사용.
 */
@Getter
@Setter
public class VendorDailySalesDTO {

    private String label;      // 차트 x축 라벨 (일간 "5/13", 주간 "5/13"(주 시작일), 월간 "5월")
    private long salesAmount;  // 그 구간 매출액 (주문취소 제외)
    private int orderCount;    // 그 구간 주문수
}
