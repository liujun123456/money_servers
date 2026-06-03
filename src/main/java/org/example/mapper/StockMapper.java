package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.Stock;
import org.example.entity.User;

import java.util.List;

@Mapper
public interface StockMapper {

    // 批量插入
    int batchInsert(@Param("stocks") List<Stock> stocks);


    // 查询所有用户
    @Select("SELECT * FROM stock")
    List<Stock> selectAll();
}
