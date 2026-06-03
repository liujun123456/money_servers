package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KongPan {
    private Long id;   //主键id
    private String symbol;   //股票代码
    private String name;   //股票代码
    private String totalFlow;   //总流通股本
    private String tenPersonFlow;    //10大流通股本
    private String personNum;  //股东人数
    private String factor;  //系数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
