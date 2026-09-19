package com.goodpang.dto;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WowMembershipDTO {

    private int wowMembershipNo;
    private int memberNo;
    private String status;

    private Date startDate;
    private Date nextPaymentDate;
    private Date cancelDate;
    private Date endDate;

    private String autoPaymentYn;

    private Integer paymentMethodNo;
    


    private String joinDate;

    private String paymentType;

    // 계좌
    private String bankCode;
    private String accountLast4;
    private String accountHolder;

    // 카드
    private String cardCompany;
    private String cardLast4;
}