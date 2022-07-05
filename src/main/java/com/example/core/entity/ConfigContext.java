package com.example.core.entity;

import com.example.core.util.PropsUtil;
import lombok.Data;

import java.util.Properties;


/**
 * @author chenwh3
 */
@Data
public class ConfigContext {

    private String sourcePath;
    private String outputPath;

    private String driver;
    private String url;
    private String userName;
    private String password;


    private String targetTable;
    private String targetName;
    private String targetPackage;
    private String targetEntity;
    private String targetService;
    private String targetServiceImpl;
    private String targetController;
    private String targetDao;
    private String mapperXmlPath;

    public ConfigContext(String sourcePath, String outputPath) {

        Properties properties = PropsUtil.loadProps(sourcePath+Constant.CONFIG_PROPS);
        setSourcePath(sourcePath);
        setOutputPath(outputPath);

        setDriver(PropsUtil.getString(properties, Constant.JDBC_DRIVER));
        setUrl(PropsUtil.getString(properties, Constant.JDBC_URL));
        setUserName(PropsUtil.getString(properties, Constant.JDBC_USERNAME));
        setPassword(PropsUtil.getString(properties, Constant.JDBC_PASSWORD));

        setTargetTable(PropsUtil.getString(properties, Constant.TARGET_TABLE));
        setTargetName(PropsUtil.getString(properties, Constant.TARGET_NAME));
        setTargetPackage(PropsUtil.getString(properties, Constant.TARGET_PACKAGE));
        setTargetEntity(PropsUtil.getString(properties, Constant.TARGET_ENTITY));
        setTargetService(PropsUtil.getString(properties, Constant.TARGET_SERVICE));
        setTargetServiceImpl(PropsUtil.getString(properties, Constant.TARGET_SERVICEIMPL));
        setTargetController(PropsUtil.getString(properties, Constant.TARGET_CONTROLLER));
        setTargetDao(PropsUtil.getString(properties, Constant.TARGET_DAO));
        setMapperXmlPath(PropsUtil.getString(properties, Constant.TARGET_MAPPER_XML_PATH));
    }


}
