package com.zero.logic.domain;
import com.zero.basic.domain.BasicBean;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 日志类
 * @auther Deram Zhao
 * @creatTime 2017/6/9
 */
@Entity
@Table(name = "sys_log")
public class Log extends BasicBean {
    @Id
    @Column(name = "LOGID")
    private String logId;//日志id
    @Column(name = "LOGCONTENT")
    private String logContent;//日志内容
    @Column(name = "TYPE")
    private int type;//日志类型 0=操作日志；1=数据库日志；2=系统日志
    @Column(name = "LEVER")
    private int lever;//
    @Column(name = "USERCODE")
    private String userCode;//操作用户

    public Log(){}

    public Log(int lever,String logContent,int type,String userCode){

    }
    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLever() {
        return lever;
    }

    public void setLever(int lever) {
        this.lever = lever;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
