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
import com.example.core.entity.table.TableIndex;
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

    private List<TableIndex>  indexs;

    private List<Col> buildMainList(List<ColumnDefinition> columns){
        Set<String> sets = CollUtil.newHashSet("createTime","updateTime","creator","modifier");
        List<String> uindex = indexs.stream().map(TableIndex::getIdxName).filter(e -> StrUtil.endWith(e, "uindex")).collect(Collectors.toList());
        List<Col> list = columns.stream()
                .filter(e -> StrUtil.isNotBlank(e.getRemark()) && !e.getRemark().startsWith("#"))
                .map(e -> {
                    Col col = new Col();
                    col.setIsPk(e.isPk());
                    col.setCol(e.getJavaFieldName());
                    col.setField(e.getJavaFieldName());
                    col.setTitle(e.getTitle());

                    uindex.stream().filter(e1 -> e1.contains(e.getColumnName())).findFirst().ifPresent(e1 -> {
                        col.setIsUnique(true);
                    });

                    if (sets.contains(col.getField())) {
                        col.setAutoBuild(true);
                    }
                    String type = e.getType();
                    if (type != null) {
                        col.setIsTime(type.startsWith("date"));
                        col.setType(type);
                    }

                    if (e.getPrefix() != null) {
                        col.setFullFieldName(StrUtil.format("{}.{}", e.getPrefix(), e.getColumnName()));
                    }
                    col.setEnumMap(e.getEnumMap());
                    col.setEnumList(e.getEnumList());

                    if (col.getTitle().endsWith("人")) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-emp-pulldown', propMap: [ { key: '{}', val: 'empName' } ]", col.getCol()));
                    } else if (CollUtil.newHashSet("account").contains(col.getField())) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-emp-pulldown', propMap: [ { key: '{}', val: 'oneAccount' } ]", col.getCol()));
                    } else if (CollUtil.newHashSet("matnr").contains(col.getField())) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-mara-pulldown', propMap: [ { key: '{}', val: 'matCode' },{ key: '-' ,val: 'matName'} ]", col.getCol()));
                    } else if (CollUtil.newHashSet("vtcode").contains(col.getField())) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-vtcode-pulldown', propMap: [ { key: '{}', val: 'cno' } ]", col.getCol()));
                    } else if (CollUtil.newHashSet("supplierCode", "supplierNo").contains(col.getField())) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-supplier-pulldown', propMap: [ { key: '{}', val: 'supplierCode' } ]", col.getCol()));
                    } else if (CollUtil.newHashSet("producerCode").contains(col.getField())) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-oriPak-producer-pulldown', propMap: [ { key: '{}', val: 'cno' } ]", col.getCol()));
                    } else if (CollUtil.newHashSet("factoryMaraGroup").contains(col.getField())) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-factory-mara-gp-pulldown', propMap: [ { key: '{}', val: 'code' }, { key: '-', val: 'descr' } ]", col.getCol()));
                    } else if (col.getField().endsWith("kostl")) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-kostl-pulldown', propMap: [ { key: '{}', val: 'kostl' } ]", col.getCol()));
                    } else if (col.getField().endsWith("resourceCode")) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-resource-pulldown', propMap: [ { key: '{}', val: 'resourceCode' }, { key: 'resourceDesc', val: 'resourceDesc' } ]", col.getCol()));
                    } else if (col.getField().endsWith("cinismp")) {
                        col.setCusMsg(StrUtil.format("type: 'vxe-cinismp-pulldown', propMap: [{ key: '{}', val: 'cno' }, { key: 'cinismpDesc', val: 'cnm' }]", col.getCol()));
                    } else if (col.getTitle().endsWith("年份")) {
                        col.setCusMsg("props: { type: 'year' }");
                    }


                    if (StrUtil.containsAnyIgnoreCase(col.getField(), "isDel", "delTag")) {
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
            List<Col> col = buildMainList(BeanUtil.copyToList(leftJoinCols, ColumnDefinition.class));
            col.forEach(e->e.setEditable(false));
            list.addAll(col);
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
                .filter(Col::getIsPk)
                .findFirst().orElse(null));

        data.put("delCol", list.stream()
                .filter(Col::getIsDel)
                .findFirst().orElse(null));
    }
}
