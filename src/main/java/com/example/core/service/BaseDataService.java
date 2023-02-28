package com.example.core.service;

import cn.hutool.json.JSONObject;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.TableDefinition;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
import com.example.mapper.BaseMapper;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            String[] temp = type.split("\\s+");


            columnDefinition.setPk(Objects.equals(order, 1));
            columnDefinition.setColumnName(columnName);

            String selectSql = columnName;
            if (type.startsWith("date")) {
                selectSql = String.format("CONVERT(varchar(19), %s, 120) as %s", columnName, columnName);
            } else if ("isDel".equals(columnName)) {
                selectSql = "case when isDel=1 then N'是' else N'否' end as isDelStr ,\n\t\tisDel";
            }
            columnDefinition.setSelectSql(selectSql);

            columnDefinition.setIdentity(temp.length > 1);
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
