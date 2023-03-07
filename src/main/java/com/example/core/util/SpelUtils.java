package com.example.core.util;

import cn.hutool.json.JSONObject;
import com.example.core.entity.Context;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author chenwh3
 */
public class SpelUtils {
    private static ExpressionParser parser = new SpelExpressionParser();

    public static <T> T parse(String expression, Map<String,Object> context, Class<T> clazz) {
        Expression exp = parser.parseExpression(expression);

        StandardEvaluationContext ctx = new StandardEvaluationContext();

        ctx.setRootObject(context);

        return exp.getValue(ctx, clazz);
    }

    public static Boolean parseBool(String expression, Map<String,Object> context) {
        return parse(expression, context, Boolean.class);
    }

}
