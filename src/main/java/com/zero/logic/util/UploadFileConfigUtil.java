package com.zero.logic.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * 文件上传配置类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
public class UploadFileConfigUtil {
    public static String uploadFilePath;//文件保存路径
    public static String uploadFileSize;//文件大小设置

    static {
        try {
            Properties propes = getPropes("/application.properties");
            uploadFilePath = propes.getProperty("uploadFilePath");
            uploadFileSize = propes.getProperty("uploadFileSize");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取Properties
     * @param string
     * @return
     */
    public static Properties getPropes(String string){
        Properties propes;
        try {
            Resource resource = new ClassPathResource(string);//读取配置文件
            propes = PropertiesLoaderUtils.loadProperties(resource);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return propes;
    }
}
