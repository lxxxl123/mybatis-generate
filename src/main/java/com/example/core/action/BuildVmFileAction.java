package com.example.core.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import com.example.core.action.inf.Action;
import com.example.core.entity.VmReplacePo;
import com.example.core.service.BaseDataService;
import com.example.core.thread.Ctx;
import com.example.core.util.SpelUtils;
import com.example.core.util.StringUtil;
import com.example.core.util.VelocityUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chenwh3
 */
public class BuildVmFileAction extends Action {

    private BaseDataService baseDataService;

    private String path;
    
    private String exitIfContain;
    
    private String spelCond;

    private String vm;

    private List<VmReplacePo> replaceList;


    @Override
    protected void run() {
        File file = new File(path);
        if (!file.exists() || CollUtil.isEmpty(replaceList)) {
            VelocityUtil.write(path, vm);
        } else {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            Ctx.put("content", content);
            if (StringUtils.isNotBlank(spelCond)) {
                if (!SpelUtils.parseBool(spelCond, Ctx.getAll())) {
                    return;
                }
            } else if (ReUtil.contains(content, exitIfContain)) {
                return;
            }
            for (VmReplacePo vmPo : replaceList) {
                String part = VelocityUtil.render(vmPo.getVm());
                content = StringUtil.merge(content, part, Pattern.compile(vmPo.getRange()));
                FileUtil.writeString(content, path, StandardCharsets.UTF_8);
            }
        }
    }

}
