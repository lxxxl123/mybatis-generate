package com.example.core.helper;

import com.example.config.MybatisSession;
import com.example.core.entity.ColumnDefinition;
import com.example.core.entity.ConfigContext;
import com.example.core.entity.DriverContext;
import com.example.core.entity.TableDefinition;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @Author LiuYue
 * @Date 2019/1/2
 * @Version 1.0
 */
@Data
abstract public class AbstractDbHelper {

    protected DriverContext context;

    protected SqlSessionFactory sessionFactory;


    public static Map<String, Supplier<AbstractDbHelper>> CHOOSE = new HashMap<>();

    static {
        CHOOSE.put("mysql", MysqlHelper::new);
        CHOOSE.put("sqlserver", SqlServerHelper::new);
    }

    public static AbstractDbHelper of(ConfigContext context) {
        DriverContext driverContext = new DriverContext();
        BeanUtil.copyProperties(context, driverContext);
        return of(driverContext);
    }

    public static AbstractDbHelper of(Map<String, Object> context) {
        DriverContext driverContext = BeanUtil.mapToBean(context, DriverContext.class, true);
        return of(driverContext);
    }

    public static AbstractDbHelper of(DriverContext driverContext) {
        String url = driverContext.getUrl();
        AbstractDbHelper helper = CHOOSE.entrySet().stream()
                .filter(e -> StringUtils.containsIgnoreCase(url, e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(MysqlHelper::new)
                .get();

        SqlSessionFactory SqlSessionFactory = MybatisSession.init(driverContext);
        helper.setSessionFactory(SqlSessionFactory);
        helper.setContext(driverContext);
        return helper;
    }


    public abstract List<ColumnDefinition> getColumnsInfo();


    public abstract TableDefinition getTableInfo();
}
