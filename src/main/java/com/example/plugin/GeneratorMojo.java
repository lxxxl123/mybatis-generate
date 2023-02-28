package com.example.plugin;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.ConfigContext;
import com.example.core.service.BaseDataService;
import com.example.core.service.Callback;
import com.example.core.util.FileUtil;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import com.example.factory.VelocityFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.Map;

/**
 *
 */

@Mojo(name = "generator")
public class GeneratorMojo extends AbstractMojo {

    @Parameter(property = "project.basedir", required = true, readonly = true)
    private File basedir;

    @Parameter(readonly = false, defaultValue = "")
    private String vmPath = "";

    private ServiceFactory serviceFactory;


    private String getResource() {
        return String.format("%s/src/main/resources/", basedir.getAbsolutePath());
    }

    private String getOutputPath() {
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
            ConfigContext configContext = new ConfigContext(vmPath, getOutputPath());

            System.out.printf("入参 basedir = %s , configDir = %s\n", basedir, vmPath);

            serviceFactory = new ServiceFactory(configContext);

            JSONObject metaMap = buildMetaData(configContext);

            String src = configContext.getOutputPath();
            String qms = configContext.getTargetPackage();
            String service = configContext.getTargetService();
            String entity = configContext.getTargetEntity();
            String controller = configContext.getTargetController();
            String prefix = configContext.getPrefix();
            String impl = configContext.getTargetServiceImpl();
            String dao = configContext.getTargetDao();
            String target = configContext.getTargetName();
            String mapperXmlPath = configContext.getMapperXmlPath();

            doGenerator(configContext, metaMap, (configContext1, context) -> {
                FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}.java", src, qms, entity, prefix, target),
                        VelocityUtil.render("entity.vm", context));

                FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Service.java", src, qms, service, prefix, target),
                        VelocityUtil.render("contract.vm", context));

                FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Mapper.java", src, qms, dao, prefix, target),
                        VelocityUtil.render("mapper.vm", context));

                FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}/{}ServiceImpl.java", src, qms, service, prefix, impl, target),
                        VelocityUtil.render("service.vm", context));

                FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Controller.java", src, qms, controller, prefix, target),
                        VelocityUtil.render("controller.vm", context));

                FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}Mapper.xml", getResource(), mapperXmlPath, prefix, target),
                        VelocityUtil.render("mapperXml.vm", context));
            });
        } catch (Exception e) {
            throw new MojoExecutionException("unable to generator codes of table.", e);
        }

    }

    public static void doGenerator(ConfigContext configContext, JSONObject metaMap, Callback callback) {
        //配置velocity的资源加载路径
        VelocityFactory.init(configContext.getVmPath());

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
        context.put("prefix", configContext.getPrefix());
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
        generatorMojo.vmPath = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm";
        generatorMojo.execute();
    }


}
