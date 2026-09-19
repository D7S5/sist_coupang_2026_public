package com.goodpang.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/*
 * 공지사항(NOTICE) 게시판 한 건 — 관리자만 등록/수정/삭제 가능, 판매자는 조회만 가능.
 */
@Getter
@Setter
public class NoticeDTO {

    private int noticeNo;
    private String title;
    private String content;
    private String noticeType; // '공지' 또는 '안내'

    private int adminNo;
    private String adminName;

    private Date createdDate;
    private Date updatedDate;
}
