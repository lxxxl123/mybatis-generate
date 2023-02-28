package com.example.core.proxy;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * 使用cglib进行动态代理
 * @author chenwh3
 */
public class ProxyUtils {

    public static  <T> T proxy(Class<T> clazz , MethodInterceptor invocationHandler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{clazz});
        enhancer.setCallback(invocationHandler);
        return (T) enhancer.create();
    }
}
