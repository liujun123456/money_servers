package org.example.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LongHuCountBean {
    private String StockCode;
    private String StockName;
    List<LongHuBean> stockLonghuBuy=new ArrayList<>();
    List<LongHuBean> stockLonghuSell=new ArrayList<>();

}
