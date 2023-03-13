package com.example.core.service;

import cn.hutool.core.util.StrUtil;
import com.example.core.daoEntity.UpmPermission;
import com.example.mapper.MenusMapper;

/**
 * @author chenwh3
 */
public class MenusService {

    private MenusMapper menusMapper;

    public String insert(String name, String pname, String pid, Integer type, String url, String permissionValue) {
        UpmPermission upmPermission = new UpmPermission();
        upmPermission.setName(name);
        upmPermission.setPname(pname);
        upmPermission.setPid(pid);
        upmPermission.setType(type);
        upmPermission.setUri(url);
        upmPermission.setPermissionValue(permissionValue);
        menusMapper.insert(upmPermission);
        return menusMapper.getId(upmPermission);
    }

    public void buildPermission(String prefix, String table, String paths) {

        paths = paths + "- ";
        String[] pathArr = paths.split("-");
        String pname = null;
        String pid = "1";
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
                permissionValue = StrUtil.format("qms:{}:read", table);
            }

            if (type == 3) {
                for (int j = 0; j < 3; j++) {
                    if (j == 0) {
                        name = "查询";
                        url = "qryBtn";
                        permissionValue = StrUtil.format("qms:{}:read", table);
                    } else if (j == 2) {
                        name = "修改";
                        url = "editBtn";
                        permissionValue = StrUtil.format("qms:{}:update",table);
                    } else {
                        name = "删除";
                        url = "delBtn";
                        permissionValue = StrUtil.format("qms:{}:delete", table);
                    }
                     insert(name, pname, pid, type, url, permissionValue);
                }
            } else {
                pid = insert(name, pname, pid, type, url, permissionValue);
            }

            pname = name;
        }

    }



}
