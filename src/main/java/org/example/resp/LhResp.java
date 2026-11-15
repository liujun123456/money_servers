package org.example.resp;

import lombok.Data;
import org.example.entity.LongHuBean;

import java.util.List;

@Data
public class LhResp {
    private int currentPage;
    private int totalPage;
    private List<LongHuBean> result;
}
