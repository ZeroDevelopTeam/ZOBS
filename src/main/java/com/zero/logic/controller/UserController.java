package com.zero.logic.controller;

import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.User;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.MD5Util;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 用户控制类
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "新增用户")
    public String addUser(@RequestBody User user) {
        try {
            String password = MD5Util.getMd5(user.getUserCode(),user.getUserPsw());//加密用户密码
            user.setUserPsw(password);
            userDao.save(user);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增用户成功");
        }catch(Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增用户失败");
        }

    }




    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取用户",notes = "分页获取用户")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        String result = "";
        Sort sort = new Sort(Sort.Direction.DESC, "userCode");
        Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
        Page<User> users = userDao.findByUserName(keyWord,pageable);
        List<Object> list = new ArrayList<>();
        for(User user:users){
            list.add(user);
        }
        long total = userDao.count(keyWord);//获取查询总数
        long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        try {
            result = TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            result = TableUtil.createTableDate(list,total,0,0,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/getUserByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",notes = "根据用户编号获取所有用户")
    public String getUserByUserCode(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode){
        String result = "";
        User user = userDao.getUserByUserCode(userCode);
        try {
            result = JsonUtil.fromObject(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "修改用户")
    public String editUser(@RequestBody User user)  {
                String msg = "";
        if(user!=null){
            User oldUser = userDao.getUserByUserCode(user.getUserCode());//修改的用户是否存在
            if(oldUser!=null){
                String pasword = null;
                try {
                    pasword = MD5Util.getMd5(user.getUserCode(),user.getUserPsw());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                user.setUserPsw(pasword);
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
                if(user!=null){
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
                        userDao.deleteByUserCode(userCode);
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
            @RequestParam("state")int state) throws JSONException {
                String msg="";
                for (String userCode:userCodes){
                    try {
                        User user = userDao.getUserByUserCode(userCode);
                        user.setState(state);
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
