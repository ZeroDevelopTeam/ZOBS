package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;
import javax.persistence.*;

/**
 * 用户实体类
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
@Entity
@Table(name = "sys_user")
public class User extends BasicBean{
    @Id
    @Column(name = "USERCODE")
    //用户编号
    private String userCode;
    @Column(name = "USERNAME")
    //用户名
    private String userName;
    @Column(name = "USERPSW")
    //密码
    private String userPsw;
    //联系电话
    @Column(name = "PHONE")
    private String phone;
    //用户地址
    @Column(name = "ADDRESS")
    private String address;
    //0为停用、1为启用
    @Column(name = "STATE")
    private int state;
    //用户邮箱
    @Column(name = "EMAIL")
    private String email;

    //getter和setter方法
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
