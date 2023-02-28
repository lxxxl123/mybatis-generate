package com.example.plugin;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import com.example.core.entity.ConfigContext;
import com.example.core.service.BaseDataService;
import com.example.core.service.Callback;
import com.example.core.util.FileUtil;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 */

@Mojo(name = "generator")
public class GeneratorMojo extends AbstractMojo {

    @Parameter(property = "project.basedir", required = true, readonly = true)
    private File basedir;

    @Parameter(readonly = false, defaultValue = "")
    private String configDir = "";

    private ServiceFactory serviceFactory ;


    private String getResource(){
        return String.format("%s/src/main/resources/", basedir.getAbsolutePath());
    }

    private String getOutputPath(){
        return String.format("%s/src/main/java/", basedir.getAbsolutePath());
    }

    public JSONObject buildMetaData(ConfigContext configContext) {
        JSONObject map = new JSONObject();
        //初始化DB工具类
        BaseDataService baseDataService = serviceFactory.getService(BaseDataService.class);

        //元数据处理
        map.put("columns", baseDataService.getColumnsInfo(configContext.getTargetTable()));

        map.put("tableInfo", baseDataService.getTableInfo(configContext.getTargetTable()));

        return map;
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            //得到配置文件对象 将指定输出路径与读取资源文件路径
            ConfigContext configContext = new ConfigContext(configDir, getOutputPath());

            System.out.printf("入参 basedir = %s , configDir = %s\n", basedir, configDir);

            serviceFactory = new ServiceFactory(configContext);

            JSONObject metaMap = buildMetaData(configContext);

            String rootPath = configContext.getOutputPath() + configContext.getTargetPackage() + "/";

            String serviceImplPath = configContext.getOutputPath() + configContext.getTargetPackage() + "/" + configContext.getTargetServiceImpl();
            doGenerator(configContext, metaMap, (configContext1, context) -> {
                FileUtil.writeFile(rootPath + configContext1.getTargetEntity(),                   //输出目录
                        String.format("%s.java", configContext1.getTargetName()),    //文件名
                        VelocityUtil.render("entity.vm", context));                 //模板生成内容

                FileUtil.writeFile(rootPath + configContext1.getTargetService(),
                        String.format("%sService.java", configContext1.getTargetName()),
                        VelocityUtil.render("contract.vm", context));

                FileUtil.writeFile(rootPath + configContext1.getTargetDao(),
                        String.format("%sMapper.java", configContext1.getTargetName()),
                        VelocityUtil.render("mapper.vm", context));

                FileUtil.writeFile(serviceImplPath,
                        String.format("%sServiceImpl.java", configContext1.getTargetName()),
                        VelocityUtil.render("service.vm", context));

                FileUtil.writeFile(rootPath + configContext1.getTargetController(),
                        String.format("%sController.java", configContext1.getTargetName()),
                        VelocityUtil.render("controller.vm", context));

                FileUtil.writeFile(getResource() + configContext1.getMapperXmlPath(),
                        String.format("%sMapper.xml", configContext1.getTargetName()),
                        VelocityUtil.render("mapperXml.vm", context));
            });
        } catch (Exception e){
            throw new MojoExecutionException("unable to generator codes of table.",e);
        }

    }

    public static void doGenerator(ConfigContext configContext, JSONObject metaMap, Callback callback) {
        //配置velocity的资源加载路径
        Properties velocityPros = new Properties();
        velocityPros.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, configContext.getSourcePath());
        velocityPros.setProperty("input.encoding", "utf-8");
        velocityPros.setProperty("output.encoding", "utf-8");
        Velocity.init(velocityPros);


        //封装velocity数据
        VelocityContext context = new VelocityContext();
        Map<String, String> other = configContext.getOther();
        for (Map.Entry<String, String> entry : other.entrySet()) {
            if (entry.getKey().startsWith("is")) {
                context.put(entry.getKey(), Convert.toBool(entry.getValue()));
            } else {
                context.put(entry.getKey(), entry.getValue());
            }
        }

        metaMap.forEach(context::put);

        context.put("table", configContext.getTargetTable());
        context.put("name", configContext.getTargetName());
        context.put("package", configContext.getTargetPackage());
        context.put("entity", configContext.getTargetEntity());
        context.put("service", configContext.getTargetService());
        context.put("serviceImpl", configContext.getTargetServiceImpl());
        context.put("controller", configContext.getTargetController());
        context.put("dao", configContext.getTargetDao());

        callback.write(configContext, context);

    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorMojo generatorMojo = new GeneratorMojo();
        generatorMojo.basedir = new File("D:\\20221014\\qms-platform\\qms-service");
//        generatorMojo.basedir = new File("D:\\20221014\\idea-workspace\\qmsapicenter\\qms-service");
        generatorMojo.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm";
        generatorMojo.execute();
    }


}
