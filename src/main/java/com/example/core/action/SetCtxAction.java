package com.example.core.action;

import cn.hutool.core.lang.ClassScanner;
import com.example.core.action.inf.Action;
import com.example.core.service.BaseDataService;
import com.example.core.thread.Ctx;
import com.example.core.util.SpelUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenwh3
 */
@Slf4j
public class SetCtxAction extends Action {

    Map<String,Object> map;

    Map<String, String> spelMap;

    @Override
    protected void run() {
        if (map != null) {
            map.forEach((k,v)->{
                Ctx.put(k, v);
            });
        }

        if (spelMap != null) {
            spelMap.forEach((k, v) -> {
                Ctx.put(k, SpelUtils.parseStr(v, Ctx.getAll()));
            });
        }
        log.info("finish");
    }



}
