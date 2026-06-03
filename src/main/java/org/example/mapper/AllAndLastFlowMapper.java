package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.example.entity.AllAndLastFlow;
import org.example.entity.FlowInvestor;
import org.example.entity.NewFlowInvertor;

import java.util.List;

@Mapper
public interface AllAndLastFlowMapper {

    // 批量插入
    int batchInsert(@Param("allAndLastFlows") List<AllAndLastFlow> allAndLastFlows);


    @ResultMap("BaseResultMap")
    @Select("SELECT * FROM all_and_last_flow where end_date = #{time}")
    List<AllAndLastFlow> selectByTime(String time);


    // 查询所有用户
    @Select("SELECT * FROM all_and_last_flow")
    List<AllAndLastFlow> selectAll();

}
