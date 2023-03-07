package com.example.plugin;

import cn.hutool.json.JSONObject;
import com.example.core.entity.Context;
import com.example.core.service.BaseDataService;
import com.example.core.thread.Ctx;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class Generator extends AbstractMojo {

    @Parameter(readonly = false, defaultValue = "")
    protected String configDir = "";

    protected ServiceFactory serviceFactory;

    protected Context context;


    public void buildMetaData() {
        //初始化DB工具类
        BaseDataService baseDataService = serviceFactory.getService(BaseDataService.class);

        JSONObject base = context.getBase();
        JSONObject data = context.getData();

        Ctx.init(data);

        String targetTable = base.getStr("targetTable");

        //数据前缀
        data.putIfAbsent("t","t");

        //元数据处理
        data.put("columns", baseDataService.getColumnsInfo(targetTable));

        data.put("tableInfo", baseDataService.getTableInfo(targetTable));

        data.put("indexs", baseDataService.getIdxInfo(targetTable));
    }

    protected void buildConfig(String fileName) {
        context = Context.of(configDir,fileName);
        serviceFactory = new ServiceFactory(context.getBase());
        VelocityUtil.init(configDir);

    }

    @Override
    protected void finalize() throws Throwable {
        Ctx.clear();
    }
}
