package com.example.core.entity;

import com.example.core.util.PropsUtil;
import cn.hutool.json.JSONObject;
import com.example.factory.YamlUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author chenwh3
 */
@Data
public class FrontContext {

    private JSONObject base;

    private JSONObject vueBuilder;
    private JSONObject data = new JSONObject();

    public static FrontContext of(String sourcePath) {
        FrontContext load = YamlUtils.load(sourcePath + "front.yaml", FrontContext.class);
        load.data.putAll(load.base);
        return load;
    }

    public static void main(String[] args) {
        FrontContext load = YamlUtils.load("vm/front.yaml", FrontContext.class);
        System.out.println(load);

    }

}
