package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/5.
 */

import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.Login;
import com.zero.logic.domain.User;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.MD5Util;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录发布类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/5
 */

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserDao userDao;
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ApiOperation(value = "登录系统",notes = "登录系统")
    public String login(@RequestBody Login login){
        User user = null;
        String msg = "";
        String result ="";
        String str = "";
        Map<String, Object> map = new HashMap<String, Object>();
             user = userDao.getUserByUserCode(login.getUserCode());
             if(user!=null){
                 String userPsw = null;
                 try {
                     userPsw = MD5Util.getMd5(login.getUserCode(),login.getUserPsw());
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 if(user.getUserPsw().equals(userPsw)){
                     msg = "登录成功";
                     user.setUserPsw("");//用户登录成功后返回一个user对象给前端时(不返回密码)
                     try {
                         map.put("user",JsonUtil.fromObject(user));
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     result = JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
                     try {
                         str =JsonUtil.makeJsonBeanByKey(result,map);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }else {
                     msg = "密码不正确";
                     result = JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
                     return result;
                 }
                 return str;
             }else {
                 msg = "用户不存在";
                 result = JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
                 return result;
             }
    }
}
