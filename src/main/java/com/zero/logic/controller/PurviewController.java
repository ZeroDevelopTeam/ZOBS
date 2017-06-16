package com.zero.logic.controller;

import com.zero.logic.dao.PurviewDao;
import com.zero.logic.domain.Purview;
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
    @ApiOperation(value = "新增权限",notes = "新增权限信息")
    public String addPurview(@RequestBody Purview purview) {
        try {
            purview.setCreateDate(new Date());
            purview.setUpdateDate(new Date());
            purviewDao.save(purview);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增权限成功" );
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增权限失败");
        }
    }

    @RequestMapping(value = "/eidtPurview",method = RequestMethod.POST)
    @ApiOperation(value = "修改权限",notes = "修改权限信息")
    public String eidtPurview(@RequestBody Purview purview) {
        try {
            Purview oldPurview = purviewDao.getPurviewByPurviewId(purview.getPurviewId());
            if (null!=oldPurview) {
                purview.setUpdateDate(new Date());//修改时间
                purview.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldPurview.getCreateDate()));//权限创建时间
                purviewDao.save(purview);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改权限成功");
            } else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改权限失败");
            }
        } catch (Exception e) {
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改权限失败");
        }
    }

    @RequestMapping(value = "/getPurviewByPurviewId",method = RequestMethod.GET)
    @ApiOperation(value = "获取权限",notes = "根据权限编号获取权限")
    public String getPurviewByPurviewId(@RequestParam String purviewId){
        try {
            Purview purview = purviewDao.getPurviewByPurviewId(purviewId);
            return JsonUtil.fromObject(purview);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取权限失败");
        }
    }

    @RequestMapping(value="/deletePurviews",method=RequestMethod.DELETE)
    @ApiOperation(value="删除权限",notes="根据权限编号删除权限")
    public String deletePurviews(@RequestParam String []purviewIds){
        try {
            String unPurviewId="";
            for (int i=0;i<purviewIds.length;i++){
                String purviewId = purviewIds[i];
                Purview purview=purviewDao.getPurviewByPurviewId(purviewId);
                if(null!=purview&&purview.getState()==0){
                    purviewDao.delete(purview);
                }else if (purview.getState()==1){
                    unPurviewId +=purviewId+"、";
                }
            }
            if ("".equals(unPurviewId)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除权限成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了"+unPurviewId+"权限未停用，其余权限删除成功");
            }
        }catch (Exception e){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除权限失败");
        }
    }

    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取权限",notes = "分页获取所有权限信息")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "purviewId");
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Purview> purviews = purviewDao.findByPurviewName(keyWord,pageable);
            long total = purviewDao.count(keyWord);
            List<Object> list = new ArrayList<>();
            for (Purview purview:purviews){
                list.add(purview);
            }
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取权限失败");
        }
    }

    @RequestMapping(value = "/changePurviewState",method = RequestMethod.GET)
    @ApiOperation(value = "修改权限状态",notes = "修改单个或多个权限状态")
    public String changePurviewState(
            @RequestParam("purviewIds") String [] purviewIds,
            @RequestParam("state")int state){

        try {
            String unPurviewId="";
            for (int i=0;i<purviewIds.length;i++){
                String purviewId = purviewIds[i];
                Purview oldPurview = purviewDao.getPurviewByPurviewId(purviewId);
                if (null!=oldPurview){
                    oldPurview.setState(state);
                    oldPurview.setUpdateDate(new Date());//修改时间
                    oldPurview.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldPurview.getCreateDate()));
                    purviewDao.save(oldPurview);
                }else {
                    unPurviewId +=purviewId+"、";
                }
            }
            if ("".equals(unPurviewId)){
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改权限状态成功");
            }else {
                return  JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了"+unPurviewId+"权限状态修改失败，其余的修改成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改权限状态失败");
        }
    }


}
