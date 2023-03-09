package com.example.config;

import com.example.core.entity.table.LeftJoinTable;
import com.example.factory.YamlUtils;
import lombok.Data;

import java.util.Map;

/**
 * @author chenwh3
 */
@Data
public class LeftJoinConfig {

    private Map<String,LeftJoinTable> tables;

    public static LeftJoinConfig init(){
        return YamlUtils.load("config/leftJoinConfig.yaml", LeftJoinConfig.class);

    }

    public LeftJoinTable get(String code){
        return tables.get(code);
    }



    public static void main(String[] args) {
        System.out.println(init());

    }

}
