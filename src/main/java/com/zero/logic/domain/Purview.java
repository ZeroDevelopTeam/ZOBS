package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/9.
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 权限类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/9
 */
@Entity
@Table(name = "sys_purview")
public class Purview {
    @Id
    @Column(name = "PURVIEWID")
    private String purviewId;//权限编号
    @Column(name = "PURVIEWNAME")
    private String purviewName;//权限名称
    @Column(name = "PURVIEWRULE")
    private String purviewRule;//权限规则
    @Column(name = "PURVIEWDESC")
    private String purviewDesc;//权限备注
    private int state;//权限状态

    public String getPurviewId() {
        return purviewId;
    }

    public void setPurviewId(String purviewId) {
        this.purviewId = purviewId;
    }

    public String getPurviewName() {
        return purviewName;
    }

    public void setPurviewName(String purviewName) {
        this.purviewName = purviewName;
    }

    public String getPurviewRule() {
        return purviewRule;
    }

    public void setPurviewRule(String purviewRule) {
        this.purviewRule = purviewRule;
    }

    public String getPurviewDesc() {
        return purviewDesc;
    }

    public void setPurviewDesc(String purviewDesc) {
        this.purviewDesc = purviewDesc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
