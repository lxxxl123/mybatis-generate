package com.example.core.entity;

import com.example.core.util.StringUtil;
import lombok.Data;

/**
 * 表字段信息
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
	private String jdbcType;
	/**
	 * 备注
	 */
	private String remark;

}
