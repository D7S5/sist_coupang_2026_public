package com.goodpang.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter

public class OrderDetailDTO {

	private int orderNo; 
	private int memberNo;
	private String orderStatus; 
	/* private LocalDate orderDate; */
	private Timestamp orderDate;
	private int totalPrice; 
	private String productName; 
	private int quantity;
	private String option1Type;
	private String option1Value;
	private String option2Type;
	private String option2Value;
	private Long orderDetailNo;
    private Long productNo;
    private int deliveryFee;
    private String paymentMethod;
    private String requestMsg;
    private String address;
    private String detailAddress;
    private String memberName;
    private String phone;
    private int itemPrice;
    
    // 주문번호 당 총 가격
    private int finalTotalPrice;
    
    //이미지
    private String imageUrl;
 
    
 // [취소/반품(PRODUCT_RETURN) 관련 추가 필드]
    private Long returnNo;
    private Timestamp requestDate;        // 취소/반품 신청일자
    private String returnReason;          // 취소/반품 사유
    private int refundAmount;             // 환불 금액
    private Timestamp expectedCancelDate; // ★ 신규 추가: 취소 완료 예정일
    
    // 포맷 관련 필드 추가
    private String cardCompanyName;
    private String bankName;
    
 // [★ 배송조회 추가 필드]
    private String invoiceNo;            // 송장번호 (DELIVERY.INVOICE_NO)
    private List<DeliveryLogDTO> logList; // 시간대별 배송 이력 리스트
    
    private String receiveLocation; // 수령방법 (ORDER_ADDRESS.RECEIVE_LOCATION)
    
    private int optionId;
    
}