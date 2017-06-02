package com.zero.logic.util;/**
 * Created by Admin on 2017/6/2.
 */

import org.hibernate.loader.custom.Return;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/2
 */
public class JDBCUtil {

    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConn(){
        Properties propes;
        Connection conn = null;
        Resource resource = new ClassPathResource("/application.properties");//
        try {
            propes = PropertiesLoaderUtils.loadProperties(resource);
            String driver =propes.getProperty("spring.datasource.driver-class-name");
            Class.forName(driver);
            conn = DriverManager.getConnection(
                    propes.getProperty("spring.datasource.url"),
                    propes.getProperty("spring.datasource.username"),
                    propes.getProperty("spring.datasource.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     *
     * @param conn
     * @param tableName
     * @return
     */
    public static int getCount(Connection conn, String tableName) {
        int total = 0;
        String sql = "select count(1) from " + tableName;
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
