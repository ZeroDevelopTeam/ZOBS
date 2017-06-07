package com.zero.logic.util;/**
 * Created by Admin on 2017/6/7.
 */

import org.springframework.util.DigestUtils;

/**
 * 密码加密
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/7
 */
public class MD5Util {

    /**
     * 将密码+帐号通过MD5加密(不可逆)
     * @param userCode
     * @param password
     * @return
     */
    public static String getMd5(String userCode,String password){
        return DigestUtils.md5DigestAsHex((userCode+password).getBytes());
    }
}
