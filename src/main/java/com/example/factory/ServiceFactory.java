package com.example.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.example.config.MybatisSession;
import com.example.core.entity.ConfigContext;
import com.example.core.entity.DriverContext;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author chenwh3
 */
public class ServiceFactory {

    protected DriverContext context;

    protected SqlSessionFactory sqlSessionFactory;


    public ServiceFactory(ConfigContext context) {
        DriverContext driverContext = new DriverContext();
        BeanUtil.copyProperties(context, driverContext);
        init(driverContext);
    }

    public ServiceFactory(Map map) {
        DriverContext driverContext = BeanUtil.mapToBean(map, DriverContext.class, true);
        init(driverContext);
    }

    public ServiceFactory(DriverContext driverContext) {
        init(driverContext);
    }

    public void init(DriverContext driverContext){
        this.context = driverContext;
        this.sqlSessionFactory =  MybatisSession.init(driverContext);
    }

    public <T> T getService(Class<T> service) {
        T t = ReflectUtil.newInstance(service);
        fillServiceMapper(t, sqlSessionFactory);
        return t;
    }


    /**
     * 使用动态代理补全所有mapper
     */
    public static void fillServiceMapper(Object service, SqlSessionFactory sessionFactory) {
        Field[] fields = ReflectUtil.getFields(service.getClass());
        for (Field field : fields) {
            if (field.getType().getAnnotation(Mapper.class) != null) {
                ReflectUtil.setFieldValue(service, field, MapperFactory.getMapper(field.getType(), sessionFactory));
            }
        }
    }
}
