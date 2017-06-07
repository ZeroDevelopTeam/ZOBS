package com.zero.logic.util;/**
 * Created by Admin on 2017/6/1.
 */

;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
/**
 * json格式
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
public class JsonUtil {
    public static final int RESULT_FAIL = 500;    //操作失败
    public static final int RESULT_SUCCESS = 200; //操作成功
    public static String returnStr(int status,String msg) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("status",status);
        json.put("msg",msg);
        return json.toString();
    }

    /**
     * 将对象转换成JSONObject
     * @param obj 对象
     * @return jsonObject类型的字符串
     */
    public static String fromObject(Object obj){
        JSONObject jsonObject = JSONObject.fromObject(obj);
        return jsonObject.toString();
    }





    /**
     * 将对象转换成JSON,添加新的键值
     * @param obj  对象
     * @param map  键值集合
     * @return JSONObject类型的字符串
     */
    public static String makeJsonBeanByKey(Object obj,Map<String, Object> map){
        JSONObject jsonObject=JSONObject.fromObject(obj);
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toString();
    }
}
