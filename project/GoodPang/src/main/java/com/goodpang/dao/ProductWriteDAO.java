package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.ProductOptionWriteDTO;
import com.goodpang.dto.ProductWriteDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 상품 등록 화면 제출 내용을 PRODUCT + PRODUCT_OPTION + PRODUCT_IMAGE 세 테이블에 트랜잭션으로 저장.
 * 이미지 파일 저장(디스크 I/O)은 서블릿 쪽 책임이고, 여기서는 이미 저장된 이미지 URL 문자열만 받아서 INSERT한다.
 */
public class ProductWriteDAO {

    public int insertProduct(ProductWriteDTO dto) throws Exception {

        try (Connection conn = ConnectionProvider.getConnection()) {

            conn.setAutoCommit(false);

            try {
                int productNo = nextVal(conn, "SEQ_PRODUCT");
                insertProductRow(conn, productNo, dto);

                for (ProductOptionWriteDTO option : dto.getOptions()) {
                    int optionId = nextVal(conn, "SEQ_OPTION");
                    insertOptionRow(conn, optionId, productNo, option);

                    if (option.getMainImageUrl() != null) {
                        insertImageRow(conn, productNo, optionId, "대표", 1, option.getMainImageUrl());
                    }

                    int order = 1;
                    for (String extraImageUrl : option.getExtraImageUrls()) {
                        insertImageRow(conn, productNo, optionId, "추가", order++, extraImageUrl);
                    }
                }

                int detailOrder = 1;
                for (String detailImageUrl : dto.getDetailImageUrls()) {
                    insertImageRow(conn, productNo, null, "상세설명", detailOrder++, detailImageUrl);
                }

                conn.commit();
                return productNo;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private int nextVal(Connection conn, String sequenceName) throws Exception {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT " + sequenceName + ".NEXTVAL FROM DUAL");
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void insertProductRow(Connection conn, int productNo, ProductWriteDTO dto) throws Exception {

        String sql = """
            INSERT INTO PRODUCT (
                product_no, seller_no, sub_category_no,
                sale_method, brand_name, no_brand_yn, product_name, internal_name,
                option_yn, product_price, quantity,
                detail_type, product_desc,
                shipping_zipcode, shipping_address, shipping_detail_address, jeju_shipping_yn,
                delivery_service_code, delivery_method, bundle_shipping_yn,
                shipping_fee_type, shipping_fee,
                lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
                sale_status, created_date, updated_date
            ) VALUES (
                ?, ?, ?,
                ?, ?, ?, ?, ?,
                ?, ?, 0,
                ?, NULL,
                ?, ?, ?, ?,
                ?, ?, ?,
                ?, 0,
                ?, ?, ?, ?,
                ?, SYSDATE, SYSDATE
            )
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;

            pstmt.setInt(i++, productNo);
            pstmt.setInt(i++, dto.getSellerNo());
            pstmt.setInt(i++, dto.getSubCategoryNo());

            pstmt.setString(i++, dto.getSaleMethod());
            pstmt.setString(i++, dto.getBrandName());
            pstmt.setString(i++, dto.getNoBrandYn());
            pstmt.setString(i++, dto.getProductName());
            pstmt.setString(i++, dto.getInternalName());

            pstmt.setString(i++, dto.getOptionYn());
            pstmt.setInt(i++, dto.getProductPrice());

            pstmt.setString(i++, dto.getDetailType());

            pstmt.setString(i++, dto.getShippingZipcode());
            pstmt.setString(i++, dto.getShippingAddress());
            pstmt.setString(i++, dto.getShippingDetailAddress());
            pstmt.setString(i++, dto.getJejuShippingYn());

            pstmt.setString(i++, dto.getDeliveryServiceCode());
            pstmt.setString(i++, dto.getDeliveryMethod());
            pstmt.setString(i++, dto.getBundleShippingYn());

            pstmt.setString(i++, dto.getShippingFeeType());

            pstmt.setString(i++, dto.getLeadTimeInputType());
            if (dto.getLeadTimeDays() != null) {
                pstmt.setInt(i++, dto.getLeadTimeDays());
            } else {
                pstmt.setNull(i++, java.sql.Types.NUMERIC);
            }
            pstmt.setString(i++, dto.getSameDayShipYn());
            pstmt.setString(i++, dto.getSameDayCutoffTime());

            pstmt.setString(i++, dto.getSaleStatus());

            pstmt.executeUpdate();
        }
    }

    private void insertOptionRow(Connection conn, int optionId, int productNo, ProductOptionWriteDTO option) throws Exception {

        String sql = """
            INSERT INTO PRODUCT_OPTION (
                OPTION_ID, product_no,
                OPTION1_TYPE, OPTION1_VALUE, OPTION2_TYPE, OPTION2_VALUE, OPTION3_TYPE, OPTION3_VALUE,
                normal_price, PRICE, auto_price_adjust_yn, quantity,
                seller_product_code, model_no, barcode, STATUS
            ) VALUES (
                ?, ?,
                ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?, ?
            )
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;

            pstmt.setInt(i++, optionId);
            pstmt.setInt(i++, productNo);

            pstmt.setString(i++, option.getOption1Type());
            pstmt.setString(i++, option.getOption1Value());
            pstmt.setString(i++, option.getOption2Type());
            pstmt.setString(i++, option.getOption2Value());
            pstmt.setString(i++, option.getOption3Type());
            pstmt.setString(i++, option.getOption3Value());

            if (option.getNormalPrice() != null) {
                pstmt.setInt(i++, option.getNormalPrice());
            } else {
                pstmt.setNull(i++, java.sql.Types.NUMERIC);
            }
            pstmt.setInt(i++, option.getSalePrice());
            pstmt.setString(i++, option.getAutoPriceAdjustYn());
            pstmt.setInt(i++, option.getQuantity());

            pstmt.setString(i++, blankToNull(option.getSellerProductCode()));
            pstmt.setString(i++, blankToNull(option.getModelNo()));
            pstmt.setString(i++, blankToNull(option.getBarcode()));
            pstmt.setString(i++, option.getQuantity() > 0 ? "Y" : "N");

            pstmt.executeUpdate();
        }
    }

    private void insertImageRow(Connection conn, int productNo, Integer optionId, String purpose, int order, String imageUrl) throws Exception {

        int imageNo = nextVal(conn, "SEQ_PRODUCT_IMAGE");

        String sql = """
            INSERT INTO PRODUCT_IMAGE (
                image_no, product_no, OPTION_ID, image_purpose, image_order, image_url, created_date
            ) VALUES (?, ?, ?, ?, ?, ?, SYSDATE)
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;

            pstmt.setInt(i++, imageNo);
            pstmt.setInt(i++, productNo);
            if (optionId != null) {
                pstmt.setInt(i++, optionId);
            } else {
                pstmt.setNull(i++, java.sql.Types.NUMERIC);
            }
            pstmt.setString(i++, purpose);
            pstmt.setInt(i++, order);
            pstmt.setString(i++, imageUrl);

            pstmt.executeUpdate();
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
