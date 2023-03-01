package com.example.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.FrontContext;
import com.example.core.service.BaseDataService;
import com.example.core.service.MenusService;
import com.example.core.util.StringUtil;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.regex.Pattern;

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
        serviceFactory = new ServiceFactory(context.getBase());
        VelocityUtil.init(configDir);
    }

    /**
     * 获取数据库信息
     */
    public void buildMetaData() {
        //初始化DB工具类
        BaseDataService dbHelper = serviceFactory.getService(BaseDataService.class);

        JSONObject data = context.getData();
        //元数据处理
        data.put("columns", dbHelper.getColumnsInfo(context.getBase().getStr("targetTable")));


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
        JSONObject base = context.getBase();
        JSONObject data = context.getData();
        String menusChPath = base.getStr("menusChPath");

        // 生成菜单名
        String[] split = menusChPath.split("-");
        Object menusName = ArrayUtil.get(split, -1);
        data.put("pathChName", menusName);

        buildRoute("routeConfig");
//        buildApi();
//        buildView();

    }

    private void buildView() {
    }

    private void buildApi() {
    }

    private void buildRoute(String buildName) {
        JSONObject base = context.getBase();
        JSONObject data = context.getData();
        JSONObject config = context.getVueBuilder().getJSONObject(buildName);
        String routePath = CollUtil.join(config.getJSONArray("path"),"");
        String replaceRange = config.getStr("replaceRange");
        String condition = config.getStr("condition");
        String vm = base.getStr("vm");
        String replaceVm = config.getStr("replaceVm");


        File file = new File(routePath);
        if (!file.exists()) {
            VelocityUtil.write(routePath, vm, data);
        } else {
            String content = FileUtil.readString(file, "utf-8");
            if (ReUtil.contains(condition, content)) {
                return;
            }
            String part = VelocityUtil.render(replaceVm, data);
            content = StringUtil.merge(content, part, Pattern.compile(replaceRange));
            FileUtil.writeString(content, routePath, "utf-8");
        }
    }

    private void buildMenus() {
        JSONObject base = context.getBase();
        String prefix = base.getStr("prefix");
        String targetName = base.getStr("targetName");
        String menusChPath = base.getStr("menusChPath");
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
