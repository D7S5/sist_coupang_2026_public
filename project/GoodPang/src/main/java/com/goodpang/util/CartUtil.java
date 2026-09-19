package com.goodpang.util;

import java.util.List;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.CartItemDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CartUtil {

	public static void refreshCartSession(
			HttpServletRequest request,
			int memberNo) {

		CartDAO cartDAO = new CartDAO();

		List<CartItemDTO> cartItems =
				cartDAO.getCartItems(memberNo);

		int cartCount =
				cartDAO.getCartCount(memberNo);

		HttpSession session =
				request.getSession();

		session.setAttribute("cartCount", cartCount);
		session.setAttribute("cartPreviewItems", cartItems);
	}
	
	
	public static void refreshGuestCartSession(
            HttpServletRequest request,
            Map<Integer, Integer> guestCart) {

        HttpSession session = request.getSession();

        if (guestCart == null || guestCart.isEmpty()) {
            session.setAttribute("cartCount", 0);
            session.removeAttribute("cartItems");
            session.removeAttribute("cartPreviewItems");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        List<CartItemDTO> cartItems = cartDAO.getGuestCartItems(guestCart);

        int cartCount = guestCart.size();
        
        session.setAttribute("guestCart", guestCart);
        session.setAttribute("cartCount", cartCount);
        session.setAttribute("cartItems", cartItems);
        session.setAttribute("cartPreviewItems", cartItems);
    }
}