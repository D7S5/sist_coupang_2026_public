package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AddressDAO;
import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/address/edit")
public class AddressEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 배송지 수정 페이지
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember =
                LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        String addressNoParam = request.getParameter("addressNo");
        String checkoutNo = request.getParameter("checkoutNo");
        String from = request.getParameter("from");

        if (addressNoParam == null || addressNoParam.isBlank()) {
            redirectBack(
                    request,
                    response,
                    from,
                    checkoutNo,
                    null
            );
            return;
        }

        try {
            int addressNo = Integer.parseInt(addressNoParam);
            int memberNo = loginMember.getMemberNo();

            AddressDAO dao = new AddressDAO();

            AddressDTO address =
                    dao.editGetAddress(addressNo, memberNo);

            if (address == null) {
                redirectBack(
                        request,
                        response,
                        from,
                        checkoutNo,
                        null
                );
                return;
            }

            request.setAttribute("address", address);
            request.setAttribute("checkoutNo", checkoutNo);
            request.setAttribute("from", from);

            request.getRequestDispatcher(
                    "/address_edit.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            redirectBack(
                    request,
                    response,
                    from,
                    checkoutNo,
                    null
            );

        } catch (Exception e) {
            throw new ServletException(
                    "배송지 조회 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 배송지 실제 수정
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember =
                LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        String checkoutNo = request.getParameter("checkoutNo");
        String from = request.getParameter("from");

        try {
            int memberNo = loginMember.getMemberNo();

            String addressNoParam =
                    request.getParameter("addressNo");

            if (addressNoParam == null
                    || addressNoParam.isBlank()) {
                throw new IllegalArgumentException(
                        "배송지 번호가 없습니다."
                );
            }

            int addressNo =
                    Integer.parseInt(addressNoParam);

            String receiverName =
                    request.getParameter("receiverName");
            String tel =
                    request.getParameter("tel");
            String zipcode =
                    request.getParameter("zipcode");
            String address =
                    request.getParameter("address");
            String detailAddress =
                    request.getParameter("detailAddress");
            String requestMsg =
                    request.getParameter("requestMsg");

            String addressDefault =
                    request.getParameter("addressDefault");

            if (addressDefault == null) {
                addressDefault = "N";
            }

            AddressDTO dto = new AddressDTO();

            dto.setAddressNo(addressNo);
            dto.setMemberNo(memberNo);
            dto.setReceiverName(receiverName);
            dto.setTel(tel);
            dto.setZipcode(zipcode);
            dto.setAddress(address);
            dto.setDetailAddress(detailAddress);
            dto.setRequestMsg(requestMsg);
            dto.setAddressDefault(addressDefault);

            AddressDAO dao = new AddressDAO();

            int result = dao.updateAddress(dto);

            
            if (result > 0) {
                redirectBack(
                        request,
                        response,
                        from,
                        checkoutNo,
                        addressNo
                );
                return;
            }

            // 결제창에서 수정한 경우
            if ("payment".equals(from)
                    && checkoutNo != null
                    && !checkoutNo.isBlank()) {

                request.getSession().setAttribute(
                        "addressEditError",
                        "배송지 수정에 실패했습니다."
                );

                response.sendRedirect(
                        request.getContextPath()
                        + "/order/payment?checkoutNo="
                        + checkoutNo
                        + "&addressNo="
                        + addressNo
                );

                return;
            }

            // 배송지 관리에서 수정한 경우
            request.setAttribute(
                    "error",
                    "배송지 수정에 실패했습니다."
            );

            request.setAttribute("address", dto);

            request.getRequestDispatcher(
                    "/address_edit.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            throw new ServletException(
                    "잘못된 배송지 번호입니다.",
                    e
            );

        } catch (Exception e) {
            e.printStackTrace();

            throw new ServletException(
                    "배송지 수정 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    // 수정 후 원래 페이지로 이동
    private void redirectBack(
            HttpServletRequest request,
            HttpServletResponse response,
            String from,
            String checkoutNo,
            Integer addressNo)
            throws IOException {

        if ("payment".equals(from)
                && checkoutNo != null
                && !checkoutNo.isBlank()) {

            StringBuilder url = new StringBuilder();

            url.append(request.getContextPath());
            url.append("/order/payment?checkoutNo=");
            url.append(checkoutNo);

            if (addressNo != null) {
                url.append("&addressNo=");
                url.append(addressNo);
            }

            response.sendRedirect(url.toString());
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/address/list"
        );
    }
}