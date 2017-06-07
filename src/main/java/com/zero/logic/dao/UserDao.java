package com.zero.logic.dao;/**
 * Created by Admin on 2017/6/1.
 */

import com.zero.logic.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


/**
 * 用户接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */


public interface UserDao extends CrudRepository<User,Integer> {

    /**
     * 根据用户编号获取用户信息
     * @param userCode
     * @return user
     */
    public User getUserByUserCode(String userCode);

    /**
     * 实现分页功能
     * @param pageable
     * @return
     */
    public Page<User> findAll(Pageable pageable);


    @Query(value = "SELECT t FROM sys_user t WHERE t.USERCODE LIKE %:keyWord% OR t.USERNAME LIKE %:keyWord%")
    public Page<User> findByUserCode(@Param("userCode")String keyWord, Pageable pageable);

}
