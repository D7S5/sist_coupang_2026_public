package com.goodpang.servlet;

import java.io.IOException;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.CartUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart/delete-selected")
public class CartDeleteSelectedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String[] optionIdParams = request.getParameterValues("optionId");

        if (optionIdParams == null || optionIdParams.length == 0) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        HttpSession session = request.getSession();
        MemberDTO loginMember =
                (MemberDTO) session.getAttribute("loginMember");

        try {
            if (loginMember != null) {
                int memberNo = loginMember.getMemberNo();
                int[] optionIds = new int[optionIdParams.length];

                for (int i = 0; i < optionIdParams.length; i++) {
                    optionIds[i] = Integer.parseInt(optionIdParams[i]);
                }

                cartDAO.deleteSelected(memberNo, optionIds);
                CartUtil.refreshCartSession(request, memberNo);

            } else {
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> guestCart =
                        (Map<Integer, Integer>) session.getAttribute("guestCart");

                if (guestCart != null) {
                    for (String optionIdParam : optionIdParams) {
                        int optionId = Integer.parseInt(optionIdParam);
                        guestCart.remove(optionId);
                    }

                    if (guestCart.isEmpty()) {
                        session.removeAttribute("guestCart");
                        session.removeAttribute("cartItems");
                        session.setAttribute("cartCount", 0);
                        session.removeAttribute("cartPreviewItems");
                    } else {
                        session.setAttribute("guestCart", guestCart);
                        CartUtil.refreshGuestCartSession(request, guestCart);
                    }
                }
            }

            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 상품 번호입니다."
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(
                    "선택 상품 삭제 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}