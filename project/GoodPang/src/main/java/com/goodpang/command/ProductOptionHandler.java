package com.goodpang.command;

import java.util.List;

import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.service.ProductOptionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /option 요청 처리 — 원래 ProductOptionServlet.doGet() 이 하던 일.
 * 옵션(사이즈/색상 등)을 바꿀 때 product.js 가 보내는 ajax(GET)를 받아 JSON 으로 응답함.
 *
 * 화면(JSP)으로 가지 않고 여기서 응답을 직접 다 쓰기 때문에 항상 null 을 리턴함 —
 * DispatcherServlet 은 null 을 받으면 forward 를 건너뜀.
 *
 * ※ ProductOptionServlet 은 아직 원본 그대로 살아있음.
 */
public class ProductOptionHandler implements CommandHandler {

    private final ProductOptionService service = new ProductOptionService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("application/json; charset=UTF-8");

        String optionIdParam = request.getParameter("optionId");

        try {
            int optionId = Integer.parseInt(optionIdParam);

            ProductOptionDTO option = service.getOption(optionId);

            if (option == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "옵션을 찾을 수 없습니다.");
                return null;
            }

            List<ProductImageDTO> images = service.getImages(optionId);

            response.getWriter().write(service.buildOptionJson(option, images));

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "optionId 가 올바르지 않습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "옵션 조회 중 오류가 발생했습니다.");
        }

        // JSON 을 직접 썼으므로 forward 할 뷰가 없음
        return null;
    }
}
