package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AddressDAO;
import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/address/add")
public class AddressAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 배송지 추가 화면
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

    	 MemberDTO loginMember =
                 LoginUtil.requireLogin(
                         request,
                         response
                 );
         
         if (loginMember == null) {
             return;
         }
     	
        request.getRequestDispatcher(
                "/address_add.jsp"
        ).forward(request, response);
    }


    // 배송지 등록
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        String checkoutNo =
                request.getParameter("checkoutNo");

        String from =
                request.getParameter("from");

        MemberDTO loginMember =
                LoginUtil.requireLogin(
                        request,
                        response
                );
        
        if (loginMember == null) {
            return;
        }
    	
        
        int memberNo = loginMember.getMemberNo();

        String receiverName =
                request.getParameter("receiverName");

        String tel =
                request.getParameter("tel");

        String zipcode =
                request.getParameter("zipcode");

        String address =
                request.getParameter("address");

        String detailAddress =
                request.getParameter("detailAddress");

        String requestMsg =
                request.getParameter("requestMsg");

        String addressDefault =
                request.getParameter("addressDefault");

        // checkbox가 체크되지 않으면 null
        if (addressDefault == null) {
            addressDefault = "N";
        }

        AddressDTO dto = new AddressDTO();

        dto.setMemberNo(memberNo);
        dto.setReceiverName(receiverName);
        dto.setTel(tel);
        dto.setZipcode(zipcode);
        dto.setAddress(address);
        dto.setDetailAddress(detailAddress);
        dto.setRequestMsg(requestMsg);
        dto.setAddressDefault(addressDefault);

        AddressDAO dao = new AddressDAO();

        try {

            int result = dao.insertAddress(dto);

            if (result > 0) {
            	
            	if ("payment".equals(from)
            	        && checkoutNo != null
            	        && !checkoutNo.isBlank()) {

            	    response.sendRedirect(
            	        request.getContextPath()
            	        + "/order/payment?checkoutNo="
            	        + checkoutNo
            	    );

            	} else {

            	    response.sendRedirect(
            	        request.getContextPath()
            	        + "/address/list"
            	    );
            	}

            } else {

                request.setAttribute(
                        "error",
                        "배송지 등록에 실패했습니다."
                );

                request.getRequestDispatcher(
                        "/address_add.jsp"
                ).forward(request, response);
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                    "배송지 등록 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}