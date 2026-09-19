package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/update")
public class MemberUpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

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

        String type =
                request.getParameter("type");

        try {

            MemberDAO dao =
                    new MemberDAO();

            int result = 0;

            // =========================
            // 이메일 변경
            // =========================
            if ("email".equals(type)) {

                String newEmail =
                        request.getParameter("newEmail");

                if (newEmail == null ||
                        newEmail.isBlank()) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/member/modify"
                    );

                    return;
                }

                result =
                        dao.updateEmail(
                                memberNo,
                                newEmail
                        );

                // 세션 정보도 갱신
                if (result > 0) {
                    loginMember.setEmail(newEmail);
                }

            }

            // =========================
            // 휴대폰 번호 변경
            // =========================
            else if ("phone".equals(type)) {

                String newPhone =
                        request.getParameter("newPhone");

                if (newPhone == null ||
                        newPhone.isBlank()) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/member/modify"
                    );

                    return;
                }

                result =
                        dao.updatePhone(
                                memberNo,
                                newPhone
                        );

                if (result > 0) {
                    loginMember.setPhone(newPhone);
                }

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/member/modify"
                );

                return;
            }


            if (result > 0) {

                // 로그인 세션 정보 갱신
                HttpSession session =
                        request.getSession();

                session.setAttribute(
                        "loginMember",
                        loginMember
                );

                response.sendRedirect(
                        request.getContextPath()
                                + "/member/modify"
                );

            } else {

                request.setAttribute(
                        "error",
                        "회원정보 변경에 실패했습니다."
                );

                response.sendRedirect(
                        request.getContextPath()
                                + "/member/modify"
                );
            }

        } catch (Exception e) {

            throw new ServletException(
                    "회원정보 변경 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}