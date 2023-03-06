package com.example.mapper;

import com.example.core.entity.table.TableIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chenwh3
 */
@Mapper
public interface BaseMapper {

    List<Map> getColumnsInfo(@Param("tableName") String tableName);


    List<TableIndex> getIdxInfo(@Param("tableName") String tableName);

    Map getTableInfo(@Param("tableName") String tableName);
}
