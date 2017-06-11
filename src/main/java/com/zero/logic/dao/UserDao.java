package com.zero.logic.dao;

import com.zero.logic.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;


/**
 * 用户接口
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
public interface UserDao extends CrudRepository<User,Integer> {

    /**
     * 根据用户编号获取用户信息
     * @param userCode
     * @return user
     */

    public User getUserByUserCode(String userCode);


    /**
     * 模糊查询分页
     * @param keyWord
     * @param pageable
     * @return users
     */
    @Query("select t from User t where t.userName like %?1% or t.userCode like %?1% or t.address like %?1% or t.phone like %?1% ")
    public Page<User> findByUserName(@Param("keyWord")String keyWord,Pageable pageable);

    /**
     * 获取模糊查询记录数
     * @param keyWord
     * @return
     */
    @Query("select count(*) from User t where t.userName like %?1% or t.userCode like %?1% or t.address like %?1% or t.phone like %?1%" )
    public long count(@Param("keyWord")String keyWord);


    /**
     * 根据用户编号删除用户
     * @param userCode
     */
    @Modifying
    @Transactional
    @Query("delete  from User t where t.userCode =:userCode")
    public void deleteByUserCode(@Param("userCode") String userCode);

}
