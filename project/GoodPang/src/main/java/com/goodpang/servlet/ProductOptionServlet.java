package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.goodpang.dao.ProductImageDAO;
import com.goodpang.dao.ProductOptionDAO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.util.ImageUrl;

/*
 * ★ 2026-08-31 추가 — 옵션(사이즈/색상 등)을 바꿀 때마다 product.js 가 여기로 ajax(GET)를 보내서
 *   그 옵션의 가격(추가금)/재고/사진을 다시 받아감.
 *
 *   원래는(2026-08-28~30) 페이지 처음 열 때 옵션 조합을 전부 JSON으로 받아두고,
 *   옵션을 바꿔도 그 값을 그대로 화면에서 찾아 쓰는 방식이었음 — 조합이 몇 개 없고 누가 보든
 *   똑같은 값이라 서버에 매번 물어볼 이유가 없었기 때문
 *
 *   이 프로젝트도 로그인·회원등급(SCM_MEMBER.GRADE)이 생길 예정이라, 값 계산 자체는 지금 당장
 *   달라지는 게 없어도(등급별 할인 로직은 아직 없음) 구조를 미리 서버 왕복으로 바꿔둠 — 나중에
 *   등급별 할인을 넣을 때 이 서블릿 안에만 로직을 추가하면 되고 product.js는 안 건드려도 됨.
 *
 *   가격 계산 공식은 ProductServlet 의 displayPrice 와 똑같음(추가금 방식, 2026-08-30 확정) —
 *   여기서 돌려주는 price/normalPrice 는 "옵션의 추가금"만이고, 기본가(PRODUCT_PRICE)를 더하는 건
 *   product.js 의 setPrice() 가 함(원래도 그렇게 하던 걸 그대로 둠).
 */
// 2026-09-04 (flicker1016) — /option 은 이제 커맨드 패턴으로 처리함
// (web.xml -> controller.DispatcherServlet -> command.ProductOptionHandler -> service.ProductOptionService).
// 같은 URL 에 서블릿이 둘이면 톰캣 배포가 실패해서 이 줄만 주석처리함.
// 코드는 비교용으로 남겨둠 — 되돌리려면 아래 주석을 풀고 web.xml 의 dispatcher 매핑을 지우면 됨.
// @WebServlet("/option")
public class ProductOptionServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");

        String optionIdParam = request.getParameter("optionId");

        try {
            int optionId = Integer.parseInt(optionIdParam);

            ProductOptionDAO optionDAO = new ProductOptionDAO();
            ProductOptionDTO option = optionDAO.selectOptionById(optionId);

            if (option == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "옵션을 찾을 수 없습니다.");
                return;
            }

            ProductImageDAO imageDAO = new ProductImageDAO();
            List<ProductImageDTO> images = imageDAO.selectImagesByOptionId(optionId);

            JsonObject result = new JsonObject();
            result.addProperty("price", option.getPrice());
            result.addProperty("normalPrice", option.getNormalPrice());   // null 이면 그대로 JSON null 로 나감
            result.addProperty("quantity", option.getQuantity());
            result.addProperty("status", option.getStatus());

            JsonArray imageUrls = new JsonArray();
            for (ProductImageDTO img : images) {
                imageUrls.add(ImageUrl.resolve(img.getImageUrl()));
            }
            result.add("imageUrls", imageUrls);

            response.getWriter().write(gson.toJson(result));

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "optionId 가 올바르지 않습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "옵션 조회 중 오류가 발생했습니다.");
        }
    }
}
