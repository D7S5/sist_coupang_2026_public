package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 관리자 액션 로그(admin-action-log-list.jsp) 테이블 한 행.
 */
@Getter
@Setter
public class AdminActionLogDTO {

    private int actionLogNo;
    private int adminNo;
    private String adminName;

    private String actionType;
    private String targetType;
    private int targetNo;
    private String reason;

    private Date actionDate;
}
