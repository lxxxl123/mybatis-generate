package com.example.core.thread;

import cn.hutool.json.JSONObject;

/**
 * @author chenwh3
 */
public class Ctx {

    private static final ThreadLocal<JSONObject> ctx = new ThreadLocal<>();

    public static void set(String key, Object obj){
        ctx.get().put(key, obj);
    }
    public static String getStr(String key){
        return ctx.get().getStr(key);
    }

    public static void init(JSONObject jsonObject){
        ctx.set(jsonObject);
    }
    public static void clear(){
        ctx.remove();
    }


}
