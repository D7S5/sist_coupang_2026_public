package com.goodpang.service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.goodpang.dao.ProductDAO;
import com.goodpang.dao.ProductImageDAO;
import com.goodpang.dao.ProductOptionDAO;
import com.goodpang.dao.ProductViewLogDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.ProductDTO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.dto.ReviewDTO;
import com.goodpang.dto.ReviewRatingSummaryDTO;
import com.goodpang.util.ImageUrl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 상품 상세페이지의 업무 로직 담당 (Service 계층).
 *
 * 원래 ProductServlet.doGet() 안에 DB 조회와 계산이 뒤섞여 있던 것을 여기로 옮김.
 * Handler(=컨트롤러 쪽)는 이 클래스의 메서드를 순서대로 부르고 request 에 담기만 하고,
 * DAO(=Persistence)는 이 클래스만 호출함 — controller -> service -> persistence 순서를 지키려고.
 *
 * ※ ProductServlet 은 아직 살아있음(원본 그대로). 이 클래스는 그 로직을 옮겨 담은 것.
 *
 * ★ 원 작성자 표기 — 상세페이지는 여러 명이 같이 만든 화면이라 이 클래스에도 팀원 코드가 섞여 있음
 *   (git blame 기준). 기능을 새로 짠 게 아니라 위치만 옮긴 것이라 원 작성자를 남겨둠:
 *     - 조회 로그(logView)                    : andy
 *     - 리뷰 별점 요약 / 리뷰 사진(getRatingSummary, getReviewsWithImages) : jihoonlee
 *     - 그 외(가격/할인율/적립금/옵션선택/배송예정일/사진분류/평균평점)       : flicker1016
 */
public class ProductService {

    private static final Gson gson = new Gson();

    /** 적립 혜택 비율 — 실제 적립 정책 테이블이 없어서 판매가의 5% 로 임의 계산 중 */
    private static final int REWARD_RATE_PERCENT = 5;

    private final ProductDAO productDAO = new ProductDAO();
    private final ProductOptionDAO optionDAO = new ProductOptionDAO();
    private final ProductImageDAO imageDAO = new ProductImageDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final ProductViewLogDAO viewLogDAO = new ProductViewLogDAO();

    // ─────────────────────────────── 조회 ───────────────────────────────

    /** 상품 1건. 없으면 null (Handler 가 404 로 처리) */
    public ProductDTO getProduct(int productNo) throws Exception {
        return productDAO.selectProduct(productNo);
    }

    /**
     * 판매자 대시보드 "오늘 방문자수/상품노출수" 집계용 조회 로그.
     * ★ 원래 ProductServlet 63~65줄에 있던 andy 작성 코드 — 기능은 그대로 두고 위치만 옮김.
     */
    public void logView(int productNo, Integer memberNo, String sessionId) throws Exception {
        viewLogDAO.logView(productNo, memberNo, sessionId);
    }

    /** 이 상품의 옵션 전체 (OPTION_ID 순) */
    public List<ProductOptionDTO> getOptions(int productNo) throws Exception {
        return optionDAO.selectOptionsByProductNo(productNo);
    }

    /**
     * 리뷰 목록 + 리뷰별 사진까지 채워서 반환.
     * ★ 리뷰별 사진 채우는 부분은 원래 ProductServlet 188~195줄에 있던 jihoonlee 작성 코드 —
     *   기능은 그대로 두고 위치만 옮김.
     */
    public List<ReviewDTO> getReviewsWithImages(int productNo) throws Exception {
        List<ReviewDTO> reviews = reviewDAO.selectReviewsByProductNo(productNo);

        for (ReviewDTO review : reviews) {
            review.setImageUrls(reviewDAO.getReviewImages(review.getReviewNo()));
        }
        return reviews;
    }

    /**
     * 별점 요약(평균/개수/분포).
     * ★ 원래 ProductServlet 170~186줄에 있던 jihoonlee 작성 코드 — 기능은 그대로 두고 위치만 옮김.
     */
    public ReviewRatingSummaryDTO getRatingSummary(int productNo) throws Exception {
        return reviewDAO.getRatingSummary(productNo);
    }

    // ─────────────────────────────── 계산 ───────────────────────────────

