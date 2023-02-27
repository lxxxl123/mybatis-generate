package com.example.plugin;

import com.example.core.entity.FrontContext;
import com.example.core.helper.AbstractDbHelper;
import com.xiaoleilu.hutool.json.JSONObject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
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

    private void buildConfig() {
        this.context = FrontContext.of(configDir);
    }

    /**
     * 获取数据库信息
     */
    public JSONObject buildMetaData() {
        JSONObject map = new JSONObject();
        //初始化DB工具类
        AbstractDbHelper dbHelper =  AbstractDbHelper.of(context);

        //元数据处理
        map.put("columns", dbHelper.getColumnsInfo());

        map.put("tableInfo", dbHelper.getTableInfo());
        return map;
    }

    public void execute(){
        buildConfig();

        JSONObject metaData = buildMetaData();
    }



    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        new GeneratorVue().execute();
    }


}
