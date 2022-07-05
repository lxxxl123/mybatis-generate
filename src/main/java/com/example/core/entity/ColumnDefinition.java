package com.example.core.entity;

import com.example.core.util.StringUtil;
import lombok.Data;

/**
 * 表字段信息
 */
@Data
public class ColumnDefinition {

	private String columnName; // 数据库字段名
	private String type; // 数据库类型
	private boolean identity; // 是否自增
	private boolean pk; // 是否主键
	private String javaType;
	private String javaFieldName;

}
