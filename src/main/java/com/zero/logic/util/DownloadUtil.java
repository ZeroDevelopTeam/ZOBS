package com.zero.logic.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 下载工具类
 * @auther Deram Zhao
 * @creatTime 2017/6/14
 */
public class DownloadUtil {
    /**
     * 下载图片
     * @param path 文件路径
     * @param response 响应对象
     */
    public static void downloadImg(String path, HttpServletResponse response){
        InputStream in =null;
        OutputStream out = null;
        try {
            response.setContentType("image/jpeg");
            in = new FileInputStream(path);
            int len = 0;
            byte [] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len=in.read(buffer))>0){
                out.write(buffer,0,len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeSilently(out);
            closeSilently(in);
        }
    }

    /**
     * 关闭资源
     * @param closeable 可关闭的资源
     * @return 关闭的资源
     */
    private static Closeable closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException exception) {

        }
        return null;
    }
}
