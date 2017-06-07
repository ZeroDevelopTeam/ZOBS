package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/6.
 */

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户角色类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/6
 */

public class UserRole {
    private String userCode;//用户编号
    private String roleId;//角色id

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
