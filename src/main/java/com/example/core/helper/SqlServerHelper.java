package com.example.core.helper;

import com.example.core.anno.SqlHelper;
import com.example.core.entity.ColumnDefinition;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
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
    String descSql() {
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
    protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap){
        //DBUtils 取的MapList中的Map  是不区分key大小写的  所以不用转换
        ColumnDefinition columnDefinition = new ColumnDefinition();

        String columnName = (String)rowMap.get("COLUMN_NAME");
        String type = (String)rowMap.get("DATA_TYPE");
        Integer order = (Integer)rowMap.get("ORDINAL_POSITION");
        String remark = (String) rowMap.get("REMARK");
        String[] temp = type.split("\\s+");

        columnDefinition.setPk(Objects.equals(order, 1));
        columnDefinition.setColumnName(columnName);
        columnDefinition.setIdentity(temp.length > 1);
        columnDefinition.setType(temp[0]);
        columnDefinition.setRemark(remark);
        columnDefinition.setJavaType(SqlTypeUtil.convertToJavaBoxType(temp[0]));
        columnDefinition.setJavaFieldName(StringUtil.underlineToCamelhump(columnName));
        columnDefinition.setJdbcType(SqlTypeUtil.convertToMyBatisJdbcType(temp[0]));

        return columnDefinition;
    }

}
