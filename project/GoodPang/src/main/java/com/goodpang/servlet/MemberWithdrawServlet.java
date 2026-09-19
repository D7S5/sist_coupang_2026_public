package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/withdraw")
public class MemberWithdrawServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        request.getRequestDispatcher("/member_withdraw.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        String password = request.getParameter("password");
        String agree = request.getParameter("agree");

        if (!"Y".equals(agree)) {
            request.setAttribute("errorMessage", "회원 탈퇴 유의사항에 동의해주세요.");
            request.getRequestDispatcher("/member_withdraw.jsp").forward(request, response);
            return;
        }

        if (password == null || password.isBlank()) {
            request.setAttribute("errorMessage", "비밀번호를 입력해주세요.");
            request.getRequestDispatcher("/member_withdraw.jsp").forward(request, response);
            return;
        }

        int memberNo = loginMember.getMemberNo();

        MemberDTO member = memberDAO.findByMemberNo(memberNo);

        if (member == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "회원 정보를 찾을 수 없습니다.");
            return;
        }

        boolean passwordMatch = BCrypt.checkpw(password, member.getMemberPw());

        if (!passwordMatch) {
            request.setAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            request.getRequestDispatcher("/member_withdraw.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("withdrawVerified", true);

        response.sendRedirect(request.getContextPath() + "/member/withdraw/check");
    }
}