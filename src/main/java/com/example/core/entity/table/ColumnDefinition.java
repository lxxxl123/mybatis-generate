package com.example.core.entity.table;

import lombok.Data;

import java.util.*;

/**
 * 后端 , 表字段信息
 */
@Data
public class ColumnDefinition {

	/**
	 * 数据库字段名
	 */
	private String columnName;
	/**
	 * 类型
	 */
	private String type;

	/**
	 * 查询时的sql
	 */
	private String selectSql;

	/**
	 * 是否自增
	 */
	private boolean identity;


	/**
	 * 是否主键
	 */
	private boolean pk;
	private String javaType;
	private String javaFieldName;
	private String javaFiledNameUpCaseFirst;
	private String jdbcType;

	private String defaultVal;

	private Map<String, String> enumMap = new LinkedHashMap<>(0);

	private List<String> enumList = new ArrayList<>();

	/**
	 * 最大长度
	 */
	private Integer maxLength;


	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 用于前端 , 表示该字段属于的表
	 */
	private String prefix;

	private String title;

}
