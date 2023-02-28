package com.example.plugin;

import cn.hutool.json.JSONObject;
import com.example.core.entity.FrontContext;
import com.example.core.service.BaseDataService;
import com.example.factory.ServiceFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * @author chenwh3
 */
public class GeneratorVue extends AbstractMojo {

    @Parameter(property = "project.backDir", required = true, readonly = true)
    private File backDir;

    private File frontDir;

    private String configDir = "";

    private String outDir = "";

    private String getResource(){
        return String.format("%s/src/main/resources/", "");
    }


    private FrontContext context ;

    private ServiceFactory serviceFactory ;

    private void buildConfig() {
        context = FrontContext.of(configDir);
        serviceFactory = new ServiceFactory(context);
    }

    /**
     * 获取数据库信息
     */
    public void buildMetaData() {
        //初始化DB工具类
        BaseDataService dbHelper = serviceFactory.getService(BaseDataService.class);

        //元数据处理
        context.put("columns", dbHelper.getColumnsInfo(context.getStr("tableName")));

        context.put("tableInfo", dbHelper.getTableInfo(context.getStr("tableName")));
    }

    public void execute(){
        buildConfig();
        // 获取数据库信息
        buildMetaData();
        // 生成前端菜单
        buildMenus();
    }

    private void buildMenus() {


    }


    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorVue generatorVue = new GeneratorVue();
        generatorVue.frontDir = new File("D:\\20221014\\qms-front");
        generatorVue.execute();
    }


}
