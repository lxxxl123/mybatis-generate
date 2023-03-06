package com.example.core.util;

import cn.hutool.json.JSONObject;
import com.example.core.entity.Context;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;

/**
 * @author chenwh3
 */
public class SpelUtils {
    private static ExpressionParser parser = new SpelExpressionParser();

    public static <T> T parse(String expression, Context context, Class<T> clazz) {
        Expression exp = parser.parseExpression(expression);

        StandardEvaluationContext ctx = new StandardEvaluationContext();

        ctx.setRootObject(context.getData());

        return exp.getValue(ctx, clazz);
    }

    public static Boolean parseBool(String expression, Context context) {
        return parse(expression, context, Boolean.class);
    }

    public static void main(String[] args) {
        Context context = new Context();
        JSONObject jsonObject = new JSONObject();
        ArrayList<Object> list = new ArrayList<>();
        jsonObject.put("list", list);
        context.setData(jsonObject);
        System.out.println(parseBool("['list'].size()>0", context));

    }

}
