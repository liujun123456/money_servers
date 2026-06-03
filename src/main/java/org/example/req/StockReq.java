package org.example.req;

import lombok.Data;

@Data
public class StockReq {
    private String api_name;
    private String token;
    private Object params;
}
