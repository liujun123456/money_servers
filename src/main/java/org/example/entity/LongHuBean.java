package org.example.entity;


import lombok.Data;

@Data
public class LongHuBean {
    private Long id;   //主键id
    private String tsCode;
    private String symbol;  //股票代码
    private String symbolName;  //股票名称
    private String tradeDate;  //日期
    private String exalter;    //营业部
    private String side;      //买卖类型0：买入金额最大的前5名， 1：卖出金额最大的前5名
    private String buy;     // 买入额（元）
    private String buyRate;  //买入占总成交比例
    private String sell;   //卖出额
    private String sellRate; //卖出占总成交比例
    private String netBuy;  //净成交额（元）
    private String reason;   //上榜理由

}
