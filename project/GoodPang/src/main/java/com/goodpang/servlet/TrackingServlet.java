package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.TrackingDAO;
import com.goodpang.dto.DeliveryLogDTO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/order_tracking")
public class TrackingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 로그인 체크
        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) {
            return;
        }

        // 2. orderNo 수신
        String orderNoParam = request.getParameter("orderNo");
        System.out.println("[TrackingServlet] 전달받은 orderNo 파라미터 원본: " + orderNoParam);
        if (orderNoParam != null && !orderNoParam.trim().isEmpty()) {
            try {
                int orderNo = Integer.parseInt(orderNoParam.trim());
                System.out.println("[TrackingServlet] 파싱 완료된 orderNo 숫자값: " + orderNo);

                TrackingDAO dao = new TrackingDAO();
                OrderDetailDTO trackingInfo = dao.getTrackingHeaderInfo(orderNo);
               
                if (trackingInfo != null) {
                    List<DeliveryLogDTO> logList = dao.getDeliveryLogList(orderNo);
                    trackingInfo.setLogList(logList);
                    System.out.println("[TrackingServlet] DB 조회 성공!");
                    request.setAttribute("trackingInfo", trackingInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. order_tracking.jsp 포워딩
        request.getRequestDispatcher("/order_tracking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}