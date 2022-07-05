package com.example.core.run;

import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.ConfigContext;
import com.example.core.helper.AbstractDbHelper;
import com.example.core.service.Callback;
import com.example.core.util.FileUtil;
import com.example.core.util.VelocityUtil;
import com.example.plugin.GeneratorMojo;
import org.apache.velocity.VelocityContext;

import java.sql.SQLException;
import java.util.List;

/**
 * @Description TODO
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
public class GeneratorTest {

    private static String SourcePath = "C:\\Users\\chenwh3\\IdeaProjects\\generator-plugin-test\\src\\main\\resources\\vm\\";

    private static String OutputPath = "C:\\Users\\chenwh3\\IdeaProjects\\qms-platform\\qms-service\\src\\main\\java\\";

    public static void main(String[] args) throws SQLException {

        ConfigContext configContext = new ConfigContext(SourcePath, OutputPath);

        //初始化DB工具类
        AbstractDbHelper dbHelper = AbstractDbHelper.of(configContext);

        List<ColumnDefinition> metaData = dbHelper.getMetaData();

        String rootPath = configContext.getOutputPath() + configContext.getTargetPackage();

        String serviceImplPath = configContext.getOutputPath() + configContext.getTargetPackage() + "/" + configContext.getTargetServiceImpl();
        //生成代码

        System.out.println(metaData);
        GeneratorMojo.doGenerator(configContext, metaData, new Callback() {
            public void write(ConfigContext configContext, VelocityContext context) {
                FileUtil.writeFile(rootPath + configContext.getTargetEntity(),                   //输出目录
                        String.format("%s.java", configContext.getTargetName()),    //文件名
                        VelocityUtil.render("entity.vm", context));                 //模板生成内容

                FileUtil.writeFile(rootPath + configContext.getTargetService(),
                        String.format("%sService.java", configContext.getTargetName()),
                        VelocityUtil.render("contract.vm", context));

                FileUtil.writeFile(rootPath + configContext.getTargetDao(),
                        String.format("%sMapper.java", configContext.getTargetName()),
                        VelocityUtil.render("dao.vm", context));

                FileUtil.writeFile(serviceImplPath,
                        String.format("%sServiceImpl.java", configContext.getTargetName()),
                        VelocityUtil.render("service.vm", context));

                FileUtil.writeFile(rootPath + configContext.getTargetController(),
                        String.format("%sController.java", configContext.getTargetName()),
                        VelocityUtil.render("controller.vm", context));

                FileUtil.writeFile(SourcePath + configContext.getMapperXmlPath(),
                        String.format("%sMapper.xml", configContext.getTargetName()),
                        VelocityUtil.render("mapper.vm", context));
            }
        });

    }

}
