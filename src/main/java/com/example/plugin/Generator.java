package com.example.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.core.action.BuildVmFileAction;
import com.example.core.action.DataBaseAction;
import com.example.core.action.SetCtxAction;
import com.example.core.action.inf.Action;
import com.example.core.entity.Context;
import com.example.core.ex.ServiceException;
import com.example.core.thread.Ctx;
import com.example.core.util.SpelUtils;
import com.example.core.util.VelocityUtil;
import com.example.factory.ServiceFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenwh3
 */
public abstract class Generator extends AbstractMojo {

    @Parameter(readonly = false, defaultValue = "")
    protected String configDir = "";

    protected ServiceFactory serviceFactory;

    protected Context context;

    public Action getAction(JSONObject obj) throws ClassNotFoundException {
        String clazz = obj.getStr("class");
        Class<? extends Action> aClass = actionMap.get(clazz);
        if (aClass == null) {
            throw new ServiceException("该action - [{}]不存在", clazz);
        }
        JSONObject params = obj.getJSONObject("params");
        params = params == null ? new JSONObject() : params;
        JSONObject spelParams = obj.getJSONObject("spelParams");
        spelParams = spelParams == null ? new JSONObject() : spelParams;

        Action action = ReflectUtil.newInstance(aClass);
        Field[] fields = ReflectUtil.getFields(action.getClass());
        if (obj.containsKey("apply")) {
            ReflectUtil.setFieldValue(action, "apply", obj.getBool("apply"));
        }
        for (Field field : fields) {
            String name = field.getName();
            if (StrUtil.endWithIgnoreCase(name, "service")) {
                ReflectUtil.setFieldValue(action, field, serviceFactory.getService(field.getType()));
            } else if (params.containsKey(name)) {
                if (params.get(name) instanceof List && field.getType().isAssignableFrom(String.class)) {
                    ReflectUtil.setFieldValue(action, field, CollUtil.join(params.getJSONArray(name), ""));
                } else if (List.class.isAssignableFrom(field.getType())) {
                    Class actualType = getActualType(field, 0);
                    ReflectUtil.setFieldValue(action, field, params.getJSONArray(name).toList(actualType));
                } else {
                    ReflectUtil.setFieldValue(action, field, params.get(name, field.getType()));
                }
            } else if (spelParams.containsKey(name)) {
                ReflectUtil.setFieldValue(action, field, SpelUtils.parse((String) spelParams.get(name), Ctx.getAll(), field.getType()));
            }
        }
        return action;
    }

    public static Class getActualType(Field o, int index) {
        Type clazz = o.getGenericType();
        ParameterizedType pt = (ParameterizedType) clazz;
        return (Class) pt.getActualTypeArguments()[index];
    }


    private Map<String, Class<? extends Action>> actionMap = MapBuilder.create(new HashMap<String, Class<? extends Action>>())
            .put("dataBaseAction", DataBaseAction.class)
            .put("buildVmFileAction", BuildVmFileAction.class)
            .put("setCtxAction", SetCtxAction.class)
            .build();

    protected void buildConfig(String fileName) {
        context = Context.of(configDir, fileName);
        Ctx.init(context.getData());
        serviceFactory = new ServiceFactory(context.getDatabase());
        VelocityUtil.init(configDir);
    }


    @Override
    protected void finalize() throws Throwable {
        Ctx.clear();
    }
}
