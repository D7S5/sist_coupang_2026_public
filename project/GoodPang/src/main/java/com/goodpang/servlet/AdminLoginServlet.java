package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.AdminDAO;
import com.goodpang.dto.AdminDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin-login.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String adminId = request.getParameter("adminId");
        String password = request.getParameter("password");

        AdminDAO dao = new AdminDAO();

        AdminDTO admin = (adminId == null || adminId.isBlank()) ? null : dao.findByAdminId(adminId);

        // 아이디 없음 또는 비밀번호 불일치
        if (admin == null || !BCrypt.checkpw(password, admin.getAdminPw())) {

            request.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            request.getRequestDispatcher("/WEB-INF/views/admin-login.jsp")
                   .forward(request, response);
            return;
        }

        HttpSession session = request.getSession();

        session.setAttribute("loginAdmin", admin);
        session.setAttribute("adminNo", admin.getAdminNo());
        session.setAttribute("adminName", admin.getAdminName());

        session.setMaxInactiveInterval(30 * 60);

        String redirectUrl = (String) session.getAttribute("adminRedirectAfterLogin");

        session.removeAttribute("adminRedirectAfterLogin");

        if (redirectUrl != null && !redirectUrl.isBlank()) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }
}
