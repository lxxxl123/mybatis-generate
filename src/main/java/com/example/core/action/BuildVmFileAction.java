package com.example.core.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.core.action.inf.Action;
import com.example.core.entity.VmReplacePo;
import com.example.core.service.BaseDataService;
import com.example.core.thread.Ctx;
import com.example.core.util.SpelUtils;
import com.example.core.util.StringUtil;
import com.example.core.util.VelocityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chenwh3
 */
@Slf4j
public class BuildVmFileAction extends Action {

    private BaseDataService baseDataService;

    private String path;
    
    private String exitIfHave;
    
    private String spelCond;

    private String vm;

    private List<VmReplacePo> replaceList;


    @Override
    protected void run() {
        log.info("path = {}",path);
        File file = new File(path);
        if ((!file.exists() || replaceList == null) && StrUtil.isNotEmpty(vm)) {
            VelocityUtil.write(path, vm);
        } else {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            Ctx.put("content", content);
            if (StringUtils.isNotBlank(spelCond)) {
                if (!SpelUtils.parseBool(spelCond, Ctx.getAll())) {
                    return;
                }
            } else if (ReUtil.contains(exitIfHave, content)) {
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
