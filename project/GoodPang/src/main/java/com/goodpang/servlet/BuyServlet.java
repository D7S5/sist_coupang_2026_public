package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;

import com.goodpang.dao.CheckoutDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/buy")
public class BuyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private final WowMembershipDAO wowDao = new WowMembershipDAO();  

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO member =
                LoginUtil.requireLogin(
                        request,
                        response
                );

        if (member == null) {
            return;
        }

        int memberNo =
                member.getMemberNo();

        String productNoParam =
                request.getParameter("productNo");

        String quantityParam =
                request.getParameter("quantity");
        
        String optionIdParam =
                request.getParameter("optionId");


        if (productNoParam == null
                || productNoParam.isBlank()
                || quantityParam == null
                || quantityParam.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "상품 정보가 올바르지 않습니다."
            );

            return;
        }

        Connection conn = null;
        try {
            int productNo =
                    Integer.parseInt(productNoParam);
            int quantity =
                    Integer.parseInt(quantityParam);
            if (quantity <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "수량은 1개 이상이어야 합니다."
                );
                return;
            }

            Integer optionId = null;

            if (optionIdParam != null
                    && !optionIdParam.isBlank()) {

                optionId =
                        Integer.valueOf(optionIdParam);
            }

            conn =
                    ConnectionProvider.getConnection();

            conn.setAutoCommit(false);

            CheckoutDAO dao =
                    new CheckoutDAO();

            int productPrice =
                    dao.getProductPrice(
                            conn,
                            productNo
                    );

            if (productPrice < 0) {

                throw new Exception(
                        "상품을 찾을 수 없습니다."
                );
            }

            int optionPrice = 0;

            if (optionId != null) {

                optionPrice =
                        dao.getOptionPrice(
                                conn,
                                productNo,
                                optionId
                        );
            }

            // 실제 구매 단가
            int unitPrice =
                    productPrice + optionPrice;

            // 수량 포함 상품금액
            int productAmount =
                    unitPrice * quantity;

            int instantDiscount = 0;
            int couponDiscount = 0;
            int cashUsed = 0;

            boolean isWowMember = wowDao.isWowMember(memberNo);
            		
            int deliveryFee = 0;

            if (isWowMember) {
            	deliveryFee = 0;
            	
            } else {
            	deliveryFee = 
                        productAmount >= 19800
                                ? 0
                                : 3000;
            }

            int totalPrice =
                    productAmount
                    - instantDiscount
                    - couponDiscount
                    - cashUsed
                    + deliveryFee;

            int checkoutNo =
                    dao.insertCheckout(
                            conn,
                            memberNo,
                            productAmount,
                            instantDiscount,
                            couponDiscount,
                            cashUsed,
                            deliveryFee,
                            totalPrice
                    );
            if (checkoutNo <= 0) {
                throw new Exception(
                        "CHECKOUT 생성 실패"
                );
            }
            int itemResult =
                    dao.insertCheckoutItem(
                            conn,
                            checkoutNo,
                            productNo,
                            optionId,
                            quantity,
                            unitPrice
                    );

            if (itemResult != 1) {

                throw new Exception(
                        "CHECKOUT_ITEM 생성 실패"
                );
            }
            conn.commit();
            response.sendRedirect(
                    request.getContextPath()
                    + "/order/payment?checkoutNo="
                    + checkoutNo
            );

        } catch (NumberFormatException e) {
            rollback(conn);
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 상품 정보입니다."
            );
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            throw new ServletException(
                    "바로구매 처리 중 오류가 발생했습니다.",
                    e
            );

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void rollback(
            Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}