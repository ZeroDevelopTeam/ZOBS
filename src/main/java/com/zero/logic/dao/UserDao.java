package com.zero.logic.dao;

import com.zero.logic.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 用户接口
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
public interface UserDao extends CrudRepository<User,Integer> {

    /**
     * 根据用户编号获取用户信息
     * @param userCode 用户账号
     * @return user 用户信息
     */

    public User getUserByUserCode(String userCode);


    /**
     * 模糊查询分页
     * @param keyWord 关键字
     * @param pageable 分页类
     * @return users 用户集合
     */
    @Query("select t from User t where t.userName like %?1% or t.userCode like %?1% or t.address like %?1% or t.phone like %?1% ")
    public Page<User> findByUserName(@Param("keyWord")String keyWord,Pageable pageable);

    /**
     * 获取模糊查询记录数
     * @param keyWord 关键字
     * @return 记录数
     */
    @Query("select count(*) from User t where t.userName like %?1% or t.userCode like %?1% or t.address like %?1% or t.phone like %?1%" )
    public long count(@Param("keyWord")String keyWord);

}
