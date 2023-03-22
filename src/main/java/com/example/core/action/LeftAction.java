package com.example.core.action;

import cn.hutool.core.util.StrUtil;
import com.example.core.action.inf.LoadConfigAction;
import com.example.core.entity.table.ColumnDefinition;
import com.example.core.entity.table.LeftJoinCol;
import com.example.core.entity.table.LeftJoinTable;
import com.example.core.thread.Ctx;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenwh3
 */
public class LeftAction extends LoadConfigAction {

    {
        config = "config/leftJoinConfig.yaml";
    }

    private List<ColumnDefinition> columns;

    @Setter
    @Getter
    private Map<String, LeftJoinTable> tables;


    @Override
    protected void run() {
        Set<String> colNames = columns.stream().map(ColumnDefinition::getColumnName).collect(Collectors.toSet());
        List<LeftJoinTable> leftJoinTables = new ArrayList<>();
        List<LeftJoinCol> leftJoinCols = new ArrayList<>();
        for (Map.Entry<String, LeftJoinTable> entry : tables.entrySet()) {
            String key = entry.getKey();
            if (!colNames.contains(key)) {
                continue;
            }
            LeftJoinTable table = entry.getValue();
            List<LeftJoinCol> cols = table.getCols();
            leftJoinTables.add(table);
            String prefix = table.getPrefix();
            cols.forEach(e->{
                String columnName = e.getColumnName();
                String javaFieldName = e.getJavaFieldName();
                String selectSql = e.getSelectSql();
                if (javaFieldName == null) {
                    javaFieldName = columnName;
                }
                if (selectSql == null) {
                    if (!javaFieldName.equals(columnName)) {
                        selectSql = StrUtil.format("{}.{} as {}", prefix, columnName, javaFieldName);
                    } else {
                        selectSql = StrUtil.format("{}.{}", prefix, columnName);
                    }
                }
                e.setJavaFieldName(javaFieldName);
                e.setSelectSql(selectSql);
                e.setPrefix(table.getPrefix());

            });
            leftJoinCols.addAll(cols);
        }
        Ctx.put("leftJoinTables", leftJoinTables);
        Ctx.put("leftJoinCols", leftJoinCols);

    }

}
