package com.goodpang.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.goodpang.dao.CategoryProductDAO;
import com.goodpang.dao.CategoryProductDAO.Sort;
import com.goodpang.dto.CategoryDTO;
import com.goodpang.dto.CategoryProductDTO;

import jakarta.servlet.ServletContext;

/**
 * 카테고리 목록페이지의 업무 로직 담당 (Service 계층).
 *
 * 원래 CategoryServlet.doGet() 안에 있던 DB 조회 + 계산 + 화면용 정적 목록을 여기로 옮김.
 * Handler 는 파라미터만 읽어서 이 클래스를 부르고 request 에 담기만 함.
 *
 * ※ CategoryServlet 은 아직 원본 그대로 살아있음. 이 클래스는 그 로직을 옮겨 담은 것.
 */
public class CategoryService {

    private final CategoryProductDAO dao = new CategoryProductDAO();

    // ─────────────────────────────── 조회 ───────────────────────────────

    /** 현재 카테고리 1건. 없는 번호면 null */
    public CategoryDTO findCategory(int categoryNo) {
        return dao.findCategory(categoryNo);
    }

    /** 바로 아래 자식 카테고리들 */
    public List<CategoryDTO> findChildCategories(int categoryNo) {
        return dao.findChildCategories(categoryNo);
    }

    /** 이 카테고리(및 하위 전체)의 상품 목록 — 정렬/필터/페이지 적용 */
    public List<CategoryProductDTO> findProducts(int categoryNo, Sort sort, int minPrice, int maxPrice,
            int minRating, List<String> colors, int page, int listSize) {
        return dao.findByCategory(categoryNo, sort, minPrice, maxPrice, minRating, colors, page, listSize);
    }

    /** 필터까지 적용한 전체 상품 수 (페이지 계산용) */
    public int countProducts(int categoryNo, int minPrice, int maxPrice, int minRating, List<String> colors) {
        return dao.countByCategory(categoryNo, minPrice, maxPrice, minRating, colors);
    }

    /** [대분류, 중분류, 소분류] 순서의 브레드크럼 */
    public CategoryDTO[] findBreadcrumb(int categoryNo) {
        return dao.findBreadcrumb(categoryNo);
    }

    /** 같은 부모를 가진 형제 카테고리들 */
    public List<CategoryDTO> findSiblingCategories(int categoryNo) {
        return dao.findSiblingCategories(categoryNo);
    }

    /** 색상 필터에 띄울 색상 목록 */
    public List<String> findColorOptions(int categoryNo) {
        return dao.findColorOptionsByCategory(categoryNo);
    }

    // ─────────────────────────────── 계산 ───────────────────────────────

    /** 전체 페이지 수 */
    public int calcTotalPages(int totalCount, int listSize) {
        return (int) Math.ceil(totalCount / (double) listSize);
    }

    /** 중분류(레벨2) 페이지인지 — 제목 아래 원형 타일 + 배너가 더 붙는다 */
    public boolean isMidCategory(CategoryDTO current) {
        return current != null && current.getCategoryLevel() == 2;
    }

    /** 대분류(레벨1) 페이지인지 — 사이드바 "카테고리"를 자식 목록으로 보여줘야 함 */
    public boolean isTopCategory(CategoryDTO current) {
        return current != null && current.getCategoryLevel() == 1;
    }

    /*
     * 배너/타일 이미지를 실제로 준비해둔 카테고리인지 (2026-09-06 추가).
     *
     * 대분류 배너(l1_hero_*, l1_tiles, l1_band_*, l1_brand_*)와 중분류 배너(banner_promo*)는
     * 전부 쿠팡 "패션의류/잡화" 페이지에서 캡처한 그림임. 특히 l1_tiles.png 는
     * "여성/남성/속옷·잠옷…" 글자가 그림 안에 박혀 있어서 다른 카테고리에 쓸 수가 없음.
     * 그런데 레벨로만 갈라서(isTopCategory) 뷰티·식품 대분류에도 패션 배너가 그대로 나오고 있었음
     * → 자료가 있는 카테고리에서만 그리게 막음. 다른 대분류 배너를 만들면 여기 번호만 추가하면 됨.
     */
    private static final Set<Long> BANNER_READY_TOP_CATEGORIES = Set.of(1L);   // 1 = 패션의류/잡화

    /** 대분류(레벨1) 중 배너 자료가 있는 것인지 — 없으면 제목+상품목록만 나온다 */
    public boolean hasTopBanners(CategoryDTO current) {
        return isTopCategory(current)
                && BANNER_READY_TOP_CATEGORIES.contains(current.getCategoryNo());
    }

    /** 중분류(레벨2) 배너 4장 — 패션 밑 중분류에서만 (원형 타일은 파일 유무로 이미 걸러짐) */
    public boolean hasMidBanners(CategoryDTO current) {
        return isMidCategory(current)
                && current.getParentCategoryNo() != null
                && BANNER_READY_TOP_CATEGORIES.contains(current.getParentCategoryNo());
    }

