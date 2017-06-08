package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/6.
 */

import javax.naming.NamingEnumeration;
import javax.persistence.*;

/**
 * 用户角色类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/6
 */
@Entity
@IdClass(UserRolePK.class)
@Table(name = "sys_user_role")
public class UserRole {
    @Id
    @Column(name = "USERCODE")
    private String userCode;//用户编号
    @Id
    @Column(name = "ROLEID")
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
