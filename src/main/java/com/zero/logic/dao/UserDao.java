package com.zero.logic.dao;/**
 * Created by Admin on 2017/6/1.
 */

import com.zero.logic.domain.User;
import org.springframework.data.repository.CrudRepository;
/**
 * 用户接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
public interface UserDao extends CrudRepository<User,Integer> {

}
