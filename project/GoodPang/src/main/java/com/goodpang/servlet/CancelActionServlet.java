package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.OrderCancelDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/cancel_action")
public class CancelActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response); // GET 요청이 들어와도 doPost로 일임하여 처리
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 로그인 체크 (미로그인 시 로그인 화면 리다이렉트)
        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) {
            return;
        }

        // 2. 파라미터 수신 (한글 깨짐 방지 UTF-8 설정)
        request.setCharacterEncoding("UTF-8");
        String orderNoParam = request.getParameter("orderNo");
        String cancelReason = request.getParameter("reason");

        System.out.println("[DEBUG CancelActionServlet] 취소 요청 시작 - orderNo: " + orderNoParam + ", reason: " + cancelReason);

        boolean isSuccess = false;

        if (orderNoParam != null && !orderNoParam.trim().isEmpty()) {
            try {
                int orderNo = Integer.parseInt(orderNoParam.trim());

                // 3. DAO를 통해 ORDERS 업데이트 & ORDER_DETAIL 조회 & PRODUCT_RETURN 데이터 등록 한 번에 실행
                OrderCancelDAO dao = new OrderCancelDAO();
				/* isSuccess = dao.cancelOrder(orderNo, cancelReason); */
               // ★ 로그인한 회원 번호를 같이 넘긴다 — 남의 주문을 취소하지 못하게 하려는 것
                isSuccess = dao.cancelOrder(
                        orderNo,
                        loginMember.getMemberNo(),
                        cancelReason);

            } catch (NumberFormatException e) {
                System.err.println("[ERROR CancelActionServlet] 주문번호 파싱 오류: " + orderNoParam);
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("[ERROR CancelActionServlet] 취소 처리 중 예외 발생");
                e.printStackTrace();
            }
        }

        // 4. DB 처리 결과에 따른 이동 분기
        if (isSuccess) {
            System.out.println("[DEBUG CancelActionServlet] 취소 성공 -> cancel_confirm.jsp 리다이렉트");
            // 취소 완료 안내 창으로 이동
            response.sendRedirect(request.getContextPath() + "/cancel_confirm.jsp");
        } else {
            System.err.println("[ERROR CancelActionServlet] 취소 처리 실패!");
            // 실패 시 이전 화면으로 되돌리기
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('주문 취소 처리에 실패했습니다. 다시 시도해주세요.');");
            response.getWriter().println("history.back();");
            response.getWriter().println("</script>");
        }
    }
}