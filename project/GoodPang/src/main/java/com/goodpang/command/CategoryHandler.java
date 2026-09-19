package com.goodpang.command;

import java.util.ArrayList;
import java.util.List;

import com.goodpang.dao.CategoryProductDAO.Sort;
import com.goodpang.dto.CategoryDTO;
import com.goodpang.dto.CategoryProductDTO;
import com.goodpang.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /category 요청 처리 — 원래 CategoryServlet.doGet() 이 하던 일.
 *
 * 여기서는 파라미터를 읽고 CategoryService 를 부른 뒤 request 에 담기만 함.
 * DB 조회와 계산은 전부 CategoryService 안에 있음(controller -> service -> persistence).
 *
 * 소분류(레벨3)/중분류(레벨2)/대분류(레벨1)가 같은 주소·같은 JSP 를 쓰고,
 * CATEGORY_LEVEL 로만 화면 모양이 갈린다 — 상품 범위는 DAO 의 CONNECT BY 로 통일돼 있어서 분기가 없음.
 *
 * ※ CategoryServlet 은 아직 원본 그대로 살아있음.
 */
public class CategoryHandler implements CommandHandler {

    private static final int DEFAULT_PAGE_SIZE = 60;

    private final CategoryService service = new CategoryService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // int categoryNo = parseIntOrDefault(request.getParameter("categoryNo"), 10301);  // ← 2026-09-06 아래로 교체

