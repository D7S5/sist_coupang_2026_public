package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.goodpang.dto.VendorProductDetailDTO;
import com.goodpang.dto.VendorProductOptionDetailDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 상품 상세(vendor-product-detail.jsp) 조회.
 * PRODUCT + 카테고리, PRODUCT_OPTION 목록, PRODUCT_IMAGE 목록을 각각 조회해 하나로 조립한다.
 * seller_no까지 같이 확인해서, 다른 판매자의 상품번호를 URL에 넣어도 조회되지 않게 한다.
 */
public class VendorProductDetailDAO {

    public VendorProductDetailDTO findByProductNo(int productNo, int sellerNo) {

        VendorProductDetailDTO dto = selectProduct(productNo, sellerNo);

        if (dto == null) {
            return null;
        }

        Map<Integer, VendorProductOptionDetailDTO> optionMap = selectOptions(productNo);

        applyImages(productNo, dto, optionMap);

        dto.getOptions().addAll(optionMap.values());

        return dto;
    }

    private VendorProductDetailDTO selectProduct(int productNo, int sellerNo) {

        String sql = """
            SELECT
                P.PRODUCT_NO, P.SELLER_NO,
                C1.CATEGORY_NAME AS MAIN_CATEGORY_NAME,
                C2.CATEGORY_NAME AS MID_CATEGORY_NAME,
                C3.CATEGORY_NAME AS SUB_CATEGORY_NAME,
                P.SALE_METHOD, P.BRAND_NAME, P.NO_BRAND_YN, P.PRODUCT_NAME, P.INTERNAL_NAME, P.PRODUCT_PRICE,
                P.DETAIL_TYPE, P.PRODUCT_DESC,
                P.SHIPPING_ZIPCODE, P.SHIPPING_ADDRESS, P.SHIPPING_DETAIL_ADDRESS, P.JEJU_SHIPPING_YN,
                P.DELIVERY_SERVICE_CODE, P.DELIVERY_METHOD, P.BUNDLE_SHIPPING_YN,
                P.SHIPPING_FEE_TYPE, P.SHIPPING_FEE,
                P.LEAD_TIME_INPUT_TYPE, P.LEAD_TIME_DAYS, P.SAME_DAY_SHIP_YN, P.SAME_DAY_CUTOFF_TIME,
                P.SALE_STATUS, P.DISPLAY_YN, P.CREATED_DATE, P.UPDATED_DATE
            FROM PRODUCT P
                JOIN CATEGORY C3 ON P.SUB_CATEGORY_NO = C3.CATEGORY_NO
                LEFT JOIN CATEGORY C2 ON C2.CATEGORY_NO = C3.PARENT_CATEGORY_NO
                LEFT JOIN CATEGORY C1 ON C1.CATEGORY_NO = C2.PARENT_CATEGORY_NO
            WHERE P.PRODUCT_NO = ?
              AND P.SELLER_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, productNo);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return mapProductRow(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<Integer, VendorProductOptionDetailDTO> selectOptions(int productNo) {

        Map<Integer, VendorProductOptionDetailDTO> optionMap = new LinkedHashMap<>();

        String sql = """
            SELECT
                OPTION_ID,
                OPTION1_TYPE, OPTION1_VALUE, OPTION2_TYPE, OPTION2_VALUE, OPTION3_TYPE, OPTION3_VALUE,
                normal_price, PRICE, auto_price_adjust_yn, quantity,
                seller_product_code, model_no, barcode, STATUS
            FROM PRODUCT_OPTION
            WHERE product_no = ?
            ORDER BY OPTION_ID
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    VendorProductOptionDetailDTO option = new VendorProductOptionDetailDTO();

                    int optionId = rs.getInt("OPTION_ID");
                    option.setOptionId(optionId);

                    option.setOption1Type(rs.getString("OPTION1_TYPE"));
                    option.setOption1Value(rs.getString("OPTION1_VALUE"));
                    option.setOption2Type(rs.getString("OPTION2_TYPE"));
                    option.setOption2Value(rs.getString("OPTION2_VALUE"));
                    option.setOption3Type(rs.getString("OPTION3_TYPE"));
                    option.setOption3Value(rs.getString("OPTION3_VALUE"));

                    int normalPrice = rs.getInt("normal_price");
                    option.setNormalPrice(rs.wasNull() ? null : normalPrice);

                    option.setPrice(rs.getInt("PRICE"));
                    option.setAutoPriceAdjustYn(rs.getString("auto_price_adjust_yn"));
                    option.setQuantity(rs.getInt("quantity"));

                    option.setSellerProductCode(rs.getString("seller_product_code"));
                    option.setModelNo(rs.getString("model_no"));
                    option.setBarcode(rs.getString("barcode"));
                    option.setStatus(rs.getString("STATUS"));

                    optionMap.put(optionId, option);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionMap;
    }

