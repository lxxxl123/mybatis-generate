package com.example.core.helper;

import com.example.core.anno.SqlHelper;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.TableDefinition;
import com.example.core.util.SqlTypeUtil;
import com.example.core.util.StringUtil;
import com.xiaoleilu.hutool.json.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
@Data
public class MysqlHelper extends AbstractDbHelper {

    @Override
    public List<ColumnDefinition> getColumnsInfo() {
        return null;
    }

    @Override
    public TableDefinition getTableInfo() {
        return null;
    }
}
