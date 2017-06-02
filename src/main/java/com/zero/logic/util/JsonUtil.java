package com.zero.logic.util;/**
 * Created by Admin on 2017/6/1.
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * json格式
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
public class JsonUtil {
    public static String returnStr(int status,String msg) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("status",status);
        json.put("msg",msg);
        return json.toString();
    }
}
