package com.example.config;

import com.example.core.entity.DriverContext;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.File;

/**
 * @author chenwh3
 */
public class MybatisSession {


    private static DataSource initDataSource(DriverContext context) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(context.getDriver());
        dataSource.setUrl(context.getUrl());
        dataSource.setUsername(context.getUsername());
        dataSource.setPassword(context.getPassword());
        dataSource.setValidationQuery("select 1");
        return dataSource;
    }

    public static SqlSessionFactory init(DriverContext driverContext) {

        DataSource dataSource = initDataSource(driverContext);

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("dev", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);

        File file = new File(MybatisSession.class.getResource("/ms-mapper/").getFile());

        for (String s : file.list()) {
            String name = new File(s).getName();
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(MybatisSession.class.getResourceAsStream("/ms-mapper/" + name)
                    , configuration
                    , "/ms-mapper/" + name
                    , configuration.getSqlFragments());
            xmlMapperBuilder.parse();
        }


        return new SqlSessionFactoryBuilder().build(configuration);
    }

}
