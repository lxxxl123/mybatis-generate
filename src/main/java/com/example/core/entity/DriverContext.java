package com.example.core.entity;

import com.example.core.util.PropsUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author chenwh3
 */
@Data
public class DriverContext {


    private String driver;
    private String url;
    private String userName;
    private String password;

    private String targetTable;

}