    /** 배송예정일 — "내일(요일) M/d" 형태로 매번 계산 */
    public String calcDeliveryDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        return "내일(" + dayOfWeek + ") " + tomorrow.getMonthValue() + "/" + tomorrow.getDayOfMonth();
    }

    /*
     * 2026-09-01 추가 — 옵션을 바꾸고 새로고침해도 그 옵션이 그대로 선택돼 있게.
     * 실제 쿠팡을 Playwright 로 확인해보니, 옵션을 바꾸면 주소(itemId/vendorItemId)가 그 자리에서
     * 바뀌고(js/product.js updateFromSelects 의 history.replaceState), 그 주소로 새로고침하면
     * 서버가 그 값을 보고 그 옵션으로 다시 그려줌 — 같은 방식을 씀.
     * URL 의 optionId 가 없거나, 있어도 이 상품 옵션 목록에 없으면(다른 상품 값을 끼워넣거나
     * 조작한 경우) 조용히 무시하고 첫 옵션으로 감 — DB 를 한 번 더 안 물어보고 이미 불러온
     * options 안에서만 찾음(그래야 "이 상품 소속"인지도 같이 검증됨).
     */
    public ProductOptionDTO chooseOption(List<ProductOptionDTO> options, String optionIdParam) {
        if (optionIdParam != null) {
            try {
                int wantedOptionId = Integer.parseInt(optionIdParam);
                for (ProductOptionDTO option : options) {
                    if (option.getOptionId() == wantedOptionId) {
                        return option;
                    }
                }
            } catch (NumberFormatException e) {
                // optionId 가 숫자가 아니면 무시 — 아래에서 첫 옵션으로 fallback
            }
        }
        return options.isEmpty() ? null : options.get(0);
    }

    /*
     * 2026-08-30 확정 — PRODUCT_OPTION.PRICE/NORMAL_PRICE 는 PRODUCT.PRODUCT_PRICE(기본가)에
     * 더해지는 "추가금"(CartDAO.getCartItems() 와 같은 전제). 그래서:
     *   판매가 총액 = PRODUCT_PRICE + 옵션의 PRICE       (할인된 추가금)
     *   정상가 총액 = PRODUCT_PRICE + 옵션의 NORMAL_PRICE (할인 전 추가금, 선택 입력이라 없을 수 있음)
     */
    public int calcDisplayPrice(ProductDTO product, ProductOptionDTO selectedOption) {
        if (selectedOption == null) {
            return product.getProductPrice();
        }
        return product.getProductPrice() + selectedOption.getPrice();
    }

    /**
     * 할인 전 정상가. 정상가 총액이 판매가 총액보다 클 때만 "할인 중"으로 보고 값을 돌려줌
     * (그 외에는 null → 화면에서 취소선/할인율을 안 보여줌).
     * 지금은 NORMAL_PRICE 를 채운 판매자가 없어서 실제로는 거의 null 인데,
     * 판매자가 정상가를 입력하기 시작하면 코드 수정 없이 자동으로 뜨게 됨.
     */
    public Integer calcDisplayNormalPrice(ProductDTO product, ProductOptionDTO selectedOption, int displayPrice) {
        if (selectedOption == null || selectedOption.getNormalPrice() == null) {
            return null;
        }
        int normalTotal = product.getProductPrice() + selectedOption.getNormalPrice();
        return normalTotal > displayPrice ? normalTotal : null;
    }

    /** 할인율(%) — 정상가가 없으면 0 */
    public int calcDiscountRate(int displayPrice, Integer displayNormalPrice) {
        if (displayNormalPrice == null || displayNormalPrice <= 0) {
            return 0;
        }
        return (int) Math.round((1 - (double) displayPrice / displayNormalPrice) * 100);
    }

    /** 적립 혜택 — 판매가의 5% (옵션 추가금까지 더한 가격 기준) */
    public int calcRewardCash(int displayPrice) {
        return displayPrice * REWARD_RATE_PERCENT / 100;
    }

    /**
     * 평균 평점 — 이미 읽어온 reviews 리스트에서 바로 계산(DB 재조회 없음), 소수 첫째자리까지.
     * 화면의 review-atf/review-score 는 "리뷰 개별 별점"이 아니라 "상품 전체 평균 별점" 자리라 이 값을 씀.
     */
    public double calcAvgRating(List<ReviewDTO> reviews) {
        if (reviews.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (ReviewDTO r : reviews) {
            sum += r.getRating();
        }
        return Math.round((double) sum / reviews.size() * 10) / 10.0;
    }

    /**
     * 사진을 용도별로 갈라줌.
     * PRODUCT_IMAGE 는 OPTION_ID 로 갈리는데, 값이 있으면 그 옵션 전용 사진이라 해당 옵션에 붙이고,
     * null 이면 상세설명용 사진이라 따로 모아서 반환함.
     *
     * @return 상세설명 사진 목록 (옵션 전용 사진은 options 안의 각 옵션에 채워짐)
     */
    public List<ProductImageDTO> attachImagesAndGetDetailImages(int productNo, List<ProductOptionDTO> options)
            throws Exception {

        List<ProductImageDTO> images = imageDAO.selectImagesByProductNo(productNo);
        List<ProductImageDTO> detailImages = new ArrayList<>();

        for (ProductImageDTO image : images) {
            if (image.getOptionId() == null) {
                detailImages.add(image);
                continue;
            }
            for (ProductOptionDTO option : options) {
                if (option.getOptionId() == image.getOptionId()) {
                    option.getImages().add(image);
                    break;
                }
            }
        }
        return detailImages;
    }

    /**
     * 옵션 드롭박스/색상칩은 js 가 그림(js/product.js 의 setupOptionSelect).
     * JSP 에서 JSON 을 손으로 조립하면 값에 따옴표 같은 게 들어갈 때 깨져서 Gson 으로 안전하게 만듦
     * (Gson 이 &lt;, &gt; 도 이스케이프해줘서 &lt;/script&gt; 사고도 없음).
     *
     * ★ 사진 주소 변환 — 원 작성자 scym(feature/scm, 2026-09-04). 기능은 그대로 옮겨 담음.
     *   이미지 경로는 DB에 "upload/5/xxx.jpg"처럼 저장돼 있어서 img:url(ImageUrl.resolve)과
     *   같은 규칙으로 미리 절대/상대 경로로 바꿔서 내려준다 — options(및 그 안의 이미지 DTO)는
     *   mainOption 으로도 쓰이고 있어서(JSP 가 img:url() 로 따로 변환), 원본을 그대로 바꾸면
     *   이중 변환되므로 JSON 트리만 복제해서 그 안의 imageUrl 만 고친다.
     */
    public String toOptionsJson(List<ProductOptionDTO> options) {
        JsonElement optionsTree = gson.toJsonTree(options);
        for (JsonElement optionEl : optionsTree.getAsJsonArray()) {
            JsonArray optionImages = optionEl.getAsJsonObject().getAsJsonArray("images");
            for (JsonElement imageEl : optionImages) {
                JsonObject imageObj = imageEl.getAsJsonObject();
                imageObj.addProperty("imageUrl", ImageUrl.resolve(imageObj.get("imageUrl").getAsString()));
            }
        }
        return gson.toJson(optionsTree);
    }
}
