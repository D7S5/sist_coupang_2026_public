package com.goodpang.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/withdraw/check")
public class MemberWithdrawCheckServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        HttpSession session = request.getSession(false);

        Boolean withdrawVerified = session == null
                ? null
                : (Boolean) session.getAttribute("withdrawVerified");

        if (!Boolean.TRUE.equals(withdrawVerified)) {
            response.sendRedirect(request.getContextPath() + "/member/withdraw");
            return;
        }
        
        int memberNo = loginMember.getMemberNo();
        WowMembershipDAO wowDao = new WowMembershipDAO();
        
        boolean wowActive = wowDao.isWowMember(memberNo);
        Date nextPaymentDate = wowDao.nextPaymentDate(memberNo);
        

        /*
         * 우선 화면 확인용 데이터
         * 이후 DAO 조회 결과로 변경
         */
        
        boolean wowCancelPending = wowDao.isCancelPending(memberNo);

        request.setAttribute("wowActive", wowActive);
        request.setAttribute("wowCancelPending", wowCancelPending);
        request.setAttribute("activeOrders", Collections.emptyList());
        request.setAttribute("refundList", Collections.emptyList());

        request.setAttribute("wowActive", wowActive);
        request.setAttribute("nextPaymentDate", nextPaymentDate);

        request.setAttribute("goodPayBalance", 0);
        request.setAttribute("refundAmount", 0);
        request.setAttribute("couponCount", 0);

        boolean withdrawBlocked = false;

        request.setAttribute("withdrawBlocked", withdrawBlocked);

        request.getRequestDispatcher("/member_withdraw_check.jsp").forward(request, response);
    }
}