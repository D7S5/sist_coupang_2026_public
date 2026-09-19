package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.SellerDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class VendorSignupServlet
 */
@WebServlet("/vendor/signup")
public class VendorSignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VendorSignupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
			   .forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String bizType = request.getParameter("bizType");
		String bizNumber = request.getParameter("bizNumber");
		String companyName = request.getParameter("companyName");
		String ceoName = request.getParameter("ceoName");
		String managerName = request.getParameter("managerName");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String passwordConfirm = request.getParameter("passwordConfirm");
		String phone = request.getParameter("phone");

		/*
		 * 1. 필수값 검사
		 */
		if (bizType == null || bizType.isBlank()
				|| bizNumber == null || bizNumber.isBlank()
				|| companyName == null || companyName.isBlank()
				|| ceoName == null || ceoName.isBlank()
				|| managerName == null || managerName.isBlank()
				|| email == null || email.isBlank()
				|| password == null || password.isBlank()
				|| passwordConfirm == null || passwordConfirm.isBlank()
				|| phone == null || phone.isBlank()) {

			request.setAttribute("error", "필수 입력값을 모두 입력해주세요.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
			return;
		}

		/*
		 * 2. 비밀번호 확인
		 */
		if (!password.equals(passwordConfirm)) {
			request.setAttribute("error", "비밀번호가 일치하지 않습니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
			return;
		}

		if (password.length() < 8) {
			request.setAttribute("error", "비밀번호는 8자 이상 입력해주세요.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
			return;
		}

		/*
		 * 3. 사업자유형 값 변환 (individual/corporate → 개인사업자/법인사업자)
		 */
		String businessType;

		if ("individual".equals(bizType)) {
			businessType = "개인사업자";
		} else if ("corporate".equals(bizType)) {
			businessType = "법인사업자";
		} else {
			request.setAttribute("error", "사업자 유형을 확인해주세요.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
			return;
		}

		SellerDAO dao = new SellerDAO();

		/*
		 * 4. 이메일 중복 검사
		 */
		if (dao.existsByEmail(email)) {
			request.setAttribute("error", "이미 가입된 이메일입니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
			return;
		}

		/*
		 * 5. 사업자등록번호 중복 검사
		 */
		if (dao.existsByBusinessNo(bizNumber)) {
			request.setAttribute("error", "이미 입점 신청된 사업자등록번호입니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
			return;
		}

		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

		SellerDTO dto = new SellerDTO();

		dto.setEmail(email);
		dto.setSellerPw(hashedPassword);
		dto.setManagerName(managerName);
		dto.setPhone(phone);
		dto.setBusinessNo(bizNumber);
		dto.setBusinessType(businessType);
		dto.setCeoName(ceoName);
		dto.setStoreName(companyName);

		int result = dao.insertSeller(dto);

		if (result == 1) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		} else {
			request.setAttribute("error", "입점 신청에 실패했습니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-signup.jsp")
				   .forward(request, response);
		}
	}

}
