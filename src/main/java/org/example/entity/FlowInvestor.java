package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowInvestor {
    private Long id;
    private String tsCode;
    private String annDate;
    private String endDate;
    private String holderName;
    private Double holdAmount;
    private String holdRatio;
    private String holdFloatRatio;
    private String holdChange;
    private String holderType;
    private String actEntType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
