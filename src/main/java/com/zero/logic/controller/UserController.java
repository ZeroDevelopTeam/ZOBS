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
    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取用户",notes = "分页获取用户")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "userCode");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<User> users = userDao.findByUserName(keyWord,pageable);
            List<Object> list = new ArrayList<>();
            for(User user:users){
                list.add(user);
            }
            long total = userDao.count(keyWord);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取用户失败");
        }

    }

    @RequestMapping(value = "/getUserByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户",notes = "根据用户编号获取所有用户")
    public String getUserByUserCode(
            @ApiParam(required=true,name="userCode", value="用户编号")
            @RequestParam("userCode")String userCode) throws Exception {
        try {
            User user = userDao.getUserByUserCode(userCode);
            return JsonUtil.fromObject(user);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取用户失败");
        }

    }

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @ApiOperation(value = "新增用户",notes = "新增用户信息")
    public String addUser(@RequestBody User user,@RequestParam String [] roles){
                try {
                    for (String roleId:roles){
                        Role role = roleDao.getRoleByRoleId(roleId);
                        if (null!=role && role.getState()!=0 ){
                            user.getRoles().add(role);
                        }
                    }
                    user.setUserPsw(MD5Util.getMd5(user.getUserCode(),user.getUserPsw()));
                    user.setCreateDate(new Date());
                    user.setUpdateDate(new Date());
                    userDao.save(user);
                    return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增用户信息成功");
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增用户信息失败");
                }
    }

    @RequestMapping(value = "/editUser",method = RequestMethod.POST)
    @ApiOperation(value = "修改用户",notes = "根据用户编号修改用户")
    public String editUser(@RequestBody User user,@RequestParam String [] roleIds) throws ParseException {
        try {
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
                user.setUserPsw(MD5Util.getMd5(user.getUserCode(),user.getUserPsw()));
                user.setUpdateDate(new Date());//修改时间
                user.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldUser.getCreateDate()));
                userDao.save(user);
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"用户修改成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户修改失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"用户修改失败");
        }
    }

    @RequestMapping(value = "/deleteUsers",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户",notes = "根据用户编号删除用户")
    public String deleteUsers(@RequestParam String []userCodes) {
        try {
            String unDeleteId="";
            for (int i=0;i<userCodes.length;i++){
                String userCode = userCodes[i];
                User oldUser = userDao.getUserByUserCode(userCode);
                if (oldUser.getState()!=0){//只能删除停用的用户
                    userDao.delete(oldUser);
                }else {
                    unDeleteId +=userCode;
                }
            }
            if ("".equals(unDeleteId)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除用户成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了" + unDeleteId + "用户未停用，其余用户删除成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除用户失败");
        }
    }

    @RequestMapping(value = "/changeUserState",method = RequestMethod.GET)
    @ApiOperation(value = "修改用户状态",notes = "修改单个或多个用户账号状态")
    public String changeUserState(
            @RequestParam("userCodes") String [] userCodes,
            @RequestParam("state")int state){
        try {
            String unUserState="";
            for (int i=0;i<userCodes.length;i++){
                String userCode = userCodes[i];
                User oldUser = userDao.getUserByUserCode(userCode);
                if (null!=oldUser){
                    oldUser.setState(state);
                    oldUser.setUpdateDate(new Date());
                    oldUser.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldUser.getCreateDate()));
                    userDao.save(oldUser);
                }else {
                    unUserState +=userCode+"";
                }
            }
            if ("".equals(unUserState)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"用户状态修改成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了"+unUserState+"用户状态修改失败，其余修改成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"用户状态修改失败");
        }
    }

    @RequestMapping(value = "/changeUserPsw",method = RequestMethod.GET)
    @ApiOperation(value = "修改用户密码",notes = "修改用户密码")
    public String changeUserPsw(@RequestParam String userCode,@RequestParam String userPsw,@RequestParam String oldUserPsw){
        try {
            User oldUser = userDao.getUserByUserCode(userCode);
            oldUserPsw = MD5Util.getMd5(userCode,oldUserPsw);//校验用户旧密码
            if (oldUserPsw.equals(oldUser.getUserPsw())){
                oldUser.setUserPsw(MD5Util.getMd5(userCode,userPsw));//设置新密码
                oldUser.setUpdateDate(new Date());
                userDao.save(oldUser);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"密码修改成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"旧密码不正确");
            }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"密码修改失败");
        }
    }


    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ApiOperation(value = "登录系统",notes = "登录系统")
    public String login(@RequestBody User user){
        try {
            Map<String, Object> map = new HashMap<>();
            User oldUser = userDao.getUserByUserCode(user.getUserCode());
            if(null!=oldUser&&oldUser.getState()!=0){
                String userPsw = MD5Util.getMd5(user.getUserCode(),user.getUserPsw());
                if(oldUser.getUserPsw().equals(userPsw)){
                    //用户登录成功后返回一个user对象给前端时(不返回密码)
                    oldUser.setUserPsw("");
                    map.put("user",JsonUtil.fromObject(oldUser));
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
