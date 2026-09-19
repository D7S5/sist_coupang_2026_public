package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.goodpang.dto.ReviewAvailableDTO;
import com.goodpang.dto.ReviewDTO;
import com.goodpang.dto.ReviewDTO2;
import com.goodpang.dto.ReviewItemDTO;
import com.goodpang.dto.ReviewRatingSummaryDTO;
import com.goodpang.util.ConnectionProvider;

public class ReviewDAO {

	public List<ReviewDTO2> selectReviewsByProductNo2(int productNo) {

	    String sql = """
	        SELECT
	            r.REVIEW_NO,
	            r.PRODUCT_RATING,
	            r.SERVICE_RATING,
	            r.REVIEW_CONTENT,
	            r.REVIEW_SUMMARY,
	            r.REVIEW_DATE,
	            r.MEMBER_NO,
	            r.ORDER_DETAIL_NO,
	            m.MEMBER_NAME,
	            po.OPTION1_TYPE,
	            po.OPTION1_VALUE,
	            po.OPTION2_TYPE,
	            po.OPTION2_VALUE
	        FROM REVIEW r
	        JOIN ORDER_DETAIL od
	          ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
	        JOIN MEMBER m
	          ON r.MEMBER_NO = m.MEMBER_NO
	        LEFT JOIN PRODUCT_OPTION po
	          ON od.OPTION_ID = po.OPTION_ID
	        WHERE od.PRODUCT_NO = ?
	        ORDER BY r.REVIEW_DATE DESC
	        """;

	    List<ReviewDTO2> list = new ArrayList<>();

	    try (
	        Connection conn =
	            ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	            conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(1, productNo);

	        try (ResultSet rs = pstmt.executeQuery()) {


	            while (rs.next()) {

	                ReviewDTO2 dto = new ReviewDTO2();

	                /* 리뷰 번호 */
	                dto.setReviewNo(
	                    rs.getInt("REVIEW_NO")
	                );

	                /* 상품 별점 */
	                int productRating =
	                    rs.getInt("PRODUCT_RATING");

	                dto.setProductRating(productRating);


	                /* 별 표시 */
	                StringBuilder stars =
	                    new StringBuilder();

	                for (int i = 0; i < productRating; i++) {
	                    stars.append("★");
	                }

	                for (int i = productRating; i < 5; i++) {
	                    stars.append("☆");
	                }

	                dto.setRatingStars(
	                    stars.toString()
	                );


	                /* 서비스 만족도 */
	                int serviceRating =
	                    rs.getInt("SERVICE_RATING");

	                if (rs.wasNull()) {
	                    dto.setServiceRating(null);
	                } else {
	                    dto.setServiceRating(
	                        serviceRating
	                    );
	                }


	                /* 리뷰 내용 */
	                dto.setReviewContent(
	                    rs.getString("REVIEW_CONTENT")
	                );


	                /* 한줄 요약 */
	                dto.setReviewSummary(
	                    rs.getString("REVIEW_SUMMARY")
	                );


	                /* 작성일 */
	                dto.setReviewDate(
	                    rs.getDate("REVIEW_DATE")
	                );


	                /* 회원번호 */
	                dto.setMemberNo(
	                    rs.getInt("MEMBER_NO")
	                );


	                /* 주문 상세번호 */
	                dto.setOrderDetailNo(
	                    rs.getInt("ORDER_DETAIL_NO")
	                );


	                /* 회원 이름 마스킹 */
	                String memberName =
	                    rs.getString("MEMBER_NAME");

	                dto.setMaskedName(
	                    maskName(memberName)
	                );


	                /* 상품 옵션 */
	                String option1Type =
	                    rs.getString("OPTION1_TYPE");

	                String option1Value =
	                    rs.getString("OPTION1_VALUE");

	                String option2Type =
	                    rs.getString("OPTION2_TYPE");

	                String option2Value =
	                    rs.getString("OPTION2_VALUE");


	                StringBuilder option =
	                    new StringBuilder();


	                if (option1Value != null
	                        && !option1Value.isBlank()) {

	                    if (option1Type != null) {
	                        option.append(option1Type)
	                              .append(" ");
	                    }

	                    option.append(option1Value);
	                }


	                if (option2Value != null
	                        && !option2Value.isBlank()) {

	                    if (option.length() > 0) {
	                        option.append(" / ");
	                    }

	                    if (option2Type != null) {
	                        option.append(option2Type)
	                              .append(" ");
	                    }

	                    option.append(option2Value);
	                }


	                dto.setOptionText(
	                    option.toString()
	                );


	                list.add(dto);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	 public List<ReviewDTO> selectReviewsByProductNo(int productNo) {

	        // 2026-08-27: 리뷰 카드에 판매자 상호명·상품명도 나와야 해서 PRODUCT·SELLER 조인 추가.
	        // (지금은 상품 상세페이지라 값이 페이지 전체에서 다 같지만, 리뷰 카드 자체에도 있어야 한다는
	        //  요구라 조인해서 내려줌 — PRODUCT/SELLER 는 od.PRODUCT_NO 기준으로 항상 있는 값이라 INNER JOIN)
	        String sql = """
	            SELECT r.REVIEW_NO, r.PRODUCT_RATING, r.REVIEW_CONTENT, r.REVIEW_DATE, r.REVIEW_SUMMARY,
	                   m.MEMBER_NAME,
	                   p.PRODUCT_NAME,
	                   s.STORE_NAME,
	                   po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE
	            FROM REVIEW r
	            JOIN ORDER_DETAIL od ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
	            JOIN MEMBER m ON r.MEMBER_NO = m.MEMBER_NO
	            JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO
	            JOIN SELLER s ON p.SELLER_NO = s.SELLER_NO
	            LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID
	            WHERE od.PRODUCT_NO = ?
	            ORDER BY r.REVIEW_DATE DESC
	            """;

	        List<ReviewDTO> list = new ArrayList<>();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

	        try (
	            Connection conn = ConnectionProvider.getConnection();
	            PreparedStatement pstmt = conn.prepareStatement(sql);
	        ) {
	            pstmt.setInt(1, productNo);

	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    ReviewDTO dto = new ReviewDTO();
	                    dto.setReviewNo(rs.getInt("REVIEW_NO"));

	                    /* 2026-08-27: 별점을 "★★★★☆" 문자열로 미리 만들어두던 것 → 화면이
	                       스프라이트 이미지(width%)로 바뀌면서 필요없어짐. rating 정수만 내려주면
	                       product.jsp 에서 ${r.rating * 20}% 로 그때그때 계산해서 씀 */
	                    dto.setRating(rs.getInt("PRODUCT_RATING"));

	                    dto.setReviewContent(rs.getString("REVIEW_CONTENT"));
	                    dto.setReviewSummary(rs.getString("REVIEW_SUMMARY"));
	                    dto.setReviewDate(sdf.format(rs.getDate("REVIEW_DATE")));
	                    dto.setMaskedName(maskName(rs.getString("MEMBER_NAME")));
	                    dto.setProductName(rs.getString("PRODUCT_NAME"));
	                    dto.setStoreName(rs.getString("STORE_NAME"));

	                    String o1v = rs.getString("OPTION1_VALUE");
	                    String o2v = rs.getString("OPTION2_VALUE");
	                    StringBuilder option = new StringBuilder();
	                    if (o1v != null) option.append(rs.getString("OPTION1_TYPE")).append(" ").append(o1v);
	                    if (o2v != null) {
	                        if (option.length() > 0) option.append(" / ");
	                        option.append(rs.getString("OPTION2_TYPE")).append(" ").append(o2v);
	                    }
	                    dto.setOptionText(option.toString());

	                    list.add(dto);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return list;
	    }

    // "홍길동" → "홍*동", "이연" → "이*"
    private String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) sb.append("*");
        sb.append(name.charAt(name.length() - 1));
        return sb.toString();
    }
    
    
    public ReviewItemDTO getReviewItem(
            int orderDetailNo,
            int productNo,
            int memberNo) {

        ReviewItemDTO dto = null;

        String sql = """
            SELECT
                od.ORDER_DETAIL_NO,
                od.PRODUCT_NO,
                p.PRODUCT_NAME,
                od.OPTION_ID
            FROM ORDER_DETAIL od
            JOIN ORDERS o
              ON o.ORDER_NO = od.ORDER_NO
            JOIN PRODUCT p
              ON p.PRODUCT_NO = od.PRODUCT_NO
            WHERE od.ORDER_DETAIL_NO = ?
              AND od.PRODUCT_NO = ?
              AND o.MEMBER_NO = ?
            """;

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderDetailNo);
            pstmt.setInt(2, productNo);
            pstmt.setInt(3, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    dto = new ReviewItemDTO();

                    dto.setOrderDetailNo(
                        rs.getInt("ORDER_DETAIL_NO")
                    );

                    dto.setProductNo(
                        rs.getInt("PRODUCT_NO")
                    );

                    dto.setProductName(
                        rs.getString("PRODUCT_NAME")
                    );

                    /*
                     * 현재 OPTION_NAME 조회 테이블을
                     * 모르기 때문에 일단 OPTION_ID만 조회.
                     *
                     * 옵션 테이블 구조에 맞춰 JOIN 후
                     * optionName을 넣으면 된다.
                     */
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "리뷰 대상 상품 조회 중 오류가 발생했습니다.",
                e
            );
        } catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        return dto;
    }


    /*
     * 이미 작성한 리뷰인지 확인
     */
    public boolean existsByOrderDetailNo(
            int orderDetailNo) {

        String sql = """
            SELECT COUNT(*)
            FROM REVIEW
            WHERE ORDER_DETAIL_NO = ?
            """;

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderDetailNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "리뷰 중복 확인 중 오류가 발생했습니다.",
                e
            );
        } catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        return false;
    }


    /*
     * 리뷰 등록
     *
     * 등록된 REVIEW_NO를 반환한다.
     */
    public int insertReview(ReviewDTO dto) {

        String sequenceSql = """
            SELECT SEQ_REVIEW_NO.NEXTVAL
            FROM DUAL
            """;

        String insertSql = """
            INSERT INTO REVIEW (
                REVIEW_NO,
                PRODUCT_RATING,
                SERVICE_RATING,
                REVIEW_CONTENT,
                REVIEW_SUMMARY,
                REVIEW_DATE,
                MEMBER_NO,
                ORDER_DETAIL_NO
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                SYSDATE,
                ?,
                ?
            )
            """;

        Connection conn = null;

        try {

            conn =
                ConnectionProvider.getConnection();

            conn.setAutoCommit(false);

            int reviewNo;


            // 1. 리뷰 번호 생성
            try (
                PreparedStatement pstmt =
                    conn.prepareStatement(sequenceSql);

                ResultSet rs =
                    pstmt.executeQuery()
            ) {

                if (!rs.next()) {
                    throw new SQLException(
                        "리뷰 번호 생성에 실패했습니다."
                    );
                }

                reviewNo = rs.getInt(1);
            }


            // 2. 리뷰 저장
            try (
                PreparedStatement pstmt =
                    conn.prepareStatement(insertSql)
            ) {

                pstmt.setInt(
                    1,
                    reviewNo
                );

                pstmt.setInt(
                    2,
                    dto.getProductRating()
                );


                // 서비스 만족도는 선택값이라면 null 허용
                if (dto.getServiceRating() == null) {

                    pstmt.setNull(
                        3,
                        java.sql.Types.NUMERIC
                    );

                } else {

                    pstmt.setInt(
                        3,
                        dto.getServiceRating()
                    );
                }


                pstmt.setString(
                    4,
                    dto.getReviewContent()
                );

                pstmt.setString(
                    5,
                    dto.getReviewSummary()
                );

                pstmt.setInt(
                    6,
                    dto.getMemberNo()
                );

                pstmt.setInt(
                    7,
                    dto.getOrderDetailNo()
                );


                int rowCount =
                    pstmt.executeUpdate();

                if (rowCount != 1) {

                    throw new SQLException(
                        "리뷰 등록에 실패했습니다."
                    );
                }
            }


            conn.commit();

            return reviewNo;

        } catch (SQLException e) {

            if (conn != null) {

                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            throw new RuntimeException(
                "리뷰 등록 중 오류가 발생했습니다.",
                e
            );

        } catch (NamingException e) {
			e.printStackTrace();
		} finally {

            if (conn != null) {

                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
		return 0;
    }
    
    public List<ReviewDTO2> selectReviewsByMemberNo(int memberNo) {

        String sql = """
            SELECT
                r.REVIEW_NO,
                r.PRODUCT_RATING,
                r.SERVICE_RATING,
                r.REVIEW_CONTENT,
                r.REVIEW_SUMMARY,
                r.REVIEW_DATE,
                r.MEMBER_NO,
                r.ORDER_DETAIL_NO,
                od.PRODUCT_NO,
                p.PRODUCT_NAME,
                po.OPTION1_TYPE,
                po.OPTION1_VALUE,
                po.OPTION2_TYPE,
                po.OPTION2_VALUE
            FROM REVIEW r
            JOIN ORDER_DETAIL od
              ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
            JOIN PRODUCT p
              ON od.PRODUCT_NO = p.PRODUCT_NO
            LEFT JOIN PRODUCT_OPTION po
              ON od.OPTION_ID = po.OPTION_ID
            WHERE r.MEMBER_NO = ?
            ORDER BY r.REVIEW_DATE DESC
            """;

        List<ReviewDTO2> list =
            new ArrayList<>();

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ReviewDTO2 dto =
                        new ReviewDTO2();

                    dto.setReviewNo(
                        rs.getInt("REVIEW_NO")
                    );

                    int productRating =
                        rs.getInt("PRODUCT_RATING");

                    dto.setProductRating(
                        productRating
                    );

                    // 별점 문자열
                    StringBuilder stars =
                        new StringBuilder();

                    for (int i = 0;
                         i < productRating;
                         i++) {

                        stars.append("★");
                    }

                    for (int i = productRating;
                         i < 5;
                         i++) {

                        stars.append("☆");
                    }

                    dto.setRatingStars(
                        stars.toString()
                    );


                    int serviceRating =
                        rs.getInt("SERVICE_RATING");

                    if (rs.wasNull()) {
                        dto.setServiceRating(null);
                    } else {
                        dto.setServiceRating(
                            serviceRating
                        );
                    }


                    dto.setReviewContent(
                        rs.getString(
                            "REVIEW_CONTENT"
                        )
                    );

                    dto.setReviewSummary(
                        rs.getString(
                            "REVIEW_SUMMARY"
                        )
                    );

                    dto.setReviewDate(
                        rs.getDate(
                            "REVIEW_DATE"
                        )
                    );

                    dto.setMemberNo(
                        rs.getInt(
                            "MEMBER_NO"
                        )
                    );

                    dto.setOrderDetailNo(
                        rs.getInt(
                            "ORDER_DETAIL_NO"
                        )
                    );
                    
                    dto.setProductNo(
                    	    rs.getInt("PRODUCT_NO")
                    	);

                	dto.setProductName(
                	    rs.getString("PRODUCT_NAME")
                	);


                    // 옵션
                    String option1Type =
                        rs.getString(
                            "OPTION1_TYPE"
                        );

                    String option1Value =
                        rs.getString(
                            "OPTION1_VALUE"
                        );

                    String option2Type =
                        rs.getString(
                            "OPTION2_TYPE"
                        );

                    String option2Value =
                        rs.getString(
                            "OPTION2_VALUE"
                        );

                    StringBuilder option =
                        new StringBuilder();

                    if (option1Value != null
                            && !option1Value.isBlank()) {

                        if (option1Type != null) {
                            option.append(
                                option1Type
                            ).append(" ");
                        }

                        option.append(
                            option1Value
                        );
                    }

                    if (option2Value != null
                            && !option2Value.isBlank()) {

                        if (option.length() > 0) {
                            option.append(" / ");
                        }

                        if (option2Type != null) {
                            option.append(
                                option2Type
                            ).append(" ");
                        }

                        option.append(
                            option2Value
                        );
                    }

                    dto.setOptionText(
                        option.toString()
                    );

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public int countReviewsByMemberNo(int memberNo) {

        String sql = """
            SELECT COUNT(*)
            FROM REVIEW
            WHERE MEMBER_NO = ?
            """;

        int count = 0;

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    
	/*
	 * public int deleteReview( int reviewNo, int memberNo) {
	 * 
	 * String sql = """ DELETE FROM REVIEW WHERE REVIEW_NO = ? AND MEMBER_NO = ?
	 * """;
	 * 
	 * int rowCount = 0;
	 * 
	 * try ( Connection conn = ConnectionProvider.getConnection();
	 * 
	 * PreparedStatement pstmt = conn.prepareStatement(sql) ) {
	 * 
	 * pstmt.setInt(1, reviewNo); pstmt.setInt(2, memberNo);
	 * 
	 * rowCount = pstmt.executeUpdate();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return rowCount; }
	 */
    
    public int deleteReview(
            int reviewNo,
            int memberNo) throws Exception {

        try (Connection conn =
                ConnectionProvider.getConnection()) {

            try {

                conn.setAutoCommit(false);

                String imageSql = """
                    DELETE FROM REVIEW_IMAGE
                    WHERE REVIEW_NO = ?
                    """;

                try (PreparedStatement pstmt =
                        conn.prepareStatement(imageSql)) {

                    pstmt.setInt(1, reviewNo);
                    pstmt.executeUpdate();
                }

                String reviewSql = """
                    DELETE FROM REVIEW
                    WHERE REVIEW_NO = ?
                      AND MEMBER_NO = ?
                    """;

                int rowCount;

                try (PreparedStatement pstmt =
                        conn.prepareStatement(reviewSql)) {

                    pstmt.setInt(1, reviewNo);
                    pstmt.setInt(2, memberNo);

                    rowCount =
                        pstmt.executeUpdate();
                }

                conn.commit();

                return rowCount;

            } catch (Exception e) {

                conn.rollback();
                throw e;
            }
        }
    }
    
    public ReviewDTO2 selectReviewByNo(
            int reviewNo,
            int memberNo) {

        String sql = """
            SELECT
                r.REVIEW_NO,
                r.PRODUCT_RATING,
                r.SERVICE_RATING,
                r.REVIEW_CONTENT,
                r.REVIEW_SUMMARY,
                r.REVIEW_DATE,
                r.MEMBER_NO,
                r.ORDER_DETAIL_NO,
                od.PRODUCT_NO,
                p.PRODUCT_NAME
            FROM REVIEW r
            JOIN ORDER_DETAIL od
              ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
            JOIN PRODUCT p
              ON od.PRODUCT_NO = p.PRODUCT_NO
            WHERE r.REVIEW_NO = ?
              AND r.MEMBER_NO = ?
            """;

        ReviewDTO2 dto = null;

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, reviewNo);
            pstmt.setInt(2, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    dto = new ReviewDTO2();

                    dto.setReviewNo(
                        rs.getInt("REVIEW_NO")
                    );

                    dto.setProductRating(
                        rs.getInt("PRODUCT_RATING")
                    );

                    int serviceRating =
                        rs.getInt("SERVICE_RATING");

                    if (rs.wasNull()) {
                        dto.setServiceRating(null);
                    } else {
                        dto.setServiceRating(
                            serviceRating
                        );
                    }

                    dto.setReviewContent(
                        rs.getString(
                            "REVIEW_CONTENT"
                        )
                    );

                    dto.setReviewSummary(
                        rs.getString(
                            "REVIEW_SUMMARY"
                        )
                    );

                    dto.setReviewDate(
                        rs.getDate(
                            "REVIEW_DATE"
                        )
                    );

                    dto.setMemberNo(
                        rs.getInt(
                            "MEMBER_NO"
                        )
                    );

                    dto.setOrderDetailNo(
                        rs.getInt(
                            "ORDER_DETAIL_NO"
                        )
                    );

                    dto.setProductNo(
                        rs.getInt(
                            "PRODUCT_NO"
                        )
                    );

                    dto.setProductName(
                        rs.getString(
                            "PRODUCT_NAME"
                        )
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
    
    public int updateReview(
            ReviewDTO dto,
            int memberNo) {

        String sql = """
            UPDATE REVIEW
               SET PRODUCT_RATING = ?,
                   SERVICE_RATING = ?,
                   REVIEW_CONTENT = ?,
                   REVIEW_SUMMARY = ?
             WHERE REVIEW_NO = ?
               AND MEMBER_NO = ?
            """;

        int rowCount = 0;

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(
                1,
                dto.getProductRating()
            );

            if (dto.getServiceRating() == null) {
                pstmt.setNull(
                    2,
                    java.sql.Types.INTEGER
                );
            } else {
                pstmt.setInt(
                    2,
                    dto.getServiceRating()
                );
            }

            pstmt.setString(
                3,
                dto.getReviewContent()
            );

            pstmt.setString(
                4,
                dto.getReviewSummary()
            );

            pstmt.setInt(
                5,
                dto.getReviewNo()
            );

            pstmt.setInt(
                6,
                memberNo
            );

            rowCount =
                pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowCount;
    }
    
	/*
	 * public List<ReviewItemDTO> selectAvailableReviewsByMemberNo( int memberNo) {
	 * 
	 * String sql = """ SELECT od.ORDER_DETAIL_NO, od.PRODUCT_NO, p.PRODUCT_NAME,
	 * po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE FROM
	 * ORDER_DETAIL od JOIN ORDERS o ON od.ORDER_NO = o.ORDER_NO JOIN PRODUCT p ON
	 * od.PRODUCT_NO = p.PRODUCT_NO LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID =
	 * po.OPTION_ID LEFT JOIN REVIEW r ON od.ORDER_DETAIL_NO = r.ORDER_DETAIL_NO
	 * WHERE o.MEMBER_NO = ? AND r.REVIEW_NO IS NULL ORDER BY o.ORDER_DATE DESC,
	 * od.ORDER_DETAIL_NO DESC """;
	 * 
	 * List<ReviewItemDTO> list = new ArrayList<>();
	 * 
	 * try ( Connection conn = ConnectionProvider.getConnection();
	 * 
	 * PreparedStatement pstmt = conn.prepareStatement(sql) ) {
	 * 
	 * pstmt.setInt(1, memberNo);
	 * 
	 * try (ResultSet rs = pstmt.executeQuery()) {
	 * 
	 * while (rs.next()) {
	 * 
	 * ReviewItemDTO dto = new ReviewItemDTO();
	 * 
	 * dto.setOrderDetailNo( rs.getInt("ORDER_DETAIL_NO") );
	 * 
	 * dto.setProductNo( rs.getInt("PRODUCT_NO") );
	 * 
	 * dto.setProductName( rs.getString("PRODUCT_NAME") );
	 * 
	 * String option1Type = rs.getString("OPTION1_TYPE");
	 * 
	 * String option1Value = rs.getString("OPTION1_VALUE");
	 * 
	 * String option2Type = rs.getString("OPTION2_TYPE");
	 * 
	 * String option2Value = rs.getString("OPTION2_VALUE");
	 * 
	 * StringBuilder option = new StringBuilder();
	 * 
	 * if (option1Value != null && !option1Value.isBlank()) {
	 * 
	 * if (option1Type != null && !option1Type.isBlank()) {
	 * 
	 * option.append(option1Type) .append(" "); }
	 * 
	 * option.append(option1Value); }
	 * 
	 * if (option2Value != null && !option2Value.isBlank()) {
	 * 
	 * if (option.length() > 0) { option.append(", "); }
	 * 
	 * if (option2Type != null && !option2Type.isBlank()) {
	 * 
	 * option.append(option2Type) .append(" "); }
	 * 
	 * option.append(option2Value); }
	 * 
	 * dto.setOptionName( option.toString() );
	 * 
	 * list.add(dto); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return list; }
	 */
    
	/*
	 * public List<ReviewAvailableDTO> getReviewStatus(int memberNo) {
	 * 
	 * List<ReviewAvailableDTO> list = new ArrayList<>();
	 * 
	 * String sql = """ SELECT od.ORDER_DETAIL_NO, od.PRODUCT_NO, p.PRODUCT_NAME,
	 * CASE WHEN EXISTS ( SELECT 1 FROM REVIEW r WHERE r.ORDER_DETAIL_NO =
	 * od.ORDER_DETAIL_NO ) THEN 1 ELSE 0 END AS REVIEW_WRITTEN FROM ORDER_DETAIL od
	 * JOIN ORDERS o ON o.ORDER_NO = od.ORDER_NO JOIN PRODUCT p ON p.PRODUCT_NO =
	 * od.PRODUCT_NO WHERE o.MEMBER_NO = ? """;
	 * 
	 * try ( Connection conn = ConnectionProvider.getConnection(); PreparedStatement
	 * pstmt = conn.prepareStatement(sql); ) {
	 * 
	 * pstmt.setInt(1, memberNo);
	 * 
	 * try (ResultSet rs = pstmt.executeQuery()) {
	 * 
	 * while (rs.next()) {
	 * 
	 * ReviewAvailableDTO dto = new ReviewAvailableDTO();
	 * 
	 * dto.setOrderDetailNo( rs.getInt("ORDER_DETAIL_NO"));
	 * 
	 * dto.setProductNo( rs.getInt("PRODUCT_NO"));
	 * 
	 * dto.setProductName( rs.getString("PRODUCT_NAME"));
	 * 
	 * dto.setReviewWritten( rs.getInt("REVIEW_WRITTEN") == 1);
	 * 
	 * list.add(dto); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return list; }
	 */
    
    public List<ReviewAvailableDTO> getReviewStatus(int memberNo) {

    	List<ReviewAvailableDTO> list = new ArrayList<>();

    	String sql = """
    			SELECT
    				od.ORDER_DETAIL_NO,
    				od.PRODUCT_NO,
    				p.PRODUCT_NAME,
    				po.OPTION1_TYPE,
    				po.OPTION1_VALUE,
    				po.OPTION2_TYPE,
    				po.OPTION2_VALUE,
    				CASE
    					WHEN EXISTS (
    						SELECT 1
    						FROM REVIEW r
    						WHERE r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
    					)
    					THEN 1
    					ELSE 0
    				END AS REVIEW_WRITTEN
    			FROM ORDER_DETAIL od
    			JOIN ORDERS o
    				ON o.ORDER_NO = od.ORDER_NO
    			JOIN PRODUCT p
    				ON p.PRODUCT_NO = od.PRODUCT_NO
    			LEFT JOIN PRODUCT_OPTION po
    				ON od.OPTION_ID = po.OPTION_ID
    			WHERE o.MEMBER_NO = ?
    			ORDER BY o.ORDER_DATE DESC,
    					 od.ORDER_DETAIL_NO DESC
    			""";

    	try (
    		Connection conn = ConnectionProvider.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)
    	) {
    		pstmt.setInt(1, memberNo);

    		try (ResultSet rs = pstmt.executeQuery()) {

    			while (rs.next()) {

    				ReviewAvailableDTO dto = new ReviewAvailableDTO();

    				dto.setOrderDetailNo(
    						rs.getInt("ORDER_DETAIL_NO")
    				);

    				dto.setProductNo(
    						rs.getInt("PRODUCT_NO")
    				);

    				dto.setProductName(
    						rs.getString("PRODUCT_NAME")
    				);

    				dto.setReviewWritten(
    						rs.getInt("REVIEW_WRITTEN") == 1
    				);

    				String option1Type =
    						rs.getString("OPTION1_TYPE");

    				String option1Value =
    						rs.getString("OPTION1_VALUE");

    				String option2Type =
    						rs.getString("OPTION2_TYPE");

    				String option2Value =
    						rs.getString("OPTION2_VALUE");

    				StringBuilder option =
    						new StringBuilder();

    				if (option1Value != null
    						&& !option1Value.isBlank()) {

    					if (option1Type != null
    							&& !option1Type.isBlank()) {

    						option.append(option1Type)
    							  .append(" ");
    					}

    					option.append(option1Value);
    				}

    				if (option2Value != null
    						&& !option2Value.isBlank()) {

    					if (option.length() > 0) {
    						option.append(" / ");
    					}

    					if (option2Type != null
    							&& !option2Type.isBlank()) {

    						option.append(option2Type)
    							  .append(" ");
    					}

    					option.append(option2Value);
    				}

    				dto.setOptionText(
    						option.toString()
    				);

    				list.add(dto);
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(
    				"리뷰 상태 조회 중 오류가 발생했습니다.",
    				e
    		);
    	}

    	return list;
    }
    
    public int insertReview(
            Connection conn,
            int memberNo,
            int orderDetailNo,
            int productNo,
            Integer serviceRating,
            int productRating,
            String reviewContent,
            String reviewSummary)
            throws Exception {

        int reviewNo = 0;

        String seqSql =
                "SELECT SEQ_REVIEW_NO.NEXTVAL FROM DUAL";

        try (
            PreparedStatement pstmt =
                    conn.prepareStatement(seqSql);

            ResultSet rs =
                    pstmt.executeQuery()
        ) {

            if (rs.next()) {
                reviewNo =
                        rs.getInt(1);
            }
        }

        String sql = """
                INSERT INTO REVIEW (
                    REVIEW_NO,
                    MEMBER_NO,
                    ORDER_DETAIL_NO,
                    PRODUCT_NO,
                    SERVICE_RATING,
                    PRODUCT_RATING,
                    REVIEW_CONTENT,
                    REVIEW_SUMMARY,
                    REVIEW_DATE
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE
                )
                """;

        try (PreparedStatement pstmt =
                conn.prepareStatement(sql)) {

            pstmt.setInt(1, reviewNo);
            pstmt.setInt(2, memberNo);
            pstmt.setInt(3, orderDetailNo);
            pstmt.setInt(4, productNo);

            if (serviceRating == null) {

                pstmt.setNull(
                        5,
                        java.sql.Types.INTEGER
                );

            } else {

                pstmt.setInt(
                        5,
                        serviceRating
                );
            }

            pstmt.setInt(
                    6,
                    productRating
            );

            pstmt.setString(
                    7,
                    reviewContent
            );

            pstmt.setString(
                    8,
                    reviewSummary
            );

            pstmt.executeUpdate();
        }

        return reviewNo;
    }
    
    public int insertReviewImage(
            Connection conn,
            int reviewNo,
            String imageUrl,
            int imageOrder)
            throws Exception {

        String sql = """
                INSERT INTO REVIEW_IMAGE (
                    REVIEW_IMAGE_NO,
                    REVIEW_NO,
                    IMAGE_URL,
                    IMAGE_ORDER
                )
                VALUES (
                    SEQ_REVIEW_IMAGE_NO.NEXTVAL,
                    ?,
                    ?,
                    ?
                )
                """;

        try (PreparedStatement pstmt =
                conn.prepareStatement(sql)) {

            pstmt.setInt(
                    1,
                    reviewNo
            );

            pstmt.setString(
                    2,
                    imageUrl
            );

            pstmt.setInt(
                    3,
                    imageOrder
            );

            return pstmt.executeUpdate();
        }
    }
    
    public int insertReviewImage(
    		int reviewNo,
    		String imageUrl,
    		int imageOrder) {

    	String sql = """
    			INSERT INTO REVIEW_IMAGE (
    				REVIEW_IMAGE_NO,
    				REVIEW_NO,
    				IMAGE_URL,
    				IMAGE_ORDER
    			)
    			VALUES (
    				SEQ_REVIEW_IMAGE_NO.NEXTVAL,
    				?,
    				?,
    				?
    			)
    			""";

    	try (
    		Connection conn = ConnectionProvider.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)
    	) {
    		pstmt.setInt(1, reviewNo);
    		pstmt.setString(2, imageUrl);
    		pstmt.setInt(3, imageOrder);

    		return pstmt.executeUpdate();

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException("리뷰 이미지 등록 실패", e);
    	}
    }
    public List<String> getReviewImages(int reviewNo) {

    	List<String> list = new ArrayList<>();

    	String sql = """
    			SELECT IMAGE_URL
    			FROM REVIEW_IMAGE
    			WHERE REVIEW_NO = ?
    			ORDER BY IMAGE_ORDER
    			""";

    	try (
    		Connection conn = ConnectionProvider.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)
    	) {
    		pstmt.setInt(1, reviewNo);

    		try (ResultSet rs = pstmt.executeQuery()) {
    			while (rs.next()) {
    				list.add(rs.getString("IMAGE_URL"));
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException("리뷰 이미지 조회 실패", e);
    	}

    	return list;
    }
    
    public ReviewRatingSummaryDTO getRatingSummary(int productNo) {

        ReviewRatingSummaryDTO dto = new ReviewRatingSummaryDTO();

        String sql = """
                SELECT
                    COUNT(*) AS REVIEW_COUNT,
                    NVL(ROUND(AVG(r.PRODUCT_RATING), 1), 0) AS AVG_RATING,
                    CASE WHEN COUNT(*) = 0 THEN 0
                         ELSE ROUND(SUM(CASE WHEN r.PRODUCT_RATING = 5 THEN 1 ELSE 0 END) * 100 / COUNT(*))
                    END AS BEST_PERCENT,
                    CASE WHEN COUNT(*) = 0 THEN 0
                         ELSE ROUND(SUM(CASE WHEN r.PRODUCT_RATING = 4 THEN 1 ELSE 0 END) * 100 / COUNT(*))
                    END AS GOOD_PERCENT,
                    CASE WHEN COUNT(*) = 0 THEN 0
                         ELSE ROUND(SUM(CASE WHEN r.PRODUCT_RATING = 3 THEN 1 ELSE 0 END) * 100 / COUNT(*))
                    END AS NORMAL_PERCENT,
                    CASE WHEN COUNT(*) = 0 THEN 0
                         ELSE ROUND(SUM(CASE WHEN r.PRODUCT_RATING = 2 THEN 1 ELSE 0 END) * 100 / COUNT(*))
                    END AS POOR_PERCENT,
                    CASE WHEN COUNT(*) = 0 THEN 0
                         ELSE ROUND(SUM(CASE WHEN r.PRODUCT_RATING = 1 THEN 1 ELSE 0 END) * 100 / COUNT(*))
                    END AS BAD_PERCENT
                FROM REVIEW r
                JOIN ORDER_DETAIL od
                  ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
                WHERE od.PRODUCT_NO = ?
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto.setReviewCount(rs.getInt("REVIEW_COUNT"));
                    dto.setAvgRating(rs.getDouble("AVG_RATING"));
                    dto.setBestPercent(rs.getInt("BEST_PERCENT"));
                    dto.setGoodPercent(rs.getInt("GOOD_PERCENT"));
                    dto.setNormalPercent(rs.getInt("NORMAL_PERCENT"));
                    dto.setPoorPercent(rs.getInt("POOR_PERCENT"));
                    dto.setBadPercent(rs.getInt("BAD_PERCENT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
    
    public List<ReviewDTO2> selectReviewsByMemberNo(int memberNo, int offset, int pageSize) {

        String sql = """
            SELECT
                r.REVIEW_NO,
                r.PRODUCT_RATING,
                r.SERVICE_RATING,
                r.REVIEW_CONTENT,
                r.REVIEW_SUMMARY,
                r.REVIEW_DATE,
                r.MEMBER_NO,
                r.ORDER_DETAIL_NO,
                od.PRODUCT_NO,
                p.PRODUCT_NAME,
                po.OPTION1_TYPE,
                po.OPTION1_VALUE,
                po.OPTION2_TYPE,
                po.OPTION2_VALUE
            FROM REVIEW r
            JOIN ORDER_DETAIL od ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
            JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO
            LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID
            WHERE r.MEMBER_NO = ?
            ORDER BY r.REVIEW_DATE DESC, r.REVIEW_NO DESC
            OFFSET ? ROWS
            FETCH NEXT ? ROWS ONLY
            """;

        List<ReviewDTO2> list = new ArrayList<>();

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberNo);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReviewDTO2 dto = new ReviewDTO2();

                    dto.setReviewNo(rs.getInt("REVIEW_NO"));

                    int productRating = rs.getInt("PRODUCT_RATING");
                    dto.setProductRating(productRating);

                    StringBuilder stars = new StringBuilder();

                    for (int i = 0; i < productRating; i++) {
                        stars.append("★");
                    }

                    for (int i = productRating; i < 5; i++) {
                        stars.append("☆");
                    }

                    dto.setRatingStars(stars.toString());

                    int serviceRating = rs.getInt("SERVICE_RATING");

                    if (rs.wasNull()) {
                        dto.setServiceRating(null);
                    } else {
                        dto.setServiceRating(serviceRating);
                    }

                    dto.setReviewContent(rs.getString("REVIEW_CONTENT"));
                    dto.setReviewSummary(rs.getString("REVIEW_SUMMARY"));
                    dto.setReviewDate(rs.getDate("REVIEW_DATE"));
                    dto.setMemberNo(rs.getInt("MEMBER_NO"));
                    dto.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));

                    String option1Type = rs.getString("OPTION1_TYPE");
                    String option1Value = rs.getString("OPTION1_VALUE");
                    String option2Type = rs.getString("OPTION2_TYPE");
                    String option2Value = rs.getString("OPTION2_VALUE");

                    StringBuilder option = new StringBuilder();

                    if (option1Value != null && !option1Value.isBlank()) {
                        if (option1Type != null && !option1Type.isBlank()) {
                            option.append(option1Type).append(" ");
                        }
                        option.append(option1Value);
                    }

                    if (option2Value != null && !option2Value.isBlank()) {
                        if (option.length() > 0) {
                            option.append(" / ");
                        }

                        if (option2Type != null && !option2Type.isBlank()) {
                            option.append(option2Type).append(" ");
                        }

                        option.append(option2Value);
                    }

                    dto.setOptionText(option.toString());
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
	/*
	 * public List<ReviewItemDTO> selectAvailableReviewsByMemberNo(int memberNo, int
	 * offset, int pageSize) {
	 * 
	 * String sql = """ SELECT od.ORDER_DETAIL_NO, od.PRODUCT_NO, p.PRODUCT_NAME,
	 * po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE FROM
	 * ORDER_DETAIL od JOIN ORDERS o ON od.ORDER_NO = o.ORDER_NO JOIN PRODUCT p ON
	 * od.PRODUCT_NO = p.PRODUCT_NO LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID =
	 * po.OPTION_ID LEFT JOIN REVIEW r ON od.ORDER_DETAIL_NO = r.ORDER_DETAIL_NO
	 * WHERE o.MEMBER_NO = ? AND r.REVIEW_NO IS NULL ORDER BY o.ORDER_DATE DESC,
	 * od.ORDER_DETAIL_NO DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY """;
	 * 
	 * List<ReviewItemDTO> list = new ArrayList<>();
	 * 
	 * try ( Connection conn = ConnectionProvider.getConnection(); PreparedStatement
	 * pstmt = conn.prepareStatement(sql) ) { pstmt.setInt(1, memberNo);
	 * pstmt.setInt(2, offset); pstmt.setInt(3, pageSize);
	 * 
	 * try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) { ReviewItemDTO
	 * dto = new ReviewItemDTO();
	 * 
	 * dto.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
	 * dto.setProductNo(rs.getInt("PRODUCT_NO"));
	 * dto.setProductName(rs.getString("PRODUCT_NAME"));
	 * 
	 * String option1Type = rs.getString("OPTION1_TYPE"); String option1Value =
	 * rs.getString("OPTION1_VALUE"); String option2Type =
	 * rs.getString("OPTION2_TYPE"); String option2Value =
	 * rs.getString("OPTION2_VALUE");
	 * 
	 * StringBuilder option = new StringBuilder();
	 * 
	 * if (option1Value != null && !option1Value.isBlank()) { if (option1Type !=
	 * null && !option1Type.isBlank()) { option.append(option1Type).append(" "); }
	 * option.append(option1Value); }
	 * 
	 * if (option2Value != null && !option2Value.isBlank()) { if (option.length() >
	 * 0) { option.append(", "); }
	 * 
	 * if (option2Type != null && !option2Type.isBlank()) {
	 * option.append(option2Type).append(" "); }
	 * 
	 * option.append(option2Value); }
	 * 
	 * dto.setOptionName(option.toString()); list.add(dto); } } } catch (Exception
	 * e) { e.printStackTrace(); }
	 * 
	 * return list; }
	 */
    
    public List<ReviewItemDTO> selectAvailableReviewsByMemberNo(
            int memberNo) {

        String sql = """
                SELECT
                    od.ORDER_DETAIL_NO,
                    od.PRODUCT_NO,
                    p.PRODUCT_NAME,

                    po.OPTION1_TYPE,
                    po.OPTION1_VALUE,
                    po.OPTION2_TYPE,
                    po.OPTION2_VALUE,

                    pi.IMAGE_URL AS PRODUCT_IMAGE

                FROM ORDER_DETAIL od

                JOIN ORDERS o
                  ON od.ORDER_NO = o.ORDER_NO

                JOIN PRODUCT p
                  ON od.PRODUCT_NO = p.PRODUCT_NO

                LEFT JOIN PRODUCT_OPTION po
                  ON od.OPTION_ID = po.OPTION_ID

                LEFT JOIN (
                    SELECT
                        PRODUCT_NO,
                        MIN(IMAGE_URL) AS IMAGE_URL
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                    GROUP BY PRODUCT_NO
                ) pi
                  ON od.PRODUCT_NO = pi.PRODUCT_NO

                LEFT JOIN REVIEW r
                  ON od.ORDER_DETAIL_NO = r.ORDER_DETAIL_NO

                WHERE o.MEMBER_NO = ?
                  AND r.REVIEW_NO IS NULL

                ORDER BY
                    o.ORDER_DATE DESC,
                    od.ORDER_DETAIL_NO DESC
                """;

        List<ReviewItemDTO> list =
                new ArrayList<>();

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs =
                    pstmt.executeQuery()) {

                while (rs.next()) {

                    ReviewItemDTO dto =
                            new ReviewItemDTO();

                    dto.setOrderDetailNo(
                            rs.getInt(
                                    "ORDER_DETAIL_NO"
                            )
                    );

                    dto.setProductNo(
                            rs.getInt(
                                    "PRODUCT_NO"
                            )
                    );

                    dto.setProductName(
                            rs.getString(
                                    "PRODUCT_NAME"
                            )
                    );

                    dto.setProductImage(
                            rs.getString(
                                    "PRODUCT_IMAGE"
                            )
                    );

                    String option1Type =
                            rs.getString(
                                    "OPTION1_TYPE"
                            );

                    String option1Value =
                            rs.getString(
                                    "OPTION1_VALUE"
                            );

                    String option2Type =
                            rs.getString(
                                    "OPTION2_TYPE"
                            );

                    String option2Value =
                            rs.getString(
                                    "OPTION2_VALUE"
                            );

                    StringBuilder option =
                            new StringBuilder();

                    if (option1Value != null
                            && !option1Value.isBlank()) {

                        if (option1Type != null
                                && !option1Type.isBlank()) {

                            option.append(option1Type)
                                  .append(" ");
                        }

                        option.append(
                                option1Value
                        );
                    }

                    if (option2Value != null
                            && !option2Value.isBlank()) {

                        if (option.length() > 0) {
                            option.append(" / ");
                        }

                        if (option2Type != null
                                && !option2Type.isBlank()) {

                            option.append(option2Type)
                                  .append(" ");
                        }

                        option.append(
                                option2Value
                        );
                    }

                    dto.setOptionName(
                            option.toString()
                    );

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public List<ReviewItemDTO> selectAvailableReviewsByMemberNo(
            int memberNo, int offset, int pageSize) {

        String sql = """
                SELECT
                    od.ORDER_DETAIL_NO,
                    od.PRODUCT_NO,
                    p.PRODUCT_NAME,
                    po.OPTION1_TYPE,
                    po.OPTION1_VALUE,
                    po.OPTION2_TYPE,
                    po.OPTION2_VALUE,
                    pi.IMAGE_URL AS PRODUCT_IMAGE
                FROM ORDER_DETAIL od
                JOIN ORDERS o
                  ON od.ORDER_NO = o.ORDER_NO
                JOIN PRODUCT p
                  ON od.PRODUCT_NO = p.PRODUCT_NO
                LEFT JOIN PRODUCT_OPTION po
                  ON od.OPTION_ID = po.OPTION_ID
                LEFT JOIN (
                    SELECT
                        PRODUCT_NO,
                        MIN(IMAGE_URL) AS IMAGE_URL
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                    GROUP BY PRODUCT_NO
                ) pi
                  ON od.PRODUCT_NO = pi.PRODUCT_NO
                LEFT JOIN REVIEW r
                  ON od.ORDER_DETAIL_NO = r.ORDER_DETAIL_NO
                WHERE o.MEMBER_NO = ?
                  AND r.REVIEW_NO IS NULL
                ORDER BY o.ORDER_DATE DESC,
                         od.ORDER_DETAIL_NO DESC
                OFFSET ? ROWS
                FETCH NEXT ? ROWS ONLY
                """;

        List<ReviewItemDTO> list = new ArrayList<>();

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberNo);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReviewItemDTO dto = new ReviewItemDTO();

                    dto.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setProductImage(rs.getString("PRODUCT_IMAGE"));

                    String option1Type = rs.getString("OPTION1_TYPE");
                    String option1Value = rs.getString("OPTION1_VALUE");
                    String option2Type = rs.getString("OPTION2_TYPE");
                    String option2Value = rs.getString("OPTION2_VALUE");

                    StringBuilder option = new StringBuilder();

                    if (option1Value != null && !option1Value.isBlank()) {
                        if (option1Type != null && !option1Type.isBlank()) {
                            option.append(option1Type).append(" ");
                        }
                        option.append(option1Value);
                    }

                    if (option2Value != null && !option2Value.isBlank()) {
                        if (option.length() > 0) {
                            option.append(" / ");
                        }

                        if (option2Type != null && !option2Type.isBlank()) {
                            option.append(option2Type).append(" ");
                        }

                        option.append(option2Value);
                    }

                    dto.setOptionName(option.toString());
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "작성 가능한 리뷰 목록 조회 중 오류가 발생했습니다.",
                    e
            );
        }

        return list;
    }
    public int countAvailableReviewsByMemberNo(int memberNo) {

        String sql = """
            SELECT COUNT(*)
            FROM ORDER_DETAIL od
            JOIN ORDERS o ON od.ORDER_NO = o.ORDER_NO
            LEFT JOIN REVIEW r ON od.ORDER_DETAIL_NO = r.ORDER_DETAIL_NO
            WHERE o.MEMBER_NO = ?
              AND r.REVIEW_NO IS NULL
            """;

        int count = 0;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}