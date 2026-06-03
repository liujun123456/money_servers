package org.example.resp;

import lombok.Data;

@Data
public class HolderResp {
    public String ts_code;   //TS股票代码
    public String ann_date;   //公告日期
    public String end_date;   //报告期
    public String holder_name;  //股东名称
    public Double hold_amount;   //持有数量（股）
    public Double hold_ratio;   //占总股本比例(%)
    public Double hold_float_ratio;  //占流通股本比例(%)
    public Double hold_change;   //持股变动
    public String holder_type;  //股东类型
}
