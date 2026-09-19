package com.goodpang.service;

import java.util.List;

import com.goodpang.dao.ProductImageDAO;
import com.goodpang.dao.ProductOptionDAO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.util.ImageUrl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 옵션 변경 ajax(/option)의 업무 로직 담당 (Service 계층).
 *
 * 원래 ProductOptionServlet.doGet() 안에 있던 DB 조회 + JSON 조립을 여기로 옮김.
 *
 * ★ 2026-08-31 배경 — 옵션(사이즈/색상 등)을 바꿀 때마다 product.js 가 ajax(GET)로 그 옵션의
 *   가격(추가금)/재고/사진을 다시 받아감. 원래는 페이지 처음 열 때 옵션 조합을 전부 JSON 으로
 *   받아두고 화면에서 찾아 쓰는 방식이었는데, 로그인·회원등급(SCM_MEMBER.GRADE)이 생길 예정이라
 *   값이 지금 당장 달라지지 않아도 구조를 미리 서버 왕복으로 바꿔둔 것 —
 *   나중에 등급별 할인을 넣을 때 이 클래스 안에만 로직을 추가하면 되고 product.js 는 안 건드려도 됨.
 *
 * ★ 가격은 "옵션의 추가금"만 돌려줌 — 기본가(PRODUCT_PRICE)를 더하는 건 product.js 의 setPrice() 가 함
 *   (2026-08-30 확정한 추가금 방식, ProductService.calcDisplayPrice() 와 같은 전제).
 *
 * ※ ProductOptionServlet 은 아직 원본 그대로 살아있음.
 */
public class ProductOptionService {

    private static final Gson gson = new Gson();

    private final ProductOptionDAO optionDAO = new ProductOptionDAO();
    private final ProductImageDAO imageDAO = new ProductImageDAO();

    /** 옵션 1건. 없으면 null (Handler 가 404 로 처리) */
    public ProductOptionDTO getOption(int optionId) throws Exception {
        return optionDAO.selectOptionById(optionId);
    }

    /** 이 옵션 전용 사진들 */
    public List<ProductImageDTO> getImages(int optionId) throws Exception {
        return imageDAO.selectImagesByOptionId(optionId);
    }

    /**
     * product.js 가 받아갈 JSON 문자열로 조립.
     * 키 이름(price/normalPrice/quantity/status/imageUrls)은 product.js 와의 약속이라 바꾸면 안 됨.
     *
     * ★ 사진 주소는 ImageUrl.resolve() 로 완성해서 내려줌 — 원 작성자 scym(feature/scm, 2026-09-04).
     *   DB 의 IMAGE_URL 은 "upload/5/xxx.jpg" 처럼 앞부분이 없는데, 예전에는 product.js 가
     *   contextPath 를 붙였고 지금은 서버가 img:url 과 같은 규칙으로 변환해서 내려줌
     *   (그래서 product.js 는 받은 값을 그대로 씀). 여기를 되돌리면 이미지가 깨짐.
     */
    public String buildOptionJson(ProductOptionDTO option, List<ProductImageDTO> images) {
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

        return gson.toJson(result);
    }
}
