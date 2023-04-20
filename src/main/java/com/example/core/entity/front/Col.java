package com.example.core.entity.front;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author chenwh3
 */
@Data
public class Col {

    private Boolean isPk = false;

    private String field;

    private String title;

    private String col;

    /**
     * 自定义信息
     */
    private String cusMsg;

    private String fullFieldName;

    private Boolean autoBuild = false;

    private String type;

    private Boolean isTime;

    private Boolean isUnique;

    private Boolean editable;
    /**
     * 是否逻辑删除字段
     */
    private Boolean isDel = false;

    private Map<String,String> enumMap;

    private List<String> enumList;

}
