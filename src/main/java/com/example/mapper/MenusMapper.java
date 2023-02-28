package com.example.mapper;

import cn.hutool.json.JSONObject;
import com.example.core.daoEntity.UpmPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chenwh3
 */
@Mapper
public interface MenusMapper {

    int insert(@Param("obj") UpmPermission upmPermission);
}
