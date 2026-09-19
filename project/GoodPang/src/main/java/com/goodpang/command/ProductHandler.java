package com.goodpang.command;

import java.util.List;

import com.goodpang.dto.ProductDTO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.dto.ReviewDTO;
import com.goodpang.dto.ReviewRatingSummaryDTO;
import com.goodpang.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /product 요청 처리 — 원래 ProductServlet.doGet() 이 하던 일.
 *
 * 여기서는 파라미터를 읽고 ProductService 를 순서대로 부른 뒤 request 에 담기만 함.
 * DB 조회와 계산은 전부 ProductService 안에 있음(controller -> service -> persistence).
 *
 * ※ ProductServlet 은 아직 원본 그대로 살아있음. web.xml 매핑을 붙이고 화면 확인이 끝나면
 *   그때 ProductServlet 의 @WebServlet 을 주석처리할 예정.
 */
public class ProductHandler implements CommandHandler {

    private final ProductService service = new ProductService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 상품번호는 주소 두 형태를 다 받음 (2026-09-06)
        //  ① /product/131          -> getPathInfo() 가 "/131" 을 돌려줌 (쿠팡식, 새 주소)
        //  ② /product?productNo=131 -> 팀원 화면 16곳이 아직 이 주소를 써서 같이 살려둠
        // ① 이 있으면 ① 을 우선함
        String productNoParam = request.getParameter("productNo");

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            productNoParam = pathInfo.substring(1);          // 맨 앞 "/" 떼기 -> "131"
            int slash = productNoParam.indexOf('/');         // "/131/뭐시기" 처럼 더 붙어도 앞부분만
            if (slash > -1) {
                productNoParam = productNoParam.substring(0, slash);
            }
        }

        try {
            if (productNoParam != null && !productNoParam.isEmpty()) {

                int productNo = Integer.parseInt(productNoParam);

                ProductDTO product = service.getProduct(productNo);

                if (product == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "상품을 찾을 수 없습니다.");
                    // null 을 리턴하면 DispatcherServlet 이 forward 를 건너뜀
                    // (여기서 이미 응답을 끝냈기 때문)
                    return null;
                }

                request.setAttribute("p", product);
                request.setAttribute("deliveryDate", service.calcDeliveryDate());

                // 판매자 대시보드 "오늘 방문자수/상품노출수" 집계용 조회 로그
                Integer memberNo = (Integer) request.getSession().getAttribute("memberNo");
                service.logView(productNo, memberNo, request.getSession().getId());

                // 옵션 + 어느 옵션이 선택된 상태인지 (URL 의 optionId 기준, 없으면 첫 옵션)
                List<ProductOptionDTO> options = service.getOptions(productNo);
                ProductOptionDTO selectedOption =
                        service.chooseOption(options, request.getParameter("optionId"));

                // product.jsp 는 예전에 <c:set var="mainOption" value="${options[0]}"/> 로 직접
                // 골랐는데, 그러면 여기서 고른 값을 페이지 스코프가 도로 덮어써버려서 이 변수로 넘김
                request.setAttribute("mainOption", selectedOption);

                // 가격 — 판매가 = 기본가 + 옵션 추가금, 정상가가 더 클 때만 할인 표시
                int displayPrice = service.calcDisplayPrice(product, selectedOption);
                Integer displayNormalPrice =
                        service.calcDisplayNormalPrice(product, selectedOption, displayPrice);

                request.setAttribute("displayPrice", displayPrice);
                request.setAttribute("displayNormalPrice", displayNormalPrice);
                request.setAttribute("discountRate", service.calcDiscountRate(displayPrice, displayNormalPrice));
                request.setAttribute("rewardCash", service.calcRewardCash(displayPrice));

                // 사진 — 옵션 전용 사진은 각 옵션에 붙고, 상세설명 사진만 따로 나옴
                List<ProductImageDTO> detailImages =
                        service.attachImagesAndGetDetailImages(productNo, options);

                request.setAttribute("options", options);
                request.setAttribute("detailImages", detailImages);
                request.setAttribute("optionsJson", service.toOptionsJson(options));

                // 리뷰
                List<ReviewDTO> reviews = service.getReviewsWithImages(productNo);
                ReviewRatingSummaryDTO reviewStats = service.getRatingSummary(productNo);

                request.setAttribute("reviews", reviews);
                request.setAttribute("reviewStats", reviewStats);
                request.setAttribute("reviewCount", reviewStats.getReviewCount());
                request.setAttribute("avgRating", service.calcAvgRating(reviews));
            }

        } catch (NumberFormatException e) {
            System.err.println("[ERROR] productNo 숫자 변환 실패 (잘못된 파라미터): " + productNoParam);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] DB 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return "/product.jsp";
    }
}
