package com.goodpang.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import com.goodpang.dao.SellerDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.util.UploadPaths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * 판매자 회원가입(vendor-signup.jsp) 직후 상태인 '입점 대기' 판매자가
 * 사업장주소·통신판매업신고번호·대표카테고리·정산계좌·서류(사업자등록증/통신판매신고증)를
 * 추가로 입력하는 페이지이자, 이미 승인된 판매자가 같은 정보를 수정하는 페이지로도 쓰인다.
 * '입점 대기'/'반려' 상태에서 제출하면 SELLER.approval_status가 '심사 중'으로 바뀌지만,
 * '승인' 상태에서 수정 제출하는 경우는 재심사로 되돌리지 않는다(SellerDAO.updateBusinessInfo 참고).
 */
@WebServlet("/vendor/business-info")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 15 * 1024 * 1024)
public class VendorBusinessInfoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("VendorBusinessInfoServlet Get");
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("loginSeller") == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		System.out.println("VendorBusinessInfoServlet2");
		request.getRequestDispatcher("/WEB-INF/views/vendor-business-info.jsp")
			   .forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("loginSeller") == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		String zipcode = request.getParameter("zipcode");
		String businessAddress = request.getParameter("businessAddress");
		String businessDetailAddress = request.getParameter("businessDetailAddress");
		String mailOrderNo = request.getParameter("mailOrderNo");
		String categoryNo = request.getParameter("categoryNo");
		String bankName = request.getParameter("bankName");
		String accountNo = request.getParameter("accountNo");
		String accountHolder = request.getParameter("accountHolder");

		if (zipcode == null || zipcode.isBlank()
				|| businessAddress == null || businessAddress.isBlank()
				|| mailOrderNo == null || mailOrderNo.isBlank()
				|| categoryNo == null || categoryNo.isBlank()
				|| bankName == null || bankName.isBlank()
				|| accountNo == null || accountNo.isBlank()
				|| accountHolder == null || accountHolder.isBlank()) {

			request.setAttribute("error", "필수 입력값을 모두 입력해주세요.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-business-info.jsp")
				   .forward(request, response);
			return;
		}

		String businessCertUrl = saveUploadedFile(request, "businessCert", loginSeller.getBusinessCertUrl(), loginSeller.getSellerNo());
		String mailOrderCertUrl = saveUploadedFile(request, "mailOrderCert", loginSeller.getMailOrderCertUrl(), loginSeller.getSellerNo());

		// 최초 제출 시에는 서류 첨부 둘 다 필수
		if (businessCertUrl == null || mailOrderCertUrl == null) {

			request.setAttribute("error", "사업자등록증과 통신판매신고증을 모두 첨부해주세요.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-business-info.jsp")
				   .forward(request, response);
			return;
		}

		SellerDTO dto = new SellerDTO();

		dto.setSellerNo(loginSeller.getSellerNo());
		dto.setZipcode(zipcode);
		dto.setBusinessAddress(businessAddress);
		dto.setBusinessDetailAddress(businessDetailAddress);
		dto.setMailOrderNo(mailOrderNo);
		dto.setCategoryNo(Integer.valueOf(categoryNo));
		dto.setBankName(bankName);
		dto.setAccountNo(accountNo);
		dto.setAccountHolder(accountHolder);
		dto.setBusinessCertUrl(businessCertUrl);
		dto.setMailOrderCertUrl(mailOrderCertUrl);

		SellerDAO dao = new SellerDAO();
		int result = dao.updateBusinessInfo(dto);

		if (result == 1) {

			// 세션의 로그인 정보를 최신 상태(approval_status='심사 중' 등)로 갱신
			SellerDTO refreshed = dao.findByEmail(loginSeller.getEmail());
			session.setAttribute("loginSeller", refreshed);

			response.sendRedirect(request.getContextPath() + "/vendor/dashboard");

		} else {

			request.setAttribute("error", "저장에 실패했습니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-business-info.jsp")
				   .forward(request, response);
		}
	}

	/**
	 * 첨부파일이 새로 들어왔으면 webapp/upload/에 저장 후 경로를 반환하고,
	 * 첨부가 없으면(재제출 등) 기존 경로(existingUrl)를 그대로 반환한다.
	 */
	private String saveUploadedFile(HttpServletRequest request, String partName, String existingUrl, int sellerNo) throws IOException, ServletException {

		Part part = request.getPart(partName);

		if (part == null || part.getSize() == 0) {
			return existingUrl;
		}

		String submittedFileName = part.getSubmittedFileName();

		if (submittedFileName == null || submittedFileName.isBlank()) {
			return existingUrl;
		}

		String originalName = Path.of(submittedFileName).getFileName().toString();
		String ext = "";

		int dotIndex = originalName.lastIndexOf('.');
		if (dotIndex >= 0) {
			ext = originalName.substring(dotIndex);
		}

		String savedName = UUID.randomUUID() + ext;

		String uploadDir = UploadPaths.resolveBaseDir(getServletContext()) + "/" + sellerNo;
		File uploadDirFile = new File(uploadDir);

		if (!uploadDirFile.exists()) {
			uploadDirFile.mkdirs();
		}

		part.write(uploadDir + File.separator + savedName);

		return "upload/" + sellerNo + "/" + savedName;
	}

}
