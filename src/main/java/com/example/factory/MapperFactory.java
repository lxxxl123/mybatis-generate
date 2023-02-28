package com.example.factory;

import com.example.config.MybatisSession;
import com.example.core.proxy.ProxyUtils;
import lombok.AllArgsConstructor;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Method;

/**
 * @author chenwh3
 */
public class MapperFactory {

    @AllArgsConstructor
    static class MapperInterceptor implements MethodInterceptor {
        private SqlSessionFactory sessionFactory;

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            try (SqlSession session = sessionFactory.openSession(true)) {
                Object mapper = session.getMapper(o.getClass().getInterfaces()[0]);
                return method.invoke(mapper, objects);
            }
        }
    }



    public static <T> T getMapper(Class<T> clazz , SqlSessionFactory sessionFactory) {
        return ProxyUtils.proxy(clazz, new MapperInterceptor(sessionFactory));
    }
}
