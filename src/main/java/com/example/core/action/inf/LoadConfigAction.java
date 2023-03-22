package com.example.core.action.inf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.example.factory.YamlUtils;
import lombok.SneakyThrows;

/**
 * @author chenwh3
 */
public abstract class LoadConfigAction extends Action {

    protected String config;
    @SneakyThrows
    public void init() {
        LoadConfigAction load = YamlUtils.load(config, getClass());
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.setIgnoreNullValue(true);
        BeanUtil.copyProperties(load, this, copyOptions);
    }
}
