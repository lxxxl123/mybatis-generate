package com.example.core.util;

import com.example.core.thread.Ctx;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity工具类,根据模板内容生成文件
 * @author chenwh3
 */
public class VelocityUtil {


	public static String render(String vm, Map<String,Object> map) {
		VelocityContext context = new VelocityContext();
		map.forEach(context::put);
		return render(vm, context);
	}

	public static String render(String vm) {
		VelocityContext context = new VelocityContext();
		Ctx.getAll().forEach(context::put);
		return render(vm, context);
	}

	public static String render(String vm, VelocityContext context) {
		String content = "";
		String[] arr = null;

		Template template = null;
		try {
			template = Velocity.getTemplate(vm);
			StringWriter writer = new StringWriter();
			if (template != null) {
				template.merge(context, writer);
			}
			writer.flush();
			writer.close();
			content = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void init(String resourcePath){
		Properties props = new Properties();
		props.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, resourcePath);
		props.setProperty("input.encoding", "utf-8");
		props.setProperty("output.encoding", "utf-8");
		// --true Velocity 会尝试解析字符串中的 $变量
		props.setProperty("runtime.interpolate.string.literals", "false");
		Velocity.init(props);
	}

	private static String getPackagePath(String targetPackage) {
		String res = StringUtils.replace(targetPackage, ".", "/");
		return res.replaceAll("/(\\w+)$", ".$1");
	}
	public static void write( String fileName, String vmName) {
		fileName = getPackagePath(fileName);
		FileUtil.writeFile(
				fileName,
				VelocityUtil.render(vmName));
	}


}
