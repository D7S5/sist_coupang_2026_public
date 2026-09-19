package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

@WebServlet("/order/cancel_history")
public class CancelHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 로그인 인증 확인
        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) {
            return;
        }

        int memberNo = loginMember.getMemberNo();

        System.out.println("[DEBUG CancelHistoryServlet] === 취소/반품 내역 조회 시작 ===");
        System.out.println("[DEBUG CancelHistoryServlet] 로그인 memberNo: " + memberNo);

        // 2. 현재 페이지 파라미터 수신 (기본값: 1)
        int curPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                curPage = Integer.parseInt(pageParam.trim());
            } catch (NumberFormatException e) {
                curPage = 1;
            }
        }

        // 3. 페이지당 5개 설정
        int pageSize = 5;

        List<OrderDetailDTO> cancelList = new ArrayList<>();
        int totalPages = 1;

        try {
            OrderCancelDAO dao = new OrderCancelDAO();

            // 총 취소건수 및 전체 페이지 수 계산
            int totalCount = dao.getCancelHistoryCount(memberNo);
            totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages == 0) totalPages = 1;

            // 페이지당 5개씩 페이징 처리된 목록 조회
            cancelList = dao.getCancelHistoryPaged(memberNo, curPage, pageSize);

            int resultCount = (cancelList != null) ? cancelList.size() : 0;
            System.out.println("[DEBUG CancelHistoryServlet] DB 취소 내역 조회 완료건: " + resultCount);

        } catch (Exception e) {
            System.err.println("[ERROR CancelHistoryServlet] DB 취소 내역 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // 4. JSP 영역 데이터 바인딩
        request.setAttribute("cancelList", cancelList);
        request.setAttribute("curPage", curPage);
        request.setAttribute("totalPages", totalPages);

        // 5. cancel_history.jsp 포워딩
        request.getRequestDispatcher("/cancel_history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}