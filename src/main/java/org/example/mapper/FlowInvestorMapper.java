package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.FlowInvestor;
import org.example.entity.Stock;

import java.util.List;

@Mapper
public interface FlowInvestorMapper {

    // 批量插入
    int batchInsert(@Param("flowInvestors") List<FlowInvestor> flowInvestors);

}
