package com.example.core.util;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {



    /**
     * 将字符串写入文件
     */
    public static void writeFile(String filePath, String fileContent) {
        File f = writeString(fileContent, filePath, "utf-8");
        System.out.println(f.getAbsoluteFile());
    }

}