        // 카테고리 번호도 주소 두 형태를 다 받음 (ProductHandler 와 같은 방식)
        //  ① /category/10301        ② /category?categoryNo=10301 (옛 주소, 팀원 링크 15곳)
        // 정렬/페이징/필터(sort, page, listSize, minPrice, color ...)는 그대로 쿼리스트링으로 둠 —
        // 쿠팡도 /np/categories/194276?sorter=... 처럼 "무엇을 보는가"만 경로로 씀
        String categoryNoParam = request.getParameter("categoryNo");

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            categoryNoParam = pathInfo.substring(1);
            int slash = categoryNoParam.indexOf('/');
            if (slash > -1) {
                categoryNoParam = categoryNoParam.substring(0, slash);
            }
        }

        int categoryNo = parseIntOrDefault(categoryNoParam, 10301);
        Sort sort = parseSortOrDefault(request.getParameter("sort"));
        int page = Math.max(1, parseIntOrDefault(request.getParameter("page"), 1));

        // 보기 개수 60/120 — 원본처럼 화이트리스트 두 개만 허용
        // (그 외 값, 즉 잘못된 파라미터 조작 등은 기본값 60으로)
        int listSize = parseIntOrDefault(request.getParameter("listSize"), DEFAULT_PAGE_SIZE);
        if (listSize != 60 && listSize != 120) listSize = DEFAULT_PAGE_SIZE;

        // 가격대 필터 — 안 고르면 전체(0 ~ 최대) 그대로
        int minPrice = Math.max(0, parseIntOrDefault(request.getParameter("minPrice"), 0));
        int maxPrice = parseIntOrDefault(request.getParameter("maxPrice"), Integer.MAX_VALUE);
        if (maxPrice < minPrice) maxPrice = Integer.MAX_VALUE;

        // 평점 필터 — 원본처럼 "N점 이상"(0 = 전체)
        int minRating = Math.max(0, Math.min(5, parseIntOrDefault(request.getParameter("rating"), 0)));

        // 색상 필터 — 색상도 다중선택(color=Black&color=White)이라 String 하나가 아니라 List 로 받음.
        // 선택한 값들끼리는 OR, 다른 필터 그룹과는 AND.
        // ★ 여기서 getBytes("ISO-8859-1") 로 재해석하면 안 됨 — 이 프로젝트는 jakarta.servlet(Tomcat 10)이라
        //   URIEncoding 기본값이 이미 UTF-8 이고, 정상 한글을 다시 해석하면 '?' 로 깨짐(2026-09-01 확인)
        String[] colorParams = request.getParameterValues("color");
        List<String> colors = new ArrayList<>();
        if (colorParams != null) {
            for (String c : colorParams) {
                if (c == null || c.isBlank()) continue;
                colors.add(c);
            }
        }

        // 없는 카테고리 번호면 current 가 null — 그때는 소분류처럼(타일 없이) 그린다
        CategoryDTO current = service.findCategory(categoryNo);
        boolean isMidCategory = service.isMidCategory(current);
        boolean isTopCategory = service.isTopCategory(current);
        // 2026-09-06: 배너 이미지가 패션 전용 캡처라 "레벨"과 "배너 자료 유무"를 따로 판정
        boolean hasTopBanners = service.hasTopBanners(current);
        boolean hasMidBanners = service.hasMidBanners(current);
        List<CategoryDTO> childCategories = service.findChildCategories(categoryNo);

        List<CategoryProductDTO> products =
                service.findProducts(categoryNo, sort, minPrice, maxPrice, minRating, colors, page, listSize);
        int totalCount = service.countProducts(categoryNo, minPrice, maxPrice, minRating, colors);

        request.setAttribute("products", products);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", service.calcTotalPages(totalCount, listSize));
        request.setAttribute("page", page);
        request.setAttribute("sort", sort.name());
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice == Integer.MAX_VALUE ? "" : maxPrice);
        request.setAttribute("rating", minRating);
        request.setAttribute("listSize", listSize);
        request.setAttribute("selectedColors", colors);
        request.setAttribute("selectedColorMap", service.buildSelectedColorMap(colors));

        // 형제 목록은 아래 sidebarCategories 에서도 쓰므로 한 번만 조회해서 재사용
        List<CategoryDTO> siblingCategories = service.findSiblingCategories(categoryNo);

        request.setAttribute("categoryNo", categoryNo);
        request.setAttribute("breadcrumb", service.findBreadcrumb(categoryNo));   // [대분류, 중분류, 소분류]
        request.setAttribute("siblingCategories", siblingCategories);
        request.setAttribute("colorOptions", service.findColorOptions(categoryNo));

        // 화면 제목(h1)과 <title> 에 쓸 이름 — 브레드크럼 칸 수가 레벨마다 달라서 따로 내려줌
        request.setAttribute("categoryName", current != null ? current.getCategoryName() : "");
        request.setAttribute("isMidCategory", isMidCategory);
        request.setAttribute("isTopCategory", isTopCategory);
        request.setAttribute("hasTopBanners", hasTopBanners);
        request.setAttribute("hasMidBanners", hasMidBanners);

        /*
         * 왼쪽 필터 사이드바의 "카테고리" 그룹 —
         *   대분류 페이지: 자식 목록(여성패션·남성패션 …)
         *   중분류 페이지: 자식 목록(티셔츠·맨투맨/후드티 …)
         *   소분류 페이지: 예전 그대로 형제 목록
         * 맨 아래 "함께 본 카테고리"는 세 경우 다 형제(siblingCategories)를 계속 씀 — 원본과 같음.
         */
        request.setAttribute("sidebarCategories",
                (isMidCategory || isTopCategory) ? childCategories : siblingCategories);

        // 중분류 제목 아래 원형 타일 — 타일 이미지 파일이 실제로 있는 카테고리만 나옴
        request.setAttribute("categoryTiles",
                isMidCategory ? service.tilesWithImage(childCategories, request.getServletContext()) : null);

        // 대분류 제목 아래 원형 타일(l1_tiles.png)의 10칸 — 이미지에 글자가 박혀 있어 순서 고정.
        // 타일 좌표는 그 배너 이미지에 딸린 것이라 배너를 안 그리는 대분류에서는 만들 필요도 없음(2026-09-06)
        request.setAttribute("tileSlots",
                hasTopBanners ? service.buildTileSlots(childCategories) : null);

        // 실제 DB에 속성 컬럼이 없는 장식용 필터 그룹들 (화면엔 보여주되 동작은 안 함)
        request.setAttribute("beforeColorGroups", service.buildBeforeColorGroups());
        request.setAttribute("afterColorGroups", service.buildAfterColorGroups());

        request.setAttribute("deliveryDate", service.calcDeliveryDate());

        return "/WEB-INF/views/category_list.jsp";
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Sort parseSortOrDefault(String value) {
        if (value == null) return Sort.LATEST;
        try {
            return Sort.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Sort.LATEST;
        }
    }
}
