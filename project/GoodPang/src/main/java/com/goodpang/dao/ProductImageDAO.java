package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.ProductImageDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductImageDAO {

    // 상품 하나의 PRODUCT_IMAGE 를 전부 가져옴 (옵션 사진 + 상세설명 사진 다 섞여서 나옴).
    // OPTION_ID 로 옵션 사진/상세설명 사진을 나누는 건 ProductServlet 쪽에서 함.
    //
    // ★ 2026-09-03 수정 — ORDER BY 에 CASE 를 추가함 (원래는 OPTION_ID, IMAGE_ORDER 뿐이었음).
    //   같은 옵션 안에서 '대표' 사진과 '추가' 사진이 IMAGE_ORDER 를 똑같이 1로 쓰는 경우가 실제로 있는데
    //   (예: OPTION_ID 395 — 대표/추가 둘 다 IMAGE_ORDER=1), 이러면 오라클이 동점을 어떻게 풀지 보장을
    //   안 해줘서 상품마다 대표/추가 중 뭐가 먼저 나올지 들쭉날쭉했음(product.jsp 의 mainOption.images[0]
    //   이 그 결과를 그대로 큰 이미지로 씀 — "가끔 대표사진 대신 추가사진이 뜬다" 버그의 원인).
    //   CASE 로 '대표'를 항상 0, 나머지를 1로 매겨서 최우선 정렬하고, 맨 뒤에 IMAGE_NO 로
    //   완전히 결정론적으로 만듦(추가 사진끼리도 순서가 매번 똑같이 나오게).
    public List<ProductImageDTO> selectImagesByProductNo(int productNo) {

        List<ProductImageDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    IMAGE_NO,
                    PRODUCT_NO,
                    OPTION_ID,
                    IMAGE_PURPOSE,
                    IMAGE_ORDER,
                    IMAGE_URL
                FROM PRODUCT_IMAGE
                WHERE PRODUCT_NO = ?
                ORDER BY OPTION_ID, CASE WHEN IMAGE_PURPOSE = '대표' THEN 0 ELSE 1 END, IMAGE_ORDER, IMAGE_NO
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ProductImageDTO dto = new ProductImageDTO();

                    dto.setImageNo(rs.getInt("IMAGE_NO"));
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));

                    int optionId = rs.getInt("OPTION_ID");
                    dto.setOptionId(rs.wasNull() ? null : optionId);   // 상세설명 사진은 OPTION_ID 가 null

                    dto.setImagePurpose(rs.getString("IMAGE_PURPOSE"));
                    dto.setImageOrder(rs.getInt("IMAGE_ORDER"));
                    dto.setImageUrl(rs.getString("IMAGE_URL"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* 옵션 하나(OPTION_ID)의 사진만 조회 — /option ajax(ProductOptionServlet)에서 씀.
       2026-08-31 추가.
       ★ 2026-09-03 수정 — ORDER BY 에 CASE 추가, 이유는 selectImagesByProductNo() 위 주석과 동일
       (같은 옵션 안에서 대표/추가가 IMAGE_ORDER 를 똑같이 쓰는 경우의 동점 문제). */
    public List<ProductImageDTO> selectImagesByOptionId(int optionId) {

        List<ProductImageDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    IMAGE_NO,
                    PRODUCT_NO,
                    OPTION_ID,
                    IMAGE_PURPOSE,
                    IMAGE_ORDER,
                    IMAGE_URL
                FROM PRODUCT_IMAGE
                WHERE OPTION_ID = ?
                ORDER BY CASE WHEN IMAGE_PURPOSE = '대표' THEN 0 ELSE 1 END, IMAGE_ORDER, IMAGE_NO
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, optionId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ProductImageDTO dto = new ProductImageDTO();

                    dto.setImageNo(rs.getInt("IMAGE_NO"));
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setOptionId(optionId);
                    dto.setImagePurpose(rs.getString("IMAGE_PURPOSE"));
                    dto.setImageOrder(rs.getInt("IMAGE_ORDER"));
                    dto.setImageUrl(rs.getString("IMAGE_URL"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
