package org.example.entity;


import lombok.Data;

@Data
public class LongHuBean {
    private String ts_code;
    private String trade_date;  //日期
    private String exalter;    //营业部
    private String side;      //买卖类型0：买入金额最大的前5名， 1：卖出金额最大的前5名
    private String buy;     // 买入额（元）
    private String buy_rate;  //买入占总成交比例
    private String sell;   //卖出额
    private String sell_rate; //卖出占总成交比例
    private String net_buy;  //净成交额（元）
    private String reason;   //上榜理由

}
