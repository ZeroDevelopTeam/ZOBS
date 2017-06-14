package com.zero.logic.controller;

import com.zero.logic.dao.PurviewDao;
import com.zero.logic.dao.RoleDao;
import com.zero.logic.domain.Purview;
import com.zero.logic.domain.Role;
import com.zero.logic.domain.User;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 角色控制类
 * @auther Deram Zhao
 * @creatTime 2017/6/8
 */
@RestController
@RequestMapping("role")
public class RoleController {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PurviewDao purviewDao;

    @RequestMapping(value = "addRole", method = RequestMethod.POST)
    @ApiOperation(value = "新增角色", notes = "新增角色信息")
    public String addRole(@RequestBody Role role,@RequestParam String[] purviews) {
        try {
            for (String purviewId : purviews) {
                Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
                if(null!=purview&&purview.getState()!=0)
                    role.getPurviews().add(purview);
            }
            Date date = new Date();//系统当前时间
            role.setCreateDate(date);//新增时间
            role.setUpdateDate(date);//修改时间
            roleDao.save(role);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增角色信息成功");
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增角色信息失败");
        }
    }

    @RequestMapping(value = "/editRole", method = RequestMethod.POST)
    @ApiOperation(value = "Role", notes = "修改角色")
    public String editRole(@RequestBody Role role,@RequestParam String[] purviews) {
        try {
            Role oldRole = roleDao.getRoleByRoleId(role.getRoleId());
            if (null != oldRole) {
                //保存权限
                for (String purviewId : purviews) {
                    Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
                    if(null!=purview&&purview.getState()!=0)
                        role.getPurviews().add(purview);
                }
                role.setUpdateDate(new Date());
                role.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldRole.getCreateDate()));
                roleDao.save(role);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改角色成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改角色失败");
            }
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改角色失败");
        }
    }


    @RequestMapping(value = "/removeRole/{roleId}",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色额",notes = "根据角色编号删除角色")
    public String removeRole(@PathVariable String roleId){
        try {
            Role role = roleDao.getRoleByRoleId(roleId);
            if(null!=role&&role.getState()==0) {//角色状态为停用才能删除
                roleDao.delete(role);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "删除角色成功");
            }else{
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"角色未停用，删除角色失败");
            }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除角色失败");
        }
    }

    @RequestMapping(value = "/deleteRoles",method = RequestMethod.GET)
    @ApiOperation(value = "批量删除角色",notes = "根据角色编号批量删除角色")
    public String deleteRoles( String str_userCode){
        try {
            String msg = "";

            String []roleIds = str_userCode.split(",");
            for (String roleId:roleIds){
                Role role = roleDao.getRoleByRoleId(roleId);
                if(null!=role&&role.getState()==0) {
                    roleDao.delete(role);
                }else if (role.getState()==1){
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"角色"+role.getRoleName()+"未停用，删除角色失败");
                }
            }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除角色失败");
        }
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "删除角色成功");
    }

    @RequestMapping(value = "/getRoleByRoleId/{roleId}",method = RequestMethod.GET)
    @ApiOperation(value = "获取角色",notes = "根据角色编号获取角色信息")
    public String getRoleByRoleId(
            @ApiParam(required=true,name="roleId", value="角色编号")
            @PathVariable String roleId){
        try {
           Role role = roleDao.getRoleByRoleId(roleId);
           return JsonUtil.fromObject(role);
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取角色信息失败");
        }
    }

    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取所有角色",notes = "分页获取所有角色信息")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        String msg="";
        String result="";
        Sort sort = new Sort(Sort.Direction.DESC, "roleId");
        Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
        Page<Role> roles = roleDao.findByRoleName(keyWord,pageable);
        long total = roleDao.count(keyWord);
        List<Object> list = new ArrayList<>();
        for (Role role:roles){
            list.add(role);
        }
        long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        try {
            result = TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @RequestMapping(value = "/changeUserState",method = RequestMethod.GET)
    @ApiOperation(value = "String roleIds[]",notes = "修改单个或多个角色状态")
    public String changeUserState(
            @RequestParam("roleIds") String [] roleIds,
            @RequestParam("state")int state){
        String msg="";
        for (String roleId:roleIds){
            try {
               Role role = roleDao.getRoleByRoleId(roleId);
                role.setState(state);
                role.setUpdateDate(new Date());//修改时间
                role.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,role.getCreateDate()));
                roleDao.save(role);
            }catch (Exception e){
                e.printStackTrace();
                msg = "角色："+roleId+"状态修改失败";
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }
        }
        msg = "角色状态修改成功";
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
    }
}
