package com.example.core.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.action.inf.Action;
import com.example.core.entity.front.Col;
import com.example.core.entity.table.ColumnDefinition;
import com.example.core.thread.Ctx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SetViewMetaDataAction extends Action {

    private List<ColumnDefinition> columns ;

    @Override
    protected void run() {
        Set<String> sets = SetUtils.hashSet("createTime","updateTime","creator","modifier");
        JSONObject data = Ctx.getAll();
        List<Col> list = columns.stream().map(e -> {
            Col col = new Col();
            col.setIsPk(e.isPk());
            col.setCol(e.getColumnName());
            col.setField(e.getColumnName());
            col.setTitle(e.getRemark().split(";")[0]);
            if (sets.contains(col.getField())) {
                col.setAutoBuild(true);
            }
            String type = e.getType();
            col.setIsTime(type.startsWith("date"));
            col.setType(type);
            col.setEnumMap(e.getEnumMap());
            col.setEnumList(e.getEnumList());

            if (col.getTitle().endsWith("人")) {
                col.setCusMsg(StrUtil.format("type: 'vxe-emp-pulldown', propMap: [ { key: '{}', val: 'empName' } ]", col.getCol()));
            }
            else if (SetUtils.hashSet("matnr").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-matnr-pulldown', propMap: [ { key: '{}', val: 'matnr' } ]", col.getCol()));
            }
            else if (SetUtils.hashSet("vtcode").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-vtcode-pulldown', propMap: [ { key: '{}', val: 'cno' } ]", col.getCol()));
            }

            else if (SetUtils.hashSet("supplierCode").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-supplier-pulldown', propMap: [ { key: '{}', val: 'supplierCode' } ]", col.getCol()));
            }


            if (StrUtil.containsAnyIgnoreCase(col.getField(), "isDel")) {
                col.setIsDel(true);
            }
            /**
             * 枚举值更改field
             */
            if (col.getEnumMap().size() > 0) {
                col.setField(col.getCol() + "Str");
            }

            return col;
        }).collect(Collectors.toList());


        data.put("showCols", list.stream()
                .filter(e->!e.getIsPk())
                .collect(Collectors.toList()));

        data.put("searchCols", list.stream()
                .filter(e->!e.getIsPk())
                .collect(Collectors.toList()));

        data.put("addCols", list.stream()
                .filter(e->!e.getIsPk())
                .filter(e->!e.getAutoBuild())
                .filter(e->!e.getIsDel())
                .collect(Collectors.toList()));

        data.put("updateCols", list.stream()
                .filter(e->!e.getIsPk())
                .filter(e->!e.getAutoBuild())
                .filter(e->!e.getIsDel())
                .collect(Collectors.toList()));

        data.put("pkCol", list.stream()
                .filter(e -> e.getIsPk())
                .findFirst().orElse(null));

        data.put("delCol", list.stream()
                .filter(e -> e.getIsDel())
                .findFirst().orElse(null));
    }
}
