package com.example.core.entity;

import com.example.core.util.PropsUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author chenwh3
 */
public class FrontContext extends JSONObject {

    public FrontContext(Object source) {
        super(source);
    }

    public static FrontContext of(String sourcePath) {
        Properties properties = PropsUtil.loadProps(sourcePath + Constant.FRONT_PROPS);
        return new FrontContext(properties);
    }

}
