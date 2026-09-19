package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/withdraw/complete")
public class MemberWithdrawCompleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final MemberDAO memberDAO = new MemberDAO();
    private final WowMembershipDAO wowDao = new WowMembershipDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) return;

        HttpSession session = request.getSession(false);

        if (session == null || !Boolean.TRUE.equals(session.getAttribute("withdrawVerified"))) {
            response.sendRedirect(request.getContextPath() + "/member/withdraw");
            return;
        }

        int memberNo = loginMember.getMemberNo();

        if (wowDao.isWithdrawalBlocked(memberNo)) {
            request.setAttribute("withdrawError", "와우 멤버십 해지 신청 후 회원 탈퇴가 가능합니다.");
            request.getRequestDispatcher("/member_withdraw_check.jsp").forward(request, response);
            return;
        }

        try {
            boolean withdrawn = memberDAO.withdrawMember(memberNo);

            if (!withdrawn) {
                request.setAttribute("withdrawError", "회원 탈퇴 처리에 실패했습니다.");
                request.getRequestDispatcher("/member_withdraw_check.jsp").forward(request, response);
                return;
            }

            session.invalidate();
            request.getRequestDispatcher("/member_withdraw_complete.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("회원 탈퇴 처리 중 오류가 발생했습니다.", e);
        }
    }
}