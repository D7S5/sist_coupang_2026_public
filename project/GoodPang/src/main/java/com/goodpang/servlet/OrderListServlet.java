package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderListDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderItemDTO;
import com.goodpang.dto.ReviewAvailableDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/order_list")
public class OrderListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) return;

        int memberNo = loginMember.getMemberNo();

        // 1. 연도 필터 및 페이지 설정
        String yearFilter = request.getParameter("year");
        if (yearFilter == null || yearFilter.trim().isEmpty()) {
            yearFilter = "recent";
        }

        int curPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                curPage = Integer.parseInt(pageParam.trim());
            } catch (NumberFormatException e) {
                curPage = 1;
            }
        }

        int pageSize = 3;

        // 2. OrderListDAO 연동
        OrderListDAO dao = new OrderListDAO();
        int totalCount = dao.getOrderCount(memberNo, yearFilter);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<OrderItemDTO> orderList = dao.getOrderListPaged(memberNo, yearFilter, curPage, pageSize);
        
        ReviewDAO reviewDAO = new ReviewDAO();

        List<ReviewAvailableDTO> reviewList =
        		reviewDAO.getReviewStatus(memberNo);

        request.setAttribute("reviewList", reviewList);

        // 3. JSP 데이터 바인딩
        request.setAttribute("orderList", orderList);
        request.setAttribute("yearFilter", yearFilter);
        request.setAttribute("curPage", curPage);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }
}