package com.example.core.daoEntity;

import com.example.core.util.StringUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author chenwh3
 */
@Data
public class UpmPermission {

    private String id = StringUtil.randomUUID();

    private String name;

    /**
     * 1- 目录,  2-菜单 , 3-权限
     */
    private Integer type;

    private String permissionValue;

    private String uri;

    private String pname;

    private Date ctime = new Date();
}
