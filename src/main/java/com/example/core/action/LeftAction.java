package com.example.core.action;

import cn.hutool.core.util.StrUtil;
import com.example.config.LeftJoinConfig;
import com.example.core.action.inf.Action;
import com.example.core.entity.table.ColumnDefinition;
import com.example.core.entity.table.LeftJoinCol;
import com.example.core.entity.table.LeftJoinTable;
import com.example.core.thread.Ctx;
import com.example.factory.YamlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LeftAction extends Action {

    private List<ColumnDefinition> columns;

    private Map<String, LeftJoinTable> tables;

    @Override
    public void init() {
        tables = LeftJoinConfig.init().getTables();
    }

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

            });
            leftJoinCols.addAll(cols);
        }
        Ctx.put("leftJoinTables", leftJoinTables);
        Ctx.put("leftJoinCols", leftJoinCols);

    }

}
