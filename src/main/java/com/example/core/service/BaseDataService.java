package com.example.core.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.TableDefinition;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
import com.example.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;

/**
 * @author chenwh3
 */
public class BaseDataService {

    private BaseMapper mapper;

    public List<ColumnDefinition> getColumnsInfo(String tableName) {
        List<Map> columnsInfo = mapper.getColumnsInfo(tableName);

        List<ColumnDefinition> list = new ArrayList<>();
        for (Map column : columnsInfo) {
            JSONObject rowMap = new JSONObject(column);

            ColumnDefinition columnDefinition = new ColumnDefinition();
            String columnName = rowMap.getStr("COLUMN_NAME");
            String type = rowMap.getStr("DATA_TYPE");
            Integer order = rowMap.getInt("ORDINAL_POSITION");
            String remark = rowMap.getStr("REMARK");
            String defaultVal = rowMap.getStr("COLUMN_DEFAULT");
            boolean isIndentity = rowMap.getBool("isIndentity");

            String[] temp = type.split("\\s+");


            if (defaultVal != null) {
                columnDefinition.setDefaultVal(StrUtil.unWrap(defaultVal, "(", ")"));
            }
            columnDefinition.setPk(Objects.equals(order, 1));
            columnDefinition.setColumnName(columnName);

            if (remark.contains("; ")) {
                Map<String,String> map = new LinkedHashMap<>();
                String[] vals = remark.split(";")[1].split("\\s*,\\s*");
                for (String val : vals) {
                    String[] entry = val.split("\\s*-\\s*");
                    String key = entry[0].trim();
                    String value = key;
                    if (entry.length > 1) {
                        value = entry[1].trim();
                    }
                    map.put(key,value);
                }
                columnDefinition.setEnumMap(map);
            }
            String selectSql = columnName;
            if (type.startsWith("date")) {
                selectSql = String.format("CONVERT(varchar(19), %s, 120) as %s", columnName, columnName);
            }
            columnDefinition.setSelectSql(selectSql);

            columnDefinition.setIdentity(isIndentity);
            columnDefinition.setType(temp[0]);
            columnDefinition.setRemark(remark);
            columnDefinition.setJavaType(SqlTypeUtil.convertToJavaBoxType(temp[0]));
            columnDefinition.setJavaFieldName(StringUtil.underlineToCamelhump(columnName));
            columnDefinition.setJdbcType(SqlTypeUtil.convertToMyBatisJdbcType(temp[0]));
            list.add(columnDefinition);
        }
        return list;

    }


    public TableDefinition getTableInfo(String tableName) {
        JSONObject rowMap = new JSONObject(mapper.getTableInfo(tableName));
        TableDefinition tableDefinition = new TableDefinition();
        tableDefinition.setTableName(rowMap.getStr("TABLE_NAME"));
        tableDefinition.setDesc(rowMap.getStr("TABLE_DESC"));
        return tableDefinition;
    }



}
