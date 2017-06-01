package com.zero.logic.controller;/**
 * Created by Admin on 2017/6/1.
 */

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    @ApiOperation(value = "获取hello",notes = "获取hello")
    public String getString(
            @ApiParam(required=true,name="world", value="用户账号1111")
            @RequestParam("world")String world){
        return "hello"+world;
    }
}
