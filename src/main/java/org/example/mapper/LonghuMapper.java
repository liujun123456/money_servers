package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.LongHuBean;
import org.example.entity.Stock;

import java.util.List;

@Mapper
public interface LonghuMapper {

    // 批量插入
    int batchInsert(@Param("longHuBeanList") List<LongHuBean> longHuBeanList);

    @Select("SELECT count(*) FROM longhu_table where exalter = #{exalter}" )
    int selectLhCount(String exalter);
    // 批量插入
    @Select("SELECT * FROM longhu_table where exalter = #{exalter}   ORDER BY trade_date DESC  LIMIT 100 OFFSET #{offset}" )
    List<LongHuBean> selectByLh(String exalter,int offset);
}
