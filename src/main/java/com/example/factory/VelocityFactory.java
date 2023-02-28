package com.example.factory;

import com.example.core.util.FileUtil;
import com.example.core.util.VelocityUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.util.Map;
import java.util.Properties;

/**
 * @author chenwh3
 */
public class VelocityFactory {

    public static void init(String resourcePath){
        Properties velocityPros = new Properties();
        velocityPros.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, resourcePath);
        velocityPros.setProperty("input.encoding", "utf-8");
        velocityPros.setProperty("output.encoding", "utf-8");
        Velocity.init(velocityPros);
    }

    public static void write( String fileName, String vmName, VelocityContext context) {
        FileUtil.writeFile(
                fileName,
                VelocityUtil.render(vmName, context));
    }


    public static void write(String fileName,String vmName, Map<String,Object> map) {
        VelocityContext context = new VelocityContext();
        map.forEach(context::put);
        write(vmName, vmName, context);
    }
}
