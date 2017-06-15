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
import java.util.*;


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

    @RequestMapping(value = "getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取用户", notes = "分页获取用户")
    public String getByPage(
            @RequestParam("keyWord") String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "userCode");
            Pageable pageable = new PageRequest(pageNum - 1, pageSize, sort);
            Page<User> users = userDao.findByUserName(keyWord, pageable);
            List<Object> list = new ArrayList<>();
            for (User user : users) {
                list.add(user);
            }
            long total = userDao.count(keyWord);//获取查询总数
            long totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;//总页数
            return TableUtil.createTableDate(list, total, pageNum, totalPage, pageSize);
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "获取用户信息失败");
        }
    }

    @RequestMapping(value = "/getAllUser", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有用户", notes = "获取所有用户")
    public String getAllUser() {
        try {
            List<Object> list = new ArrayList<>();
            Iterable<User> users = userDao.findAll();
            for (User user : users) {
                list.add(user);
            }
            return TableUtil.createTableDate(list, list.size(), 0, 0, 0);
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "获取用户信息失败");
        }
    }

    @RequestMapping(value = "/getUserByUserCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户", notes = "根据用户编号获取所有用户")
    public String getUserByUserCode(
            @ApiParam(required = true, name = "userCode", value = "用户编号")
            @RequestParam("userCode") String userCode) throws Exception {
        try {
            User user = userDao.getUserByUserCode(userCode);
            return JsonUtil.fromObject(user);
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "获取用户信息失败");
        }
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ApiOperation(value = "新增用户", notes = "新增用户信息")
    public String addUser(@RequestBody User user, @RequestParam String[] roles) {
        try {
            for (String roleId : roles) {
                Role role = roleDao.getRoleByRoleId(roleId);
                if (null != role && role.getState() != 0) {
                    user.getRoles().add(role);
                }
            }
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            user.setUserPsw(MD5Util.getMd5(user.getUserCode(), user.getUserPsw()));
            userDao.save(user);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增用户信息成功");
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增用户信息成功");
        }
    }

    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户", notes = "根据用户编号修改用户信息")
    public String editUser(@RequestBody User user, @RequestParam String[] roleIds) throws ParseException {
        try {
            User oldUser = userDao.getUserByUserCode(user.getUserCode());
            if (null != oldUser) {
                //保存用户角色
                for (int i = 0; i < roleIds.length; i++) {
                    String roleId = roleIds[i];
                    Role role = roleDao.getRoleByRoleId(roleId);
                    if (null != role && role.getState() != 0) {
                        user.getRoles().add(role);
                    }
                }
                user.setUserPsw(MD5Util.getMd5(user.getUserCode(), user.getUserPsw()));
                user.setUpdateDate(new Date());//修改时间
                user.setCreateDate(DateUtil.parse(DateUtil.FORMAT2, oldUser.getCreateDate()));
                userDao.save(user);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改用户成功");
            }else{
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改用户失败");
            }
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改用户失败");
        }
    }

    @RequestMapping(value = "/deleteUser",method = RequestMethod.GET)
    @ApiOperation(value = "删除用户",notes = "根据用户编号删除用户")
    public String deleteUser(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode){
        try {
                User user = userDao.getUserByUserCode(userCode);
                if(null!=user&&user.getState()!=0) {
                    userDao.delete(user);
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "删除用户成功");
                }else{
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "用户未停用，删除用户失败");
                }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "删除用户失败");
        }
    }

    @RequestMapping(value = "/deleteUsers", method = RequestMethod.GET)
    @ApiOperation(value = "str_userCode", notes = "根据用户编号批量删除用户")
    public String deleteUsers(String str_userCode) {
        try {
            String[] userCodes = str_userCode.split(",");
            String unDeleteId = "";
            for (String userCode : userCodes) {
                User oldUser = userDao.getUserByUserCode(userCode);
                if (oldUser.getState() != 0) {
                    userDao.delete(oldUser);
                } else {
                    unDeleteId += userCode + ",";
                }
            }
            if ("".equals(unDeleteId)) {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "删除用户成功");
            } else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "除" + unDeleteId + "用户未停用，其余删除用户成功");
            }
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "删除用户失败");
        }
    }

    @RequestMapping(value = "/changeUserState", method = RequestMethod.GET)
    @ApiOperation(value = "String userCodes[]", notes = "修改单个或多个用户账号状态")
    public String changeUserState(
            @RequestParam("userCodes") String[] userCodes,
            @RequestParam("state") int state) {
        try {
            for (String userCode : userCodes) {
                User user = userDao.getUserByUserCode(userCode);
                user.setState(state);
                user.setUpdateDate(new Date());
                user.setCreateDate(DateUtil.parse(DateUtil.FORMAT2, user.getCreateDate()));
                userDao.save(user);
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "用户修改成功");
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "用户修改失败");
        }
    }

    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ApiOperation(value = "登录系统",notes = "登录系统")
    public String login(@RequestParam String userCode,@RequestParam String userPsw){
        try {
            Map<String, Object> map = new HashMap<>();
            User user = userDao.getUserByUserCode(userCode);
            if(null!=user&&user.getState()!=0){
                userPsw = MD5Util.getMd5(userCode,userPsw);
                if(user.getUserPsw().equals(userPsw)){
                    //用户登录成功后返回一个user对象给前端时(不返回密码)
                    user.setUserPsw("");
                    map.put("user",JsonUtil.fromObject(user));
                    String result = JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"登录成功");
                    return JsonUtil.makeJsonBeanByKey(result,map);
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户或密码不正确");
                }
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户或密码不正确");
            }
        }catch(Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"登录失败");
        }
    }

    //增加一个账号锁定功能和账号解锁功能
    //增加一个发送验证码到邮箱功能
}
