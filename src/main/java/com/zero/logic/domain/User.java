package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/1.
 */

import com.zero.basic.domain.BasicBean;
import javax.persistence.*;

/**
 * 用户类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@Entity
@Table(name = "sys_user")
public class User extends BasicBean{

    @Id
    @Column(name = "USERCODE")
    private String userCode;//用户编号
    @Column(name = "USERNAME")
    private String userName;//用户名
    private String password;//密码
    private String phone;//联系电话
    private String address;//用户地址
    private String state;//0为停用、1为启用
    private String email;//用户邮箱

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
