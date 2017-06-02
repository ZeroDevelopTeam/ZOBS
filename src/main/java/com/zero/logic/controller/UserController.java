package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/1.
 */

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.zero.logic.dao.UserDao;
import com.zero.logic.dao.UserDaoService;
import com.zero.logic.domain.User;
import com.zero.logic.util.JDBCUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserDaoService userDaoService;
    @RequestMapping(value = "getUserByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取用户",notes = "分页获取用户")
    public List<Object> getUserByPage(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "size", defaultValue = "15") Integer pageSize){
        Sort sort = new Sort(Sort.Direction.DESC, "userCode");
        Pageable pageable = new PageRequest(pageNo-1 , pageSize, sort);
        Page<User> users = userDao.findAll(pageable);

        List<Object> list = new ArrayList<>();
        for(User user:users){
            list.add(user);
        }

        int total = JDBCUtil.getCount(JDBCUtil.getConn(),"sys_user");//获取总用户数
        int totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        list.add(TableUtil.createTableDate(total,pageNo,totalPage));
        return list;
    }

    @RequestMapping(value = "queryPageByLike",method = RequestMethod.GET)
    @ApiOperation(value = "模糊查询分页获取用户",notes = "模糊查询分页获取用户")
    public List<Object> queryPageByLike(
            @RequestParam("keyWord")String keyWord,
            @RequestParam("pageNo")int pageNo,
            @RequestParam("pageSize")int pageSize){
        List<Object> users = userDaoService.getUserByPage(keyWord,pageNo,pageSize);
        int total = userDaoService.getTotalCount(keyWord);//获取模糊查询用户总数
        int totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        users.add(TableUtil.createTableDate(total,pageNo,totalPage));
        return users;
    }

    @RequestMapping(value = "/getAllUser",method = RequestMethod.GET)
    @ApiOperation(value = "获取所有用户",notes = "获取所有用户")
    public List<Object> getAllUser(){
        List<Object> list = new ArrayList<>();
        Iterable<User> users = userDao.findAll();
        for (User user : users){
            list.add(user);
        }
        Map map = new HashMap();
        map.put("total",list.size());
        list.add(map);
        return list;
    }

    @RequestMapping(value = "/getUserByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",notes = "根据用户编号获取所有用户")
    public User getUserByUserCode(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode){
        User user = userDao.getUserByUserCode(userCode);
        return user;
    }

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "新增用户")
    public String addUser(User user) throws JSONException {
        if(user!=null){
            userDao.save(user);
            return  JsonUtil.returnStr(200,"新增用户成功");
        }
        return JsonUtil.returnStr(500,"新增用户失败");
    }
    @RequestMapping(value = "/updateUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "修改用户")
    public String updateUser(User user) throws JSONException {
        if(user!=null){
            userDao.save(user);
            return  JsonUtil.returnStr(200,"修改用户成功");
        }
        return JsonUtil.returnStr(500,"修改用户失败");
    }
    @RequestMapping(value = "/deleteUser",method = RequestMethod.POST)
    @ApiOperation(value = "userode",notes = "根据用户编号删除用户")
    public String deleteUser(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode) throws JSONException {
        if(!"".equals(userCode)){
            User user = userDao.getUserByUserCode(userCode);
            if(user!=null){
                userDao.delete(user);
                return JsonUtil.returnStr(200,"删除用户成功");
            }
        }
        return JsonUtil.returnStr(500,"删除用户失败");
    }
    @RequestMapping(value = "/deleteUsers",method = RequestMethod.POST)
    @ApiOperation(value = "str_userCode",notes = "根据用户编号批量删除用户")
    public String deleteUsers(String str_userCode) throws JSONException {
                List<User> users = new ArrayList<>();
                String []userCodes = str_userCode.split(",");
                for(String userCode:userCodes){
                    users.add(userDao.getUserByUserCode(userCode));
                }
                if(users.size()>0){
                    userDao.delete(users);
                    return JsonUtil.returnStr(200,"批量删除用户成功");
                }
                return JsonUtil.returnStr(500,"批量删除用户失败");
    }
}
