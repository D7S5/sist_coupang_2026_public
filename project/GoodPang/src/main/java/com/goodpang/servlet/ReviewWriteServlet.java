package com.goodpang.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.ReviewDTO;
import com.goodpang.dto.ReviewItemDTO;
import com.goodpang.util.LoginUtil;
import com.goodpang.util.UploadPaths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/review/write")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024,
		maxFileSize = 10 * 1024 * 1024,
		maxRequestSize = 100 * 1024 * 1024
)
public class ReviewWriteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ReviewDAO reviewDAO = new ReviewDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String orderDetailNoParam = request.getParameter("orderDetailNo");
		String productNoParam = request.getParameter("productNo");

		if (orderDetailNoParam == null || productNoParam == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 리뷰 요청입니다.");
			return;
		}

		int orderDetailNo;
		int productNo;

		try {
			orderDetailNo = Integer.parseInt(orderDetailNoParam);
			productNo = Integer.parseInt(productNoParam);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 리뷰 요청입니다.");
			return;
		}

		MemberDTO loginMember = LoginUtil.requireLogin(request, response);

		if (loginMember == null) {
			return;
		}

		ReviewItemDTO reviewItem = reviewDAO.getReviewItem(
				orderDetailNo,
				productNo,
				loginMember.getMemberNo()
		);

		if (reviewItem == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "리뷰 작성 대상 상품을 찾을 수 없습니다.");
			return;
		}

		int reviewCount = reviewDAO.countReviewsByMemberNo(loginMember.getMemberNo());

		request.setAttribute("reviewCount", reviewCount);
		request.setAttribute("reviewItem", reviewItem);

		request.getRequestDispatcher("/review_write.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		MemberDTO loginMember = LoginUtil.requireLogin(request, response);

		if (loginMember == null) {
			return;
		}

		try {
			String orderDetailNoParam = request.getParameter("orderDetailNo");
			String productRatingParam = request.getParameter("productRating");
			String serviceRatingParam = request.getParameter("serviceRating");
			String reviewContent = request.getParameter("reviewContent");
			String reviewSummary = request.getParameter("reviewSummary");

			if (orderDetailNoParam == null || orderDetailNoParam.isBlank()
					|| productRatingParam == null || productRatingParam.isBlank()
					|| reviewContent == null || reviewContent.isBlank()) {

				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "필수 리뷰 정보가 없습니다.");
				return;
			}

			int orderDetailNo = Integer.parseInt(orderDetailNoParam);
			int productRating = Integer.parseInt(productRatingParam);

			if (productRating < 1 || productRating > 5) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "상품 별점이 올바르지 않습니다.");
				return;
			}

			if (reviewDAO.existsByOrderDetailNo(orderDetailNo)) {
				response.sendError(HttpServletResponse.SC_CONFLICT, "이미 작성한 리뷰입니다.");
				return;
			}

			ReviewDTO dto = new ReviewDTO();

			dto.setOrderDetailNo(orderDetailNo);
			dto.setMemberNo(loginMember.getMemberNo());
			dto.setProductRating(productRating);
			dto.setReviewContent(reviewContent.trim());
			dto.setReviewSummary(
					reviewSummary == null ? null : reviewSummary.trim()
			);

			if (serviceRatingParam != null && !serviceRatingParam.isBlank()) {
				int serviceRating = Integer.parseInt(serviceRatingParam);

				if (serviceRating < 1 || serviceRating > 2) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "서비스 만족도가 올바르지 않습니다.");
					return;
				}

				dto.setServiceRating(serviceRating);
			}

			// 1. 리뷰 등록 후 REVIEW_NO 반환
			int reviewNo = reviewDAO.insertReview(dto);

			// 2. 리뷰 이미지 파일 저장
			List<String> imageUrls = saveUploadedFiles(
					request,
					"reviewImages",
					reviewNo
			);

			// 3. 리뷰 이미지 DB 저장
			int imageOrder = 1;

			for (String imageUrl : imageUrls) {
				reviewDAO.insertReviewImage(
						reviewNo,
						imageUrl,
						imageOrder++
				);
			}

			response.sendRedirect(
					request.getContextPath() + "/review/list"
			);

		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 리뷰 데이터입니다.");

		} catch (Exception e) {
			e.printStackTrace();

			throw new ServletException(
					"리뷰 등록 중 오류가 발생했습니다.",
					e
			);
		}
	}

	private List<String> saveUploadedFiles(
			HttpServletRequest request,
			String partName,
			int reviewNo)
			throws IOException, ServletException {

		List<String> imageUrls = new ArrayList<>();
		int count = 0;

		for (Part part : request.getParts()) {

			if (!partName.equals(part.getName())) {
				continue;
			}

			if (part.getSize() == 0) {
				continue;
			}

			String submittedFileName = part.getSubmittedFileName();

			if (submittedFileName == null || submittedFileName.isBlank()) {
				continue;
			}

			if (count >= 10) {
				throw new ServletException("사진은 최대 10장까지 첨부할 수 있습니다.");
			}

			String contentType = part.getContentType();

			if (contentType == null || !contentType.startsWith("image/")) {
				throw new ServletException("이미지 파일만 업로드할 수 있습니다.");
			}

			String originalName = Path.of(submittedFileName)
					.getFileName()
					.toString();

			String ext = "";
			int dotIndex = originalName.lastIndexOf('.');

			if (dotIndex >= 0) {
				ext = originalName.substring(dotIndex).toLowerCase();
			}

			if (!ext.equals(".jpg")
					&& !ext.equals(".jpeg")
					&& !ext.equals(".png")
					&& !ext.equals(".gif")
					&& !ext.equals(".webp")) {

				throw new ServletException(
						"jpg, jpeg, png, gif, webp 이미지만 업로드할 수 있습니다."
				);
			}

			String savedName = UUID.randomUUID() + ext;

			String uploadDir =
					UploadPaths.resolveBaseDir(getServletContext())
					+ File.separator
					+ "review"
					+ File.separator
					+ reviewNo;

			File uploadDirFile = new File(uploadDir);

			if (!uploadDirFile.exists() && !uploadDirFile.mkdirs()) {
				throw new IOException("리뷰 이미지 저장 폴더를 생성할 수 없습니다.");
			}

			part.write(
					uploadDir
					+ File.separator
					+ savedName
			);

			String imageUrl =
					"/upload/review/"
					+ reviewNo
					+ "/"
					+ savedName;

			imageUrls.add(imageUrl);
			count++;
		}

		return imageUrls;
	}
}