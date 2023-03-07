package com.example.core.ex;

import cn.hutool.core.util.StrUtil;

/**
 * @author chenwh3
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 2359767895161832954L;


    public ServiceException(String message) {
    }

    public ServiceException(String message, Object... objects) {
        this(StrUtil.format(message, objects));
    }

}