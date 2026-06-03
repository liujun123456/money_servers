package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewFlowInvertor {
    private Long id;
    private String tsCode;
    private String symbol;   //股票代码
    private String name;   //股票名称
    private String annDate;
    private String endDate;
    private String allHolderName;
    private String totalHoldAmount;
    private String lastHoldAmount;
    private String lastHoldRatio;
    private String lastHoldFloatRatio;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
