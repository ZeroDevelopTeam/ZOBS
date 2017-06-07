package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/1.
 */

import com.zero.logic.dao.UserDao;
import com.zero.logic.dao.UserDaoService;
import com.zero.logic.domain.User;
import com.zero.logic.util.JDBCUtil;
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
            @RequestParam(value = "page", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "15") Integer pageSize){
        Sort sort = new Sort(Sort.Direction.DESC, "userCode");
        Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
        Page<User> users = userDao.findAll(pageable);

        List<Object> list = new ArrayList<>();
        for(User user:users){
            list.add(user);
        }

        int total = JDBCUtil.getCount(JDBCUtil.getConn(),"sys_user");//获取总用户数
        int totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        list.add(TableUtil.createTableDate(total,pageNum,totalPage));
        return list;
    }

    /**
     *原生查询  之后再改
     */
    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "模糊查询分页获取用户",notes = "模糊查询分页获取用户")
    public List<Object> getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam("pageNum")int pageNum,
            @RequestParam("pageSize")int pageSize){
        List<Object> users = userDaoService.getUserByPage(keyWord,pageNum,pageSize);
        int total = userDaoService.getTotalCount(keyWord);//获取模糊查询用户总数
        int totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        users.add(TableUtil.createTableDate(total,pageNum,totalPage));
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
    public String addUser(@RequestBody User user) throws JSONException {
                String msg ="";
        if(user!=null){
            if(!"".equals(user.getUserCode())&& user.getUserCode()!=null){
                String password = MD5Util.getMd5(user.getUserCode(),user.getUserPsw());//加密用户密码
                user.setUserPsw(password);
                userDao.save(user);
                msg = "新增用户成功";
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }else {
                msg = "用户编号不能为空";
                return  JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }
        msg = "新增用户失败";
        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
    }
    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    @ApiOperation(value = "user",notes = "修改用户")
    public String editUser(@RequestBody User user) throws JSONException {
                String msg = "";
        if(user!=null){
            User oldUser = userDao.getUserByUserCode(user.getUserCode());//修改的用户是否存在
            if(oldUser!=null){
                String pasword = MD5Util.getMd5(user.getUserCode(),user.getUserPsw());
                user.setUserPsw(pasword);
                userDao.save(user);
                msg = "修改用户成功";
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }
        }
        msg = "修改用户失败";
        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
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

    @RequestMapping(value = "/changeUserState",method = RequestMethod.POST)
    @ApiOperation(value = "String userCodes[]",notes = "单个或多个改变用户账号状态")
    public Object changeUserState(
            @RequestParam("userCodes") String [] userCodes,
            @RequestParam("state")int state) throws JSONException {
                String msg="";
                for (String userCode:userCodes){
                    try {
                        User user = userDao.getUserByUserCode(userCode);
                        user.setState(state);
                        userDao.save(user);
                        msg +=userCode+"、";
                    }catch (Exception e){
                        e.printStackTrace();
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户"+userCode+"修改状态失败");
                    }
                }
                msg = msg.substring(0,msg.length()>0?msg.length()-1:msg.length());
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改用户"+msg+"状态成功");
    }
}
