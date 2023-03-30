package com.example.core.action;

import cn.hutool.core.collection.ListUtil;
import com.example.core.action.inf.Action;
import com.example.core.service.BaseDataService;
import com.example.core.thread.Ctx;

/**
 * @author chenwh3
 */
public class DataBaseAction extends Action {

    private BaseDataService baseDataService;

    private String tableName;


    @Override
    protected void run() {
        Ctx.putIfAbsent("t", "t");

        //元数据处理
        Ctx.put("columns", baseDataService.getColumnsInfo(tableName));

        Ctx.put("tableInfo", baseDataService.getTableInfo(tableName));

        Ctx.put("indexs", baseDataService.getIdxInfo(tableName));

        Ctx.put("insertList", ListUtil.of("creator","create_time","createTime"));
        Ctx.put("updateList", ListUtil.of("modifier","update_time","updateTime","mdyUser","mdyDate"));
    }


}
