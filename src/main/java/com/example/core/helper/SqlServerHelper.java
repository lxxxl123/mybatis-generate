package com.example.core.helper;

import com.example.core.anno.SqlHelper;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.TableDefinition;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
import com.xiaoleilu.hutool.json.JSONObject;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
@Data
@SqlHelper("sqlserver")
public class SqlServerHelper extends AbstractDbHelper {

    @Override
    String descColumnSql() {
        String tableName = context.getTargetTable();
        return String.format("SELECT TABLE_NAME, c.COLUMN_NAME, c.DATA_TYPE, t.REMARK , ORDINAL_POSITION\n" +
                "FROM [INFORMATION_SCHEMA].[COLUMNS] c\n" +
                "left join (SELECT\n" +
                "       B.name  AS COLUMN_NAME,\n" +
                "       C.value AS REMARK\n" +
                "FROM sys.tables A\n" +
                "         INNER JOIN sys.columns B ON B.object_id = A.object_id\n" +
                "         LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id\n" +
                "WHERE A.name = '%s') t on t.COLUMN_NAME = c.COLUMN_NAME\n" +
                "WHERE TABLE_NAME = '%s'\n", tableName, tableName);
    }


    @Override
    String descTableSql() {
        String tableName = context.getTargetTable();
        return String.format("select a.name  AS TABLE_NAME,\n" +
                "       g.value AS TABLE_DESC\n" +
                "from sys.tables a\n" +
                "         left join sys.extended_properties g on a.object_id = g.major_id AND g.minor_id = 0\n" +
                "where a.name = '%s'", tableName);
    }


    @Override
    protected ColumnDefinition buildColumnDefinition(JSONObject rowMap){
        //DBUtils 取的MapList中的Map  是不区分key大小写的  所以不用转换
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

        return columnDefinition;
    }

    @Override
    TableDefinition buildTableDefinition(JSONObject rowMap) {
        TableDefinition tableDefinition = new TableDefinition();
        tableDefinition.setTableName(rowMap.getStr("TABLE_NAME"));
        tableDefinition.setDesc(rowMap.getStr("TABLE_DESC"));
        return tableDefinition;
    }

}
