package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.goodpang.dao.VendorSettlementDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorSettlementDTO;
import com.goodpang.dto.VendorSettlementDetailDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 정산관리 - 정산상세(vendor-settlement-detail.jsp) 화면 진입.
 * "정산내역 리스트"의 한 정산기간(주 단위) 행을 클릭했을 때, 그 안에 포함된 주문라인을
 * 하나씩 펼쳐서 보여준다.
 */
@WebServlet("/vendor/settlement/detail")
public class VendorSettlementDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorSettlementDAO settlementDAO = new VendorSettlementDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		LocalDate periodStart = parseDate(request.getParameter("periodStart"));

		if (periodStart == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/settlement");
			return;
		}

		List<VendorSettlementDetailDTO> detailList = settlementDAO.findDetailBySellerNo(loginSeller.getSellerNo(), periodStart);

		VendorSettlementDTO summary = buildSummary(periodStart, detailList);

		request.setAttribute("summary", summary);
		request.setAttribute("detailList", detailList);

		request.getRequestDispatcher("/WEB-INF/views/vendor-settlement-detail.jsp").forward(request, response);
	}

	// findBySellerNo()가 GROUP BY로 만드는 요약을, 이미 조회한 detailList로부터 그대로 다시 만든다
	// (같은 정산기간을 다시 쿼리하지 않기 위함)
	private VendorSettlementDTO buildSummary(LocalDate periodStart, List<VendorSettlementDetailDTO> detailList) {

		VendorSettlementDTO summary = new VendorSettlementDTO();
		summary.setPeriodStart(periodStart);

		Set<Integer> orderNos = new HashSet<>();
		long salesAmount = 0;

		for (VendorSettlementDetailDTO detail : detailList) {
			orderNos.add(detail.getOrderNo());
			salesAmount += detail.getLineAmount();
		}

		summary.setOrderCount(orderNos.size());
		summary.setSalesAmount(salesAmount);

		return summary;
	}

	private LocalDate parseDate(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(value);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

}
