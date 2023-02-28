package com.example.core.util;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {

    public static String getPackagePath(String targetPackage) {
        return StringUtils.replace(targetPackage, ".", "/");
    }

    /**
     * 将字符串写入文件
     */
    public static void writeFile(String filePath, String fileContent) {
        filePath = getPackagePath(filePath);
        filePath = filePath.replaceAll("/(\\w+)$", ".$1");
        File f = writeString(fileContent, filePath, "utf-8");
        System.out.println(f.getAbsoluteFile());
    }

}
