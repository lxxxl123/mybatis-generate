package com.example.factory;

import cn.hutool.core.io.resource.ResourceUtil;
import org.yaml.snakeyaml.Yaml;

/**
 * @author chenwh3
 */
public class YamlUtils {

    public static <T> T load(String path, Class<T> clazz) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(ResourceUtil.getStream(path), clazz);
    }


}
