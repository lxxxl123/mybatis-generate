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
        return String.format("sp_columns %s", context.getTargetTable());
    }

    @Override
    protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap){
        //DBUtils 取的MapList中的Map  是不区分key大小写的  所以不用转换
        ColumnDefinition columnDefinition = new ColumnDefinition();

        String columnName = (String)rowMap.get("COLUMN_NAME");
        String type = (String)rowMap.get("TYPE_NAME");
        Integer order = (Integer)rowMap.get("ORDINAL_POSITION");
        String[] temp = type.split("\\s+");

        columnDefinition.setPk(Objects.equals(order, 1));
        columnDefinition.setColumnName(columnName);
        columnDefinition.setIdentity(temp.length > 1);
        columnDefinition.setType(temp[0]);
        columnDefinition.setJavaType(SqlTypeUtil.convertToJavaBoxType(temp[0]));
        columnDefinition.setJavaFieldName(StringUtil.underlineToCamelhump(columnName));
        columnDefinition.setJdbcType(SqlTypeUtil.convertToMyBatisJdbcType(temp[0]));

        return columnDefinition;
    }

}
