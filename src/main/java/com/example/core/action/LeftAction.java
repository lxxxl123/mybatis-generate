package com.example.core.action;

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
            LeftJoinTable value = entry.getValue();
            List<LeftJoinCol> cols = value.getCols();
            leftJoinTables.add(value);
            leftJoinCols.addAll(cols);
        }
        Ctx.put("leftJoinTables", leftJoinTables);
        Ctx.put("leftJoinCols", leftJoinCols);

    }

}
