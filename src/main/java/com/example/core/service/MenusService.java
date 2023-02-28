package com.example.core.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.core.daoEntity.UpmPermission;
import com.example.mapper.MenusMapper;

import java.util.Arrays;

/**
 * @author chenwh3
 */
public class MenusService {

    private MenusMapper menusMapper;

    public void insert(String name, String pname, Integer type, String url, String permissionValue) {
        UpmPermission upmPermission = new UpmPermission();
        upmPermission.setName(name);
        upmPermission.setPname(pname);
        upmPermission.setType(type);
        upmPermission.setUri(url);
        upmPermission.setPermissionValue(permissionValue);
        menusMapper.insert(upmPermission);

    }

    public void buildPermission(String prefix, String table, String paths) {

        paths = paths + "- ";
        String[] pathArr = paths.split("-");
        String pname = null;
        for (int i = 0; i < pathArr.length; i++) {
            String name = pathArr[i];
            Integer type = 1;
            if (i == pathArr.length - 1) {
                type = 3;
            } else if (i == pathArr.length - 2) {
                type = 2;
            }

            String url = "";
            String permissionValue = "";
            if (type == 2) {
                url = StrUtil.format("/{}/{}", prefix, table);
                permissionValue = StrUtil.format("qms:{}:read");
            }

            if (type == 3) {
                for (int j = 0; j < 3; j++) {
                    if (j == 0) {
                        name = "查询";
                        url = "qryBtn";
                        permissionValue = StrUtil.format("qms:{}:read");
                    } else if (j == 2) {
                        name = "修改";
                        url = "editBtn";
                        permissionValue = StrUtil.format("qms:{}:update");
                    } else {
                        name = "删除";
                        url = "delBtn";
                        permissionValue = StrUtil.format("qms:{}:delete");
                    }
                    insert(name, pname, type, url, permissionValue);
                }
            } else {
                insert(name, pname, type, url, permissionValue);
            }

            pname = name;
        }

    }



}
