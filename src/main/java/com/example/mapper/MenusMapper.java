package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chenwh3
 */
@Mapper
public interface MenusMapper {

    List<Map> getAllMenus(@Param("tableName") String tableName);
}
