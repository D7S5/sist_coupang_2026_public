package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorSettlementDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorSettlementDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 정산관리 - 정산내역 리스트(vendor-settlement.jsp) 화면 진입.
 * 실제 수수료율/정산주기 데이터가 없어 근사치로 계산한다 (VendorSettlementDTO 주석 참고).
 */
@WebServlet("/vendor/settlement")
public class VendorSettlementServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorSettlementDAO settlementDAO = new VendorSettlementDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		List<VendorSettlementDTO> settlementList = settlementDAO.findBySellerNo(loginSeller.getSellerNo());

		long totalSettlementAmount = settlementList.stream().mapToLong(VendorSettlementDTO::getSettlementAmount).sum();

		request.setAttribute("settlementList", settlementList);
		request.setAttribute("totalSettlementAmount", totalSettlementAmount);

		request.getRequestDispatcher("/WEB-INF/views/vendor-settlement.jsp").forward(request, response);
	}

}
