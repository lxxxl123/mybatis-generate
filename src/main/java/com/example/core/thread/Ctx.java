package com.example.core.thread;

import cn.hutool.json.JSONObject;

/**
 * @author chenwh3
 */
public class Ctx {

    private static final ThreadLocal<JSONObject> ctx = new ThreadLocal<>();

    public static void put(String key, Object obj){
        ctx.get().put(key, obj);
    }

    public static boolean containsKey(String key){
        return ctx.get().containsKey(key);
    }

    public static void putIfAbsent(String key, Object obj){
        ctx.get().putIfAbsent(key, obj);
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
    public static JSONObject getAll(){
        return ctx.get();
    }



}
