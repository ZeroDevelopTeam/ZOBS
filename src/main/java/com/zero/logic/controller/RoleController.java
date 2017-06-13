package com.zero.logic.controller;

import com.zero.logic.dao.PurviewDao;
import com.zero.logic.dao.RoleDao;
import com.zero.logic.domain.Purview;
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
                for (String purviewId : purviews) {
                    Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
                    if(null!=purview&&purview.getState()!=0)
                        role.getPurviews().add(purview);
                }
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
            if(null!=role&&role.getState()==0) {
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

    @RequestMapping(value = "/getAllRole",method = RequestMethod.GET)
    @ApiOperation(value = "获取所有角色",notes = "获取所有角色信息")
    public String getRoleList(){
        try {
            Iterable<Role>  roles = roleDao.findAll();
            return JsonUtil.fromArray(roles);
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取全部角色失败");
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

}
