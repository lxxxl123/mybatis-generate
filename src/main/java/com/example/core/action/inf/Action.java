package com.example.core.action.inf;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenwh3
 */
@Slf4j
public abstract class Action {

    /**
     * 开关
     */
    private Boolean apply = null;

    private JSONObject ctx;


    protected void run(){};

    public  void init(){};


    public final void start(){
        if (Boolean.FALSE.equals(apply)) {
            return;
        }
        log.info("运行action = [{}]", getClass().getSimpleName());
        init();

        run();
    }
}
