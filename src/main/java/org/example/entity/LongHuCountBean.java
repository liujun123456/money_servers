package org.example.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LongHuCountBean {
    private String StockCode;
    private String StockName;
    List<String> stockLonghuBuy=new ArrayList<>();
    List<String> stockLonghuSell=new ArrayList<>();

}
