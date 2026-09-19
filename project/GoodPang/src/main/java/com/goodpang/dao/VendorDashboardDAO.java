package com.goodpang.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorDailySalesDTO;
import com.goodpang.dto.VendorDailyTrafficDTO;
import com.goodpang.dto.VendorDashboardStatDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 대시보드(vendor-dashboard.jsp) 상단 KPI 카드 집계.
 */
public class VendorDashboardDAO {

    // 기준일/전날 주문수·매출·방문자수·상품노출수 - 이 판매자 상품 기준, 주문취소 건은 매출/주문수에서 제외
    public VendorDashboardStatDTO getTodayStat(int sellerNo, Date targetDate) {

        VendorDashboardStatDTO dto = new VendorDashboardStatDTO();

        fillOrderSalesStat(dto, sellerNo, targetDate);
        fillTrafficStat(dto, sellerNo, targetDate);

        return dto;
    }

    // 기준일/전날 주문수·매출 - 이 판매자 상품이 포함된 주문(ORDER_DETAIL) 기준, 주문취소 건 제외
    private void fillOrderSalesStat(VendorDashboardStatDTO dto, int sellerNo, Date targetDate) {

        String sql = """
            SELECT
                COUNT(DISTINCT CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE
                                     THEN O.ORDER_NO END) AS TODAY_ORDER_COUNT,
                NVL(SUM(CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE
                              THEN OD.PRICE * OD.ORDER_QTY END), 0) AS TODAY_SALES,
                COUNT(DISTINCT CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE - 1
                                     THEN O.ORDER_NO END) AS YESTERDAY_ORDER_COUNT,
                NVL(SUM(CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE - 1
                              THEN OD.PRICE * OD.ORDER_QTY END), 0) AS YESTERDAY_SALES
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                CROSS JOIN (SELECT ? AS TARGET_DATE FROM DUAL) D
            WHERE P.SELLER_NO = ?
              AND O.ORDER_STATUS != '주문취소'
              AND TRUNC(O.ORDER_DATE) IN (D.TARGET_DATE, D.TARGET_DATE - 1)
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setDate(1, targetDate);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto.setTodayOrderCount(rs.getInt("TODAY_ORDER_COUNT"));
                    dto.setTodaySales(rs.getLong("TODAY_SALES"));
                    dto.setYesterdayOrderCount(rs.getInt("YESTERDAY_ORDER_COUNT"));
                    dto.setYesterdaySales(rs.getLong("YESTERDAY_SALES"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 기준일/전날 방문자수·상품노출수 - PRODUCT_VIEW_LOG(상품 상세페이지 조회 로그) 기준.
     * 방문자수 = 세션ID 중복제거(COUNT DISTINCT), 상품노출수 = 조회 건수 그대로(COUNT).
     */
    private void fillTrafficStat(VendorDashboardStatDTO dto, int sellerNo, Date targetDate) {

        String sql = """
            SELECT
                COUNT(DISTINCT CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE
                                     THEN L.SESSION_ID END) AS TODAY_VISITOR_COUNT,
                COUNT(CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE
                           THEN 1 END) AS TODAY_VIEW_COUNT,
                COUNT(DISTINCT CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE - 1
                                     THEN L.SESSION_ID END) AS YESTERDAY_VISITOR_COUNT,
                COUNT(CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE - 1
                           THEN 1 END) AS YESTERDAY_VIEW_COUNT
            FROM PRODUCT_VIEW_LOG L
                JOIN PRODUCT P ON L.PRODUCT_NO = P.PRODUCT_NO
                CROSS JOIN (SELECT ? AS TARGET_DATE FROM DUAL) D
            WHERE P.SELLER_NO = ?
              AND TRUNC(L.VIEW_DATE) IN (D.TARGET_DATE, D.TARGET_DATE - 1)
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setDate(1, targetDate);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto.setTodayVisitorCount(rs.getInt("TODAY_VISITOR_COUNT"));
                    dto.setTodayProductViewCount(rs.getInt("TODAY_VIEW_COUNT"));
                    dto.setYesterdayVisitorCount(rs.getInt("YESTERDAY_VISITOR_COUNT"));
                    dto.setYesterdayProductViewCount(rs.getInt("YESTERDAY_VIEW_COUNT"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 매출 현황 차트(일간) - 기준일 포함 최근 7일 날짜별 매출액·주문수.
     * 이 판매자 상품이 포함된 주문 기준, 주문취소 건 제외. 주문이 없는 날짜도 0으로 채워서
     * 항상 7일치가 빠짐없이 나오게 한다 (날짜 스핀 D를 만들어 실적 서브쿼리 S를 LEFT JOIN).
     */
    public List<VendorDailySalesDTO> getDailySalesStat(int sellerNo, Date targetDate) {

        List<VendorDailySalesDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                D.STAT_DATE,
                NVL(S.SALES_AMOUNT, 0) AS SALES_AMOUNT,
                NVL(S.ORDER_COUNT, 0) AS ORDER_COUNT
            FROM (
                SELECT ? - LEVEL + 1 AS STAT_DATE
                FROM DUAL
                CONNECT BY LEVEL <= 7
            ) D
            LEFT JOIN (
                SELECT
                    TRUNC(O.ORDER_DATE) AS ORDER_DAY,
                    SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT,
                    COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT
                FROM ORDER_DETAIL OD
                    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                WHERE P.SELLER_NO = ?
                  AND O.ORDER_STATUS != '주문취소'
                  AND TRUNC(O.ORDER_DATE) >= ? - 6
                GROUP BY TRUNC(O.ORDER_DATE)
            ) S ON S.ORDER_DAY = D.STAT_DATE
            ORDER BY D.STAT_DATE
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setDate(1, targetDate);
            pstmt.setInt(2, sellerNo);
            pstmt.setDate(3, targetDate);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    list.add(mapDailyRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * KPI 카드 스파크라인용 - 기준일 포함 최근 7일 날짜별 방문자수·상품노출수.
     * PRODUCT_VIEW_LOG 기준, 로직은 getDailySalesStat과 동일한 날짜 스핀 + LEFT JOIN 방식.
     */
    public List<VendorDailyTrafficDTO> getDailyTrafficStat(int sellerNo, Date targetDate) {

        List<VendorDailyTrafficDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                D.STAT_DATE,
                NVL(S.VISITOR_COUNT, 0) AS VISITOR_COUNT,
                NVL(S.VIEW_COUNT, 0) AS VIEW_COUNT
            FROM (
                SELECT ? - LEVEL + 1 AS STAT_DATE
                FROM DUAL
                CONNECT BY LEVEL <= 7
            ) D
            LEFT JOIN (
                SELECT
                    TRUNC(L.VIEW_DATE) AS VIEW_DAY,
                    COUNT(DISTINCT L.SESSION_ID) AS VISITOR_COUNT,
                    COUNT(*) AS VIEW_COUNT
                FROM PRODUCT_VIEW_LOG L
                    JOIN PRODUCT P ON L.PRODUCT_NO = P.PRODUCT_NO
                WHERE P.SELLER_NO = ?
                  AND TRUNC(L.VIEW_DATE) >= ? - 6
                GROUP BY TRUNC(L.VIEW_DATE)
            ) S ON S.VIEW_DAY = D.STAT_DATE
            ORDER BY D.STAT_DATE
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setDate(1, targetDate);
            pstmt.setInt(2, sellerNo);
            pstmt.setDate(3, targetDate);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    VendorDailyTrafficDTO dto = new VendorDailyTrafficDTO();
                    dto.setLabel(rs.getDate("STAT_DATE").toLocalDate().format(DateTimeFormatter.ofPattern("M/d")));
                    dto.setVisitorCount(rs.getInt("VISITOR_COUNT"));
                    dto.setViewCount(rs.getInt("VIEW_COUNT"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 매출 현황 차트(주간) - 기준일이 속한 주 포함 최근 5주(월요일 시작 ISO 주) 주별 매출액·주문수.
     * 라벨은 각 주의 시작일(월요일)을 "M/d"로 표시. 나머지 로직은 일간과 동일한 날짜 스핀 + LEFT JOIN 방식.
     */
    public List<VendorDailySalesDTO> getWeeklySalesStat(int sellerNo, Date targetDate) {

        List<VendorDailySalesDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                D.WEEK_START,
                NVL(S.SALES_AMOUNT, 0) AS SALES_AMOUNT,
                NVL(S.ORDER_COUNT, 0) AS ORDER_COUNT
            FROM (
                SELECT TRUNC(?, 'IW') - (LEVEL - 1) * 7 AS WEEK_START
                FROM DUAL
                CONNECT BY LEVEL <= 5
            ) D
            LEFT JOIN (
                SELECT
                    TRUNC(O.ORDER_DATE, 'IW') AS WEEK_START,
                    SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT,
                    COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT
                FROM ORDER_DETAIL OD
                    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                WHERE P.SELLER_NO = ?
                  AND O.ORDER_STATUS != '주문취소'
                  AND O.ORDER_DATE >= TRUNC(?, 'IW') - 28
                GROUP BY TRUNC(O.ORDER_DATE, 'IW')
            ) S ON S.WEEK_START = D.WEEK_START
            ORDER BY D.WEEK_START
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setDate(1, targetDate);
            pstmt.setInt(2, sellerNo);
            pstmt.setDate(3, targetDate);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    list.add(mapPeriodRow(rs, "WEEK_START", "M/d"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 매출 현황 차트(월간) - 기준일이 속한 달 포함 최근 5개월 월별 매출액·주문수.
     */
    public List<VendorDailySalesDTO> getMonthlySalesStat(int sellerNo, Date targetDate) {

        List<VendorDailySalesDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                D.MONTH_START,
                NVL(S.SALES_AMOUNT, 0) AS SALES_AMOUNT,
                NVL(S.ORDER_COUNT, 0) AS ORDER_COUNT
            FROM (
                SELECT ADD_MONTHS(TRUNC(?, 'MM'), -(LEVEL - 1)) AS MONTH_START
                FROM DUAL
                CONNECT BY LEVEL <= 5
            ) D
            LEFT JOIN (
                SELECT
                    TRUNC(O.ORDER_DATE, 'MM') AS MONTH_START,
                    SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT,
                    COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT
                FROM ORDER_DETAIL OD
                    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                WHERE P.SELLER_NO = ?
                  AND O.ORDER_STATUS != '주문취소'
                  AND O.ORDER_DATE >= ADD_MONTHS(TRUNC(?, 'MM'), -4)
                GROUP BY TRUNC(O.ORDER_DATE, 'MM')
            ) S ON S.MONTH_START = D.MONTH_START
            ORDER BY D.MONTH_START
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setDate(1, targetDate);
            pstmt.setInt(2, sellerNo);
            pstmt.setDate(3, targetDate);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    list.add(mapPeriodRow(rs, "MONTH_START", "M월"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private VendorDailySalesDTO mapDailyRow(ResultSet rs) throws java.sql.SQLException {
        return mapPeriodRow(rs, "STAT_DATE", "M/d");
    }

    private VendorDailySalesDTO mapPeriodRow(ResultSet rs, String dateColumn, String labelPattern) throws java.sql.SQLException {

        VendorDailySalesDTO dto = new VendorDailySalesDTO();

        dto.setLabel(rs.getDate(dateColumn).toLocalDate().format(DateTimeFormatter.ofPattern(labelPattern)));
        dto.setSalesAmount(rs.getLong("SALES_AMOUNT"));
        dto.setOrderCount(rs.getInt("ORDER_COUNT"));

        return dto;
    }
}
