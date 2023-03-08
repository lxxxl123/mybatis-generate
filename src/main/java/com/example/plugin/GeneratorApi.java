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

import java.util.List;

/**
 * @author chenwh3
 */

@Mojo(name = "generator")
public class GeneratorApi extends Generator {


    public void execute() throws MojoExecutionException {
        //得到配置文件对象 将指定输出路径与读取资源文件路径
        buildConfig("gen-api.yaml");
        List<JSONObject> actions = context.getActions();
        for (JSONObject action : actions) {
            getAction(action).start();
        }
    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorApi generatorMojo = new GeneratorApi();
        generatorMojo.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorMojo.execute();
    }


}
