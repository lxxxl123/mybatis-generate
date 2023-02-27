package com.example.core.helper;

import com.example.core.anno.SqlHelper;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.TableDefinition;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
import com.xiaoleilu.hutool.json.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
@Data
@SqlHelper("mysql")
public class MysqlHelper extends AbstractDbHelper {

    @Override
    String descColumnSql() {
        return String.format("desc %s", context.getTargetTable());
    }

    @Override
    String descTableSql() {
        return null;
    }

    @Override
    protected ColumnDefinition buildColumnDefinition(JSONObject rowMap) {
        //DBUtils 取的MapList中的Map  是不区分key大小写的  所以不用转换
        ColumnDefinition columnDefinition = new ColumnDefinition();

        boolean isIdentity = "auto_increment".equalsIgnoreCase((String) rowMap.get("EXTRA"));
        boolean isPk = "PRI".equalsIgnoreCase((String) rowMap.get("KEY"));
        String type = buildType((String) rowMap.get("TYPE"));
        String columnName = (String) rowMap.get("FIELD");

        columnDefinition.setColumnName(columnName);
        columnDefinition.setIdentity(isIdentity);
        columnDefinition.setPk(isPk);
        columnDefinition.setType(type);
        columnDefinition.setJavaType(SqlTypeUtil.convertToJavaBoxType(type));
        columnDefinition.setJavaFieldName(StringUtil.underlineToCamelhump(columnName));
        columnDefinition.setJdbcType(SqlTypeUtil.convertToMyBatisJdbcType(type));
        
        return columnDefinition;
    }

    @Override
    TableDefinition buildTableDefinition(JSONObject rowMap) {
        return null;
    }

    private String buildType(String type) {
        if (StringUtils.isNotEmpty(type)) {
            int index = type.indexOf("(");
            if (index > 0) {
                return type.substring(0, index).toUpperCase();
            }
            return type;
        }
        return "varchar";
    }

}
