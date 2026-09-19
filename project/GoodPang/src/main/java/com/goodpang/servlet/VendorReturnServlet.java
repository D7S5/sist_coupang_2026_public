package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorReturnDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorReturnDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 취소/반품/교환 관리(vendor-return.jsp) 화면 진입.
 */
@WebServlet("/vendor/return")
public class VendorReturnServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorReturnDAO returnDAO = new VendorReturnDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		List<VendorReturnDTO> returnList = returnDAO.findBySellerNo(loginSeller.getSellerNo());

		long cancelCount = returnList.stream().filter(r -> "취소".equals(r.getReturnType())).count();
		long returnCount = returnList.stream().filter(r -> "반품".equals(r.getReturnType())).count();
		long exchangeCount = returnList.stream().filter(r -> "교환".equals(r.getReturnType())).count();

		request.setAttribute("returnList", returnList);
		request.setAttribute("cancelCount", cancelCount);
		request.setAttribute("returnCount", returnCount);
		request.setAttribute("exchangeCount", exchangeCount);

		request.getRequestDispatcher("/WEB-INF/views/vendor-return.jsp").forward(request, response);
	}

}
