package com.zero.logic.util;/**
 * Created by Admin on 2017/6/2.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 表工具类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/2
 */
public class TableUtil {
    /**
     * 创建表格数据
     * @param total 总数
     * @param currentPage 当前页
     * @param totalPage 总页数
     * @return 分页信息
     */
    public static Object createTableDate(long total,int currentPage,long totalPage){

        Map map = new HashMap();
        map.put("currentPage", currentPage);
        map.put("totalPage", totalPage);
        map.put("total", total);
        return map;
    }
}
