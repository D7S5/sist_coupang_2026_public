package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderCancelDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/cancel_detail")
public class CancelDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 로그인 검증
        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) {
            return;
        }
        
        System.out.println("[DEBUG CancelHistoryServlet] === 취소상세 조회 시작 ===");

        // 2. 파라미터 수신 및 DAO 호출
        String orderNoParam = request.getParameter("orderNo");
        List<OrderDetailDTO> cancelDetailList = null;
        OrderDetailDTO cancelInfo2 = null;

        if (orderNoParam != null && !orderNoParam.trim().isEmpty()) {
            try {
                int orderNo = Integer.parseInt(orderNoParam.trim());

                OrderCancelDAO dao = new OrderCancelDAO();
                cancelDetailList = dao.getCancelDetailList(orderNo);
                System.err.println("OrderCancelDAO: cancelDetailList");

                if (cancelDetailList != null && !cancelDetailList.isEmpty()) {
                    cancelInfo2 = cancelDetailList.get(0); // 공통 대표 정보 DTO
                }
            } catch (NumberFormatException e) {
                System.err.println("[ERROR CancelDetailServlet] 파라미터 변환 오류");
                e.printStackTrace();
            }
        }

        // 3. Request 바인딩 및 JSP 포워딩
        request.setAttribute("cancelDetailList", cancelDetailList);
        request.setAttribute("cancelInfo2", cancelInfo2);

        request.getRequestDispatcher("/cancel_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}