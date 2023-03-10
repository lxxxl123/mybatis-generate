package com.example.core.entity.table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenwh3
 */
@Data
@NoArgsConstructor
public class LeftJoinCol {

    private String columnName;

    private String remark;

    private String selectSql;

    private String javaFieldName;

}
