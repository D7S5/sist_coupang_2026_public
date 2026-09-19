package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderDetailDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/order_cancel")
public class OrderCancelServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 로그인 인증 확인
        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) {
            return;
        }
        
     // ★ [핵심 해결 위치] 로그인한 사용자의 memberNo 변수 선언 및 추출
        int memberNo = loginMember.getMemberNo();

        // 2. orderNo 파라미터 수신
        String orderNoParam = request.getParameter("orderNo");
        OrderDetailDTO cancelInfo = null;

        if (orderNoParam != null && !orderNoParam.trim().isEmpty()) {
            try {
                int orderNo = Integer.parseInt(orderNoParam.trim());

                // 3. 기존 OrderDetailDAO의 6개 테이블 조인 메서드를 사용해 주문 데이터 가져오기
                OrderDetailDAO dao = new OrderDetailDAO();
                List<OrderDetailDTO> list = dao.getOrderDetailList(orderNo , memberNo);

                if (list != null && !list.isEmpty()) {
                    cancelInfo = list.get(0); // 화면 출력에 필요한 단일 대표 객체 추출
                }

            } catch (NumberFormatException e) {
                System.err.println("[ERROR OrderCancelServlet] 주문번호 파싱 실패: " + orderNoParam);
                e.printStackTrace();
            }
        }

        // 4. JSP 데이터 바인딩
        request.setAttribute("cancelInfo", cancelInfo);

        // 5. order_cancel.jsp 포워딩
        request.getRequestDispatcher("/order_cancel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}