    private void applyImages(int productNo, VendorProductDetailDTO dto, Map<Integer, VendorProductOptionDetailDTO> optionMap) {

        String sql = """
            SELECT OPTION_ID, image_purpose, image_order, image_url
            FROM PRODUCT_IMAGE
            WHERE product_no = ?
            ORDER BY OPTION_ID, image_purpose, image_order
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    int optionId = rs.getInt("OPTION_ID");
                    boolean hasOptionId = !rs.wasNull();
                    String purpose = rs.getString("image_purpose");
                    String imageUrl = rs.getString("image_url");

                    if (!hasOptionId) {
                        // 옵션에 속하지 않는 이미지 = 상세설명 이미지
                        dto.getDetailImageUrls().add(imageUrl);
                        continue;
                    }

                    VendorProductOptionDetailDTO option = optionMap.get(optionId);
                    if (option == null) {
                        continue;
                    }

                    if ("대표".equals(purpose)) {
                        option.setMainImageUrl(imageUrl);
                    } else if ("추가".equals(purpose)) {
                        option.getExtraImageUrls().add(imageUrl);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VendorProductDetailDTO mapProductRow(ResultSet rs) throws java.sql.SQLException {

        VendorProductDetailDTO dto = new VendorProductDetailDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setSellerNo(rs.getInt("SELLER_NO"));

        dto.setMainCategoryName(rs.getString("MAIN_CATEGORY_NAME"));
        dto.setMidCategoryName(rs.getString("MID_CATEGORY_NAME"));
        dto.setSubCategoryName(rs.getString("SUB_CATEGORY_NAME"));

        dto.setSaleMethod(rs.getString("SALE_METHOD"));
        dto.setBrandName(rs.getString("BRAND_NAME"));
        dto.setNoBrandYn(rs.getString("NO_BRAND_YN"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setInternalName(rs.getString("INTERNAL_NAME"));
        dto.setProductPrice(rs.getInt("PRODUCT_PRICE"));

        dto.setDetailType(rs.getString("DETAIL_TYPE"));
        dto.setProductDesc(rs.getString("PRODUCT_DESC"));

        dto.setShippingZipcode(rs.getString("SHIPPING_ZIPCODE"));
        dto.setShippingAddress(rs.getString("SHIPPING_ADDRESS"));
        dto.setShippingDetailAddress(rs.getString("SHIPPING_DETAIL_ADDRESS"));
        dto.setJejuShippingYn(rs.getString("JEJU_SHIPPING_YN"));
        dto.setDeliveryServiceCode(rs.getString("DELIVERY_SERVICE_CODE"));
        dto.setDeliveryMethod(rs.getString("DELIVERY_METHOD"));
        dto.setBundleShippingYn(rs.getString("BUNDLE_SHIPPING_YN"));
        dto.setShippingFeeType(rs.getString("SHIPPING_FEE_TYPE"));
        dto.setShippingFee(rs.getInt("SHIPPING_FEE"));
        dto.setLeadTimeInputType(rs.getString("LEAD_TIME_INPUT_TYPE"));

        int leadTimeDays = rs.getInt("LEAD_TIME_DAYS");
        dto.setLeadTimeDays(rs.wasNull() ? null : leadTimeDays);

        dto.setSameDayShipYn(rs.getString("SAME_DAY_SHIP_YN"));
        dto.setSameDayCutoffTime(rs.getString("SAME_DAY_CUTOFF_TIME"));

        dto.setSaleStatus(rs.getString("SALE_STATUS"));
        dto.setDisplayYn(rs.getString("DISPLAY_YN"));
        dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        dto.setUpdatedDate(rs.getTimestamp("UPDATED_DATE"));

        return dto;
    }
}
