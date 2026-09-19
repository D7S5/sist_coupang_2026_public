package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.ReviewDTO;
import com.goodpang.dto.ReviewDTO2;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/review/edit")
public class ReviewEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDAO reviewDAO =
        new ReviewDAO();

    // 수정 페이지
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember =
            LoginUtil.requireLogin(
                request,
                response
            );

        if (loginMember == null) {
            return;
        }

        String reviewNoParam =
            request.getParameter("reviewNo");

        if (reviewNoParam == null
                || reviewNoParam.isBlank()) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "리뷰 번호가 없습니다."
            );

            return;
        }

        try {

            int reviewNo =
                Integer.parseInt(reviewNoParam);

            ReviewDTO2 review =
                reviewDAO.selectReviewByNo(
                    reviewNo,
                    loginMember.getMemberNo()
                );

            if (review == null) {

                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "리뷰를 찾을 수 없거나 수정 권한이 없습니다."
                );

                return;
            }

            request.setAttribute(
                "review",
                review
            );

            request.getRequestDispatcher(
                "/review_edit.jsp"
            ).forward(
                request,
                response
            );

        } catch (NumberFormatException e) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 리뷰 번호입니다."
            );
        }
    }


    // 리뷰 수정
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember =
            LoginUtil.requireLogin(
                request,
                response
            );

        if (loginMember == null) {
            return;
        }

        try {

            String reviewNoParam =
                request.getParameter("reviewNo");

            String productRatingParam =
                request.getParameter("productRating");

            String serviceRatingParam =
                request.getParameter("serviceRating");

            String reviewContent =
                request.getParameter("reviewContent");

            String reviewSummary =
                request.getParameter("reviewSummary");

            if (reviewNoParam == null
                    || reviewNoParam.isBlank()
                    || productRatingParam == null
                    || productRatingParam.isBlank()
                    || reviewContent == null
                    || reviewContent.isBlank()) {

                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "필수 리뷰 정보가 없습니다."
                );

                return;
            }

            int reviewNo =
                Integer.parseInt(reviewNoParam);

            int productRating =
                Integer.parseInt(
                    productRatingParam
                );

            if (productRating < 1
                    || productRating > 5) {

                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "상품 별점이 올바르지 않습니다."
                );

                return;
            }

            Integer serviceRating = null;

            if (serviceRatingParam != null
                    && !serviceRatingParam.isBlank()) {

                serviceRating =
                    Integer.parseInt(
                        serviceRatingParam
                    );

                if (serviceRating != 1
                        && serviceRating != 2) {

                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "서비스 평가가 올바르지 않습니다."
                    );

                    return;
                }
            }

            ReviewDTO dto =
                new ReviewDTO();

            dto.setReviewNo(reviewNo);
            dto.setProductRating(productRating);
            dto.setServiceRating(serviceRating);
            dto.setReviewContent(
                reviewContent.trim()
            );

            if (reviewSummary != null) {
                dto.setReviewSummary(
                    reviewSummary.trim()
                );
            }

            int rowCount =
                reviewDAO.updateReview(
                    dto,
                    loginMember.getMemberNo()
                );

            if (rowCount == 0) {

                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "리뷰를 찾을 수 없거나 수정 권한이 없습니다."
                );

                return;
            }

            response.sendRedirect(
                request.getContextPath()
                + "/review/list"
            );

        } catch (NumberFormatException e) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 리뷰 정보입니다."
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                "리뷰 수정 중 오류가 발생했습니다.",
                e
            );
        }
    }
}