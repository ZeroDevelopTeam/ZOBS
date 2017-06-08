package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/8.
 */

import com.zero.logic.dao.RoleDao;
import com.zero.logic.domain.Role;
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
import java.util.List;

/**
 * 角色控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/8
 */
@RestController
@RequestMapping("role")
public class RoleController {
    @Autowired
    private RoleDao roleDao;

    @RequestMapping(value = "getRole",method = RequestMethod.GET)
    @ApiOperation(value = "获取角色",notes = "根据角色编号获取角色信息")
    public String getRole(
            @ApiParam(required=true,name="roleId", value="角色编号")
            @RequestParam("roleId")String roleId){
        String result = "";
        String msg = "";
        try {
           Role role = roleDao.getRoleByRoleId(roleId);
           result = JsonUtil.fromObject(role);
        }catch (Exception e){
            e.printStackTrace();
            msg ="获取角色信息失败";
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
        }
        return result;
    }

    @RequestMapping(value = "addRole",method = RequestMethod.POST)
    @ApiOperation(value = "新增角色",notes = "新增角色信息")
    public String addRole(@RequestBody Role role){
                String msg = "";
                String result = "";
                try {
                    if(!"".equals(role.getRoleId()) && role.getRoleId()!=null){
                        roleDao.save(role);
                        msg = "新增角色信息成功";
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
                    }else  {
                        msg = "新增角色信息失败";
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    msg = "新增角色信息失败";
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
                }
        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
    }

    @RequestMapping(value = "getRoleList",method = RequestMethod.GET)
    @ApiOperation(value = "获取所有角色",notes = "获取所有角色信息")
    public String getRoleList(){
        String result = "";
        String msg="";
        long total=0;
        List<Role> list = new ArrayList<>();
        try {
            Iterable<Role>  roles = roleDao.findAll();
            for (Role role:roles){
                list.add(role);
            }
            total = list.size();//角色总数
            result = TableUtil.createTableDate(list,total,0,0,0);
            msg = "获取所有角色成功";
        }catch (Exception e){
            e.printStackTrace();
            msg = "获取所有角色失败";
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
        }
        return result;
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
        result = TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        return result;
    }

    @RequestMapping(value = "修改角色",method = RequestMethod.POST)
    @ApiOperation(value = "Role",notes = "修改角色")
    public String editRole(@RequestBody Role role){
        String msg ="";
        String result = "";
        if(role!=null && role.getRoleId()!=null && !"".equals(role.getRoleId())){
            try {
                Role oldRole = roleDao.getRoleByRoleId(role.getRoleId());
                if(oldRole!=null){
                    roleDao.save(role);
                    msg = "修改角色成功";
                }else {
                    msg = "修改的角色不存在";
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
                }
            }catch (Exception e){
                e.printStackTrace();
                msg = "修改角色失败";
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
        }else {
            msg = "请输入角色或者角色ID";
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
        }
    }

    @RequestMapping(value = "删除一个或多个角色",method = RequestMethod.GET)
    @ApiOperation(value = "str_roleId",notes = "根据角色编号删除一个或多个角色")
    public String removeRole(String str_roleId){
        String msg = "";
        String result = "";
        String [] roleIds = str_roleId.split(",");
        for (String roleId:roleIds){
            Role oldRole = roleDao.getRoleByRoleId(roleId);
            if(oldRole!=null){
                try {
                    roleDao.delete(oldRole);
                }catch (Exception e){
                    e.printStackTrace();
                    msg = "删除角色："+oldRole.getRoleId()+"失败";
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
                }
            }else {
                msg = "角色："+roleId+"不存在";
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }
        msg = "删除角色成功";
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
    }
}
