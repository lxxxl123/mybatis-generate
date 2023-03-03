package com.example.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.Context;
import com.example.core.entity.VmReplacePo;
import com.example.core.entity.front.Col;
import com.example.core.service.BaseDataService;
import com.example.core.service.MenusService;
import com.example.core.util.StringUtil;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author chenwh3
 */
@Slf4j
public class GeneratorVue extends AbstractMojo {

    @Parameter(property = "project.backDir", required = true, readonly = true)
    private File backDir;

    private File frontDir;

    private String configDir = "";


    private Context context;

    private ServiceFactory serviceFactory;

    private void buildConfig() {
        context = Context.of(configDir, "gen-front.yaml");
        serviceFactory = new ServiceFactory(context.getBase());
        VelocityUtil.init(configDir);
    }

    /**
     * 获取数据库信息
     */
    public void buildMetaData() {
        //初始化DB工具类
        BaseDataService dbHelper = serviceFactory.getService(BaseDataService.class);

        JSONObject data = context.getData();
        //元数据处理
        data.put("columns", dbHelper.getColumnsInfo(context.getBase().getStr("targetTable")));


    }

    public void execute() {
        buildConfig();
        // 获取数据库信息
        buildMetaData();
        // 生成前端菜单
        buildMenus();

        buildVue();

    }

    private void buildVue() {
        JSONObject base = context.getBase();
        JSONObject data = context.getData();
        String menusChPath = base.getStr("menusChPath");

        // 生成菜单名
        String[] split = menusChPath.split("-");
        Object menusName = ArrayUtil.get(split, -1);
        data.put("pathChName", menusName);

        buildVue("backYmlFormConfig");
        buildVue("routeConfig");
        buildVue("routeFormConfig");
        buildVue("apiConfig");
        buildViewMetaData();
        buildVue("viewConfig");

    }


    private void buildViewMetaData() {
        Set<String> sets = SetUtils.hashSet("createTime","updateTime","creator","modifier");
        JSONObject data = context.getData();
        List<ColumnDefinition> columns = data.getJSONArray("columns").toList(ColumnDefinition.class);

        List<Col> list = columns.stream().map(e -> {
            Col col = new Col();
            col.setIsPk(e.isPk());
            col.setCol(e.getColumnName());
            col.setField(e.getColumnName());
            col.setTitle(e.getRemark().split(";")[0]);
            if (sets.contains(col.getField())) {
                col.setAutoBuild(true);
            }
            String type = e.getType();
            col.setIsTime(type.startsWith("date"));
            col.setType(type);
            col.setEnumMap(e.getEnumMap());
            col.setEnumList(e.getEnumList());

            if (col.getTitle().endsWith("人")) {
                col.setCusMsg(StrUtil.format("type: 'vxe-emp-pulldown', propMap: [ { key: '{}', val: 'empName' } ]", col.getCol()));
            }
            else if (SetUtils.hashSet("matnr").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-matnr-pulldown', propMap: [ { key: '{}', val: 'matnr' } ]", col.getCol()));
            }
            else if (SetUtils.hashSet("vtcode").contains(col.getField())) {
                col.setCusMsg(StrUtil.format("type: 'vxe-vtcode-pulldown', propMap: [ { key: '{}', val: 'cno' } ]", col.getCol()));
            }


            if (StrUtil.containsAnyIgnoreCase(col.getField(), "isDel")) {
                col.setIsDel(true);
            }
            /**
             * 枚举值更改field
             */
            if (col.getEnumMap().size() > 0) {
                col.setField(col.getCol() + "Str");
            }

            return col;
        }).collect(Collectors.toList());

        data.put("showCols", list.stream()
                .filter(e->!e.getIsPk())
                .collect(Collectors.toList()));

        data.put("searchCols", list.stream()
                .filter(e->!e.getIsPk())
                .collect(Collectors.toList()));

        data.put("addCols", list.stream()
                .filter(e->!e.getIsPk())
                .filter(e->!e.getAutoBuild())
                .filter(e->!e.getIsDel())
                .collect(Collectors.toList()));

        data.put("updateCols", list.stream()
                .filter(e->!e.getIsPk())
                .filter(e->!e.getAutoBuild())
                .filter(e->!e.getIsDel())
                .collect(Collectors.toList()));

        data.put("pkCol", list.stream()
                .filter(e -> e.getIsPk())
                .findFirst().orElse(null));

        data.put("delCol", list.stream()
                .filter(e -> e.getIsDel())
                .findFirst().orElse(null));

    }


    private void buildVue(String buildName) {
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
            VelocityUtil.write(routePath, vm, data);
        } else {
            String content = FileUtil.readString(file, "utf-8");
            if (ReUtil.contains(condition, content)) {
                return;
            }
            for (VmReplacePo vmPo : replaceList) {
                String part = VelocityUtil.render(vmPo.getVm(), data);
                content = StringUtil.merge(content, part, Pattern.compile(vmPo.getRange()));
                FileUtil.writeString(content, routePath, "utf-8");
            }
        }
    }

    private void buildMenus() {
        JSONObject base = context.getBase();
        String prefix = base.getStr("prefix");
        String targetName = base.getStr("targetName");
        String menusChPath = base.getStr("menusChPath");
        MenusService service = serviceFactory.getService(MenusService.class);
        service.buildPermission(prefix, targetName, menusChPath);
    }


    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        GeneratorVue generatorVue = new GeneratorVue();
        generatorVue.frontDir = new File("D:\\20221014\\qms-front");
        generatorVue.configDir = "D:\\20221014\\generator-plugin-test\\src\\main\\resources\\vm\\";
        generatorVue.execute();

    }


}
