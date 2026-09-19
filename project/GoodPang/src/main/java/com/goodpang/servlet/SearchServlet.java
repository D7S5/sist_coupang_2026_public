package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.goodpang.dao.CategoryProductDAO.Sort;
import com.goodpang.dao.SearchDAO;
import com.goodpang.dto.CategoryProductDTO;

/**
 * 검색 결과 페이지(/search?keyword=...) - 상품명에 검색어가 포함된 상품 목록을 보여준다.
 * CategoryServlet과 같은 구조(파라미터 파싱 → DAO 조회 → JSP forward)를 그대로 따른다.
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    private static final int PAGE_SIZE = 60;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        keyword = (keyword == null) ? null : keyword.trim();

        int page = Math.max(1, parseIntOrDefault(request.getParameter("page"), 1));
        Sort sort = parseSortOrDefault(request.getParameter("sort"));

        List<CategoryProductDTO> products;
        int totalCount;

        if (keyword == null || keyword.isBlank()) {
            products = List.of();
            totalCount = 0;
        } else {
            SearchDAO dao = new SearchDAO();
            products = dao.findByKeyword(keyword, sort, page, PAGE_SIZE);
            totalCount = dao.countByKeyword(keyword);
        }

        int totalPages = (int) Math.ceil(totalCount / (double) PAGE_SIZE);

        request.setAttribute("keyword", keyword);
        request.setAttribute("products", products);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("sort", sort.name());

        // 상품 카드의 배송문구 — ProductServlet/CategoryServlet과 같은 계산식 재사용
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        request.setAttribute("deliveryDate", "내일(" + dayOfWeek + ")");

        request.getRequestDispatcher("/WEB-INF/views/search_list.jsp").forward(request, response);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // 검색은 '쿠팡랭킹순'(RANKING)을 지원하지 않으므로 그 값이 오면 최신순으로 대체
    private Sort parseSortOrDefault(String value) {
        if (value == null) return Sort.LATEST;
        try {
            Sort sort = Sort.valueOf(value.trim().toUpperCase());
            return sort == Sort.RANKING ? Sort.LATEST : sort;
        } catch (IllegalArgumentException e) {
            return Sort.LATEST;
        }
    }

}
