package com.example.plugin;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

/**
 * @author chenwh3
 */
@Slf4j
public class GeneratorFrontEnd extends Generator {

    public void execute() {
        buildConfig("gen-front.yaml");
        // 获取数据库信息
        List<JSONObject> actions = context.getActions();
        for (JSONObject action : actions) {
            getAction(action).start();
        }
    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorFrontEnd generatorVue = new GeneratorFrontEnd();
        generatorVue.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorVue.execute();

    }


}
