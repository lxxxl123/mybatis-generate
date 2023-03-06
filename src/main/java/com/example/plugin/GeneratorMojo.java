package com.example.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.Context;
import com.example.core.entity.table.TableIndex;
import com.example.core.service.BaseDataService;
import com.example.core.util.FileUtil;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 *
 */

@Mojo(name = "generator")
public class GeneratorMojo extends AbstractMojo {

    @Parameter(readonly = false, defaultValue = "")
    private String configDir = "";

    private ServiceFactory serviceFactory;

    private Context context;

    public void buildMetaData() {
        //初始化DB工具类
        BaseDataService baseDataService = serviceFactory.getService(BaseDataService.class);

        JSONObject base = context.getBase();
        JSONObject data = context.getData();
        String targetTable = base.getStr("targetTable");

        //元数据处理
        data.put("columns", baseDataService.getColumnsInfo(targetTable));

        data.put("tableInfo", baseDataService.getTableInfo(targetTable));

        data.put("indexs", baseDataService.getIdxInfo(targetTable));
    }

    private void buildConfig() {
        context = Context.of(configDir,"gen-backend.yaml");
        serviceFactory = new ServiceFactory(context.getBase());
        VelocityUtil.init(configDir);
    }


    public void execute() throws MojoExecutionException {
        try {
            //得到配置文件对象 将指定输出路径与读取资源文件路径
            buildConfig();

            buildMetaData();

            VelocityUtil.buildPage("backIndexTip",context);

            JSONObject base = context.getBase();
            JSONObject data = context.getData();

            String codePath = base.getStr("backEndPath") + base.getStr("codePath");
            String resourcePath = base.getStr("backEndPath") + base.getStr("resourcePath");
            String qms = base.getStr("package").replace(".", "/");
            String service = base.getStr("service");
            String entity = base.getStr("entity");
            String controller = base.getStr("controller");
            String prefix = base.getStr("prefix");
            String impl = base.getStr("serviceImpl");
            String dao = base.getStr("dao");
            String objName = StrUtil.upperFirst(base.getStr("targetName"));

            data.put("table", data.getStr("targetTable"));
            data.put("name", objName);

            String mapperXmlPath = base.getStr("mapperXmlPath");

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}.java", codePath, qms, entity, prefix, objName),
                    VelocityUtil.render("entity.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Service.java", codePath, qms, service, prefix, objName),
                    VelocityUtil.render("contract.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Mapper.java", codePath, qms, dao, prefix, objName),
                    VelocityUtil.render("mapper.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}/{}ServiceImpl.java", codePath, qms, service, prefix, impl, objName),
                    VelocityUtil.render("service.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Controller.java", codePath, qms, controller, prefix, objName),
                    VelocityUtil.render("controller.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}Mapper.xml", resourcePath, mapperXmlPath, prefix, objName),
                    VelocityUtil.render("mapperXml.vm", data));

        } catch (Exception e) {
            throw new MojoExecutionException("unable to generator codes of table.", e);
        }

    }
    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorMojo generatorMojo = new GeneratorMojo();
        generatorMojo.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorMojo.execute();
    }


}
