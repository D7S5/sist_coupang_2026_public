package com.goodpang.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeliveryLogDTO {
    private Long logNo;
    private int orderNo;
    private Timestamp logTime;
    private String currentLocation;
    private String deliveryStatus;
}