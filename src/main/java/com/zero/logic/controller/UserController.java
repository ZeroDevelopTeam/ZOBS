package com.zero.logic.controller;
import com.zero.logic.dao.RoleDao;
import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.Role;
import com.zero.logic.domain.User;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.MD5Util;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private RoleDao roleDao;
    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取用户",notes = "分页获取用户")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        String result = null;
        Sort sort = new Sort(Sort.Direction.DESC, "userCode");
        Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
        Page<User> users = userDao.findByUserName(keyWord,pageable);
        List<Object> list = new ArrayList<>();
        for(User user:users){
            list.add(user);
        }
        long total = userDao.count(keyWord);//获取查询总数
        long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        result = TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        return result;
    }

    @RequestMapping(value = "/getAllUser",method = RequestMethod.GET)
    @ApiOperation(value = "获取所有用户",notes = "获取所有用户")
    public String getAllUser(){
        String result = "";
        long total=0;
        List<Object> list = new ArrayList<>();
       Iterable<User> users = userDao.findAll();
        for (User user : users){
            list.add(user);
        }
        total = list.size();
        result = TableUtil.createTableDate(list,total,0,0,0);
        return result;
    }

    @RequestMapping(value = "/getUserByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",notes = "根据用户编号获取所有用户")
    public String getUserByUserCode(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode) throws Exception {
        String result = "";
        User user = userDao.getUserByUserCode(userCode);
        result = JsonUtil.fromObject(user);
        return result;
    }

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "新增用户")
    public String addUser(@RequestBody User user,@RequestParam String [] roles){
                try {
                    for (String roleId:roles){
                        Role role = roleDao.getRoleByRoleId(roleId);
                        if (null!=role && role.getState()!=0 ){
                            user.getRoles().add(role);
                        }
                    }
                    Date date = new Date();//系统当前时间
                    user.setCreateDate(date);
                    user.setUpdateDate(date);
                    userDao.save(user);
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增用户信息成功");
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增用户信息成功");
                }
    }

    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "修改用户")
    public String editUser(@RequestBody User user,@RequestParam String [] roleIds) throws ParseException {
                String msg = "";
        if(user!=null){
            User oldUser = userDao.getUserByUserCode(user.getUserCode());
            if(null!=oldUser){
                //保存用户角色
                for (int i=0;i<roleIds.length;i++){
                    String roleId = roleIds[i];
                    Role role = roleDao.getRoleByRoleId(roleId);
                    if(null!=role && role.getState()!=0){
                        user.getRoles().add(role);
                    }
                }
                String pasword = MD5Util.getMd5(user.getUserCode(),user.getUserPsw());
                user.setUserPsw(pasword);
                user.setUpdateDate(new Date());//修改时间
                user.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldUser.getCreateDate()));
                userDao.save(user);
                msg = "修改用户成功";
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }
        }
        msg = "修改用户失败";
        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
    }
    @RequestMapping(value = "/deleteUser",method = RequestMethod.GET)
    @ApiOperation(value = "userode",notes = "根据用户编号删除用户")
    public String deleteUser(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode){
        String msg = "";
        try {
            if(!"".equals(userCode)){
                User user = userDao.getUserByUserCode(userCode);
                if(null!=user&&user.getState()!=0){//用户状态为停用时才能删除
                    userDao.delete(user);
                    msg = "删除用户成功";
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
                }
            }else {
                msg = "请输入要删除的用户编码";
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            msg = "删除用户失败";
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
        }
        msg = "删除用户失败";
        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
    }
    @RequestMapping(value = "/deleteUsers",method = RequestMethod.GET)
    @ApiOperation(value = "str_userCode",notes = "根据用户编号批量删除用户")
    public String deleteUsers(String str_userCode) {
                String msg = "";

                String []userCodes = str_userCode.split(",");
                for(String userCode:userCodes){
                    try {
                        User oldUser = userDao.getUserByUserCode(userCode);
                        if(oldUser.getState()!=0){//只能删除状态为停用的用户
                            userDao.deleteByUserCode(userCode);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        msg = "删除用户"+userCode+"失败";
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
                    }
                }
                msg = "批量删除用户成功";
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
    }

    @RequestMapping(value = "/changeUserState",method = RequestMethod.GET)
    @ApiOperation(value = "String userCodes[]",notes = "修改单个或多个用户账号状态")
    public String changeUserState(
            @RequestParam("userCodes") String [] userCodes,
            @RequestParam("state")int state){
                String msg="";
                for (String userCode:userCodes){
                    try {
                        User user = userDao.getUserByUserCode(userCode);
                        user.setState(state);
                        user.setUpdateDate(new Date());
                        user.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,user.getCreateDate()));
                        userDao.save(user);
                    }catch (Exception e){
                        e.printStackTrace();
                        msg = "用户："+userCode+"状态修改失败";
                        return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
                    }
                }
                msg = "用户状态修改成功";
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
    }
}
