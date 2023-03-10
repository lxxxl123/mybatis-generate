package com.example.core.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.action.inf.Action;
import com.example.core.entity.front.Col;
import com.example.core.entity.table.ColumnDefinition;
import com.example.core.entity.table.LeftJoinCol;
import com.example.core.thread.Ctx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.list.SetUniqueList;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenwh3
 */
@Slf4j
public class SetViewMetaDataAction extends Action {

    private List<ColumnDefinition> columns ;

    private List<LeftJoinCol> leftJoinCols;

    private List<Col> buildMainList(List<ColumnDefinition> columns){
        Set<String> sets = CollUtil.newHashSet("createTime","updateTime","creator","modifier");
        List<Col> list = columns.stream().map(e -> {
            Col col = new Col();
            col.setIsPk(e.isPk());
            col.setCol(e.getJavaFieldName());
            col.setField(e.getJavaFieldName());
            col.setTitle(e.getRemark().split(";")[0].trim());
            if (sets.contains(col.getField())) {
                col.setAutoBuild(true);
            }
            String type = e.getType();
            if (type != null) {
                col.setIsTime(type.startsWith("date"));
                col.setType(type);
            }
            col.setEnumMap(e.getEnumMap());
            col.setEnumList(e.getEnumList());

            if (col.getTitle().endsWith("人")) {
                col.setCusMsg(StrUtil.format("type: 'vxe-emp-pulldown', propMap: [ { key: '{}', val: 'empName' } ]", col.getCol()));
            }
            else if (CollUtil.newHashSet("account").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-emp-pulldown', propMap: [ { key: '{}', val: 'oneAccount' } ]", col.getCol()));
            }
            else if (CollUtil.newHashSet("matnr").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-matnr-pulldown', propMap: [ { key: '{}', val: 'matnr' } ]", col.getCol()));
            }
            else if (CollUtil.newHashSet("vtcode").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-vtcode-pulldown', propMap: [ { key: '{}', val: 'cno' } ]", col.getCol()));
            }
            else if (CollUtil.newHashSet("supplierCode").contains(col.getField())) {
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
        return list;
    }

    @Override
    protected void run() {
        JSONObject data = Ctx.getAll();

        List<Col> list = buildMainList(columns);
        if (leftJoinCols != null) {
            list.addAll(buildMainList(BeanUtil.copyToList(leftJoinCols, ColumnDefinition.class)));
        }

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
