package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Stock {
    private Long id;   //主键id
    private String tsCode;  //股票Ts
    private String symbol;   //股票代码
    private String name;   //股票名称
    private String area;    //股票地址
    private String industry;  //股票类型
    private String cnSpell;  //中文缩写
    private String market;  //板块
    private String listDte;  //股票开始时间
    private String actName;   //实际控制人
    private String actEntType;  //企业类型
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
