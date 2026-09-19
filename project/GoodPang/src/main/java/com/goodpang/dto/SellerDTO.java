package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDTO {

    private int sellerNo;
    private String email;
    private String sellerPw;
    private String managerName;
    private String phone;

    private String businessNo;
    private String businessType;
    private String ceoName;
    private String storeName;

    private String zipcode;
    private String businessAddress;
    private String businessDetailAddress;
    private String mailOrderNo;
    private Integer categoryNo;

    private String bankName;
    private String accountNo;
    private String accountHolder;

    private String businessCertUrl;
    private String mailOrderCertUrl;

    private String approvalStatus;
    private String rejectReason;
    private Integer approvedAdminNo;
    private Date approvedDate;

    private Date createdDate;
    private Date updatedDate;
}
