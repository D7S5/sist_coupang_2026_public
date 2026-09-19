package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

@WebServlet("/order/order_detail")
public class OrderDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	 // 로그인 확인
        MemberDTO loginMember =
                LoginUtil.requireLogin(
                        request,
                        response
                );

        if (loginMember == null) {
            return;
        }

        int memberNo =
                loginMember.getMemberNo();

        // 2. 파라미터 수신 및 데이터 초기화
        String orderNoParam = request.getParameter("orderNo");
        List<OrderDetailDTO> detailList = new ArrayList<>();
        OrderDetailDTO orderInfo = null;

        System.out.println("[DEBUG OrderDetailServlet] === 주문 상세 정보 조회 시작 ===");
        System.out.println("[DEBUG OrderDetailServlet] 전달받은 orderNoParam: " + orderNoParam);

        try {
            if (orderNoParam != null && !orderNoParam.trim().isEmpty()) {
                int orderNo = Integer.parseInt(orderNoParam.trim());

                OrderDetailDAO dao = new OrderDetailDAO();
                detailList = dao.getOrderDetailList(orderNo , memberNo);

                // 주문 공통 정보(수령인, 배송지, 결제수단 등)를 JSP 상단에서 편하게 쓰기 위해 첫 번째 항목 바인딩
				/*
				 * if (detailList != null && !detailList.isEmpty()) { int result_size =
				 * detailList.size() -1; orderInfo = detailList.get(result_size); }
				 */
                if (detailList != null && !detailList.isEmpty()) {
                    orderInfo = detailList.get(0);
                }
                
                int resultCount = (detailList != null) ? detailList.size() : 0;
                System.out.println("[DEBUG OrderDetailServlet] DB 조회 완료건: " + resultCount);
            }
        } catch (NumberFormatException e) {
            System.err.println("[ERROR OrderDetailServlet] 주문번호 숫자 변환 실패: " + orderNoParam);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR OrderDetailServlet] DB 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. JSP 영역 데이터 저장
        request.setAttribute("detailList", detailList); // 상품 목록 (c:forEach용)
        request.setAttribute("orderInfo", orderInfo);   // 주문/배송/결제 공통 정보 단일 객체

        // 4. JSP 포워딩
        request.getRequestDispatcher("/order_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}