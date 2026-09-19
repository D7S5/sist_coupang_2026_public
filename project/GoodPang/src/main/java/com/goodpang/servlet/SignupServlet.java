package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/signup.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        /*
         * 1. 회원가입 폼 데이터 받기
         */
        String memberId =
                request.getParameter("memberId");

        String memberPw =
                request.getParameter("memberPw");

        String memberPwConfirm =
                request.getParameter("memberPwConfirm");

        String memberName =
                request.getParameter("memberName");

        String phone =
                request.getParameter("phone");

        String email =
                request.getParameter("email");

        /*
         * 2. 필수값 검사
         */
        if (memberId == null || memberId.isBlank()
                || memberPw == null || memberPw.isBlank()
                || memberPwConfirm == null || memberPwConfirm.isBlank()
                || memberName == null || memberName.isBlank()
                || phone == null || phone.isBlank()
                || email == null || email.isBlank()) {

            request.setAttribute(
                    "error",
                    "필수 입력값을 모두 입력해주세요."
            );

            request.getRequestDispatcher("/signup.jsp")
                   .forward(request, response);

            return;
        }

        /*
         * 3. 비밀번호 확인
         */
        if (!memberPw.equals(memberPwConfirm)) {

            request.setAttribute(
                    "error",
                    "비밀번호가 일치하지 않습니다."
            );

            request.getRequestDispatcher("/signup.jsp")
                   .forward(request, response);

            return;
        }

        /*
         * 4. 비밀번호 길이 검사
         */
        if (memberPw.length() < 8) {

            request.setAttribute(
                    "error",
                    "비밀번호는 8자 이상 입력해주세요."
            );

            request.getRequestDispatcher("/signup.jsp")
                   .forward(request, response);

            return;
        }

        MemberDAO dao = new MemberDAO();

        
        if (dao.existsByMemberId(memberId)) {

            request.setAttribute(
                    "error",
                    "이미 사용 중인 아이디입니다."
            );

            request.getRequestDispatcher("/signup.jsp")
                   .forward(request, response);

            return;
        }

        /*
         * 6. 이메일 중복 검사
         */
        if (dao.existsByEmail(email)) {

            request.setAttribute(
                    "error",
                    "이미 가입된 이메일입니다."
            );

            request.getRequestDispatcher("/signup.jsp")
                   .forward(request, response);

            return;
        }

        String hashedPassword =
                BCrypt.hashpw(
                        memberPw,
                        BCrypt.gensalt()
                );

        
        MemberDTO dto = new MemberDTO();

        dto.setMemberId(memberId);
        dto.setMemberPw(hashedPassword);
        dto.setMemberName(memberName);
        dto.setPhone(phone);
        dto.setEmail(email);

        dto.setRank("USER");

        int result = dao.insertMember(dto);
        
        if (result == 1) {
        	
            MemberDTO loginMember =
                    dao.findByEmail(email);
            if (loginMember != null) {
                // 로그인 세션 생성
                HttpSession session =
                        request.getSession();

                session.setAttribute(
                        "loginMember",
                        loginMember
                );

                // 메인 페이지 이동
                response.sendRedirect(
                        request.getContextPath() + "/"
                );
            } else {
                request.setAttribute(
                        "error",
                        "회원가입은 완료되었지만 로그인 처리에 실패했습니다."
                );
                request.getRequestDispatcher("/login.jsp")
                       .forward(request, response);
            }
        } else {
            request.setAttribute(
                    "error",
                    "회원가입에 실패했습니다."
            );
            request.getRequestDispatcher("/signup.jsp")
                   .forward(request, response);
        }
    }
}