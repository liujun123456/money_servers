package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class NiuSanConnect {
    private Long id;
    private String firstPeople;
    private String secondPeople;
    private String connectCount;
    private String connectSymbol;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
