package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderDAO;
import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/address/list")
public class AddressListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
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
    	
        int memberNo = loginMember.getMemberNo();

        try {
            OrderDAO orderDAO = new OrderDAO();

            // 회원의 전체 배송지 조회
            List<AddressDTO> addressList =
                    orderDAO.getAddressList(memberNo);

            // JSP에서 사용할 데이터
            request.setAttribute(
                    "addressList",
                    addressList
            );

            // 배송지 관리 페이지로 이동
            request.getRequestDispatcher(
                    "/goodpang_addressbook.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            throw new ServletException(
                    "배송지 목록을 불러오는 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}