package com.zero.logic.controller;

import com.zero.logic.dao.PurviewDao;
import com.zero.logic.domain.Purview;
import com.zero.logic.util.JsonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 权限控制类
 * @auther Deram Zhao
 * @creatTime 2017/6/9
 */
@RestController
@RequestMapping("purview")
public class PurviewController {
    @Autowired
    private PurviewDao purviewDao;

    @RequestMapping(value = "/addPurview",method = RequestMethod.POST)
    @ApiOperation(value = "新增权限",notes = "新增用户的权限")
    public String addPurview(@RequestBody Purview purview) {
        try {
            purviewDao.save(purview);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增权限成功" );
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增权限失败");
        }
    }

    @RequestMapping(value = "/eidtPurview",method = RequestMethod.POST)
    @ApiOperation(value = "修改权限",notes = "修改用户的权限")
    public String eidtPurview(@RequestBody Purview purview) {
        try {
            Purview oldPurview = purviewDao.getPurviewByPurviewId(purview.getPurviewId());
            if (null!=oldPurview) {
                purviewDao.save(purview);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改权限成功");
            } else {

                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改权限失败");
            }
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改权限失败");
        }
    }

    @RequestMapping(value = "/getPurviewByPurviewId/{purviewId}",method = RequestMethod.GET)
    @ApiOperation(value = "获取权限",notes = "根据权限编号获取权限")
    public String getPurviewByPurviewId(
            @ApiParam(required=true,name="purviewId", value="权限编号")
            @PathVariable String purviewId){
        try {
            Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
            return JsonUtil.fromObject(purview);
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取权限失败");
        }
    }

    @RequestMapping(value= "/getAllPurview",method=RequestMethod.GET)
    @ApiOperation(value="获取全部权限",notes="获取全部权限")
    public String getAllPurview(){
        try {
            Iterable<Purview> purviews = purviewDao.findAll();
            return JsonUtil.fromArray(purviews);
        }catch(Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取全部权限失败");
        }
    }
    @RequestMapping(value="/removePurview/{purviewId}",method=RequestMethod.DELETE)
    @ApiOperation(value="删除权限",notes="根据权限编号删除权限")
    public String removePurview(
            @ApiParam(required = true,name="PurviewId",value="权限编号")
            @PathVariable String purviewId){
        try {
           Purview purview=purviewDao.getPurviewByPurviewId(purviewId);
           if(null!=purview&&purview.getState()==0){
               purviewDao.delete(purview);
               return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除权限成功");
           }else{
               return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"权限未停用，删除权限失败");
           }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除权限失败");
        }
    }
}
