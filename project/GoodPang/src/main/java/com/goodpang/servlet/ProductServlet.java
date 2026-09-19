package com.goodpang.servlet;

import java.io.IOException;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// 2026-09-04 (flicker1016) — /product 는 이제 커맨드 패턴으로 처리함
// (web.xml -> controller.DispatcherServlet -> command.ProductHandler -> service.ProductService).
// 같은 URL 에 서블릿이 둘이면 톰캣 배포가 실패해서 이 줄만 주석처리함.
// 코드는 비교용으로 남겨둠 — 되돌리려면 아래 주석을 풀고 web.xml 의 dispatcher 매핑을 지우면 됨.
// @WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String productNoParam = request.getParameter("productNo");

        try {
            if (productNoParam != null && !productNoParam.isEmpty()) {

                int productNo = Integer.parseInt(productNoParam);

                ProductDAO dao = new ProductDAO();
                ProductDTO product = dao.selectProduct(productNo);

                if (product == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "상품을 찾을 수 없습니다.");
                    return;
                }
                
                // 배송예정일 — "내일(요일) M/d" 형태로 매번 계산
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                String deliveryDate = "내일(" + dayOfWeek + ") " + tomorrow.getMonthValue() + "/" + tomorrow.getDayOfMonth();
                request.setAttribute("deliveryDate", deliveryDate);

                request.setAttribute("p", product);

                // 판매자 대시보드 "오늘 방문자수/상품노출수" 집계용 조회 로그
                Integer memberNo = (Integer) request.getSession().getAttribute("memberNo");
                new ProductViewLogDAO().logView(productNo, memberNo, request.getSession().getId());

                // 옵션 + 옵션별/상세설명 사진
                //  옵션 하나(OPTION_ID)마다 대표/추가 사진이 딸려있고, OPTION_ID 가 null 인 사진은
                //  상세설명용이라 따로 뺌)
                ProductOptionDAO optionDAO = new ProductOptionDAO();
                List<ProductOptionDTO> options = optionDAO.selectOptionsByProductNo(productNo);

                /* 2026-09-01 추가 — 옵션을 바꾸고 새로고침해도 그 옵션이 그대로 선택돼 있게.
                   실제 쿠팡을 Playwright 로 확인해보니, 옵션을 바꾸면 주소(itemId/vendorItemId)가
                   그 자리에서 바뀌고(js/product.js updateFromSelects 의 history.replaceState),
                   그 주소로 새로고침하면 서버가 그 값을 보고 그 옵션으로 다시 그려줌 — 같은 방식을 씀.
                   URL 의 optionId 가 없거나, 있어도 이 상품 옵션 목록에 없으면(다른 상품 값을 끼워넣거나
                   조작한 경우) 조용히 무시하고 예전처럼 첫 옵션으로 감 — DB 를 한 번 더 안 물어보고
                   이미 불러온 options 안에서만 찾음(그래야 "이 상품 소속"인지도 같이 검증됨) */
                ProductOptionDTO selectedOption = null;
                String optionIdParam = request.getParameter("optionId");
                if (optionIdParam != null) {
                    try {
                        int wantedOptionId = Integer.parseInt(optionIdParam);
                        for (ProductOptionDTO option : options) {
                            if (option.getOptionId() == wantedOptionId) {
                                selectedOption = option;
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // optionId 가 숫자가 아니면 무시 — 아래에서 첫 옵션으로 fallback
                    }
                }
                if (selectedOption == null && !options.isEmpty()) {
                    selectedOption = options.get(0);
                }
                // product.jsp 는 예전에 <c:set var="mainOption" value="${options[0]}"/> 로 직접
                // 골랐는데, 그러면 여기서 고른 값을 페이지 스코프가 도로 덮어써버려서 이 변수로 넘김
                request.setAttribute("mainOption", selectedOption);

                /* 2026-08-30 확정 — PRODUCT_OPTION.PRICE/NORMAL_PRICE 는 PRODUCT.PRODUCT_PRICE(기본가)에
                   더해지는 "추가금"(CartDAO.getCartItems() 와 같은 전제). 그래서:
                     판매가 총액 = PRODUCT_PRICE + 옵션의 PRICE       (할인된 추가금)
                     정상가 총액 = PRODUCT_PRICE + 옵션의 NORMAL_PRICE (할인 전 추가금, 선택 입력이라 없을 수 있음)
                   정상가 총액이 판매가 총액보다 클 때만 "할인 중"으로 보고 취소선/할인율을 보여줌.
                   지금은(2026-08-30) NORMAL_PRICE 를 채운 판매자가 없어서 할인 표시가 실제로는 안 뜨는데,
                   판매자가 정상가를 입력하기 시작하면 코드 수정 없이 자동으로 뜨게 됨. */
                int displayPrice = product.getProductPrice();
                Integer displayNormalPrice = null;

                if (selectedOption != null) {
                    displayPrice = product.getProductPrice() + selectedOption.getPrice();

                    if (selectedOption.getNormalPrice() != null) {
                        int normalTotal = product.getProductPrice() + selectedOption.getNormalPrice();
                        if (normalTotal > displayPrice) {
                            displayNormalPrice = normalTotal;
                        }
                    }
                }

                int discountRate = 0;
                if (displayNormalPrice != null && displayNormalPrice > 0) {
                    discountRate = (int) Math.round((1 - (double) displayPrice / displayNormalPrice) * 100);
                }

                request.setAttribute("displayPrice", displayPrice);
                request.setAttribute("displayNormalPrice", displayNormalPrice);
                request.setAttribute("discountRate", discountRate);

                // 적립 혜택 — 가격의 5% (옵션 가격 기준으로 계산해야 해서 옵션 조회 뒤로 옮김)
                int rewardCash = displayPrice * 5 / 100;
                request.setAttribute("rewardCash", rewardCash);

                ProductImageDAO imageDAO = new ProductImageDAO();
                List<ProductImageDTO> images = imageDAO.selectImagesByProductNo(productNo);

                List<ProductImageDTO> detailImages = new ArrayList<>();

                for (ProductImageDTO image : images) {
                    if (image.getOptionId() == null) {
                        // OPTION_ID 없는 사진 = 상세설명 사진
                        detailImages.add(image);
                        continue;
                    }
                    // OPTION_ID 있는 사진 = 그 옵션의 대표/추가 사진 → 해당 옵션에 붙여줌
                    for (ProductOptionDTO option : options) {
                        if (option.getOptionId() == image.getOptionId()) {
                            option.getImages().add(image);
                            break;
                        }
                    }
                }

                request.setAttribute("options", options);
                request.setAttribute("detailImages", detailImages);

                // 옵션 드롭박스/색상칩은 js 가 그림(js/product.js 의 setupOptionSelect).
                // JSP 에서 JSON 을 손으로 조립하면 값에 따옴표 같은 게 들어갈 때 깨져서,
                // Gson 으로 안전하게 만들어 넘김 (Gson 이 <, > 도 이스케이프해줘서 </script> 사고도 없음)
                //
                // 이미지 경로는 DB에 "upload/5/xxx.jpg"처럼 저장돼 있어서 img:url(ImageUrl.resolve)과
                // 같은 규칙으로 미리 절대/상대 경로로 바꿔서 내려준다 - options(및 그 안의 이미지 DTO)는
                // 위에서 mainOption으로도 쓰이고 있어서(JSP가 img:url()로 따로 변환), 원본을 그대로 바꾸면
                // 이중 변환되므로 JSON 트리만 복제해서 그 안의 imageUrl만 고친다.
                JsonElement optionsTree = gson.toJsonTree(options);
                for (JsonElement optionEl : optionsTree.getAsJsonArray()) {
                    JsonArray optionImages = optionEl.getAsJsonObject().getAsJsonArray("images");
                    for (JsonElement imageEl : optionImages) {
                        JsonObject imageObj = imageEl.getAsJsonObject();
                        imageObj.addProperty("imageUrl", ImageUrl.resolve(imageObj.get("imageUrl").getAsString()));
                    }
                }
                request.setAttribute("optionsJson", gson.toJson(optionsTree));

                // 리뷰
                ReviewDAO reviewDAO = new ReviewDAO();
                List<ReviewDTO> reviews = reviewDAO.selectReviewsByProductNo(productNo);
                request.setAttribute("reviews", reviews);
                request.setAttribute("reviewCount", reviews.size());

                ReviewRatingSummaryDTO reviewStats =
                        reviewDAO.getRatingSummary(productNo);

                request.setAttribute(
                        "reviewStats",
                        reviewStats
                );

                request.setAttribute(
                        "avgRating",
                        reviewStats.getAvgRating()
                );

                request.setAttribute(
                        "reviewCount",
                        reviewStats.getReviewCount()
                );

                for (ReviewDTO review : reviews) {
                	List<String> imageUrls =
                			reviewDAO.getReviewImages(
                					review.getReviewNo()
                			);

                	review.setImageUrls(imageUrls);
                }

                request.setAttribute("reviews", reviews);
                // 평균 평점 — 2026-08-27: 별점 UI를 두 자리 다 스프라이트 이미지로 바꾸면서 필요해짐
                // (review-atf/review-score 는 "리뷰 개별 별점"이 아니라 "상품 전체 평균 별점"을 보여주는 자리라
                //  이미 읽어온 reviews 리스트에서 바로 평균을 냄 — DB 를 한 번 더 조회할 필요 없음)
                double avgRating = 0;
                if (!reviews.isEmpty()) {
                    int sum = 0;
                    for (ReviewDTO r : reviews) sum += r.getRating();
                    avgRating = Math.round((double) sum / reviews.size() * 10) / 10.0;   // 소수 첫째자리까지
                }
                request.setAttribute("avgRating", avgRating);
            }

        } catch (NumberFormatException e) {
            System.err.println("[ERROR] productNo 숫자 변환 실패 (잘못된 파라미터): " + productNoParam);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] DB 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher(
                "/product.jsp"
        ).forward(request, response);
    }
}
