package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.WowMembershipDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/wow/cancel")
public class WowCancelServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final WowMembershipDAO wowMembershipDAO = new WowMembershipDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) return;

        int memberNo = loginMember.getMemberNo();
        HttpSession session = request.getSession();

        try {
            WowMembershipDTO membership =
                    wowMembershipDAO.findByMemberNo(memberNo);

            // 멤버십 가입 이력 자체가 없음
            if (membership == null) {
                session.setAttribute("wowMember", false);

                response.sendRedirect(
                        request.getContextPath()
                        + "/wow/membership?notMember=Y"
                );
                return;
            }

            String status = membership.getStatus();

            // 이미 해지 신청 완료
            if ("CANCEL_PENDING".equals(status)) {
                // 이용기간이 끝나기 전까지는 와우 혜택 유지
                session.setAttribute("wowMember", true);

                response.sendRedirect(
                        request.getContextPath()
                        + "/wow/membership?alreadyCanceled=Y"
                );
                return;
            }

            // 이미 멤버십 종료
            if ("EXPIRED".equals(status)
                    || "SUSPENDED".equals(status)) {

                session.setAttribute("wowMember", false);

                response.sendRedirect(
                        request.getContextPath()
                        + "/wow/membership?alreadyExpired=Y"
                );
                return;
            }

            // ACTIVE 상태만 실제 해지 처리
            if ("ACTIVE".equals(status)) {

                int result =
                        wowMembershipDAO.cancelMembership(memberNo);

                if (result > 0) {
                    /*
                     * CANCEL_PENDING은 아직 이용기간이 남아 있으므로
                     * 와우 회원 혜택은 유지
                     */
                    session.setAttribute("wowMember", true);

                    response.sendRedirect(
                            request.getContextPath()
                            + "/wow/membership?canceled=Y"
                    );
                    return;
                }
            }

            /*
             * 상태가 예상하지 못한 값이어도
             * 무조건 500으로 터뜨리기보다는 다시 상태 확인
             */
            boolean wowMember =
                    wowMembershipDAO.isWowMember(memberNo);

            session.setAttribute("wowMember", wowMember);

            response.sendRedirect(
                    request.getContextPath()
                    + "/wow/membership"
            );

        } catch (Exception e) {
            throw new ServletException(
                    "와우 멤버십 해지 처리에 실패했습니다.",
                    e
            );
        }
    }
}