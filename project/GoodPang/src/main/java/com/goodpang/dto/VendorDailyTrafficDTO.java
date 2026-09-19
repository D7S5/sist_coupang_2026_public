package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 대시보드(vendor-dashboard.jsp) KPI 카드 스파크라인용 일별 방문자수/상품노출수.
 * VendorDashboardDAO.getDailyTrafficStat 사용.
 */
@Getter
@Setter
public class VendorDailyTrafficDTO {

    private String label;        // 차트 x축 라벨 ("5/13" 형태)
    private int visitorCount;    // 그 날짜 방문자수 (세션ID 중복제거)
    private int viewCount;       // 그 날짜 상품노출수 (조회 건수)
}
