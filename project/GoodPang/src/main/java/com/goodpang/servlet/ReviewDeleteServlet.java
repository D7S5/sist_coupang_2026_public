package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/review/delete")
public class ReviewDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDAO reviewDAO =
        new ReviewDAO();

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
                Integer.parseInt(
                    reviewNoParam
                );

            int memberNo =
                loginMember.getMemberNo();

            int rowCount =
                reviewDAO.deleteReview(
                    reviewNo,
                    memberNo
                );

            if (rowCount == 0) {

                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "삭제할 리뷰가 없거나 삭제 권한이 없습니다."
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
                "잘못된 리뷰 번호입니다."
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                "리뷰 삭제 중 오류가 발생했습니다.",
                e
            );
        }
    }
}