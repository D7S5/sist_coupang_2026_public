package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.OrderDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderCompleteDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/complete")
public class OrderCompleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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

        String orderNoParam =
            request.getParameter("orderNo");

        if (orderNoParam == null
                || orderNoParam.isBlank()) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "주문번호가 없습니다."
            );
            return;
        }

        int orderNo;
        try {
            orderNo =
                Integer.parseInt(orderNoParam);
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 주문번호입니다."
            );
            return;
        }

        OrderDAO dao =
            new OrderDAO();

        OrderCompleteDTO orderComplete =
            dao.getOrderComplete(orderNo);
        
        if (orderComplete == null) {

            response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "주문 정보를 찾을 수 없습니다."
            );
            return;
        }

        request.setAttribute(
            "orderComplete",
            orderComplete
        );
        
        request.setAttribute(
                "orderNo",
                orderNo
        );

        request
            .getRequestDispatcher(
                "/goodpang_order_complete.jsp"
            )
            .forward(
                request,
                response
            );
    }
}