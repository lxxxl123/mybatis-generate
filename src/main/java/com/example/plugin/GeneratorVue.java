package com.example.plugin;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.example.core.entity.FrontContext;
import com.example.core.service.BaseDataService;
import com.example.core.service.MenusService;
import com.example.factory.ServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * @author chenwh3
 */
@Slf4j
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
        context.put("columns", dbHelper.getColumnsInfo(context.getStr("targetTable")));


    }

    public void execute(){
        buildConfig();
        // 获取数据库信息
        buildMetaData();
        // 生成前端菜单
        buildMenus();
        
        buildVue();
        
    }

    private void buildVue() {
        buildRoute();
        buildApi();
        buildView();

    }

    private void buildView() {
    }

    private void buildApi() {
    }

    private void buildRoute() {
        String routePath = StrUtil.removeSuffix(context.getStr("routePath"), "/");
        String prefix = context.getStr("prefix");
        String menusChPath = context.getStr("menusChPath");
        String[] split = menusChPath.split("-");
        context.put("pathChName", ArrayUtil.get(split, -1));
        String modulesFilePath = StrUtil.format("{}/modules/{}.js", routePath, prefix);
        String routeFilePath = StrUtil.format("{}/routes.js", routePath);



    }

    private void buildMenus() {
        String prefix = context.getStr("prefix");
        String targetName = context.getStr("targetName");
        String menusChPath = context.getStr("menusChPath");
        MenusService service = serviceFactory.getService(MenusService.class);
        service.buildPermission(prefix, targetName, menusChPath);


    }


    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorVue generatorVue = new GeneratorVue();
        generatorVue.frontDir = new File("D:\\20221014\\qms-front");
        generatorVue.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorVue.execute();

    }


}
