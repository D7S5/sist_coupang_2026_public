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

@WebServlet("/member/password")
public class PasswordChangeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember =
                LoginUtil.requireLogin(
                        request,
                        response
                );

        if (loginMember == null) {
            return;
        }

        String currentPassword =
                request.getParameter(
                        "currentPassword"
                );

        String newPassword =
                request.getParameter(
                        "newPassword"
                );

        String confirmPassword =
                request.getParameter(
                        "confirmPassword"
                );

        if (currentPassword == null
                || currentPassword.isBlank()
                || newPassword == null
                || newPassword.isBlank()
                || confirmPassword == null
                || confirmPassword.isBlank()) {

            request.setAttribute(
                    "error",
                    "비밀번호를 모두 입력해주세요."
            );

            request.getRequestDispatcher(
                    "/member/modify"
            ).forward(request, response);

            return;
        }

        if (!isValidPassword(newPassword)) {

            request.setAttribute(
                    "error",
                    "비밀번호는 8자리 이상 입력해주세요."
            );

            request.getRequestDispatcher(
                    "/member/modify"
            ).forward(request, response);

            return;
        }

        if (!newPassword.equals(
                confirmPassword)) {

            request.setAttribute(
                    "error",
                    "새 비밀번호가 일치하지 않습니다."
            );

            request.getRequestDispatcher(
                    "/member/modify"
            ).forward(request, response);

            return;
        }


        try {

            MemberDAO dao =
                    new MemberDAO();


            MemberDTO member =
                    dao.getMember(
                            loginMember.getMemberNo()
                    );


            if (member == null) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login"
                );

                return;
            }


            // DB에 저장된 BCrypt 비밀번호
            String encodedPassword =
                    member.getMemberPw();


            if (encodedPassword == null
                    || encodedPassword.isBlank()) {

                throw new ServletException(
                        "회원 비밀번호 정보가 존재하지 않습니다."
                );
            }

            if (!BCrypt.checkpw(
                    currentPassword,
                    encodedPassword)) {

                request.setAttribute(
                        "error",
                        "현재 비밀번호가 올바르지 않습니다."
                );

                request.getRequestDispatcher(
                        "/member/modify"
                ).forward(request, response);

                return;
            }

            if (BCrypt.checkpw(
                    newPassword,
                    encodedPassword)) {

                request.setAttribute(
                        "error",
                        "새 비밀번호는 현재 비밀번호와 다르게 입력해주세요."
                );

                request.getRequestDispatcher(
                        "/member/modify"
                ).forward(request, response);

                return;
            }

            String encodedNewPassword =
                    BCrypt.hashpw(
                            newPassword,
                            BCrypt.gensalt()
                    );

            int result =
                    dao.updatePassword(
                            member.getMemberNo(),
                            encodedNewPassword
                    );


            if (result > 0) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/member/modify"
                );

            } else {

                throw new ServletException(
                        "비밀번호 변경에 실패했습니다."
                );
            }


        } catch (ServletException e) {

            throw e;

        } catch (Exception e) {

            throw new ServletException(
                    "비밀번호 변경 중 오류가 발생했습니다."
            );
        }
    }

    private boolean isValidPassword(
            String password) {

        return password != null
                && password.length() >= 8;
    }
}