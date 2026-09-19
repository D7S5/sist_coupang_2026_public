package com.goodpang.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.UUID;

import com.goodpang.dao.ProductWriteDAO;
import com.goodpang.dao.VendorActionLogDAO;
import com.goodpang.dto.ProductOptionWriteDTO;
import com.goodpang.dto.ProductWriteDTO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.util.UploadPaths;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * 판매자 상품 등록 화면(vendor-product-write.jsp) GET 진입 + POST 등록 처리.
 */
@WebServlet("/vendor/product/write")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 150 * 1024 * 1024)
public class VendorProductWriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Gson gson = new Gson();
	private final ProductWriteDAO productWriteDAO = new ProductWriteDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		if (!"승인".equals(loginSeller.getApprovalStatus())) {
			request.setAttribute("sellerApprovalStatus", loginSeller.getApprovalStatus());
			request.getRequestDispatcher("/WEB-INF/views/vendor-product-write-locked.jsp").forward(request, response);
			return;
		}

		request.getRequestDispatcher("/WEB-INF/views/vendor-product-write.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			writeJson(response, 401, new Result(false, "로그인이 필요합니다.", 0));
			return;
		}

		if (!"승인".equals(loginSeller.getApprovalStatus())) {
			writeJson(response, 403, new Result(false, "입점 승인이 완료된 판매자만 상품을 등록할 수 있습니다.", 0));
			return;
		}

		if (loginSeller.getZipcode() == null || loginSeller.getZipcode().isBlank()
				|| loginSeller.getBusinessAddress() == null || loginSeller.getBusinessAddress().isBlank()) {
			writeJson(response, 400, new Result(false, "사업자 정보에 주소가 등록되어 있지 않습니다. 판매자센터에서 사업자 정보를 먼저 등록해주세요.", 0));
			return;
		}

		try {
			request.getParts(); // multipart 파싱 실패를 IllegalStateException으로 먼저 드러내기 위함 (getParameter()는 파싱 실패 시 예외 없이 null만 반환함)
			ProductWriteDTO dto = buildProductWriteDTO(request, loginSeller);

			if (dto.getProductName() == null || dto.getProductName().isBlank()) {
				writeJson(response, 400, new Result(false, "노출상품명을 입력해주세요.", 0));
				return;
			}
			if (dto.getInternalName() == null || dto.getInternalName().isBlank()) {
				writeJson(response, 400, new Result(false, "등록상품명(판매자관리용)을 입력해주세요.", 0));
				return;
			}
			if (dto.getProductPrice() <= 0) {
				writeJson(response, 400, new Result(false, "기본 상품가격을 입력해주세요.", 0));
				return;
			}
			if (dto.getShippingZipcode() == null || dto.getShippingZipcode().isBlank()
					|| dto.getShippingAddress() == null || dto.getShippingAddress().isBlank()) {
				writeJson(response, 400, new Result(false, "출고지 우편번호와 주소를 입력해주세요.", 0));
				return;
			}
			if (dto.getOptions().isEmpty()) {
				writeJson(response, 400, new Result(false, "옵션을 최소 1개 이상 추가해주세요.", 0));
				return;
			}

			int productNo = productWriteDAO.insertProduct(dto);

			new VendorActionLogDAO().log(loginSeller.getSellerNo(), "상품 등록", "PRODUCT", productNo, dto.getProductName());

			writeJson(response, 200, new Result(true, null, productNo));

		} catch (IllegalStateException e) {
			e.printStackTrace();
			Throwable cause = e.getCause();
			if (cause != null) {
				try {
					Object limit = cause.getClass().getMethod("getLimit").invoke(cause);
					System.err.println("[DEBUG] " + cause.getClass().getName() + " limit=" + limit);
				} catch (ReflectiveOperationException ignore) {
				}
			}
			writeJson(response, 400, new Result(false, "첨부 이미지의 개수 또는 용량이 너무 큽니다. 이미지 수를 줄이거나 용량을 낮춰 다시 시도해주세요.", 0));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			writeJson(response, 400, new Result(false, "입력값을 다시 확인해주세요.", 0));
		} catch (Exception e) {
			e.printStackTrace();
			writeJson(response, 500, new Result(false, "상품 등록 중 오류가 발생했습니다.", 0));
		}
	}

	private ProductWriteDTO buildProductWriteDTO(HttpServletRequest request, SellerDTO loginSeller) throws IOException, ServletException {

		ProductWriteDTO dto = new ProductWriteDTO();

		dto.setSellerNo(loginSeller.getSellerNo());
		dto.setSubCategoryNo(Integer.parseInt(request.getParameter("categoryNo")));
		dto.setProductPrice(parseIntOrZero(request.getParameter("productPrice")));

		dto.setSaleMethod("판매자배송");

		dto.setBrandName(blankToNull(request.getParameter("brandName")));
		dto.setNoBrandYn(request.getParameter("noBrandYn"));
		dto.setProductName(request.getParameter("displayName"));
		dto.setInternalName(request.getParameter("internalName"));

		String optionYn = request.getParameter("optionYn");
		dto.setOptionYn("N".equals(optionYn) ? "N" : "Y");

		dto.setDetailType(request.getParameter("detailType"));

		// 출고지 주소록 기능이 아직 없어서, 등록 폼에서 입력받되 비어 있으면 사업장 주소로 대체
		String shippingZipcode = blankToNull(request.getParameter("shippingZipcode"));
		String shippingAddress = blankToNull(request.getParameter("shippingAddress"));
		String shippingDetailAddress = blankToNull(request.getParameter("shippingDetailAddress"));

		dto.setShippingZipcode(shippingZipcode != null ? shippingZipcode : loginSeller.getZipcode());
		dto.setShippingAddress(shippingAddress != null ? shippingAddress : loginSeller.getBusinessAddress());
		dto.setShippingDetailAddress(shippingDetailAddress != null ? shippingDetailAddress : loginSeller.getBusinessDetailAddress());

		dto.setJejuShippingYn(request.getParameter("jejuShippingYn"));
		dto.setDeliveryServiceCode(request.getParameter("courier"));
		dto.setDeliveryMethod(request.getParameter("deliveryMethod"));
		dto.setBundleShippingYn(request.getParameter("bundleShippingYn"));
		dto.setShippingFeeType(request.getParameter("shippingFeeType"));

		dto.setLeadTimeInputType(request.getParameter("leadTimeInputType"));
		dto.setLeadTimeDays(parseIntOrNull(request.getParameter("leadTimeDays")));
		dto.setSameDayShipYn(request.getParameter("sameDayShipYn"));
		dto.setSameDayCutoffTime(blankToNull(request.getParameter("cutoffTime")));

		dto.setSaleStatus("승인 대기");

		int optionCount = parseIntOrZero(request.getParameter("optionCount"));
		for (int i = 0; i < optionCount; i++) {
			dto.getOptions().add(buildOptionDTO(request, i, loginSeller.getSellerNo()));
		}

		int descImageCount = parseIntOrZero(request.getParameter("descImageCount"));
		for (int i = 0; i < descImageCount; i++) {
			String url = saveUploadedImage(request, "descImage_" + i, loginSeller.getSellerNo());
			if (url != null) {
				dto.getDetailImageUrls().add(url);
			}
		}

		return dto;
	}

	private ProductOptionWriteDTO buildOptionDTO(HttpServletRequest request, int index, int sellerNo) throws IOException, ServletException {

		ProductOptionWriteDTO option = new ProductOptionWriteDTO();

		option.setOption1Type(blankToNull(request.getParameter("option_" + index + "_option1Type")));
		option.setOption1Value(blankToNull(request.getParameter("option_" + index + "_option1Value")));
		option.setOption2Type(blankToNull(request.getParameter("option_" + index + "_option2Type")));
		option.setOption2Value(blankToNull(request.getParameter("option_" + index + "_option2Value")));
		option.setOption3Type(blankToNull(request.getParameter("option_" + index + "_option3Type")));
		option.setOption3Value(blankToNull(request.getParameter("option_" + index + "_option3Value")));
		option.setNormalPrice(parseIntOrNull(request.getParameter("option_" + index + "_normalPrice")));
		option.setSalePrice(parseIntOrZero(request.getParameter("option_" + index + "_salePrice")));
		option.setAutoPriceAdjustYn(request.getParameter("option_" + index + "_autoPriceAdjustYn"));
		option.setQuantity(parseIntOrZero(request.getParameter("option_" + index + "_quantity")));
		option.setSellerProductCode(blankToNull(request.getParameter("option_" + index + "_sellerProductCode")));
		option.setModelNo(blankToNull(request.getParameter("option_" + index + "_modelNo")));
		option.setBarcode(blankToNull(request.getParameter("option_" + index + "_barcode")));

		option.setMainImageUrl(saveUploadedImage(request, "option_" + index + "_mainImage", sellerNo));

		int extraCount = parseIntOrZero(request.getParameter("option_" + index + "_extraImageCount"));
		for (int j = 0; j < extraCount; j++) {
			String extraUrl = saveUploadedImage(request, "option_" + index + "_extraImage_" + j, sellerNo);
			if (extraUrl != null) {
				option.getExtraImageUrls().add(extraUrl);
			}
		}

		return option;
	}

	private String saveUploadedImage(HttpServletRequest request, String partName, int sellerNo) throws IOException, ServletException {

		Part part = request.getPart(partName);

		if (part == null || part.getSize() == 0) {
			return null;
		}

		String submittedFileName = part.getSubmittedFileName();
		String originalName = (submittedFileName != null) ? Path.of(submittedFileName).getFileName().toString() : "";

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

	private void writeJson(HttpServletResponse response, int status, Result result) throws IOException {
		response.setStatus(status);
		try (PrintWriter out = response.getWriter()) {
			out.print(gson.toJson(result));
		}
	}

	private String blankToNull(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}

	private int parseIntOrZero(String value) {
		if (value == null || value.isBlank()) return 0;
		return Integer.parseInt(value.trim());
	}

	private Integer parseIntOrNull(String value) {
		if (value == null || value.isBlank()) return null;
		return Integer.parseInt(value.trim());
	}

	private static class Result {
		private final boolean success;
		private final String message;
		private final int productNo;

		private Result(boolean success, String message, int productNo) {
			this.success = success;
			this.message = message;
			this.productNo = productNo;
		}
	}

}
