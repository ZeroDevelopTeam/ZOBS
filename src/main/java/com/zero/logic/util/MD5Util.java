package com.zero.logic.util;

import org.springframework.util.DigestUtils;

/**
 * 密码加密
 * @auther Deram Zhao
 * @creatTime 2017/6/7
 */
public class MD5Util {

    /**
     * 将密码+帐号通过MD5加密(不可逆)
     * @param userCode 用户名
     * @param password 密码
     * @return MD5密码
     * @throws Exception
     */
    public static String getMd5(String userCode,String password)throws Exception{
        return DigestUtils.md5DigestAsHex((userCode+password).getBytes());
    }
}
