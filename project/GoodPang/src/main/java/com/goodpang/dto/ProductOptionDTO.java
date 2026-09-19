package com.goodpang.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * PRODUCT_OPTION 한 행 + 그 옵션(OPTION_ID) 전용 PRODUCT_IMAGE 목록.
 * OPTION1_TYPE/VALUE 는 상품마다 다름(사이즈일 수도 색상일 수도 용량일 수도 있음).
 * OPTION2/3 은 상품에 따라 null 일 수 있음.
 * "옵션이 아예 없는 상품"도 PRODUCT_OPTION 에 행이 1개 있고 OPTION1~3 이 전부 null 인 형태로 들어있음
 * (익스포트.sql 상품 3번 확인) — product.jsp 에서 옵션 섹션을 그릴지 말지는 이 값으로 판단할 것.
 *
 * 지금은(2026-08-28) "조합 하나(OPTION_ID) = 드롭박스 항목 하나" 방식으로 화면에 그림.
 * 나중에 옵션1/옵션2를 계단식(하나 고르면 다른 하나가 그에 맞게 바뀜)으로 바꾸게 되면
 * 이 DTO 구조는 그대로 두고 product.jsp/js 만 바꾸면 됨.
 */
@Getter
@Setter
public class ProductOptionDTO {

    private int optionId;

    private String option1Type;
    private String option1Value;
    private String option2Type;
    private String option2Value;
    private String option3Type;
    private String option3Value;

    /* 2026-08-30 확정 — PRODUCT.PRODUCT_PRICE(기본가)에 더해지는 "추가금".
       CartDAO.getCartItems() 가 이미 이 전제로 짜여있어서(p.PRODUCT_PRICE + NVL(po.PRICE,0)) 통일함.
       NVL 처리해서 0 으로 들어옴 — 0 이면 "이 옵션엔 추가금이 없다"는 뜻이라 그냥 기본가 그대로 씀. */
    private int price;
    private Integer normalPrice; // 할인 전 "정상 추가금" — 선택 입력이라 없으면 null(그럼 그 옵션은 할인 표시 안 함)
    private int quantity;
    private String status;

    private List<ProductImageDTO> images = new ArrayList<>();  // 이 옵션의 대표/추가 사진

    // product.jsp 드롭박스에 보여줄 라벨. 예: "S" / "블랙 / S" / "화이트 / 260 / 일반"
    // (있는 값만 " / "로 이어붙임 — option2/3 이 null 이면 자동으로 빠짐)
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        if (option1Value != null) sb.append(option1Value);
        if (option2Value != null) sb.append(sb.length() > 0 ? " / " : "").append(option2Value);
        if (option3Value != null) sb.append(sb.length() > 0 ? " / " : "").append(option3Value);
        return sb.toString();
    }
}
