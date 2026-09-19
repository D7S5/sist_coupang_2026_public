package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.CartItemDTO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CartDAO cartDAO = new CartDAO();
    private final WowMembershipDAO wowDAO = new WowMembershipDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        List<CartItemDTO> cartItems;

        if (loginMember != null) {

            int memberNo = loginMember.getMemberNo();

            cartItems = cartDAO.getCartItems(memberNo);

            boolean isWowMember = wowDAO.isWowMember(memberNo);

            request.setAttribute("isWowMember", isWowMember);
            session.setAttribute("cartPreviewItems", cartItems);

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            if (guestCart == null || guestCart.isEmpty()) {

                cartItems = new ArrayList<>();

                session.setAttribute("cartCount", 0);
                session.removeAttribute("cartPreviewItems");

            } else {

                cartItems = cartDAO.getGuestCartItems(guestCart);

                int guestCartCount = guestCart.size();

                session.setAttribute("cartCount", guestCartCount);
                session.setAttribute("cartPreviewItems", cartItems);
            }

            request.setAttribute("isWowMember", false);
        }

        int totalPrice = 0;

        for (CartItemDTO item : cartItems) {
            totalPrice += item.getTotalPrice();
        }

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartCount", cartItems.size());
        request.setAttribute("totalPrice", totalPrice);

        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }
}