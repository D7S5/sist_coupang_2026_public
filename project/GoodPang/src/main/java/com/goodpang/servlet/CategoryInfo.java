package com.goodpang.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.goodpang.dto.CategoryDTO;
import com.goodpang.util.ConnectionProvider;

@WebServlet("/category/getinfo")
public class CategoryInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CategoryInfo() {
		super();

	}

	private static final Gson gson = new Gson();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String keyword = request.getParameter("keyword");

		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		if (keyword != null && !keyword.isBlank()) {
			handleKeywordSearch(keyword.trim(), response);
			return;
		}

		String sql = """
				SELECT category_no,
				       category_name,
				       parent_category_no,
				       category_level
				FROM CATEGORY
				START WITH parent_category_no IS NULL
				CONNECT BY PRIOR category_no = parent_category_no
				       AND LEVEL <= 3
				ORDER SIBLINGS BY category_no


				""";

		List<CategoryDTO> categoryList = new ArrayList<>();
		boolean hasError = false;

		try (
				Connection conn = ConnectionProvider.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				long categoryNo = rs.getLong("category_no");
				String categoryName = rs.getString("category_name");
				long parentCategoryNo = rs.getLong("parent_category_no");
				boolean parentIsNull = rs.wasNull();
				int categoryLevel = rs.getInt("category_level");

				categoryList.add(new CategoryDTO(
						categoryNo,
						categoryName,
						parentIsNull ? null : parentCategoryNo,
						categoryLevel,
						categoryLevel == 1 ? "/pds/category_" + categoryNo + ".png" : null,
						null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
		}

		String ctype = request.getParameter("ctype");

		if (hasError) {
			response.setStatus(500);
		}

		try (PrintWriter out = response.getWriter()) {
			if (hasError) {
				out.print(gson.toJson(new ErrorResponse("카테고리 조회 중 오류가 발생했습니다.")));
			} else if ("main".equals(ctype)) {
				gson.toJson(groupByLevel(categoryList), out);
			} else {
				gson.toJson(categoryList, out);
			}
		}
	}

	/**
	 * 검색어(keyword)와 이름이 일치하는 카테고리 중 "최종 항목"(자식이 없는 리프,
	 * 즉 상품 등록 시 실제로 선택하는 소분류)만 조회해서 JSON으로 응답한다.
	 * PRODUCT는 아직 최신 스키마로 정리되지 않아 조인하지 않고 CATEGORY만 조회한다.
	 *
	 * 같은 이름의 리프가 서로 다른 상위 카테고리에 있을 수 있어서(예: 남성패션/여성패션에 각각 "의류"),
	 * CONNECT BY로 리프 → 루트까지 거슬러 올라가며 이름을 모아 categoryPath("대분류 > 중분류 > 소분류")를
	 * 함께 내려준다 - 프론트 자동완성 목록에서 구분할 수 있도록.
	 */
	private void handleKeywordSearch(String keyword, HttpServletResponse response) throws IOException {

		String innerSql = """
				SELECT CONNECT_BY_ROOT category_no AS leaf_no,
				       CONNECT_BY_ROOT category_name AS leaf_name,
				       CONNECT_BY_ROOT parent_category_no AS leaf_parent_no,
				       CONNECT_BY_ROOT category_level AS leaf_level,
				       SYS_CONNECT_BY_PATH(category_name, '|') AS path_from_leaf,
				       parent_category_no AS cur_parent_no
				FROM CATEGORY c
				START WITH category_name LIKE '%' || ? || '%'
				  AND NOT EXISTS (
				        SELECT 1
				        FROM CATEGORY child
				        WHERE child.parent_category_no = c.category_no
				      )
				CONNECT BY category_no = PRIOR parent_category_no
				""";

		// CONNECT BY는 리프에서 루트 방향으로 거슬러 올라가며 한 단계씩 행을 만들어내는데,
		// cur_parent_no가 NULL인 행이 곧 "루트까지 다 올라간" 행이라 그때의 path_from_leaf에
		// 리프부터 루트까지 이름이 전부 쌓여 있음 (buildCategoryPath()에서 순서를 뒤집어 씀)
		String sql = "SELECT * FROM (" + innerSql + ") WHERE cur_parent_no IS NULL ORDER BY leaf_no";

		List<CategoryDTO> resultList = new ArrayList<>();
		boolean hasError = false;

		try (
				Connection conn = ConnectionProvider.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, keyword);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					long categoryNo = rs.getLong("leaf_no");
					String categoryName = rs.getString("leaf_name");
					long parentCategoryNo = rs.getLong("leaf_parent_no");
					boolean parentIsNull = rs.wasNull();
					int categoryLevel = rs.getInt("leaf_level");
					String pathFromLeaf = rs.getString("path_from_leaf");

					CategoryDTO dto = new CategoryDTO(
							categoryNo,
							categoryName,
							parentIsNull ? null : parentCategoryNo,
							categoryLevel,
							null,
							buildCategoryPath(pathFromLeaf));
					resultList.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
		}

		if (hasError) {
			response.setStatus(500);
		}

		try (PrintWriter out = response.getWriter()) {
			if (hasError) {
				out.print(gson.toJson(new ErrorResponse("검색어 카테고리 조회 중 오류가 발생했습니다.")));
			} else {
				gson.toJson(resultList, out);
			}
		}
	}

	/** "|리프|부모|조부모|...|루트" (SYS_CONNECT_BY_PATH 결과) → "루트 > ... > 부모 > 리프" 로 순서를 뒤집는다 */
	private String buildCategoryPath(String pathFromLeaf) {
		if (pathFromLeaf == null || pathFromLeaf.isBlank()) {
			return null;
		}

		String[] names = pathFromLeaf.split("\\|");
		StringBuilder path = new StringBuilder();

		// split 결과의 0번째는 구분자 앞의 빈 문자열이라 1번째부터 순회, 뒤에서부터 이어붙임
		for (int i = names.length - 1; i >= 1; i--) {
			if (path.length() > 0) {
				path.append(" > ");
			}
			path.append(names[i]);
		}

		return path.toString();
	}

	/** categoryLevel(1=대분류, 2=중분류, 3=소분류)별로 묶어서 "1", "2", "3" 키를 가진 맵으로 정리 */
	private Map<Integer, List<CategoryDTO>> groupByLevel(List<CategoryDTO> categoryList) {
		Map<Integer, List<CategoryDTO>> groupedByLevel = new LinkedHashMap<>();

		for (CategoryDTO category : categoryList) {
			groupedByLevel
					.computeIfAbsent(category.getCategoryLevel(), level -> new ArrayList<>())
					.add(category);
		}

		return groupedByLevel;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private static class ErrorResponse {
		private final String error;

		private ErrorResponse(String error) {
			this.error = error;
		}
	}

}
