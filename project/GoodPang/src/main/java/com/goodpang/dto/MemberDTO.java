package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {

    private int memberNo;
    private String memberId;
    private String memberPw;
    private String memberName;
    private String phone;
    private String email;
    private String rank;
    
    private int status;
}
