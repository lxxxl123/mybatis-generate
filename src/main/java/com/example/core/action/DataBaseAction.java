package com.example.core.action;

import com.example.core.action.inf.Action;
import com.example.core.service.BaseDataService;
import com.example.core.thread.Ctx;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    }

    public static void main(String[] args) {

        List<String> list = new ArrayList<String>() {};
        Type actualType = getActualType(list, 0);
        System.out.println(123);
    }

    public static Type getActualType(Object o,int index) {
        Type clazz = o.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)clazz;
        return pt.getActualTypeArguments()[index];
    }

}
