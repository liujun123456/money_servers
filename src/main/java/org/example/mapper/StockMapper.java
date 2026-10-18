package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.NiuSanConnect;
import org.example.entity.Stock;
import org.example.entity.User;

import java.util.List;

@Mapper
public interface StockMapper {

    // 批量插入
    int batchInsert(@Param("stocks") List<Stock> stocks);


    @Select("SELECT * FROM stock where ts_code = #{ts_code}")
    List<Stock> queryStockByTsCode(String ts_code);

    // 查询所有用户
    @Select("SELECT * FROM stock")
    List<Stock> selectAll();
}
