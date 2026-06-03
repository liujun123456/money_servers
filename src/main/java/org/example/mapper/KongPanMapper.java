package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.FlowInvestor;
import org.example.entity.KongPan;
import org.example.entity.NewFlowInvertor;

import java.util.List;

@Mapper
public interface KongPanMapper {

    // 批量插入
    int batchInsert(@Param("kongPanList") List<KongPan> kongPans);


    // 插入用户
    @Insert("INSERT INTO kong_pan(symbol, name, total_flow, ten_person_flow, person_num,factor) " +
            "VALUES(#{symbol}, #{name}, #{totalFlow}, #{tenPersonFlow}, #{personNum}, #{factor})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KongPan user);

    // 查询所有用户
    @Select("SELECT * FROM kong_pan")
    List<KongPan> queryAll();

}
