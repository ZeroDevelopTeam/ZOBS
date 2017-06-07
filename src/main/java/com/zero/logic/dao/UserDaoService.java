package com.zero.logic.dao;/**
 * Created by Admin on 2017/6/1.
 */



import com.zero.logic.domain.User;
import com.zero.logic.util.JDBCUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.MD5Util;
import net.sf.json.JSONException;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 用户服务类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@Component
public class UserDaoService {
    private UserDao userDao;
    /**
     * 用户信息分页模糊查询
     * @param keyWord
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<Object> getUserByPage(String keyWord,int pageNo,int pageSize){
        int pageNumber = (pageNo-1)*pageSize;//
        String sql = "select o.* from (SELECT * FROM sys_user t WHERE t.USERCODE LIKE '%"+keyWord+"%'"
                +"OR t.USERNAME LIKE '%"+keyWord+"%' OR t.ADDRESS LIKE '%"+keyWord+"%' OR t.PHONE LIKE '%"+keyWord+"%'" +
                "OR t.EMAIL LIKE '%"+keyWord+"%'OR t.CREATEUSER LIKE '%"+keyWord+"%'OR t.UPDATEUSER LIKE '%"+keyWord+"%'"
                + " ORDER BY USERCODE ASC) o limit "+pageNumber+","+pageSize;
        List<Object> list = new ArrayList<>();
        Connection conn = JDBCUtil.getConn();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                User user = new User();
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPsw(rs.getString("userpsw"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setEmail(rs.getString("email"));
                user.setState(rs.getInt("state"));
               // user.setCreateuser(rs.getString("createuser"));
               // user.setUpdateuser(rs.getString("updateuser"));

                String createDate = rs.getString("createdate");
                String updateDate = rs.getString("updatedate");
                //去掉2017-06-06 14:21:57.0的.0
                //if(createDate!=null && createDate.length()>2){user.setCreatedate(createDate.substring(0,createDate.length()-2));}
               // if(updateDate!=null && updateDate.length()>2){user.setUpdatedate(updateDate.substring(0,updateDate.length()-2));}
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取模糊查询分页总记录数
     * @param keyWord
     * @return
     */
    public int  getTotalCount(String keyWord){
        String sql = "select * from  sys_user t WHERE t.USERCODE LIKE '%"+keyWord+"%' OR t.USERNAME LIKE '%"+keyWord+"%' " +
                "OR t.ADDRESS LIKE '%"+keyWord+"%' OR t.PHONE LIKE '%"+keyWord+"%' OR t.EMAIL LIKE '%"+keyWord+"%'" +
                "OR t.CREATEUSER LIKE '%"+keyWord+"%'OR t.UPDATEUSER LIKE '%"+keyWord+"%'";
        Connection conn = JDBCUtil.getConn();
        PreparedStatement preparedStatement = null;
        int total=0;
        try {
            preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                total = total+1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

}
