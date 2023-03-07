package com.example.plugin;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.Context;
import com.example.core.service.BaseDataService;
import com.example.core.util.FileUtil;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author chenwh3
 */

@Mojo(name = "generator")
public class GeneratorApi extends Generator {


    public void execute() throws MojoExecutionException {
        try {
            //得到配置文件对象 将指定输出路径与读取资源文件路径
            buildConfig("gen-api.yaml");

            buildMetaData();

//            VelocityUtil.buildPage("backIndexTip",context);

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
                    VelocityUtil.render("api/entity.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Service.java", codePath, qms, service, prefix, objName),
                    VelocityUtil.render("api/contract.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Mapper.java", codePath, qms, dao, prefix, objName),
                    VelocityUtil.render("api/mapper.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}/{}ServiceImpl.java", codePath, qms, service, prefix, impl, objName),
                    VelocityUtil.render("api/service.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}/{}Controller.java", codePath, qms, controller, prefix, objName),
                    VelocityUtil.render("api/controller.vm", data));

            FileUtil.writeFile(StrUtil.format("{}/{}/{}/{}Mapper.xml", resourcePath, mapperXmlPath, prefix, objName),
                    VelocityUtil.render("api/mapperXml.vm", data));

        } catch (Exception e) {
            throw new MojoExecutionException("unable to generator codes of table.", e);
        }

    }
    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorApi generatorMojo = new GeneratorApi();
        generatorMojo.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorMojo.execute();
    }


}
