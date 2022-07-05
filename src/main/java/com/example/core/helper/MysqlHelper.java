package com.example.core.helper;

import com.example.core.anno.SqlHelper;
import com.example.core.entity.ColumnDefinition;
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
    String descSql() {
        return String.format("desc %s", context.getTargetTable());
    }

    @Override
    protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap) {
        //DBUtils 取的MapList中的Map  是不区分key大小写的  所以不用转换
        ColumnDefinition columnDefinition = new ColumnDefinition();

        columnDefinition.setColumnName((String) rowMap.get("FIELD"));

        boolean isIdentity = "auto_increment".equalsIgnoreCase((String) rowMap.get("EXTRA"));
        columnDefinition.setIdentity(isIdentity);

        boolean isPk = "PRI".equalsIgnoreCase((String) rowMap.get("KEY"));
        columnDefinition.setPk(isPk);

        String type = buildType((String) rowMap.get("TYPE"));
        columnDefinition.setType(type);

        return columnDefinition;
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
