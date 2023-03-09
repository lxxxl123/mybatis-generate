package com.example.core.entity.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenwh3
 */
@Data
@NoArgsConstructor
public class LeftJoinTable {

    private String prefix;

    private String tableName;

    private String on;

    private String leftCol;

    private List<LeftJoinCol> cols;
}
