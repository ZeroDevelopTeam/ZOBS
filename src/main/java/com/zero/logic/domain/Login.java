package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/5.
 */

/**
 * 用户登录类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/5
 */
public class Login {
    private String userCode;//用户编码
    private String userName;//用户名
    private String userPsw;//密码

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPsw() {
        return userPsw;
    }

    public void setUserPsw(String userPsw) {
        this.userPsw = userPsw;
    }
}
