package com.example.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
public class FileUtil {

    public static String getPackagePath(String targetPackage){
        return StringUtils.replace(targetPackage,".","/")+"/";
    }

    /**
     * 将字符串写入文件
     */
    public static void writeFile(String filePath,String fileName, String fileContent) {
        filePath = getPackagePath(filePath);
        File f = new File(filePath);

        if (!f.exists()) {
            f.mkdirs();
        }

        File myFile = new File(f, fileName);

        FileWriter w = null;
        try {
            w = new FileWriter(myFile);

            w.write(fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Error creating file " + fileName, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
