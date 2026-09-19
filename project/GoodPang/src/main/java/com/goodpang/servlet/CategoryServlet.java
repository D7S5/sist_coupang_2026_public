package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.File;   // 2026-09-03 추가 — 타일 이미지 파일이 실제로 있는지 확인하는 데 씀

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.goodpang.dao.CategoryProductDAO;
import com.goodpang.dao.CategoryProductDAO.Sort;
import com.goodpang.dto.CategoryDTO;
import com.goodpang.dto.CategoryProductDTO;

// 2026-09-04 (flicker1016) — /category 는 이제 커맨드 패턴으로 처리함
// (web.xml -> controller.DispatcherServlet -> command.CategoryHandler -> service.CategoryService).
// 같은 URL 에 서블릿이 둘이면 톰캣 배포가 실패해서 이 줄만 주석처리함.
// 코드는 비교용으로 남겨둠 — 되돌리려면 아래 주석을 풀고 web.xml 의 dispatcher 매핑을 지우면 됨.
// @WebServlet("/category")
public class CategoryServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 60;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryNo = parseIntOrDefault(request.getParameter("categoryNo"), 10301);
        Sort sort = parseSortOrDefault(request.getParameter("sort"));
        int page = Math.max(1, parseIntOrDefault(request.getParameter("page"), 1));

        // 보기 개수 60/120 — 2026-09-03 실동작으로 변경(예전엔 "60개 고정" 확정이었는데 사용자 요청으로 정정).
        // 원본처럼 화이트리스트 두 개만 허용 — 그 외 값(잘못된 파라미터 조작 등)은 기본값 60으로
        int listSize = parseIntOrDefault(request.getParameter("listSize"), DEFAULT_PAGE_SIZE);
        if (listSize != 60 && listSize != 120) listSize = DEFAULT_PAGE_SIZE;

        // 가격대 필터 — 안 고르면 전체(0 ~ 최대) 그대로
        int minPrice = Math.max(0, parseIntOrDefault(request.getParameter("minPrice"), 0));
        int maxPrice = parseIntOrDefault(request.getParameter("maxPrice"), Integer.MAX_VALUE);
        if (maxPrice < minPrice) maxPrice = Integer.MAX_VALUE;

        // 평점 필터 — 원본처럼 "N점 이상"(0 = 전체)
        int minRating = Math.max(0, Math.min(5, parseIntOrDefault(request.getParameter("rating"), 0)));

        // 색상 필터 — 실제 쿠팡 원본을 다시 확인해보니(2026-09-01 Playwright 재확인) 색상도 다중선택이고,
        // 선택한 값들은 OR("Black 또는 White"), 다른 필터 그룹과는 AND. 그래서 String 하나가 아니라 List 로 받음
        // (color=Black&color=White 형태).
        // ★ 2026-09-01: 예전엔 여기서 getBytes("ISO-8859-1")로 재해석하는 방어 코드가 있었는데, 실제
        // 다중선택을 배포해서 확인해보니 "선택한 필터"에 한글이 "???"로 깨지고 체크 표시도 안 붙는
        // 버그였음 — 원인은 이 프로젝트가 jakarta.servlet(Tomcat 10 계열)이라 URIEncoding 기본값이 이미
        // UTF-8이라 request.getParameter()가 애초에 정상 한글을 돌려주는데, 거기다 대고 "깨졌을 것"이라며
        // ISO-8859-1로 재해석하면 매핑 안 되는 한글이 전부 '?'로 대체돼버림(Java 인코더 기본 동작).
        // 그래서 재해석 없이 받은 값을 그대로 씀. (이전 세션 진단은 재배포 확인 전의 추측이었음 — 틀렸던 것으로 결론)
        String[] colorParams = request.getParameterValues("color");
        List<String> colors = new ArrayList<>();
        if (colorParams != null) {
            for (String c : colorParams) {
                if (c == null || c.isBlank()) continue;
                colors.add(c);
            }
        }
        // JSP 에서 "이 색상이 선택됐는지" 를 EL 로 바로 물어볼 수 있게 Map 으로도 같이 넘김
        // (JSTL EL 은 List.contains() 를 직접 못 부르고, fn:contains 는 문자열 부분일치라 여기엔 안 맞음)
        Map<String, Boolean> selectedColorMap = new LinkedHashMap<>();
        for (String c : colors) {
            selectedColorMap.put(c, Boolean.TRUE);
        }

        CategoryProductDAO dao = new CategoryProductDAO();

        /*
         * 2026-09-03 추가 — 이 페이지가 중분류(레벨2)인지 소분류(레벨3)인지 여기서 갈린다.
         *
         * 쿠팡 원본도 /np/categories/502993(남녀 공용 의류)와 /np/categories/502994(티셔츠)가
         * 같은 주소 모양을 쓰고, 중분류일 때만 제목 아래에 카테고리 타일 + 배너가 더 붙는 구조.
         * 그래서 우리도 서블릿·JSP 를 새로 만들지 않고 레벨로만 갈라 쓴다.
         *
         * 없는 카테고리 번호면 current 가 null 이 되는데, 그때는 소분류처럼(타일 없이) 그린다.
         */
        CategoryDTO current = dao.findCategory(categoryNo);
        boolean isMidCategory = (current != null && current.getCategoryLevel() == 2);
        // 2026-09-03 추가 — 대분류(레벨1) 페이지 구분용. 사이드바 "카테고리"를 자식(중분류) 목록으로
        // 보여줘야 하는 게 중분류뿐 아니라 대분류도 해당돼서 따로 뺌(아래 sidebarCategories 참고)
        boolean isTopCategory = (current != null && current.getCategoryLevel() == 1);
        List<CategoryDTO> childCategories = dao.findChildCategories(categoryNo);

        List<CategoryProductDTO> products =
                dao.findByCategory(categoryNo, sort, minPrice, maxPrice, minRating, colors, page, listSize);
        int totalCount = dao.countByCategory(categoryNo, minPrice, maxPrice, minRating, colors);
        int totalPages = (int) Math.ceil(totalCount / (double) listSize);

        CategoryDTO[] breadcrumb = dao.findBreadcrumb(categoryNo);
        List<CategoryDTO> siblingCategories = dao.findSiblingCategories(categoryNo);
        List<String> colorOptions = dao.findColorOptionsByCategory(categoryNo);

        request.setAttribute("products", products);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("sort", sort.name());
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice == Integer.MAX_VALUE ? "" : maxPrice);
        request.setAttribute("rating", minRating);
        request.setAttribute("listSize", listSize);
        request.setAttribute("selectedColors", colors);
        request.setAttribute("selectedColorMap", selectedColorMap);

        request.setAttribute("categoryNo", categoryNo);
        request.setAttribute("breadcrumb", breadcrumb);          // [대분류, 중분류, 소분류]
        request.setAttribute("siblingCategories", siblingCategories);
        request.setAttribute("colorOptions", colorOptions);

        /*
         * 2026-09-03 추가 — 화면 제목(h1)과 <title> 에 쓸 이름.
         * 예전엔 JSP 가 breadcrumb[2].categoryName 으로 소분류 이름을 집어 썼는데, 브레드크럼 칸 수가
         * 레벨마다 달라져서(중분류는 2칸) [2]번이 없어 빈칸이 됐음. 그래서 이름은 따로 내려준다.
         */
        request.setAttribute("categoryName", current != null ? current.getCategoryName() : "");
        request.setAttribute("isMidCategory", isMidCategory);
        request.setAttribute("isTopCategory", isTopCategory);   // 2026-09-03 추가

        /*
         * 왼쪽 필터 사이드바의 "카테고리" 그룹 —
         *   대분류 페이지: 자식 목록(여성패션·남성패션 …)      ← 2026-09-03 추가, 원본도 이럼
         *   중분류 페이지: 자식 목록(티셔츠·맨투맨/후드티 …)   ← 원본 쿠팡도 이럼
         *   소분류 페이지: 예전 그대로 형제 목록
         * 맨 아래 "함께 본 카테고리"는 세 경우 다 형제(siblingCategories)를 계속 쓴다 — 이것도 원본과 같음.
         * (대분류는 findSiblingCategories() 를 NULL-세이프하게 고쳐서 "다른 대분류들"이 형제로 잡히게 함)
         */
        // request.setAttribute("sidebarCategories", isMidCategory ? childCategories : siblingCategories);
        request.setAttribute("sidebarCategories", (isMidCategory || isTopCategory) ? childCategories : siblingCategories);

        /*
         * 제목 아래 원형 타일 그리드에 쓸 목록.
         *
         * ★ "이미지 파일이 실제로 있는 카테고리만" 넣는다.
         *   쿠팡 원본도 남녀 공용 의류 밑에 카테고리가 12개인데 타일은 11개뿐이고(스포츠의류는 사이드바에만 있음),
         *   우리도 원본에서 가져온 타일 이미지가 11개뿐이라 같은 모양이 된다.
         *   나중에 스포츠의류 타일 이미지가 생기면 webapp/images/category/tile_10312.png 로 넣기만 하면
         *   코드는 안 고쳐도 타일이 12개로 늘어난다.
         */
        request.setAttribute("categoryTiles", isMidCategory ? tilesWithImage(childCategories) : null);

        /*
         * 대분류(레벨1) 제목 아래 "원형 타일" 이미지(l1_tiles.png)의 10칸을 우리 DB 카테고리와 잇는다
         * (2026-09-03 추가). 중분류 타일과 달리 이건 글자가 이미지에 박혀 있어서 칸 순서가 고정이다.
         */
        request.setAttribute("tileSlots", isTopCategory ? buildTileSlots(childCategories) : null);

        /* 실제 DB에 속성 컬럼 자체가 없는 필터들 — 화면엔 보여주되(원본과 동일 구성) 동작은 안 함(2026-08-30 확정).
         * 2026-08-31: 원본 실측(Playwright browser_evaluate, ref/category/STRUCTURE.md)한 순서 그대로 맞추려고
         * "색상 앞"/"색상 뒤" 두 그룹으로 나눔 — 원본은 카테고리→브랜드→상품상태→색상→핏→...→별점→가격 순서라
         * 색상(실제 동작)이 inert 그룹들 사이에 끼어있음. 별점/가격도 원본은 맨 끝이라 JSP에서 순서 맞춰 넣음. */
        request.setAttribute("beforeColorGroups", buildBeforeColorGroups());
        request.setAttribute("afterColorGroups", buildAfterColorGroups());
        // 2026-09-05: "필터" 제목 아래 배송 체크박스 줄(topFilterItems)은 여기서 안 내려줌 —
        // 원본을 다시 재보니 평평한 목록이 아니라 "로켓 전체" 밑에 3줄이 들어가는 2단 구조여서,
        // 문자열 배열로는 표현이 안 됨. DB 연동이 없는 장식용 줄이라 category_list.jsp 안에 직접 적어둠

        // 배송예정일 — ProductServlet(상세페이지)과 완전히 같은 계산식 재사용(2026-09-02 추가).
        // 실제 배송정보 컬럼(SHIPPING_FEE_TYPE/DELIVERY_METHOD/LEAD_TIME_DAYS)을 아직 어디서도 안 써서
        // (CLAUDE.md "지금 하는 일" 4번 미해결) 카드마다 다르게는 못 보여줌 — "내일(요일) M/d 도착 예정"을
        // 모든 카드에 똑같이 씀. 실제 컬럼 연동되면 그때 상품별로 갈라줄 것
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        // 2026-09-05 재실측: 원본은 로켓 상품일 때 날짜(M/d) 없이 "내일(목) 도착 보장" 이라고만 씀
        // (날짜가 붙는 건 "9/5(토) 도착 예정" 처럼 로켓이 아닌 상품 쪽). "도착 보장" 글자는 JSP 에 있음
        String deliveryDate = "내일(" + dayOfWeek + ")";
        request.setAttribute("deliveryDate", deliveryDate);

        request.getRequestDispatcher("/WEB-INF/views/category_list.jsp").forward(request, response);
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

    /*
     * JSP EL 은 리스트 리터럴(${['a','b']}) 을 못 써서, 화면에만 쓰이는 정적 필터 목록은
     * 서블릿에서 Map 으로 만들어 넘기고 JSP 는 <c:forEach> 로 그냥 돌기만 함.
     * (2026-08-31 STRUCTURE.md 3장 표를 그대로 옮김 — 카테고리/평점/가격/색상은 이미 위에서 따로 처리하니 제외)
     */
    // 원본 실측 순서: 카테고리 → 브랜드 → 상품상태 → (색상, JSP에서 별도 처리) → 핏 → ...
    private Map<String, String[]> buildBeforeColorGroups() {
        Map<String, String[]> groups = new LinkedHashMap<>();
        groups.put("브랜드", new String[] { "나이키", "노스페이스", "뉴발란스", "지프" });
        groups.put("상품 상태", new String[] { "새 상품", "박스 훼손", "반품" });
        return groups;
    }

    // 원본 실측 순서: (색상) → 핏 → 사용대상 → 소재 → 네크라인 → 사용계절 → 소매길이 → 길이 →
    // 패턴/프린트 → 출시년도 → 제조년도 → 출시 계절 → 세탁방법 → 상하의세트 여부 → 스타일 → (별점/가격은 JSP에서 별도)
    private Map<String, String[]> buildAfterColorGroups() {
        Map<String, String[]> groups = new LinkedHashMap<>();
        groups.put("핏", new String[] { "슬림", "일반", "오버사이즈" });
        groups.put("사용대상", new String[] { "남성용", "여성용", "남녀공용", "아동·유아용" });
        // 2026-09-03: 아래 소재/네크라인/패턴·프린트/제조년도 4개는 원본이 "5개 초과 → +더보기" 로 접기 때문에
        // (coupang.com 실측, 5개까지만 보이고 6번째부터 접힘) 실제로 접히는 걸 눈으로 볼 수 있게
        // 항목 수를 실측값 그대로 채움. 나머지(핏/사용대상 등)는 원본도 5개 이하라 그대로 둠
        groups.put("소재", new String[] {
                "면 100%", "니트", "면혼방", "린넨", "레이온", "폴리에스터/나일론",
                "울/모직", "가죽", "인조가죽 (합성피혁)", "인조퍼", "기타 합성 섬유", "기모", "스판덱스", "아크릴", "캐시미어" });
        groups.put("네크라인", new String[] {
                "라운드넥", "브이넥", "헨리넥 (라운드넥+버튼)", "터틀넥/폴라", "일반 칼라", "버튼다운 칼라", "반집업 칼라" });
        groups.put("사용계절", new String[] { "사계절용", "봄가을용", "여름용", "겨울용" });
        groups.put("소매길이", new String[] { "민소매", "반소매", "7부소매", "긴소매" });
        groups.put("길이", new String[] { "숏/크롭", "기본", "롱" });
        groups.put("패턴/프린트", new String[] {
                "단색", "스트라이프", "도트", "체크/격자", "플라워",
                "밀리터리", "헤링본/기하학", "애니멀", "페이즐리/에스닉", "트로피칼/과일", "레터링" });
        groups.put("출시년도", new String[] { "2023", "2022", "2021", "2020" });
        groups.put("제조년도", new String[] { "2023", "2022", "2021", "2020", "2019", "2018", "2017 이전" });
        groups.put("출시 계절", new String[] { "봄", "여름", "가을", "겨울" });
        groups.put("세탁방법", new String[] { "손세탁권장", "세탁기사용가능", "드라이클리닝", "세탁불가" });
        groups.put("상하의세트 여부", new String[] { "상의", "하의", "상하의세트" });
        groups.put("스타일", new String[] { "캐주얼", "홈웨어", "오피스", "스포티" });
        return groups;
    }

    /*
     * 타일 이미지가 실제로 있는 카테고리만 걸러낸다 (2026-09-03 추가).
     *
     * getRealPath() 는 "웹에서 보이는 경로(/images/...)"를 "하드디스크의 진짜 경로(C:\\...)"로 바꿔준다.
     * 그래야 File.exists() 로 파일이 있는지 확인할 수 있다.
     * 파일이 없는 카테고리는 타일에서 빼고, 왼쪽 사이드바 목록에는 그대로 남는다(원본과 같은 동작).
     */
    /*
     * 대분류 타일 이미지(l1_tiles.png)의 10칸 순서 — 이미지에 글자가 박혀 있어서 순서를 바꿀 수 없다.
     *   윗줄  : 여성 / 남성 / 남녀공용 / 속옷·잠옷 / 신발
     *   아랫줄 : 가방·잡화 / 유아동 / C.에비뉴 / C.스트리트 / R.LUX
     *
     * 각 칸에 "우리 DB 카테고리 이름에 이 낱말이 들어 있으면 거기로 연결" 이라는 키워드만 적어둔다.
     * 카테고리 번호를 여기 직접 적지 않는 이유: 번호는 DB 에 있는 값이라 팀에서 바뀔 수 있고,
     * 이름으로 찾으면 카테고리가 바뀌어도 코드를 안 고쳐도 되기 때문.
     * null 인 칸(C.에비뉴/C.스트리트)은 우리한테 해당 카테고리가 없어서 링크를 안 만든다 —
     * 없는 페이지로 보내면 안 되니까. "신발"/"가방" 도 DB 에 없으면 자동으로 링크가 안 생긴다.
     */
    private static final String[] TILE_KEYWORDS = {
            "여성", "남성", "남녀", "속옷", "신발",
            "가방", "유아동", null, null, "럭셔리"
    };

    /*
     * 타일 10칸 각각에 붙일 카테고리를 찾아준다 (2026-09-03 추가).
     * 찾은 게 없으면 categoryNo 가 0 이고, JSP 는 0 이면 링크 없는 빈 칸으로 그린다.
     * (칸 위치는 CSS 가 :nth-child 로 잡으므로 없는 칸도 자리는 그대로 둬야 함)
     */
    private List<Map<String, Object>> buildTileSlots(List<CategoryDTO> children) {
        List<Map<String, Object>> slots = new ArrayList<>();

        for (String keyword : TILE_KEYWORDS) {
            Map<String, Object> slot = new LinkedHashMap<>();
            long categoryNo = 0;
            String categoryName = "";

            if (keyword != null && children != null) {
                for (CategoryDTO child : children) {
                    String name = child.getCategoryName();
                    if (name != null && name.contains(keyword)) {
                        categoryNo = child.getCategoryNo();
                        categoryName = name;
                        break;
                    }
                }
            }

            slot.put("categoryNo", categoryNo);
            slot.put("categoryName", categoryName);
            slots.add(slot);
        }

        return slots;
    }

    private List<CategoryDTO> tilesWithImage(List<CategoryDTO> children) {
        List<CategoryDTO> tiles = new ArrayList<>();
        for (CategoryDTO child : children) {
            String realPath = getServletContext()
                    .getRealPath("/images/category/tile_" + child.getCategoryNo() + ".png");
            if (realPath != null && new File(realPath).exists()) {
                tiles.add(child);
            }
        }
        return tiles;
    }
}
