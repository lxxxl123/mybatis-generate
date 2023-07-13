package com.example.plugin;

import cn.hutool.json.JSONObject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

/**
 *
 * @author chenwh3
 */

@Mojo(name = "generator")
public class GeneratorBackEnd extends Generator {


    public void execute() throws MojoExecutionException {
        //得到配置文件对象 将指定输出路径与读取资源文件路径
        buildConfig("gen-backend.yaml");
        List<JSONObject> actions = context.getActions();
        for (JSONObject action : actions) {
            getAction(action).start();
        }
    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorBackEnd generatorMojo = new GeneratorBackEnd();
        generatorMojo.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorMojo.execute();

    }


}
