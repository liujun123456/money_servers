package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllAndLastFlow {
    private Long id;
    private String tsCode;
    private String symbol;   //股票代码
    private String name;   //股票名称
    private String totalAmount;   //总股本
    private String tenAmount;   //10大股东总股本
    private String lastFlowHoldAmount;  //10大流通最后一位持股量
    private String endDate;  //报告期
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
