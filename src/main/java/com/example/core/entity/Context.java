package com.example.core.entity;

import cn.hutool.json.JSONObject;
import com.example.factory.YamlUtils;
import lombok.Data;

import java.util.List;


/**
 * @author chenwh3
 */
@Data
public class Context {

    private JSONObject base;

    private JSONObject database;

    private JSONObject fileBuilder;

    private List<JSONObject> actions;
    private JSONObject data = new JSONObject();

    public static Context of(String sourcePath, String fileName) {
        Context load = YamlUtils.load(sourcePath + fileName, Context.class);
        load.data.putAll(load.base);
        return load;
    }

    public static void main(String[] args) {
        Context load = YamlUtils.load("vm/gen-backend-1.yaml", Context.class);
        System.out.println(load);

    }

}
