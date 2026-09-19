package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductOptionDAO {

    // 상품 하나의 옵션 목록 (PRICE 는 NVL 로 0 처리)
    public List<ProductOptionDTO> selectOptionsByProductNo(int productNo) {

        List<ProductOptionDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    OPTION_ID,
                    OPTION1_TYPE,
                    OPTION1_VALUE,
                    OPTION2_TYPE,
                    OPTION2_VALUE,
                    OPTION3_TYPE,
                    OPTION3_VALUE,
                    NVL(PRICE, 0) AS PRICE,
                    NORMAL_PRICE,
                    QUANTITY,
                    STATUS
                FROM PRODUCT_OPTION
                WHERE PRODUCT_NO = ?
                ORDER BY OPTION_ID
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ProductOptionDTO dto = new ProductOptionDTO();

                    dto.setOptionId(rs.getInt("OPTION_ID"));

                    dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                    dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                    dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                    dto.setOption2Value(rs.getString("OPTION2_VALUE"));
                    dto.setOption3Type(rs.getString("OPTION3_TYPE"));
                    dto.setOption3Value(rs.getString("OPTION3_VALUE"));

                    dto.setPrice(rs.getInt("PRICE"));
                    // NORMAL_PRICE 는 NVL 안 함 — null 이면 "정상 추가금 입력 안 함"이라는 뜻이라 그대로 null 로 남겨둠
                    dto.setNormalPrice(rs.getObject("NORMAL_PRICE", Integer.class));
                    dto.setQuantity(rs.getInt("QUANTITY"));
                    dto.setStatus(rs.getString("STATUS"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* 옵션 하나(OPTION_ID)만 조회 — /option ajax(ProductOptionServlet)에서 씀.
       2026-08-31 추가: 옵션을 바꿀 때마다 서버에 다시 물어보는 구조로 바꾸면서 필요해짐
       (나중에 로그인 회원등급별 할인 같은 게 생기면 이 메서드 안에서 계산하면 됨). */
    public ProductOptionDTO selectOptionById(int optionId) {

        String sql = """
                SELECT
                    OPTION_ID,
                    OPTION1_TYPE,
                    OPTION1_VALUE,
                    OPTION2_TYPE,
                    OPTION2_VALUE,
                    OPTION3_TYPE,
                    OPTION3_VALUE,
                    NVL(PRICE, 0) AS PRICE,
                    NORMAL_PRICE,
                    QUANTITY,
                    STATUS
                FROM PRODUCT_OPTION
                WHERE OPTION_ID = ?
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, optionId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) return null;   // 없는 optionId

                ProductOptionDTO dto = new ProductOptionDTO();

                dto.setOptionId(rs.getInt("OPTION_ID"));

                dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                dto.setOption2Value(rs.getString("OPTION2_VALUE"));
                dto.setOption3Type(rs.getString("OPTION3_TYPE"));
                dto.setOption3Value(rs.getString("OPTION3_VALUE"));

                dto.setPrice(rs.getInt("PRICE"));
                dto.setNormalPrice(rs.getObject("NORMAL_PRICE", Integer.class));
                dto.setQuantity(rs.getInt("QUANTITY"));
                dto.setStatus(rs.getString("STATUS"));

                return dto;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
