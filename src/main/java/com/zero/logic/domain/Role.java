package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/8.
 */

import javax.naming.NamingEnumeration;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/8
 */
@Entity
@Table(name = "sys_role")
public class Role {
    @Id
    @Column(name = "ROLEID")
    private  String roleId;//角色编号
    @Column(name = "ROLENAME")
    private  String roleName;//角色名称
    @Column(name = "ROLEDESC")
    private  String roleDesc;//备注
    private  int state;//0为停用、1为启用

    public String getRoleId() {return roleId;}

    public void setRoleId(String roleId) {this.roleId = roleId;}

    public String getRoleName() {return roleName;}

    public void setRoleName(String roleName) {this.roleName = roleName;}

    public String getRoleDesc() {return roleDesc;}

    public void setRoleDesc(String roleDesc) {this.roleDesc = roleDesc;}

    public int getState() {return state;}

    public void setState(int state) {this.state = state;}
}