    /**
     * JSP 에서 "이 색상이 선택됐는지" 를 EL 로 바로 물어볼 수 있게 Map 으로 만들어줌.
     * (JSTL EL 은 List.contains() 를 직접 못 부르고, fn:contains 는 문자열 부분일치라 여기엔 안 맞음)
     */
    public Map<String, Boolean> buildSelectedColorMap(List<String> colors) {
        Map<String, Boolean> selectedColorMap = new LinkedHashMap<>();
        for (String c : colors) {
            selectedColorMap.put(c, Boolean.TRUE);
        }
        return selectedColorMap;
    }

    /**
     * 배송예정일 — 상세페이지(ProductService)와 달리 날짜(M/d)를 안 붙인다.
     * 2026-09-05 재실측: 원본은 로켓 상품일 때 "내일(목) 도착 보장" 이라고만 씀
     * (날짜가 붙는 건 "9/5(토) 도착 예정" 처럼 로켓이 아닌 상품 쪽). "도착 보장" 글자는 JSP 에 있음.
     */
    public String calcDeliveryDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        return "내일(" + dayOfWeek + ")";
    }

    // ───────────────────────── 화면용 정적 목록 ─────────────────────────

    /*
     * JSP EL 은 리스트 리터럴(${['a','b']}) 을 못 써서, 화면에만 쓰이는 정적 필터 목록은
     * 여기서 Map 으로 만들어 넘기고 JSP 는 <c:forEach> 로 그냥 돌기만 함.
     * 실제 DB에 속성 컬럼 자체가 없는 필터들 — 화면엔 보여주되 동작은 안 함(2026-08-30 확정).
     * 원본 실측(Playwright, ref/category/STRUCTURE.md) 순서가
     * 카테고리→브랜드→상품상태→색상→핏→...→별점→가격 이라 색상(실제 동작)이 inert 그룹들 사이에
     * 끼어있어서 "색상 앞"/"색상 뒤" 두 그룹으로 나눔.
     */
    public Map<String, String[]> buildBeforeColorGroups() {
        Map<String, String[]> groups = new LinkedHashMap<>();
        groups.put("브랜드", new String[] { "나이키", "노스페이스", "뉴발란스", "지프" });
        groups.put("상품 상태", new String[] { "새 상품", "박스 훼손", "반품" });
        return groups;
    }

    // 원본 실측 순서: (색상) → 핏 → 사용대상 → 소재 → 네크라인 → 사용계절 → 소매길이 → 길이 →
    // 패턴/프린트 → 출시년도 → 제조년도 → 출시 계절 → 세탁방법 → 상하의세트 여부 → 스타일 → (별점/가격은 JSP에서 별도)
    public Map<String, String[]> buildAfterColorGroups() {
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

    // ─────────────────────────────── 타일 ───────────────────────────────

    /*
     * 대분류 타일 이미지(l1_tiles.png)의 10칸 순서 — 이미지에 글자가 박혀 있어서 순서를 바꿀 수 없다.
     *   윗줄  : 여성 / 남성 / 남녀공용 / 속옷·잠옷 / 신발
     *   아랫줄 : 가방·잡화 / 유아동 / C.에비뉴 / C.스트리트 / R.LUX
     *
     * 각 칸에 "우리 DB 카테고리 이름에 이 낱말이 들어 있으면 거기로 연결" 이라는 키워드만 적어둔다.
     * 카테고리 번호를 여기 직접 적지 않는 이유: 번호는 DB 에 있는 값이라 팀에서 바뀔 수 있고,
     * 이름으로 찾으면 카테고리가 바뀌어도 코드를 안 고쳐도 되기 때문.
     * null 인 칸(C.에비뉴/C.스트리트)은 우리한테 해당 카테고리가 없어서 링크를 안 만든다.
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
    public List<Map<String, Object>> buildTileSlots(List<CategoryDTO> children) {
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

    /*
     * 타일 이미지가 실제로 있는 카테고리만 걸러낸다 (2026-09-03 추가).
     *
     * getRealPath() 는 "웹에서 보이는 경로(/images/...)"를 "하드디스크의 진짜 경로(C:\\...)"로 바꿔준다.
     * 그래야 File.exists() 로 파일이 있는지 확인할 수 있다.
     * 파일이 없는 카테고리는 타일에서 빼고, 왼쪽 사이드바 목록에는 그대로 남는다(원본과 같은 동작).
     *
     * ※ 서블릿이 아니라 Service 라 getServletContext() 를 직접 못 부름 —
     *   Handler 가 request.getServletContext() 로 받아서 넘겨줌.
     */
    public List<CategoryDTO> tilesWithImage(List<CategoryDTO> children, ServletContext context) {
        List<CategoryDTO> tiles = new ArrayList<>();
        for (CategoryDTO child : children) {
            String realPath = context.getRealPath("/images/category/tile_" + child.getCategoryNo() + ".png");
            if (realPath != null && new File(realPath).exists()) {
                tiles.add(child);
            }
        }
        return tiles;
    }
}
