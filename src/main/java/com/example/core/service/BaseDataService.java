package com.example.core.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.table.ColumnDefinition;
import com.example.core.entity.table.TableDefinition;
import com.example.core.entity.table.TableIndex;
import com.example.core.ex.ServiceException;
import com.example.core.thread.Ctx;
import com.example.core.util.SpelUtils;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
import com.example.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author chenwh3
 */
public class BaseDataService {

    private BaseMapper mapper;



    public List<ColumnDefinition> getColumnsInfo(String tableName) {
        List<Map> columnsInfo = mapper.getColumnsInfo(tableName);

        String t = Ctx.getStr("t");

        List<ColumnDefinition> list = new ArrayList<>();
        for (Map column : columnsInfo) {
            JSONObject rowMap = new JSONObject(column);

            ColumnDefinition obj = new ColumnDefinition();
            String columnName = rowMap.getStr("COLUMN_NAME");
            if(StrUtil.isUpperCase(columnName)){
                columnName = columnName.toLowerCase();
            }
            String type = rowMap.getStr("DATA_TYPE");
            if("timestamp".equals(type)){
                continue;
            }
            Integer order = rowMap.getInt("ORDINAL_POSITION");
            String remark = rowMap.getStr("REMARK");
            String defaultVal = rowMap.getStr("COLUMN_DEFAULT");
            boolean isIndentity = rowMap.getBool("isIndentity");

            String[] temp = type.split("\\s+");


            if (defaultVal != null) {
                obj.setDefaultVal(StrUtil.unWrap(defaultVal, "(", ")"));
            }
            obj.setPk(Objects.equals(order, 1));
            obj.setColumnName(columnName);
            obj.setMaxLength(rowMap.getInt("CHARACTER_MAXIMUM_LENGTH"));
            obj.setTitle(StrUtil.nullToDefault(remark, "").split("[ ;；]")[0].trim());

            if (StrUtil.isNotBlank(remark) && StrUtil.containsAny(remark, "；", ";")) {
                String[] vals = remark.split("[；;]")[1].split("\\s*[,，]\\s*");
                if (remark.contains("-")) {
                    Map<String, String> map = new LinkedHashMap<>();
                    for (String val : vals) {
                        String[] entry = val.split("\\s*-\\s*");
                        String key = entry[0].trim();
                        String value = entry[1].trim();
                        map.put(key, value);
                    }
                    obj.setEnumMap(map);
                } else {
                    List<String> enumList = new ArrayList<>();
                    for (String val : vals) {
                        enumList.add(val.trim());
                    }
                    obj.setEnumList(enumList);
                }
            }


            String javaFieldName = StringUtil.underlineToCamelhump(columnName);

            if (StrUtil.equalsAnyIgnoreCase(javaFieldName, "isDel")) {
                obj.getEnumMap().put("0", "否");
                obj.getEnumMap().put("1", "是");
            }

            String selectSql;
            if (javaFieldName.equals(columnName)) {
                selectSql = StrUtil.format("{}.{}", t, columnName);
            } else {
                selectSql = StrUtil.format("{}.{} as {}", t, columnName, javaFieldName);
            }
            if (StringUtils.equalsAny(type, "date")) {
                selectSql = String.format("CONVERT(varchar(10), %s.%s, 120) as %s", t, columnName, javaFieldName);
            }
            if (StringUtils.equalsAny(type, "datetime")) {
                selectSql = String.format("CONVERT(varchar(19), %s.%s, 120) as %s",t, columnName, javaFieldName);
            }
            obj.setSelectSql(selectSql);

            obj.setIdentity(isIndentity);
            obj.setType(temp[0]);
            obj.setRemark(remark);
            obj.setJavaType(SqlTypeUtil.convertToJavaBoxType(temp[0]));
            obj.setJavaFieldName(javaFieldName);

            obj.setJavaFiledNameUpCaseFirst(StrUtil.upperFirst(javaFieldName));
            obj.setJdbcType(SqlTypeUtil.convertToMyBatisJdbcType(temp[0]));
            list.add(obj);
        }
        return list;

    }


    public TableDefinition getTableInfo(String tableName) {
        JSONObject rowMap = new JSONObject(mapper.getTableInfo(tableName));
        if(rowMap.isEmpty()){
            throw new ServiceException("表{}不存在", tableName);
        }
        TableDefinition tableDefinition = new TableDefinition();
        tableDefinition.setTableName(rowMap.getStr("TABLE_NAME"));
        tableDefinition.setDesc(rowMap.getStr("TABLE_DESC"));
        return tableDefinition;
    }

    public List<TableIndex> getIdxInfo(String tableName) {
        return mapper.getIdxInfo(tableName);
    }


}
