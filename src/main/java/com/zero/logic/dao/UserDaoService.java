package com.zero.logic.dao;/**
 * Created by Admin on 2017/6/1.
 */



import com.zero.logic.domain.User;
import com.zero.logic.util.JDBCUtil;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@Component
public class UserDaoService {
    /**
     * 用户信息分页模糊查询
     * @param keyWord
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<Object> getUserByPage(String keyWord,int pageNo,int pageSize){
        int pageNumber = (pageNo-1)*pageSize;//
        String sql = "select o.* from (SELECT * FROM sys_user WHERE USERCODE LIKE '%"+keyWord+"%'"
                +"OR USERNAME "
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
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setEmail(rs.getString("email"));
                user.setState(rs.getString("state"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int  getTotalCount(String keyWord){
        String sql = "select * from  sys_user WHERE USERCODE LIKE '%"+keyWord+"%'";
        Connection conn = JDBCUtil.getConn();
        PreparedStatement preparedStatement = null;
        int total=0;
        try {
            preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
