package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.FlowInvestor;
import org.example.entity.NewFlowInvertor;
import org.example.entity.Stock;
import org.example.entity.User;
import org.example.resp.NiuSanResp;

import java.util.List;

@Mapper
public interface NewFlowInvestorMapper {


    // 批量插入
    int batchInsert(@Param("flowInvestors") List<NewFlowInvertor> flowInvestors);


    // 查询所有用户
    @Select("SELECT * FROM new_flow_investor")
    List<NewFlowInvertor> selectAll();

    @Select("SELECT * FROM new_flow_investor where end_date > 20250331")
    List<NewFlowInvertor> selectByCondition();


    @Select("SELECT name,symbol,end_date as endDate,all_holder_name as allHolderName FROM new_flow_investor  WHERE all_holder_name like  CONCAT('%', #{name}, '%')")
    List<NiuSanResp> queryNewSanByName(String name);

    @Select("SELECT name,symbol,end_date as endDate,all_holder_name as allHolderName FROM new_flow_investor  WHERE symbol = #{code}")
    List<NiuSanResp> queryNewSanByCode(String code);


}
