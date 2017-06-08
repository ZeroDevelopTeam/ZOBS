package com.zero.basic.domain;
import javax.naming.NamingEnumeration;
import javax.persistence.*;
import java.util.Date;

/**
 * 基础实体类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@MappedSuperclass
public class BasicBean {
    @Column(name = "CREATEUSER")
    private String createUser;
    @Column(name = "CREATEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "UPDATEDATE")
    private Date updateDate;
    @Column(name = "UPDATEUSER")
    private String updateUser;


    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
