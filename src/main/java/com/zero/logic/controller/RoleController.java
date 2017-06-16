package com.zero.logic.controller;

import com.zero.logic.dao.PurviewDao;
import com.zero.logic.dao.RoleDao;
import com.zero.logic.domain.Purview;
import com.zero.logic.domain.Role;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
            role.setCreateDate(new Date());
            role.setUpdateDate(new Date());
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
            String unPurviewId="";
            for (String purviewId : purviews) {
                Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
                if(null!=purview&&purview.getState()!=0){
                    role.getPurviews().add(purview);//保存权限
                }else {
                    unPurviewId+=unPurviewId+"、";
                }
            }
            role.setUpdateDate(new Date());
            role.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldRole.getCreateDate()));
            roleDao.save(role);
            if ("".equals(unPurviewId)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改角色成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改角色成功,权限"+unPurviewId+"不存在或者为停用，角色未拥有这些权限");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改角色失败");
        }
    }

    @RequestMapping(value = "/deleteRoles",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色",notes = "根据角色编号删除角色")
    public String deleteRoles(@RequestParam String []roleIds){
        try {
            String unDeleteId="";
            for (int i=0;i<roleIds.length;i++){
                String roleId = roleIds[i];
                Role role = roleDao.getRoleByRoleId(roleId);
                if(null!=role&&role.getState()==0) {
                    roleDao.delete(role);
                }else if (role.getState()==1){
                    unDeleteId +=roleId+"、";
                }
            }
            if ("".equals(unDeleteId)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除角色成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除" + unDeleteId + "角色未停用，其余删除角色成功");
            }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除角色失败");
        }
    }

    @RequestMapping(value = "/getRoleByRoleId",method = RequestMethod.GET)
    @ApiOperation(value = "获取角色",notes = "根据角色编号获取角色信息")
    public String getRoleByRoleId(@RequestParam String roleId){
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
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "roleId");
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Role> roles = roleDao.findByRoleName(keyWord,pageable);
            long total = roleDao.count(keyWord);
            List<Object> list = new ArrayList<>();
            for (Role role:roles){
                list.add(role);
            }
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list, total, pageNum, totalPage, pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取角色失败");
        }
    }

    @RequestMapping(value = "/changeRoleState",method = RequestMethod.GET)
    @ApiOperation(value = "修改角色状态",notes = "修改单个或多个角色状态")
    public String changeRoleState(
            @RequestParam("roleIds") String [] roleIds,
            @RequestParam("state")int state){
         try {
             String unRoleState="";
             for (int i=0;i<roleIds.length;i++){
                 String roleId = roleIds[i];
                 Role oldRole = roleDao.getRoleByRoleId(roleId);
                 if (null!=oldRole){
                     oldRole.setState(state);
                     oldRole.setUpdateDate(new Date());//修改时间
                     oldRole.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldRole.getCreateDate()));
                     roleDao.save(oldRole);
                 }else {
                     unRoleState +=roleId;
                 }
             }
             if ("".equals(unRoleState)){
                 return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改角色状态成功");
             }else {
                 return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "除了角色"+unRoleState+"状态修改失败，其余角色状态修改成功");
             }
         }catch (Exception e){
             return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改角色状态失败");
         }
    }
}
