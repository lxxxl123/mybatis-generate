package com.example.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.Context;
import com.example.core.entity.VmReplacePo;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Velocity工具类,根据模板内容生成文件
 */
public class VelocityUtil {


	public static String render(String vm, Map<String,Object> map) {
		VelocityContext context = new VelocityContext();
		map.forEach(context::put);
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
		Properties velocityPros = new Properties();
		velocityPros.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, resourcePath);
		velocityPros.setProperty("input.encoding", "utf-8");
		velocityPros.setProperty("output.encoding", "utf-8");
		Velocity.init(velocityPros);
	}

	private static String getPackagePath(String targetPackage) {
		String res = StringUtils.replace(targetPackage, ".", "/");
		return res.replaceAll("/(\\w+)$", ".$1");
	}
	public static void write( String fileName, String vmName, VelocityContext context) {
		fileName = getPackagePath(fileName);
		FileUtil.writeFile(
				fileName,
				VelocityUtil.render(vmName, context));
	}


	public static void write(String fileName, String vmName, Map<String, Object> map) {
		VelocityContext context = new VelocityContext();
		map.forEach(context::put);
		write(fileName, vmName, context);
	}


    public static void buildPage(String buildName, Context context) {
        JSONObject base = context.getBase();
        JSONObject data = context.getData();
        JSONObject config = context.getFileBuilder().getJSONObject(buildName);
        String routePath = CollUtil.join(config.getJSONArray("path"), "");
        String vm = config.getStr("vm");

        String condition = null;
        if (config.containsKey("condition")) {
            condition = CollUtil.join(config.getJSONArray("condition"), "");
        }
        List<VmReplacePo> replaceList = new ArrayList<>();
        if (config.containsKey("replace")) {
            replaceList = config.getJSONArray("replace").toList(VmReplacePo.class);
        } else {
            String replaceRange = config.getStr("replaceRange");
            String replaceVm = config.getStr("replaceVm");
            if (StringUtil.isNotEmpty(replaceVm)) {
                VmReplacePo vmReplacePo = new VmReplacePo(replaceRange, replaceVm);
                replaceList.add(vmReplacePo);
            }
        }

        File file = new File(routePath);
        if (!file.exists() || CollUtil.isEmpty(replaceList)) {
            write(routePath, vm, data);
        } else {
            String content = cn.hutool.core.io.FileUtil.readString(file, "utf-8");
			data.put("content", content);
			if (condition.startsWith("spel:")) {
				condition = condition.substring(5);
				if (SpelUtils.parseBool(condition, context)) {
					return;
				}
			} else if (ReUtil.contains(condition, content)) {
				return;
			}
            for (VmReplacePo vmPo : replaceList) {
                String part = render(vmPo.getVm(), data);
                content = StringUtil.merge(content, part, Pattern.compile(vmPo.getRange()));
                cn.hutool.core.io.FileUtil.writeString(content, routePath, "utf-8");
            }
        }
    }
}
