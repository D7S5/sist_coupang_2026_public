package com.goodpang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private long categoryNo;
    private String categoryName;
    private Long parentCategoryNo;
    private int categoryLevel;
    private String imgUrl;
    private String categoryPath; // 검색 결과 자동완성 표시용 - 루트부터 이 카테고리까지 "대분류 > 중분류 > 소분류" 형태
}
