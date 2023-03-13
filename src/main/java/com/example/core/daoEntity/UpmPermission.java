package com.example.core.daoEntity;

import cn.hutool.core.date.DateUtil;
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

    private String pid;

    /**
     * 1- 目录,  2-菜单 , 3-权限
     */
    private Integer type;

    private String permissionValue;

    private String uri;

    private String pname;

    private String ctime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
}
