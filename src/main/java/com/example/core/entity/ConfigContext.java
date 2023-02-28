package com.example.core.entity;

import com.example.core.util.PropsUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
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
    private String username;
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
    private String author;
    private Map<String, String> other = new HashMap<>();

    public ConfigContext(String sourcePath, String outputPath) {
        sourcePath = StringUtils.appendIfMissing(sourcePath,"/","/");

        Properties properties = PropsUtil.loadProps(sourcePath+Constant.CONFIG_PROPS);
        setSourcePath(sourcePath);
        setOutputPath(outputPath);

        setDriver(PropsUtil.getString(properties, Constant.JDBC_DRIVER));
        setUrl(PropsUtil.getString(properties, Constant.JDBC_URL));
        setUsername(PropsUtil.getString(properties, Constant.JDBC_USERNAME));
        setPassword(PropsUtil.getString(properties, Constant.JDBC_PASSWORD));

        setTargetTable(PropsUtil.getString(properties, Constant.TARGET_TABLE));
        setTargetName(PropsUtil.getString(properties, Constant.TARGET_NAME));
        setTargetPackage(PropsUtil.getString(properties, Constant.TARGET_PACKAGE));
        setTargetEntity(PropsUtil.getString(properties, Constant.TARGET_ENTITY));
        setTargetService(PropsUtil.getString(properties, Constant.TARGET_SERVICE));
        setTargetServiceImpl(PropsUtil.getString(properties, Constant.TARGET_SERVICEIMPL));
        setTargetController(PropsUtil.getString(properties, Constant.TARGET_CONTROLLER));
        setTargetDao(PropsUtil.getString(properties, Constant.TARGET_DAO));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = ((String) entry.getKey());
            if (StringUtils.startsWithAny(key, "other.")) {
                other.put(key.substring(6), (String) entry.getValue());
            }
        }
        setMapperXmlPath(PropsUtil.getString(properties, Constant.TARGET_MAPPER_XML_PATH));
    }


}
