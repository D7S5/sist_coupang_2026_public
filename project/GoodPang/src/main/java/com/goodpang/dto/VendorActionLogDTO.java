package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자 액션 로그(VENDOR_ACTION_LOG) 한 건.
 */
@Getter
@Setter
public class VendorActionLogDTO {

    private int actionLogNo;
    private int sellerNo;
    private String storeName;

    private String actionType;
    private String targetType;
    private int targetNo;
    private String detail;

    private Date actionDate;
}
