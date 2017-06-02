package com.zero.basic.domain;/**
 * Created by Admin on 2017/6/1.
 */

import javax.persistence.*;
import java.util.Date;

/**
 * 基础类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@MappedSuperclass
public class BasicBean {
    private String createuser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATDATE")
    private Date createdate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATEDATE")
    private Date updatedate;
    private String updateuser;

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }
}
