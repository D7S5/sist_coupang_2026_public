package com.goodpang.servlet;

import java.io.IOException;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.CartDAO;
import com.goodpang.dao.MemberDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.CartUtil;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession existingSession = request.getSession(false);

        if (existingSession != null) {
            MemberDTO loginMember =
                    (MemberDTO) existingSession.getAttribute("loginMember");

            if (loginMember != null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
        }

        setNoCache(response);

        HttpSession session = request.getSession();

        String redirectAfterLogin =
                (String) session.getAttribute("redirectAfterLogin");

        if (redirectAfterLogin == null || redirectAfterLogin.isBlank()) {
            String referer = request.getHeader("Referer");

            if (referer != null
                    && !referer.isBlank()
                    && !referer.contains("/login")) {

                String contextPath = request.getContextPath();
                int index = referer.indexOf(contextPath);

                if (index >= 0) {
                    String redirectUrl = referer.substring(index);

                    session.setAttribute(
                            "redirectAfterLogin",
                            redirectUrl
                    );
                }
            }
        }

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setNoCache(response);

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null
                || email.isBlank()
                || password == null
                || password.isBlank()) {

            request.setAttribute(
                    "error",
                    "아이디 또는 비밀번호를 입력해주세요."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);

            return;
        }

        MemberDAO memberDAO = new MemberDAO();
        MemberDTO member = memberDAO.findByEmail(email);

        if (member == null) {
            request.setAttribute(
                    "error",
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);

            return;
        }

        if (!BCrypt.checkpw(password, member.getMemberPw())) {
            request.setAttribute(
                    "error",
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);

            return;
        }
        
        if (member.getStatus() == 0) {
            request.setAttribute("withdrawnMember", member);
            request.getRequestDispatcher("/member_withdrawn.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();

        session.setAttribute("loginMember", member);
        
        CartUtil.refreshCartSession(
        		request,
        		member.getMemberNo()
        );
        
        session.setAttribute("memberNo", member.getMemberNo());
        session.setAttribute("memberName", member.getMemberName());

        WowMembershipDAO wowMembershipDAO =
                new WowMembershipDAO();

        boolean wowMember =
                wowMembershipDAO.isWowMember(
                        member.getMemberNo()
                );

        session.setAttribute("wowMember", wowMember);

        CartDAO cartDAO = new CartDAO();

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> guestCart =
                (Map<Integer, Integer>)
                session.getAttribute("guestCart");

        if (guestCart != null && !guestCart.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry
                    : guestCart.entrySet()) {

                int optionId = entry.getKey();
                int quantity = entry.getValue();

                cartDAO.addCart(
                        member.getMemberNo(),
                        optionId,
                        quantity
                );
            }

            session.removeAttribute("guestCart");
        }

        int cartCount =
                cartDAO.getCartCount(
                        member.getMemberNo()
                );

        session.setAttribute("cartCount", cartCount);
        session.setMaxInactiveInterval(30 * 60);

        String redirectUrl =
                (String) session.getAttribute(
                        "redirectAfterLogin"
                );

        session.removeAttribute("redirectAfterLogin");

        if (redirectUrl != null
                && !redirectUrl.isBlank()
                && !redirectUrl.contains("/login")) {

            response.sendRedirect(redirectUrl);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/"
        );
    }

    private void setNoCache(HttpServletResponse response) {
        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0"
        );

        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}