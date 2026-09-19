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

@WebServlet("/wow/membership")
public class WowMembershipServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        WowMembershipDAO dao = new WowMembershipDAO();
        
        WowMembershipDTO membership = dao.getMembershipDetail(loginMember.getMemberNo());
        request.setAttribute("membership", membership);
        
        request.setAttribute("savingAmount", 17000);
        request.setAttribute("deliverySaving", 0);
        request.setAttribute("discountSaving", 14000);
        request.setAttribute("eatsSaving", 3000);
        request.setAttribute("cashSaving", 0);
        request.setAttribute("returnSaving", 0);
        request.setAttribute("globalSaving", 0);

        request.getRequestDispatcher("/WEB-INF/views/wow_membership.jsp").forward(request, response);
    }
}