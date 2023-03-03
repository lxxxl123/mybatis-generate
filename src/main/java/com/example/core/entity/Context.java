package com.example.core.entity;

import cn.hutool.json.JSONObject;
import com.example.factory.YamlUtils;
import lombok.Data;


/**
 * @author chenwh3
 */
@Data
public class Context {

    private JSONObject base;

    private JSONObject fileBuilder;
    private JSONObject data = new JSONObject();

    public static Context of(String sourcePath, String fileName) {
        Context load = YamlUtils.load(sourcePath + fileName, Context.class);
        load.data.putAll(load.base);
        return load;
    }

    public static void main(String[] args) {
        Context load = YamlUtils.load("vm/gen-front.yaml", Context.class);
        System.out.println(load);

    }

}
