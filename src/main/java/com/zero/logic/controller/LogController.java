package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/9.
 */

import com.zero.logic.dao.LogDao;
import com.zero.logic.domain.Log;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/9
 */
@RestController
@RequestMapping("log")
public class LogController {
    @Autowired
    private LogDao logDao;
   @RequestMapping(value = "/getByPage",method = RequestMethod.GET)
   @ApiOperation(value = "分页获取日志",notes = "分页获取日志")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam("type")int type,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        String result = "";
        Sort sort = new Sort(Sort.Direction.DESC, "logId");
        Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
        Page<Log> logs = logDao.getByPage(keyWord, type, pageable);
        List<Object> list = new ArrayList<>();
        for (Log log:logs){
            list.add(log);
        }
        long total = logDao.count(keyWord,type);//获取查询总数
        long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
       try {
           result = TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return result;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    @ApiOperation(value = "保存日志",notes = "保存日志")
    public String saveLog(@RequestBody Log log){
              logDao.save(log);
              return "保存成功";
    }
}
