package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/9.
 */

import com.zero.logic.dao.PurviewDao;
import com.zero.logic.domain.Purview;
import com.zero.logic.util.JsonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 权限控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/9
 */
@RestController
@RequestMapping("purview")
public class PurviewController {
    @Autowired
    private PurviewDao purviewDao;
    @RequestMapping(value = "/getPurviewByPurviewId",method = RequestMethod.GET)
    @ApiOperation(value = "获取权限",notes = "根据权限编号获取权限")
    public String getPurviewByPurviewId(
            @ApiParam(required=true,name="purviewId", value="权限编号")
            @RequestParam("purviewId")String purviewId){
        String msg = "";
        String result = "";
        if(!"".equals(purviewId) && purviewId!=null){
            try {
                Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
                result = JsonUtil.fromObject(purview);
            }catch (Exception e){
                e.printStackTrace();
                msg = "获取权限失败";
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
            return result;
        }else {
            msg = "请输入权限编号";
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
        }
    }

    @RequestMapping(value = "/addPurview",method = RequestMethod.POST)
    @ApiOperation(value = "purview",notes = "新增权限")
    public String addPurview(@RequestBody Purview purview){
        String msg = "";
        String result = "";
        if(purview!=null && !"".equals(purview.getPurviewId()) && purview.getPurviewId()!=null){
            try {
                purviewDao.save(purview);
                msg = "新增权限成功";
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }catch (Exception e){
                e.printStackTrace();
                msg = "新增权限失败";
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }else {
            msg = "新增权限对象或权限ID为空，新增失败";
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
        }

    }

    @RequestMapping(value = "/eidtPurview",method = RequestMethod.POST)
    @ApiOperation(value = "purview",notes = "修改权限")
    public String eidtPurview(@RequestBody Purview purview){
        String msg = "";
        String result = "";
        if(purview!=null && !"".equals(purview.getPurviewId()) && purview.getPurviewId()!=null){
            try {
               Purview oldPurview = purviewDao.getPurviewByPurviewId(purview.getPurviewId());
               if(oldPurview!=null){
                   purviewDao.save(purview);
                   msg = "修改权限成功";
                   return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
               }else {
                   msg = "修改的权限不存";
                   return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
               }
            }catch (Exception e){
                e.printStackTrace();
                msg = "修改权限失败";
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }else {
            if(purview==null){
                msg = "修改权限对象为空，修改失败";
            }else if(purview.getPurviewId()==null && !"".equals(purview.getPurviewId())){
                msg = "修改权限ID为空，修改失败";
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
        }
    }

}
