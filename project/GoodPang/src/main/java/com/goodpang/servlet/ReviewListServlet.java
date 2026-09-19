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

@WebServlet("/review/list")
public class ReviewListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 5;
    private static final int PAGE_BLOCK = 5;

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        int memberNo = loginMember.getMemberNo();

        String tab = request.getParameter("tab");

        if (!"available".equals(tab) && !"written".equals(tab)) {
            tab = "written";
        }

        int page = 1;

        try {
            String pageParam = request.getParameter("page");

            if (pageParam != null && !pageParam.isBlank()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        if (page < 1) {
            page = 1;
        }

        int availableCount = reviewDAO.countAvailableReviewsByMemberNo(memberNo);
        int writtenCount = reviewDAO.countReviewsByMemberNo(memberNo);

        int totalCount = "available".equals(tab)
                ? availableCount
                : writtenCount;

        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        if (totalPages < 1) {
            totalPages = 1;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        int offset = (page - 1) * PAGE_SIZE;

        if ("available".equals(tab)) {
            request.setAttribute(
                "availableReviewList",
                reviewDAO.selectAvailableReviewsByMemberNo(memberNo, offset, PAGE_SIZE)
            );
        } else {
            request.setAttribute(
                "writtenReviewList",
                reviewDAO.selectReviewsByMemberNo(memberNo, offset, PAGE_SIZE)
            );
        }

        int startPage = ((page - 1) / PAGE_BLOCK) * PAGE_BLOCK + 1;
        int endPage = Math.min(startPage + PAGE_BLOCK - 1, totalPages);

        request.setAttribute("availableCount", availableCount);
        request.setAttribute("writtenCount", writtenCount);
        request.setAttribute("activeTab", tab);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);

        request.getRequestDispatcher("/review_list.jsp")
               .forward(request, response);
    }
}
