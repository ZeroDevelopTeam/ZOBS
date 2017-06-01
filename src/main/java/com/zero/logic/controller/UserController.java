package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/1.
 */

import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/getAllUser",method = RequestMethod.GET)
    @ApiOperation(value = "获取所有用户",notes = "获取所有用户")
    public List<User> getAllUser(){
        List<User> list=new ArrayList<>();
        Iterable<User> users = userDao.findAll();
        for (User user : users){
            list.add(user);
        }
        return list;
    }
}
