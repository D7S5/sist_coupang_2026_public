package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

	 private int addressNo;
     private int memberNo;
     private String receiverName;
     private String tel;
     private String zipcode;
     private String address;
     private String detailAddress;
     private String requestMsg;

     private String addressDefault;
}