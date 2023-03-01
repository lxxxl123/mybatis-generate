package com.example.factory;

import cn.hutool.core.io.resource.ResourceUtil;
import com.example.core.util.StringUtil;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.mvel2.ParserContext;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RuleFactory {

    public static void fire(Map<String,Object> map , String vmPath){
        /**
         * 增加静态类
         */
        ParserContext parserContext = new ParserContext();
        parserContext.addImport(StringUtil.class);

        MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader(), parserContext);
        Rules yamlRules = null;
        try {
            yamlRules = ruleFactory.createRules(new InputStreamReader(ResourceUtil.getStream(vmPath)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Facts facts = new Facts();
        map.forEach(facts::put);

        DefaultRulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(yamlRules, facts);
    }


    public static void main(String[] args) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("map", new HashMap<>());
        fire(map, "vm/front/rules.yaml");
        System.out.println(map);

    }
}
