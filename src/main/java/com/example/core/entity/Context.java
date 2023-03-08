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

    private List<JSONObject> actions;
    public static Context of(String sourcePath, String fileName) {
        return YamlUtils.load(sourcePath + fileName, Context.class);
    }

    public static void main(String[] args) {
    }

}
