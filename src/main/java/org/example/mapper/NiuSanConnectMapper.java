package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.NiuSanConnect;
import org.example.resp.NiuSanResp;

import java.util.List;

@Mapper
public interface NiuSanConnectMapper {
    // 批量插入
    int batchInsert(@Param("connectList") List<NiuSanConnect> connectList);


    @Select("SELECT * FROM niusan_connect where first_people = #{name}")
    List<NiuSanConnect> queryConnectByName(String name);
}
